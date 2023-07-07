package com.waldron.ecommerceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ECommerceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ECommerceServiceApplication.class, args);
	}

	/*@Bean
	public CommandLineRunner loadDummyData(ProductRepository repository) {

		return (args) -> {
			repository.saveAll(Arrays.asList(
							Product.builder().name("Book 1").price(BigDecimal.valueOf(19.99)).build(),
							Product.builder().name("Book 2").price(BigDecimal.valueOf(29.99)).build()))
					.blockLast(Duration.ofSeconds(10));


		};
	}*/
}
