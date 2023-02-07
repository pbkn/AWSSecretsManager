package com.example.awssecretsmanager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.SystemProperties;

import com.example.awssecretsmanager.dto.AppParameters;
import com.example.awssecretsmanager.service.ETLServiceImpl;
import com.example.awssecretsmanager.util.AppConstants;
import com.example.awssecretsmanager.util.AwsSNSUtil;
import com.example.awssecretsmanager.util.AwsSSMUtil;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.ssm.SsmClient;

@SpringBootApplication
@Slf4j
public class AwsSecretsManagerApplication implements ApplicationRunner {

	@Autowired
	ETLServiceImpl etlServiceImpl;

	@Autowired
	SnsClient snsClient;

	@Autowired
	Gson gson;

	@Autowired
	SsmClient ssmClient;

	public static void main(String[] args) {
		SpringApplication.run(AwsSecretsManagerApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) {
		String appTempCsvFilePath = "./data";
		try {
			String pipelineNumber = SystemProperties.get(AppConstants.PIPELINE_KEY);
			String appParams = AwsSSMUtil.getParaValue(ssmClient, "/poc/poc_app/" + pipelineNumber);
			AppParameters appParameters = gson.fromJson(appParams, AppParameters.class);
			appTempCsvFilePath = appParameters.getAppTempCsvFilePath();
			etlServiceImpl.executeETL(appParameters);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			String message = "Execption occurred at " + SystemProperties.get(AppConstants.PIPELINE_KEY) + "at "
					+ LocalDateTime.now(AppConstants.UTC_ZONE) + "For exception: " + e.getMessage() + "With Cause: "
					+ e.getCause().toString();
			String subject = "POC-" + SystemProperties.get(AppConstants.PIPELINE_KEY) + " Exception during execution "
					+ LocalDate.now(AppConstants.UTC_ZONE);
			AwsSNSUtil.pubTopic(snsClient, message, AppConstants.SNS_TOPIC_ARN, subject);
		} finally {
			try {
				Files.deleteIfExists(Paths.get(appTempCsvFilePath));
			} catch (IOException e) {
				e.printStackTrace();
				log.error(e.getMessage());
				String message = "Execption occurred at " + SystemProperties.get(AppConstants.PIPELINE_KEY) + "at "
						+ LocalDateTime.now(AppConstants.UTC_ZONE) + "For exception: " + e.getMessage() + "With Cause: "
						+ e.getCause().toString();
				String subject = "POC-" + SystemProperties.get(AppConstants.PIPELINE_KEY)
						+ " Exception during temp Csv File deletion " + LocalDate.now(AppConstants.UTC_ZONE);
				AwsSNSUtil.pubTopic(snsClient, message, AppConstants.SNS_TOPIC_ARN, subject);
			}
		}
	}

}
