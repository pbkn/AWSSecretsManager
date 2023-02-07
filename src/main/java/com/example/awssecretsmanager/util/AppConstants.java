package com.example.awssecretsmanager.util;

import software.amazon.awssdk.regions.Region;

public class AppConstants {

	private AppConstants() {
		throw new IllegalStateException("Constants class should not be initiated");
	}

	public static final Region AWS_REGION = Region.AP_SOUTH_1;
	public static final String SNS_TOPIC_ARN = "arn:aws:sns:ap-south-1:401135408972:poc-sns";

}
