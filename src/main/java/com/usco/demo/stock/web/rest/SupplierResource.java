package com.usco.demo.stock.web.rest;

import com.usco.demo.stock.service.SupplierService;
import com.usco.demo.stock.service.dto.SupplierDTO;
import com.usco.demo.stock.web.util.HeaderUtil;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class SupplierResource {

    private final Logger log = LoggerFactory.getLogger(SupplierResource.class);

    private final SupplierService supplierService;

    @PostMapping("/supplier")
    public Mono<ResponseEntity<SupplierDTO>> createItem(@RequestBody SupplierDTO input) {
        log.debug("REST request to create tourist destination " + input);
        return supplierService.save(input).map(ResponseEntity::ok);
    }

    @GetMapping("/supplier")
    //@PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public Mono<ResponseEntity<Flux<SupplierDTO>>> getAllSupplier(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        log.debug("REST request to get all User for an admin");
        Pageable pageable = PageRequest.of(page, size);

        return Mono.just(ResponseEntity.ok().body(supplierService.findAllBy(pageable)));
    }

    @DeleteMapping("/supplier/{id}")
    public Mono<ResponseEntity<Void>> deleteSupplier(@PathVariable Long id) {
        log.debug("REST request to delete supplier: {}", id);
        return supplierService.deleteSupplier(id).then(Mono.just(ResponseEntity.noContent().build()));
    }

}
