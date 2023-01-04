package com.example.weight_inspection.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.validation.Valid;

import com.example.weight_inspection.models.Packaging;
import com.example.weight_inspection.models.ProductPackaging;
import com.example.weight_inspection.repositories.PackagingRepository;
import com.example.weight_inspection.repositories.ProductPackagingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.weight_inspection.models.Palette;
import com.example.weight_inspection.models.Product;
import com.example.weight_inspection.transfer.ListResponse;
import com.example.weight_inspection.repositories.PaletteRepository;
import com.example.weight_inspection.repositories.ProductRepository;

@RestController
@RequestMapping(path = "api/product")
public class ProductController {

	private final ProductRepository productRepository;
    private final PaletteRepository paletteRepository;
	private final ProductPackagingRepository productPackagingRepository;
	private final PackagingRepository packagingRepository;

	@Autowired
	public ProductController(ProductRepository productRepository, PaletteRepository paletteRepository, ProductPackagingRepository productPackagingRepository, PackagingRepository packagingRepository) {
		this.productRepository = productRepository;
		this.paletteRepository = paletteRepository;
		this.productPackagingRepository = productPackagingRepository;
		this.packagingRepository = packagingRepository;
	}

	@GetMapping
	public ResponseEntity<ListResponse<Product>> GetProducts(
			@RequestParam(value = "reference", defaultValue = "") String reference,
			@RequestParam(value = "page", defaultValue = "0") int currentPage,
			@RequestParam(value = "page_size", defaultValue = "100") int pageSize) {

		if (!reference.isEmpty()) {
			Product product = productRepository.findByReference(reference);
			ListResponse<Product> listResponse = new ListResponse<>(product);
			return new ResponseEntity<>(listResponse, HttpStatus.OK);
		}

		Pageable pageable = PageRequest.of(currentPage, pageSize);
		Page<Product> page = productRepository.findAll(pageable);
		ListResponse<Product> listResponse = new ListResponse<>(page);
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
		product.setPalette(null);
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
			product.setPalette(null);
			productRepository.save(product);
			return new ResponseEntity<>(product, HttpStatus.NO_CONTENT);
		}

		product.setId(productId);
		product.setPalette(replacedProduct.get().getPalette());
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

	@PostMapping("{productId}/palette/{paletteId}")
	public ResponseEntity<Product> addPaletteToProduct(@PathVariable Long productId, @PathVariable Long paletteId) {

		Optional<Product> product = productRepository.findById(productId);
		Optional<Palette> palette = paletteRepository.findById(paletteId);

		if (!product.isPresent() || !palette.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		Product newProduct = product.get();
		newProduct.getPalette().add(palette.get());
		productRepository.save(newProduct);

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@DeleteMapping("{productId}/palette/{paletteId}")
	public ResponseEntity<Product> deletePaletteFromProduct(@PathVariable Long productId, @PathVariable Long paletteId) {

		Optional<Product> product = productRepository.findById(productId);
		Optional<Palette> palette = paletteRepository.findById(paletteId);

		if (!product.isPresent() || !palette.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		Product delProduct = product.get();
		delProduct.getPalette().remove(palette.get());
		productRepository.save(delProduct);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping("{productId}/packaging/{packagingId}")
	public ResponseEntity<Product> addPackagingToProduct(@RequestBody @Valid ProductPackaging productPackaging,
														  BindingResult bindingResult,
														  @PathVariable Long productId,
														  @PathVariable Long packagingId) {

		if (bindingResult.hasErrors() || productPackaging == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		Optional<Product> product = productRepository.findById(productId);
		Optional<Packaging> packaging = packagingRepository.findById(packagingId);

		if(!product.isPresent() || !packaging.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		Product newProduct = product.get();
		Packaging newPackaging = packaging.get();

		productPackaging.setPackaging(newPackaging);
		productPackaging.setProduct(newProduct);
		productPackagingRepository.save(productPackaging);

		newProduct.getProductPackaging().add(productPackaging);
		productRepository.save(newProduct);

		newPackaging.getProductPackaging().add(productPackaging);
		packagingRepository.save(newPackaging);

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@DeleteMapping("{productId}/packaging/{packagingId}")
	public ResponseEntity<Product> deletePackagingFromProduct(@PathVariable Long productId, @PathVariable Long packagingId) {

		Optional<Product> product = productRepository.findById(productId);
		if (!product.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		Optional<ProductPackaging> productPackaging =  product.get().getProductPackaging()
				.stream()
				.filter(it -> Objects.equals(it.getPackaging().getId(), packagingId))
				.findFirst();

		if(!productPackaging.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		Product delProduct = product.get();
		delProduct.getProductPackaging().remove(productPackaging.get());
		productPackaging.get().getPackaging().getProductPackaging().remove(productPackaging.get());
		productRepository.save(delProduct);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
