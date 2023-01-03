package com.example.weight_inspection.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    public PaletteController(PaletteRepository paletteRepository) {
        this.paletteRepository = paletteRepository;
    }

    @GetMapping
    public ResponseEntity<ListResponse<Palette>> GetPalettes(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "page", defaultValue = "0") int currentPage,
            @RequestParam(value = "page_size", defaultValue = "100") int pageSize) {

        if (!name.isEmpty()) {
            Palette palette = paletteRepository.findByName(name);
            List<Palette> list = new ArrayList<Palette>();
            if (palette != null) {
                list.add(palette);
            }

            ListResponse<Palette> listResponse = new ListResponse<Palette>();
            listResponse.setPage(0);
            listResponse.setItems(list);
            listResponse.setTotalItems(list.size());
            listResponse.setTotalPages(1);

            return new ResponseEntity<>(listResponse, HttpStatus.OK);
        }

        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<Palette> page = paletteRepository.findAll(pageable);

        ListResponse<Palette> listResponse = new ListResponse<Palette>();
        listResponse.setPage(currentPage);
        listResponse.setItems(page.getContent());
        listResponse.setTotalItems(page.getTotalElements());
        listResponse.setTotalPages(page.getTotalPages());

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
        paletteRepository.save(palette);
        return new ResponseEntity<>(palette, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("{paletteId}")
    public ResponseEntity<Palette> deletePalette(@PathVariable Long paletteId) {

        Optional<Palette> palette = paletteRepository.findById(paletteId);
        if (!palette.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Palette deletedPalette = palette.get();
        deletedPalette.setId(paletteId);
        paletteRepository.delete(deletedPalette);
        return new ResponseEntity<>(deletedPalette, HttpStatus.NO_CONTENT);
    }
}
