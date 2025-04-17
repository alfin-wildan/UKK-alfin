package com.ujiKom.ukkKasir.UserManagement.User;

import com.ujiKom.ukkKasir.GeneralComponent.Entity.SearchResult;
import com.ujiKom.ukkKasir.UserManagement.User.DTO.DTOUser;
import com.ujiKom.ukkKasir.UserManagement.User.Exception.EmailExistsExc;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface UserService {
    List<UserEntity> getUsers();
    SearchResult<UserEntity> listAll(String search, int pageNumber, int pageSize);
    UserEntity findUserByName(String name);
    UserEntity findByEmail(String email);
    UserEntity findById(Integer id);
    void deleteUser(Integer id, HttpServletRequest httpServletRequest) throws Exception;
    void activateUser(String name);
    void deactivateUser(String name);
    void addUser(DTOUser userEntity, HttpServletRequest httpServletRequest) throws Exception;
    void resetPassword(String name, String newPassword) throws EmailExistsExc;
    void forgetPassword(String name, String newPassword) throws EmailExistsExc;
    void updateUser(DTOUser user) throws Exception;
    SearchResult searchData(Map<String, Object> reqBody, int pageNumber, int pageSize);
    void validateLoginAttempt(UserEntity user);
}
