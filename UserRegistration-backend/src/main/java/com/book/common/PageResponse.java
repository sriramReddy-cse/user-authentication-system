package com.book.common;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

    private List<T>content;

    private int number;
    private int size;
    private long totalElements;
    private int totalPages;
    //basically this first and last are used to track the page whether it is the first page or the last page
    private boolean first;
    private boolean last;

}
