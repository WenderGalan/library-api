package io.github.wendergalan.libraryapi.model.repository;

import io.github.wendergalan.libraryapi.model.entity.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    private BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com o isbn informado.")
    public void returnTrueWhenIsbnExists() {
        //cenario
        String isbn = "123";
        Book book = createNewBook(isbn);
        entityManager.persist(book);

        //execucao
        boolean exists = repository.existsByIsbn(isbn);

        assertThat(exists).isTrue();
    }

    private Book createNewBook(String isbn) {
        return Book.builder().title("Aventuras").author("Fulano").isbn(isbn).build();
    }

    @Test
    @DisplayName("Deve retornar falso quando não existir um livro na base com o isbn informado.")
    public void returnFalseWhenIsbnDoesntExists() {
        //cenario
        String isbn = "123";

        //execucao
        boolean exists = repository.existsByIsbn(isbn);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve obter um livro por ID.")
    public void findByIdTest() {
        Book book = createNewBook("123");
        entityManager.persist(book);

        // execucao
        Optional<Book> foundBook = repository.findById(book.getId());

        assertThat(foundBook.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Deve salvar um livro.")
    public void saveBookTest() {
        Book book = createNewBook("123");

        Book savedBook = repository.save(book);

        assertThat(savedBook.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar um livro.")
    public void deleteBookTest() {
        Book book = createNewBook("123");
        entityManager.persist(book);

        Book foundBook = entityManager.find(Book.class, book.getId());

        repository.delete(foundBook);

        Book deletedBook = entityManager.find(Book.class, book.getId());
        assertThat(deletedBook).isNull();
    }
}
