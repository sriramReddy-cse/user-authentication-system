package com.book.bookEntity;

import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book>withOwnerId(Integer id){
         return ((root, query, criteriaBuilder) ->
                 criteriaBuilder.equal(root.get("owner").get("id"),id));
    }

}
