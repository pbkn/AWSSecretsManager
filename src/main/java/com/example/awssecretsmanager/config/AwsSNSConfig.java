package com.example.awssecretsmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.awssecretsmanager.util.AppConstants;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class AwsSNSConfig {

	@Bean
	SnsClient getSnsClient(AwsCredentialsProvider awsCredentials) {
		return SnsClient.builder().region(AppConstants.AWS_REGION).credentialsProvider(awsCredentials).build();
	}
}
