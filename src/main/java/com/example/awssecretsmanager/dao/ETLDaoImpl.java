package com.example.awssecretsmanager.dao;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.awssecretsmanager.util.ResultSetConverter;
import com.opencsv.CSVWriter;

import software.amazon.awssdk.services.sns.SnsClient;

@Component
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
					if (resultSet.isFirst())
						csvFile.writeNext(ResultSetConverter.getRowValues(resultSet, true), false);
					csvFile.writeNext(ResultSetConverter.getRowValues(resultSet, false), false);
				}

			});

			csvFile.flushQuietly();
		}
	}

}
