package com.ujiKom.ukkKasir.Product;


import com.ujiKom.ukkKasir.GeneralComponent.Entity.SearchResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    List<Product> getProducts();
    SearchResult<Product> listAll(String search, Integer id, int page, int size);
    void addData(Product products, List<MultipartFile> files, HttpServletRequest request) throws Exception;
    Product updateData(Integer id, String name, Integer price, List<MultipartFile> files) throws Exception;
    Product updateStock(Integer id, Integer stock);
    void deleteData (Integer id, HttpServletRequest request)throws Exception;
    Resource getFileResource(String path) throws Exception;
}
