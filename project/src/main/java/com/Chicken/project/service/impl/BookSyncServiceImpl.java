package com.Chicken.project.service.impl;

import com.Chicken.project.entity.Book;
import com.Chicken.project.exception.BusinessException;
import com.Chicken.project.repository.BookRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BookSyncServiceImpl {

    @Autowired
    BookRepository repo;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    public int syncGoogleBooks() {
        String url = "https://www.googleapis.com/books/v1/volumes?q=java";
        int count = 0;
        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.get("items");

            if (items != null && items.isArray()) {
                for (JsonNode item : items) {
                    String code = item.path("id").asText();
                    if (repo.existsByCode(code)) continue;

                    JsonNode info = item.path("volumeInfo");
                    Book book;
                    if(repo.findByCode(code)!= null) book = repo.findByCode(code);
                    else book = new Book();
                    book.setCode(code);
                    book.setTitle(info.path("title").asText(null));
                    book.setAuthor(info.path("authors").isArray() ? info.path("authors").get(0).asText(null) : null);
                    book.setPublisher(info.path("publisher").asText(null));
                    book.setPageCount(info.path("pageCount").asInt(0));
                    book.setPrintType(info.path("printType").asText(null));
                    book.setLanguage(info.path("language").asText(null));
                    book.setDescription(info.path("description").asText(null));
                    book.setQuantity(info.path("totalItems").asInt(1));

                    repo.save(book);
                    count++;
                }
            }
        } catch (Exception e) {
            throw new BusinessException("error.sync.failure");
        }
        return count;
    }
}
