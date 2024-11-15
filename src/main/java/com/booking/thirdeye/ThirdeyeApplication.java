package com.booking.thirdeye;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ThirdeyeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThirdeyeApplication.class, args);
	}

}
