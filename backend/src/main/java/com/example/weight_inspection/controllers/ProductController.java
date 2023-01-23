package com.example.weight_inspection.controllers;

import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.example.weight_inspection.models.Packaging;
import com.example.weight_inspection.models.ProductPackaging;
import com.example.weight_inspection.repositories.*;
import com.example.weight_inspection.services.ConfigurationService;
import com.example.weight_inspection.transfer.GetPackagingOfProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.weight_inspection.models.Palette;
import com.example.weight_inspection.models.Product;
import com.example.weight_inspection.transfer.ListResponse;

@RestController
@RequestMapping(path = "api/product")
public class ProductController {

	private final ProductRepository productRepository;
    private final PaletteRepository paletteRepository;
	private final ProductPackagingRepository productPackagingRepository;
	private final PackagingRepository packagingRepository;
	private final ConfigurationRepository configurationRepository;
	private final ConfigurationService configurationService;

	@Autowired
	public ProductController(ProductRepository productRepository, PaletteRepository paletteRepository, ProductPackagingRepository productPackagingRepository, PackagingRepository packagingRepository,
							 ConfigurationRepository configurationRepository, ConfigurationService configurationService) {
		this.productRepository = productRepository;
		this.paletteRepository = paletteRepository;
		this.productPackagingRepository = productPackagingRepository;
		this.packagingRepository = packagingRepository;
		this.configurationRepository = configurationRepository;
		this.configurationService = configurationService;
	}

	@GetMapping
	public ResponseEntity<ListResponse<Product>> GetProducts(
			@RequestParam(value = "reference", defaultValue = "") String reference,
			@RequestParam(value = "page", defaultValue = "0") int currentPage,
			@RequestParam(value = "page_size", defaultValue = "100") int pageSize) {

		if (!reference.isEmpty()) {
			Product product = productRepository.findByReferenceOrderByIdDesc(reference);
			ListResponse<Product> listResponse = new ListResponse<>(product);
			return new ResponseEntity<>(listResponse, HttpStatus.OK);
		}

		Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("id").descending());
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
		product.setProductPackaging(null);
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
			product.setProductPackaging(null);
			productRepository.save(product);
			return new ResponseEntity<>(product, HttpStatus.NO_CONTENT);
		}

		product.setId(productId);
		product.setPalette(replacedProduct.get().getPalette());
		product.setProductPackaging(replacedProduct.get().getProductPackaging());
		productRepository.save(product);
		return new ResponseEntity<>(product, HttpStatus.NO_CONTENT);
	}

	@DeleteMapping("{productId}")
	public ResponseEntity<Product> deleteProduct(@PathVariable Long productId) {

		Optional<Product> product = productRepository.findById(productId);
		if (!product.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		if(0 < product.get().getPalette().size() || 0 < product.get().getProductPackaging().size()) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}

		Product deletedProduct = product.get();
		deletedProduct.setId(productId);
		productRepository.delete(deletedProduct);
		return new ResponseEntity<>(deletedProduct, HttpStatus.NO_CONTENT);
	}

	@PostMapping("{productReference}/palette/{paletteId}")
	public ResponseEntity<Product> addPaletteToProduct(@PathVariable String productReference, @PathVariable Long paletteId) {

		Product product = productRepository.findByReferenceOrderByIdDesc(productReference);
		Optional<Palette> palette = paletteRepository.findById(paletteId);

		if (product == null || !palette.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		product.getPalette().add(palette.get());
		productRepository.save(product);

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
	
	@PostMapping("{productReference}/packaging/{packagingId}")
	public ResponseEntity<Product> addPackagingToProduct(@RequestBody @Valid ProductPackaging productPackaging,
														  BindingResult bindingResult,
														  @PathVariable String productReference,
														  @PathVariable Long packagingId) {

		if (bindingResult.hasErrors() || productPackaging == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		Product product = productRepository.findByReferenceOrderByIdDesc(productReference);
		Optional<Packaging> packaging = packagingRepository.findById(packagingId);

		if(product == null || !packaging.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		Packaging newPackaging = packaging.get();

		productPackaging.setId(null);
		productPackaging.setPackaging(newPackaging);
		productPackaging.setProduct(product);
		productPackagingRepository.save(productPackaging);

		product.getProductPackaging().add(productPackaging);
		productRepository.save(product);

		newPackaging.getProductPackaging().add(productPackaging);
		packagingRepository.save(newPackaging);

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PutMapping("{productReference}/packaging/{packagingId}")
	public ResponseEntity<Product> replacePackagingToProduct(@RequestBody @Valid ProductPackaging productPackaging,
														 BindingResult bindingResult,
														 @PathVariable String productReference,
														 @PathVariable Long packagingId) {

		if (bindingResult.hasErrors() || productPackaging == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		Product product = productRepository.findByReferenceOrderByIdDesc(productReference);
		Optional<Packaging> optionalPackaging = packagingRepository.findById(packagingId);

		if (product == null || !optionalPackaging.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		Packaging packaging = optionalPackaging.get();
		ProductPackaging replacedProductPackaging = productPackagingRepository.findByPackagingAndProduct(packaging, product);

		if(replacedProductPackaging == null) {
			productPackaging.setId(null);
			productPackaging.setPackaging(packaging);
			productPackaging.setProduct(product);
			productPackagingRepository.save(productPackaging);

			product.getProductPackaging().add(productPackaging);
			productRepository.save(product);

			packaging.getProductPackaging().add(productPackaging);
			packagingRepository.save(packaging);

			return new ResponseEntity<>(HttpStatus.CREATED);
		}

		replacedProductPackaging.setTolerance(productPackaging.getTolerance());
		replacedProductPackaging.setQuantity(productPackaging.getQuantity());
		productPackagingRepository.save(replacedProductPackaging);

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
	@GetMapping("{productId}/packaging")
	public ResponseEntity<ListResponse<GetPackagingOfProductDTO>> getPackagingsOfProduct(@PathVariable Long productId) {
		Optional<Product> product = productRepository.findById(productId);
		if (!product.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		Set<ProductPackaging> productPackagings = product.get().getProductPackaging();
		ListResponse<GetPackagingOfProductDTO> listResponse = new ListResponse<>();
		listResponse.setPage(0);
		listResponse.setTotalItems(productPackagings.size());
		listResponse.setTotalPages(1);
		listResponse.setItems(
				productPackagings.stream()
						.map(productPackaging -> {
							GetPackagingOfProductDTO getPackagingOfProductDTO = new GetPackagingOfProductDTO();
							getPackagingOfProductDTO.setId(productPackaging.getPackaging().getId());
							getPackagingOfProductDTO.setName(productPackaging.getPackaging().getName());
							getPackagingOfProductDTO.setType(productPackaging.getPackaging().getType());
							getPackagingOfProductDTO.setQuantity(productPackaging.getQuantity());
							if(productPackaging.getTolerance() != null) {
								getPackagingOfProductDTO.setTolerance(productPackaging.getTolerance());
							}
							else {
								getPackagingOfProductDTO.setTolerance(configurationRepository.findByName(configurationService.getDefaultToleranceName()).getValue());
							}
							return getPackagingOfProductDTO;
						})
						.collect(Collectors.toList())
		);
		return new ResponseEntity<>(listResponse, HttpStatus.OK);
	}

	@GetMapping("{productId}/palette")
	public ResponseEntity<ListResponse<Palette>> getPalettesOfProduct(@PathVariable Long productId) {
		Optional<Product> product = productRepository.findById(productId);
		if (!product.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		ListResponse<Palette> palletes = new ListResponse<>(product.get().getPalette());
		return new ResponseEntity<>(palletes, HttpStatus.OK);
	}
}
