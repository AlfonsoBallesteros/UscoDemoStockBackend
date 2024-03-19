package com.usco.demo.stock.web.rest;

import com.usco.demo.stock.service.CategoryService;
import com.usco.demo.stock.service.dto.CategoryDTO;
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
public class CategoryResource {

    private final Logger log = LoggerFactory.getLogger(CategoryResource.class);

    private final CategoryService categoryService;

    @PostMapping("/category")
    public Mono<ResponseEntity<CategoryDTO>> createItem(@RequestBody CategoryDTO input) {
        log.debug("REST request to create tourist destination " + input);
        return categoryService.save(input).map(ResponseEntity::ok);
    }

    @GetMapping("/category")
    //@PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public Mono<ResponseEntity<Flux<CategoryDTO>>> getAllCategory(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        log.debug("REST request to get all User for an admin");
        Pageable pageable = PageRequest.of(page, size);

        return Mono.just(ResponseEntity.ok().body(categoryService.findAllBy(pageable)));
    }

    @DeleteMapping("/category/{id}")
    public Mono<ResponseEntity<Void>> deleteSupplier(@PathVariable Long id) {
        log.debug("REST request to delete supplier: {}", id);
        return categoryService.deleteCategory(id).then(Mono.just(ResponseEntity.noContent().build()));
    }


}
