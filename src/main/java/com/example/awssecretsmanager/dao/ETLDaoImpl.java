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

import com.opencsv.CSVWriter;

@Component
public class ETLDaoImpl {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Transactional(readOnly = true)
	public void writeCsvFromQuery(String sqlQuery, String appTempCsvFilePath) throws Exception {
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
	}

}
