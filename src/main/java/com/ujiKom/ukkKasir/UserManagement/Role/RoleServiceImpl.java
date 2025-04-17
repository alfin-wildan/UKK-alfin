package com.ujiKom.ukkKasir.UserManagement.Role;

import com.ujiKom.ukkKasir.GeneralComponent.DTO.DTOGeneral;
import com.ujiKom.ukkKasir.GeneralComponent.Entity.SearchResult;
import com.ujiKom.ukkKasir.UserManagement.Operation.OperationEntity;
import com.ujiKom.ukkKasir.UserManagement.Operation.OperationRepository;
import com.ujiKom.ukkKasir.UserManagement.Operation.OperationService;
import com.ujiKom.ukkKasir.UserManagement.Role.DTO.DTORole;
import com.ujiKom.ukkKasir.UserManagement.Role.Exception.RoleExistExc;
import com.ujiKom.ukkKasir.UserManagement.Role.Exception.RoleNotFoundExc;
import com.ujiKom.ukkKasir.GeneralComponent.Utility.ServiceHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    protected final OperationService operationService;
    private final ServiceHelper serviceHelper;
    private final OperationRepository operationRepository;
    @PersistenceContext
    private transient EntityManager em;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, OperationService operationService, ServiceHelper serviceHelper, OperationRepository operationRepository) {
        this.roleRepository = roleRepository;
        this.operationService = operationService;
        this.serviceHelper = serviceHelper;
        this.operationRepository = operationRepository;
    }


    @Override
    public SearchResult<RoleEntity> listAll(String search, int pageNumber, int pageSize) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<RoleEntity> query = cb.createQuery(RoleEntity.class);
        Root<RoleEntity> root = query.from(RoleEntity.class);
        List<Predicate> predicates = createPredicates(search, cb, root);

        query.select(root)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(cb.desc(root.get("id")));

        TypedQuery<RoleEntity> typedQuery = em.createQuery(query);
        typedQuery.setFirstResult((pageNumber - 1) * pageSize);
        typedQuery.setMaxResults(pageSize);

        List<RoleEntity> resultList = typedQuery.getResultList();

        // Count
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<RoleEntity> countRoot = countQuery.from(RoleEntity.class);
        countQuery.select(cb.count(countRoot))
                .where(createPredicates(search, cb, countRoot).toArray(new Predicate[0]));

        Long totalData = em.createQuery(countQuery).getSingleResult();
        int totalPages = (int) Math.ceil((double) totalData / pageSize);

        return new SearchResult<>(resultList, totalData.intValue(), pageSize, pageNumber, totalPages);
    }

    @Override
    public List<RoleEntity> listRole() {
        return roleRepository.findAll();
    }

    private List<Predicate> createPredicates(String search, CriteriaBuilder cb, Root<RoleEntity> root) {
        List<Predicate> predicates = new ArrayList<>();
        String likeValue = "%" + search.toUpperCase() + "%";

        if (serviceHelper.isNumeric(search)) {
            predicates.add(cb.equal(root.get("id"), Long.valueOf(search)));
        } else {
            predicates.add(cb.or(
                    cb.like(cb.upper(root.get("name")), likeValue)
            ));
        }
        return predicates;
    }

    @Override
    public List<RoleEntity> findByOrderByIdDesc(Pageable pageable) {
        return roleRepository.findByOrderByIdDesc(pageable);
    }

    @Override
    public RoleEntity addRole(DTORole role, HttpServletRequest httpServletRequest) throws Exception {
        validateRole("", role);
        RoleEntity newRole = new RoleEntity();

        newRole.setName(role.getName());
        Set<DTOGeneral> operationDTOs = role.getOperations();
        Set<OperationEntity> operations = new HashSet<>();

        for (DTOGeneral dtoGeneral : operationDTOs) {
            OperationEntity operationEntity = operationRepository.findById(dtoGeneral.getId());
            operations.add(operationEntity);
        }
        newRole.setOperations(getOperations(operations));

        roleRepository.save(newRole);

        return newRole;
    }

    @Override
    public RoleEntity updateRole(DTORole role, HttpServletRequest httpServletRequest) throws Exception {
        long id = role.getId();
        RoleEntity existingRole = roleRepository.findById(id);

        if (existingRole == null) {
            throw new RuntimeException("Role not found with id " + id);
        }

        String oldDataJson = serviceHelper.convertObjectToJsonString(existingRole);

        validateRole(existingRole.getName(), role);
        existingRole.setName(role.getName());

        Set<DTOGeneral> operationDTOs = role.getOperations();
        Set<OperationEntity> operations = new HashSet<>();

        for (DTOGeneral dtoGeneral : operationDTOs) {
            OperationEntity operationEntity = operationRepository.findById(dtoGeneral.getId());
            operations.add(operationEntity);
        }
        existingRole.setOperations(getOperations(operations));

        roleRepository.save(existingRole);
        return existingRole;
    }


    @Override
    public void deleteRole(long id, HttpServletRequest httpServletRequest) throws Exception {
        if (id == 1) {
            throw new RuntimeException("Role with ID 1 cannot be deleted.");
        }

        RoleEntity existingRole = roleRepository.findById(id);

        if (existingRole == null) {
            throw new RuntimeException("Role not found with id " + id);
        }

        roleRepository.delete(existingRole);
    }

    private void validateRoleName(String roleName) throws RoleExistExc {
        RoleEntity role = roleRepository.findRoleByName(roleName);
        if(role != null){
            throw new RoleExistExc("Role already exists!");
        }
    }

    @Override
    public RoleEntity findRoleByName(String name){
        return roleRepository.findRoleByName(name);
    }

    @Override
    public RoleEntity findById(long id) {
        return roleRepository.findById(id);
    }

    @Override
    public SearchResult<RoleEntity> searchData(Map<String, Object> reqBody, int pageNumber, int pageSize) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<RoleEntity> query = cb.createQuery(RoleEntity.class);
        Root<RoleEntity> root = query.from(RoleEntity.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody.forEach((key, value) -> {
            switch (key) {
                case "name", "operation" -> {
                    String data = ((String) value).toLowerCase();
                    predicates.add(cb.like(cb.lower(root.get("name")),  "%" + data + "%"));
                }
                default -> predicates.add(cb.equal(root.get(key), value));
            }
        });
        query.where(cb.and(predicates.toArray(new Predicate[0])));

        query.orderBy(cb.desc(root.get("id")));

        TypedQuery<RoleEntity> typedQuery = this.em.createQuery(query);
        int totalData = typedQuery.getResultList().size();

        // Pagination
        int firstResult = (pageNumber - 1) * pageSize;
        typedQuery.setFirstResult(firstResult);
        typedQuery.setMaxResults(pageSize);
        int totalPage = (int) Math.ceil((double) totalData / pageSize);
        return new SearchResult<>(typedQuery.getResultList(), totalData, pageSize, pageNumber, totalPage);
    }

    private Set<OperationEntity> getOperations(Set<OperationEntity> operations){
        List<Long> listOperations = new ArrayList<>();
        operations.forEach(operation -> {
            listOperations.add(operation.getId());
        });
        return operationService.getOperationByListId(listOperations);
    }

    private DTORole validateRole(String currentRoleName, DTORole role) throws RoleExistExc, RoleNotFoundExc {
        if (!Objects.equals(currentRoleName, role.getName())) {
            if (roleRepository.findRoleByName(role.getName()) != null) {
                throw new RoleExistExc("Role already exists.");
            }
        }
        return role;
    }

    private List<String> getConnectedEntities(long id) {
        List<String> connectedEntities = new ArrayList<>();

        if (roleRepository.isConnectedToUser(id)) {
            connectedEntities.add("User");
        }
        return connectedEntities;
    }
}
