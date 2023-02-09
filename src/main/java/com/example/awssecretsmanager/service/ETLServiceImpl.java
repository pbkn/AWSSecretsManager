package com.example.awssecretsmanager.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.SystemProperties;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.example.awssecretsmanager.dao.ETLDaoImpl;
import com.example.awssecretsmanager.dto.AppParameters;
import com.example.awssecretsmanager.util.AppConstants;
import com.example.awssecretsmanager.util.AwsS3Util;
import com.example.awssecretsmanager.util.AwsSNSUtil;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

@Service
@Slf4j
public class ETLServiceImpl {

	@Autowired
	ETLDaoImpl etlDaoImpl;

	@Autowired
	S3TransferManager s3TransferManager;

	@Autowired
	SnsClient snsClient;

	private Integer retryAttempt = -1;

	@Retryable(retryFor = { Exception.class,
			RuntimeException.class }, maxAttempts = 4, backoff = @Backoff(delay = 30000, multiplier = 2, maxDelay = 120000))
	public void executeETL(AppParameters appParameters) throws Exception, RuntimeException {

		String bucketName = appParameters.getS3BucketName();
		String bucketPathKey = appParameters.getS3BucketPath();
		String appTempCsvFilePath = appParameters.getAppTempCsvFilePath();
		String sqlQuery = appParameters.getSqlQuery();

		if (retryAttempt++ > -1) {
			log.warn("Retry attempted for data pipeline {} with retry count {}",
					SystemProperties.get(AppConstants.PIPELINE_KEY), retryAttempt);
		}

		etlDaoImpl.writeCsvFromQuery(sqlQuery, appTempCsvFilePath);

		AwsS3Util.uploadFile(s3TransferManager, bucketName, bucketPathKey, appTempCsvFilePath);

		String message = "Successfully uploaded the extract for " + SystemProperties.get(AppConstants.PIPELINE_KEY)
				+ " at " + LocalDateTime.now(AppConstants.UTC_ZONE);
		String subject = "POC-" + SystemProperties.get(AppConstants.PIPELINE_KEY) + " Successful execution "
				+ LocalDate.now(AppConstants.UTC_ZONE);
		AwsSNSUtil.pubTopic(snsClient, message, AppConstants.SNS_TOPIC_ARN, subject);

	}

	@Recover
	public void recoverExecuteETL(Exception e) {
		e.printStackTrace();
		log.error(e.getMessage());
		String message = "Max retries exhausted for " + SystemProperties.get(AppConstants.PIPELINE_KEY) + "at "
				+ LocalDateTime.now(AppConstants.UTC_ZONE) + "For exception: " + e.getMessage() + "With Cause: "
				+ e.getCause().toString();
		String subject = "POC-" + SystemProperties.get(AppConstants.PIPELINE_KEY) + " Max retires exhausted "
				+ LocalDate.now(AppConstants.UTC_ZONE);
		AwsSNSUtil.pubTopic(snsClient, message, AppConstants.SNS_TOPIC_ARN, subject);
	}

}
