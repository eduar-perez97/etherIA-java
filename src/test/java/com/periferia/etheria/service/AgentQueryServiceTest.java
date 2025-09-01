package com.periferia.etheria.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.periferia.etheria.dto.QueryAgentDto;
import com.periferia.etheria.dto.RecordDto;
import com.periferia.etheria.dto.TitleRecordDto;
import com.periferia.etheria.entity.RecordEntity;
import com.periferia.etheria.entity.TitleRecordEntity;
import com.periferia.etheria.repository.RecordUserRepository;
import com.periferia.etheria.security.JwtService;
import com.periferia.etheria.service.impl.AgentQueryServiceImpl;
import com.periferia.etheria.util.Response;

@ExtendWith(MockitoExtension.class)
class AgentQueryServiceTest {

	@Mock
	private JwtService jwtService;
	@Mock
	private RecordUserRepository recordUserRepository;

	@InjectMocks
	private AgentQueryServiceImpl agentQueryServiceImpl;
	private QueryAgentDto queryAgentDto;
	private TitleRecordEntity titleRecordEntity;
	private RecordEntity recordEntity;

	@BeforeEach
	void setUp() {
		queryAgentDto = new QueryAgentDto();
		queryAgentDto.setModel("Claude-agent");
		queryAgentDto.setQuestion("Dime como puedo hacer una funcion en python que me aumente la resolucion de una imagen");
		queryAgentDto.setResponse("No tengo respuesta para esto");
		queryAgentDto.setUuid("J7huh7hu22jjd88dj8d");
		queryAgentDto.setCedula("1088970080");
		queryAgentDto.setTitle("Solicitud de agente: team_datastage_documentation");

		titleRecordEntity = new TitleRecordEntity();
		titleRecordEntity.setAgent("Claude-agent");
		titleRecordEntity.setId(3L);
		titleRecordEntity.setIdUser("1088970080");
		titleRecordEntity.setUuid("77ss6fe982h02j923j3h");

		List<RecordEntity> recordsEntity = new ArrayList<>();
		recordEntity = new RecordEntity();
		recordEntity.setId(1L);
		recordEntity.setQuestion("Dime como puedo hacer una funcion en python que me aumente la resolucion de una imagen");
		recordsEntity.add(recordEntity);

		titleRecordEntity.setRecordEntity(recordsEntity);

	}

	@Test
	void TestRequestQueryNoExistTitleSucces() {
		String token = "Bearer jwt-fake-token";

		when(jwtService.validateToken(token.substring(7))).thenReturn(true);
		when(recordUserRepository.getTitleRecord(queryAgentDto.getUuid(), queryAgentDto.getCedula())).thenReturn(titleRecordEntity);

		TitleRecordDto titleSaved = new TitleRecordDto();
		titleSaved.setId(1L);

		List<RecordDto> recordsDto = new ArrayList<>();
		RecordDto recordDto = new RecordDto(null, null, null);
		recordDto.setId(2L);
		recordsDto.add(recordDto);

		titleSaved.setRecordDto(recordsDto);

		when(recordUserRepository.saveTitleRecordEntity(
				queryAgentDto.getTitle(), 
				queryAgentDto.getCedula(), 
				queryAgentDto.getUuid(), 
				queryAgentDto.getQuestion(), 
				queryAgentDto.getResponse(), 
				queryAgentDto.getModel())).thenReturn(titleRecordEntity);

		Response<TitleRecordDto> response = agentQueryServiceImpl.requestQuery(token, queryAgentDto);

		assertNotNull(response);
		assertEquals(200, response.getStatusCode());
		assertEquals("Se guarda el historial con exito...", response.getMessage());
		verify(recordUserRepository, times(1)).saveTitleRecordEntity(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());

	}

	@Test
	void TestRequestQueryInvalidToken() {
		String token = "Bearer jwt-fake-token";

		when(jwtService.validateToken(token.substring(7))).thenReturn(false);

		Response<TitleRecordDto> response = agentQueryServiceImpl.requestQuery(token, queryAgentDto);

		assertNotNull(response);
		assertEquals(401, response.getStatusCode());
		assertEquals("Token no valido para la sesión", response.getMessage());
	}

}
