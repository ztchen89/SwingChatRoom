package com.chat.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

import com.chat.Util.CharacterUtil;

public class ReceiveMessageThread extends Thread
{
	private ChatClient chatClient;
	private ServerSocket serverSocket;
	
	public ReceiveMessageThread(ChatClient chatClient)
	{
		this.chatClient = chatClient;
		try
		{
			//与ServerUdpThread中的socket通信
			//也与Server类中的窗口关闭事件通信
			serverSocket = new ServerSocket(CharacterUtil.randomPort);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void run()
	{
		while(true)
		{
			try
			{
				Socket socket = serverSocket.accept();
				InputStream is = socket.getInputStream();
				byte[] buf = new byte[1024];
				int length = is.read(buf);
				String message = new String(buf, 0, length);
				
				if("SERVER_CLOSED".equals(message))
				{
					JOptionPane.showMessageDialog(chatClient,"服务器已关闭，程序将退出！","错误",JOptionPane.ERROR_MESSAGE); 
					System.exit(0);//程序退出  
				}
					chatClient.getMessagesArea().append(message + "\n");
					
					is.close();
					socket.close();
				
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}	
		}
		
	}
}
