package com.google.apigee.onlineboutique.recommendation;

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
public class RecommendationService {
    
    private static final Logger logger = Logger.getLogger(RecommendationService.class.getName());

    @Value("${gRPC.recommendationservice.server}")
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

    public String getRecommendations(String userId) throws Exception {
        ManagedChannel channel = getChannel();

        RecommendationServiceGrpc.RecommendationServiceBlockingStub blockingStub = 
            RecommendationServiceGrpc.newBlockingStub(channel);
        
        logger.info("Getting the recommendations...");

        ListRecommendationsRequest request = ListRecommendationsRequest.newBuilder().setUserId(userId).build();
        ListRecommendationsResponse listRecommendationsResponse;
        try {
            listRecommendationsResponse = blockingStub.listRecommendations(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            throw e;
        }

        List<String> productIds = new ArrayList<String>();
        if ((listRecommendationsResponse != null) && 
            (listRecommendationsResponse.getProductIdsList() != null)) {
            productIds = listRecommendationsResponse.getProductIdsList();
            logger.info("Product recommendations: " + 
                listRecommendationsResponse.getProductIdsList().size());
        }

        String recommendationIdsInJson = new Gson().toJson(productIds);

        return recommendationIdsInJson;
        
    }
    
}
