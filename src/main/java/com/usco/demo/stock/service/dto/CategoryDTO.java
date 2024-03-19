package com.usco.demo.stock.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO extends AbstractAuditingDTO {

    private Integer id;

    private String name;

    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CategoryDTO)) {
            return false;
        }
        return Objects.equals(id, ((CategoryDTO) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
