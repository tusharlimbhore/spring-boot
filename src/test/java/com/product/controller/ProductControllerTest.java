package com.product.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.product.exception.NegativePriceException;
import com.product.exception.ProductNotFoundException;
import com.product.model.Product;
import com.product.repo.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {
	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private ProductController productController;

	@Test
	void testGetAllProducts() {
		List<Product> products = TestData.getAllProducts();
		when(productRepository.findAll()).thenReturn(products);
		ResponseEntity<List<Product>> responseEntity = productController.getAllProduct();
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		List<Product> list = responseEntity.getBody();
		assertNotNull(list);
		assertEquals(3, list.size());
	}

	@Test
	void testGetProductByID() {
		Optional<Product> product = Optional.of(TestData.getProduct());
		when(productRepository.findById(Mockito.anyLong())).thenReturn(product);
		ResponseEntity<Product> responseEntity = productController.getProductByID(1L);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertNotNull(responseEntity.getBody());
		assertEquals("Clothes", responseEntity.getBody().getCategory());
		assertNotNull(responseEntity.getBody().getId());
	}

	@Test
	void testProductNotFound() {
		ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,
				() -> productController.getProductByID(10L));
		assertTrue(exception.getMessage().contains("not found"));
	}

	@Test
	void testSaveProduct() {
		when(productRepository.save(Mockito.any(Product.class))).thenReturn(TestData.getProduct());
		ResponseEntity<Product> responseEntity = productController.saveProduct(TestData.getProduct());
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
		assertNotNull(responseEntity.getBody());
		assertEquals("Clothes", responseEntity.getBody().getCategory());
	}

	@Test
	void testNegativePriceException() {
		NegativePriceException negativePriceException = assertThrows(NegativePriceException.class,
				() -> productController.saveProduct(TestData.getProductWithNegativePrice()));
		assertTrue(negativePriceException.getMessage().contains("Price must be greater than 0"));
	}

	@Test
	void testUpdateProduct() {
		when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(TestData.getProduct()));
		Product updatedProduct = TestData.getProduct();
		updatedProduct.setId(9L);
		updatedProduct.setName("T-Shirt");
		when(productRepository.save(Mockito.any(Product.class))).thenReturn(updatedProduct);
		ResponseEntity<Product> responseEntity = productController.updateProduct(1L, updatedProduct);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertNotNull(responseEntity.getBody());
		assertEquals("T-Shirt", responseEntity.getBody().getName());
	}

	@Test
	void testNegativePriceExceptionInUpdate() {
		when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(TestData.getProduct()));
		NegativePriceException negativePriceException = assertThrows(NegativePriceException.class,
				() -> productController.updateProduct(1L, TestData.getProductWithNegativePrice()));
		assertTrue(negativePriceException.getMessage().contains("Price must be greater than 0"));
	}

	@Test
	void testProductNotFoundInUpdate() {
		ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,
				() -> productController.updateProduct(10L, TestData.getProduct()));
		assertTrue(exception.getMessage().contains("not found"));
	}

	@Test
	void testDeleteProduct() {
		when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(TestData.getProduct()));
		ResponseEntity<Void> responseEntity = productController.deleteProduct(1L);
		assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
	}
	
	@Test
	void testDeleteProductNotFound() {
		ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,
				() -> productController.deleteProduct(10L));
		assertTrue(exception.getMessage().contains("not found"));
	}
}
