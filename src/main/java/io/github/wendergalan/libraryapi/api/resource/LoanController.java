package io.github.wendergalan.libraryapi.api.resource;

import io.github.wendergalan.libraryapi.api.dto.BookDTO;
import io.github.wendergalan.libraryapi.api.dto.LoanDTO;
import io.github.wendergalan.libraryapi.api.dto.LoanFilterDTO;
import io.github.wendergalan.libraryapi.api.dto.ReturnedLoanDTO;
import io.github.wendergalan.libraryapi.model.entity.Book;
import io.github.wendergalan.libraryapi.model.entity.Loan;
import io.github.wendergalan.libraryapi.service.BookService;
import io.github.wendergalan.libraryapi.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Api("Loan API")
public class LoanController {

    private final LoanService service;
    private final BookService bookService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Create a loan")
    public Long create(@RequestBody LoanDTO dto) {
        Book book = bookService.getBookByIsbn(dto.getIsbn())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));
        Loan entity = Loan.builder()
                .book(book)
                .customer(dto.getCustomer())
                .loanDate(LocalDate.now())
                .build();

        entity = service.save(entity);
        return entity.getId();
    }

    @PatchMapping("{id}")
    @ApiOperation("Update parameter \"returned\" of loan")
    public void returnBook(
            @PathVariable Long id,
            @RequestBody ReturnedLoanDTO dto) {
        Loan loan = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        loan.setReturned(dto.getReturned());
        service.update(loan);
    }

    @GetMapping
    @ApiOperation("Obtains a loan by parameters")
    public Page<LoanDTO> find(LoanFilterDTO dto, Pageable pageRequest) {
        Page<Loan> result = service.find(dto, pageRequest);
        List<LoanDTO> loans = result.getContent().stream().map(entity -> {
            Book book = entity.getBook();
            BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
            LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
            loanDTO.setBook(bookDTO);
            return loanDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(loans, pageRequest, result.getTotalElements());
    }
}
