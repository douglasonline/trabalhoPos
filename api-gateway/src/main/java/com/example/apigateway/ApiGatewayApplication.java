package com.example.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
		return  builder.routes()
				 .route(p -> p
						 .path("/api/produto/**")
						 .filters(f -> f.stripPrefix(2)
								 .addRequestHeader("hello", "world")
								 .addRequestHeader("Allow-Origins", "Pong"))


						 .uri("lb://PRODUTO-SERVICE")


				 ).build();

	}

}
