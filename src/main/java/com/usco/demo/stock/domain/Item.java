package com.usco.demo.stock.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Table("item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("buy_price")
    private BigDecimal buyPrice;

    @Column("sale_price")
    private BigDecimal salePrice;

    @Column("stock")
    private Integer stock;

    @Column("id_category")
    private Long categoryId;

    @Column("id_supplier")
    private Long supplierId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Item)) {
            return false;
        }
        return Objects.equals(id, ((Item) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
