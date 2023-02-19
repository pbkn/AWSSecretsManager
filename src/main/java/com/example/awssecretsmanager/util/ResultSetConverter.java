package com.example.awssecretsmanager.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetConverter {

	private ResultSetConverter() {
		throw new IllegalStateException("ResultSetConverter class should not be initiated");
	}

	public static String[] getColumnValues(ResultSet resultSet) throws SQLException {
		ResultSetMetaData rsmd = resultSet.getMetaData();
		int numberOfColumns = rsmd.getColumnCount();
		String[] resultArray = new String[numberOfColumns];
		for (int i = 1; i <= numberOfColumns; i++) {
			resultArray[i - 1] = resultSet.getString(i);
		}
		return resultArray;
	}

	public static String[] getColumnHeaders(ResultSet resultSet) throws SQLException {
		ResultSetMetaData rsmd = resultSet.getMetaData();
		int numberOfColumns = rsmd.getColumnCount();
		String[] resultArray = new String[numberOfColumns];
		for (int i = 1; i <= numberOfColumns; i++) {
			resultArray[i - 1] = rsmd.getColumnName(i);
		}
		return resultArray;
	}
}
