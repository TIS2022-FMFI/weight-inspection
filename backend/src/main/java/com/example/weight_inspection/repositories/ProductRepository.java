package com.example.weight_inspection.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.weight_inspection.models.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {

  Product findByRefference(String refference);

  Product findById(long id);
}