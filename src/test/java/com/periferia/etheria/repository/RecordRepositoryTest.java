package com.periferia.etheria.repository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.periferia.etheria.config.DBConfig;
import com.periferia.etheria.entity.RecordEntity;
import com.periferia.etheria.entity.TitleRecordEntity;
import com.periferia.etheria.exception.UserException;
import com.periferia.etheria.repository.impl.RecordRepositoryImpl;

@ExtendWith(MockitoExtension.class)
class RecordRepositoryTest {

	@Mock
	private DBConfig dbConfig;
	@Mock
	private Connection connection;
	@Mock
	private PreparedStatement preparedStatement;
	@Mock
	private ResultSet resultSet;

	@InjectMocks
	private RecordRepositoryImpl recordRepositoryImpl;
	private List<TitleRecordEntity> titleRecordsEntity;
	private TitleRecordEntity titleRecordEntity;
	private List<RecordEntity> recordsEntity;
	private RecordEntity recordEntity;

	@BeforeEach
	void setUp() {
		titleRecordEntity = new TitleRecordEntity();
		titleRecordEntity.setId(1L);
		titleRecordEntity.setTitle("Titulo nuevo");
		titleRecordEntity.setDateCreate(LocalDate.now());
		titleRecordEntity.setIdUser("1088970080");
		titleRecordEntity.setUuid("11kp0sos00s000s0l");
		titleRecordEntity.setAgent("Claude-agent");

		recordEntity = new RecordEntity();
		recordEntity.setId(1L);
		recordEntity.setQuestion("dame un ejemplo de html");
		recordEntity.setResponse("No puedo responder en el momento");
		recordsEntity = new ArrayList<>();
		recordsEntity.add(recordEntity);
		titleRecordsEntity = new ArrayList<>();
		titleRecordsEntity.add(titleRecordEntity);

		when(dbConfig.getConnection()).thenReturn(connection);
	}

	@Test
	void testGetRecordsSucces() throws Exception {
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true, false);
		when(resultSet.getLong("id_title")).thenReturn(titleRecordEntity.getId());
		when(resultSet.getString("title")).thenReturn(titleRecordEntity.getTitle());
		when(resultSet.getDate("date_create")).thenReturn(Date.valueOf(titleRecordEntity.getDateCreate()));
		when(resultSet.getString("id_user")).thenReturn(titleRecordEntity.getIdUser());
		when(resultSet.getString("uuid")).thenReturn(titleRecordEntity.getUuid());
		when(resultSet.getString("agent")).thenReturn(titleRecordEntity.getAgent());
		when(resultSet.getLong("id_record")).thenReturn(recordEntity.getId());
		when(resultSet.getString("question")).thenReturn(recordEntity.getQuestion());
		when(resultSet.getString("response")).thenReturn(recordEntity.getResponse());

		List<TitleRecordEntity> titleRecordResponse = recordRepositoryImpl.getRecords(titleRecordEntity.getIdUser());

		assertNotNull(titleRecordResponse);
		assertEquals("1088970080", titleRecordEntity.getIdUser());
	}

	@Test
	void testSaveRecordSucces() throws Exception {
		when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
		when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);
		when(resultSet.getLong(1)).thenReturn(1L);

		RecordEntity response = recordRepositoryImpl.saveRecords(recordEntity.getQuestion(), recordEntity.getResponse());

		assertNotNull(response);
		assertEquals(1L, response.getId());
		assertEquals(recordEntity.getQuestion(), response.getQuestion());		
	}

	@Test
	void testExistByIdSucces() throws Exception {
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);

		boolean response = recordRepositoryImpl.existById(titleRecordEntity.getIdUser());

		assertTrue(response);
	}

	@Test
	void testGetRecordSucces() throws Exception {
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);
		when(resultSet.getLong("id")).thenReturn(recordEntity.getId());
		when(resultSet.getString("question")).thenReturn(recordEntity.getQuestion());
		when(resultSet.getString("response")).thenReturn(recordEntity.getResponse());

		RecordEntity response = recordRepositoryImpl.getRecord(1L);

		assertNotNull(response);
	}

	@Test
	void testDeleteById() throws Exception {
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		doNothing().when(preparedStatement).setString(anyInt(), anyString());
		when(preparedStatement.executeUpdate()).thenReturn(1);

		assertDoesNotThrow(() -> recordRepositoryImpl.deleteById(titleRecordEntity.getUuid()));
	}

	@Test
	void testUpdateTitleRecord_success() throws Exception {
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);
		when(resultSet.getLong("id")).thenReturn(1L);
		when(resultSet.getString("title")).thenReturn("Nuevo Título");
		when(resultSet.getDate("date_create")).thenReturn(Date.valueOf(LocalDate.now()));
		when(resultSet.getString("id_user")).thenReturn("123");
		when(resultSet.getString("uuid")).thenReturn("uuid-123");

		TitleRecordEntity updated = recordRepositoryImpl.updateTitleRecord(1L, "Nuevo Título");

		assertNotNull(updated);
		assertEquals("Nuevo Título", updated.getTitle());
	}

	@Test
	void testUpdateTitleRecord_notFound() throws Exception {
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(false);

		assertThrows(UserException.class, () -> 
		recordRepositoryImpl.updateTitleRecord(99L, "xxx"));
	}

}
