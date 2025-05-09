package com.ujiKom.ukkKasir.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Integer> {
    @Override
    List<Product> findAll();

    Product findById(long id);
}
