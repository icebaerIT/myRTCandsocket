/**
 * Created by Administrator on 2016/9/22.
 */


function connect(){

	var p = document.getElementById("LIVE");
    if ('WebSocket' in window)
        ws = new WebSocket("ws://"+window.location.host+"/myRTCandsocket/mySocketLive");
    else if ('MozWebSocket' in window)
        ws = new MozWebSocket("ws://"+window.location.host+"/myRTCandsocket/mySocketLive");
    else
        alert("not support");


    ws.onerror = function () {

        console.log("websocket发生错误");

    };


    window.onbeforeunload = function () {
    	
    	

        ws.close();

    };


    ws.onmessage = function(evt) {
       
        p.innerHTML = p.innerHTML + evt.data + "<br/>";
    };

    ws.onclose = function(evt) {
   	 	p.innerHTML = p.innerHTML + "实时LIVE连接已经断开" + "<br/>";
   	 	alert("实时LIVE连接已经断开请刷新页面");
   	 	console.log("WebSocketClose");
    };

    ws.onopen = function(evt) {
    	console.log("WebSocket连接成功");
        p.innerHTML = p.innerHTML + "实时LIVE连接成功" + "<br/>";
    };
    
    
    document.getElementById("clearLIVE").onclick = function(){
    	
    	 p.innerHTML = "";
    	 p.innerHTML = p.innerHTML + "清屏成功" + "<br/>";
    };


}






connect();