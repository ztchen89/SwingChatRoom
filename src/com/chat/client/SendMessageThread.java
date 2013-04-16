package com.chat.client;

import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import com.chat.Util.CharacterUtil;

/*
 * 用UDP协议来实现聊天消息的发送和接收
 */
public class SendMessageThread extends Thread
{
	private String text;
	
	public SendMessageThread(String text)
	{
		this.text = text;
	}
	
	@Override
	public void run()
	{
		try
		{
			DatagramSocket datagramSocket = new DatagramSocket();
			int[] ports = CharacterUtil.getPortsArray(CharacterUtil.SERVER_PORT);
			int port = ports[1];
			System.out.println("send port" + port);
			InetAddress address = InetAddress.getByName(CharacterUtil.SERVER_HOST);
			
//			Socket socket = new Socket(address, port);
//			OutputStream os = socket.getOutputStream();
//			os.write(text.getBytes());
//			os.close();
//			socket.close();
			
			//要发送的数据包
			DatagramPacket datagramPacket = new DatagramPacket(text.getBytes(), text.length(), address, port);
			datagramSocket.send(datagramPacket);
			datagramSocket.close();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
}

