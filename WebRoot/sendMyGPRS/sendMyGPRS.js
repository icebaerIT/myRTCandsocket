


function send(){
	
	var mySend = document.getElementById("text").value;
	console.log(mySend);
	
	$.ajax({
		type : "POST",
		url : "../servlet/sendMyGPRS",
		data : {
			mySend : mySend,
		},
		timeout:1000,
		traditional : true,
		async : false,
		success : function(data) {
			
		},
		error:function(){

			},
		complete : function(XMLHttpRequest,status){ //请求完成后最终执行参数
		
			
		}
		
	});
	
}