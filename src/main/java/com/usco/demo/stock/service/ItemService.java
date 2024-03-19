package com.usco.demo.stock.service;

import com.usco.demo.stock.repository.ItemRepository;
import com.usco.demo.stock.service.dto.ItemDTO;
import com.usco.demo.stock.service.mapper.ItemMapper;
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
public class ItemService {

    private final Logger log = LoggerFactory.getLogger(ItemService.class);

    private final ItemRepository itemRepository;

    private final ItemMapper itemMapper;

    public Mono<ItemDTO> save(ItemDTO itemDTO){
        return itemRepository.save(itemMapper.toEntity(itemDTO)).map(itemMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Flux<ItemDTO> findAllBy(Pageable pageable){
        return itemRepository.findAllBy(pageable).map(itemMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Mono<ItemDTO> findOne(Long id){
        return itemRepository.findById(id).map(itemMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Mono<Long> countManaged() {
        return itemRepository.count();
    }

    @Transactional
    public Mono<Void> deleteItem(Long id) {
        return itemRepository
                .findById(id)
                .flatMap(item -> itemRepository.delete(item).thenReturn(item))
                .doOnNext(item -> log.debug("Deleted item: {}", item))
                .then();
    }

}
