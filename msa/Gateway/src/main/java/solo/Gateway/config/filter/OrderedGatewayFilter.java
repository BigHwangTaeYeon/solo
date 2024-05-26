package solo.Gateway.config.filter;

import lombok.Getter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Getter
public class OrderedGatewayFilter implements GatewayFilter, Ordered {
    private final GatewayFilter delegate;
    private final int order;

    //우리가 구현함
    public OrderedGatewayFilter(GatewayFilter delegate, int order) {
        this.delegate = delegate;
        this.order = order;
    }

    //필터가 해야할 역할을 재정의 할 수 있다.
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return this.delegate.filter(exchange, chain);
    }

    public String toString() {
        return "[" + this.delegate + ", order = " + this.order + "]";
    }
}
