package com.book.bookEntity;

import com.book.common.PageResponse;
import com.book.exception.OperationNotPermittedException;
import com.book.file.FileStorageService;
import com.book.history.BookTransactionHistory;
import com.book.history.BookTransactionHistoryRepo;
import com.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.OperationNotSupportedException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepo transactionHistoryRepo;
    private final FileStorageService fileStorageService;

    public Integer save(RequestBook request, Authentication currentuser) {
        User user = (User)(currentuser.getPrincipal());
        Book book = BookMapper.toBook(request);
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    //todo check this method once
    public BookResponse findById(Integer bookId) {
         return bookRepository.findById(bookId)
                 .map(book->BookMapper.toResponseBook(book))//BookMapper::toResponseBook
                 .orElseThrow(()->new EntityNotFoundException("Book is not found with this id"+bookId));
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {

        User user = (User)connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());//this will sort the books in the descending order that means recently created will be seen on the top
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable,user.getId());
        List<BookResponse> bookResponses = books.stream()
                .map(book->BookMapper.toResponseBook(book))
                .toList();

        return new PageResponse<>(
                //this is the constructor of PageResponse
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements()
                ,books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = (User)connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page,size,Sort.by("creationDate").descending());
        Page<Book>books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()),pageable);
        List<BookResponse> bookResponses = books.stream()
                .map(BookMapper::toResponseBook)
                .toList();

        return new PageResponse<>(
                //this is the constructor
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements()
                ,books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
                User user = (User)connectedUser.getPrincipal();
                Pageable pageable = PageRequest.of(page,size,Sort.by("creationDate").descending());
                Page<BookTransactionHistory>allBorrowedBooks =
                                   transactionHistoryRepo.findAllBorrowedBooks(pageable,user.getId());
                List<BorrowedBookResponse>bookResponse = allBorrowedBooks.stream()
                        .map(BookMapper::toBorrowedBookResponse)
                        .toList();

                return new PageResponse<>(
                        bookResponse,
                        allBorrowedBooks.getNumber(),
                        allBorrowedBooks.getSize()
                        , allBorrowedBooks.getNumberOfElements(),
                        allBorrowedBooks.getTotalPages(),
                        allBorrowedBooks.isFirst(),
                        allBorrowedBooks.isLast()
                );
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size,
                                                                   Authentication connectedUser) {
        User user = (User)connectedUser.getPrincipal();//get the user from the context holder object
        Pageable pageable = PageRequest.of(page,size,Sort.by("creationDate").descending());
        Page<BookTransactionHistory>allBorrowedBooks =
                transactionHistoryRepo.findAllReturnedBooks(pageable,user.getId());
        List<BorrowedBookResponse>bookResponse = allBorrowedBooks.stream()
                .map(BookMapper::toBorrowedBookResponse)
                .toList();

        return new PageResponse<>(
                bookResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize()
                , allBorrowedBooks.getNumberOfElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public Integer updateSharableStatus(Integer bookId, Authentication connectedUser) {
           //before updating a book first we have to check whether the book is available or not in the database
            Book book = bookRepository.findById(bookId).orElseThrow(
                    () -> new EntityNotFoundException("Book is not found with this id"+bookId)
            );
            User user = (User)connectedUser.getPrincipal();
            //In the above step we return the exception if the book is not found with the provided id
            //suppose if the book is found now only the owner of the book is eligible to set the sharable status of this book
           if(!(Objects.equals(book.getOwner().getId(),user.getId()))){
               //if this is the case then he is not the owner of the book so he cannot update the book
               throw  new OperationNotPermittedException("you have No rights to update the status of the Book..");
            }
           //now he is the owner he can update the book
           book.setShareable(!book.isShareable());//previous will be Update for instance if the book is shareable first then it will update the shareable status to non shareable
           bookRepository.save(book);
           return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        //before updating a book first we have to check whether the book is available or not in the database
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new EntityNotFoundException("Book is not found with this id"+bookId)
        );
        User user = (User)connectedUser.getPrincipal();
        //In the above step we return the exception if the book is not found with the provided id
        //suppose if the book is found now only the owner of the book is eligible to set the sharable status of this book
        if(!(Objects.equals(book.getOwner().getId(),user.getId()))){
            //if this is the case then he is not the owner of the book so he cannot update the book
            throw  new OperationNotPermittedException("You have No rights to archive the Book..");
        }
        //now he is the owner he can update the book
        book.setArchived(!book.isArchived());//previous will be Update for instance if the book is shareable first then it will update the shareable status to non archived
        bookRepository.save(book);
        return bookId;
    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {

        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new EntityNotFoundException("Book is not found with this id"+bookId)
        );

        //check if the book is archived or not-sharable
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("This book is not borrowable becoz it is archived or non-shareable");
        }

        User user = (User)connectedUser.getPrincipal();

        //the owner of the book cannot borrow the same book
        if(Objects.equals(book.getOwner().getId(),user.getId())){
             throw new OperationNotPermittedException("owner of this book is you..so you cant borrow your own book..");
        }
        //now check whether the book is already borrowed or not
         final boolean isAlreadyBorrowed = transactionHistoryRepo.
                                                       isAlreadyborrowedByUser(book.getId(),user.getId());

         if(isAlreadyBorrowed){
             throw new OperationNotPermittedException("The requested book is already borrowed");
         }

         BookTransactionHistory bookTransactionHistory =
                  BookTransactionHistory.builder()
                                  .user(user)
                                          .book(book)
                                                  .returned(false)
                                                          .returnApproved(false).build();

         return  transactionHistoryRepo.save(bookTransactionHistory).getId();
    }

    public Integer returnBook(Integer bookId, Authentication connectedUser) {

        //firstly we have to check the book is available in the database
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new EntityNotFoundException("Book is not found with this id"+bookId)
        );

        //check if the book is archived or not-sharable
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("This book is not ready to return because it is archived or non-shareable");
        }

        User user = (User)connectedUser.getPrincipal();
        //the owner of the book cannot borrow the same book
        if(Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("owner of this book is you..so you cant borrow your own book..");
        }

        //now check if the book is borrowed or not
        BookTransactionHistory bookTransactionHistory = transactionHistoryRepo.
                                                          findByBookIdAndUserId(bookId,user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("The requested book is not borrowed bro..."));

        bookTransactionHistory.setReturned(true);//now we are returning the book
        return transactionHistoryRepo.save(bookTransactionHistory).getId();
    }

    public Integer approveReturnedBook(Integer bookId, Authentication connectedUser) {
        //firstly we have to check the book is available in the database or not
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new EntityNotFoundException("Book is not found with this id"+bookId)
        );

        User user = (User)connectedUser.getPrincipal();
        //the owner of the book cannot approve return for  the same book
        if(Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("owner of this book is you..so you cant approve return your own book..");
        }

        //now check if the book is borrowed or not
        BookTransactionHistory bookTransactionHistory = transactionHistoryRepo.
                findByBookOwnerIdAndUserId(bookId,user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("the book is not returned yet...so you cannot approve this return"));
        bookTransactionHistory.setReturnApproved(true);
        return transactionHistoryRepo.save(bookTransactionHistory).getId();
    }

    public void uploadCoverPic(MultipartFile file, Authentication connectedUser, Integer bookId) {
          //first we need to get the book to upload its cover picture
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new EntityNotFoundException("Book is not found with this id"+bookId)
        );
        User user = (User)connectedUser.getPrincipal();
        //we have to upload the files separetely for the users at some folder at the server
        var bookCover = fileStorageService.saveFile(file,user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);
    }

}
