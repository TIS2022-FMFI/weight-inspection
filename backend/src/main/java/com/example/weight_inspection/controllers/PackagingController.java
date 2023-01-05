package com.example.weight_inspection.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.validation.Valid;

import com.example.weight_inspection.models.Product;
import com.example.weight_inspection.models.ProductPackaging;
import com.example.weight_inspection.repositories.ProductPackagingRepository;
import com.example.weight_inspection.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
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

	private final ProductRepository productRepository;
	private final ProductPackagingRepository productPackagingRepository;

	@Autowired
	public PackagingController(PackagingRepository packagingRepository, ProductRepository productRepository, ProductPackagingRepository productPackagingRepository) {
		this.packagingRepository = packagingRepository;
		this.productRepository = productRepository;
		this.productPackagingRepository = productPackagingRepository;
	}


	@GetMapping
	public ResponseEntity<ListResponse<Packaging>> GetPackagings(
			@RequestParam(value = "name", defaultValue = "") String name,
			@RequestParam(value = "page", defaultValue = "0") int currentPage,
			@RequestParam(value = "page_size", defaultValue = "100") int pageSize) {

		if (!name.isEmpty()) {
			Packaging packaging = packagingRepository.findByNameOrderByIdDesc(name);
			ListResponse<Packaging> listResponse = new ListResponse<>(packaging);
			return new ResponseEntity<>(listResponse, HttpStatus.OK);
		}

		Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("id").descending());
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

	//TODO: treba vytvorit GET response pre /{packagingId}/product, aby vratilo list response produktov

	@PostMapping("{packagingId}/product/{productId}")
	public  ResponseEntity<Packaging> addProductToPackaging (@RequestBody @Valid ProductPackaging productPackaging,
															 BindingResult bindingResult,
															 @PathVariable Long packagingId,
															 @PathVariable Long productId) {
		if (bindingResult.hasErrors() || productPackaging == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		Optional<Packaging> packaging = packagingRepository.findById(packagingId);
		Optional<Product> product = productRepository.findById(productId);

		if (!packaging.isPresent() || !product.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		Packaging newPackaging = packaging.get();
		Product newProduct = product.get();

		productPackaging.setPackaging(newPackaging);
		productPackaging.setProduct(newProduct);
		productPackagingRepository.save(productPackaging
		);

		newPackaging.getProductPackaging().add(productPackaging);
		packagingRepository.save(newPackaging);

		newProduct.getProductPackaging().add(productPackaging);
		productRepository.save(newProduct);

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@DeleteMapping("{packagingId}/product/{productId}")
	public ResponseEntity<Packaging> deleteProductFromPackaging(@PathVariable Long packagingId,
																@PathVariable Long productId) {
		Optional<Packaging> packaging = packagingRepository.findById(packagingId);
		Optional<Product> product = productRepository.findById(productId);
		if (!packaging.isPresent() || !product.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		Optional<ProductPackaging> productPackaging = packaging.get().getProductPackaging()
				.stream()
				.filter(it -> Objects.equals(it.getId(), packagingId))
				.findFirst();

		if (!productPackaging.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		}

		Packaging delPackaging = packaging.get();
		ProductPackaging newProductPackaging =  productPackaging.get();
		delPackaging.getProductPackaging().remove(newProductPackaging);
		newProductPackaging.getPackaging().getProductPackaging().remove(newProductPackaging);
		packagingRepository.save(delPackaging);
		 return new ResponseEntity<>(HttpStatus.NO_CONTENT);

	}

}
