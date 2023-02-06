package com.example.awssecretsmanager.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResultView1 {

	@CsvBindByName(column = "Id", required = true)
	@CsvBindByPosition(position = 0)
	private Long id;

	@CsvBindByName(column = "Col1", required = true)
	@CsvBindByPosition(position = 1)
	private String col1;

	@CsvBindByName(column = "Col2", required = true)
	@CsvBindByPosition(position = 2)
	private String col2;

	@CsvBindByName(column = "Col3", required = true)
	@CsvBindByPosition(position = 3)
	private String col3;

}
