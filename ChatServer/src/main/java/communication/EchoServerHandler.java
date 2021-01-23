package communication;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

import databaseManage.DruidFactory;
import databaseManage.DruidHandler;
import databaseManage.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * netty处理类
 * @author Administrator
 */
public class EchoServerHandler extends SimpleChannelInboundHandler<String> {

	//文件写指针
	private BufferedWriter bw = null;
	//数据库连接
	private Connection connection = null;
	//客户端（登录成功）队列
	private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	//channel（登录成功）与userName映射
	private static HashMap<Channel, String> channelMap = new HashMap<Channel, String>();
	//私聊对象缓存（最近8个）
	private static EchoCache channelCache = new EchoCache(8);
	//时间格式
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public EchoServerHandler(BufferedWriter bw) {
		this.bw = bw;
	}

	/**
	 * 当客户端有消息写入（请求应答）
	 */
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		Channel channel = ctx.channel();
		String[] split = msg.split("_");
		if (Pattern.matches("log_.*", msg)) {
			login(channel, split);
		} else if (Pattern.matches("reg_.*", msg)) {
			register(channel, split);
		} else if (Pattern.matches("pub_.*", msg)) {
			publicChat(channel, split);
		} else if (Pattern.matches("pri_.*", msg)) {
			privateChat(channel, split);
		}
	}

	/**
	 * 当有客户端连接
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		String time = sdf.format(new Date()); //当前时间
		connection = DruidFactory.getConnection(); //连接数据库
		Channel coming = ctx.channel(); //获得客户端通道
		String record = time + " [" + coming.remoteAddress() + "]已连接";
		System.out.println(record);
		bw.write(record);
		bw.newLine();
		record = time + " 当前数据库连接数量：" + DruidFactory.getDataSource().getActiveCount();
		System.out.println(record);
		bw.write(record);
		bw.newLine();
		bw.flush();
	}

	/**
	 * 当有客户端断开连接
	 */
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		String time = sdf.format(new Date());
		Channel leaving = ctx.channel();
		quit(leaving, time);
		try {
			//关闭数据库连接
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String record = time + " 当前数据库连接数量：" + DruidFactory.getDataSource().getActiveCount();
		System.out.println(record);
		bw.write(record);
		bw.newLine();
		bw.flush();
	}

	/**
	 * 当客户端有活动
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		String time = sdf.format(new Date());
		Channel channel = ctx.channel();
		String record = time + " [" + channel.remoteAddress() + "]在线中";
		System.out.println(record);
		bw.write(record);
		bw.newLine();
		bw.flush();
	}

	/**
	 * 当客户端没有活动
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		String time = sdf.format(new Date());
		Channel channel = ctx.channel();
		String record = time + " [" + channel.remoteAddress() + "]已离线";
		System.out.println(record);
		bw.write(record);
		bw.newLine();
		bw.flush();
	}

	/**
	 * 异常处理
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		String time = sdf.format(new Date());
		String record = time + " 通讯异常";
		System.out.println(record);
		bw.write(record);
		bw.newLine();
		cause.printStackTrace();
		ctx.close(); //关闭客户端
		ctx.channel().parent().close(); //关闭服务端
		bw.close();
		bw.flush();
	}

	/**
	 * 请求登录
	 * @param channel
	 * @param split
	 * @throws SQLException
	 * @throws IOException
	 */
	protected void login(Channel channel, String[] split) throws SQLException, IOException {
		String time = sdf.format(new Date());
		User user = new User(split[1], split[2]);
		String record;
		if (new DruidHandler().login(connection, user)) {
			//登录成功
			channel.writeAndFlush("log_true\n");
			record = time + " [" + channel.remoteAddress() + "]登录成功";
			//添加私聊对象
			String objName = "obj";
			if (channelMap.size() == 0)
				objName = objName + "_!!!"; //表示第一个登录的用户，无私聊对象
			else {
				//遍历channelMap
				Iterator<Channel> iterator = channelMap.keySet().iterator();
				while (iterator.hasNext()) {
					Channel key = iterator.next();
					objName = objName + "_" + channelMap.get(key);
				}
			}
			channel.writeAndFlush(objName + "\n");
			channelMap.put(channel, split[1]); //增加至映射
			for (Channel ch : channels) {
				ch.writeAndFlush("pub_[服务端]：" + split[1] + "进入聊天室\n");
				ch.writeAndFlush("obj_" + split[1] + "\n");
			}
			channels.add(channel); //增加至队列
		} else {
			//登录失败
			channel.writeAndFlush("log_false\n");
			record = time + " [" + channel.remoteAddress() + "]登录失败";
		}
		System.out.println(record);
		bw.write(record);
		bw.newLine();
		bw.flush();
	}

	/**
	 * 请求注册
	 * @param channel
	 * @param split
	 * @throws SQLException
	 * @throws IOException
	 */
	protected void register(Channel channel, String[] split) throws SQLException, IOException {
		String time = sdf.format(new Date());
		User user = new User(split[1], split[2], time);
		String record;
		if (new DruidHandler().register(connection, user)) {
			channel.writeAndFlush("reg_true\n");
			record = time + " [" + channel.remoteAddress() + "]注册成功";
		} else {
			channel.writeAndFlush("reg_false\n");
			record = time + " [" + channel.remoteAddress() + "]注册失败";
		}
		System.out.println(record);
		bw.write(record);
		bw.newLine();
		bw.flush();
	}

	/**
	 * 公聊
	 * @param channel
	 * @param split
	 * @throws IOException
	 */
	protected void publicChat(Channel channel, String[] split) throws IOException {
		String time = sdf.format(new Date());
		String channelName = channelMap.get(channel);
		for (Channel ch : channels) { //遍历客户端队列
			if (ch != channel)
				//群发消息
				ch.writeAndFlush("pub_[" + channelName + "]：" + split[1] + "\n");
			else
				ch.writeAndFlush("pub_[我]：" + split[1] + "\n");
		}
		String record = time + " [" + channel.remoteAddress() + "]：" + split[1];
		System.out.println(record);
		bw.write(record);
		bw.newLine();
		bw.flush();
	}

	/**
	 * 私聊
	 * @param channel
	 * @param split
	 * @throws IOException
	 */
	protected void privateChat(Channel channel, String[] split) throws IOException {
		String time = sdf.format(new Date());
		String channlName = channelMap.get(channel);
		//通讯对象通道，查找缓存
		Channel channel2 = channelCache.findChannel(split[1]);
		//缓存无记录，遍历channelMap
		if (channel2 == null) {
			Iterator<Channel> iterator = channelMap.keySet().iterator();
			while (iterator.hasNext()) {
				Channel key = iterator.next();
				if (split[1].equals(channelMap.get(key))) {
					channel2 = key;
					channelCache.add(split[1], channel2);
					break;
				}
			}
		}
		channel2.writeAndFlush("pri_[" + channlName + "]：" + split[2] + "\n");
		channel.writeAndFlush("pri_[我]：" + split[2] + "\n");
		String record = time + " [" + channel.remoteAddress() + " to " + channel2.remoteAddress() + "]：" + split[2];
		System.out.println(record);
		bw.write(record);
		bw.newLine();
		bw.flush();
	}

	/**
	 * 退出
	 * @param leaving
	 * @throws IOException
	 */	 
	protected void quit(Channel leaving, String time) throws IOException {
		String channelName = channelMap.get(leaving);
		if (channelMap.containsKey(leaving)) {
			channels.remove(leaving);
			for (Channel ch : channels) { //群发
				ch.writeAndFlush("pub_[服务端]：" + channelName + "离开聊天室\n");
				ch.writeAndFlush("rem_" + channelMap.get(leaving) + "\n");
			}
			channelMap.remove(leaving);
		}
		String record = time + " [" + leaving.remoteAddress() + "]断开连接";
		System.out.println(record);
		bw.write(record);
		bw.newLine();
		bw.flush();
	}
	
}
