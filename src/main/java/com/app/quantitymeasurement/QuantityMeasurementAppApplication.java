package com.app.quantitymeasurement;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Quantity Measurement API",
                version = "1.0",
                description = "REST API for Quantity Measurement operations — compare, convert, add, subtract, divide.",
                contact = @Contact(name = "Ankit Chaudhary")
        )
)
public class QuantityMeasurementAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuantityMeasurementAppApplication.class, args);
    }
}
