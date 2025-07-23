package ru.yandex.intershop.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.User;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, String> {
    Mono<User> findByUsername(String username);
}