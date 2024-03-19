package com.usco.demo.stock.service.dto;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDTO extends AbstractAuditingDTO {

    private Long id;

    private String name;

    private String address;

    private String phoneNumber;

    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SupplierDTO)) {
            return false;
        }
        return Objects.equals(id, ((SupplierDTO) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
