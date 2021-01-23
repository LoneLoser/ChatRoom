package graphicInterface;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.Color;
import javax.swing.JTextArea;

import communication.EchoClient;
import io.netty.channel.Channel;

import javax.swing.JScrollPane;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;

/**
 * 公聊窗口
 * @author Administrator
 */
public class PublicChatFrame {

	private static JFrame publicChatFrame = new JFrame();
	private JTextArea sendText;
	private static JTextArea chatText = new JTextArea();
	private static Channel channel;

	public static void form(Channel channel, final String userName) {
		PublicChatFrame.channel = channel;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PublicChatFrame window = new PublicChatFrame(userName);
					PublicChatFrame.publicChatFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public PublicChatFrame(String userName) {
		initialize(userName);
	}

	public static JFrame getPublicChatFrame() {
		return publicChatFrame;
	}

	public static JTextArea getChatText() {
		return chatText;
	}

	public static void appendChatText(String msg) {
		PublicChatFrame.chatText.append(msg);
	}

	private void initialize(String userName) {
		publicChatFrame.setTitle("公聊  [" + userName + "]");
		publicChatFrame.getContentPane().setBackground(new Color(255, 250, 250));
		publicChatFrame.getContentPane().setLayout(null);
		publicChatFrame.setResizable(false);
		publicChatFrame.setBounds(200, 200, 450, 350);
		publicChatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		chatText.setEditable(false);
		chatText.setFont(new Font("黑体", Font.PLAIN, 18));
		chatText.setBounds(22, 20, 400, 230);
		chatText.setLineWrap(true);
		JScrollPane chatPane = new JScrollPane(chatText);
		chatPane.setLocation(22, 15);
		chatPane.setSize(400, 220);
		sendText = new JTextArea();
		sendText.setFont(new Font("黑体", Font.PLAIN, 18));
		JScrollPane sendPane = new JScrollPane(sendText);
		sendPane.setLocation(22, 250);
		sendPane.setSize(300, 50);
		publicChatFrame.getContentPane().add(chatPane);
		publicChatFrame.getContentPane().add(sendPane);

		JButton sendButton = new JButton("发送");
		sendButton.setForeground(new Color(0, 0, 0));
		sendButton.setFont(new Font("方正舒体", Font.PLAIN, 22));
		sendButton.setBackground(new Color(220, 220, 220));
		sendButton.setBounds(342, 255, 80, 40);
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = "pub_" + sendText.getText() + "\n";
				channel.writeAndFlush(msg);
				sendText.setText("");
			}
		});
		publicChatFrame.getContentPane().add(sendButton);

		publicChatFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				EchoClient.isRun = false;
				publicChatFrame.dispose();
				PrivateChatFrame.getPrivateChatFrame().dispose();
				// 等待EchoClient关闭连接
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				super.windowClosing(e);
			}
		});
	}

}
