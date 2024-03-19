package com.usco.demo.stock.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Table("supplier")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Supplier extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("address")
    private String address;

    @Column("phone_number")
    private String phoneNumber;

    @Column("email")
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Supplier)) {
            return false;
        }
        return Objects.equals(id, ((Supplier) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
