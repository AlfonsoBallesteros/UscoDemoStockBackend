package com.usco.demo.stock.security;

import com.usco.demo.stock.domain.Authority;
import com.usco.demo.stock.domain.User;
import com.usco.demo.stock.service.UserService;
import com.usco.demo.stock.web.errors.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
@Configuration
public class DomainUserDetailsService implements ReactiveUserDetailsService {

    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private final UserService userService;

    public DomainUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<UserDetails> findByUsername(final String login) {
        log.debug("Authenticating {}", login);
        String lowercaseLogin = login.toLowerCase();
        return userService
            .findOneWithAuthoritiesByLogin(lowercaseLogin)
            .switchIfEmpty(Mono.error(new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database")))
            .map(user -> createSpringSecurityUser(lowercaseLogin, user));
    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(String lowercaseLogin, User user) {
        if (!user.isActivated()) {
            throw new BadRequestException(HttpStatus.UNAUTHORIZED, "User " + lowercaseLogin + " was not activated", "user", lowercaseLogin);
        }
        List<SimpleGrantedAuthority> grantedAuthorities = user
            .getAuthorities()
            .stream()
            .map(Authority::getName)
            .map(SimpleGrantedAuthority::new)
            .toList();
        return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(), grantedAuthorities);
    }
}
