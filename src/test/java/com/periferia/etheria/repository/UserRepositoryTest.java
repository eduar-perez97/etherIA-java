package com.periferia.etheria.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.periferia.etheria.config.DBConfig;
import com.periferia.etheria.entity.UserEntity;
import com.periferia.etheria.exception.UserException;
import com.periferia.etheria.repository.impl.UserRepositoryImpl;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

	@Mock
	private DBConfig dataBaseConnection;
	@Mock
	private Connection connection;
	@Mock
	private PreparedStatement preparedStatement;
	@Mock
	private ResultSet resultSet;

	@InjectMocks
	private UserRepositoryImpl userRepositoryImpl;
	private UserEntity userEntity;

	@BeforeEach
	void setUp() throws Exception {
		userEntity = new UserEntity(
				"1088070080", 
				"Jose Miguel", 
				"Puentes Mosqueraa", 
				"joseMosquera.a@periferi-it.com", 
				"1243JoMo", 
				"usuario", 
				null);

		when(dataBaseConnection.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
	}

	@Test
	void testFindByEmail_UserExists() throws Exception {
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);
		when(resultSet.getString("cedula")).thenReturn("123");
		when(resultSet.getString("first_name")).thenReturn("John");
		when(resultSet.getString("last_name")).thenReturn("Doe");
		when(resultSet.getString("email")).thenReturn("john@example.com");
		when(resultSet.getString("password")).thenReturn("password123");
		when(resultSet.getString("role")).thenReturn("USER");
		when(resultSet.getString("image")).thenReturn("image.png");

		Optional<UserEntity> result = userRepositoryImpl.findByEmail("john@example.com");

		assertTrue(result.isPresent());
		assertEquals("123", result.get().getCedula());
		verify(preparedStatement, times(1)).setString(1, "john@example.com");

	}

	@Test
	void testFindByEmail_UserNotFound() throws Exception {
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(false);

		Optional<UserEntity> result = userRepositoryImpl.findByEmail("unknown@example.com");

		assertTrue(result.isPresent());
		assertNull(result.get().getCedula());

	}

	@Test
	void testExistsById_UserExists() throws Exception {
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);

		boolean exists = userRepositoryImpl.existsById("123");

		assertTrue(exists);
		verify(preparedStatement, times(1)).setString(1, "123");

	}

	@Test
	void testExistsById_UserNotExists() throws Exception {
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(false);

		boolean exists = userRepositoryImpl.existsById("999");

		assertFalse(exists);

	}

	@Test
	void testSave_UserInserted() throws Exception {
		when(preparedStatement.executeUpdate()).thenReturn(1);

		UserEntity saved = userRepositoryImpl.save(userEntity);

		assertNotNull(saved);
		assertEquals("1088070080", saved.getCedula());

	}

	@Test
	void testUpdate_UserUpdated() throws Exception {
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);

		when(resultSet.getString("cedula")).thenReturn("123");
		when(resultSet.getString("first_name")).thenReturn("John");
		when(resultSet.getString("last_name")).thenReturn("Doe");
		when(resultSet.getString("email")).thenReturn("john@example.com");
		when(resultSet.getString("password")).thenReturn("password123");
		when(resultSet.getString("role")).thenReturn("USER");
		when(resultSet.getString("image")).thenReturn("image.png");

		UserEntity updated = userRepositoryImpl.update(userEntity);

		assertNotNull(updated);
		assertEquals("123", updated.getCedula());
		verify(connection, times(1)).commit();

	}

	@Test
	void testUpdate_UserNotUpdated() throws Exception {
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(false);

		assertThrows(UserException.class, () -> userRepositoryImpl.update(userEntity));

	}
}