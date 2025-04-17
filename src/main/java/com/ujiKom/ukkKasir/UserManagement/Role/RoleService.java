package com.ujiKom.ukkKasir.UserManagement.Role;

import com.ujiKom.ukkKasir.GeneralComponent.Entity.SearchResult;
import com.ujiKom.ukkKasir.UserManagement.Role.DTO.DTORole;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface RoleService {
    RoleEntity addRole(DTORole role, HttpServletRequest httpServletRequest) throws Exception;
    SearchResult<RoleEntity> listAll(String search, int pageNumber, int pageSize);
    List<RoleEntity> listRole();
    List<RoleEntity> findByOrderByIdDesc(Pageable pageable);
    RoleEntity updateRole(DTORole role, HttpServletRequest httpServletRequest) throws Exception;
    void deleteRole(long id, HttpServletRequest httpServletRequest) throws Exception;
    RoleEntity findRoleByName(String name);
    RoleEntity findById(long id);
    SearchResult<RoleEntity> searchData(Map<String, Object> reqBody, int pageNumber, int pageSize);
}

