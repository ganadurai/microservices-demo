package com.google.apigee.onlineboutique.shipping;

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
public class ShippingService {
    
    private static final Logger logger = Logger.getLogger(ShippingService.class.getName());

    @Value("${gRPC.server}")
    private String gRPCServer;

    @Value("${gRPC.port}")
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

    public String getQuote(QuoteRequestDetails quoteReqDetails) throws Exception {
        
        ManagedChannel channel = getChannel();

        ShippingServiceGrpc.ShippingServiceBlockingStub blockingStub = 
            ShippingServiceGrpc.newBlockingStub(channel);
        
        logger.info("Getting the quote...");

        com.google.apigee.onlineboutique.Address addressReq = 
            com.google.apigee.onlineboutique.Address.newBuilder()
                .setStreetAddress(quoteReqDetails.getAddress().getStreetAddress())
                .setCity(quoteReqDetails.getAddress().getCity())
                .setState(quoteReqDetails.getAddress().getState())
                .setCountry(quoteReqDetails.getAddress().getCountry()).build();

        GetQuoteRequest.Builder requestBuilder = GetQuoteRequest.newBuilder()
                                    .setAddress(addressReq);
        
        for (int i=0; i < quoteReqDetails.getItems().length; i++) {
            requestBuilder.setItems(i,
                CartItem.newBuilder()
                    .setProductId(quoteReqDetails.getItems()[i].getProductId())
                    .setQuantity(quoteReqDetails.getItems()[i].getQuantity())
                    .build());
        }
        
        GetQuoteRequest request = requestBuilder.build();
        GetQuoteResponse quoteResponse;
        try {
            quoteResponse = blockingStub.getQuote(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            throw e;
        }

        String quoteDetailsInJson = new Gson().toJson(quoteResponse.getCostUsd());

        return quoteDetailsInJson;
        
    }



    
}
