package com.ujiKom.ukkKasir.GeneralComponent.Entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SuccessFloatResult {
    private String timestamp;
    private int responseCode;
    private String responseReason;
    private String responseMessage;
    private float responseData;
    private int page;
    private int size;
    private int totalData;
    private int pageCount;
}
