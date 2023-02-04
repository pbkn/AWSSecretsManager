package com.example.awssecretsmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

@Configuration
public class AwsS3TransferManagerConfig {

	@Bean
	S3TransferManager getS3TransferManager(AwsCredentialsProvider awsCredentials, S3AsyncClient s3AsyncClient) {
		return S3TransferManager.builder().s3Client(s3AsyncClient).build();
	}

}
