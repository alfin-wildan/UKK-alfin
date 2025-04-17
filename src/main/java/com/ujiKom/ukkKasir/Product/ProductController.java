package com.ujiKom.ukkKasir.Product;

import com.ujiKom.ukkKasir.GeneralComponent.Entity.SearchResult;
import com.ujiKom.ukkKasir.GeneralComponent.Utility.ServiceHelper;
import com.ujiKom.ukkKasir.Product.Exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/product")
public class ProductController {
    private final ProductService service;
    private final ServiceHelper serviceHelper;

    public ProductController(ProductService service, ServiceHelper serviceHelper) {
        this.service = service;
        this.serviceHelper = serviceHelper;
    }
    @GetMapping("/list")
    public String listAll(@RequestParam(defaultValue = "") String search,
                          @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          Model model) {
        SearchResult<Product> result = service.listAll(search, null, page, size);

        model.addAttribute("products", result.getListData());
        model.addAttribute("search", search);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", result.getTotalPage());

        return "product/list";
    }
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("product", new Product());
        return "product/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Product product, @RequestParam(value = "files", required = false) List<MultipartFile> files, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        try {
            service.addData(product, files, request);
            redirectAttributes.addFlashAttribute("message", "Produk berhasil ditambahkan.");
            return "redirect:/product/list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Produk gagal ditambahkan.");
            return "product/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Integer id, Model model) {
        Product product = service.getProducts().stream().filter(p -> p.getId().equals(id)).findFirst().orElseThrow(() -> new NotFoundException("Product not found"));
        model.addAttribute("product", product);
        return "product/edit";
    }

    @PostMapping("/update")
    public String updateData(@RequestParam("id") Integer id, @RequestParam("name") String name, @RequestParam("price") Integer price, @RequestParam(value = "files", required = false) List<MultipartFile> files, Model model,RedirectAttributes redirectAttributes) {
        try {
            service.updateData(id, name, price, files);
            redirectAttributes.addFlashAttribute("message", "Produk berhasil diperbarui.");
            return "redirect:/product/list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            Product product = service.getProducts().stream().filter(p -> p.getId().equals(id)).findFirst().orElseThrow(() -> new NotFoundException("Product not found"));
            model.addAttribute("product", product);
            redirectAttributes.addFlashAttribute("error", "Produk gagal diperbarui.");
            return "product/edit";
        }
    }

    @GetMapping("/edit-stock/{id}")
    public String editStockForm(@PathVariable("id") Integer id, Model model) {
        Product product = service.getProducts().stream().filter(p -> p.getId().equals(id)).findFirst().orElseThrow(() -> new NotFoundException("Product not found"));
        model.addAttribute("product", product);
        return "product/edit-stock";
    }

    @PostMapping("/update-stock")
    public String updateStock(@RequestParam("id") Integer id, @RequestParam("stock") Integer stock, Model model,RedirectAttributes redirectAttributes) {
        try {
            service.updateStock(id, stock);
            redirectAttributes.addFlashAttribute("message", "Stock berhasil diupdate.");
            return "redirect:/product/list";
        } catch (Exception e) {
            Product product = service.getProducts().stream().filter(p -> p.getId().equals(id)).findFirst().orElseThrow(() -> new NotFoundException("Product not found"));
            model.addAttribute("product", product);
            model.addAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Stock gagal diupdate.");
            return "product/edit-stock";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Integer id, HttpServletRequest request, Model model,RedirectAttributes redirectAttributes) {
        try {
            service.deleteData(id, request);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/product/list";
    }

    @GetMapping("/file/{pathImage}")
    public ResponseEntity<Resource> findImage(@PathVariable("pathImage") String pathImage) throws Exception {
        try {
            Resource imageResource = service.getFileResource(pathImage);
            String contentType = Files.probeContentType(Paths.get("./file/", pathImage));
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(imageResource);
        } catch (Exception e) {
            throw new Exception("Image not found", e);
        }
    }

}
