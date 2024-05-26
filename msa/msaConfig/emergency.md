self-preservation
Eureka 서버가 client를 registry에 유지하는 방법에 대해 알아보자.

시작할 때 client는 server의 registry에 등록하기 위해 REST call을 한다.
instance가 graceful shutdown 되는 경우에는 그 instance는 Eureka 서버에 REST call을 보내 registry에서 제거하도록 한다.

ungraceful shutdown을 처리하기 위해서 서버는 client로부터 특정 간격으로의 heartbeat를 기대한다.
이것을 renewal이라 한다.

서버가 지정된 기간 동안 heartbeat 수신을 중지하면 오래된 인스턴스를 제거하기 시작한다.
client가 unregister call을 보내지 않으면 서버는 이를 ungraceful shutdown이라고 추측하게 된다.
heartbeat가 기대한 임계치보다 낮을 때 퇴출하려는 것을 멈추는 것을 self preservation 이라 한다.

This might happen in the case of a poor network partition, where the instances are still up,
but just can't be reached for a moment or in the case of an abrupt client shutdown.
(이는 네트워크 파티션이 불량한 경우 (인스턴스가 여전히 작동 중이지만 잠시 동안 또는 클라이언트가 갑자기 종료되는 경우) 발생할 수 있다.)

그리고 서버가 self-preservation 을 발동시키면 renewal rate가 기대한 임계값만큼 올라올때 까지 instance 퇴출작업을 멈춘다.

https://github.com/Netflix/eureka/wiki/Server-Self-Preservation-Mode
https://www.baeldung.com/eureka-self-preservation-renewal
https://d2.naver.com/helloworld/3963776
https://cloud.spring.io/spring-cloud-netflix/multi/multi__service_discovery_eureka_clients.html

속성들
eureka.server.enable-self-preservation: Configuration for disabling self-preservation – the default value is true

eureka.server.expected-client-renewal-interval-seconds: The server expects client heartbeats at an interval configured with this property – the default value is 30

(클라이언트의 하트비트 간격)
eureka.instance.lease-expiration-duration-in-seconds: Indicates the time in seconds that the Eureka server waits since it received the last heartbeat from a client before it can remove that client from its registry – the default value is 90

(마지막 하트비트를 받은 시점으로부터 기다릴 값, 이후 유레카 서버는 클라이언트를 레지스트리에서 삭제할 수 있다)
eureka.server.eviction-interval-timer-in-ms: This property tells the Eureka server to run a job at this frequency to evict the expired clients – the default value is 60 seconds

이 설정값마다 만료된 클라이언트를 탈퇴시킬 수 있다
eureka.server.renewal-percent-threshold: Based on this property, the server calculates the expected heartbeats per minute from all the registered clients – the default value is 0.85

임계값
eureka.server.renewal-threshold-update-interval-ms: This property tells the Eureka server to run a job at this frequency to calculate the expected heartbeats from all the registered clients at this minute – the default value is 15 minutes

임계값 측정 기간




이 계산이 맞을까?
Eureka 서버에 등록된 instances의 수가 12개라고 하자.
한 instance당 expected-client-renewal-interval-seconds 에 따르면 30초이니 분당 2회의 heartbeat를 받는다.
Eureka 서버가 분당 받는 heartbeat 수: 12 * 2 = 24회

renewal-threshold-update-interval-ms에 따르면 15분이니 15분간 임계치를 측정하는 것 같다.
15 * 24 = 360회

renewal-percent-threshold에 따르면 0.85 이하이면 self-preservation 모드가 발동된다.
360 * 0.85 = 306회

instance 한대당 15분간 30회의 heartbeat를 보낸다.

lease-expiration-duration-in-seconds에 따르면 90초가 지났음에도 heartbeat를 보내지않으면 그 instance는 registry에서 삭제된다.

eviction-interval-timer-in-ms에 따르면 1분간격으로 instance를 탈퇴시키는 job이 실행된다.

만약 12개의 instances 중 2개의 instance가 ungraceful shutdown 되었다면?
서버는 해당 2개의 instance를 종료시킬 수 없다 (registry에서 빼라는 REST call이 안오므로..)





주의하기
In most cases, the default configuration is sufficient. But for specific requirements, we might want to change these configurations. Utmost care needs to be taken in those cases to avoid unexpected consequences like wrong renew threshold calculation or delayed self-preservation mode activation.
대부분의 경우 디폴트 설정만으로도 충분하지만, 특정 상황에서는 설정을 변경해야 할 수도 있다.
다만 잘못된 threshold 계산 등으로 인해 더 안좋은 결과를 초래 할 수 있으니 조심해야 할 것이다.





근본 해결은?
위에 정답이 있다.

계산어쩌고저쩌고 했지만 문제는 graceful shutdown이 되지 않은 것이 문제다.

kill -9 는 좋지않다

kill -15 pid를 통해 안전하게 애플리케이션을 종료하라.

셧다운에서, 스프링의 TaskExecutor는 모든 running tasks를 interrupt 하는데, 이는 모든 tasks의 shutdown이 safe하게끔 할 수 있다.







아래  블로그를 참고하자.



https://www.baeldung.com/spring-boot-graceful-shutdown

https://www.baeldung.com/spring-boot-shutdown

https://jsonobject.tistory.com/417

https://blog.marcosbarbero.com/graceful-shutdown-spring-boot-apps/

https://heowc.dev/2018/12/27/spring-boot-graceful-shutdown/