package com.ujiKom.ukkKasir.UserManagement.User;

import com.ujiKom.ukkKasir.GeneralComponent.Entity.SearchResult;
import com.ujiKom.ukkKasir.SecurityEngine.Service.LoginAttemptService;
import com.ujiKom.ukkKasir.UserManagement.Role.RoleEntity;
import com.ujiKom.ukkKasir.UserManagement.Role.RoleRepository;
import com.ujiKom.ukkKasir.UserManagement.Role.RoleService;
import com.ujiKom.ukkKasir.UserManagement.User.Constant.FileConstant;
import com.ujiKom.ukkKasir.UserManagement.User.Constant.UserConstant;
import com.ujiKom.ukkKasir.UserManagement.User.DTO.DTOUser;
import com.ujiKom.ukkKasir.UserManagement.User.Domain.UserPrincipal;
import com.ujiKom.ukkKasir.UserManagement.User.Exception.EmailExistsExc;
import com.ujiKom.ukkKasir.UserManagement.User.Exception.UsernameExistsExc;
import com.ujiKom.ukkKasir.GeneralComponent.Utility.ServiceHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final LoginAttemptService loginAttemptService;
    @PersistenceContext
    private transient EntityManager em;
    private final ServiceHelper serviceHelper;
    private final RoleRepository roleRepository;

    @Override
    public List<UserEntity> getUsers() {
        return repository.findAllByOrderByIdAsc();
    }

    @Override
    public SearchResult<UserEntity> listAll(String search, int pageNumber, int pageSize) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<UserEntity> query = cb.createQuery(UserEntity.class);
        Root<UserEntity> root = query.from(UserEntity.class);

        Join<UserEntity, RoleEntity> roleJoin = root.join("role", JoinType.LEFT);

        List<Predicate> predicates = createPredicates(search, cb, root, roleJoin, null);

        query.select(root)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(cb.desc(root.get("id")));

        TypedQuery<UserEntity> typedQuery = em.createQuery(query);
        typedQuery.setFirstResult((pageNumber - 1) * pageSize);
        typedQuery.setMaxResults(pageSize);

        List<UserEntity> resultList = typedQuery.getResultList();

        // Count
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<UserEntity> countRoot = countQuery.from(UserEntity.class);
        Join<UserEntity, RoleEntity> countRoleJoin = countRoot.join("role", JoinType.LEFT);

        countQuery.select(cb.count(countRoot))
                .where(createPredicates(search, cb, countRoot, countRoleJoin, null).toArray(new Predicate[0]));

        Long totalData = em.createQuery(countQuery).getSingleResult();
        int totalPages = (int) Math.ceil((double) totalData / pageSize);

        return new SearchResult<>(resultList, totalData.intValue(), pageSize, pageNumber, totalPages);
    }

    private List<Predicate> createPredicates(String search, CriteriaBuilder cb, Root<UserEntity> root, Join<UserEntity, RoleEntity> roleJoin, Object o) {
        List<Predicate> predicates = new ArrayList<>();
        String likeValue = "%" + search.toUpperCase() + "%";

        if (serviceHelper.isNumeric(search)) {
            predicates.add(cb.equal(root.get("id"), Long.valueOf(search)));
        } else {
            predicates.add(cb.or(
                    cb.like(cb.upper(root.get("name")), likeValue),
                    cb.like(cb.upper(root.get("email")), likeValue),
                    cb.like(cb.upper(root.get("password")), likeValue),
                    cb.equal(root.get("isActive"), Boolean.parseBoolean(search)),
                    cb.like(cb.upper(roleJoin.get("name")), likeValue)
            ));
        }
        return predicates;
    }

    @Override
    public void resetPassword(String email, String newPassword) throws EmailExistsExc {
        UserEntity user = repository.findUserByEmail(email);
        if (user == null) {
            throw new EmailExistsExc(UserConstant.NO_USER_FOUND_BY_USERNAME + email);
        }
        user.setPassword(encodePassword(newPassword));
        user.setIsNotLocked(true);
        user.setIsActive(true);
        repository.save(user);
    }

    @Override
    public void forgetPassword(String email, String newPassword) throws EmailExistsExc {
        UserEntity user = repository.findUserByEmail(email);
        if (user == null) {
            throw new EmailExistsExc(UserConstant.NO_USER_FOUND_BY_USERNAME + email);
        }
        user.setPassword(encodePassword(newPassword));
        user.setIsNotLocked(true);
        user.setIsActive(true);
        repository.save(user);
    }

    @Override
    public void addUser(DTOUser userEntity, HttpServletRequest httpServletRequest) throws Exception {
        validateUsername("", userEntity);
        validateEmail("", userEntity);

        UserEntity user = new UserEntity();

        RoleEntity newRole = roleRepository.findById(userEntity.getRole().getId());
        if (newRole == null) {
            throw new RuntimeException("Role not found with id " + userEntity.getRole().getId());
        }

        user.setName(userEntity.getName());
        user.setEmail(userEntity.getEmail());
        user.setPassword(encodePassword(userEntity.getPassword()));
        user.setIsActive(true);
        user.setIsNotLocked(true);
        user.setRole(newRole);

        repository.save(user);
    }


    @Override
    public void updateUser(DTOUser user) throws Exception {
        Integer id = user.getId();
        UserEntity currentUser = repository.findAllById(id);
        if (currentUser == null) {
            throw new RuntimeException("User not found with id " + id);
        }

        String oldDataJson = serviceHelper.convertObjectToJsonString(currentUser);

        validateUsername(currentUser.getName(), user);
        validateEmail(currentUser.getEmail(), user);

        currentUser.setName(user.getName());
        currentUser.setEmail(user.getEmail());

        RoleEntity newRole = roleRepository.findById(user.getRole().getId());
        if (newRole == null) {
            throw new RuntimeException("Role not found with id " + user.getRole().getId());
        }
        currentUser.setRole(newRole);

        repository.save(currentUser);
    }



    @Override
    public void deleteUser(Integer id, HttpServletRequest httpServletRequest) throws Exception {
        if (id == 1) {
            throw new RuntimeException("User with ID 1 cannot be deleted.");
        }

        UserEntity user = repository.findAllById(id);

        if (user == null) {
            throw new RuntimeException("User not found with id " + id);
        }

        repository.deleteById(user.getId());
    }

//    @Override
//    public boolean isUserConnectedToOtherEntities(Integer id) {
//        return false;
//    }


    @Override
    public SearchResult<UserEntity> searchData(Map<String, Object> reqBody, int pageNumber, int pageSize) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<UserEntity> query = cb.createQuery(UserEntity.class);
        Root<UserEntity> root = query.from(UserEntity.class);
        Join<UserEntity, RoleEntity> roleEntityJoin = root.join("role", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        reqBody.forEach((key, value) -> {
            switch (key) {
                case "role" -> {
                    String data = ((String) "%" + value + "%").toLowerCase();
                    predicates.add(cb.like(cb.lower(roleEntityJoin.get("name")), "%" + data + "%"));
                }
                case "name" -> {
                    String data = ((String) "%" + value + "%").toLowerCase();
                    predicates.add(cb.like(cb.lower(root.get("name")), "%" + data + "%"));
                }
                case "username" -> {
                    String data = ((String) "%" + value + "%").toLowerCase();
                    predicates.add(cb.like(cb.lower(root.get("username")), "%" + data + "%"));
                }
                case "email" -> {
                    String data = ((String) "%" + value + "%").toLowerCase();
                    predicates.add(cb.like(cb.lower(root.get("email")), "%" + data + "%"));
                }
                default -> predicates.add(cb.equal(root.get(key), value));
            }
        });
        query.where(cb.and(predicates.toArray(new Predicate[0])));

        query.orderBy(cb.desc(root.get("id")));

        TypedQuery<UserEntity> typedQuery = this.em.createQuery(query);
        int totalData = typedQuery.getResultList().size();

        // Pagination
        int firstResult = (pageNumber - 1) * pageSize;
        typedQuery.setFirstResult(firstResult);
        typedQuery.setMaxResults(pageSize);
        int totalPage = (int) Math.ceil((double) totalData / pageSize);

        List<UserEntity> userEntityList = typedQuery.getResultList();

        return new SearchResult<>(userEntityList, totalData, pageSize, pageNumber, totalPage);
    }

    private DTOUser validateUsername(String currentUsername, DTOUser user) throws UsernameExistsExc {

        if (!Objects.equals(currentUsername, user.getUsername()) &&
                repository.findByName(user.getUsername()) != null) {
            throw new UsernameExistsExc("Username already exists.");
        }
        return user;
    }

    private DTOUser validateEmail(String currentEmail, DTOUser user) throws EmailExistsExc {
        if (!Objects.equals(currentEmail, user.getEmail()) &&
                repository.findUserByEmail(user.getEmail()) != null) {
            throw new EmailExistsExc("Email already exists.");
        }
        return user;
    }

    private RoleEntity validateRole(long id) {
        RoleEntity foundRole = roleService.findById(id);
        if (foundRole != null) {
            return foundRole;
        }
        return null;
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(Integer.parseInt("10"));
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(Integer.parseInt("10"));
    }

    private String getTemporaryProfileImageUrl(String name) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(FileConstant.DEFAULT_USER_IMAGE_PATH + name).toUriString();
    }

    @Override
    public UserEntity findUserByName(String name) {
        return repository.findUserByName(name);
    }

    @Override
    public UserEntity findUserByEmail(String email) {
        return repository.findUserByEmail(email);
    }

    @Override
    public UserEntity findByEmail(String email) {
        return repository.findByEmail(email);
    }


    @Override
    public UserEntity findById(Integer id) {
        return repository.findAllById(id);
    }

    @Override
    public void activateUser(String name) {
        UserEntity activateUser = repository.findByName(name);
        activateUser.setIsActive(true);
        log.info("User by username : " + activateUser.getName() + " has been active");
    }

    @Override
    public void deactivateUser(String username) {
        UserEntity deactivateUser = repository.findByName(username);
        deactivateUser.setIsActive(false);
        log.info("User by username : " + deactivateUser.getName() + " has been deactivate");
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = repository.findUserByEmail(email);
        if (user == null) {
            log.error("User Not Found by email: " + email);
            throw new UsernameNotFoundException("User Not Found by email: " + email);
        } else {

            repository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            log.info("Returning found user By Email: " + email);
            return userPrincipal;
        }
    }

    @Override
    public void validateLoginAttempt(UserEntity user) {
        if (user.getIsActive()) {
            if (loginAttemptService.hasExceedMaxAttempts(user.getEmail())) {
                user.setIsNotLocked(false);
            } else {
                user.setIsNotLocked(true);
            }
            repository.save(user);
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getEmail());
        }
    }
}
