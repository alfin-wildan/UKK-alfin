package com.ujiKom.ukkKasir.GeneralComponent.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SuccessStringResult {
    private String timestamp;
    private int responseCode;
    private String responseReason;
    private String responseMessage;
    private String responseData;
    private int page;
    private int size;
    private int totalData;
    private int pageCount;

    public SuccessStringResult(String message) {
        this.responseMessage = message;
    }
}
