package com.example.weight_inspection.controllers;

import com.example.weight_inspection.models.Configuration;
import com.example.weight_inspection.models.Email;
import com.example.weight_inspection.repositories.ConfigurationRepository;
import com.example.weight_inspection.services.ConfigurationService;
import com.example.weight_inspection.transfer.SetConfigurationToleranceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("api/configuration")
public class ConfigurationController {
    private final ConfigurationRepository configurationRepository;
    private final ConfigurationService configurationService;

    @Autowired
    public ConfigurationController(ConfigurationRepository configurationRepository, ConfigurationService configurationService) {

        this.configurationRepository = configurationRepository;
        this.configurationService = configurationService;
    }

    @GetMapping("tolerance")
    public ResponseEntity<Float> getTolerance() {

        Configuration toleranceConfiguration = configurationRepository.findByName(configurationService.getDefaultToleranceName());
        if (toleranceConfiguration == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(toleranceConfiguration.getValue(), HttpStatus.OK);
    }

    @PutMapping("tolerance")
    public ResponseEntity<String> setTolerance(@RequestBody @Valid SetConfigurationToleranceDTO setConfigurationToleranceDTO,
                                               BindingResult bindingResult) {

        if (bindingResult.hasErrors() || setConfigurationToleranceDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Configuration toleranceConfiguration = configurationRepository.findByName(configurationService.getDefaultToleranceName());
        if (toleranceConfiguration == null) {
            toleranceConfiguration = new Configuration();
            toleranceConfiguration.setName(configurationService.getDefaultToleranceName());
            toleranceConfiguration.setValue(setConfigurationToleranceDTO.getTolerance());
            configurationRepository.save(toleranceConfiguration);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        toleranceConfiguration.setValue(setConfigurationToleranceDTO.getTolerance());
        configurationRepository.save(toleranceConfiguration);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}