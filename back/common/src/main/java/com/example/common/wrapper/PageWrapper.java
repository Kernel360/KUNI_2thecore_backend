package com.example.common.wrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageWrapper<T> {

    private List<T> items;
    private int totalCount;
    private int page;
    private int size;

    public static <T> PageWrapper<T> of(List<T> items, int totalCount, int page, int size){
        return new PageWrapper<>(items, totalCount, page, size);
    }

}
