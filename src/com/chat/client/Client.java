package com.chat.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.chat.Util.CharacterUtil;

public class Client extends JFrame
{
	private JPanel jpanel;
	private JLabel lbl_username;
	private JLabel lbl_serverAddress;
	private JLabel lbl_port;
	private JTextField txt_username;
	private JTextField txt_address;
	private JTextField txt_port;
	private JButton btn_login;
	private JButton btn_reset;
	private Thread clientConnectThread;
	
	public Client(String name)
	{
		super(name);
		this.initComponents();
	}
	
	//初始化界面
	private void initComponents()
	{
		
		lbl_username = new JLabel();
		lbl_serverAddress = new JLabel();
		lbl_port = new JLabel();
		txt_username = new JTextField(20);
		txt_address = new JTextField(20);
		txt_port = new JTextField(20);
		btn_login = new JButton();
		btn_reset = new JButton();
		jpanel = new JPanel();
		
		txt_username.setText("aa");
		txt_address.setText("localhost");
		txt_port.setText("3333");
		
		lbl_username.setText("用户名");
		lbl_serverAddress.setText("服务器");
		lbl_port.setText("端口号");
		
		btn_login.setText("登录");
		btn_login.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				loginActionPerformed(e);
			}
		});
		
		btn_reset.setText("重置");
		btn_reset.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				resetActionPerformed(e);
			}
		});

		jpanel.add(lbl_username);
		jpanel.add(txt_username);
		jpanel.add(lbl_serverAddress);
		jpanel.add(txt_address);
		jpanel.add(lbl_port);
		jpanel.add(txt_port);
		jpanel.add(btn_login);
		jpanel.add(btn_reset);
		jpanel.setBorder(BorderFactory.createTitledBorder("用户登陆"));
		
		this.setSize(300, 180);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setAlwaysOnTop(true);
		this.setResizable(false);
		this.setLocationRelativeTo(null);//屏幕居中
		this.setVisible(true);
		this.getContentPane().add(jpanel);
	}
	
	
	//负责登录按钮的处理程序
	private void loginActionPerformed(ActionEvent e)
	{
		String username = this.txt_username.getText();
		String serverAddress = this.txt_address.getText();
		String hostPort = this.txt_port.getText();
		
		//校验用户名
		if(!CharacterUtil.isEmpty(username))
		{
			JOptionPane.showMessageDialog(this, "用户名不能为空", "警告", JOptionPane.WARNING_MESSAGE);
			this.txt_username.requestFocus();
			return;
		}
		
		if(!CharacterUtil.isUsernameCorrect(username))
		{
			JOptionPane.showMessageDialog(this, "用户名不能包含@ 与  / 字符！", "警告", JOptionPane.WARNING_MESSAGE);
			this.txt_username.requestFocus();
			return;
		}
		
		//校验服务器地址
		if(!CharacterUtil.isEmpty(serverAddress))
		{
			JOptionPane.showMessageDialog(this, "服务器地址不能为空", "警告", JOptionPane.WARNING_MESSAGE);
			this.txt_address.requestFocus();
			return;
		}
		
		//检验端口
		if(!CharacterUtil.isEmpty(hostPort))
		{
			JOptionPane.showMessageDialog(this, "端口号不能为空", "警告", JOptionPane.WARNING_MESSAGE);
			this.txt_port.requestFocus();
			return;
			
		}else if(!CharacterUtil.isNumeric2(hostPort)) {

			JOptionPane.showMessageDialog(this, "端口号必须为数字", "警告", JOptionPane.WARNING_MESSAGE);
			this.txt_port.requestFocus();
			return;
			
		}else if (!CharacterUtil.isPortCorrect(hostPort)) {
			
			JOptionPane.showMessageDialog(this, "端口号必须在1204与65535之间", "警告", JOptionPane.WARNING_MESSAGE);
			this.txt_port.requestFocus();
			return;
		}
		
		//程序执行到这里，说明数据都是合法的
		int port = Integer.parseInt(hostPort);
		
		CharacterUtil.SERVER_HOST = serverAddress;
		CharacterUtil.CLIENT_NAME = username;
		
		//点击登录，触发客户端启动连接服务器的线程，发送用户名和自己的随机生成的端口号以及自己的地址 -->到服务器
		clientConnectThread = new ClientConnectThread(this, serverAddress, port, username);
		clientConnectThread.start();
		
	}
	//负责重置的处理程序
	private void resetActionPerformed(ActionEvent e)
	{
		this.txt_username.setText("");
		this.txt_address.setText("");
		this.txt_port.setText("");
	}
	
	public static void main(String[] args)
	{
		new Client("客户端");
	}
	
}
