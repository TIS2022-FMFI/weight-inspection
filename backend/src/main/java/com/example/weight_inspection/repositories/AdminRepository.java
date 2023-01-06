package com.example.weight_inspection.repositories;

import com.example.weight_inspection.models.Admin;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.weight_inspection.models.Packaging;

public interface AdminRepository extends PagingAndSortingRepository<Admin, Long> {

    Admin findByUsername(String username);
}