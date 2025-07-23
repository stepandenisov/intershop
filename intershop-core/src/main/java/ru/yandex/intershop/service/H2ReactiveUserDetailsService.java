package ru.yandex.intershop.service;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.repository.UserRepository;

public class H2ReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository; // твой реактивный репозиторий

    public H2ReactiveUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRoles())
                        .build()
                );
    }
}

