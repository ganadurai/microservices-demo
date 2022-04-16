package com.google.apigee.onlineboutique;

import java.util.List;
import java.util.Collections;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.apigee.onlineboutique.*;

@RestController
@SpringBootApplication
@RequestMapping("/onlineboutique")
public class OnlineBoutiqueController {
    
    @Autowired
    private ProductCatalogService productCatalogService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    public OnlineBoutiqueController(
        ProductCatalogService productCatalogService,
        CurrencyService currencyService) {
        this.productCatalogService = productCatalogService;
        this.currencyService = currencyService;
    }

    @RequestMapping(
        value="/currencies/list", 
        method = RequestMethod.GET, 
        produces = "application/json"
    )
    @ResponseBody
    public String getCurrenciesList() {

        try {
            return currencyService.getCurrencyCodes();
        } catch (Exception e) {
            System.out.println("##########################################################################");
            System.out.println("ERRO: " + e.getMessage());
            return "{'error':, '" + e.getMessage() + "'}";
        }
    }

    @RequestMapping(
        value="/products/list", 
        method = RequestMethod.GET, 
        produces = "application/json"
    )
    @ResponseBody
    public String getProductsList() {
        
        try {
            return productCatalogService.getProductsList();
        } catch (Exception e) {
            System.out.println("##########################################################################");
            System.out.println("ERRO: " + e.getMessage());
            return "{'error':, '" + e.getMessage() + "'}";
        }
    }

    @GetMapping("/")
    public String test() {
        return "Product Catalog : Test is valid";
    }

}