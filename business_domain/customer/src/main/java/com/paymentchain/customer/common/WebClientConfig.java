/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paymentchain.customer.common;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration of {@link WebClient} for microservices with Spring Cloud.
 * 
 * This configuration class is crucial for enabling service-to-service communication
 * in a microservices environment (Customer, product, Transactions, ...). It provides a {@link WebClient.Builder} configured
 * to use load balancing, allowing services to communicate using logical names instead
 * of physical IP addresses. This facilitates service discovery and scalability in the cloud,
 * leveraging integration with Eureka.
 * 
 * <p>Load balancing is achieved through the {@link LoadBalanced} annotation, which
 * instructs Spring Cloud to automatically configure the {@link WebClient} to use
 * {@link org.springframework.cloud.client.loadbalancer.LoadBalancerClient}. This allows
 * dynamic service resolution and distributes requests among available service instances,
 * improving application availability and resilience.</p>
 * 
 * <p>Centralized configuration of the {@link WebClient} simplifies HTTP client management
 * in the application, promoting consistent development practices and facilitating the implementation
 * of resilience patterns like circuit breakers.</p>
 *
 * @Configuration indicates that this class is a source of bean definitions for the application context.
 * @author SantiagoSRP
 */
@Configuration
public class WebClientConfig {

  /**
    * Provides a {@link WebClient.Builder} enabled for load balancing.
    * 
    * This bean is pivotal for communication between services in a microservices architecture,
    * enabling dynamic discovery and communication with services registered in Eureka
    * using their logical names. Equipped with load balancing, the {@link WebClient}
    * created from this builder can efficiently distribute requests among multiple service instances,
    * based on the load balancing strategy configured in the runtime environment.
    * 
    * <p>The use of {@link LoadBalanced} is essential to activate these capabilities, closely integrating
    * with Spring Cloud and Eureka's service discovery capabilities.</p>
    * 
    * @return a {@link WebClient.Builder} configured to support load balancing.
    */
  @Bean
  @LoadBalanced
  public WebClient.Builder loadBalancedWebClientBuilder() {
    return WebClient.builder();
  }
}
