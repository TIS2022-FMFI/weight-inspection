package com.example.weight_inspection.services;

import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
public class ConfigurationService {
    private final String DefaultToleranceName = "default_tolerance";
}
