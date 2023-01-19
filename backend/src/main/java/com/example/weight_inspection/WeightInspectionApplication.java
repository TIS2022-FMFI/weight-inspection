package com.example.weight_inspection;

import com.opencsv.CSVReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
@RestController
@EnableAsync
@EnableScheduling
public class WeightInspectionApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(WeightInspectionApplication.class, args);
	}
}
