package com.usco.demo.stock.service;

import com.usco.demo.stock.domain.Authority;
import com.usco.demo.stock.domain.User;
import com.usco.demo.stock.domain.UserAuthority;
import com.usco.demo.stock.repository.AuthorityRepository;
import com.usco.demo.stock.repository.UserAuthorityRepository;
import com.usco.demo.stock.repository.UserRepository;
import com.usco.demo.stock.security.AuthoritiesConstants;
import com.usco.demo.stock.security.SecurityUtils;
import com.usco.demo.stock.service.dto.RegisterUserDTO;
import com.usco.demo.stock.service.dto.UserDTO;
import com.usco.demo.stock.service.mapper.UserMapper;
import com.usco.demo.stock.web.errors.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    private final UserAuthorityRepository userAuthorityRepository;

    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public Flux<String> getUserAuthorities(Long userId) {
        return userAuthorityRepository.findAllByUserId(userId).map(UserAuthority::getAuthorityName);
    }

    public Mono<User> findOneWithAuthoritiesByLogin(String login) {
        return userRepository.findUserByLogin(login)
                .flatMap(u -> getUserAuthorities(u.getId())
                        .collectList()
                        .map(au -> u.addAuthorities(au.stream().map(Authority::new).toList())));
    }

    @Transactional
    public Mono<UserDTO> registerUser(RegisterUserDTO userDTO, String password) {
        return userRepository
                .findUserByLogin(userDTO.getLogin().toLowerCase())
                .flatMap(existingUser -> {
                    if (!existingUser.isActivated()) {
                        return this.deleteUser(existingUser.getLogin());
                    } else {
                        return Mono.error(new BadRequestException(HttpStatus.BAD_REQUEST, "Login name already used!", "REGISTER", HttpStatus.BAD_REQUEST.toString()));
                    }
                })
                .then(userRepository.findUserByLogin(userDTO.getLogin()))//login to phonenumber - or alias
                .flatMap(existingUser -> {
                    if (!existingUser.isActivated()) {
                        return this.deleteUser(existingUser.getLogin());
                    } else {
                        return Mono.error(new BadRequestException(HttpStatus.BAD_REQUEST, "Login name already used!", "REGISTER", HttpStatus.BAD_REQUEST.toString()));
                    }
                })
                .publishOn(Schedulers.boundedElastic())
                .then(
                        Mono.fromCallable(() -> {
                            User newUser = new User();
                            String encryptedPassword = passwordEncoder.encode(password);
                            newUser.setLogin(userDTO.getLogin().toLowerCase());
                            // new user gets initially a generated password
                            newUser.setPassword(encryptedPassword);
                            newUser.setName(userDTO.getName());
                            return newUser;
                        })
                )
                .flatMap(newUser -> {
                    Set<Authority> authorities = new HashSet<>();
                    return authorityRepository
                            .findById(AuthoritiesConstants.USER)
                            .map(authorities::add)
                            .thenReturn(newUser)
                            .doOnNext(user -> user.setAuthorities(authorities))
                            .flatMap(this::saveUser)
                            .map(userMapper::userToUserDTO)
                            .doOnNext(user -> log.debug("Created Information for User: {}", user));
                });
    }

    @Transactional
    public Mono<User> saveUser(User user) {
        return userRepository
                .save(user)
                .flatMap(savedUser -> Flux.fromIterable(user.getAuthorities())
                        .flatMap(authority -> userAuthorityRepository.save(new UserAuthority(savedUser.getId(), authority.getName())))
                        .then(Mono.just(savedUser)));
    }

    @Transactional
    public Mono<Void> deleteUser(String login) {
        return userRepository
                .findUserByLogin(login)
                .flatMap(user -> userAuthorityRepository.delete(new UserAuthority(user.getId(), AuthoritiesConstants.USER)).thenReturn(user))
                .flatMap(user -> userRepository.delete(user).thenReturn(user))
                .doOnNext(user -> log.debug("Deleted User: {}", user))
                .then();
    }

    @Transactional(readOnly = true)
    public Mono<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(this::findOneWithAuthoritiesByLogin);
    }

    @Transactional(readOnly = true)
    public Mono<Long> countManagedUsers() {
        return userRepository.count();
    }

    @Transactional(readOnly = true)
    public Flux<UserDTO> findAllWithAuthorities(Pageable pageable) {
        return userRepository.findAllBy(pageable)
                .flatMap(user -> getUserAuthorities(user.getId()).collectList().map(au -> user.addAuthorities(au.stream().map(Authority::new).toList())))
                .map(userMapper::userToUserDTO);
    }
}
