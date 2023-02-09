package com.example.awssecretsmanager.config;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.system.SystemProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;

@Configuration
public class HikariDataSourceConfig {

	@Bean
	HikariConfig getHikariConfig() {
		HikariConfig config = new HikariConfig();
		config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(30));
		config.setIdleTimeout(TimeUnit.SECONDS.toMillis(60));
		config.setMaxLifetime(TimeUnit.HOURS.toMillis(1));
		config.setPoolName(SystemProperties.get("PipelineNumber") + "-hikari-pool");
		config.setMaximumPoolSize(1);
		config.setMinimumIdle(1);
		return config;
	}

}
