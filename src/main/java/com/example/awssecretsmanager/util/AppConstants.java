package com.example.awssecretsmanager.util;

import java.time.ZoneId;

import software.amazon.awssdk.regions.Region;

public class AppConstants {

	private AppConstants() {
		throw new IllegalStateException("Constants class should not be initiated");
	}

	public static final String PIPELINE_KEY = "PipelineNumber";
	public static final Region AWS_REGION = Region.AP_SOUTH_1;
	public static final ZoneId UTC_ZONE = ZoneId.of("UTC");
	public static final String SNS_TOPIC_ARN = "arn:aws:sns:ap-south-1:401135408972:poc-sns";
	public static final int MAX_RETRY_COUNT = 5;

}
