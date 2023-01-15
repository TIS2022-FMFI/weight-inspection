package com.example.weight_inspection.repositories;

import com.example.weight_inspection.models.Admin;
import com.example.weight_inspection.models.Email;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.weight_inspection.models.Packaging;

import java.util.List;

public interface EmailRepository extends PagingAndSortingRepository<Email, Long> {

    Email findByEmail(String email);

    @Query("SELECT email FROM Email WHERE sendExports = true")
    String[] getEmailsBySendExportsIsTrue();
}