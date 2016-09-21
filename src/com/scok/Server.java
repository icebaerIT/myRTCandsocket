package com.scok;

import java.io.BufferedReader;  
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.io.PrintWriter;  
import java.net.ServerSocket;  
import java.net.Socket;  
  
public class Server   
{  
    private ServerSocket ss;  
    private BufferedReader in;  
    private PrintWriter out;  
  
    public Server()   
    {  
        try   
        {  
            ss = new ServerSocket(20000);  
              
            System.out.println("服务器等待输入中");  
              
            while(true)   
            {  
            	Socket socket; 
                socket = ss.accept();  
                System.out.println("收到客户端");
                
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));  
                System.out.println("服务器新的BUFF");
                out = new PrintWriter(socket.getOutputStream(), true); 
                System.out.println("服务器新的WRITER");
                
                String line = in.readLine();  
                
                System.out.println("我是来自客户端的消息:" + line);  
                
                
                out.println("you input is :" + line);
                
/*                System.out.println("已经向客户端发送消息");*/
                  
                out.close();  
                in.close();  
                socket.close();  
                  
                if(line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit"))  
                    break;  
            }  
              
            ss.close();
            System.out.println("服务器全部关闭");
              
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
    public static void main(String[] args)   
    {  
        new Server();  
    }  
}  