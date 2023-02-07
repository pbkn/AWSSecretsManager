package com.example.awssecretsmanager.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.SystemProperties;
import org.springframework.stereotype.Service;

import com.example.awssecretsmanager.dao.ETLDaoImpl;
import com.example.awssecretsmanager.dto.AppParameters;
import com.example.awssecretsmanager.util.AppConstants;
import com.example.awssecretsmanager.util.AwsS3Util;
import com.example.awssecretsmanager.util.AwsSNSUtil;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

@Service
public class ETLServiceImpl {

	@Autowired
	ETLDaoImpl etlDaoImpl;

	@Autowired
	S3TransferManager s3TransferManager;

	@Autowired
	SnsClient snsClient;

	public void executeETL(AppParameters appParameters) throws Exception {

		String bucketName = appParameters.getS3BucketName();
		String bucketPathKey = appParameters.getS3BucketPath();
		String appTempCsvFilePath = appParameters.getAppTempCsvFilePath();
		String sqlQuery = appParameters.getSqlQuery();

		etlDaoImpl.writeCsvFromQuery(sqlQuery, appTempCsvFilePath);

		AwsS3Util.uploadFile(s3TransferManager, bucketName, bucketPathKey, appTempCsvFilePath);

		String message = "Successfully uploaded the extract for " + SystemProperties.get(AppConstants.PIPELINE_KEY)
				+ " at " + LocalDateTime.now(AppConstants.UTC_ZONE);
		String subject = "POC-" + SystemProperties.get(AppConstants.PIPELINE_KEY) + " Successful execution "
				+ LocalDate.now(AppConstants.UTC_ZONE);
		AwsSNSUtil.pubTopic(snsClient, message, AppConstants.SNS_TOPIC_ARN, subject);

	}

}
