package com.periferia.etheria.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.periferia.etheria.constants.Constants;
import com.periferia.etheria.exception.UserException;
import com.periferia.etheria.security.ReadSecret;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DBConfig {

	public DBConfig() {} 

	public Connection getConnection() {
		Connection connection = null;

		try {
			String jdbc = ReadSecret.getSecret("db_url");
			String userName = ReadSecret.getSecret("username");
			String password = ReadSecret.getSecret("password");

			connection = DriverManager.getConnection(jdbc, userName, password);
			log.info(Constants.CONECTION_OK);
		}
		catch (SQLException e) {
			throw new UserException(Constants.CONECTION_ERROR, 500, e.getMessage());
		}
		return connection;
	}
}
