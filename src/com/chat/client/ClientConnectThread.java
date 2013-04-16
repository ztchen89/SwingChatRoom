package com.chat.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import com.chat.Util.CharacterUtil;

public class ClientConnectThread extends Thread
{
	private Client client;
	private String serverAddress;
	private int hostPort;
	private String username;
	private boolean flag = true;
	private Thread receiveMessageThread;//接收消息线程
	private Thread receiveUsersListThread;//接收在线用户列表线程
	
	public ClientConnectThread(Client client, String serverAddress, int hostPort, String username)
	{
		this.client = client;
		this.serverAddress = serverAddress;
		this.hostPort = hostPort;
		this.username = username;
	}
	
	public boolean isFlag()
	{
		return flag;
	}
	
	public void setFlag(boolean flag)
	{
		this.flag = flag;
	}
	
	@Override
	public void run()
	{
		Socket socket = null;
		InputStream is = null;
		OutputStream os = null;
		String error = CharacterUtil.ERROR;
		
		try
		{
			InetAddress host = InetAddress.getByName(serverAddress); //获取client面板所填写的服务器地址
			//表示客户端向服务器发起连接
			socket = new Socket(host, hostPort);

			is = socket.getInputStream();
			os = socket.getOutputStream();
			
			//封装要发送给服务器的信息（客户端地址，用户名和两个随机生成的端口号）
			int randomPort = CharacterUtil.randomPort; //接收聊天消息端口
			int randomPort2 = CharacterUtil.randimPort2;//接收在线用户列表端口
			
			String clientAddress = InetAddress.getLocalHost().toString();
			
			int l = clientAddress.indexOf("/");//因为clientAddress会有如下格式：XXX-PC/192.168.1.234
			clientAddress = clientAddress.substring(l + 1); //获取客户端ip
			
			//定义客户端发送给服务器信息的格式
			String clientInfo = username + "@@@" + randomPort + "_" + randomPort2 + "_" + clientAddress;
			
			os.write(clientInfo.getBytes()); //发送数据
			
			byte[] buf = new byte[100];
			int length = is.read(buf);
			String temp = new String(buf, 0, length);
			
			int lastIndex = temp.lastIndexOf("@");
			String portInfo = temp.substring(lastIndex + 1);
			
			CharacterUtil.SERVER_PORT = portInfo;//客户端与服务器连接成功后，存储服务端发送过来的两个端口号
			
			os.close();
			is.close();
			socket.close();
			
			//若服务端判断所发送的用户重名，会返回一个ERROR，根据这个来提示客户端更换用户名
			if(temp.equals(error))
			{
				JOptionPane.showMessageDialog(client, "用户名与已有用户名重名，请更换用户名！", "错误", JOptionPane.WARNING_MESSAGE);

				socket.close();
				is.close();
				os.close();
				return;
			}
			
				//进入聊天窗口
				ChatClient chatClient = new ChatClient(this);
				
				chatClient.setVisible(true);
				client.setVisible(false);
				
				//启动接收在线用户列表的线程
				receiveUsersListThread = new ReceiveUsersListThread(chatClient);
				receiveUsersListThread.start();
				
			
				//启动接收聊天消息的线程
				receiveMessageThread = new ReceiveMessageThread(chatClient);
				receiveMessageThread.start();
				
				
				
			//不断循环直到主屏幕退出	
			while(flag)
			{
				try
				{
					Thread.sleep(5000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			
		}
		catch (UnknownHostException e)
		{
			JOptionPane.showMessageDialog(client, "服务器地址有误，请正确填写地址！", "错误", JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
		}
		catch (ConnectException e) 
		{
			JOptionPane.showMessageDialog(client, "服务器未启动，请确认服务器正常！", "错误", JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
