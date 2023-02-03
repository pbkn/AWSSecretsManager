package com.example.awssecretsmanager.util;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

@Slf4j
public class AwsSNSUtil {

	private AwsSNSUtil() {
		throw new IllegalStateException("AwsSNSUtil class should not be initiated");
	}

	public static void pubTopic(SnsClient snsClient, String message, String topicArn) throws SnsException {

		PublishRequest request = PublishRequest.builder().message(message).topicArn(topicArn).build();

		PublishResponse result = snsClient.publish(request);
		log.info(result.messageId() + " Message sent. Status is " + result.sdkHttpResponse().statusCode());

	}

}
