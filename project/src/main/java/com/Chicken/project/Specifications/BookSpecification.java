package com.Chicken.project.Specifications;

import com.Chicken.project.entity.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {
    public static Specification<Book> hasCode(String code){
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.lower(root.get("code")),  "%"+code.toLowerCase() +"%");
    }
    public static Specification<Book> hasAuthor(String author){
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.lower(root.get("author")),  "%"+author.toLowerCase() +"%");
    }
    public static Specification<Book> hasTitle(String title){
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.lower(root.get("title")),  "%"+title.toLowerCase() +"%");
    }
    public static Specification<Book> hasPublisher(String publisher){
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.lower(root.get("publisher")),  "%"+publisher.toLowerCase() +"%");
    }
    public static Specification<Book> maxPage(int page){
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .greaterThanOrEqualTo(root.get("pageCount"), page);
    }
    public static Specification<Book> minPage(int page){
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .lessThanOrEqualTo(root.get("pageCount"), page);
    }
    public static Specification<Book> maxQuantity(int quantity){
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .greaterThanOrEqualTo(root.get("quantity"), quantity);
    }
    public static Specification<Book> minQuantity(int quantity){
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .lessThanOrEqualTo(root.get("quantity"), quantity);
    }
    public static Specification<Book> hasPrintType(String printType){
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.lower(root.get("printType")),  "%"+printType.toLowerCase() +"%");
    }
    public static Specification<Book> hasLanguage(String language){
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.lower(root.get("language")),  "%"+language.toLowerCase() +"%");
    }
    public static Specification<Book> hasDescription(String description){
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.lower(root.get("description")),  "%"+description.toLowerCase() +"%");
    }





}
