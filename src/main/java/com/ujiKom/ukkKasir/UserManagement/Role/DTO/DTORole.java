package com.ujiKom.ukkKasir.UserManagement.Role.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ujiKom.ukkKasir.GeneralComponent.DTO.DTOGeneral;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DTORole {
    @Id
    private Long id;
    @Column(nullable = false)
    private String name;
    @ManyToOne
    private DTOGeneral roleGroup;
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name="role_op",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "op_id")
    )
    private Set<DTOGeneral> operations;

}
