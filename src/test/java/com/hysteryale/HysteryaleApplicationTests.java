package com.hysteryale;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootTest
@EnableJpaRepositories(basePackages = "com.hysteryale.repository")
class HysteryaleApplicationTests {

	@Test
	void contextLoads() {
	}

}
