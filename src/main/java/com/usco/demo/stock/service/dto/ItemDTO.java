package com.usco.demo.stock.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO extends AbstractAuditingDTO  {

    private Long id;

    private String name;

    private String description;

    private BigDecimal buyPrice;

    private BigDecimal salePrice;

    private Integer stock;

    private Long categoryId;

    private Long supplierId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemDTO)) {
            return false;
        }
        return Objects.equals(id, ((ItemDTO) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
