package com.book.bookEntity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowedBookResponse {

    private Integer id;
    private String title;
    private String authorName;
    private String isbn;
    private double rate;//It is the rating for that particular book
    private boolean returned;
    private boolean returnApproved;

}
