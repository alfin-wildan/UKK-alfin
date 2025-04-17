package com.ujiKom.ukkKasir.SalesManagement.SalesDetail.DTO;

import com.ujiKom.ukkKasir.GeneralComponent.DTO.DTOGeneral;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DTOSalesDetail {
    private Integer id;
    private Integer amount;
    private Integer subTotal;
    private DTOGeneral product;
    private DTOGeneral sales;
    private LocalDateTime createdAt;
}
