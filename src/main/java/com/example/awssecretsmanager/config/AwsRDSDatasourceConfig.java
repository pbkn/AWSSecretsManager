package com.example.awssecretsmanager.config;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.awssecretsmanager.dto.AWSDBSecret;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@Configuration
@Slf4j
public class AwsRDSDatasourceConfig {

	@Bean
	DataSource getDataSource(SecretsManagerClient secretsManagerClient, Gson gson) {

		String secretName = "dev/poc/postgres";

		GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder().secretId(secretName).build();

		GetSecretValueResponse getSecretValueResponse;

		try {
			getSecretValueResponse = secretsManagerClient.getSecretValue(getSecretValueRequest);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}

		String secret = getSecretValueResponse.secretString();
		AWSDBSecret secrets = gson.fromJson(secret, AWSDBSecret.class);
		return DataSourceBuilder.create()
				.driverClassName("com.amazonaws.secretsmanager.sql.AWSSecretsManagerPostgreSQLDriver")
				.username(secretName).url("jdbc-secretsmanager:postgresql://" + secrets.getHost() + ":"
						+ secrets.getPort() + "/" + secrets.getDbInstanceIdentifier())
				.build();
	}
}
