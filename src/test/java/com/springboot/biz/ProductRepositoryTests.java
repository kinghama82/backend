package com.springboot.biz;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.biz.domain.Product;
import com.springboot.biz.repository.ProductRepository;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootTest
public class ProductRepositoryTests {
	
	@Autowired
	ProductRepository productRepository;
	
	@Transactional
	@Test
	public void testInsert() {
		
		/*
		 * for(int i = 0; i < 10 ; i++) { Product product = Product.builder()
		 * .pname("상품" + i) .price(100*i) .pdesc("상품설명 " + i) .build();
		 * product.addImageString(UUID.randomUUID().toString()+ "_" + "IMAGE1.jpg");
		 * product.addImageString(UUID.randomUUID().toString()+ "_" + "IMAGE2.jpg");
		 * 
		 * productRepository.save(product);
		 * 
		 * log.info("----------------------"); }
		 */
		
		Long pno = 1L;
		Optional<Product> result = productRepository.findById(pno);
		
		Product product = result.orElseThrow();
		
		log.info(product);
		log.info(product.getImageList());
		
	}

}
