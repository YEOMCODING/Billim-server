package com.web.billim.product.domain.service;

import com.web.billim.common.exception.NotFoundException;
import org.springframework.stereotype.Service;

import com.web.billim.common.exception.handler.ErrorCode;
import com.web.billim.product.domain.Product;
import com.web.billim.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductDomainService {

	private final ProductRepository productRepository;

	public Product find(long productId) {
		return productRepository.findById(productId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
	}

}
