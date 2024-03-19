package com.usco.demo.stock.repository;

import com.usco.demo.stock.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {

    Mono<User> findUserByLogin(String login);

    Flux<User> findAllBy(Pageable pageable);


}
