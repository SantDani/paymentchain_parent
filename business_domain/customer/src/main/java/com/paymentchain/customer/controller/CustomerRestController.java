/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/RestController.java to edit this template
 */
package com.paymentchain.customer.controller;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

/**
 *
 * @author SantiagoSRP
 */
@RestController
@RequestMapping("/customer")
public class CustomerRestController {

    @Autowired
    CustomerRepository customerRepository;
    
    private final WebClient.Builder webClientBuilder;
    
    public CustomerRestController(WebClient.Builder webClientBuilder){
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
    
    @GetMapping()
    public ResponseEntity<List<Customer>> list() {
        List<Customer> customers = customerRepository.findAll();
        
        if(customers == null || customers.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(name = "id") long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            return new ResponseEntity<>(customer.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") long id, @RequestBody Customer input) {
        Optional<Customer> optionalcustomer = customerRepository.findById(id);
        if (optionalcustomer.isPresent()) {
            Customer newcustomer = optionalcustomer.get();
            newcustomer.update(
                    input.getName(),
                    input.getPhone(), 
                    input.getCode(), 
                    input.getIban(), 
                    input.getSurname(), 
                    input.getAddress());
            Customer save = customerRepository.save(newcustomer);
            return new ResponseEntity<>(save, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Customer input) {
        input.getProducts().forEach(product -> product.setCustomer(input));
        Customer save = customerRepository.save(input);
        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") long id) {
        customerRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    
   
    
    @GetMapping("/full")
    public Customer getByCode(@RequestParam(name = "code") String code){
        Customer customer = customerRepository.findByCode(code);
        if(customer != null){
            List<CustomerProduct> products = customer.getProducts();
            
            //for each product find it name
            products.forEach(x -> {
                String productName = getProductName(x.getProductId());
                x.setProductName(productName);
            });
            
            //find all transactions that belong this account number
            List<?> transactions = getTransactions(customer.getIban());
            customer.setTransactions(transactions);
        }
        
        return customer;
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
     
    private List<?> getTransactions(String iban){
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
