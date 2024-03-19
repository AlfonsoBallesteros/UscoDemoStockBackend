package com.usco.demo.stock.repository;

import com.usco.demo.stock.domain.UserAuthority;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface UserAuthorityRepository extends R2dbcRepository<UserAuthority, Long> {

    Flux<UserAuthority> findAllByUserId(Long userId);
}
