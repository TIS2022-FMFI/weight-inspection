package com.example.weight_inspection.repositories;

import com.example.weight_inspection.models.Palette;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PaletteRepository extends PagingAndSortingRepository<Palette, Long> {
    Palette fyndById(long id);
    Palette findByName(String name);
}
