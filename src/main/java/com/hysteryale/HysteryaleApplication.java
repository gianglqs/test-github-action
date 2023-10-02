package com.hysteryale;

import com.hysteryale.rollbar.RollbarInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HysteryaleApplication extends RollbarInitializer {

	public static void main(String[] args) {
		rollbar.log("Hello, Rollbar");
		SpringApplication.run(HysteryaleApplication.class, args);
	}

}
