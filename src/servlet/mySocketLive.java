package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;







import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import net.sf.json.JSONObject;



@ServerEndpoint("/mySocketLive")
public class mySocketLive  {
	
	private Session session;  
    private static final Logger sysLogger = Logger.getLogger("sysLog"); 
    public static Map<String,Session> sessionMapList = new Hashtable<String,Session>();;
    
    
    
    public void sendLIVE(String LIVE){
    	try {
			this.session.getBasicRemote().sendText(LIVE);
		} catch (IOException e) {
			System.out.println("实况已断开");
		}
    }
    
    @OnOpen  
    public void open(Session session,  @PathParam(value = "user")String user) {
        this.session = session;
        sessionMapList.put(this.session.getId(), session);
        try {
			this.session.getBasicRemote().sendText("ok");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("发送失败");
		}

        sysLogger.info("*** WebSocket opened from sessionID " + session.getId() +"\n");
        

    }  
      
    @OnMessage  
    public void inMessage(String message){
    	
    	
        sysLogger.info("*** WebSocket Received from sessionId " + this.session.getId() + ": " + message);
        
    }  
      
    
    @OnError 
    public void onError(Throwable error) {
    	
    	sysLogger.info("socket出错了");
    	error.printStackTrace();
    	
    }
    
    
    @OnClose  
    public void end(@PathParam(value = "user")String user) {
    	
    	sessionMapList.remove(this.session.getId());
    	
/*    	sessionMap.remove(this.session.getId());*/
        sysLogger.info("*** WebSocket closed from sessionId " + this.session.getId());  
    }  	
}
