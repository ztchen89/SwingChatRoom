package com.chat.sever;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;


import com.chat.Util.CharacterUtil;

public class ServerConnectThread extends Thread
{
	private Server mainServer; //对主线程的引用
	private ServerSocket serverSocket;
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	
	public ServerConnectThread(Server mainServer,String threadName,int port)
	{
		this.mainServer = mainServer;
		this.setName(threadName);//线程的名字
		try
		{
			serverSocket = new ServerSocket(port);
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
				this.socket = this.serverSocket.accept();
				inputStream = socket.getInputStream();
				outputStream = socket.getOutputStream();
				
				byte[] buf = new byte[1024];
				int length = inputStream.read(buf);
				
				String info = new String(buf, 0, length);//客户端发送过来的信息
				//解析客户端发送过来的信息
				int index = info.lastIndexOf("@@@");
				String username = info.substring(0, index);
				
				int lastIndex = info.lastIndexOf("@");
				String clientPort = info.substring(lastIndex + 1);
				
				Server server = (Server) mainServer;
				Map<String , String> map = server.getMap();
				//服务器判断用户名是否存在,若存在，将所有流都关闭
				if(CharacterUtil.isUsernameExists(map, username))
				{
					String error = CharacterUtil.ERROR;
					outputStream.write(error.getBytes());
					
					inputStream.close();
					outputStream.close();
					socket.close();
				}
				else
				{
					String success = CharacterUtil.SUCCESS;
					String info2 = success + "@@@" + CharacterUtil.PORT + "_" + CharacterUtil.PORT2;
					
					map.put(username, clientPort);//将新用户的名字和端口号加入到map中
					
					server.updateUsersList(); //将服务器端用户列表更新
					
					//这一段干什么用的？很重要，要理解
					outputStream.write(info2.getBytes());
					inputStream.close();
					outputStream.close();
					socket.close();
					
					//发送用户列表给每一个客户端
					CharacterUtil.sendUsersList2Clients(map);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
