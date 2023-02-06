package com.example.awssecretsmanager.service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import com.example.awssecretsmanager.dto.ResultView1;
import com.example.awssecretsmanager.util.AwsS3Util;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import software.amazon.awssdk.transfer.s3.S3TransferManager;

@Service
public class ETLServiceImpl {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	S3TransferManager s3TransferManager;

	public void executeETL(String pipelineNumber)
			throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {

		switch (pipelineNumber) {
			case "DataPipeline1" -> processPipeline1();
//			case "DataPipeline2" -> processPipeline1();

			default -> throw new IllegalArgumentException("Unexpected value for pipelineNumber:" + pipelineNumber);
		}
	}

	private void processPipeline1() throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
		String bucketName = "poc.rs1";

		String bucketPathKey = "pending/resultView1.csv";

		String rs1Path = "./data/rs1/resultView1.csv";

		String sqlQuery = "SELECT * FROM poc_tbl";

		jdbcTemplate.setFetchSize(10000);
		List<ResultView1> resultList = new ArrayList<>();

		jdbcTemplate.query(sqlQuery, new RowCallbackHandler() {
			public void processRow(ResultSet resultSet) throws SQLException {
				resultList.add(ResultView1.builder().id(resultSet.getLong("id")).col1(resultSet.getString("col1"))
						.col2(resultSet.getString("col2")).col3(resultSet.getString("col3")).build());
			}
		});

		try (FileWriter writer = new FileWriter(rs1Path)) {
			ColumnPositionMappingStrategy<ResultView1> mappingStrategy = new ColumnPositionMappingStrategy<>();
			mappingStrategy.setType(ResultView1.class);

			StatefulBeanToCsv<ResultView1> beanWriter = new StatefulBeanToCsvBuilder<ResultView1>(writer)
					.withApplyQuotesToAll(false).withMappingStrategy(mappingStrategy).build();

			beanWriter.write(resultList);
		}

		AwsS3Util.uploadFile(s3TransferManager, bucketName, bucketPathKey, rs1Path);

		Files.deleteIfExists(Paths.get(rs1Path));
	}

}
