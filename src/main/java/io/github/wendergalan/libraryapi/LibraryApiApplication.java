package io.github.wendergalan.libraryapi;

import io.github.wendergalan.libraryapi.service.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class LibraryApiApplication {

//    @Autowired
//    private EmailService emailService;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

//    @Bean
//    public CommandLineRunner runner() {
//        return args -> {
//            List<String> emails = Collections.singletonList("library-api-29322c@inbox.mailtrap.io");
//            emailService.sendEmails("Testando serviço de emails.", emails);
//        };
//    }

    public static void main(String[] args) {
        SpringApplication.run(LibraryApiApplication.class, args);
    }

}
