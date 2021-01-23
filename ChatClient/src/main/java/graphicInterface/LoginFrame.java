package graphicInterface;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;

import communication.EchoClient;
import io.netty.channel.Channel;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JPasswordField;

/**
 * 登录窗口
 * @author Administrator
 */
public class LoginFrame {

	private JFrame loginFrame;
	private JTextField usernameText;
	private JPasswordField passwordField;
	private JLabel failLabel;
	private static Channel channel;
	public static String flag = "";

	/**
	 * 运行窗体
	 * @param channel
	 */
	public static void form(Channel channel) {
		LoginFrame.channel = channel;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginFrame window = new LoginFrame();
					window.loginFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 创建窗体
	 */
	public LoginFrame() {
		initialize();
	}

	/**
	 * 初始化窗体
	 */
	private void initialize() {
		loginFrame = new JFrame();
		loginFrame.setTitle("登录");
		loginFrame.getContentPane().setBackground(new Color(255, 250, 250));
		loginFrame.setForeground(new Color(255, 255, 255));
		loginFrame.setBackground(new Color(255, 255, 255));
		loginFrame.setResizable(false);
		loginFrame.setBounds(200, 200, 450, 350);
		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loginFrame.getContentPane().setLayout(null);
		
		JLabel loginLabel = new JLabel("登录");
		loginLabel.setFont(new Font("方正舒体", Font.PLAIN, 36));
		loginLabel.setBounds(176, 23, 79, 51);
		loginFrame.getContentPane().add(loginLabel);
		
		JLabel usernameLabel = new JLabel("用户名：");
		usernameLabel.setFont(new Font("方正舒体", Font.PLAIN, 24));
		usernameLabel.setBounds(76, 101, 101, 39);
		loginFrame.getContentPane().add(usernameLabel);
		
		usernameText = new JTextField();
		usernameText.setFont(new Font("Consolas", Font.PLAIN, 24));
		usernameText.setBounds(176, 101, 178, 39);
		loginFrame.getContentPane().add(usernameText);
		usernameText.setColumns(10);
		
		JLabel passwordLabel = new JLabel("    密码：");
		passwordLabel.setFont(new Font("方正舒体", Font.PLAIN, 24));
		passwordLabel.setBounds(76, 153, 101, 39);
		loginFrame.getContentPane().add(passwordLabel);
		
		passwordField = new JPasswordField();
		passwordField.setFont(new Font("Consolas", Font.PLAIN, 24));
		passwordField.setBounds(176, 153, 178, 39);
		loginFrame.getContentPane().add(passwordField);
		
		JButton registerButton = new JButton("注册");
		registerButton.setBackground(new Color(220, 220, 220));
		registerButton.setFont(new Font("方正舒体", Font.PLAIN, 24));
		registerButton.setBounds(100, 225, 86, 39);
		registerButton.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				loginFrame.dispose();
				RegisterFrame.form(channel);
			}
		});
		loginFrame.getContentPane().add(registerButton);
		
		JButton loginButton = new JButton("登录");
		loginButton.setBackground(new Color(220, 220, 220));
		loginButton.setFont(new Font("方正舒体", Font.PLAIN, 24));
		loginButton.setBounds(245, 225, 86, 39);
		loginButton.addActionListener(new ActionListener() {
			public synchronized void actionPerformed(ActionEvent e) {
				String msg = "log_" + usernameText.getText() + "_" + new String(passwordField.getPassword()) + "\n";
				channel.writeAndFlush(msg);
				//等待登录验证结果
				while(flag.isEmpty()) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				if("true".equals(flag)) {
					//登录成功
					failLabel.setVisible(false);
					loginFrame.dispose();
					PublicChatFrame.form(channel, usernameText.getText());
					PrivateChatFrame.form(channel, usernameText.getText());
				} else if("false".equals(flag)) {
					//登录失败
					usernameText.setText("");
					passwordField.setText("");
					failLabel.setVisible(true);
					//System.out.println("输入错误");
				}
				flag = "";
			}
		});
		loginFrame.getContentPane().add(loginButton);
		
		failLabel = new JLabel("登录失败");
		failLabel.setForeground(new Color(255, 0, 0));
		failLabel.setFont(new Font("方正舒体", Font.PLAIN, 20));
		failLabel.setBounds(175, 70, 86, 22);
		failLabel.setVisible(false);
		loginFrame.getContentPane().add(failLabel);
		
		loginFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				EchoClient.isRun = false;
				loginFrame.dispose();
				//等待EchoClient关闭连接
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
