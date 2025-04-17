package com.ujiKom.ukkKasir.Product;
import com.ujiKom.ukkKasir.GeneralComponent.Utility.ServiceHelper;
import com.ujiKom.ukkKasir.GeneralComponent.Entity.SearchResult;
import com.ujiKom.ukkKasir.Product.Exception.NotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.ujiKom.ukkKasir.GeneralComponent.Constant.ResponseMessageConstant.DATA_NOT_FOUND;

@Service
public class ProductServiceImpl implements ProductService{
    private final ServiceHelper serviceHelper;
    private final ProductRepository repository;
    @PersistenceContext
    private transient EntityManager em;

    public ProductServiceImpl(ServiceHelper serviceHelper, ProductRepository repository,EntityManager em) {
        this.serviceHelper = serviceHelper;
        this.repository = repository;
        this.em = em;
    }

    @Override
    public List<Product> getProducts() {
        return repository.findAll();
    }

    @Override
    public SearchResult<Product> listAll(String search, Integer id, int page, int size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);
        List<Predicate> predicates = createPredicate(search, id, cb, root);
        query.select(root)
                .where(predicates.toArray(new Predicate[0]));

        query.orderBy(cb.desc(root.get("id")));

        TypedQuery<Product> typedQuery = em.createQuery(query);
        typedQuery.setFirstResult((page - 1) * size);
        typedQuery.setMaxResults(size);
        List<Product> resultList = typedQuery.getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(Product.class);
        countQuery.select(cb.count(countRoot))
                .where(createPredicate(search, id, cb, countRoot).toArray(new Predicate[0]));


        Long totalData = em.createQuery(countQuery).getSingleResult();
        int totalPage = (int) Math.ceil((double) totalData / size);

        return new SearchResult<>(resultList, totalData.intValue(), size, page, totalPage);
    }

    @Override
    public void addData(Product products, List<MultipartFile> files, HttpServletRequest request) throws Exception {
        Product newData = new Product();
        newData.setId(products.getId());
        newData.setName(products.getName());
        newData.setPrice(products.getPrice());
        newData.setStock(products.getStock());
        newData.setCreateAt(LocalDateTime.now(ZoneId.of("Asia/Jakarta")));

        List<String> fileList = new ArrayList<>();
        String uploadDir = "./file/";

        if (files != null && !files.isEmpty()) {
            Path uploadPath = Paths.get(uploadDir);
            if (!uploadPath.toFile().exists()) {
                uploadPath.toFile().mkdirs();
            }
            String date = serviceHelper.fileNameDate();
            for (MultipartFile file : files) {
                String originalFileName = file.getOriginalFilename();
                if (originalFileName != null) {
                    String newFileName = date + "-" + originalFileName;
                    Path filePath = uploadPath.resolve(newFileName);

                    if (isImageFile(originalFileName)) {
                        Thumbnails.of(file.getInputStream())
                                .size(800, 600)
                                .toFile(filePath.toFile());
                    } else {
                        Path zipFilePath = filePath.resolveSibling(newFileName + ".zip");
                        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFilePath));
                             InputStream fis = file.getInputStream()) {
                            ZipEntry zipEntry = new ZipEntry(originalFileName);
                            zos.putNextEntry(zipEntry);

                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = fis.read(buffer)) >= 0) {
                                zos.write(buffer, 0, length);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    fileList.add(newFileName);
                }
            }
        }

        newData.setFile(fileList);
        repository.save(newData);
    }

    @Override
    public Product updateData(Integer id, String name, Integer price, List<MultipartFile> files) throws Exception {
        Product existingData = validateProductId(id);
        List<String> updatedFileList = new ArrayList<>();
        String uploadDir = "./file/";

        if (files != null && !files.isEmpty()) {
            Path uploadPath = Paths.get(uploadDir);
            if (!uploadPath.toFile().exists()) {
                uploadPath.toFile().mkdirs();
            }

            String date = serviceHelper.fileNameDate();

            for (MultipartFile file : files) {
                String originalFileName = file.getOriginalFilename();
                if (originalFileName != null) {
                    String newFileName = date + "-" + originalFileName;
                    Path filePath = uploadPath.resolve(newFileName);

                    if (isImageFile(originalFileName)) {
                        Thumbnails.of(file.getInputStream())
                                .size(800, 600)
                                .toFile(filePath.toFile());
                    } else {
                        Path zipFilePath = filePath.resolveSibling(newFileName + ".zip");
                        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFilePath));
                             InputStream fis = file.getInputStream()) {
                            ZipEntry zipEntry = new ZipEntry(originalFileName);
                            zos.putNextEntry(zipEntry);

                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = fis.read(buffer)) >= 0) {
                                zos.write(buffer, 0, length);
                            }
                        }
                    }

                    updatedFileList.add(newFileName);
                }
            }

            existingData.setFile(updatedFileList);
        }

        existingData.setName(name);
        existingData.setPrice(price);

        return repository.save(existingData);
    }

    @Override
    public Product updateStock(Integer id, Integer stock) {
        Product existingData = validateProductId(id);
        existingData.setStock(stock);
        return repository.save(existingData);
    }

    @Override
    public void deleteData(Integer id, HttpServletRequest request) throws Exception {
        validateProductId(id);
        Product existingEntity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        repository.deleteById(id);
    }

    @Override
    public Resource getFileResource(String path) throws Exception {
        String basePath = "./file/";
        Path filePath = Paths.get(basePath, path);

        if (Files.exists(filePath)) {
            byte[] imageData = Files.readAllBytes(filePath);
            return new ByteArrayResource(imageData);
        } else {
            throw new NotFoundException("File not found.");
        }
    }

    private List<Predicate> createPredicate(String search, Integer id, CriteriaBuilder cb, Root<Product> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (id != null) {
            predicates.add(cb.equal(root.get("id"), id));
        }

        if (search != null && !search.isEmpty()) {
            String likeValue = "%" + search.toUpperCase() + "%";

            if (serviceHelper.isNumeric(search)) {
                Integer numericValue = Integer.valueOf(search);
                predicates.add(cb.or(
                        cb.equal(root.get("id"), numericValue),
                        cb.equal(root.get("stock"), numericValue),
                        cb.equal(root.get("price"), numericValue)
                ));
            } else {
                predicates.add(cb.like(cb.upper(root.get("name")), likeValue));
            }
        }

        return predicates;
    }

    private boolean isImageFile(String fileName) {
        String fileNameLower = fileName.toLowerCase();
        return fileNameLower.endsWith(".jpg") || fileNameLower.endsWith(".jpeg")
                || fileNameLower.endsWith(".png") || fileNameLower.endsWith(".bmp")
                || fileNameLower.endsWith(".gif");
    }

    private Product validateProductId(Integer listId) {
        return repository.findById(listId).orElseThrow(() ->
                new NotFoundException(DATA_NOT_FOUND));
    }
}