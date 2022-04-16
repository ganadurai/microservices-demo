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

import com.google.apigee.onlineboutique.*;

@Service
public class ProductCatalogService {
    
    private static final Logger logger = Logger.getLogger(ProductCatalogService.class.getName());

    @Value("${gRPC.server}")
    private String gRPCServer;

    @Value("${gRPC.port}")
    private String gRPCServerPort;

    @Value("${gRPC.useTLS}")
    private boolean gRPCServerUseTLS;

    public String getProductsList() throws Exception {
        ManagedChannel channel;
        if (gRPCServerUseTLS) {
            channel = ManagedChannelBuilder.forAddress(gRPCServer, Integer.parseInt(gRPCServerPort))
            .build();
        } else {
            channel = ManagedChannelBuilder.forAddress(gRPCServer, Integer.parseInt(gRPCServerPort))
                .usePlaintext()
                .build();
        }

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
        if (response.getProductsList() != null) {
            productList = response.getProductsList();
            logger.info("Product listing: " + response.getProductsList().size());
        }

        String productListInJson = new Gson().toJson(productList);

        return productListInJson;
    }

}
