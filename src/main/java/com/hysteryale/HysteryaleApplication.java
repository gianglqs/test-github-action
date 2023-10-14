package com.hysteryale;

import com.hysteryale.rollbar.RollbarInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HysteryaleApplication extends RollbarInitializer {

	public static void main(String[] args) {

		SpringApplication.run(HysteryaleApplication.class, args);
		rollbar.log("Hello, Rollbar has been installed on Server QA");
	//	System.out.println(rollbar);
	}

}
