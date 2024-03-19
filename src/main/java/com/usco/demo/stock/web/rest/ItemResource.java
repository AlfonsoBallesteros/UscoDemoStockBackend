package com.usco.demo.stock.web.rest;

import com.usco.demo.stock.service.ItemService;
import com.usco.demo.stock.service.dto.ItemDTO;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ItemResource {

    private final Logger log = LoggerFactory.getLogger(ItemResource.class);

    private final ItemService itemService;

    @PostMapping("/item")
    public Mono<ResponseEntity<ItemDTO>> createItem(@RequestBody ItemDTO input) {
        log.debug("REST request to create tourist destination " + input);
        return itemService.save(input).map(ResponseEntity::ok);
    }

    @GetMapping("/items")
    //@PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public Mono<ResponseEntity<Flux<ItemDTO>>> getAllItems(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        log.debug("REST request to get all User for an admin");
        Pageable pageable = PageRequest.of(page, size);

        return Mono.just(ResponseEntity.ok().body(itemService.findAllBy(pageable)));
    }

    @DeleteMapping("/item/{id}")
    public Mono<ResponseEntity<Void>> deleteSupplier(@PathVariable Long id) {
        log.debug("REST request to delete supplier: {}", id);
        return itemService.deleteItem(id).then(Mono.just(ResponseEntity.noContent().build()));
    }

}
