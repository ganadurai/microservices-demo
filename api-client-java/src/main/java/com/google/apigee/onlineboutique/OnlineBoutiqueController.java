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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.apigee.onlineboutique.productcatalog.ProductCatalogService;
import com.google.apigee.onlineboutique.currency.CurrencyService;
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
        value="/products", 
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

    @RequestMapping(
        value="/product/{id}", 
        method = RequestMethod.GET, 
        produces = "application/json"
    )
    @ResponseBody
    public String getProductById(@PathVariable("id") String productId) {
        
        try {
            return productCatalogService.getProductById(productId);
        } catch (Exception e) {
            System.out.println("##########################################################################");
            System.out.println("ERRO: " + e.getMessage());
            return "{'error':, '" + e.getMessage() + "'}";
        }
    }

    @RequestMapping(
        value="/product/search", 
        method = RequestMethod.GET, 
        produces = "application/json"
    )
    @ResponseBody
    public String searchProduct(@RequestParam("query") String query) {
        
        try {
            return productCatalogService.searchProduct(query);
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