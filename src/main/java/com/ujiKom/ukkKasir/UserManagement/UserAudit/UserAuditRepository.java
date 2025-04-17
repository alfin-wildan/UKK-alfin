package com.ujiKom.ukkKasir.UserManagement.UserAudit;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAuditRepository extends JpaRepository<UserAuditEntity, Long> {
    List<UserAuditEntity> findAllByUsername(String username);
    List<UserAuditEntity> findByOrderByIdDesc(Pageable pageable);
}
