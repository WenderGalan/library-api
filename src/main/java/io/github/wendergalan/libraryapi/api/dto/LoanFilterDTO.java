package io.github.wendergalan.libraryapi.api.dto;

import lombok.Data;

@Data
public class LoanFilterDTO {
    private String isbn;
    private String customer;
}
