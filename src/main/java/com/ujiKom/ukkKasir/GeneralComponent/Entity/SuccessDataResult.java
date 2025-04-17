package com.ujiKom.ukkKasir.GeneralComponent.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.ss.formula.functions.T;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SuccessDataResult{
    private String timestamp;
    private int responseCode;
    private String responseReason;
    private String responseMessage;
    private T listData;
    private int page;
    private int size;
    private int totalData;
    private int pageCount;
}