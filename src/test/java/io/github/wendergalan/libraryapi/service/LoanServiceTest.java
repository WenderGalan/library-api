package io.github.wendergalan.libraryapi.service;

import io.github.wendergalan.libraryapi.api.dto.LoanFilterDTO;
import io.github.wendergalan.libraryapi.exception.BusinessException;
import io.github.wendergalan.libraryapi.model.entity.Book;
import io.github.wendergalan.libraryapi.model.entity.Loan;
import io.github.wendergalan.libraryapi.model.repository.LoanRepository;
import io.github.wendergalan.libraryapi.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    LoanService service;

    @MockBean
    LoanRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um empréstimo.")
    public void saveLoanTest() {
        Book book = Book.builder().id(11L).build();
        Loan savingLoan = Loan.builder()
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = Loan.builder()
                .id(savingLoan.getId())
                .loanDate(LocalDate.now())
                .customer("Fulano")
                .book(book)
                .build();

        when(repository.existsByBookAndNotReturned(book)).thenReturn(false);
        when(repository.save(savingLoan)).thenReturn(savedLoan);

        Loan loan = service.save(savingLoan);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
    }

    @Test
    @DisplayName("Deve lançar erro de negócio ao salvar um emprestimo com livro ja emprestado.")
    public void loanedBookTest() {
        Book book = Book.builder().id(11L).build();
        Loan savingLoan = Loan.builder()
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();

        when(repository.existsByBookAndNotReturned(book)).thenReturn(true);

        Throwable exception = catchThrowable(() -> service.save(savingLoan));
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");

        verify(repository, never()).save(savingLoan);
    }

    @Test
    @DisplayName("Deve obter as informações de um empréstimo pelo ID")
    public void getLoanDetailsTest() {
        // cenário
        Long id = 11L;
        Loan loan = createLoan();
        loan.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(loan));

        // execucao
        Optional<Loan> result = service.getById(id);

        // verificacao
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());

        verify(repository).findById(id);
    }

    @Test
    @DisplayName("Deve atualizar um empréstimo.")
    public void updateLoanTest() {
        Loan loan = createLoan();
        loan.setId(11L);
        loan.setReturned(true);

        when(repository.save(loan)).thenReturn(loan);

        Loan updatedLoan = service.update(loan);

        assertThat(updatedLoan.getReturned()).isTrue();
        verify(repository).save(loan);
    }

    @Test
    @DisplayName("Deve filtrar empréstimos pelas propriedades.")
    public void findLoanTest() {
        // cenario
        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder().customer("Fulano").isbn("321").build();

        Loan loan = createLoan();
        loan.setId(11L);

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Loan> lista = Collections.singletonList(loan);
        Page<Loan> page = new PageImpl<Loan>(lista, pageRequest, lista.size());

        when(repository.findByBookIsbnOrCustomer(
                anyString(),
                anyString(),
                any(PageRequest.class))
        )
                .thenReturn(page);

        // Execucao
        Page<Loan> result = service.find(loanFilterDTO, pageRequest);

        // Verificacoes
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    public static Loan createLoan() {
        Book book = Book.builder().id(11L).build();
        return Loan.builder()
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();
    }
}
