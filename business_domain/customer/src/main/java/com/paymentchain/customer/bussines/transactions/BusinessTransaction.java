/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paymentchain.customer.bussines.transactions;

import com.fasterxml.jackson.databind.JsonNode;
import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.entities.CustomerProduct;
import com.paymentchain.customer.exception.BusinessRuleException;
import com.paymentchain.customer.repository.CustomerRepository;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 *
 * @author SantiagoSRP
 */
@Service
public class BusinessTransaction {
    
    private final String ENDPOINT_PRODUCT= "http://business-domain-product/product";
    @Autowired
    CustomerRepository customerRepository;

    private final WebClient.Builder webClientBuilder;

    public BusinessTransaction(WebClient.Builder webClientBuilder) {
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

    public Customer getByCode(String code) throws UnknownHostException {
        Customer customer = customerRepository.findByCode(code);
        if (customer != null) {
            List<CustomerProduct> products = customer.getProducts();

            for (CustomerProduct product : products) {
                String productName;
                try {
                    productName = getProductName(product.getProductId());
                } catch (UnknownHostException e) {
                    throw e;
                }
                product.setProductName(productName);
            }

            //find all transactions that belong this account number
            List<?> transactions = getTransactions(customer.getIban());
            customer.setTransactions(transactions);
        }

        return customer;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(BusinessTransaction.class);

     public Customer save(Customer input) throws BusinessRuleException, UnknownHostException {
        logger.info("Start Save for Customer: {}", input);

        if (input.getProducts() != null) {
            for (Iterator<CustomerProduct> it = input.getProducts().iterator(); it.hasNext();) {
                CustomerProduct dto = it.next();
                String productName = getProductName(dto.getProductId());
                
                if (productName.isBlank()) {
                    logger.error("Validation error, from product ID {} not exists", dto.getProductId());
                    String msgError = "Validation error, producto not exists. Code: " +  dto.getProductId()+ " Name: " + dto.getProductName();
                    BusinessRuleException exception = new BusinessRuleException("1025", msgError, HttpStatus.PRECONDITION_FAILED);
                    throw exception;
                } else {
                    dto.setCustomer(input);
                }
            }
        }
        return customerRepository.save(input);
    }

    private String getProductName(long id) throws UnknownHostException {
        String name = null;
        try {
            WebClient webClientBuilded = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                        .baseUrl(ENDPOINT_PRODUCT)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultUriVariables(Collections.singletonMap("url", ENDPOINT_PRODUCT))
                    .build();

            // Add id to endpoint
            JsonNode block = webClientBuilded.method(HttpMethod.GET).uri("/" + id)
                    .retrieve().bodyToMono(JsonNode.class).block();
            name = block.get("name").asText();
        }catch (WebClientResponseException e){
            HttpStatusCode statusCode = e.getStatusCode();
           
            if(statusCode.equals(HttpStatus.NOT_FOUND)){
                return "";
            }else{
                String errorMessage ="endpoint : " + ENDPOINT_PRODUCT + " " +   e.getMessage();
                throw new UnknownHostException(errorMessage);
            }
        }

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
