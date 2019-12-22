package io.github.wendergalan.libraryapi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "loan")
public class Loan {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer", length = 100)
    private String customer;

    @JoinColumn(name = "id_book")
    @ManyToOne
    private Book book;

    @Column(name = "loanDate")
    private LocalDate loanDate;

    @Column(name = "returned")
    private Boolean returned;
}
