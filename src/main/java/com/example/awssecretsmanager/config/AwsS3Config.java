package com.example.awssecretsmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

@Configuration
public class AwsS3Config {

	@Bean
	S3TransferManager getS3TransferManager() {
		return S3TransferManager.builder()
				.s3Client(S3AsyncClient.crtBuilder().credentialsProvider(DefaultCredentialsProvider.create())
						.region(Region.AP_SOUTH_1).targetThroughputInGbps(20.0)
						.minimumPartSizeInBytes(Long.valueOf(8 * 1024 * 1024)).build())
				.build();
	}
}
