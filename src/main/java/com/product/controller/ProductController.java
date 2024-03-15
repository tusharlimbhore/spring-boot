package com.product.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.product.exception.NegativePriceException;
import com.product.exception.ProductNotFoundException;
import com.product.model.Product;
import com.product.repo.ProductRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Product API", description = "Product CRUD operation")
@RestController
@RequestMapping("/api/product")
public class ProductController {

	private ProductRepository productRepository;

	public ProductController(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Operation(summary = "Get all products", tags = { "product", "get" })
	@ApiResponse(responseCode = "200", content = {
			@Content(schema = @Schema(implementation = Product.class), mediaType = "application/json") })
	@ApiResponse(responseCode = "204", description = "There are no products", content = @Content(schema = @Schema()))

	@ApiResponse(responseCode = "500", content = @Content(schema = @Schema()))
	@GetMapping
	public ResponseEntity<java.util.List<Product>> getAllProduct() {
		return ResponseEntity.ok(productRepository.findAll());
	}

	@Operation(summary = "Find product by ID", tags = { "product", "get" })
	@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Product.class), mediaType = "application/json"))
	@ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) })
	@ApiResponse(responseCode = "500", content = @Content(schema = @Schema()))
	@GetMapping("/{id}")
	public ResponseEntity<Product> getProductByID(@PathVariable Long id) {
		Optional<Product> optionalProduct = productRepository.findById(id);
		if (optionalProduct.isPresent()) {
			productRepository.findById(id);
			return ResponseEntity.status(HttpStatus.OK).body(optionalProduct.get());
		} else {
			throw new ProductNotFoundException("Product with ID " + id + " not found");
		}
	}

	@Operation(summary = "Create new product", tags = { "product", "post" })
	@ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = Product.class), mediaType = "application/json"))
	@ApiResponse(responseCode = "500", content = @Content(schema = @Schema()))
	@PostMapping
	public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
		if (product.getPrice() > 0) {
			return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(product));
		} else {
			throw new NegativePriceException("Price must be greater than 0");
		}

	}

	@Operation(description = "Update a product by ID", tags = { "product", "put" })
	@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Product.class), mediaType = "application/json"))
	@ApiResponse(responseCode = "500", content = @Content(schema = @Schema()))
	@ApiResponse(responseCode = "404", content = @Content(schema = @Schema()))

	@PutMapping("/{id}")
	public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
		Optional<Product> optionalProduct = productRepository.findById(id);
		if (optionalProduct.isPresent()) {
			if (product.getPrice() > 0) {
				Product existingProduct = optionalProduct.get();
				existingProduct.setName(product.getName());
				existingProduct.setCategory(product.getCategory());
				existingProduct.setPrice(product.getPrice());
				return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(existingProduct));
			} else {
				throw new NegativePriceException("Price must be greater than 0");
			}
		} else {
			throw new ProductNotFoundException("Product with ID " + id + " not found");
		}

	}

	@Operation(summary = "Delete product by ID", tags = { "product", "delete" })
	@ApiResponse(responseCode = "204", content = @Content(schema = @Schema()))
	@ApiResponse(responseCode = "500", content = @Content(schema = @Schema))
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
		Optional<Product> optionalProduct = productRepository.findById(id);
		if (optionalProduct.isPresent()) {
			productRepository.deleteById(id);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} else {
			throw new ProductNotFoundException("Product with ID " + id + " not found");
		}
	}
}
