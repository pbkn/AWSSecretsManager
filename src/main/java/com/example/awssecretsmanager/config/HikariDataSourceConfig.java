package com.example.awssecretsmanager.config;

import org.springframework.boot.system.SystemProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;

@Configuration
public class HikariDataSourceConfig {

	@Bean
	HikariConfig getHikariConfig() {
		HikariConfig config = new HikariConfig();
		config.setConnectionTimeout(30000);
		config.setIdleTimeout(60000);
		config.setMaxLifetime(21600000);
		config.setPoolName(SystemProperties.get("PipelineNumber") + "-hikari-pool");
		config.setMaximumPoolSize(1);
		config.setMinimumIdle(1);
		return config;
	}

}
