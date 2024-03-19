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
import java.util.Objects;

@Table("category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        return Objects.equals(id, ((Category) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
