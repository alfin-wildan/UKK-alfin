package com.ujiKom.ukkKasir.SalesManagement.SalesDetail;

import com.ujiKom.ukkKasir.GeneralComponent.Entity.SearchResult;
import com.ujiKom.ukkKasir.SalesManagement.SalesDetail.DTO.DTOSalesDetail;

import java.util.List;

public interface SalesDetailService {
    List<SalesDetail> getSalesDetails();
    SearchResult<DTOSalesDetail> listAll(String search, Integer id, int page, int size);
    void createSalesDetail(DTOSalesDetail dtoSalesDetail);
}
