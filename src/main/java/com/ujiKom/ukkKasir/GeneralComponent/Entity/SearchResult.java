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
public class SearchResult<T> {
    private List<T> listData;
    private int totalData;
    private int pageSize;
    private int currentPage;
    private int totalPage;

}
