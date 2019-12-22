package io.github.wendergalan.libraryapi.service.impl;

import io.github.wendergalan.libraryapi.exception.BusinessException;
import io.github.wendergalan.libraryapi.model.entity.Book;
import io.github.wendergalan.libraryapi.model.repository.BookRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements io.github.wendergalan.libraryapi.service.BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if (repository.existsByIsbn(book.getIsbn()))
            throw new BusinessException("Isbn já cadastrado.");
        return repository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public void delete(Book book) {
        if (book == null || book.getId() == null)
            throw new IllegalArgumentException("Book id cant be null");
        this.repository.delete(book);
    }

    @Override
    public Book update(Book book) {
        if (book == null || book.getId() == null)
            throw new IllegalArgumentException("Book id cant be null");
        return this.repository.save(book);
    }

    @Override
    public Page<Book> find(Book filter, Pageable pageRequest) {
        Example<Book> example = Example.of(filter,
                ExampleMatcher
                        .matching()
                        // Ignora maiusculas e minusculas
                        .withIgnoreCase()
                        // Ignora os valores nulos
                        .withIgnoreNullValues()
                        // Verifica se a string contem algo da pesquisa
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
        );
        return repository.findAll(example, pageRequest);
    }

    @Override
    public Optional<Book> getBookByIsbn(String isbn) {
        return repository.findByIsbn(isbn);
    }
}
