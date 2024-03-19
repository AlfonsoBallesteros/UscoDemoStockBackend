package com.usco.demo.stock.service;

import com.usco.demo.stock.repository.CategoryRepository;
import com.usco.demo.stock.service.dto.CategoryDTO;
import com.usco.demo.stock.service.dto.SupplierDTO;
import com.usco.demo.stock.service.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    public Mono<CategoryDTO> save(CategoryDTO categoryDTO){
        return categoryRepository.save(categoryMapper.toEntity(categoryDTO)).map(categoryMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Flux<CategoryDTO> findAllBy(Pageable pageable){
        return categoryRepository.findAllBy(pageable).map(categoryMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Mono<CategoryDTO> findOne(Long id){
        return categoryRepository.findById(id).map(categoryMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Mono<Long> countManaged() {
        return categoryRepository.count();
    }

    @Transactional
    public Mono<Void> deleteCategory(Long id) {
        return categoryRepository
                .findById(id)
                .flatMap(category -> categoryRepository.delete(category).thenReturn(category))
                .doOnNext(category -> log.debug("Deleted category: {}", category))
                .then();
    }
}
