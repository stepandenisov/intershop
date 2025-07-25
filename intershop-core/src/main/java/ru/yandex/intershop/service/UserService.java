package ru.yandex.intershop.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.User;
import ru.yandex.intershop.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public Mono<User> findUserByName(String username){
        return userRepository.findByUsername(username);
    }
}
