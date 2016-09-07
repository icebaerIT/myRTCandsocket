/**
 * Created by joseber on 2016/9/6.
 */





//初始化
window.onload = function(){
    console.log("屏幕尺寸为"+screen.width);
    
    //设置部分尺寸
    document.getElementById("title").setAttribute("style","width:"+screen.width+"px;");
    document.getElementById("JoinRoomBox").setAttribute("style","width:"+screen.width+"px;");


};


function Join(){
    var JROM = document.getElementById("JoinRoom").value;

    window.location.href = "../video/video.html?Rnum=" +JROM+"";

}