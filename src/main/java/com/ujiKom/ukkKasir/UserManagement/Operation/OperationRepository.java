package com.ujiKom.ukkKasir.UserManagement.Operation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<OperationEntity, Long> {
    List<OperationEntity> findAllByOrderByNameDesc(Pageable pageable);
    OperationEntity findById(long id);
}