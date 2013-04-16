package com.chat.sever;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import com.chat.Util.CharacterUtil;

public class receiveClientExitThread extends Thread
{
	private Server mainServer;
	private ServerSocket serverSocket;
	
	public receiveClientExitThread(Server mainServer)
	{
		this.mainServer = mainServer;
		
		try
		{
			this.serverSocket = new ServerSocket(CharacterUtil.PORT);
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
				byte[] buf = new byte[1024];
				int length = is.read(buf);
				String username = new String(buf, 0, length);//获取ChatClient中
				
				Map<String, String> map = mainServer.getMap();
				map.remove(username);
				mainServer.updateUsersList();//服务器更新自身在线用户列表
				
				CharacterUtil.sendUsersList2Clients(map);//发送用户列表给每一个客户端
				
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
