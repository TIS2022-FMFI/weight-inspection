package com.example.weight_inspection.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.example.weight_inspection.models.Product;
import com.example.weight_inspection.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.weight_inspection.models.Palette;
import com.example.weight_inspection.transfer.ListResponse;
import com.example.weight_inspection.repositories.PaletteRepository;

@RestController
@RequestMapping(path = "api/palette")
public class PaletteController {

    private final PaletteRepository paletteRepository;
    private final ProductRepository productRepository;

    @Autowired
    public PaletteController(PaletteRepository paletteRepository, ProductRepository productRepository) {
        this.paletteRepository = paletteRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<ListResponse<Palette>> GetPalettes(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "page", defaultValue = "0") int currentPage,
            @RequestParam(value = "page_size", defaultValue = "100") int pageSize) {

        if (!name.isEmpty()) {
            Palette palette = paletteRepository.findByNameOrderByIdDesc(name);
            ListResponse<Palette> listResponse = new ListResponse<>(palette);
            return new ResponseEntity<>(listResponse, HttpStatus.OK);
        }

        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("id").descending());
        Page<Palette> page = paletteRepository.findAll(pageable);
        ListResponse<Palette> listResponse = new ListResponse<>(page);
        return new ResponseEntity<>(listResponse, HttpStatus.OK);
    }

    @GetMapping("{paletteId}")
    public ResponseEntity<Palette> getPaletteById(@PathVariable("paletteId") Long paletteId) {

        Optional<Palette> palette = paletteRepository.findById(paletteId);
        if (!palette.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(palette.get(), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<Palette> savePalette(@RequestBody @Valid Palette palette, BindingResult bindingResult) {

        if (bindingResult.hasErrors() || (palette == null)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        palette.setId(null);
        paletteRepository.save(palette);
        return new ResponseEntity<>(palette, HttpStatus.CREATED);
    }

    @PutMapping("{paletteId}")
    public ResponseEntity<Palette> replacePalette(@RequestBody @Valid Palette palette, BindingResult bindingResult,
                                                  @PathVariable Long paletteId) {

        if (bindingResult.hasErrors() || palette == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Palette> replacedPalette = paletteRepository.findById(paletteId);
        if (!replacedPalette.isPresent()) {
            palette.setId(null);
            paletteRepository.save(palette);
            return new ResponseEntity<>(palette, HttpStatus.NO_CONTENT);
        }

        palette.setId(paletteId);
        palette.setProduct(replacedPalette.get().getProduct());
        paletteRepository.save(palette);
        return new ResponseEntity<>(palette, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("{paletteId}")
    public ResponseEntity<Palette> deletePalette(@PathVariable Long paletteId) {

        Optional<Palette> palette = paletteRepository.findById(paletteId);
        if (!palette.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(0 < palette.get().getProduct().size()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Palette deletedPalette = palette.get();
        deletedPalette.setId(paletteId);
        paletteRepository.delete(deletedPalette);
        return new ResponseEntity<>(deletedPalette, HttpStatus.NO_CONTENT);
    }


    @DeleteMapping("{paletteId}/product/{productId}")
    public ResponseEntity<Palette> deleteProductFromPalette(@PathVariable Long paletteId, @PathVariable Long productId) {
        Optional<Palette> palette = paletteRepository.findById(paletteId);
        Optional<Product> product = productRepository.findById(productId);

        if (!palette.isPresent() || !product.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Palette delPalette = palette.get();
        delPalette.getProduct().remove(product.get());
        paletteRepository.save(delPalette);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("{paletteId}/product")
    public ResponseEntity<ListResponse<Product>> getProductsOfPalette(@PathVariable Long paletteId) {
        Optional<Palette> palette = paletteRepository.findById(paletteId);
        if (!palette.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ListResponse<Product> products = new ListResponse<>(palette.get().getProduct());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}
