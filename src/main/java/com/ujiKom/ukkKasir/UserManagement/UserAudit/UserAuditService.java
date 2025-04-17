package com.ujiKom.ukkKasir.UserManagement.UserAudit;

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
import java.util.List;

@Service
@Transactional
public class UserAuditService {
    protected final UserAuditRepository userAuditRepository;
    private final ServiceHelper serviceHelper;
    @PersistenceContext
    private transient EntityManager em;

    @Autowired
    public UserAuditService(UserAuditRepository userAuditRepository, ServiceHelper serviceHelper) {
        this.userAuditRepository = userAuditRepository;
        this.serviceHelper = serviceHelper;
    }

    public List<UserAuditEntity> findByOrderByIdDesc(Pageable pageable) {
        return userAuditRepository.findByOrderByIdDesc(pageable);
    }

    public SearchResult<UserAuditEntity> listAll(String search, int pageNumber, int pageSize){
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<UserAuditEntity> query = cb.createQuery(UserAuditEntity.class);
        Root<UserAuditEntity> root = query.from(UserAuditEntity.class);
        List<Predicate> predicates = createPredicates(search, cb, root);

        query.select(root)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(cb.desc(root.get("id")));

        TypedQuery<UserAuditEntity> typedQuery = em.createQuery(query);
        typedQuery.setFirstResult((pageNumber - 1) * pageSize);
        typedQuery.setMaxResults(pageSize);

        List<UserAuditEntity> resultList = typedQuery.getResultList();

        // Count
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<UserAuditEntity> countRoot = countQuery.from(UserAuditEntity.class);
        countQuery.select(cb.count(countRoot))
                .where(createPredicates(search, cb, countRoot).toArray(new Predicate[0]));

        Long totalData = em.createQuery(countQuery).getSingleResult();
        int totalPages = (int) Math.ceil((double) totalData / pageSize);

        return new SearchResult<>(resultList, totalData.intValue(), pageSize, pageNumber, totalPages);
    }

    private List<Predicate> createPredicates(String search, CriteriaBuilder cb, Root<UserAuditEntity> root) {
        List<Predicate> predicates = new ArrayList<>();
        String likeValue = "%" + search.toUpperCase() + "%";

        if (serviceHelper.isNumeric(search)) {
            predicates.add(cb.equal(root.get("id"), Long.valueOf(search)));
        } else {
            predicates.add(cb.or(
                    cb.like(cb.upper(root.get("captureDate")), likeValue),
                    cb.like(cb.upper(root.get("timeTaken")), likeValue),
                    cb.like(cb.upper(root.get("method")), likeValue),
                    cb.like(cb.upper(root.get("uri")), likeValue),
                    cb.like(cb.upper(root.get("host")), likeValue),
                    cb.like(cb.upper(root.get("userAgent")), likeValue),
                    cb.like(cb.upper(root.get("remoteAddress")), likeValue),
                    cb.like(cb.upper(root.get("reqContentType")), likeValue),
                    cb.like(cb.upper(root.get("respContentType")), likeValue),
                    cb.like(cb.upper(root.get("username")), likeValue)
            ));
        }
        return predicates;
    }

    public List<UserAuditEntity> fetchAuditByUserId(String username) {
        return userAuditRepository.findAllByUsername(username);
    }

    public void saveAuditLog(UserAuditEntity userAudit) {
        userAuditRepository.save(userAudit);
    }
}
