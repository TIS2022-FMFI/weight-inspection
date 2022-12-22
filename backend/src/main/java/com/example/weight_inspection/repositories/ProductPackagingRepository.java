package com.example.weight_inspection.repositories;

import com.example.weight_inspection.models.Packaging;
import com.example.weight_inspection.models.Product;
import com.example.weight_inspection.models.ProductPackaging;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductPackagingRepository extends PagingAndSortingRepository<ProductPackaging, Long> {
    ProductPackaging findById(long id);
    ProductPackaging findByPackagingAndProduct(Packaging packaging, Product product);

}
