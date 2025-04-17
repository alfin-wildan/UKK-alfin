package com.ujiKom.ukkKasir.UserManagement.User.Domain;

import com.ujiKom.ukkKasir.UserManagement.Operation.OperationEntity;
import com.ujiKom.ukkKasir.UserManagement.Role.RoleEntity;
import com.ujiKom.ukkKasir.UserManagement.User.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.ujiKom.ukkKasir.UserManagement.User.Constant.OperationalURIConstant.LIST_URI_BY_OP;


public class UserPrincipal implements UserDetails {
    private final UserEntity userEntity;
    public UserPrincipal(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getGrantedAuthorities(getOperationPrevillage(userEntity.role));
    }

    public List<String> getOperationPrevillage(RoleEntity role){
        List<String> operationPrevillage = new ArrayList<>();

        operationPrevillage.add(role.getName());
        for(OperationEntity operation : role.getOperations()){
            operationPrevillage.add(
                    new StringBuilder()
                            .append(operation.getId())
                            .append("_OP_")
                            .append(operation.getName())
                            .append("[")
                            .append(LIST_URI_BY_OP.get(operation.getName()))
                            .append("]")
                            .toString()
            );
        }
        return operationPrevillage;
    }

    public List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for(String privilege : privileges){
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

    @Override
    public String getPassword(){
        return this.userEntity.getPassword();
    }

    @Override
    public String getUsername(){
        return this.userEntity.getEmail();
    }

    public String getOutletCode(){
        String outletCode = null;
        return outletCode;
    }

    @Override
    public boolean isAccountNonExpired(){
        return true;
    }

    @Override
    public boolean isAccountNonLocked(){
        return this.userEntity.getIsNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired(){
        return this.userEntity.getIsNotLocked();
    }

    @Override
    public boolean isEnabled(){
        return this.userEntity.getIsActive();
    }
}
