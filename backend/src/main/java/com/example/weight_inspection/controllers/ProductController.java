package com.example.weight_inspection.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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

import com.example.weight_inspection.models.Product;
import com.example.weight_inspection.payloads.ListResponse;
import com.example.weight_inspection.repositories.ProductRepository;

@RestController
@RequestMapping(path = "api/product")
public class ProductController {

	private final ProductRepository productRepository;

	@Autowired
	public ProductController(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@GetMapping
	public ResponseEntity<ListResponse<Product>> GetProducts(
			@RequestParam(value = "reference", defaultValue = "") String reference,
			@RequestParam(value = "page", defaultValue = "0") int currentPage,
			@RequestParam(value = "page_size", defaultValue = "100") int pageSize) {

		if (!reference.isEmpty()) {
			Product product = productRepository.findByReference(reference);
			List<Product> list = new ArrayList<Product>();
			if (product != null) {
				list.add(product);
			}

			ListResponse<Product> listResponse = new ListResponse<Product>();
			listResponse.setPage(0);
			listResponse.setItems(list);
			listResponse.setTotalItems(list.size());
			listResponse.setTotalPages(1);

			return new ResponseEntity<>(listResponse, HttpStatus.OK);
		}

		Pageable pageable = PageRequest.of(currentPage, pageSize);
		Page<Product> page = productRepository.findAll(pageable);

		ListResponse<Product> listResponse = new ListResponse<Product>();
		listResponse.setPage(currentPage);
		listResponse.setItems(page.getContent());
		listResponse.setTotalItems(page.getTotalElements());
		listResponse.setTotalPages(page.getTotalPages());

		return new ResponseEntity<>(listResponse, HttpStatus.OK);
	}

	@GetMapping("{productId}")
	public ResponseEntity<Product> getProductById(@PathVariable("productId") Long productId) {

		Optional<Product> product = productRepository.findById(productId);
		if (!product.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(product.get(), HttpStatus.OK);
	}

	@PostMapping("")
	public ResponseEntity<Product> saveProduct(@RequestBody @Valid Product product, BindingResult bindingResult) {

		if (bindingResult.hasErrors() || (product == null)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		product.setId(null);
		productRepository.save(product);
		return new ResponseEntity<>(product, HttpStatus.CREATED);
	}

	@PutMapping("{productId}")
	public ResponseEntity<Product> replaceProduct(@RequestBody @Valid Product product, BindingResult bindingResult,
			@PathVariable Long productId) {

		if (bindingResult.hasErrors() || product == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		Optional<Product> replacedProduct = productRepository.findById(productId);
		if (!replacedProduct.isPresent()) {
			product.setId(null);
			productRepository.save(product);
			return new ResponseEntity<>(product, HttpStatus.NO_CONTENT);
		}

		product.setId(productId);
		productRepository.save(product);
		return new ResponseEntity<>(product, HttpStatus.NO_CONTENT);
	}

	@DeleteMapping("{productId}")
	public ResponseEntity<Product> deleteProduct(@PathVariable Long productId) {

		Optional<Product> product = productRepository.findById(productId);
		if (!product.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		Product deletedProduct = product.get();
		deletedProduct.setId(productId);
		productRepository.delete(deletedProduct);
		return new ResponseEntity<>(deletedProduct, HttpStatus.NO_CONTENT);
	}
}
