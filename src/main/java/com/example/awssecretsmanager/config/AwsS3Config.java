package com.example.awssecretsmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import com.example.awssecretsmanager.util.AppConstants;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.s3.S3AsyncClient;

@Configuration
public class AwsS3Config {

	@Bean
	S3AsyncClient getS3AsyncClient(AwsCredentialsProvider awsCredentials) {
		return S3AsyncClient.crtBuilder().region(AppConstants.AWS_REGION).credentialsProvider(awsCredentials)
				.targetThroughputInGbps(20.0).minimumPartSizeInBytes(DataSize.ofMegabytes(8).toBytes()).build();
	}
}
