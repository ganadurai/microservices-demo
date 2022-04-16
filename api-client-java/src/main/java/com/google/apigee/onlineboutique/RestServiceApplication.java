package com.google.apigee.onlineboutique;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.apigee.onlineboutique.productcatalog.ProductCatalogController;

@SpringBootApplication
public class RestServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductCatalogController.class, args);
    }

}
