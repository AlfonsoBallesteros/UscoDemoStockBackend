package com.usco.demo.stock.repository;

import com.usco.demo.stock.domain.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ItemRepository extends R2dbcRepository<Item, Long> {

    //@Query("SELECT * FROM item WHERE name LIKE :name")
    Flux<Item> findByNameContains(String name);

    Flux<Item> findAllBy(Pageable pageable);
}
