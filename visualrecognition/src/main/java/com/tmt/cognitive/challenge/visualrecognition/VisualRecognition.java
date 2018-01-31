package com.tmt.cognitive.challenge.visualrecognition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/*@EnableDiscoveryClient*/
@SpringBootApplication
@ComponentScan(basePackages = { "com.tmt" })
@PropertySources({
@PropertySource(value = "file:${spring.config.location}")
})
public class VisualRecognition {

	public static void main(String[] args) {
		SpringApplication.run(VisualRecognition.class, args);

	}
}
