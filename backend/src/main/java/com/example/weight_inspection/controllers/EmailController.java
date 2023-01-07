package com.example.weight_inspection.controllers;

import com.example.weight_inspection.models.Email;
import com.example.weight_inspection.repositories.EmailRepository;
import com.example.weight_inspection.transfer.ListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("api/email")
public class EmailController {
    private final EmailRepository emailRepository;

    @Autowired
    public EmailController(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @GetMapping
    public ResponseEntity<ListResponse<Email>> GetEmails(
            @RequestParam(value = "page", defaultValue = "0") int currentPage,
            @RequestParam(value = "page_size", defaultValue = "100") int pageSize) {

        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<Email> page = emailRepository.findAll(pageable);
        ListResponse<Email> listResponse = new ListResponse<>(page);
        return new ResponseEntity<>(listResponse, HttpStatus.OK);
    }

    @GetMapping("{emailId}")
    public ResponseEntity<Email> getEmailById(@PathVariable Long emailId) {

        Optional<Email> email = emailRepository.findById(emailId);
        if (!email.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(email.get(), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<Email> saveEmail(@RequestBody @Valid Email email, BindingResult bindingResult) {

        if (bindingResult.hasErrors() || email == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        email.setId(null);
        emailRepository.save(email);
        return new ResponseEntity<>(email, HttpStatus.CREATED);
    }

    @PutMapping("{emailId}")
    public ResponseEntity<Email> replaceEmail(@RequestBody @Valid Email email,
                                                            BindingResult bindingResult,
                                                            @PathVariable Long emailId) {

        if (bindingResult.hasErrors() || email == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Email> replacedEmail = emailRepository.findById(emailId);
        if (!replacedEmail.isPresent()) {
            email.setId(null);
            emailRepository.save(email);
            return new ResponseEntity<>(email, HttpStatus.NO_CONTENT);
        }

        email.setId(emailId);
        emailRepository.save(email);
        return new ResponseEntity<>(email, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("{emailId}")
    public ResponseEntity<Email> deleteEmail(@PathVariable Long emailId) {

        Optional<Email> email  = emailRepository.findById(emailId);
        if (!email.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Email delEmail  = email.get();
        delEmail.setId(emailId);
        emailRepository.delete(delEmail);
        return new ResponseEntity<>(delEmail, HttpStatus.NO_CONTENT);
    }
}