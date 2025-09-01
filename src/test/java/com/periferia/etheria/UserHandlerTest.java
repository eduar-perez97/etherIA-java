package com.periferia.etheria;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.periferia.etheria.config.DBConfig;
import com.periferia.etheria.entity.InstructionEntity;
import com.periferia.etheria.exception.UserException;
import com.periferia.etheria.repository.impl.InstructionRepositoryImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserHandlerTest {
	@Mock
	private DBConfig dbConfig;
	@Mock
	private Connection connection;
	@Mock
	private PreparedStatement preparedStatement;
	@Mock
	private ResultSet resultSet;
	@Mock
	private InstructionRepositoryImpl repository;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		repository = new InstructionRepositoryImpl(dbConfig);

		when(dbConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
	}

	@Test
	void testDeleteInstruction_Success() throws Exception {
		when(preparedStatement.executeUpdate()).thenReturn(1);

		assertDoesNotThrow(() -> repository.deleteInstruction(1L, "user123"));

		verify(preparedStatement).setLong(1, 1L);
		verify(preparedStatement).setString(2, "user123");
		verify(preparedStatement).executeUpdate();
	}

	@Test
	void testDeleteInstruction_Failure() throws Exception {
		when(preparedStatement.executeUpdate()).thenThrow(new SQLException("fail"));

		UserException ex = assertThrows(UserException.class,
				() -> repository.deleteInstruction(1L, "user123"));

		assertTrue(ex.getMessage().contains("Error SQL: Durante la eliminación de la instrucción: fail"));
	}

	@Test
	void testCreateInstruction_Success() throws Exception {
		InstructionEntity instruction = new InstructionEntity();
		instruction.setName("name");
		instruction.setInstruction("inst");
		instruction.setDescription("desc");
		instruction.setGeneral(true);
		instruction.setIdUser("user123");

		when(preparedStatement.executeUpdate()).thenReturn(1);

		repository.createInstruction(instruction);

		verify(connection).setAutoCommit(false);
		verify(preparedStatement).setString(1, "name");
		verify(preparedStatement).setString(2, "inst");
		verify(preparedStatement).setString(3, "desc");
		verify(preparedStatement).setBoolean(4, true);
		verify(preparedStatement).setString(5, "user123");
		verify(connection).commit();
	}

	@Test
	void testCreateInstruction_Failure() throws Exception {
		InstructionEntity instruction = new InstructionEntity();
		instruction.setName("test");
		instruction.setGeneral(Boolean.FALSE);

		when(preparedStatement.executeUpdate()).thenThrow(new SQLException("insert fail"));

		assertThrows(UserException.class, () -> repository.createInstruction(instruction));

	}

	@Test
	void testUpdateInstruction_Success() throws Exception {
		InstructionEntity instruction = new InstructionEntity();
		instruction.setId(10L);
		instruction.setName("name");
		instruction.setInstruction("inst");
		instruction.setDescription("desc");
		instruction.setGeneral(true);
		instruction.setIdUser("user123");

		when(preparedStatement.executeUpdate()).thenReturn(1);

		repository.updateInstruction(instruction);

		verify(preparedStatement).setString(1, "name");
		verify(preparedStatement).setString(2, "inst");
		verify(preparedStatement).setString(3, "desc");
		verify(preparedStatement).setBoolean(4, true);
		verify(preparedStatement).setString(5, "user123");
		verify(preparedStatement).setLong(6, 10L);
		verify(connection).commit();
	}

	@Test
	void testUpdateInstruction_Failure() throws Exception {
		InstructionEntity instruction = new InstructionEntity();
		instruction.setId(10L);
		instruction.setName("Test name");
		instruction.setInstruction("Some SQL instruction");
		instruction.setDescription("Some description");
		instruction.setGeneral(Boolean.FALSE);
		instruction.setIdUser("user123");

		when(preparedStatement.executeUpdate()).thenThrow(new SQLException("update fail"));

		assertThrows(UserException.class, () -> repository.updateInstruction(instruction));
	}

	@Test
	void testGetInstructions_Success() throws Exception {
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true, false);
		when(resultSet.getLong("id")).thenReturn(1L);
		when(resultSet.getString("description")).thenReturn("desc");
		when(resultSet.getString("instruction")).thenReturn("inst");
		when(resultSet.getBoolean("general")).thenReturn(true);
		when(resultSet.getString("name")).thenReturn("name");
		when(resultSet.getString("id_user")).thenReturn("user123");

		List<InstructionEntity> result = repository.getInstructions("user123");

		assertEquals(1, result.size());
		assertEquals("name", result.get(0).getName());
	}

	@Test
	void testGetInstructions_Failure() throws Exception {
		when(preparedStatement.executeQuery()).thenThrow(new SQLException("query fail"));

		assertThrows(UserException.class, () -> repository.getInstructions("user123"));
	}

	@Test
	void testGetInstructionsGeneral_Success() throws Exception {
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true, false);
		when(resultSet.getLong("id")).thenReturn(2L);
		when(resultSet.getString("description")).thenReturn("desc2");
		when(resultSet.getString("instruction")).thenReturn("inst2");
		when(resultSet.getBoolean("general")).thenReturn(true);
		when(resultSet.getString("name")).thenReturn("name2");
		when(resultSet.getString("id_user")).thenReturn("user2");

		List<InstructionEntity> baseList = new java.util.ArrayList<>();
		List<InstructionEntity> result = repository.getInstructionsGeneral(baseList);

		assertEquals(1, result.size());
		assertEquals("name2", result.get(0).getName());
	}

	@Test
	void testGetInstructionsGeneral_Failure() throws Exception {
		when(dbConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenThrow(new SQLException("query fail"));

		assertThrows(UserException.class, () -> repository.getInstructionsGeneral(new ArrayList<>()));
	}
}