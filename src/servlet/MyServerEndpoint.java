package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;






import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import net.sf.json.JSONObject;



@ServerEndpoint("/webSocket")
public class MyServerEndpoint  {
	private Session session;  
    private static final Logger sysLogger = Logger.getLogger("sysLog"); 
/*    public static Map<String,Session> sessionMap = new Hashtable<String,Session>();*/
    public static Map<String,Map<String,Session>> sessionMapList = new Hashtable<String,Map<String,Session>>();;
    public static Map<String,String> sessionInRoom = new Hashtable<String,String>();
    
    
    @OnOpen  
    public void open(Session session,  @PathParam(value = "user")String user) {
        this.session = session;
/*        sessionMap.put(session.getId(),session);*/
        Map<String,Object> MyID = new HashMap<String, Object>();

        		try {
        			MyID.put("event", "myID");
        			MyID.put("data", this.session.getId());
        			this.session.getBasicRemote().sendText(JSONObject.fromObject(MyID).toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

     
        sysLogger.info("*** WebSocket opened from sessionID " + session.getId() +"\n");
        

    }  
      
    @OnMessage  
    public void inMessage(String message){
    	
    	
        sysLogger.info("*** WebSocket Received from sessionId " + this.session.getId() + ": " + message);
        
        JSONObject theJson = JSONObject.fromObject(message);
        String event = theJson.getString("event");
        
       
        if(event.equals("Rnum")){	/*识别房间房间*/
        	String Rnum = theJson.getString("Rnum");
        	/*默认没有房间*/
        	Boolean noRoom = true;
        	/*遍历寻找房间*/
            for (String key : sessionMapList.keySet()) {
            	if(key.equals(Rnum)){ //找到房间后把用户放进房间里面
            		
            	/*	this.session;*/
            		Map<String, Object> reRoom = new HashMap<String, Object>();
            		noRoom = false;//房间存在
            		Map<String, Session> theRoom = sessionMapList.get(key);
            		int theRoomNum = 0;
            		ArrayList<String>  UserkeyList = new ArrayList<String> (); 
            		theRoom.put(this.session.getId(), this.session);
            		sessionInRoom.put(this.session.getId(), Rnum);
            		/*返回房间信息,因为房间存在所以要将房间内的其他用户的数量和ID收集起来返告诉新加入着需要创建几次offer*/
            		for (String Userkey : theRoom.keySet()) {
            			if(!Userkey.equals(this.session.getId())){
            				UserkeyList.add(Userkey);
            				theRoomNum++;
            			}
                    }
            		reRoom.put("sessionID", UserkeyList);
            		reRoom.put("Usernum",theRoomNum);
            		reRoom.put("event","needOffer");
            		/*返回数据*/
            		try {
						this.session.getBasicRemote().sendText(JSONObject.fromObject(reRoom).toString());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		
            		break;
            	}
            }
            /*判断房间是否存在若不存在就创建新房间*/
            if(noRoom){
            	sessionMapList.put(Rnum,new Hashtable<String,Session>());//创建新房间
            	sessionMapList.get(Rnum).put(this.session.getId(), this.session);//把用户加入房间
            	sessionInRoom.put(this.session.getId(), Rnum);//保存房间号
            }
        }else if(event.equals("talk")){//如果是talk就群发给当前房间的所有人
        	String roomNum= sessionInRoom.get(this.session.getId());//找到用户所在房间号
        	Map<String, Session> theRoom = sessionMapList.get(roomNum);//找到房间
        	for (String Userkey : theRoom.keySet()) {
        		try {
        			
					theRoom.get(Userkey).getBasicRemote().sendText(message);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

            }
        	
        	
        }
        else{
        	String myRoom = sessionInRoom.get(this.session.getId());//获取自己的房间
        	Map<String, Session> theRoom = sessionMapList.get(myRoom);//通过房间号获取房间
        	Session session = theRoom.get(theJson.getString("targetID"));//通过目标ID获取目标的session
        	try {//发送数据
				session.getBasicRemote().sendText(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	
        };
        
        
        
        
        /*遍历*/
/*        for (String key : sessionMap.keySet()) {
        	if(!key.equals(this.session.getId())){
        		try {
        			sessionMap.get(key).getBasicRemote().sendText(message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }*/
/*        try {
			session.getBasicRemote().sendText(message+"  你好");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			sysLogger.info("WebSocket 响应消息出错");
			e.printStackTrace();
		}*/
    }  
      
    
/*    @OnError 
    public void onError(Throwable error) {
    	
    	sysLogger.info("socket出错了");
    	error.printStackTrace();
    	
    }
    */
    
    @OnClose  
    public void end(@PathParam(value = "user")String user) {
    	
    	Map<String, Object> reRoom = new HashMap<String, Object>();//创建返回仓库
    	
    	
    	reRoom.put("event", "bye");//模式为再见
    	
    	reRoom.put("myID", this.session.getId());//再见的人的ID放进去
    	
    	String roomNum= sessionInRoom.get(this.session.getId());//找到用户所在房间号
    	
    	
    	sessionInRoom.remove(this.session.getId());//删除房间记录
    	
    	Map<String, Session> theRoom = sessionMapList.get(roomNum);//找到房间
    	
    	theRoom.remove(this.session.getId());//令用户离开房间
    	
    	for (String Userkey : theRoom.keySet()){//遍历房间把自己退出的消息告诉其他人
    			try {
					theRoom.get(Userkey).getBasicRemote().sendText(JSONObject.fromObject(reRoom).toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	}
    	
/*    	sessionMap.remove(this.session.getId());*/
        sysLogger.info("*** WebSocket closed from sessionId " + this.session.getId());  
    }  	
}
