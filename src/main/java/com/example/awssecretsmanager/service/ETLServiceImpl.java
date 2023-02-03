package com.example.awssecretsmanager.service;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import com.example.awssecretsmanager.dto.ResultView1;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileUpload;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;
import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener;

@Service
@Slf4j
public class ETLServiceImpl {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	S3TransferManager s3TransferManager;

	String sqlQuery = "SELECT * FROM poc";

	String bucketPath = "poc.";

	public void executeETL() throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
		log.info("DB Connection is established with {}", jdbcTemplate.toString());

		jdbcTemplate.setFetchSize(10);
		List<ResultView1> resultList = new ArrayList<>();

		jdbcTemplate.query(sqlQuery, new RowCallbackHandler() {
			public void processRow(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					resultList.add(ResultView1.builder().id(Long.valueOf(1)).col1(resultSet.getString("col1"))
							.col2(resultSet.getString("col2")).col3(resultSet.getString("col3")).build());
				}
			}
		});

		try (FileWriter writer = new FileWriter("./data/rs1/resultView1.csv")) {
			ColumnPositionMappingStrategy<ResultView1> mappingStrategy = new ColumnPositionMappingStrategy<>();
			mappingStrategy.setType(ResultView1.class);

			Field[] fields = ResultView1.class.getDeclaredFields();
			String[] columns = Arrays.stream(fields).map(Field::toString).toArray(String[]::new);

			mappingStrategy.setColumnMapping(columns);

			StatefulBeanToCsv<ResultView1> beanWriter = new StatefulBeanToCsvBuilder<ResultView1>(writer)
					.withMappingStrategy(mappingStrategy).build();

			beanWriter.write(resultList);
		}

		uploadFile(s3TransferManager, bucketPath, "resultView1", "./data/rs1/resultView1.csv");

	}

	public String uploadFile(S3TransferManager transferManager, String bucketName, String key, String filePath) {
		UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
				.putObjectRequest(b -> b.bucket(bucketName).key(key))
				.addTransferListener(LoggingTransferListener.create()).source(Paths.get(filePath)).build();

		FileUpload fileUpload = transferManager.uploadFile(uploadFileRequest);

		CompletedFileUpload uploadResult = fileUpload.completionFuture().join();
		return uploadResult.response().eTag();
	}

}
