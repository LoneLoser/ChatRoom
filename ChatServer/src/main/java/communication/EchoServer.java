package communication;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * 服务端
 * @author Administrator
 */
public class EchoServer {

	private int port = 8001; //端口

	public EchoServer() {}

	public EchoServer(int port) {
		this.port = port;
	}
	
	/**
	 * 启动
	 * @throws InterruptedException
	 */
	public void start() throws InterruptedException {
		EventLoopGroup bossGroup = new NioEventLoopGroup(); //负责客户端的连接
		EventLoopGroup workerGroup = new NioEventLoopGroup(); //负责与连接的客户端通讯
		try (BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream("./src/main/resources/record.txt")))) {
			ServerBootstrap bootstrap = new ServerBootstrap();
			//配置服务端
			bootstrap.group(bossGroup, workerGroup) //绑定线程组
					.channel(NioServerSocketChannel.class) //设置通道类型
					.localAddress(new InetSocketAddress(port)) //设置监听端口
					.childHandler(new ChannelInitializer<SocketChannel>() { //初始化责任链
						@Override
						protected void initChannel(SocketChannel channel) throws Exception {
							//添加处理类流水线
							channel.pipeline()
									.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter())) //固定分隔符将数据分帧
									.addLast("decoder", new StringDecoder()) //解码器
									.addLast("encoder", new StringEncoder()) //编码器
									.addLast("handler", new EchoServerHandler(bw)); //处理类
						}
					})
					.option(ChannelOption.SO_BACKLOG, 1024); //设置缓冲区大小
			ChannelFuture future = bootstrap.bind().sync(); //开启监听
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String record;
			if (future.isSuccess()) {
				record = sdf.format(new Date()) + " [服务端]已启动";
				System.out.println(record);
				bw.write(record);
				bw.newLine();
				bw.flush();
			}
			//IO阻塞，等待退出命令
			Scanner sc = new Scanner(System.in);
			String command = "";
			while(!"shutdown".equals(command)) {
				command = sc.nextLine();
			}
			sc.close();
			future.channel().close();
			future.channel().closeFuture().sync();
			record = sdf.format(new Date()) + " [服务端]已关闭";
			System.out.println(record);
			bw.write(record);
			bw.newLine();
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//关闭线程组
			bossGroup.shutdownGracefully().sync();
			workerGroup.shutdownGracefully().sync();
		}
	}

}
