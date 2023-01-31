package com.example.awssecretsmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

@Configuration
public class AwsSecretsManagerConfig {

	@Bean
	SecretsManagerClient secretsManagerClient(AwsCredentialsProvider awsCredentials) {
		return SecretsManagerClient.builder().credentialsProvider(awsCredentials).region(Region.AP_SOUTH_1).build();
	}
}