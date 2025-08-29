package com.periferia.etheria.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import com.periferia.etheria.entity.RecordUserEntity;
import com.periferia.etheria.entity.TitleRecordEntity;
import com.periferia.etheria.repository.impl.RecordRepositoryImpl;
import com.periferia.etheria.repository.impl.RecordUserRepositoryImpl;

@ExtendWith(MockitoExtension.class)
public class RecordUserRepositoryTest {

	@Mock
	private DBConfig dataBaseConnection;
	@Mock
	private Connection connection;
	@Mock
	private PreparedStatement preparedStatement;
	@Mock
	private ResultSet resultSet;
	@Mock
	private RecordRepositoryImpl recordRepositoryImpl;

	@InjectMocks
	private RecordUserRepositoryImpl recordUserRepositoryImpl;
	private TitleRecordEntity titleRecordUserEntity;
	private List<RecordEntity> recordsEntity;
	private RecordEntity recordEntity;

	@BeforeEach
	void setUp() throws Exception {
		titleRecordUserEntity = new TitleRecordEntity();
		titleRecordUserEntity.setAgent("Claude-agent");
		titleRecordUserEntity.setId(1L);
		titleRecordUserEntity.setIdUser("1088970080");
		titleRecordUserEntity.setDateCreate(LocalDate.now());
		titleRecordUserEntity.setTitle("Titulo nuevo");
		titleRecordUserEntity.setUuid("11kp0sos00s000s0l");

		recordsEntity = new ArrayList<>();
		recordEntity = new RecordEntity();
		recordEntity.setId(1L);
		recordEntity.setQuestion("dame un ejemplo de html");
		recordEntity.setResponse("No puedo responder en el momento");
		recordsEntity.add(recordEntity);
		titleRecordUserEntity.setRecordEntity(recordsEntity);

		when(dataBaseConnection.getConnection()).thenReturn(connection);
	}

	@Test
	void testGetTitleRecordSucces() throws Exception {
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);
		when(resultSet.getLong("id")).thenReturn(1L);
		when(resultSet.getString("title")).thenReturn("Titulo nuevo");
		when(resultSet.getDate("date_create")).thenReturn(Date.valueOf(LocalDate.now()));
		when(resultSet.getString("id_user")).thenReturn("1088970080");
		when(resultSet.getString("uuid")).thenReturn("11kp0sos00s000s0l");

		TitleRecordEntity response = recordUserRepositoryImpl.getTitleRecord("Titulo nuevo", "1088970080");

		assertNotNull(response);
		assertEquals("1088970080", response.getIdUser());
		assertEquals("Titulo nuevo", response.getTitle());
		verify(preparedStatement, times(1)).setString(1, titleRecordUserEntity.getTitle());
		verify(preparedStatement, times(1)).setString(2, titleRecordUserEntity.getIdUser());

	}

	@Test
	void testGetTitleRecordNoFound() throws Exception {
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(false);

		TitleRecordEntity response = recordUserRepositoryImpl.getTitleRecord("Titulo nuevo", "1088970080");

		assertNotNull(response);
		verify(preparedStatement, times(1)).setString(1, titleRecordUserEntity.getTitle());
		verify(preparedStatement, times(1)).setString(2, titleRecordUserEntity.getIdUser());

	}

	@Test
	void testSaveTitleRecordSucces() throws Exception {
		when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
		when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
		when(recordRepositoryImpl.saveRecords(recordEntity.getQuestion(), recordEntity.getResponse())).thenReturn(recordEntity);
		when(resultSet.next()).thenReturn(true);
		when(resultSet.getLong(1)).thenReturn(1L);		
		when(resultSet.getString(2)).thenReturn("Titulo nuevo");
		when(resultSet.getDate(3)).thenReturn(Date.valueOf(LocalDate.now()));
		when(resultSet.getString(4)).thenReturn("1088970080");
		when(resultSet.getString(5)).thenReturn("11kp0sos00s000s0l");
		when(resultSet.getString(6)).thenReturn("Claude-agent");

		TitleRecordEntity response = recordUserRepositoryImpl.saveTitleRecordEntity(
				titleRecordUserEntity.getTitle(), titleRecordUserEntity.getIdUser(), 
				titleRecordUserEntity.getUuid(), recordEntity.getQuestion(), 
				recordEntity.getResponse(), titleRecordUserEntity.getAgent());

		assertNotNull(response);
		assertEquals(response.getId(), response.getId());
		verify(preparedStatement, times(1)).executeUpdate();
		verify(recordRepositoryImpl, times(1)).saveRecords(recordEntity.getQuestion(), recordEntity.getResponse());

	}

	@Test
	void testRecordUserSucces() throws Exception {
		when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
		when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);
		when(resultSet.getLong(1)).thenReturn(1L);
		when(resultSet.getLong(2)).thenReturn(1L);
		when(resultSet.getString(3)).thenReturn("Titulo nuevo");

		RecordUserEntity response = recordUserRepositoryImpl.saveRecordUser(recordEntity.getId(), titleRecordUserEntity.getId());

		assertNotNull(response);
		verify(preparedStatement, times(1)).executeUpdate();

	}

}
