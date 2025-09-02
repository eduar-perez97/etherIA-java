package com.periferia.etheria.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecordDtoTest {

	@Test
	void testConstructorAndGetters() {
		RecordDto dto = new RecordDto(1L, "¿Cuál es la capital de Francia?", "París");

		assertEquals(1L, dto.getId());
		assertEquals("¿Cuál es la capital de Francia?", dto.getQuestion());
		assertEquals("París", dto.getResponse());
	}

	@Test
	void testSettersAndGetters() {
		RecordDto dto = new RecordDto(2L, "pregunta inicial", "respuesta inicial");

		dto.setId(10L);
		dto.setQuestion("Nueva pregunta");
		dto.setResponse("Nueva respuesta");

		assertEquals(10L, dto.getId());
		assertEquals("Nueva pregunta", dto.getQuestion());
		assertEquals("Nueva respuesta", dto.getResponse());
	}
}
