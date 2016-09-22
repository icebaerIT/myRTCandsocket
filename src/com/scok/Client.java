package com.scok;

import java.io.*;  
import java.net.*;  
  
public class Client    
{  
	
    static String theString="";
	
    void query(String ip,int port)  
    {  
        DataInputStream netInputStream;  
        DataOutputStream netOutputStream;  
        Socket sc;  
        byte[] readLen=new byte[10];  
        try  
        {  
            sc=new Socket(ip,port);  
            netInputStream=new DataInputStream(sc.getInputStream());  
            netOutputStream=new DataOutputStream(sc.getOutputStream());
        	System.out.println("客户端读取数据");
            while(true){
            	netInputStream.read(readLen);
            	
/*                for(int i = 0; i < 1;i++){
                	if(readLen[i]==0){
                		theString="";
                		break;	
                	}
                	//theString=theString + (char)readLen[i];
                	
                }*/
            	System.out.print((char)readLen[0]);
            	theString=theString + (char)readLen[0];
                System.out.println("这局话是:"+theString);
                if(readLen[0] == 0x0A){
                	System.out.println("收到换行符进行初始化");
                	theString = "";
                }
                //System.out.println(theString);
                if(theString.equalsIgnoreCase("byebye")){
                	System.out.println("收到byebye进行断开");
                	String byebye = "exit";//断开后说拜拜
                	char[] theChar = byebye.toCharArray();
                	byte[] portNameListNum = new byte[theChar.length];
                	for(int i=0;i<theChar.length;i++){
                		portNameListNum[i] = (byte) theChar[i];
                	}
                	netOutputStream.write(portNameListNum);
                	break;
                }
            }
            netInputStream.close();  
            netOutputStream.close();  
            sc.close();	
            System.out.println("客户端主动断开");
        }  
        catch(Exception e)  
        {  
            e.printStackTrace();  
        }  
    } 
  
    public static void main(String[] args)  
    {  
    	Client client=new Client();  
        //client.query("123.57.207.4",10000); 
    	client.query("127.0.0.1",10000); 
    }  
}  