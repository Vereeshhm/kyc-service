package com.saswat.kyc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(scanBasePackages = "com.saswat.kyc")
@PropertySource("file:config/application.properties")
@ComponentScan(basePackages= {"com.saswat"})

public class SaswatKYCServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaswatKYCServiceApplication.class, args);
	}

}
