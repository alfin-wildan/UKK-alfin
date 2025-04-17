package com.ujiKom.ukkKasir.UserManagement.Role;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    RoleEntity findRoleByName(String name);
    List<RoleEntity> findByOrderByIdDesc(Pageable pageable);
    RoleEntity findById(long id);
    @Query("SELECT COUNT(t) > 0 FROM UserEntity t WHERE t.role.id = :role")
    boolean isConnectedToUser(long role);
}
