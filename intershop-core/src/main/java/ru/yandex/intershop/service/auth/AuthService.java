package ru.yandex.intershop.service.auth;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    public Mono<Boolean> isAuthenticated(ServerWebExchange exchange) {
        return exchange.getPrincipal()
                .map(principal -> {
                    if (principal instanceof Authentication authentication) {
                        return authentication.isAuthenticated() &&
                                !(authentication instanceof AnonymousAuthenticationToken);
                    }
                    return false;
                })
                .defaultIfEmpty(false);
    }
}