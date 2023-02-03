package com.example.awssecretsmanager.service;

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

	public void executeETL() {
		log.info("DB Connection is established with {}", jdbcTemplate.toString());

		jdbcTemplate.setFetchSize(10);
		List<ResultView1> resultList = new ArrayList<>();

		jdbcTemplate.query(sqlQuery, new RowCallbackHandler() {
			public void processRow(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					resultList.add(ResultView1.builder().col1(resultSet.getString("col1"))
							.col2(resultSet.getString("col2")).col3(resultSet.getString("col3")).build());
				}
			}
		});

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
