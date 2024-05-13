# solo

참조 : https://github.com/Benkoff/WebRTC-SS<br>
참조 : https://velog.io/@leeyw2709/WebRTC%EC%9D%98-%EB%8F%99%EC%9E%91-%EB%B0%8F-%EA%B5%AC%ED%98%84by.-socket.io#1-offers-peer-a<br>

webRTC mesh 방식으로 1:1 비디오 채팅을 구현하고, 시그널 서버로 spring websocket 을 통해 peer 의 통신 수립을 확인한다.<br>
랜덤 채팅을 구현하고 spring security 로 인증을 통해 회원간의 채팅을 만들었다.

navigator.mediaDevices.getUserMedia(constraints) 로 미디어 화면을 가져오고,<br>

let desc = new RTCSessionDescription(message.sdp);<br>
myPeerConnection.setRemoteDescription(desc)<br>
상대 peer 가 자신에게 접근하면 호출되어 연결을 수립해준다.

desc 데이터를 까보니, 흥미로운 데이터가 있었다.<br>
참조 : https://cryingnavi.github.io/webrtc/2016/12/30/WebRTC-SDP.html<br>
WebRTC 의 SDP 에 대해서 자세히 알아본다.<br>
SDP란 Session Description Protocol 의 약자로 연결하고자 하는 Peer 서로간의 미디어와 네트워크에 관한 정보를 이해하기 위해 사용된다.

전체 전문을 확인하지는 않았지만,<br>
m=audio 9 UDP/TLS/RTP/SAVPF 111 103 104 9 0 8 126<br>
    m 은 미디어라인을 의미한다. 이는 오디오 스트림에 관한 속성들에 대한 정보들을 가지고 있다. 순서는 아래와 같다<br>
    미디어 타입 (audio)<br>
    포트 번호 (9)<br>
    형식을 일컫는다 곧 사용되어지는 프로토콜이다 (UDP/TLS/RTP/SAVPF)<br>
    브라우저에서 미디어를 보내고 받을 수 있는 미디어 형식(코덱)을 가르키는데 이는 프로파일 번호이다. 프로파일 번호에 해당하는 코덱에 관한 상세 설명은 아래에 있다<br>
    Peer 간의 협상과정에서 앞에 있는 프로파일 번호를 순서대로 적용한다. 예를 들어 111번에 해당하는 코덱이 상호간에 가능한지 확인하고 가능하지 않다면 103번으로 넘어간다.<br><br>
c=IN IP4 0.0.0.0<br>
    c 는 실시간 트래픽을 보내고 받을 IP를 제공합니다. 그러나 ICE 에서 이미 IP 가 제공 되고 있으므로 c 라인의 IP 는 사용되지 않는다.<br><br>
a=rtcp:9 IN IP4 0.0.0.0<br>
    이 행은 RTCP에 사용될 IP 및 포트를 지정합니다. RTCP multiplex가 지원되므로 SRTP와 동일한 포트이다.

왜 흥미로웠는지는, 첫째로 webRTC 는 UDP 통신을 한다는데 그것을 확인하고 싶었다.<br>
둘째는 어떻게 브라우저끼리 통신이 되는지 궁금했는데 이 부분에서 많은 궁금증을 해결해주는 것 같았다.
