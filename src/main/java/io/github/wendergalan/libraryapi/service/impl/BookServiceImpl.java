package io.github.wendergalan.libraryapi.service.impl;

import io.github.wendergalan.libraryapi.model.entity.Book;
import io.github.wendergalan.libraryapi.model.repository.BookRepository;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements io.github.wendergalan.libraryapi.service.BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        return repository.save(book);
    }
}
