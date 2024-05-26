package solo.Gateway.config.filter;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;

import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    public LoggingFilter(){
        super(Config.class);
    }

    //구현시켜야할 객체 : apply
    //반환타입 : gatewayFilter
    @Override
    public GatewayFilter apply(Config config) {
        GatewayFilter filter = new OrderedGatewayFilter((exchange, chain)->{
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            //pre 필터 적용
            log.info("Logging filter baseMessage: request id-> {}", config.getBaseMessage());

            if (config.isPreLogger()){
                log.info("Logging PRE Filter start: request id -> {}", request.getId());
            }

            //post 필터 적용
            return chain.filter(exchange).then(Mono.fromRunnable(()-> {
                if (config.isPostLogger()){
                    log.info("Logging POST Filter start: response code -> {}", response.getStatusCode());
                }
            }));
        }, Ordered.HIGHEST_PRECEDENCE);//gatewayFilter 의 두번재 인자값은 필터의 우선순위를 지정하는 인자값을 넣는다.
        return filter;
    }

    //configuration정보는 내가 자유롭게 지정할 수 있다.
    //내가 여기서 메소드를 지정하면 필터에서 해당 메소드를 지정할 수 있다.
    @Data
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
        //private String sayHello;
    }
}