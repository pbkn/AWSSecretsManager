package com.example.awssecretsmanager;

import java.time.LocalTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.SystemProperties;

import com.example.awssecretsmanager.service.ETLServiceImpl;
import com.example.awssecretsmanager.util.AppConstants;
import com.example.awssecretsmanager.util.AwsSNSUtil;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sns.SnsClient;

@SpringBootApplication
@Slf4j
public class AwsSecretsManagerApplication implements ApplicationRunner {

	@Autowired
	ETLServiceImpl etlServiceImpl;

	@Autowired
	SnsClient snsClient;

	public static void main(String[] args) {
		SpringApplication.run(AwsSecretsManagerApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) {
		try {
			etlServiceImpl.executeETL(SystemProperties.get("PipelineNumber"));
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			String message = "Execption occurred at " + SystemProperties.get("PipelineNumber") + "at "
					+ LocalTime.now(ZoneId.of("UTC")) + "For exception: " + e.getMessage() + "With Cause: "
					+ e.getCause().toString();
			AwsSNSUtil.pubTopic(snsClient, message, AppConstants.SNStopicArn);
		}
	}

}
