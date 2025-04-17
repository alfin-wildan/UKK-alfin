package com.ujiKom.ukkKasir.SalesManagement.SalesDetail;

import com.ujiKom.ukkKasir.GeneralComponent.DTO.DTOGeneral;
import com.ujiKom.ukkKasir.GeneralComponent.Entity.SearchResult;
import com.ujiKom.ukkKasir.GeneralComponent.Utility.ServiceHelper;
import com.ujiKom.ukkKasir.Product.Product;
import com.ujiKom.ukkKasir.Product.ProductRepository;
import com.ujiKom.ukkKasir.SalesManagement.Sales.Sales;
import com.ujiKom.ukkKasir.SalesManagement.Sales.SalesRepository;
import com.ujiKom.ukkKasir.SalesManagement.SalesDetail.DTO.DTOSalesDetail;
import com.ujiKom.ukkKasir.UserManagement.User.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalesDetailServiceImpl implements SalesDetailService{
    private final SalesDetailRepository repository;
    private final SalesRepository salesRepository;
    private final UserRepository userRepository;
    private final ServiceHelper serviceHelper;
    private final ProductRepository productRepository;
    @PersistenceContext
    private final EntityManager entityManager;

    public SalesDetailServiceImpl(SalesDetailRepository repository, SalesRepository salesRepository, UserRepository userRepository, ServiceHelper serviceHelper, ProductRepository productRepository, EntityManager entityManager) {
        this.repository = repository;
        this.salesRepository = salesRepository;
        this.userRepository = userRepository;
        this.serviceHelper = serviceHelper;
        this.productRepository = productRepository;
        this.entityManager = entityManager;
    }

    @Override
    public List<SalesDetail> getSalesDetails() {
        return repository.findAll();
    }

    @Override
    public SearchResult<DTOSalesDetail> listAll(String search, Integer id, int page, int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<SalesDetail> cq = cb.createQuery(SalesDetail.class);
        Root<SalesDetail> root = cq.from(SalesDetail.class);
        Join<SalesDetail, Sales> salesJoin = root.join("sales", JoinType.LEFT);

        List<Predicate> predicates = createPredicates(search, cb, root, salesJoin);

        if (id != null) {
            predicates.add(cb.equal(root.get("id"), id));
        }
        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        cq.orderBy(cb.desc(root.get("createdAt")));

        TypedQuery<SalesDetail> query = entityManager.createQuery(cq);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        List<SalesDetail> salesDetails = query.getResultList();
        List<DTOSalesDetail> dtoList = salesDetails.stream()
                .map(this::convertToDTO)
                    .collect(Collectors.toList());

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<SalesDetail> countRoot = countQuery.from(SalesDetail.class);
        Join<SalesDetail, Sales> countSalesJoin = countRoot.join("sales", JoinType.LEFT);

        List<Predicate> countPredicates = createPredicates(search, cb, countRoot, countSalesJoin);
        if (id != null) {
            countPredicates.add(cb.equal(countRoot.get("id"), id));
        }

        countQuery.select(cb.count(countRoot))
                .where(cb.and(countPredicates.toArray(new Predicate[0])));

        Long total = entityManager.createQuery(countQuery).getSingleResult();
        int totalPages = (int) Math.ceil((double) total / size);

        return new SearchResult<>(dtoList, total.intValue(), size, page, totalPages);

    }
    private List<Predicate> createPredicates(String search, CriteriaBuilder cb, Root<SalesDetail> root, Join<SalesDetail, Sales> salesJoin) {
        List<Predicate> predicates = new ArrayList<>();
        String likeValue = "%" + search.toLowerCase() + "%";

        if (serviceHelper.isNumeric(search)) {
            predicates.add(cb.equal(root.get("id"), Long.valueOf(search)));
            predicates.add(cb.equal(salesJoin.get("id"), Long.valueOf(search)));
        } else {
            Join<SalesDetail, Product> productJoin = root.join("product", JoinType.LEFT);

            predicates.add(cb.or(
                    cb.like(cb.lower(productJoin.get("name")), likeValue),
                    cb.like(cb.lower(salesJoin.get("createdBy")), likeValue),
                    cb.like(cb.lower(salesJoin.get("id").as(String.class)), likeValue)
            ));
            try {
                Integer numericValue = Integer.parseInt(search);
                predicates.add(cb.or(
                        cb.equal(root.get("amount"), numericValue),
                        cb.equal(root.get("subTotal"), numericValue)
                ));
            } catch (NumberFormatException e) {
            }
            try {
                Float price = Float.parseFloat(search);
                predicates.add(cb.equal(salesJoin.get("totalPrice"), price));
            } catch (NumberFormatException e) {
            }
        }

        return predicates;

    }

    private DTOSalesDetail convertToDTO(SalesDetail salesDetail) {
        DTOSalesDetail dto = new DTOSalesDetail();

        dto.setId(salesDetail.getId());
        dto.setAmount(salesDetail.getAmount());
        dto.setSubTotal(salesDetail.getSubTotal());
        dto.setCreatedAt(salesDetail.getCreateAt());

        if (salesDetail.getProduct() != null) {
            dto.setProduct(new DTOGeneral(salesDetail.getProduct().getId()));
        } else {
            dto.setProduct(new DTOGeneral(0));
        }
        if (salesDetail.getSales() != null) {
            dto.setSales(new DTOGeneral(salesDetail.getSales().getId()));
        } else {
            dto.setSales(new DTOGeneral(0));
        }
        return dto;
    }


    @Override
    public void createSalesDetail(DTOSalesDetail dtoSalesDetail) {
        SalesDetail salesDetail = new SalesDetail();
        Product product = productRepository.findById(dtoSalesDetail.getProduct().getId());
        Sales newSales = salesRepository.findById(dtoSalesDetail.getSales().getId());
        if (newSales == null){
            throw new RuntimeException("Sale not found with id " + dtoSalesDetail.getSales().getId());
        }
            salesDetail.setAmount(dtoSalesDetail.getAmount());
            salesDetail.setSubTotal(dtoSalesDetail.getSubTotal());
            salesDetail.setProduct(product);
            salesDetail.setSales(newSales);
            salesDetail.setCreateAt(dtoSalesDetail.getCreatedAt());

        repository.save(salesDetail);
    }
}
