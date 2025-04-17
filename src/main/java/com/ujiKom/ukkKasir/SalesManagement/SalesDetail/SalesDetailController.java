package com.ujiKom.ukkKasir.SalesManagement.SalesDetail;

import com.ujiKom.ukkKasir.GeneralComponent.Entity.SearchResult;
import com.ujiKom.ukkKasir.GeneralComponent.Utility.ServiceHelper;
import com.ujiKom.ukkKasir.SalesManagement.SalesDetail.DTO.DTOSalesDetail;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@Slf4j
@RequestMapping("/sales/detail")
public class SalesDetailController {
    private final ServiceHelper serviceHelper;
    private final SalesDetailService salesDetailService;

    public SalesDetailController(ServiceHelper serviceHelper, SalesDetailService salesDetailService) {
        this.serviceHelper = serviceHelper;
        this.salesDetailService = salesDetailService;
    }

    @GetMapping("/list")
    public String listAll(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            Model model) {
        try {
            SearchResult<DTOSalesDetail> result = salesDetailService.listAll(search, id, page, size);
            model.addAttribute("searchResult", result);
        } catch (Exception e) {
            log.error("Failed to retrieve sales details: {}", e.getMessage());
            model.addAttribute("error", "Failed to load sales details.");
        }
        return "sales/detail/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("dtoSalesDetail", new DTOSalesDetail());
        return "sales/detail/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("dtoSalesDetail") DTOSalesDetail dtoSalesDetail,
                      HttpServletRequest request,
                      RedirectAttributes redirectAttributes,
                      Model model) {
        try {
            salesDetailService.createSalesDetail(dtoSalesDetail);
            redirectAttributes.addFlashAttribute("message", "Sales detail berhasil ditambahkan.");
            return "redirect:/sales/detail/list";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "sales/detail/add";
        } catch (Exception e) {
            log.error("Error adding sales detail: {}", e.getMessage());
            model.addAttribute("error", "Terjadi kesalahan!");
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan!");
            return "sales/detail/add";
        }
    }

}
