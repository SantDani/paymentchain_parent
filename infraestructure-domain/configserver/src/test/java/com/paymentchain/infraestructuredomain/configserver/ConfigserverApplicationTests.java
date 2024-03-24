package com.paymentchain.infraestructuredomain.configserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "eureka.client.register-with-eureka=false",
    "eureka.client.fetch-registry=false",
    "spring.cloud.discovery.enabled=false"
})
class ConfigserverApplicationTests {

	@Test
	void contextLoads() {
	}

}
