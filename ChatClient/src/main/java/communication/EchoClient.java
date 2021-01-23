package communication;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

import graphicInterface.LoginFrame;

/**
 * 客户端
 * @author Administrator
 */
public class EchoClient {

	private String host; //地址
	private int port; //端口
	public static boolean isRun = false; //是否运行中

	public EchoClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * 启动
	 * @throws InterruptedException
	 */
	public void start() throws InterruptedException {
		isRun = true;
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(workerGroup)
					.channel(NioSocketChannel.class)
					.remoteAddress(new InetSocketAddress(host, port))
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel channel) throws Exception {
							channel.pipeline()
									.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
									.addLast("decoder", new StringDecoder())
									.addLast("encoder", new StringEncoder())
									.addLast("handler", new EchoClientHandler());
						}
					});
			Channel channel = bootstrap.connect().sync().channel(); //连接服务端
			/*
			//客户端写数据
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
			channel.writeAndFlush(br.readLine() + "\n");
			}
			*/
			LoginFrame.form(channel);
			while (isRun) {
				Thread.sleep(500);
			}
			channel.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully().sync();
		}
	}

}
