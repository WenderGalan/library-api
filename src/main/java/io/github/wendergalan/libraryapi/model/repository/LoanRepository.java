package io.github.wendergalan.libraryapi.model.repository;

import io.github.wendergalan.libraryapi.model.entity.Book;
import io.github.wendergalan.libraryapi.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query(value = "SELECT CASE WHEN (COUNT(l.id) > 0) THEN TRUE ELSE FALSE END " +
            "FROM Loan l WHERE l.book = :book AND (l.returned IS NULL OR l.returned IS false)")
    boolean existsByBookAndNotReturned(@Param("book") Book book);

    @Query("SELECT l FROM Loan l JOIN l.book b WHERE b.isbn = :isbn OR l.customer = :customer")
    Page<Loan> findByBookIsbnOrCustomer(
            @Param("isbn") String isbn,
            @Param("customer") String customer,
            Pageable pageRequest
    );

    Page<Loan> findByBook(Book book, Pageable pageable);

    @Query("SELECT l FROM Loan l WHERE l.loanDate <= :threeDaysAgo AND (l.returned IS NULL OR l.returned IS FALSE)")
    List<Loan> findByLoanDateLessThanAndNotReturned(@Param("threeDaysAgo") LocalDate threeDaysAgo);

}
