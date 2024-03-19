package com.usco.demo.stock.service;

import com.usco.demo.stock.repository.SupplierRepository;
import com.usco.demo.stock.service.dto.ItemDTO;
import com.usco.demo.stock.service.dto.SupplierDTO;
import com.usco.demo.stock.service.mapper.SupplierMapper;
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
public class SupplierService {

    private final Logger log = LoggerFactory.getLogger(SupplierService.class);

    private final SupplierRepository supplierRepository;

    private final SupplierMapper supplierMapper;

    public Mono<SupplierDTO> save(SupplierDTO supplierDTO){
        return supplierRepository.save(supplierMapper.toEntity(supplierDTO)).map(supplierMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Flux<SupplierDTO> findAllBy(Pageable pageable){
        return supplierRepository.findAllBy(pageable).map(supplierMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Mono<SupplierDTO> findOne(Long id){
        return supplierRepository.findById(id).map(supplierMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Mono<Long> countManaged() {
        return supplierRepository.count();
    }

    @Transactional
    public Mono<Void> deleteSupplier(Long id) {
        return supplierRepository
                .findById(id)
                .flatMap(supplier -> supplierRepository.delete(supplier).thenReturn(supplier))
                .doOnNext(supplier -> log.debug("Deleted supplier: {}", supplier))
                .then();
    }


}
