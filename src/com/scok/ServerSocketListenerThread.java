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
    static String theASCII = "";
    static int tooMoreZero = 0;
    static int overtime = 0;
    static int connectOpen = 0;
    
    
    public static void shutdownAll(Socket socket){
    	try {
			socket.close();
			netInputStream.close();
			netOutputStream.close();
			System.out.println("所有连接断开");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
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
    	System.out.println(overtime + ":我的心跳了一下");
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
						overtime += 1;
	    				if(heartJump(socket) == 0 || overtime >= 4){
	    					System.out.println("发现客户端断开或者客户端未发送信息超时");
	    					break;
	    				};
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

					System.out.println("心跳尝试关闭socket");
		            if(connectOpen == 1){
			            shutdownAll(socket);//关闭所有连接
		            }
		            connectOpen = 0;
		            System.out.println("心跳关闭操作完成");
		            
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
	            
	            connectOpen = 1;
	            
	            System.out.println("对客户端说你好");
	            
	            sayHelloClient("Hello Client!");
	            
	    		Thread t = createHeartJumpThread(socket);//创建一个心跳
	    		
	    		 t.start();

	            
	            while(true){//读取发送循环
	            	
		           

	                System.out.println("读取数据");
	               	try{
	            		socket.sendUrgentData(0xFF);//判断连接是否正常
	            		netInputStream.read(readLen);
	            		overtime = 0;
	            		}catch(Exception ex){
	            			System.out.println("对方已经断开停止读取数据");
	            			break;
	            	}
	                System.out.println("读取完毕");
	                theString="";
	                theASCII = "";
	                
	                for(int i = 0; i < 100;i++){//组合收到的数据
	                	if(readLen[i] == 0){
	                		tooMoreZero += 1; 
	                		break;
	                		
	                	}
	                	
	                	tooMoreZero = 0;//只要一次不是0就初始化;
	                	theString = theString+(char)readLen[i];
	                	theASCII = theASCII +","+ readLen[i];
	                	//System.out.println((char)readLen[i]);
	                }
	                
	                

	                
	                for(int i=0 ;i < readLen.length; i++){//readLen初始化
	                	if(readLen[i] == 0){
	                	 break;	
	                	}
	                	readLen[i] = 0;
	                }
	                
	                
	                
	                System.out.println(theString);
	                
	                
	                if(theString.equalsIgnoreCase("exit")||tooMoreZero >= 10){//获取到信息后如果是exit就断开连接,或者空循环太多次也跳出
	                	tooMoreZero = 0;
	                	System.out.println("服务接收到exit");
	                	sayHelloClient("byebye");
	                	break;
	                }

					
	            }
	            if(connectOpen == 1){
		            shutdownAll(socket);//关闭所有连接
	            }
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

