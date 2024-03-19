package com.usco.demo.stock.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name = "user_authority")
@Getter
@Setter
@ToString
@AllArgsConstructor
public class UserAuthority {

    @Column("user_id")
    private Long userId;

    @Column("authority_name")
    private String authorityName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAuthority)) {
            return false;
        }
        return userId != null && userId.equals(((UserAuthority) o).userId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
