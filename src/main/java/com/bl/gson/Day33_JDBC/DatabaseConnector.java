package com.bl.gson.Day33_JDBC;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class DatabaseConnector {
	
	private static Connection connection;
	
	private DatabaseConnector() {
		
	}
	
	public static Connection getConnection() {
		if(connection == null) {
			String jdbcUrl = "jdbc:mysql://localhost:3306/employee_db";
			String username = "root";
			String password = "root";
			
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				System.out.println("JDBC Driver class is loaded.");
			} catch (ClassNotFoundException e) {
				System.out.println("mysql connector is not loaded");
				e.printStackTrace();
			}
			
			listDbDriver();
			
			try {
				connection = DriverManager.getConnection(jdbcUrl, username, password);
				System.out.println("Connection established successfully.");
			} catch (SQLException e) {
				System.out.println("Connection establish failed. \n");
				e.printStackTrace();
			}	
		}
		return connection;
	}

	private static void listDbDriver() {
		Enumeration<Driver> listDriver = DriverManager.getDrivers();
		while(listDriver.hasMoreElements()) {
			Driver d = listDriver.nextElement();
			System.out.println(d.getClass().getName());
		}
	}
}
