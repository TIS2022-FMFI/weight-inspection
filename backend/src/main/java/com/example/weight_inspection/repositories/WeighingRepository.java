package com.example.weight_inspection.repositories;

import com.example.weight_inspection.models.Weighing;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface WeighingRepository extends PagingAndSortingRepository<Weighing, Long> {
    Weighing findById(long id);
}
