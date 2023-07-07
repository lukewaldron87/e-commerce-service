package com.waldron.ecommerceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ECommerceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ECommerceServiceApplication.class, args);
	}

	//todo Fix this as it currently breaks the controller and repository tests.
	/*@Bean
	public CommandLineRunner loadDummyData(ProductRepository repository) {

		return (args) -> {
			repository.saveAll(Arrays.asList(
							Product.builder().name("Video Game").price(BigDecimal.valueOf(45.50)).build(),
							Product.builder().name("Mug").price(BigDecimal.valueOf(14.99)).build()))
					.blockLast(Duration.ofSeconds(10));


		};
	}*/
}
