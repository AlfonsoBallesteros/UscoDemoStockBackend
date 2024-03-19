package com.usco.demo.stock.repository;

import com.usco.demo.stock.domain.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CategoryRepository extends R2dbcRepository<Category, Long> {

    Flux<Category> findAllBy(Pageable pageable);
}
