package com.example.awssecretsmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

@Configuration
public class AwsCredentialProviderConfig {

	@Bean
	AwsCredentialsProvider getAWSCredentialsProvider() {
		return DefaultCredentialsProvider.create();
	}
}
