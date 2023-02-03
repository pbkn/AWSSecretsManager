package com.example.awssecretsmanager.util;

import java.nio.file.Paths;

import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileUpload;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;
import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener;

public class AwsS3Util {

	private AwsS3Util() {
		throw new IllegalStateException("AwsS3Util class should not be initiated");
	}

	public static String uploadFile(S3TransferManager transferManager, String bucketName, String key, String filePath) {
		UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
				.putObjectRequest(b -> b.bucket(bucketName).key(key))
				.addTransferListener(LoggingTransferListener.create()).source(Paths.get(filePath)).build();

		FileUpload fileUpload = transferManager.uploadFile(uploadFileRequest);

		CompletedFileUpload uploadResult = fileUpload.completionFuture().join();
		return uploadResult.response().eTag();
	}
}
