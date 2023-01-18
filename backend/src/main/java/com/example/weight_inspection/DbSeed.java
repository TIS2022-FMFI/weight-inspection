package com.example.weight_inspection;

import com.example.weight_inspection.models.*;
import com.example.weight_inspection.repositories.*;
import com.opencsv.CSVReader;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javatuples.Pair;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DbSeed {
    private final Path importFilePath;
    private final ProductRepository productRepository;

    private final PackagingRepository packagingRepository;

    private final PaletteRepository paletteRepository;

    private final ProductPackagingRepository productPackagingRepository;

    public DbSeed(ProductRepository productRepository, PackagingRepository packagingRepository, PaletteRepository paletteRepository, ProductPackagingRepository productPackagingRepository) throws URISyntaxException {
        this.importFilePath = Paths.get(this.getClass().getClassLoader().getResource("dbSeed.csv").toURI());
        this.productRepository = productRepository;
        this.packagingRepository = packagingRepository;
        this.paletteRepository = paletteRepository;
        this.productPackagingRepository = productPackagingRepository;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    private class Relationship {
        private String reference;
        private String paletteName;
        private String packagingName;
    }

    public List<Relationship> readImportFile(Path filePath) throws Exception {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                return csvReader.readAll().stream()
                        .map(item -> {
                            Relationship relationship = new Relationship();
                            relationship.setReference(item[1]);
                            relationship.setPaletteName(item[4]);
                            relationship.setPackagingName(item[5]);
                            return relationship;
                        })
                        .collect(Collectors.toList());
            }
        }
    }

    private Set<String> getUniqueReference(List<Relationship> relationships) {
        return relationships.stream()
                .map(Relationship::getReference)
                .collect(Collectors.toSet());
    }

    private Set<String>  getUniquePaletteName(List<Relationship> relationships) {
        return relationships.stream()
                .map(Relationship::getPaletteName)
                .collect(Collectors.toSet());
    }

    private Set<String>  getUniquePackagingName(List<Relationship> relationships) {
        return relationships.stream()
                .map(Relationship::getPackagingName)
                .collect(Collectors.toSet());
    }

    private Set<Pair<String, String>> getProductPackagingRelationships(List<Relationship> relationships) {
        return relationships.stream()
                .filter(relationship -> !relationship.getReference().isEmpty() && !relationship.getPackagingName().isEmpty())
                .map(relationship -> new Pair<String, String>(relationship.getReference(), relationship.getPackagingName()))
                .collect(Collectors.toSet());
    }

    private Set<Pair<String, String>> getProductPaletteRelationships(List<Relationship> relationships) {
        return relationships.stream()
                .filter(relationship -> !relationship.getReference().isEmpty() && !relationship.getPaletteName().isEmpty())
                .map(relationship -> new Pair<String, String>(relationship.getReference(), relationship.getPaletteName()))
                .collect(Collectors.toSet());
    }

    @EventListener(ContextRefreshedEvent.class)
    public void seed() throws Exception {
        if(productRepository.findAll().spliterator().getExactSizeIfKnown() != 0 ||
            paletteRepository.findAll().spliterator().getExactSizeIfKnown() != 0 ||
            packagingRepository.findAll().spliterator().getExactSizeIfKnown() != 0 ||
            productPackagingRepository.findAll().spliterator().getExactSizeIfKnown() != 0
        )
            return;

        List<Relationship> relationships = readImportFile(this.importFilePath);

        productRepository.saveAll(
            getUniqueReference(relationships).stream().map(reference -> {
                Product product = new Product();
                product.setReference(reference);
                return product;
            }).collect(Collectors.toSet())
        );

        paletteRepository.saveAll(
            getUniquePaletteName(relationships).stream().map(paletteName -> {
                Palette palette = new Palette();
                palette.setName(paletteName);
                return palette;
            }).collect(Collectors.toSet())
        );

        packagingRepository.saveAll(
            getUniquePackagingName(relationships).stream().map(packagingName -> {
                Packaging packaging = new Packaging();
                packaging.setName(packagingName);
                return packaging;
            }).collect(Collectors.toSet())
        );

        getProductPackagingRelationships(relationships).stream().forEach(relationship -> {
            String reference = relationship.getValue0();
            String packagingName = relationship.getValue1();

            Product product = productRepository.findByReferenceOrderByIdDesc(reference);
            Packaging packaging = packagingRepository.findByNameOrderByIdDesc(packagingName);

            ProductPackaging productPackaging = new ProductPackaging();
            productPackaging.setProduct(product);
            productPackaging.setPackaging(packaging);
            productPackagingRepository.save(productPackaging);

            product.getProductPackaging().add(productPackaging);
            productRepository.save(product);

            packaging.getProductPackaging().add(productPackaging);
            packagingRepository.save(packaging);
        });

        getProductPackagingRelationships(relationships).stream().forEach(relationship -> {
            String reference = relationship.getValue0();
            String paletteName = relationship.getValue1();

            Product product = productRepository.findByReferenceOrderByIdDesc(reference);
            Palette palette = paletteRepository.findByNameOrderByIdDesc(paletteName);

            product.getPalette().add(palette);
            productRepository.save(product);
        });
    }
}
