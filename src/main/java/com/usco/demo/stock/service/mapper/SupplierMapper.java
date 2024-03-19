package com.usco.demo.stock.service.mapper;

import com.usco.demo.stock.domain.Supplier;
import com.usco.demo.stock.service.dto.SupplierDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SupplierMapper extends EntityMapper<SupplierDTO, Supplier>{


    default Supplier fromId(Long id) {
        if (id == null) {
            return null;
        }
        Supplier supplier = new Supplier();
        supplier.setId(id);
        return supplier;
    }
}
