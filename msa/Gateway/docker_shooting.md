

[컨테이너 외부의 파일을 컨테이너 내부에서 접근](#컨테이너-외부의-파일을-컨테이너-내부에서-접근)

[docker-컨테이너container간의-통신](#docker-컨테이너container간의-통신)

[Docker Compose 네트워크](#docker-compose-네트워크)

====================================================================================

클라이언트 호출 → api gateway → eureka server → 서버 확인 → api gateway → 해당서버로 이동

====================================================================================
### 컨테이너 외부의 파일을 컨테이너 내부에서 접근

도커 빌드 실행 후 http://localhost:8080/ 접속 시도,
그런데 들어가보면 또 오류가 발생할 것입니다.

연결 불가

컨테이너 외부의 파일을 컨테이너 내부에서 접근할 수 없어서 발생하는 오류와 비슷합니다.
네트워크 역시 로컬 네트워크와 컨테이너 내부의 네트워크를 연결시켜주는 작업이 필요합니다.

-p 옵션으로 로컬 포트번호:컨테이너 내부 포트번호 지정
출처: https://ttl-blog.tistory.com/761 [Shin._.Mallang:티스토리]

====================================================================================

### Docker 컨테이너(container)간의 통신

Docker 컨테이너(container)는 격리된 환경에서 돌아가기 때문에 기본적으로 다른 컨테이너와의 통신이 불가능합니다.
하지만 여러 개의 컨테이너를 하나의 Docker 네트워크(network)에 연결시키면 서로 통신이 가능해집니다.
이번 포스팅에서는 컨테이너 간 네트워킹이 가능하도록 도와주는 Docker 네트워크에 대해서 알아보도록 하겠습니다.
출처: https://www.daleseo.com/docker-networks/

$ docker network ls
    커맨드를 사용하면 현재 생성되어 있는 Docker 네트워크 목록을 조회할 수 있습니다.
```text
$ docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
143496b94e57        bridge              bridge              local
311d6534f79f        host                host                local
aa89f58200a6        none                null                local
```
bridge, host, none 은 Docker 데몬(daemon)이 실행되면서 디폴트로 생성되는 네트워크입니다.
대부분의 경우에는 이러한 디폴트 네트워크를 이용하는 것 보다는 사용자가 직접 네트워크를 생성해서 사용하는 것이 권장됩니다.

bridge, host, overlay 등 목적에 따라 다양한 종류의 네트워크 드라이버(driver)를 지원합니다.
    - bridge 네트워크는 하나의 호스트 컴퓨터 내에서 여러 컨테이너들이 서로 소통할 수 있도록 해줍니다.
    - host 네트워크는 컨터이너를 호스트 컴퓨터와 동일한 네트워크에서 컨테이너를 돌리기 위해서 사용됩니다.
    - overlay 네트워크는 여러 호스트에 분산되어 돌아가는 컨테이너들 간에 네트워킹을 위해서 사용됩니다.

본 포스팅에서는 기본값이고 가장 많이 사용되는 bridge 네트워크에 대해서만 다루도록 하겠습니다.

1. docker network create 커맨드를 사용해서 새로운 Docker 네트워크를 생성

    -d 옵션을 사용하지 않았기 때문에 기본값인 bridge 네트워크로 생성된 것을 볼 수 있습니다.
    - $ docker network inspect our-net
        ```json
        [
            {
                "Name": "our-net",
                "Id": "e6dfe4a9a5ec85abcb484662c30a3a0fc76df217dde76d52fac39fae8412ca68",
                "Created": "2020-04-26T19:23:04.563643516Z",
                "Scope": "local",
                "Driver": "bridge",
                "EnableIPv6": false,
                "IPAM": {
                    "Driver": "default",
                    "Options": {},
                    "Config": [
                    {
                        "Subnet": "172.19.0.0/16",
                        "Gateway": "172.19.0.1"
                    }
                ]
                },
                "Internal": false,
                "Attachable": false,
                "Ingress": false,
                "ConfigFrom": {
                  "Network": ""
                },
                "ConfigOnly": false,
                "Containers": {},
                "Options": {},
                "Labels": {}
            }
        ]
        ```
        Containers 항목을 보면 이 네트워크에 아무 컨테이너도 아직 연결되지 않은 것을 알 수 있습니다.

2. 네트워크에 컨테이너 연결
    
   $ docker run -itd --name one busybox
    ```json
        "Containers": {
            "660bafdce2996378cde070dfd894731bb90745e46d2ab10d6504c0cc9f4bdea9": {
                "Name": "one",
                "EndpointID": "40b4bbd8385debf86eef2fc2136315e1a82fa1ef72877bfae25477d6e8e46726",
                "MacAddress": "02:42:ac:11:00:04",
                "IPv4Address": "172.17.0.4/16",
                "IPv6Address": ""
            },
        },
    ```

   $ docker network connect our-net one
   one 컨테이너를 위에서 생성한 our-net 네트워크에 연결해보도록 하겠습니다.

    $ docker network inspect our-net
    ```json
        "Containers": {
            "660bafdce2996378cde070dfd894731bb90745e46d2ab10d6504c0cc9f4bdea9": {
                "Name": "one",
                "EndpointID": "cc490148a533d40b3aff33a421cc9a01c731c75a8deb70ab729a5358f2fd381c",
                "MacAddress": "02:42:ac:13:00:02",
                "IPv4Address": "172.19.0.2/16",
                "IPv6Address": ""
            }
        },
    ```
   Containers 항목에 one 컨테이너가 추가된 것을 볼 수 있습니다.
   one 컨테이너에 IP 172.19.0.2가 할당된 것도 확인할 수 있습니다.

3. 네트워크로부터 컨테이너 연결 해제

    하나의 컨테이너는 여러 개의 네트워크에 동시에 연결할 수 있습니다.
    
    최초에 one 컨테이너를 생성할 때 bridge 네트워크 붙었기 때문에,
    현재 one 컨테이너는 our-net 네트워크와 bridge 네트워크에 동시에 붙어있게 됩니다.
    
    one 컨테이너를 bridge 네트워크로부터 때어 내도록 하겠습니다.
    
    $ docker network disconnect bridge one

4. 두번째 컨테이너 연결

   이번에는 --network 옵션을 사용해서 컨테이너를 실행하면서 바로 연결할 네트워크를 지정해주도록 하겠습니다.
      $ docker run -itd --name two --network our-net busybox
      - 처음 실행한 명령어 $ docker run -itd --name one busybox
      ```json
        "Containers": {
            "0e7fe8a59f9d3f8bd545d3e557ffd34100a09b8ebe92ae5a375f37a5d072873d": {
                "Name": "two",
                "EndpointID": "a0e3625e48f0b833cd551d6dfb3b1a2a7614f1343adbc5e6aefa860d917ddea9",
                "MacAddress": "02:42:ac:13:00:03",
                "IPv4Address": "172.19.0.3/16",
                "IPv6Address": ""
            },
            "660bafdce2996378cde070dfd894731bb90745e46d2ab10d6504c0cc9f4bdea9": {
                "Name": "one",
                "EndpointID": "cc490148a533d40b3aff33a421cc9a01c731c75a8deb70ab729a5358f2fd381c",
                "MacAddress": "02:42:ac:13:00:02",
                "IPv4Address": "172.19.0.2/16",
                "IPv6Address": ""
            }
        },
      ``` 
      our-net 네트워크의 상세 정보를 확인해보면 two 컨테이너에 IP 172.19.0.3가 할당되어 연결되어 있는 것을 확인할 수 있습니다.

5. 컨테이너 간 네트워킹
   one 컨테이너에서 two 컨테이너를 상대로, 또 반대로도 ping 명령어를 날려보겠습니다.
   컨테이너 이름을 호스트네임(hostname)처럼 사용할 수 있습니다.

    ```text
    $ docker exec one ping two
    PING two (172.19.0.3): 56 data bytes
    64 bytes from 172.19.0.3: seq=0 ttl=64 time=0.119 ms
    64 bytes from 172.19.0.3: seq=1 ttl=64 time=0.105 ms
    64 bytes from 172.19.0.3: seq=2 ttl=64 time=0.116 ms
    64 bytes from 172.19.0.3: seq=3 ttl=64 time=0.883 ms
    64 bytes from 172.19.0.3: seq=4 ttl=64 time=0.127 ms
    
    $ docker exec two ping 172.19.0.2
    PING 172.19.0.2 (172.19.0.2): 56 data bytes
    64 bytes from 172.19.0.2: seq=0 ttl=64 time=0.927 ms
    64 bytes from 172.19.0.2: seq=1 ttl=64 time=0.079 ms
    64 bytes from 172.19.0.2: seq=2 ttl=64 time=0.080 ms
    64 bytes from 172.19.0.2: seq=3 ttl=64 time=0.079 ms
    64 bytes from 172.19.0.2: seq=4 ttl=64 time=0.107 ms
    ```

6. 네트워크 제거

   $ docker network rm our-net
   Error response from daemon: error while removing network: network our-net id e6dfe4a9a5ec85abcb484662c30a3a0fc76df217dde76d52fac39fae8412ca68 has active endpoints

    위와 같이 제거하려는 네트워크 상에서 실행 중인 컨테이너가 있을 때는 제거가 되지가 않습니다.
   그럴 때는 해당 네트워크에 연결되어 실행 중인 모든 컨테이너를 먼저 중지 시키고, 네트워크를 삭제해야 합니다.

7. 네트워크 청소

   하나의 호스트 컴퓨터에서 다수의 컨테이너를 돌리다 보면 아무 컨테이너도 연결되어 있지 않은 네트워크가 생기가 마련입니다.
   이럴 때는 docker network prune 커맨드를 이용해서 불필요한 네트워크를 한번에 모두 제거할 수 있습니다.

   $ docker network prune
   WARNING! This will remove all networks not used by at least one container.
   Are you sure you want to continue? [y/N] y

====================================================================================

### Docker Compose 네트워크

출처 : https://www.daleseo.com/docker-compose-networks/




====================================================================================

1. java.net.unknownhostexception: failed to resolve '' [a(1)]
   eureka:
      instance:
         preferIpAddress: true

2. 403 FORBIDDEN
   .access((authentication, context) -> new AuthorizationDecision(hasIpAddress2.matches(context.getRequest())))
   io.netty.channel.AbstractChannel$AnnotatedConnectException: finishConnect(..) failed: Connection refused: /172.19.0.3:44371
   
3. Exception processing template "index":
   jar 로 배포하니까..




















