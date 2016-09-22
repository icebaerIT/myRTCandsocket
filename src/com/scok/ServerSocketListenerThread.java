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
    
    public static void sayHelloClient(String Hello) {//向客户端发送信息
        System.out.println("向客户端发送信息");
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
			
 
    public final static int heartJump(Socket socket){//心调程序
    	System.out.println("我的心跳了一下");
       	try{
    		socket.sendUrgentData(0xFF);//判断连接是否正常
    		sayHelloClient("Heart Jump!!!");
    		}catch(Exception ex){
    			return 0;
    	}
       	return 1;
    }
    
    
    public static Thread createHeartJumpThread(final Socket socket){
    	return new Thread(new Runnable(){//创建一个线程
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true){
					try {
						Thread.sleep(10000);
	    				if(heartJump(socket) == 0){
	    					System.out.println("发现客户端断开心跳跳出");
	    					break;
	    				};
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try {
					System.out.println("心跳尝试关闭socket");
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("心跳中关闭socket失败可能已经被关闭");
				}

			}  
			});  
    }
    
    
	public static void ListenerSocket(){
		try {
			ss = new ServerSocket(10000);
			
			while(true){//等待连接循环
				System.out.println("服务器等待连接中"); 
				
				final Socket socket = ss.accept();
				
				System.out.println("服务器已经连接客户端");
				
	            netInputStream=new DataInputStream(socket.getInputStream());  
	            
	            netOutputStream=new DataOutputStream(socket.getOutputStream());
	            
	            System.out.println("对客户端说你好");
	            
	            sayHelloClient("Hello Client!");
	            
	    		Thread t = createHeartJumpThread(socket);//创建一个心跳
	    		
	    		 t.start();

	            
	            while(true){//读取发送循环
	            	
		           

	                System.out.println("读取数据");
	               	try{
	            		//socket.sendUrgentData(0xFF);//判断连接是否正常
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
	                	sayHelloClient("byebye");
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

