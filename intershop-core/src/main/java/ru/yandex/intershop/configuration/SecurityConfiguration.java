package ru.yandex.intershop.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerHttpBasicAuthenticationConverter;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.WebSessionServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.XorServerCsrfTokenRequestAttributeHandler;
import ru.yandex.intershop.repository.UserRepository;
import ru.yandex.intershop.service.H2ReactiveUserDetailsService;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository userRepository) {
        return new H2ReactiveUserDetailsService(userRepository);
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService) {
        return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
    }

    @Bean
    public WebSessionServerCsrfTokenRepository webSessionServerCsrfTokenRepository(){
        WebSessionServerCsrfTokenRepository csrfTokenRepository = new WebSessionServerCsrfTokenRepository();
        csrfTokenRepository.setSessionAttributeName("_csrf");
        return csrfTokenRepository;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         ReactiveAuthenticationManager authenticationManager) {
        AuthenticationWebFilter authFilter = new AuthenticationWebFilter(authenticationManager);
        authFilter.setServerAuthenticationConverter(new ServerHttpBasicAuthenticationConverter());

        ServerSecurityContextRepository contextRepository = new WebSessionServerSecurityContextRepository();
        authFilter.setSecurityContextRepository(contextRepository);

        var csrfHandler = new XorServerCsrfTokenRequestAttributeHandler();
        csrfHandler.setTokenFromMultipartDataEnabled(true);

        return http
                .addFilterAt(authFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .formLogin(form -> form
                        .loginPage("/login")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                )
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/login", "/logout").permitAll()
                        .anyExchange().permitAll()
                )
                .securityContextRepository(contextRepository)
                .csrf(csrf -> csrf
                        .csrfTokenRequestHandler(csrfHandler)
                        .csrfTokenRepository(webSessionServerCsrfTokenRepository())
                )
                .build();
    }

}
