package com.chat.Util;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class CharacterUtil
{
	public static final String ERROR = "ERROR";
	public static final String SUCCESS = "SUCCESS";
	
	public static int PORT = generatePort();
	public static int PORT2 = generatePort();
	
	public static String SERVER_HOST; //服务器地址
	public static String SERVER_PORT; //服务器端口
	public static String CLIENT_NAME; //客户端用户名
	
	public static int randomPort = generatePort(); //接收消息的端口号
	public static int randimPort2 = generatePort(); //接收用户列表的端口号
	/*
	 * 判断字符串是否为空
	 */
	public static boolean isEmpty(String str)
	{
		if("".equals(str))
		{
			return false;
		}
		
		return true;
	}
	
	/*
	 * 判断字符串中是否包含 @ 和 / 符号
	 */
	public static boolean isUsernameCorrect(String str)
	{
		for(int i = 0; i < str.length(); i++)
		{
			char ch = str.charAt(i);//获取字符串中的每一个字符
			if('@' == ch || '/' == ch)
			{
				return false;
			}
		}
		return true;
	}
	
	/* 用JDK自带的函数 Character.isDigit
	 * 判断字符串是否为数字
	 */
	public static boolean isNumeric(String str)
	{
		for(int i = 0; i < str.length(); i++)
		{
			if(!Character.isDigit(str.charAt(i)))
			{
				return false;
			}
		}
		
		return true;
	}
	//用正则表达式来判断
	public static boolean isNumeric2(String str)
	{
		Pattern pattern = Pattern.compile("[0-9]*");
		
		return pattern.matcher(str).matches();
	}
	
	/*
	 * 判断服务器端口号是否在正确范围之内
	 */
	public static boolean isPortCorrect(String port)
	{
		int temp = Integer.parseInt(port);
		
		if(temp < 1024 || temp > 65535)
		{
			return false;
		}
		
		return true;
	}
	
	/*
	 * 产生一个随机的端口号，范围在1024-65535之间
	 */
	public static int generatePort()
	{
		int port = (int) (Math.random() * 5000 + 1024);
		return port;
	}
	
	/*
	 * 判断用户名在服务器上的在线用户列表是否存在,有返回true,没有返回false
	 */
	public static boolean isUsernameExists(Map<String, String> map, String username)
	{
		for(Iterator<String> iter = map.keySet().iterator();iter.hasNext();)
		{
			String tempKey = iter.next();
			
			if(tempKey.equals(username))
			{
				return true;
			}
		}
		
		return false;
	}
	/*
	 * 解析端口号字符串，返回两个端口号的数组
	 */
	public static int[] getPortsArray(String str)
	{
		int[] ports = new int[2];
		int index = str.indexOf("_");
		ports[0] = Integer.parseInt(str.substring(0, index));
		ports[1] = Integer.parseInt(str.substring(index + 1));
		
		return ports;
	}
	
	/*
	 * 遍历map中所有的key，即用户名,返回一个String
	 */
	public static String getUsersListString(Map<String, String> map)
	{
		StringBuffer sb = new StringBuffer();//存放所有用户名
		Iterator<String> iter = map.keySet().iterator();
		while(iter.hasNext())
		{
			String username = iter.next();
			sb.append(username + "\n");
		}
		
		return sb.toString();
	}
	
	/*
	 * 将用户列表发送给每一个客户端
	 */
	public static void sendUsersList2Clients(Map<String, String> map)
	{
		String userList = CharacterUtil.getUsersListString(map);//得到用户列表
		try
		{
			//将新的用户列表发送到每个客户端
			Iterator<Map.Entry<String, String>> iter2 = map.entrySet().iterator();
			while(iter2.hasNext())
			{
				Map.Entry<String, String> me = iter2.next();
				String temp = me.getValue();
				
				int index2 = temp.indexOf("_");
				int lastIndex2 = temp.lastIndexOf("_");
				int port = Integer.parseInt(temp.substring(index2 + 1, lastIndex2));//获取端口号
				String address = temp.substring(lastIndex2 + 1); 
				InetAddress clientAddress = InetAddress.getByName(address);//获取每个客户端的ip
				
				//与每一个客户端生成一个socket
				Socket socket = new Socket(clientAddress, port);
				System.out.println(socket);
				//发送用户列表
				OutputStream os = socket.getOutputStream();
				os.write(userList.getBytes());
				os.close();
				socket.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	
	
	
	
}
