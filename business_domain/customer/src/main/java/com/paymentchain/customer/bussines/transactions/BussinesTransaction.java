/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paymentchain.customer.bussines.transactions;

import com.fasterxml.jackson.databind.JsonNode;
import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.entities.CustomerProduct;
import com.paymentchain.customer.repository.CustomerRepository;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

/**
 *
 * @author SantiagoSRP
 */
@Service
public class BussinesTransaction {

    @Autowired
    CustomerRepository customerRepository;

    private final WebClient.Builder webClientBuilder;

    public BussinesTransaction(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    //webClient requires HttpClient library to work propertly       
    HttpClient client = HttpClient.create()
            //Connection Timeout: is a period within which a connection between a client and a server must be established
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(EpollChannelOption.TCP_KEEPIDLE, 300)
            .option(EpollChannelOption.TCP_KEEPINTVL, 60)
            //Response Timeout: The maximun time we wait to receive a response after sending a request
            .responseTimeout(Duration.ofSeconds(1))
            // Read and Write Timeout: A read timeout occurs when no data was read within a certain 
            //period of time, while the write timeout when a write operation cannot finish at a specific time
            .doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
            });

    public Customer getByCode(String code) {
        Customer customer = customerRepository.findByCode(code);
        //if(customer != null){
        List<CustomerProduct> products = customer.getProducts();

        //for each product find it name
        products.forEach(x -> {
            String productName = getProductName(x.getProductId());
            x.setProductName(productName);
        });

        //find all transactions that belong this account number
        List<?> transactions = getTransactions(customer.getIban());
        customer.setTransactions(transactions);
        //}

        return customer;
    }
    
    
     public Customer save( Customer input) {
        input.getProducts().forEach(product -> product.setCustomer(input));
        Customer save = customerRepository.save(input);
        return  save;
    }

    private String getProductName(long id) {
        WebClient webClientBuilded = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl("http://business-domain-product/product")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", "http://business-domain-product/product"))
                .build();

        // Add id to endpoint
        JsonNode block = webClientBuilded.method(HttpMethod.GET).uri("/" + id)
                .retrieve().bodyToMono(JsonNode.class).block();
        String name = block.get("name").asText();
        return name;
    }

    private List<?> getTransactions(String iban) {
        WebClient webClientBuilded = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl("http://business-domain-transactions/transaction")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", "http://business-domain-transactions/product"))
                .build();

        Optional<List<?>> transactionsOptional = Optional.ofNullable(webClientBuilded.method(HttpMethod.GET)
                .uri(uriBuilder -> uriBuilder
                .pathSegment("customer", "transactions", "{iban}")
                .build(iban))
                .retrieve()
                .bodyToFlux(Object.class)
                .collectList()
                .block());

        return transactionsOptional.orElse(Collections.emptyList());
    }
}
