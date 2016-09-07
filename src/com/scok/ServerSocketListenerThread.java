package com.scok;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketListenerThread{/* extends Thread */
	private static ServerSocket ss;
			

	public static void ListenerSocket(){
		try {
			ss = new ServerSocket(10000);
			System.out.println("服务器等待输入中"); 
			Socket socket = ss.accept();
			
			System.out.println("服务器已经连接客户端");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
	}
}
