package com.chat.client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.chat.Util.CharacterUtil;

public class ChatClient extends JFrame
{
	private JPanel westPanel;
	private JPanel btnPanel;
	private JScrollPane userListScroll;
	private JScrollPane messagesScroll;
	private JTextArea messagesArea;
	private JTextArea userListArea;
	private JTextField txt_message;
	private JButton btn_send;
	private JButton btn_clear;
	
	private Thread thread;
	
	public JTextArea getUserListArea()
	{
		return userListArea;
	}

	public void setUserListArea(JTextArea userListArea)
	{
		this.userListArea = userListArea;
	}
	
	public JTextArea getMessagesArea()
	{
		return messagesArea;
	}

	public ChatClient(Thread parentThread)
	{
		super("聊天室");
		this.thread = parentThread;
		this.initComponents();
		this.addWindowListener(new ClientExitEvent(parentThread));//监听聊天窗口关闭事件
		
	}
	
	private void initComponents()
	{
		westPanel = new JPanel();
		btnPanel = new JPanel();
		userListScroll = new JScrollPane();
		messagesScroll = new JScrollPane();
		messagesArea = new JTextArea();
		userListArea = new JTextArea();
		txt_message = new JTextField(20);
		btn_send = new JButton();
		btn_clear = new JButton();
		
		westPanel.setBorder(BorderFactory.createTitledBorder("聊天消息"));
		userListScroll.setBorder(BorderFactory.createTitledBorder("在线用户列表"));
		
//		txt_message.addKeyListener(new KeyAdapter()
//		{
//			@Override
//			public void keyPressed(KeyEvent e)
//			{
//				txt_messageKeyPressed(e);
//			}
//		});
		btn_send.setText("发送");
		btn_send.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				btn_sendActionPerformed(e);
			}
		});
		btn_clear.setText("清屏");
		btn_clear.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				btn_clearActionPerformed(e);
			}
		});
		
		messagesArea.setColumns(30);
		messagesArea.setRows(20);
		messagesArea.setEditable(false);
		
		userListArea.setColumns(10);
		userListArea.setRows(22);
		userListArea.setEditable(false);
		
		userListScroll.setViewportView(userListArea);
		messagesScroll.setViewportView(messagesArea);
		
		btnPanel.add(txt_message);
		btnPanel.add(btn_send);
		btnPanel.add(btn_clear);
		
		
		westPanel.setLayout(new BorderLayout());//这句很重要
		
		westPanel.add(messagesScroll, BorderLayout.NORTH);
		westPanel.add(btnPanel, BorderLayout.SOUTH);
		
		this.getContentPane().add(westPanel, BorderLayout.WEST);
		this.getContentPane().add(userListScroll, BorderLayout.EAST);
		
		this.pack();
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		
	}
	
	private void sendMessage()
	{
		String text = this.txt_message.getText();
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = dateformat.format(new Date());
		String message = CharacterUtil.CLIENT_NAME + " " + date + "\n" + "   " + text;
		
		if("".equals(text.trim()))
		{
			JOptionPane.showMessageDialog(this,"发送内容不能为空！","警告",JOptionPane.WARNING_MESSAGE); 
			this.txt_message.requestFocus();
			return;
		}
		
		this.txt_message.setText("");
		//开启客户端发送消息的线程
		thread = new SendMessageThread(message);
		thread.start();
	}
	
//	private void txt_messageKeyPressed(KeyEvent e)
//	{
//		sendMessage();
//	}
	
	private void btn_sendActionPerformed(ActionEvent e)
	{
		sendMessage();
	}
	
	private void btn_clearActionPerformed(ActionEvent e)
	{
		this.messagesArea.setText("");
	}
	
}

class ClientExitEvent extends WindowAdapter
{
	private Thread parentThread;
	
	public ClientExitEvent(Thread parentThread)
	{
		this.parentThread = parentThread;
	}
	
	@Override
	public void windowClosing(WindowEvent e)
	{
		int[] ports = CharacterUtil.getPortsArray(CharacterUtil.SERVER_PORT);
		int port = ports[0];
		
		try
		{
			InetAddress address = InetAddress.getByName(CharacterUtil.SERVER_HOST);
			Socket socket = new Socket(address, port);
			OutputStream os = socket.getOutputStream();
			os.write(CharacterUtil.CLIENT_NAME.getBytes());//发送退出聊天的用户名

	
			((ClientConnectThread)parentThread).setFlag(false);//ClientConnectThread中的flag为false时，会断开连接
			os.close();
			socket.close();
			System.exit(0);//退出聊天窗口
			
		}
		catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}





















