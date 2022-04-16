package com.google.apigee.onlineboutique;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.apigee.onlineboutique.productcatalog.OnlineBoutiqueController;

@SpringBootApplication
public class RestServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnlineBoutiqueController.class, args);
    }

}
