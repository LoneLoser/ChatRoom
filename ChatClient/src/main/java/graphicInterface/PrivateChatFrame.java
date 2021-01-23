package graphicInterface;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import communication.EchoClient;
import io.netty.channel.Channel;
import javax.swing.JComboBox;

/**
 * 私聊窗口
 * @author Administrator
 */
public class PrivateChatFrame {

	private static JFrame privateChatFrame = new JFrame();
	private JTextArea sendText;
	private static JTextArea chatText = new JTextArea();
	private static JButton sendButton = new JButton("发送");
	private static JComboBox<String> objectBox = new JComboBox<String>();
	private static Channel channel;

	public static void form(Channel channel, final String userName) {
		PrivateChatFrame.channel = channel;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PrivateChatFrame window = new PrivateChatFrame(userName);
					PrivateChatFrame.privateChatFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public PrivateChatFrame(String userName) {
		initialize(userName);
	}

	public static JFrame getPrivateChatFrame() {
		return privateChatFrame;
	}

	public static JTextArea getChatText() {
		return chatText;
	}

	public static void appendChatText(String msg) {
		PrivateChatFrame.chatText.append(msg);
	}

	public static JComboBox<String> getObjectBox() {
		return objectBox;
	}

	public static void addOjectBox(String objName) {
		PrivateChatFrame.objectBox.addItem(objName);
	}
	
	public static void removeOjectBox(String objName) {
		PrivateChatFrame.objectBox.removeItem(objName);
	}

	public static JButton getSendButton() {
		return sendButton;
	}

	public static void setEnableSendButton(boolean enable) {
		PrivateChatFrame.sendButton.setEnabled(enable);
	}

	private void initialize(String userName) {
		privateChatFrame.setTitle("私聊  [" + userName + "]");
		privateChatFrame.getContentPane().setBackground(new Color(255, 250, 250));
		privateChatFrame.getContentPane().setLayout(null);
		privateChatFrame.setResizable(false);
		privateChatFrame.setBounds(700, 200, 450, 350);
		privateChatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		chatText.setEditable(false);
		chatText.setFont(new Font("黑体", Font.PLAIN, 18));
		chatText.setBounds(22, 20, 400, 230);
		chatText.setLineWrap(true);
		JScrollPane chatPane = new JScrollPane(chatText);
		chatPane.setLocation(22, 40);
		chatPane.setSize(400, 200);
		sendText = new JTextArea();
		sendText.setFont(new Font("黑体", Font.PLAIN, 18));
		JScrollPane sendPane = new JScrollPane(sendText);
		sendPane.setLocation(22, 250);
		sendPane.setSize(300, 50);
		privateChatFrame.getContentPane().add(chatPane);
		privateChatFrame.getContentPane().add(sendPane);

		sendButton.setForeground(new Color(0, 0, 0));
		sendButton.setFont(new Font("方正舒体", Font.PLAIN, 22));
		sendButton.setBackground(new Color(220, 220, 220));
		sendButton.setBounds(342, 255, 80, 40);
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = "pri_" + objectBox.getSelectedItem() + "_" + sendText.getText() + "\n";
				channel.writeAndFlush(msg);
				sendText.setText("");
			}
		});
		privateChatFrame.getContentPane().add(sendButton);

		objectBox.setBackground(new Color(255, 255, 255));
		objectBox.setFont(new Font("Consolas", Font.PLAIN, 18));
		objectBox.setBounds(22, 8, 148, 24);
		objectBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				appendChatText("您选择了与" + objectBox.getSelectedItem() + "的对话窗口\n");
			}
		});
		privateChatFrame.getContentPane().add(objectBox);

		privateChatFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				EchoClient.isRun = false;
				privateChatFrame.dispose();
				PublicChatFrame.getPublicChatFrame().dispose();
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
