package com.usco.demo.stock.repository;

import com.usco.demo.stock.domain.Supplier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface SupplierRepository extends R2dbcRepository<Supplier, Long> {
    Flux<Supplier> findAllBy(Pageable pageable);

}
