package com.product.controller;

import java.util.Arrays;
import java.util.List;

import com.product.model.Product;

public class TestData {
	
	public static List<Product> getAllProducts(){
		return Arrays.asList(new Product(1L, "Jeans", "Clothes", 1100.00),
				new Product(2L, "Parle-G", "Food", 10.00), new Product(3L, "CK", "Perfume", 3000.00));
	}
	
	public static Product getProduct() {
		return new Product(1L, "Jeans", "Clothes", 1100.00);
	}
	
	public static Product getUpdatedProduct1() {
		return new Product(1L, "T-Shirt", "Clothes", 1100.00);
	}
	
	public static Product getProductWithNegativePrice() {
		return new Product(1L, "Jeans", "Clothes", -100.00);
	}

}
