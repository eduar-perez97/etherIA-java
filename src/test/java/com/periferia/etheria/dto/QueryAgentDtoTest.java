package com.periferia.etheria.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class QueryAgentDtoTest {

	@Test
	void testSettersAndGetters() {
		QueryAgentDto dto = new QueryAgentDto();

		dto.setModel("gpt-4");
		dto.setAgentId("agent_123");
		dto.setQuestion("¿Cuál es la capital de Francia?");
		dto.setResponse("París");
		dto.setUuid("uuid-001");
		dto.setCedula("123456");
		dto.setTitle("Consulta de prueba");

		assertEquals("gpt-4", dto.getModel());
		assertEquals("agent_123", dto.getAgentId());
		assertEquals("¿Cuál es la capital de Francia?", dto.getQuestion());
		assertEquals("París", dto.getResponse());
		assertEquals("uuid-001", dto.getUuid());
		assertEquals("123456", dto.getCedula());
		assertEquals("Consulta de prueba", dto.getTitle());
	}
}