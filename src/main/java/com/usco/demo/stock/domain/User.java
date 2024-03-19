package com.usco.demo.stock.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.usco.demo.stock.domain.enumeration.Gender;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * A user.
 */
@Table(name = "usuario")
@Getter
@Setter
@ToString
public class User extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("login")
    private String login;

    @JsonIgnore
    @Column("password")
    private String password;

    @Column("name")
    private String name;

    @Column("birthdate")
    private LocalDate birthdate;

    @Column("gender")
    private Gender gender;

    @Column("phone_number")
    private String phoneNumber;

    @NotNull
    @Column("activated")
    private boolean activated = true;

    @JsonIgnore
    @Transient
    private Set<Authority> authorities = new HashSet<>();

    public boolean isActivated() {
        return activated;
    }

    public User addAuthorities(List<Authority> Authority){
        this.getAuthorities().addAll(Authority);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        return id != null && id.equals(((User) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
