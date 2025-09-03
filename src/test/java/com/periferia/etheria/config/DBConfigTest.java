package com.periferia.etheria.config;

import com.periferia.etheria.constants.Constants;
import com.periferia.etheria.exception.UserException;
import com.periferia.etheria.security.ReadSecret;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

class DBConfigTest {

	private static MockedStatic<DriverManager> driverManagerMock;
	private static MockedStatic<ReadSecret> readSecretMock;
	private Connection mockConnection;

	@BeforeAll
	static void init() {
		driverManagerMock = Mockito.mockStatic(DriverManager.class);
		readSecretMock = Mockito.mockStatic(ReadSecret.class);
	}

	@AfterAll
	static void close() {
		driverManagerMock.close();
		readSecretMock.close();
	}

	@BeforeEach
	void setUp() {
		mockConnection = Mockito.mock(Connection.class);

		readSecretMock.when(() -> ReadSecret.getSecret("db_url"))
		.thenReturn("jdbc:postgresql://localhost:5432/testdb");
		readSecretMock.when(() -> ReadSecret.getSecret("username"))
		.thenReturn("testuser");
		readSecretMock.when(() -> ReadSecret.getSecret("password"))
		.thenReturn("testpass");
	}

	@Test
	void testGetConnectionSuccess() {
		driverManagerMock.when(() ->
		DriverManager.getConnection(
				eq("jdbc:postgresql://localhost:5432/testdb"),
				eq("testuser"),
				eq("testpass")))
		.thenReturn(mockConnection);

		DBConfig dbConfig = new DBConfig();
		Connection result = dbConfig.getConnection();

		assertNotNull(result);
		assertEquals(mockConnection, result);
	}

	@Test
	void testGetConnectionFailure() {
		readSecretMock.when(() -> ReadSecret.getSecret("db_url"))
		.thenReturn("jdbc:postgresql://localhost:5432/testdb");
		readSecretMock.when(() -> ReadSecret.getSecret("username"))
		.thenReturn("testuser");
		readSecretMock.when(() -> ReadSecret.getSecret("password"))
		.thenReturn("testpass");

		driverManagerMock.when(() -> 
		DriverManager.getConnection(
				"jdbc:postgresql://localhost:5432/testdb",
				"testuser",
				"testpass"
				)
				).thenAnswer(invocation -> { throw new SQLException("Connection failed"); });

		DBConfig dbConfig = new DBConfig();

		UserException ex = assertThrows(UserException.class, dbConfig::getConnection);

		assertEquals(Constants.CONECTION_ERROR, ex.getMessage());
		assertEquals(500, ex.getErrorCode());
	}

	@Test
	void testConstructor() {
		DBConfig dbConfig = new DBConfig();
		assertNotNull(dbConfig);
	}
}
