package com.ujiKom.ukkKasir.UserManagement.User;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    List<UserEntity> findByOrderByIdDesc(Pageable pageable);
    List<UserEntity> findAllByOrderByIdAsc();
    UserEntity findUserByName(String name);
    UserEntity findByName(String name);
    UserEntity findAllById(Integer id);
    UserEntity findUserByEmail(String email);
    UserEntity findByEmail(String email);
}
