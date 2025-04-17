package com.ujiKom.ukkKasir.Members;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MembersRepository extends JpaRepository<Members,Integer> {
    Optional<Members> findByPhoneNumber(String phoneNumber);
}
