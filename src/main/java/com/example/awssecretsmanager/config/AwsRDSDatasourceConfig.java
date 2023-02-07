package com.example.awssecretsmanager.config;

import javax.sql.DataSource;

import org.springframework.boot.system.SystemProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.awssecretsmanager.dto.AWSDBSecret;
import com.example.awssecretsmanager.dto.AppParameters;
import com.example.awssecretsmanager.util.AwsSSMUtil;
import com.google.gson.Gson;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.ssm.SsmClient;

@Configuration
public class AwsRDSDatasourceConfig {

	@Bean
	DataSource getDataSource(SecretsManagerClient secretsManagerClient, Gson gson, HikariConfig hikariConfig,
			SsmClient ssmClient) {

		String appParams = AwsSSMUtil.getParaValue(ssmClient, "/poc/poc_app/" + SystemProperties.get("PipelineNumber"));
		AppParameters appParameters = gson.fromJson(appParams, AppParameters.class);

		String secretName = appParameters.getSecretsManagerKey();

		GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder().secretId(secretName).build();

		GetSecretValueResponse getSecretValueResponse;

		getSecretValueResponse = secretsManagerClient.getSecretValue(getSecretValueRequest);

		String secret = getSecretValueResponse.secretString();
		AWSDBSecret secrets = gson.fromJson(secret, AWSDBSecret.class);

		hikariConfig.setDriverClassName("com.amazonaws.secretsmanager.sql.AWSSecretsManagerPostgreSQLDriver");
		hikariConfig.setUsername(secretName);
		hikariConfig.setJdbcUrl("jdbc-secretsmanager:postgresql://" + secrets.getHost() + ":" + secrets.getPort() + "/"
				+ secrets.getDbname());
		hikariConfig.setAutoCommit(false);
		return new HikariDataSource(hikariConfig);
	}
}
