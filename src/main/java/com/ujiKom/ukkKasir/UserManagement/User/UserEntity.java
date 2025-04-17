package com.ujiKom.ukkKasir.UserManagement.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ujiKom.ukkKasir.UserManagement.Role.RoleEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_users")
public class UserEntity {
    @Id
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")

    @Column(nullable = false, updatable = false)
    private Integer id;
    private String name;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private Boolean isActive;
    private Boolean isNotLocked;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    public RoleEntity role;

}
