package com.example.awssecretsmanager.dao;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.SystemProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.awssecretsmanager.util.AppConstants;
import com.example.awssecretsmanager.util.AwsSNSUtil;
import com.opencsv.CSVWriter;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sns.SnsClient;

@Component
@Slf4j
public class ETLDaoImpl {

	@Autowired
	SnsClient snsClient;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Transactional(readOnly = true)
	public void writeCsvFromQuery(String sqlQuery, String appTempCsvFilePath) throws IOException {

		jdbcTemplate.setFetchSize(10000);

		try (CSVWriter csvFile = new CSVWriter(new FileWriter(appTempCsvFilePath))) {

			jdbcTemplate.query(sqlQuery, new RowCallbackHandler() {
				public void processRow(ResultSet resultSet) throws SQLException {
					try {
						csvFile.writeAll(resultSet, true, false, false);
					} catch (SQLException | IOException e) {
						e.printStackTrace();
						log.error(e.getMessage());
						String message = "Execption occurred while executing SQL query at "
								+ SystemProperties.get(AppConstants.PIPELINE_KEY) + "at "
								+ LocalDateTime.now(AppConstants.UTC_ZONE) + "For exception: " + e.getMessage()
								+ "With Cause: " + e.getCause().toString();
						String subject = "POC-" + SystemProperties.get(AppConstants.PIPELINE_KEY)
								+ " Exception during SQL query execution " + LocalDate.now(AppConstants.UTC_ZONE);
						AwsSNSUtil.pubTopic(snsClient, message, AppConstants.SNS_TOPIC_ARN, subject);
					}
				}

			});

			csvFile.flushQuietly();
		}
	}

}
