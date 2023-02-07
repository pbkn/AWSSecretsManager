package com.example.awssecretsmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.awssecretsmanager.util.AppConstants;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.ssm.SsmClient;

@Configuration
public class AwsSsmConfig {

	@Bean
	SsmClient getSsmClient(AwsCredentialsProvider awsCredentials) {
		return SsmClient.builder().region(AppConstants.AWS_REGION).credentialsProvider(awsCredentials).build();
	}
}
