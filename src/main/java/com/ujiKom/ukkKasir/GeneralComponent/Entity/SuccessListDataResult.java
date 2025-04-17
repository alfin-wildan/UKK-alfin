package com.ujiKom.ukkKasir.GeneralComponent.Entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SuccessListDataResult<T> {
    private String timestamp;
    private int responseCode;
    private String responseReason;
    private String responseMessage;
    private List<T> listData;
    private int page;
    private int size;
    private int totalData;
    private int pageCount;
}
