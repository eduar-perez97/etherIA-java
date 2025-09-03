package com.periferia.etheria.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class TitleRecordDtoTest {

	@Test
	void testGettersAndSetters() {
		// Arrange
		TitleRecordDto dto = new TitleRecordDto();
		LocalDate now = LocalDate.now();
		RecordDto recordDto = new RecordDto(null, null, null);

		// Act
		dto.setId(1L);
		dto.setTitle("Test Title");
		dto.setDateCreate(now);
		dto.setIdUser("user123");
		dto.setUuid("uuid-1234");
		dto.setAgent("agentX");
		dto.setRecordDto(List.of(recordDto));

		// Assert
		assertEquals(1L, dto.getId());
		assertEquals("Test Title", dto.getTitle());
		assertEquals(now, dto.getDateCreate());
		assertEquals("user123", dto.getIdUser());
		assertEquals("uuid-1234", dto.getUuid());
		assertEquals("agentX", dto.getAgent());
		assertNotNull(dto.getRecordDto());
		assertEquals(1, dto.getRecordDto().size());
		assertSame(recordDto, dto.getRecordDto().get(0));
	}

	@Test
	void testJsonIncludeAnnotation() throws Exception {
		TitleRecordDto dto = new TitleRecordDto(); 
		dto.setId(1L);

		String json = new ObjectMapper().writeValueAsString(dto);

		assertTrue(json.contains("id"));
		assertFalse(json.contains("title"));
		assertFalse(json.contains("dateCreate"));
		assertFalse(json.contains("idUser"));
		assertFalse(json.contains("uuid"));
		assertFalse(json.contains("agent"));
		assertFalse(json.contains("recordDto"));
	}
}
