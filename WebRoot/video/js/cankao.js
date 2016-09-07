// in controller (socket is already defined in controller)
var videolocal = document.getElementById('videolocal');
var videoremote = document.getElementById('videoremote');
var streamlocal = null;
var pc = null;
window.URL = window.URL || window.webkitURL;
navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia;
window.RTCPeerConnection = window.RTCPeerConnection || window.mozRTCPeerConnection || window.webkitRTCPeerConnection;

var configuration = {'iceServers': [
        // {'url': 'stun:stun.services.mozilla.com'}, 
        {'url': 'stun:stun.l.google.com:19302'}
    ]};

// run start(true) to initiate a call
$scope.start = function() {
    console.log('start');

    // get the local stream, show it in the local video element and send it
    navigator.getUserMedia({ "audio": true, "video": true }, function (stream) {
        videolocal.src = URL.createObjectURL(stream);
        pc = new RTCPeerConnection(configuration);
        pc.addStream(stream);

        // once remote stream arrives, show it in the remote video element
        pc.onaddstream = function (evt) {
            console.log('onaddstream');
            videoremote.src = URL.createObjectURL(evt.stream);
        };

        // send any ice candidates to the other peer
        pc.onicecandidate = function (evt) {
            console.log('onicecandidate');
            if(evt.candidate){
                socket.emit('video_call',{user:2, type: 'candidate', candidate: evt.candidate});
            }                       
        };                          

        // create an offer 
        pc.createOffer(function (offer) {
            socket.emit('video_call', {user:2,  type: "offer", offer: offer}); 
            pc.setLocalDescription(offer);
        }, function (error) { 
            alert("Error when creating an offer"); 
        });
    }, function () {alert('error in start')});
}
$scope.start();

socket.on('video_call', function (data) {
    console.log(data);
    //when somebody sends us an offer 
    function handleOffer(offer) {
        // this line is giving error
        pc.setRemoteDescription(new RTCSessionDescription(offer), function(){alert('success')}, function(e){ console.log(e); alert(e)});

        //create an answer to an offer 
        pc.createAnswer(function (answer) { 
            pc.setLocalDescription(answer); 
            socket.emit('video_call', {user:2, type: "answer", answer: answer});                            
        }, function (error) {
            console.log(error); 
            alert("Error when creating an answer"); 
        }); 
    };

    //when we got an answer from a remote user
    function handleAnswer(answer) { 
       pc.setRemoteDescription(new RTCSessionDescription(answer)); 
    };

    //when we got an ice candidate from a remote user 
    function handleCandidate(candidate) { 
       pc.addIceCandidate(new RTCIceCandidate(candidate)); 
    };

    switch(data['type']) { 
        case "offer": 
            handleOffer(data["offer"]); 
            break; 
        case "answer": 
            handleAnswer(data['answer']); 
            break; 
        //when a remote peer sends an ice candidate to us 
        case "candidate": 
            handleCandidate(data['candidate']); 
            break; 
        default:
            break; 
   }
});