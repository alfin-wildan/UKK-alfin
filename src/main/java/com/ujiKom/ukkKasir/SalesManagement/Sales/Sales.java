package com.ujiKom.ukkKasir.SalesManagement.Sales;


import com.ujiKom.ukkKasir.Members.Members;
import com.ujiKom.ukkKasir.UserManagement.User.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_sales")
public class Sales {
    @Id
    @SequenceGenerator(name = "sales_seq", sequenceName = "sales_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sales_seq")
    private Integer id;
    private Integer totalPrice;
    private Integer totalPay;
    private Integer totalReturn;
    private Integer point;
    @ManyToOne
    @JoinColumn(name = "id_members")
    private Members member;
    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private UserEntity user;
    private LocalDateTime createAt = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));
}
