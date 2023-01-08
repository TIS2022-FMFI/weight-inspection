package com.example.weight_inspection.controllers;

import com.example.weight_inspection.models.*;
import com.example.weight_inspection.repositories.*;
import com.example.weight_inspection.services.WeighingService;
import com.example.weight_inspection.transfer.ListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    }

    @GetMapping
    public ResponseEntity<ListResponse<Weighing>> getWeighings(
            @RequestParam(value = "page", defaultValue = "0") int currentPage,
            @RequestParam(value = "page_size", defaultValue = "100") int pageSize) {
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<Weighing> page = weighingRepository.findAll(pageable);
        ListResponse<Weighing> listResponse = new ListResponse<>(page);
        return new ResponseEntity<>(listResponse, HttpStatus.OK);

    }

    @PostMapping("")
    public ResponseEntity<Weighing> addWeighing(@RequestBody @Valid Weighing weighing, BindingResult bindingResult) {
        if (bindingResult.hasErrors() || weighing == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Product product = weighing.getProduct();
        Palette palette = weighing.getPalette();
        Packaging packaging = weighing.getPackaging();
        float paletteWeight = palette.getWeight();
        float packagingWeight = packaging.getWeight();
        Long paletteId = palette.getId();
        Long packagingId = packaging.getId();
        Product productDB = productRepository.findByReferenceOrderByIdDesc(product.getReference());
        if (productDB == null) {
            Optional<Packaging> packagingDB = packagingRepository.findById(packagingId);
            Optional<Palette> paletteDB = paletteRepository.findById(paletteId);
            if (!packagingDB.isPresent() || !paletteDB.isPresent()) {
                // TODO send email and add notification
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }


            ProductPackaging productPackaging = productPackagingRepository.findByPackagingAndProduct(packagingDB.get()
                    , product);
            float weightOfOne = weighingService.calculateWeightOfOneProduct(weighing.getWeight(), weighing.getQuantity(),
                    productPackaging.getQuantity(), packagingWeight, paletteWeight);
            product.setWeigth(weightOfOne);
            productRepository.save(product);
            float expectedWeight = weighingService.calculateExpectedWeight(weighing.getQuantity(), weightOfOne,
                    packagingWeight, paletteWeight);
            if (expectedWeight > weighing.getCalculated_weight() + productPackaging.getTolerance()
                    || expectedWeight < weighing.getCalculated_weight() - productPackaging.getTolerance()) {
                // TODO send email and add notification
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }


        }
        return new ResponseEntity<>(weighing, HttpStatus.CREATED);


    }
}
