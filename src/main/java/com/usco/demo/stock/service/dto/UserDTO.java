package com.usco.demo.stock.service.dto;

import com.usco.demo.stock.domain.Authority;
import com.usco.demo.stock.domain.User;
import com.usco.demo.stock.domain.enumeration.Gender;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserDTO extends AbstractAuditingDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String login;

    private String name;

    private LocalDate birthdate;

    private Gender gender;

    private String phoneNumber;

    //private String imageUrl;

    private boolean activated = true;

    private Set<String> authorities;

    public UserDTO(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.name = user.getName();
        this.birthdate = user.getBirthdate();
        this.gender = user.getGender();
        this.phoneNumber = user.getPhoneNumber();
        this.activated = user.isActivated();
        this.setCreatedDate(user.getCreatedDate());
        this.setLastModifiedDate(user.getLastModifiedDate());
        this.authorities = user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet());
    }

}
