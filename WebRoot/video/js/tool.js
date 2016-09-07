//获取get数据
function getUrlParam(name) {

    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
    if (r != null) return unescape(r[2]); return null; //返回参数值



}



//删除特定iD的元素
function removeElementbyID(ID){
    var idObject = document.getElementById(ID);
    if (idObject != null){
        idObject.parentNode.removeChild(idObject);
    }
}/**
 * Created by joseber on 2016/9/7.
 */
