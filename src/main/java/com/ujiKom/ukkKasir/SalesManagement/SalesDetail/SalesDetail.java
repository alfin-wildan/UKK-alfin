package com.ujiKom.ukkKasir.SalesManagement.SalesDetail;

import com.ujiKom.ukkKasir.Product.Product;
import com.ujiKom.ukkKasir.SalesManagement.Sales.Sales;
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
@Table(name = "t_sales_detail")
public class SalesDetail {
    @Id
    @SequenceGenerator(name = "sales_details_seq", sequenceName = "sales_details_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sales_details_seq")
    private Integer id;
    private Integer amount;
    private Integer subTotal;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @ManyToOne
    @JoinColumn(name = "sales_id", nullable = false)
    private Sales sales;
    private LocalDateTime createAt = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));
}
