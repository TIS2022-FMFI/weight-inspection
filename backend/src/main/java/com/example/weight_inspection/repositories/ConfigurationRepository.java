package com.example.weight_inspection.repositories;

import com.example.weight_inspection.models.Admin;
import com.example.weight_inspection.models.Configuration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ConfigurationRepository extends CrudRepository<Configuration, Long> {

    Configuration findByName(String name);
}