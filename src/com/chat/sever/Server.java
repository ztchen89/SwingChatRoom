package com.chat.sever;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.chat.Util.CharacterUtil;

public class Server extends JFrame
{
	private JPanel northPanel;
	private JPanel southPanel;
	//northPanel上的控件
	private JLabel lbl_server;
	private JLabel lbl_status;
	private JLabel lbl_port;
	private JTextField txt_port;
	private JButton btn_start;
	//southPanel上的控件
	private JTextArea userListArea;
	private JScrollPane jScrollPane;
	
	private Thread connectThread;
	private Thread receiveClientExitThread;
	private Thread serverUdpThread;
	
	private Map<String, String> map = new HashMap<String, String>();//用户名及其端口的映射
	
	public Map<String, String> getMap()
	{
		return map;
	}

	public void setMap(Map<String, String> map)
	{
		this.map = map;
	}

	public Server(String name)
	{
		super(name);
		this.initCompoents();
		this.addWindowListener(new ServerClosed(this));//监听窗口关闭
	}
	
	//更新服务器端的在线用户列表
	public void updateUsersList()
	{
		this.userListArea.setText("");//先清空之前的在线用户列表
		
		for(Iterator<String> iter = map.keySet().iterator(); iter.hasNext();)
		{
			String username = iter.next();
			
			this.userListArea.append(username + "\n");
		}
	}
	
	private void initCompoents()
	{
		northPanel = new JPanel();
		southPanel = new JPanel();
		
		lbl_server = new JLabel();
		lbl_status = new JLabel();
		lbl_port = new JLabel();
		txt_port = new JTextField(5);
		btn_start = new JButton();
		
		txt_port.setText("3333");
		
		userListArea = new JTextArea();
		jScrollPane = new JScrollPane();
		
		northPanel.setBorder(BorderFactory.createTitledBorder("服务器信息"));
		southPanel.setBorder(BorderFactory.createTitledBorder("在线用户列表"));
		
		lbl_server.setText("服务器状态：");
		lbl_status.setText("停止");
		lbl_status.setForeground(Color.RED);
		
		lbl_port.setText("端口号：");
		txt_port.setToolTipText("输入1024-65535之间的任意端口号");
		
		btn_start.setText("启动服务器");
		btn_start.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				startServer();
			}
		});
		
		this.userListArea.setEditable(false);//使得textarea不可编辑
		this.userListArea.setForeground(new Color(245, 0, 0));
		
		//将相关控件加入norhPanel中
		northPanel.add(lbl_server);
		northPanel.add(lbl_status);
		northPanel.add(lbl_port);
		northPanel.add(txt_port);
		northPanel.add(btn_start);
		
		
		userListArea.setColumns(40);
		userListArea.setRows(20);
		userListArea.setForeground(new Color(0, 51, 204));
		//将userListArea加入到jScrollPane中
		jScrollPane.setViewportView(userListArea);
		
		southPanel.add(jScrollPane);
		
		//将两个panel加到当前JFrame中
		this.getContentPane().add(northPanel, BorderLayout.NORTH);
		this.getContentPane().add(southPanel, BorderLayout.SOUTH);
		
		//设置关闭的方式
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//设置为不可调整大小
		this.setResizable(false);
		//调整为最佳大小
		this.setSize(500, 450);
		this.setAlwaysOnTop(true);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
		
	}
	
	//端口号的输入校验,并启动服务器
	private void startServer()
	{
		String hostPort = this.txt_port.getText();
		
		if(!(CharacterUtil.isEmpty(hostPort)))
		{
			JOptionPane.showMessageDialog(this, "端口号不能为空", "警告", JOptionPane.WARNING_MESSAGE);
			
			this.txt_port.requestFocus();
			return;
		}
		
		if(!CharacterUtil.isNumeric2(hostPort))
		{
			JOptionPane.showMessageDialog(this, "端口号必须为数字", "警告", JOptionPane.WARNING_MESSAGE);
			
			this.txt_port.requestFocus();
			return;
		}
		
		if(!CharacterUtil.isPortCorrect(hostPort))
		{
			JOptionPane.showMessageDialog(this, "端口号必须在1204与65535之间", "警告", JOptionPane.WARNING_MESSAGE);
			
			this.txt_port.requestFocus();
			return;
		}
		
		int port = Integer.parseInt(hostPort);
		//启动服务器连接监听线程
		connectThread = new ServerConnectThread(this, "connect thread", port);
		connectThread.start();
		//启动服务器端监听接收客户端退出的线程
		receiveClientExitThread = new receiveClientExitThread(this);
		receiveClientExitThread.start();
		//启动服务器端接收和发送消息的线程
		serverUdpThread = new ServerUdpThread(this);
		serverUdpThread.start();
		
		this.lbl_status.setText("运行中");
		this.txt_port.setEnabled(false);
		this.btn_start.setEnabled(false);
		
	}
	
	
	public static void main(String[] args)
	{
		new Server("服务器");
	}
	
}


class ServerClosed extends WindowAdapter
{
	private Server server;
	
	public ServerClosed(Server server)
	{
		this.server = server;
	}
	
	@Override
	public void windowClosing(WindowEvent e)
	{
		try
		{
			Map<String, String> map = server.getMap();
			Iterator<Map.Entry<String, String>> iter = map.entrySet().iterator();
			//当服务端关闭时，遍历发送给每一个客户端一个SERVER_CLOSED的字符串
			while(iter.hasNext())
			{
				Map.Entry<String, String> me = iter.next();
				String username = me.getKey();
				String ports = me.getValue();
				
				int index = ports.indexOf("_");
				int port = Integer.parseInt(ports.substring(0, index));//获取客户端接收消息的端口
				int lastIndex = ports.lastIndexOf("_");
				String address = ports.substring(lastIndex + 1);//获取客户端ip字符串
				InetAddress clientAddress = InetAddress.getByName(address);//获取客户端ip

				String serverClosedMessage = "SERVER_CLOSED";//服务器关闭时要发送给客户端的字符串
				
				Socket socket = new Socket(clientAddress, port);
				OutputStream os = socket.getOutputStream();
				
				os.write(serverClosedMessage.getBytes());//发送数据
				os.close();
				socket.close();
			}
		}
		catch (Exception e2)
		{
			e2.printStackTrace();
		}
		
		System.exit(0);//关闭服务器端
	}
}
