package com.example.weight_inspection.controllers;

import com.example.weight_inspection.models.*;
import com.example.weight_inspection.repositories.*;
import com.example.weight_inspection.services.WeighingService;
import com.example.weight_inspection.transfer.AddWeighingDTO;
import com.example.weight_inspection.transfer.ListResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping("api/weighing")
public class WeighingController {
    private final WeighingRepository weighingRepository;
    private final WeighingService weighingService;
    private final ProductPackagingRepository productPackagingRepository;
    private final PackagingRepository packagingRepository;
    private final ProductRepository productRepository;
    private final PaletteRepository paletteRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public WeighingController(WeighingRepository weighingRepository,
                              WeighingService weighingService,
                              ProductPackagingRepository productPackagingRepository,
                              PackagingRepository packagingRepository,
                              ProductRepository productRepository,
                              PaletteRepository paletteRepository) {
        this.weighingRepository = weighingRepository;
        this.weighingService = weighingService;
        this.productPackagingRepository = productPackagingRepository;
        this.packagingRepository = packagingRepository;
        this.productRepository = productRepository;
        this.paletteRepository = paletteRepository;
        this.modelMapper = new ModelMapper();

    }

    @GetMapping
    public ResponseEntity<ListResponse<Weighing>> getWeighings(
            @RequestParam(value = "page", defaultValue = "0") int currentPage,
            @RequestParam(value = "page_size", defaultValue = "100") int pageSize) {
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("id").descending());
        Page<Weighing> page = weighingRepository.findAll(pageable);
        ListResponse<Weighing> listResponse = new ListResponse<>(page);
        return new ResponseEntity<>(listResponse, HttpStatus.OK);

    }

    @PostMapping("")
    public ResponseEntity<Weighing> addWeighing(@RequestBody @Valid AddWeighingDTO weighingDTO,
                                                BindingResult bindingResult) {

        if (bindingResult.hasErrors() || weighingDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Weighing weighing = modelMapper.map(weighingDTO, Weighing.class);

        Product product = productRepository.findByReferenceOrderByIdDesc(weighingDTO.getReference());
        Palette palette = null;
        Packaging packaging = null;
        ProductPackaging productPackaging = null;

        if(weighingDTO.getPaletteId() != null && weighingDTO.getPackagingId() != null) {
            palette = paletteRepository.findById((long) weighingDTO.getPaletteId());
            packaging = packagingRepository.findById((long) weighingDTO.getPackagingId());
            productPackaging = productPackagingRepository.findByPackagingAndProduct(packaging, product);
        }

        if (product == null || palette == null || packaging == null || productPackaging == null ||
                palette.getWeight() == null || packaging.getWeight() == null) {
            // TODO send email and add notification
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Float paletteWeight = palette.getWeight();
        Float packagingWeight = packaging.getWeight();
        Float productWeight = product.getWeight();
        int numberOfProductsInPackaging = productPackaging.getQuantity();
        float productPackagingTolerance = productPackaging.getTolerance();
        int totalNumberOfProducts = weighing.getQuantity();

        if (productWeight == null) {
            productWeight = weighingService.calculateWeightOfOneProduct(weighingDTO.getWeight(), totalNumberOfProducts, numberOfProductsInPackaging, packagingWeight, paletteWeight);
            if(productWeight < 0) {
                // TODO send email negative weight
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            product.setWeight(productWeight);
            productRepository.save(product);
        }

        float calculatedWeight = weighingService.calculateExpectedWeight(totalNumberOfProducts, product.getWeight(), packagingWeight, paletteWeight);
        float differenceInWeight = Math.abs(weighingDTO.getWeight() - calculatedWeight);
        boolean correctWeighing = differenceInWeight < productWeight * productPackagingTolerance;

        if (!correctWeighing) {
            // TODO send email and add notification
        }

        weighing.setCalculatedWeight(calculatedWeight);
        weighing.setWeighedOn(new Timestamp(System.currentTimeMillis()));
        weighing.setCorrect(correctWeighing);
        weighing.setExported(false);

        weighing.setPalette(palette);
        weighing.setProduct(product);
        weighing.setPackaging(productPackaging.getPackaging());
        weighingRepository.save(weighing);

        return new ResponseEntity<>(weighing, HttpStatus.CREATED);
    }
}
