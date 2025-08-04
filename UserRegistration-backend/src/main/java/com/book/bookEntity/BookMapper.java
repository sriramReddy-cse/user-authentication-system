package com.book.bookEntity;

import com.book.file.FileUtils;
import com.book.history.BookTransactionHistory;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {

    public static Book toBook(RequestBook request) {
         return  Book.builder()
                  .id(request.id())
                  .authorName(request.authorName())
                  .title(request.title())
                  .archived(false)//book is not archived first
                  .shareable(request.shareable())
                  .isbn(request.isbn())
                  .synopsis(request.synopsis())
                  .build();
    }

    public static BookResponse toResponseBook(Book book) {
         return BookResponse.builder()
                 .id(book.getId())
                 .authorName(book.getAuthorName())
                 .title(book.getTitle())
                 .isbn(book.getIsbn())
                 .synopsis(book.getSynopsis())
                 .shareable(book.isShareable())
                 .archived(book.isArchived())
                 .cover(FileUtils.readFileFromPath(book.getBookCover()))
                 .owner(book.getOwner().fullname())
                 .rate(book.getRate())
                 .build();
    }

    public static BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory history) {
        return BorrowedBookResponse.builder()
                .id(history.getBook().getId())
                .title(history.getBook().getTitle())
                .authorName(history.getBook().getAuthorName())
                .isbn(history.getBook().getIsbn())
                .rate(history.getBook().getRate())
                .returned(history.isReturned())
                .returnApproved(history.isReturnApproved())
                .build();
    }
}
