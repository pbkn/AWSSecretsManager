package com.example.awssecretsmanager.dto;

import lombok.Data;

@Data
public class AppParameters {

	private String secretsManagerKey;

	private String s3BucketName;

	private String s3BucketPath;

	private String appTempCsvFilePath;

	private String sqlQuery;

}
