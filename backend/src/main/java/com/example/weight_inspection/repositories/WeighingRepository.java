package com.example.weight_inspection.repositories;

import com.example.weight_inspection.models.Weighing;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface WeighingRepository extends PagingAndSortingRepository<Weighing, Long> {
    Weighing findById(long id);

    @Query("SELECT w FROM Weighing w WHERE w.isExported = false")
    Weighing[] findNotExportedWeighings();
}
