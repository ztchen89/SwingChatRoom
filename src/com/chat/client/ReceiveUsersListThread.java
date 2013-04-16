package com.chat.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.chat.Util.CharacterUtil;

public class ReceiveUsersListThread extends Thread
{
	private ChatClient chatClient;
	private ServerSocket serverSocket;
	
	public ReceiveUsersListThread(ChatClient chatClient)
	{
		this.chatClient = chatClient;
		try
		{
			serverSocket = new ServerSocket(CharacterUtil.randimPort2);//与receiveClientExitThread通信
		}
		catch (IOException e)
		{
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
				byte[] buf = new byte[4096];
				int length = is.read(buf);
				String userList = new String(buf, 0, length);
				
				chatClient.getUserListArea().setText(userList);//更新客户端的用户列表
				
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
