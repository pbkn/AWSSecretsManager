package com.example.awssecretsmanager.service;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import com.example.awssecretsmanager.dto.AppParameters;
import com.example.awssecretsmanager.util.AwsS3Util;
import com.opencsv.CSVWriter;

import software.amazon.awssdk.transfer.s3.S3TransferManager;

@Service
public class ETLServiceImpl {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	S3TransferManager s3TransferManager;

	public void executeETL(AppParameters appParameters) throws Exception {

		String bucketName = appParameters.getS3BucketName();
		String bucketPathKey = appParameters.getS3BucketPath();
		String appTempCsvFilePath = appParameters.getAppTempCsvFilePath();
		String sqlQuery = appParameters.getSqlQuery();

		jdbcTemplate.setFetchSize(10000);

		try (CSVWriter csvFile = new CSVWriter(new FileWriter(appTempCsvFilePath))) {

			jdbcTemplate.query(sqlQuery, new RowCallbackHandler() {
				public void processRow(ResultSet resultSet) throws SQLException {
					try {
						csvFile.writeAll(resultSet, true);
					} catch (SQLException | IOException e) {
						e.printStackTrace();
					}
				}
			});

			csvFile.flushQuietly();
		}

		AwsS3Util.uploadFile(s3TransferManager, bucketName, bucketPathKey, appTempCsvFilePath);

	}

}
