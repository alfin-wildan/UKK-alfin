package com.ujiKom.ukkKasir.GeneralComponent.GlobalException;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParams(MissingServletRequestParameterException ex) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta")); // Setting the timezone to WIB
        String formattedDate = sdf.format(new Date());

        String paramName = ex.getParameterName();
        Map<String, Object> body = new HashMap<>();
        body.put("timeStamp", formattedDate);
        body.put("responseCode", HttpStatus.BAD_REQUEST.value());
        body.put("responseReason", HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put("responseMessage", "Parameter '" + paramName + "' is required!");
        body.put("responseData", null);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
