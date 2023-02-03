package com.example.awssecretsmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.awssecretsmanager.service.ETLServiceImpl;

@SpringBootApplication
public class AwsSecretsManagerApplication implements ApplicationRunner {

	@Autowired
	ETLServiceImpl etlServiceImpl;

	public static void main(String[] args) {
		SpringApplication.run(AwsSecretsManagerApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		etlServiceImpl.executeETL();
	}

}
