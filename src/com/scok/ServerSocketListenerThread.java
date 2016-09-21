package com.scok;

//import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
//import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketListenerThread{/* extends Thread */
	private static ServerSocket ss; 
/*    private BufferedReader in;  
    private PrintWriter out; */ 
    public static DataInputStream netInputStream;  
    public static DataOutputStream netOutputStream;
    static byte[] readLen=new byte[100];
    static byte[] writeLen=new byte[100];
    static String theString="";
    
    public static void sayHelloClient(String Hello) {
        System.out.println("对客户端说你好");
    	char[] theChar = Hello.toCharArray();
    	byte[] portNameListNum = new byte[theChar.length];
    	for(int i=0;i<theChar.length;i++){
    		portNameListNum[i] = (byte) theChar[i];
    	}
    	try{
    		//socket.sendUrgentData(0xFF);
    		netOutputStream.write(portNameListNum);
    		}catch(Exception ex){
    			System.out.println("对方已经断开");
    	}
	}
			

	public static void ListenerSocket(){
		try {
			ss = new ServerSocket(10000);
			
			while(true){//等待连接循环
				System.out.println("服务器等待连接中"); 
				Socket socket = ss.accept();
				System.out.println("服务器已经连接客户端");
	            netInputStream=new DataInputStream(socket.getInputStream());  
	            netOutputStream=new DataOutputStream(socket.getOutputStream());
	            sayHelloClient("Hello Client!");
	            while(true){//读取发送循环

	                System.out.println("读取数据");
	               	try{
	            		socket.sendUrgentData(0xFF);//判断连接是否正常
	            		netInputStream.read(readLen);
	            		}catch(Exception ex){
	            			System.out.println("对方已经断开停止读取数据");
	            			break;
	            	}
	                System.out.println("读取完毕");
	                theString="";
	               
	                for(int i = 0; i < 100;i++){
	                	if(readLen[i] == 0){
	                		break;
	                	}
	                	theString = theString+(char)readLen[i];
	                	//System.out.println((char)readLen[i]);
	                }
	                for(int i=0 ;i < readLen.length; i++){
	                	if(readLen[i] == 0){
	                	 break;	
	                	}
	                	readLen[i] = 0;
	                }
	                System.out.println(theString);
	                if(theString.equalsIgnoreCase("exit")){//获取到信息后如果是exit就断开连接
	                	System.out.println("服务接收到exit");
	                	String byebye = "byebye";//断开后说拜拜
	                	char[] theChar = byebye.toCharArray();
	                	byte[] portNameListNum = new byte[theChar.length];
	                	for(int i=0;i<theChar.length;i++){
	                		portNameListNum[i] = (byte) theChar[i];
	                	}
	                	try{
	                		//socket.sendUrgentData(0xFF);
	                		netOutputStream.write(portNameListNum);
	                		}catch(Exception ex){
	                			System.out.println("对方已经断开");
	                	}
	                	
	                	break;
	                }

					
	            }
	            netInputStream.close();  
	            netOutputStream.close();  
                socket.close();	
                System.out.println("服务器主动断开");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
	}
	public static void main(String[] args){
		ListenerSocket();
	}
}

