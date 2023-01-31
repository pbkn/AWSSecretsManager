package com.example.awssecretsmanager.dto;

import lombok.Data;

@Data
public class AWSDBSecret {

	private String host;

	private String port;

	private String dbInstanceIdentifier;
}
