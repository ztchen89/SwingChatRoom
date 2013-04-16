package com.chat.sever;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.chat.Util.CharacterUtil;

public class ServerUdpThread extends Thread
{
	private Server mainServer;
	private ServerSocket serverSocket;
	private Socket socket2;
	public ServerUdpThread(Server mainServer)
	{
		this.mainServer = mainServer;
		
		try
		{
			serverSocket = new ServerSocket(CharacterUtil.PORT2);
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
				DatagramSocket datagramSocket = new DatagramSocket(CharacterUtil.PORT2);
				System.out.println(datagramSocket);
				byte[] buf = new byte[1024];
				DatagramPacket datagramPacket = new DatagramPacket(buf, 1024);
				datagramSocket.receive(datagramPacket);
				String message = new String(buf, 0, datagramPacket.getLength());//某个用户发来的信息
				System.out.println(message);
//				this.socket2 = serverSocket.accept();
//				InputStream is = socket2.getInputStream();
//				byte[] buf = new byte[1024];
//				int length = is.read(buf);
//				String message = new String(buf, 0, length);
				
				
				Map<String, String> map = mainServer.getMap();
				Iterator<String> iter = map.values().iterator();
				while(iter.hasNext())
				{
					String ports = iter.next();
					
					int index = ports.indexOf("_");
					int port = Integer.parseInt(ports.substring(0, index));
					int lastIndex = ports.lastIndexOf("_");
					String address = ports.substring(lastIndex + 1);
					System.out.println(address);
					
					Socket socket = new Socket(address, port);
					OutputStream os = socket.getOutputStream();
					os.write(message.getBytes());
					os.close();
					socket.close();
				}
				
				datagramSocket.close();//莫忘！
				
			}
			catch (SocketException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
