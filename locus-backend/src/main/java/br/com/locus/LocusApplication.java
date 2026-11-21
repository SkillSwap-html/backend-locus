package br.com.locus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LocusApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocusApplication.class, args);
	}

}
