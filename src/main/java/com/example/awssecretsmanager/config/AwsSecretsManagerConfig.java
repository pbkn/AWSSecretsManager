package com.example.awssecretsmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.awssecretsmanager.util.AppConstants;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

@Configuration
public class AwsSecretsManagerConfig {

	@Bean
	SecretsManagerClient secretsManagerClient(AwsCredentialsProvider awsCredentials) {
		return SecretsManagerClient.builder().credentialsProvider(awsCredentials).region(AppConstants.AWS_REGION)
				.build();
	}
}