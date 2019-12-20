package io.github.wendergalan.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.wendergalan.libraryapi.api.dto.BookDTO;
import io.github.wendergalan.libraryapi.exception.BusinessException;
import io.github.wendergalan.libraryapi.model.entity.Book;
import io.github.wendergalan.libraryapi.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    private BookService service;

    @Test
    @DisplayName("Deve criar um livro com sucesso.")
    public void createBookTest() throws Exception {
        BookDTO dto = createNewBook();
        Book savedBook = Book.builder().id(11L).author("Arthur").title("As aventuras").isbn("001").build();

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(11L))
                .andExpect(jsonPath("title").value(dto.getTitle()))
                .andExpect(jsonPath("author").value(dto.getAuthor()))
                .andExpect(jsonPath("isbn").value(dto.getIsbn()));
    }

    @Test
    @DisplayName("Deve lançar um erro de validação quando não houver dados suficientes para a criação do livro.")
    public void createInvalidBookTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn já utilizado por outro.")
    public void createBookWithDuplicatedIsbn() throws Exception {
        BookDTO dto = createNewBook();
        String json = new ObjectMapper().writeValueAsString(dto);

        String mensagemErro = "Isbn já cadastrado.";
        BDDMockito.given(service.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException(mensagemErro));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(mensagemErro));
    }

    @Test
    @DisplayName("Deve obter informações de um livro.")
    public void getBookDetailsTest() throws Exception {
        Long id = 11L;

        Book book = Book.builder()
                .id(id)
                .title(createNewBook().getTitle())
                .author(createNewBook().getAuthor())
                .isbn(createNewBook().getIsbn())
                .build();

        BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(11L))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));
    }

    @Test
    @DisplayName("Deve retornar resource not found quando o livro procurado não existir.")
    public void bookNotFoundTest() throws Exception {
        BDDMockito.given(service.getById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um livro.")
    public void deleteBookTest() throws Exception {

        BDDMockito.given(service.getById(anyLong())).willReturn(Optional.of(Book.builder().id(11L).build()));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 11))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar resource not found quando não encontar um livro para deletar.")
    public void deleteInexistentBookTest() throws Exception {

        BDDMockito.given(service.getById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 11))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar um livro.")
    public void updateBookTest() throws Exception {
        Long id = 11L;
        String json = new ObjectMapper().writeValueAsString(createNewBook());

        Book updatingBook = Book.builder().id(id).title("some title").author("some author").isbn("321").build();
        BDDMockito.given(service.getById(anyLong()))
                .willReturn(Optional.of(updatingBook));

        Book updateBook = Book.builder().id(id).author("Arthur").title("As aventuras").isbn("001").build();
        BDDMockito.given(service.update(updatingBook))
                .willReturn(updateBook);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 11))
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(11L))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar um livro inexistente.")
    public void updateInexistentBookTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(createNewBook());
        BDDMockito.given(service.getById(anyLong()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 11))
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve filtrar livros.")
    public void findBooksTest() throws Exception {
        Long id = 11L;
        Book book = Book.builder()
                .id(id)
                .title(createNewBook().getTitle())
                .isbn(createNewBook().getIsbn())
                .author(createNewBook().getAuthor())
                .build();

        BDDMockito.given(service.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Book>(Collections.singletonList(book), PageRequest.of(0, 100), 1));

        String queryString = String.format("?title=%s&author=%s&page=0&size=100", book.getTitle(), book.getAuthor());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }

    private BookDTO createNewBook() {
        return BookDTO.builder().author("Arthur").title("As aventuras").isbn("001").build();
    }
}
