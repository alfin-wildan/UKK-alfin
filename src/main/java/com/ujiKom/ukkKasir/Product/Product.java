package com.ujiKom.ukkKasir.Product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_product")
public class Product {
    @Id
    @SequenceGenerator(name = "products_seq", sequenceName = "products_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_seq")
    private Integer id;
    private String name;
    private Integer stock;
    private Integer price;
    @ElementCollection
    private List<String> file = new ArrayList<>();
    private LocalDateTime createAt = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));

}
