/**
 * Created by joseber on 2016/9/7.
 */
var isCaller = null;
var getUserMedia = (navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia);
var video = document.getElementById('video');
var PeerConnection = (window.PeerConnection ||
window.webkitPeerConnection00 ||
window.webkitRTCPeerConnection ||
window.mozRTCPeerConnection);
var RTCIceCandidate = (window.mozRTCIceCandidate || window.RTCIceCandidate);
var RTCSessionDescription = (window.mozRTCSessionDescription || window.RTCSessionDescription);
var PeerConnectionsOfferlist = {};
var PeerConnectionsAnswerPclist = {};
var PeerConnectionslist = {};
var answerPc = null;
var offerPC = null;
var stream = "";
var sessionID = "";
var myID = "";

window.onload = initialize();


function createPeerConnections(isCall,sessionID){
    console.log("创建createPeerConnections");
    var Server = {"iceServers" : [{"url" : "stun:stun.l.google.com:19302"}]};
    var pc = new PeerConnection(Server);
    PeerConnectionslist[sessionID] = pc;

    //初始化参数
    //发送ICE候选到其他客户端
    pc.onicecandidate = function(event){
        ws.send(JSON.stringify({
            "myID":myID,
            "event": "__ice_candidate",
            "targetID":sessionID,
            "data": {
                "candidate": event.candidate
            }
        }));
    };
    //如果检测到媒体流连接到本地，将其绑定到一个video标签上输出
    pc.onaddstream = function(event){

        var theVideo = "<video class='videoSize' id="+sessionID+" src="+URL.createObjectURL(event.stream)+" autoplay></video>";
        document.getElementById("allvideo").innerHTML = document.getElementById("allvideo").innerHTML + theVideo;

    };
    pc.opened = function(){
        console.log("PeerConnectionOpen");
    };

    pc.addStream(stream);

    return pc;
}


function initializelocalstream(){
    getUserMedia.call(navigator, {
        "audio": true,
        "video": true
    }, function(localstream){
        //绑定本地媒体流到video标签用于输出
        stream = localstream;
        startWebSocket();
        video.src = URL.createObjectURL(stream);
        //向PeerConnection中加入需要发送的流
        console.log("开始初始化视频");
    },function(error){
        //处理媒体流创建失败错误
    });
}


function sendoffer(thews,thePC,target){
    console.log("进入offer传送带 ,目标:" + target);
    thePC.createOffer(function(desc){
        thePC.setLocalDescription(desc);
        var send  = JSON.stringify({
            "myID": myID,
            "targetID" : target,
            "event": "__offer",
            "data": {
                "sdp": desc
            }
        });
        ws.send(send);
        console.log("offer已经发送");
    },function(error){
        console.log(error);
    });




}


function startWebSocket() {
    console.log("连接WebSocket");
    if ('WebSocket' in window)
        ws = new WebSocket("wss://"+window.location.host+"/myRTC/webSocket");
    else if ('MozWebSocket' in window)
        ws = new MozWebSocket("wss://"+window.location.host+"/myRTC/webSocket");
    else
        alert("not support");

    ws.onerror = function () {

        console.log("websocket发生错误");

    };




    ws.onmessage = function(evt) {

        var json = JSON.parse(evt.data);
        console.log("返回了一条数据:" + json.event);


        if(json.event === "needOffer"){
            var sessionIDList = json.sessionID;
            //遍历传过来的名称
            for(var i=0; i < sessionIDList.length;i++){

                createPeerConnections(isCaller,sessionIDList[i]);
                var targetID = sessionIDList[i];
                console.log("发送offer给:"+targetID);


                sendoffer(ws,PeerConnectionslist[sessionIDList[i]],targetID);


                /*             		PeerConnectionslist[sessionIDList[i]].createOffer(function(desc){
                 console.log("将要发送offer");
                 opc.setLocalDescription(desc);
                 console.log("i的数值"+i);

                 console.log("offer已经发送");


                 },function(error){
                 console.log(error);
                 }); */

            }

        }else if(json.event === "__ice_candidate" ){
            console.log("json.myID是:"+json.myID);
            if(json.data.candidate != null){
                PeerConnectionslist[json.myID].addIceCandidate(new RTCIceCandidate(json.data.candidate));
            }
        }else if(json.event ==="__offer"){

            console.log("我是" + myID);
            console.log("收到__offer");
            console.log("本应该给"+json.targetID);
            console.log("目标:"+json.myID);
            createPeerConnections(isCaller,json.myID);
            /*             	answerPc.addStream(stream); */
            PeerConnectionslist[json.myID].setRemoteDescription(new RTCSessionDescription(json.data.sdp));
            PeerConnectionslist[json.myID].createAnswer(function(desc){
                PeerConnectionslist[json.myID].setLocalDescription(desc);
                ws.send(JSON.stringify({
                    "myID":myID,
                    "targetID":json.myID,
                    "event": "__answer",
                    "data": {
                        "sdp": desc
                    }
                }));
                console.log("answer已经发送");
            },function(error){
                console.log(error);
            });
        }else if(json.event ==="__answer"){

            console.log("收到__answer");
            PeerConnectionslist[json.myID].setRemoteDescription(new RTCSessionDescription(json.data.sdp));

        }else if(json.event ==="myID"){

            console.log("收到_myID");
            myID = json.data;
        }else if(json.event === "bye"){
            console.log(json.myID + " 给大家说再见了");
            removeElementbyID(json.myID);
        }else if(json.event === "talk"){
            console.log(json.myID+"说:"+json.data);
        }else{
            console.log("未知参数不错响应:"+evt.data);
        }

    };

    ws.onclose = function(evt) {
        console.log("WebSocketClose");
    };

    ws.onopen = function(evt) {
        //当连接后要发送房间号给socket
        var Rnum = getUrlParam("Rnum");
        if(Rnum != null){
            console.log("进入房间"+Rnum);
            ws.send(JSON.stringify({
                "event": "Rnum",
                "Rnum":Rnum
            }));
        }
        console.log("连接WebSocketOpen");

    };
}


function initialize() {
    console.log("初始化");

    initializelocalstream();

}


function sendMsg(){

    var talk = document.getElementById("writeMsg").value;
    console.log("我打算说"+talk);
    ws.send(JSON.stringify({
        "myID":myID,
        "event": "talk",
        "data":talk,
    }));

}