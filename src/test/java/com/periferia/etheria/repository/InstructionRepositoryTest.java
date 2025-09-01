package com.periferia.etheria.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.periferia.etheria.config.DBConfig;
import com.periferia.etheria.entity.InstructionEntity;
import com.periferia.etheria.exception.UserException;
import com.periferia.etheria.repository.impl.InstructionRepositoryImpl;

@ExtendWith(MockitoExtension.class)
class InstructionRepositoryTest {

	@Mock
	private DBConfig dbConection;
	@Mock
	private Connection connection;
	@Mock
	private PreparedStatement preparedStatement;
	@Mock
	private ResultSet resultSet;

	@InjectMocks
	private InstructionRepositoryImpl instructionRepositoryImpl;

	@BeforeEach
	void setUp() {


		when(dbConection.getConnection()).thenReturn(connection);
	}

	@Test
	void testDeleteInstructionSucces() throws Exception {
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

		instructionRepositoryImpl.deleteInstruction(1L, "1088970618");

		verify(preparedStatement).setLong(1, 1L);
		verify(preparedStatement).setString(2, "1088970618");
		verify(preparedStatement).executeUpdate();

	}

	@Test
	void testDeleteInstructionThrowsException() throws Exception {
		when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

		UserException exception = assertThrows(UserException.class,
				() -> instructionRepositoryImpl.deleteInstruction(1L, "user123"));

		assertTrue(exception.getMessage().contains("DB error"));

	}

	@Test
	void testCreateInstructionSuccess() throws Exception {
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

		InstructionEntity entity = new InstructionEntity();
		entity.setName("Test name");
		entity.setInstruction("Do this");
		entity.setDescription("desc");
		entity.setGeneral(true);
		entity.setIdUser("user123");

		instructionRepositoryImpl.createInstruction(entity);

		verify(connection).setAutoCommit(false);
		verify(preparedStatement).setString(1, "Test name");
		verify(preparedStatement).setString(2, "Do this");
		verify(preparedStatement).setString(3, "desc");
		verify(preparedStatement).setBoolean(4, true);
		verify(preparedStatement).setString(5, "user123");
		verify(connection).commit();
	}

	@Test
	void testUpdateInstructionSuccess() throws Exception {
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

		InstructionEntity entity = new InstructionEntity();
		entity.setId(1L);
		entity.setName("Updated name");
		entity.setInstruction("Updated instruction");
		entity.setDescription("Updated desc");
		entity.setGeneral(false);
		entity.setIdUser("user123");

		instructionRepositoryImpl.updateInstruction(entity);

		verify(preparedStatement).setString(1, "Updated name");
		verify(preparedStatement).setString(2, "Updated instruction");
		verify(preparedStatement).setString(3, "Updated desc");
		verify(preparedStatement).setBoolean(4, false);
		verify(preparedStatement).setString(5, "user123");
		verify(preparedStatement).setLong(6, 1L);
		verify(connection).commit();
	}

	@Test
	void testGetInstructionsSuccess() throws Exception {
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);

		when(resultSet.next()).thenReturn(true, false);
		when(resultSet.getLong("id")).thenReturn(1L);
		when(resultSet.getString("description")).thenReturn("desc");
		when(resultSet.getString("instruction")).thenReturn("instr");
		when(resultSet.getBoolean("general")).thenReturn(true);
		when(resultSet.getString("name")).thenReturn("Test name");
		when(resultSet.getString("id_user")).thenReturn("user123");
		when(connection.prepareStatement(contains("general"))).thenReturn(preparedStatement);

		var result = instructionRepositoryImpl.getInstructions("user123");

		assertEquals(1, result.size());
		assertEquals("Test name", result.get(0).getName());
	}

	@Test
	void testGetInstructionsThrowsException() throws Exception {
		when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

		assertThrows(UserException.class, () -> instructionRepositoryImpl.getInstructions("user123"));
	}
}