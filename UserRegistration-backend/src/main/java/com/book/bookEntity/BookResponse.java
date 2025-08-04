package com.book.bookEntity;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse{

    private Integer id;
    private String title;
    private String authorName;
    private String synopsis;
    private String isbn;
    private String owner;//just the full name of the user
    private byte[] cover;
    private double rate;//It is the rating for that particular book
    private boolean archived;
    private boolean shareable;

}
