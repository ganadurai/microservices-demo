package com.google.apigee.onlineboutique.productcatalog;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import com.google.gson.Gson;
import com.google.apigee.onlineboutique.*;

@Service
public class ProductCatalogService {
    
    private static final Logger logger = Logger.getLogger(ProductCatalogService.class.getName());

    @Value("${gRPC.productcatalogservice.server}")
    private String gRPCServer;

    @Value("${gRPC.productcatalogservice.port}")
    private String gRPCServerPort;

    @Value("${gRPC.useTLS}")
    private boolean gRPCServerUseTLS;

    private ManagedChannel getChannel() {
        ManagedChannel channel;
        if (gRPCServerUseTLS) {
            channel = ManagedChannelBuilder.forAddress(gRPCServer, Integer.parseInt(gRPCServerPort))
            .build();
        } else {
            channel = ManagedChannelBuilder.forAddress(gRPCServer, Integer.parseInt(gRPCServerPort))
                .usePlaintext()
                .build();
        }
        return channel;
    }

    public String getProductsList() throws Exception {
        ManagedChannel channel = getChannel();

        ProductCatalogServiceGrpc.ProductCatalogServiceBlockingStub blockingStub = 
            ProductCatalogServiceGrpc.newBlockingStub(channel);
        
        logger.info("Getting the product list...");
        ListProductsResponse response;
        try {
            response = blockingStub.listProducts(Empty.getDefaultInstance());
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            throw e;
        }
        List<Product> productList = new ArrayList<Product>();
        if ((response != null) && (response.getProductsList() != null)) {
            productList = response.getProductsList();
            logger.info("Product listing: " + response.getProductsList().size());
        }

        String productListInJson = new Gson().toJson(productList);

        return productListInJson;
    }

    public String getProductById(String id) throws Exception {
        ManagedChannel channel = getChannel();

        ProductCatalogServiceGrpc.ProductCatalogServiceBlockingStub blockingStub = 
            ProductCatalogServiceGrpc.newBlockingStub(channel);
        
        logger.info("Getting the product details...");

        GetProductRequest request = GetProductRequest.newBuilder().setId(id).build();
        Product productResponse;
        try {
            productResponse = blockingStub.getProduct(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            throw e;
        }

        String productDetailsInJson = new Gson().toJson(productResponse);

        return productDetailsInJson;
    }

    public String searchProduct(String query) {
        ManagedChannel channel = getChannel();

        ProductCatalogServiceGrpc.ProductCatalogServiceBlockingStub blockingStub = 
            ProductCatalogServiceGrpc.newBlockingStub(channel);
        
        logger.info("search product...");

        SearchProductsRequest request 
            = SearchProductsRequest.newBuilder().setQuery(query).build();
        SearchProductsResponse response;
        try {
            response = blockingStub.searchProducts(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            throw e;
        }
        List<Product> productList = new ArrayList<Product>();
        if ((response != null) && (response.getResultsList() != null)) {
            productList = response.getResultsList();
            logger.info("Product listing: " + response.getResultsList().size());
        }

        String productListInJson = new Gson().toJson(productList);

        return productListInJson;


    }



}
