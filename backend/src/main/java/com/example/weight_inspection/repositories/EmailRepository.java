package com.example.weight_inspection.repositories;

import com.example.weight_inspection.models.Admin;
import com.example.weight_inspection.models.Email;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.weight_inspection.models.Packaging;

public interface EmailRepository extends PagingAndSortingRepository<Email, Long> {

    Email findByEmail(String email);
}