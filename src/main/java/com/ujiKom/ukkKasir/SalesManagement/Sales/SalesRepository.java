package com.ujiKom.ukkKasir.SalesManagement.Sales;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesRepository extends JpaRepository<Sales,Integer> {
    Sales findById(long id);
}
