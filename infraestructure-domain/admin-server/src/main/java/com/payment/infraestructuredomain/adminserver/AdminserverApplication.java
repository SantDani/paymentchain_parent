package com.payment.infraestructuredomain.adminserver;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


// Get start Admin Server https://docs.spring-boot-admin.com/current/getting-started.html
@SpringBootApplication
@EnableAdminServer
@Configuration
@EnableAutoConfiguration
@EnableDiscoveryClient
public class AdminserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminserverApplication.class, args);
    }
    
    
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) {
        try {
            return http.authorizeHttpRequests((authorizeRequests) -> authorizeRequests.anyRequest().permitAll())
                    .csrf().disable().build();
        } catch (Exception ex) {
            Logger.getLogger(AdminserverApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
