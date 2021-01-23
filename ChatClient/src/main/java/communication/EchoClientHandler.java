package communication;

import java.util.regex.Pattern;

import graphicInterface.LoginFrame;
import graphicInterface.PrivateChatFrame;
import graphicInterface.PublicChatFrame;
import graphicInterface.RegisterFrame;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 客户端处理类
 * @author Administrator
 */
public class EchoClientHandler extends SimpleChannelInboundHandler<String> {

	/**
	 * 读数据
	 */
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
		if ("log_true".equals(msg))
			LoginFrame.flag = "true";
		else if ("log_false".equals(msg))
			LoginFrame.flag = "false";
		else if ("reg_true".equals(msg))
			RegisterFrame.flag = "true";
		else if ("reg_false".equals(msg))
			RegisterFrame.flag = "false";
		else if (Pattern.matches("pub_.*", msg))
			PublicChatFrame.appendChatText(msg.substring(4) + "\n");
		else if (Pattern.matches("pri_.*", msg))
			PrivateChatFrame.appendChatText(msg.substring(4) + "\n");
		else if (Pattern.matches("obj_.*", msg)) {
			String[] objName = msg.substring(4).split("_");
			if ("!!!".equals(objName[0])) {
				//第一个登录，无私聊对象
				PrivateChatFrame.setEnableSendButton(false);
			} else {
				PrivateChatFrame.setEnableSendButton(true);
				//添加私聊对象
				for (String obj : objName)
					PrivateChatFrame.addOjectBox(obj);
			}
		} 
		else if (Pattern.matches("rem_.*", msg)) {
			String[] removeObj = msg.substring(4).split("_");
			PrivateChatFrame.removeOjectBox(removeObj[0]);
			if (PrivateChatFrame.getObjectBox().getItemCount() == 0) {
				//无私聊对象
				PrivateChatFrame.setEnableSendButton(false);
			}
		}
		else
			System.out.println("通讯错误");
	}

	/**
	 * 异常处理
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("通讯异常");
		cause.printStackTrace();
		ctx.close();
	}
}
