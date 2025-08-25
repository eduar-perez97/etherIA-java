package com.periferia.etheria.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.periferia.etheria.dto.RecordDto;
import com.periferia.etheria.dto.TitleRecordDto;
import com.periferia.etheria.entity.RecordEntity;
import com.periferia.etheria.entity.TitleRecordEntity;
import com.periferia.etheria.exception.UserException;
import com.periferia.etheria.repository.RecordRepository;
import com.periferia.etheria.security.JwtService;
import com.periferia.etheria.service.impl.RecordServiceImpl;
import com.periferia.etheria.util.Response;

@ExtendWith(MockitoExtension.class)
public class RecordServiceTest {

	@Mock
	private RecordRepository recordRepository;
	@Mock
	private JwtService jwtService;
	@InjectMocks
	private TitleRecordDto titleRecordDto;
	private RecordServiceImpl recordService;
	private RecordDto recordDto;

	@BeforeEach
	void setUp() {
		recordService = new RecordServiceImpl(recordRepository, jwtService);

		List<RecordDto> listRecords = new ArrayList<>();
		recordDto = new RecordDto(null, null, null);
		recordDto.setId(1L);
		recordDto.setQuestion("Hola Claude, ayudame describiendo cuales son las ciudades principales del mundo");
		recordDto.setResponse("Las ciudades más importantes del mundo varían según el criterio utilizado, ya sea población, influencia económica, innovación o cultura, pero en general se destacan Tokio, Nueva York, Londres, París y Shanghái");
		listRecords.add(recordDto);

		titleRecordDto = new TitleRecordDto();
		titleRecordDto.setId(1L);
		titleRecordDto.setTitle("Nuevo titulo para la test");
		titleRecordDto.setDateCreate(LocalDate.now());
		titleRecordDto.setIdUser("1088976287");
		titleRecordDto.setUuid("9ff98wj27sX9f0");
		titleRecordDto.setRecordDto(listRecords);
	}

	@Test
	void testConsultRecordsSucces() {
		List<TitleRecordEntity> entityList = new ArrayList<>();
		TitleRecordEntity entity = new TitleRecordEntity();
		entity.setId(1L);
		entity.setTitle("Titulo guardado");
		entity.setIdUser("1088976287");

		List<RecordEntity> recordEntityList = new ArrayList<>();
		RecordEntity record = new RecordEntity();
		record.setId(1L);
		record.setQuestion("Hola Claude, ayudame describiendo cuales son las ciudades principales del mundo");
		record.setResponse("Las ciudades más importantes del mundo varían según el criterio utilizado, ya sea población, influencia económica, innovación o cultura, pero en general se destacan Tokio, Nueva York, Londres, París y Shanghái");
		recordEntityList.add(record);

		entity.setRecordEntity(recordEntityList);
		entityList.add(entity);

		when(jwtService.validateToken("fake.jwt.token")).thenReturn(true);		
		when(recordRepository.getRecords(titleRecordDto.getIdUser())).thenReturn(entityList);

		Response<List<TitleRecordDto>> response = recordService.consultRecords(titleRecordDto.getIdUser(), "Bearer fake.jwt.token");

		assertNotNull(response);
		assertEquals(200, response.getStatusCode());
		assertEquals("Registro consultado con exito", response.getMessage());
		assertFalse(response.getData().isEmpty());
	}

	@Test
	void testConsultRecordsInvalidToken() {
		when(jwtService.validateToken("fake.jwt.token")).thenReturn(false);

		UserException ex = assertThrows(UserException.class, () -> 
		recordService.consultRecords(titleRecordDto.getIdUser(), "Bearer fake.jwt.token"));

		assertTrue(ex.getMessage().contains("Contraseña incorrecta"));
	}

	@Test
	void testSaveRecordSuccess() {
		when(recordRepository.saveRecords(recordDto.getQuestion(), recordDto.getResponse())).thenReturn(new RecordEntity());

		RecordDto result = recordService.saveRecord(recordDto.getQuestion(), recordDto.getResponse());

		assertNotNull(result);
	}

	@Test
	void testSaveRecordNullQuestion() {
		UserException ex = assertThrows(UserException.class, () -> 
		recordService.saveRecord(null, recordDto.getResponse()));

		assertTrue(ex.getMessage().contains("Los datos son obligatorios"));
	}

	@Test
	void testDeleteRecordNotExists() {
		when(jwtService.validateToken("fake.jwt.token")).thenReturn(true);
		when(recordRepository.existByModule("uuid123")).thenReturn(false);

		Response<Boolean> response = recordService.deleteRecord("uuid123", "Bearer fake.jwt.token");

		assertEquals(200, response.getStatusCode());
		assertFalse(response.getData());
	}

	@Test
	void testDeleteRecordSuccess() {
		when(jwtService.validateToken("fake.jwt.token")).thenReturn(true);
		when(recordRepository.existByModule("uuid123")).thenReturn(true);

		Response<Boolean> response = recordService.deleteRecord("uuid123", "Bearer fake.jwt.token");

		assertEquals(200, response.getStatusCode());
		assertTrue(response.getData());
		verify(recordRepository).deleteById("uuid123");
	}

	@Test
	void testUpdateTitleRecordSuccess() {
		when(jwtService.validateToken("fake.jwt.token")).thenReturn(true);
		TitleRecordEntity entity = new TitleRecordEntity();
		entity.setId(1L);
		entity.setTitle("Nuevo Titulo");

		when(recordRepository.updateTitleRecord(1L, "Nuevo Titulo")).thenReturn(entity);

		Response<TitleRecordDto> response = recordService.updateTitleRecord(1L, "Nuevo Titulo", "Bearer fake.jwt.token");

		assertEquals(200, response.getStatusCode());
		assertEquals("Nuevo Titulo", response.getData().getTitle());
	}

}

