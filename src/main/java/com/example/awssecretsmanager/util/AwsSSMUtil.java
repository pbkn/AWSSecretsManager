package com.example.awssecretsmanager.util;

import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

public class AwsSSMUtil {

	private AwsSSMUtil() {
		throw new IllegalStateException("AwsSSMUtil class should not be initiated");
	}

	public static String getParaValue(SsmClient ssmClient, String paraName) {

		GetParameterRequest parameterRequest = GetParameterRequest.builder().name(paraName).build();

		GetParameterResponse parameterResponse = ssmClient.getParameter(parameterRequest);
		return parameterResponse.parameter().value();

	}
}
