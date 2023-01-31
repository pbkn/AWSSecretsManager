package com.example.awssecretsmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;

@Configuration
public class GsonConfig {

	@Bean
	Gson getGson() {
		return new Gson();
	}

}
