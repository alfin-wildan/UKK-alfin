package com.ujiKom.ukkKasir.UserManagement.Operation;

import com.ujiKom.ukkKasir.GeneralComponent.Entity.SearchResult;
import com.ujiKom.ukkKasir.GeneralComponent.Utility.ServiceHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class OperationService {
    protected final OperationRepository operationRepository;
    private final ServiceHelper serviceHelper;
    @PersistenceContext
    private transient EntityManager em;

    @Autowired
    public OperationService(OperationRepository operationRepository, ServiceHelper serviceHelper) {
        this.operationRepository = operationRepository;
        this.serviceHelper = serviceHelper;
    }

    public List<OperationEntity> findAllOperations(Pageable pageable){
        return operationRepository.findAllByOrderByNameDesc(pageable);
    }

    public SearchResult<OperationEntity> listAll(String search, int pageNumber, int pageSize) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<OperationEntity> query = cb.createQuery(OperationEntity.class);
        Root<OperationEntity> root = query.from(OperationEntity.class);
        List<Predicate> predicates = createPredicates(search, cb, root);

        query.select(root)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(cb.desc(root.get("id")));

        TypedQuery<OperationEntity> typedQuery = em.createQuery(query);
        typedQuery.setFirstResult((pageNumber - 1) * pageSize);
        typedQuery.setMaxResults(pageSize);

        List<OperationEntity> resultList = typedQuery.getResultList();

        // Count
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<OperationEntity> countRoot = countQuery.from(OperationEntity.class);
        countQuery.select(cb.count(countRoot))
                .where(createPredicates(search, cb, countRoot).toArray(new Predicate[0]));

        Long totalData = em.createQuery(countQuery).getSingleResult();
        int totalPages = (int) Math.ceil((double) totalData / pageSize);

        return new SearchResult<>(resultList, totalData.intValue(), pageSize, pageNumber, totalPages);
    }

    private List<Predicate> createPredicates(String search, CriteriaBuilder cb, Root<OperationEntity> root) {

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

    public Set<OperationEntity> getOperationByListId(List<Long> listId){
        return new HashSet<>(operationRepository.findAllById(listId));
    }
}
