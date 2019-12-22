package io.github.wendergalan.libraryapi.model.repository;

import io.github.wendergalan.libraryapi.model.entity.Book;
import io.github.wendergalan.libraryapi.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query(value = "SELECT CASE WHEN (COUNT(l.id) > 0) THEN TRUE ELSE FALSE END " +
            "FROM Loan l WHERE l.book = :book AND (l.returned IS NULL OR l.returned IS NOT TRUE)")
    boolean existsByBookAndNotReturned(@Param("book") Book book);
}
