package solo.Gateway.config.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> { // AbstractGatewayFilterFactory<데이터타입>
    //여기서 데이터 타입은 우리가 작성한 CustomFilter이고
    public CustomFilter(){ //디폴트 생성자에, Config 상위 클래스만 호출한다.
        super(Config.class);
    }

    @Override //우리가 구현해야할 Config를 오버라이드 한다.
    public GatewayFilter apply(Config config) {
        return (exchange, chain)-> { //람다에서 여기부터는 GatewayFilter의 반환값을 작성한다
            ServerHttpRequest request = exchange.getRequest(); //비동기화된 webflux을 사용할 경우 동기화된 톰캣과 다르게
            ServerHttpResponse response = exchange.getResponse(); //serveletrequest가 아닌 severrequest를 받아야 한다

            //pre필터 적용
            log.info("Custom PRE filter: request id-> {}", request.getId());

            //post 필터 적용
            return chain.filter(exchange).then(Mono.fromRunnable(()-> { //Mono는 webflux처럼 비동기 방식에서 단일값을 전달한다고 지정해주기 위해 작성하는 것
                log.info("Custom POST filter: response code -> {}", response.getStatusCode()); //단일값 하나 전달
            }));
        };
    }

    public static class Config {
        //
    }
}