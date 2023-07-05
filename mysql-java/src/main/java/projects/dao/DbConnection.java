package projects.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import projects.exception.DbException;

//create the following constants: HOST, PASSWORD, PORT, SCHEMA, and USER. 
public class DbConnection {
	private static final String HOST = "localhost";
	private static final String PORT = "3306";
	private static final String SCHEMA = "projects";
	private static final String USER = "projects";
	private static final String PASSWORD = "projects";

	public static Connection getConnection() {
		String url = String.format(
				"jdbc:mysql://%s:%s/%s?user=%s&password=%s&useSSL=false&allowPublicKeyRetrieval=true", HOST, PORT,
				SCHEMA, USER, PASSWORD);
		try {
			Connection connection = DriverManager.getConnection(url);
			System.out.println("Connected to the database successfully.");
			return connection;
		} catch (SQLException e) {
			System.err.println("Error connecting to the database: " + e.getMessage());
			throw new DbException("Failed to establish a database connection.", e);
		}
	}
}