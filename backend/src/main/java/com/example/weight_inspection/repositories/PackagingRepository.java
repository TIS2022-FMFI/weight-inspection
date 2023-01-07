package com.example.weight_inspection.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.weight_inspection.models.Packaging;

public interface PackagingRepository extends PagingAndSortingRepository<Packaging, Long>{

  Packaging findByNameOrderByIdDesc(String name);

  Packaging findById(long id);
}