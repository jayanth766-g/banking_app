package com.demo.thebankingproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "The Banking Project",
				description = "Backend REST APIs for the banking project",
				version = "v1.0",
				contact = @Contact(
						name = "G Jayanth"
						),
				license = @License(
						name="The Banking Project"
						)
				),
		externalDocs = @ExternalDocumentation(
				description = "The Banking Project Documentation"
				)
		)
public class TheBankingProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(TheBankingProjectApplication.class, args);
	}

}
