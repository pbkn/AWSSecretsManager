package com.example.awssecretsmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class AwsSNSConfig {

	@Bean
	SnsClient getSnsClient(AwsCredentialsProvider awsCredentials) {
		return SnsClient.builder().region(Region.AP_SOUTH_1).credentialsProvider(awsCredentials).build();
	}
}
