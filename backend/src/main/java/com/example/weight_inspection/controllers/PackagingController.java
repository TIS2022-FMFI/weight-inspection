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

import com.example.weight_inspection.models.Packaging;
import com.example.weight_inspection.transfer.ListResponse;
import com.example.weight_inspection.repositories.PackagingRepository;

@RestController
@RequestMapping(path = "api/packaging")
public class PackagingController {

	private final PackagingRepository packagingRepository;

	@Autowired
	public PackagingController(PackagingRepository packagingRepository) {
		this.packagingRepository = packagingRepository;
	}

	@GetMapping
	public ResponseEntity<ListResponse<Packaging>> GetPackagings(
			@RequestParam(value = "name", defaultValue = "") String name,
			@RequestParam(value = "page", defaultValue = "0") int currentPage,
			@RequestParam(value = "page_size", defaultValue = "100") int pageSize) {

		if (!name.isEmpty()) {
			Packaging packaging = packagingRepository.findByName(name);
			ListResponse<Packaging> listResponse = new ListResponse<>(packaging);
			return new ResponseEntity<>(listResponse, HttpStatus.OK);
		}

		Pageable pageable = PageRequest.of(currentPage, pageSize);
		Page<Packaging> page = packagingRepository.findAll(pageable);
		ListResponse<Packaging> listResponse = new ListResponse<>(page);
		return new ResponseEntity<>(listResponse, HttpStatus.OK);
	}

	@GetMapping("{packagingId}")
	public ResponseEntity<Packaging> getPackagingById(@PathVariable("packagingId") Long packagingId) {

		Optional<Packaging> packaging = packagingRepository.findById(packagingId);
		if (!packaging.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(packaging.get(), HttpStatus.OK);
	}

	@PostMapping("")
	public ResponseEntity<Packaging> savePackaging(@RequestBody @Valid Packaging packaging, BindingResult bindingResult) {

		if (bindingResult.hasErrors() || (packaging == null)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		packaging.setId(null);
		packagingRepository.save(packaging);
		return new ResponseEntity<>(packaging, HttpStatus.CREATED);
	}

	@PutMapping("{packagingId}")
	public ResponseEntity<Packaging> replacePackaging(@RequestBody @Valid Packaging packaging, BindingResult bindingResult,
			@PathVariable Long packagingId) {

		if (bindingResult.hasErrors() || packaging == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		Optional<Packaging> replacedPackaging = packagingRepository.findById(packagingId);
		if (!replacedPackaging.isPresent()) {
			packaging.setId(null);
			packagingRepository.save(packaging);
			return new ResponseEntity<>(packaging, HttpStatus.NO_CONTENT);
		}

		packaging.setId(packagingId);
		packagingRepository.save(packaging);
		return new ResponseEntity<>(packaging, HttpStatus.NO_CONTENT);
	}

	@DeleteMapping("{packagingId}")
	public ResponseEntity<Packaging> deletePackaging(@PathVariable Long packagingId) {

		Optional<Packaging> packaging = packagingRepository.findById(packagingId);
		if (!packaging.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		Packaging deletedPackaging = packaging.get();
		deletedPackaging.setId(packagingId);
		packagingRepository.delete(deletedPackaging);
		return new ResponseEntity<>(deletedPackaging, HttpStatus.NO_CONTENT);
	}
}
