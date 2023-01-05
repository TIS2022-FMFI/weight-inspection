 package com.example.weight_inspection.repositories;

 import org.springframework.data.repository.PagingAndSortingRepository;

 import com.example.weight_inspection.models.Palette;

 public interface PaletteRepository extends PagingAndSortingRepository<Palette, Long> {

     Palette findById(long id);

     Palette findByNameOrderByIdDesc(String name);
 }
