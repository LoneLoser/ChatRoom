package graphicInterface;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import communication.EchoClient;
import io.netty.channel.Channel;

/**
 * 注册窗口
 * @author Administrator
 */
public class RegisterFrame {

	private JFrame registerFrame;
	private JTextField usernameText;
	private JPasswordField passwordField, repasswordField;
	private JLabel existLabel, differentLabel;
	private static Channel channel;
	public static String flag = "";

	public static void form(Channel channel) {
		RegisterFrame.channel = channel;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RegisterFrame window = new RegisterFrame();
					window.registerFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public RegisterFrame() {
		initialize();
	}

	private void initialize() {
		registerFrame = new JFrame();
		registerFrame.setTitle("注册");
		registerFrame.getContentPane().setBackground(new Color(255, 250, 250));
		registerFrame.setForeground(new Color(255, 255, 255));
		registerFrame.setBackground(new Color(255, 255, 255));
		registerFrame.setResizable(false);
		registerFrame.setBounds(200, 200, 450, 350);
		registerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		registerFrame.getContentPane().setLayout(null);

		JLabel registerLabel = new JLabel("注册");
		registerLabel.setFont(new Font("方正舒体", Font.PLAIN, 36));
		registerLabel.setBounds(182, 19, 79, 51);
		registerFrame.getContentPane().add(registerLabel);

		JLabel usernameLabel = new JLabel("用户名：");
		usernameLabel.setFont(new Font("方正舒体", Font.PLAIN, 24));
		usernameLabel.setBounds(76, 83, 101, 39);
		registerFrame.getContentPane().add(usernameLabel);

		usernameText = new JTextField();
		usernameText.setFont(new Font("Consolas", Font.PLAIN, 24));
		usernameText.setBounds(176, 83, 178, 39);
		registerFrame.getContentPane().add(usernameText);
		usernameText.setColumns(10);

		JLabel passwordLabel = new JLabel("    密码：");
		passwordLabel.setFont(new Font("方正舒体", Font.PLAIN, 24));
		passwordLabel.setBounds(76, 135, 101, 39);
		registerFrame.getContentPane().add(passwordLabel);

		passwordField = new JPasswordField();
		passwordField.setFont(new Font("Consolas", Font.PLAIN, 24));
		passwordField.setBounds(176, 135, 178, 39);
		registerFrame.getContentPane().add(passwordField);

		JLabel repasswordLabel = new JLabel("重复密码：");
		repasswordLabel.setFont(new Font("方正舒体", Font.PLAIN, 24));
		repasswordLabel.setBounds(51, 187, 126, 39);
		registerFrame.getContentPane().add(repasswordLabel);

		repasswordField = new JPasswordField();
		repasswordField.setFont(new Font("Consolas", Font.PLAIN, 24));
		repasswordField.setBounds(176, 187, 178, 39);
		registerFrame.getContentPane().add(repasswordField);

		JButton registerButton = new JButton("注册");
		registerButton.setBackground(new Color(220, 220, 220));
		registerButton.setFont(new Font("方正舒体", Font.PLAIN, 24));
		registerButton.setBounds(179, 244, 86, 39);
		registerButton.addActionListener(new ActionListener() {
			public synchronized void actionPerformed(ActionEvent e) {
				String passwordMsg = new String(passwordField.getPassword());
				String repasswordMsg = new String(repasswordField.getPassword());
				if (!passwordMsg.equals(repasswordMsg)) {
					usernameText.setText("");
					passwordField.setText("");
					repasswordField.setText("");
					existLabel.setVisible(false);
					differentLabel.setVisible(true);
					//System.out.println("密码与重复密码不一致");
					return;
				}
				String msg = "reg_" + usernameText.getText() + "_" + passwordMsg + "\n";
				channel.writeAndFlush(msg);
				//等待注册结果
				while (flag.isEmpty()) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				if ("true".equals(flag)) {
					existLabel.setVisible(false);
					differentLabel.setVisible(false);
					registerFrame.dispose();
					LoginFrame.form(channel);
				} else if ("false".equals(flag)) {
					usernameText.setText("");
					passwordField.setText("");
					repasswordField.setText("");
					existLabel.setVisible(true);
					differentLabel.setVisible(false);
					//System.out.println("该用户已存在");
				}
				flag = "";
			}
		});
		registerFrame.getContentPane().add(registerButton);

		existLabel = new JLabel("该用户已存在");
		existLabel.setForeground(new Color(255, 0, 0));
		existLabel.setFont(new Font("方正舒体", Font.PLAIN, 20));
		existLabel.setBounds(161, 60, 120, 22);
		existLabel.setVisible(false);
		registerFrame.getContentPane().add(existLabel);

		differentLabel = new JLabel("密码不一致");
		differentLabel.setForeground(new Color(255, 0, 0));
		differentLabel.setFont(new Font("方正舒体", Font.PLAIN, 20));
		differentLabel.setBounds(168, 60, 106, 22);
		differentLabel.setVisible(false);
		registerFrame.getContentPane().add(differentLabel);

		registerFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				EchoClient.isRun = false;
				registerFrame.dispose();
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
