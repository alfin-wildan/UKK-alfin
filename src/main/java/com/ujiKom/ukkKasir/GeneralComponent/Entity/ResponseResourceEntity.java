package com.ujiKom.ukkKasir.GeneralComponent.Entity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public abstract class ResponseResourceEntity<T> {
    public ResponseEntity<HttpResponse<T>> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse<T>(message, httpStatus.value(), httpStatus.getReasonPhrase().toUpperCase()), httpStatus);
    }

    public ResponseEntity<HttpResponse<T>> responseWithData(HttpStatus httpStatus, String message, T responseData) {
        return new ResponseEntity<>(new HttpResponse<T>(message, httpStatus.value(), httpStatus.getReasonPhrase().toUpperCase(),
                responseData), httpStatus);
    }

    public ResponseEntity<HttpResponse<List<T>>> responseWithTotalData(HttpStatus httpStatus, String message, List<T> responseData, int totalData) {
        return new ResponseEntity<>(new HttpResponse<>(message, httpStatus.value(), httpStatus.getReasonPhrase().toUpperCase(), responseData, totalData), httpStatus);
    }

    public ResponseEntity<HttpResponse<T>> responseWithString(HttpStatus httpStatus, String message, String data) {
        return new ResponseEntity<>(new HttpResponse<>(message, httpStatus.value(), httpStatus.getReasonPhrase().toUpperCase(), (T) data), httpStatus);
    }


    public ResponseEntity<HttpResponse<T>> responseInteger(HttpStatus httpStatus, String message, Integer responseData) {
        return new ResponseEntity<>(new HttpResponse<T>(message, httpStatus.value(), httpStatus.getReasonPhrase().toUpperCase(),
                (T) responseData), httpStatus);
    }

    public ResponseEntity<HttpResponse<T>> responseLong(HttpStatus httpStatus, String message, Long responseData) {
        return new ResponseEntity<>(new HttpResponse<T>(message, httpStatus.value(), httpStatus.getReasonPhrase().toUpperCase(),
                (T) responseData), httpStatus);
    }

    public ResponseEntity<HttpResponse<T>> responseFloat(HttpStatus httpStatus, String message, Float responseData) {
        return new ResponseEntity<>(new HttpResponse<T>(message, httpStatus.value(), httpStatus.getReasonPhrase().toUpperCase(),
                (T) responseData), httpStatus);
    }

    public ResponseEntity<HttpResponse<T>> responseDouble(HttpStatus httpStatus, String message, Double responseData) {
        return new ResponseEntity<>(new HttpResponse<T>(message, httpStatus.value(), httpStatus.getReasonPhrase().toUpperCase(),
                (T) responseData), httpStatus);
    }

    public ResponseEntity<HttpResponse<Object>> responseWithDataObject(HttpStatus httpStatus, String message, Object responseData) {
        return new ResponseEntity<>(new HttpResponse<>(message, httpStatus.value(), httpStatus.getReasonPhrase().toUpperCase(),
                responseData), httpStatus);
    }

    public ResponseEntity<HttpResponse<List<T>>> responseWithListData(HttpStatus httpStatus, String message, List<T> responseData) {
        return new ResponseEntity<>(new HttpResponse<>(message, httpStatus.value(), httpStatus.getReasonPhrase().toUpperCase(),
                responseData), httpStatus);
    }

    public ResponseEntity<HttpResponse<List<T>>> responseWithListAndCount(HttpStatus httpStatus, String message, List<T> responseData, int countData) {
        return new ResponseEntity<>(new HttpResponse<>(message, httpStatus.value(), httpStatus.getReasonPhrase().toUpperCase(),
                responseData, countData), httpStatus);
    }

    public ResponseEntity<HttpResponse<List<T>>> responseWithPagination(HttpStatus httpStatus, String message, List<T> responseData, int page, int size, int totalData, int pageSize) {
        return new ResponseEntity<>(new HttpResponse<>(message, httpStatus.value(), httpStatus.getReasonPhrase().toUpperCase(),
                responseData, page, size, totalData, pageSize), httpStatus);
    }

    public ResponseEntity<HttpResponse<T>> responseHeader(HttpStatus httpStatus, String message, HttpHeaders headers) {
        return new ResponseEntity<>(new HttpResponse<T>(message, httpStatus.value(), httpStatus.getReasonPhrase().toUpperCase()), headers, httpStatus);
    }

    public ResponseEntity<HttpResponse<T>> responseWithDataHeader(HttpStatus httpStatus, String message, T responseData, HttpHeaders headers) {
        return new ResponseEntity<>(new HttpResponse<T>(message, httpStatus.value(), httpStatus.getReasonPhrase().toUpperCase(),
                responseData), headers, httpStatus);
    }
}