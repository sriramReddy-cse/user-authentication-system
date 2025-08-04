package com.book.bookEntity;

import com.book.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("books")
@Tag(name = "Books", description = "APIs related to book management") // Adding a tag for the controller
public class BookController {

    private final BookService bookService;

    @PostMapping
    @Operation(summary = "Save a new book", description = "Saves a new book to the library")
    public ResponseEntity<Integer> saveBook(@RequestBody @Valid RequestBook request,
                                            Authentication currentuser) {
        return ResponseEntity.ok(bookService.save(request, currentuser));
    }

    @GetMapping("{book-id}")
    @Operation(summary = "Find book by ID", description = "Fetches details of a book by its ID")
    public ResponseEntity<BookResponse> findBookById(@PathVariable(name = "book-id") Integer bookId) {
        return ResponseEntity.ok(bookService.findById(bookId));
    }

    @GetMapping
    @Operation(summary = "Get all books", description = "Fetches all books with pagination")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser) {
        return ResponseEntity.ok(bookService.findAllBooks(page, size, connectedUser));
    }

    @GetMapping("/owner")
    @Operation(summary = "Get all books by owner", description = "Fetches books owned by the current user")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser) {
        return ResponseEntity.ok(bookService.findAllBooksByOwner(page, size, connectedUser));
    }

    @GetMapping("/borrowed")
    @Operation(summary = "Get all borrowed books", description = "Fetches all borrowed books by the current user")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser) {
        return ResponseEntity.ok(bookService.findAllBorrowedBooks(page, size, connectedUser));
    }

    @GetMapping("/returned")
    @Operation(summary = "Get all returned books", description = "Fetches all returned books by the current user")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser) {
        return ResponseEntity.ok(bookService.findAllReturnedBooks(page, size, connectedUser));
    }

    @PatchMapping("/shareable/{book-id}")
    @Operation(summary = "Update book's shareable status", description = "Allows updating the shareable status of a book")
    public ResponseEntity<Integer> updateSharableStatus(@PathVariable("book-id") Integer bookId,
                                                        Authentication connectedUser,
                                                        ServletResponse servletResponse) {
        return ResponseEntity.ok(bookService.updateSharableStatus(bookId, connectedUser));
    }

    @PatchMapping("/archived/{book-id}")
    @Operation(summary = "Update book's archived status", description = "Allows updating the archived status of a book")
    public ResponseEntity<Integer> updateArchivedStatus(@PathVariable("book-id") Integer bookId,
                                                        Authentication connectedUser) {
        return ResponseEntity.ok(bookService.updateArchivedStatus(bookId, connectedUser));
    }

    @PostMapping("/borrow/{book-id}")
    @Operation(summary = "Borrow a book", description = "Allows a user to borrow a book")
    public ResponseEntity<Integer> borrowBook(@PathVariable(name = "book-id") Integer bookId,
                                              Authentication connectedUser) {
        return ResponseEntity.ok(bookService.borrowBook(bookId, connectedUser));
    }

    @PatchMapping("/borrow/return/{book-id}")
    @Operation(summary = "Return a borrowed book", description = "Allows a user to return a borrowed book")
    public ResponseEntity<Integer> returnBorrowedBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser) {
        return ResponseEntity.ok(bookService.returnBook(bookId, connectedUser));
    }

    @PatchMapping("/borrow/return/approve/{book-id}")
    @Operation(summary = "Approve returned book", description = "Allows the owner to approve the returned book")
    public ResponseEntity<Integer> approveReturnedBorrowedBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser) {
        return ResponseEntity.ok(bookService.approveReturnedBook(bookId, connectedUser));
    }

    @PostMapping(value = "/cover/{book-id}", consumes = "multipart/form-data")
    @Operation(summary = "Upload book cover", description = "Allows a user to upload a cover picture for a book")
    public ResponseEntity<?> uploadBookCoverPicture(
            @PathVariable("book-id") Integer bookId,
            @RequestPart("file") MultipartFile file,
            Authentication connectedUser){
        bookService.uploadCoverPic(file,connectedUser,bookId);
        return ResponseEntity.accepted().build();
    }
}
