package com.usco.demo.stock.service.mapper;

import com.usco.demo.stock.domain.Category;
import com.usco.demo.stock.domain.Item;
import com.usco.demo.stock.service.dto.CategoryDTO;
import com.usco.demo.stock.service.dto.ItemDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper extends EntityMapper<CategoryDTO, Category>{


    default Category fromId(Long id) {
        if (id == null) {
            return null;
        }
        Category category = new Category();
        category.setId(id);
        return category;
    }
}
