package com.ezpay.main.config;

import com.ezpay.core.gateway.Payment;
import com.ezpay.core.gateway.QRCode;
import com.ezpay.core.gateway.impl.*;
import com.ezpay.main.process.Facade.ProcessFacade;
import com.google.gson.Gson;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * @author OI
 */
@Configuration
public class BeanConfig {

    @Bean(name = "onePay")
    public Payment onePay() {
        return new OnePayImpl();
    }

    @Bean(name = "megaPay")
    public Payment mePay() {
        return new MegaPayImpl();
    }

    @Bean(name = "vnPay")
    public Payment vnPay() {
        return new VNPayImpl();
    }

    @Bean(name = "viettelPay")
    public Payment viettelPay() {
        return new ViettelPayImpl();
    }

    @Bean(name = "qrCodeVNPay")
    public QRCode qrCodeVNPay() {
        return new QRCodeVNPayImpl();
    }

    @Bean(name = "gson")
    public Gson gson() {
        return new Gson();
    }

    @Bean(name = "restTemplate")
    public RestTemplate restTemplate(RestTemplateBuilder builder) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> socketFactoryRegistry =  RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("https", sslsf)
                        .register("http", new PlainConnectionSocketFactory())
                        .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setMaxTotal(20);
        connectionManager.setDefaultMaxPerRoute(5);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .setConnectionManager(connectionManager)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        builder.requestFactory(()->new  HttpComponentsClientHttpRequestFactory(httpClient));
        RestTemplate restTemplate = builder.build();
        restTemplate.setRequestFactory(requestFactory);

        return restTemplate;
    }

    @Bean(name = "processService")
    public ProcessFacade processService() {
        return new ProcessFacade();
    }
}
