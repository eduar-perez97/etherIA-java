package com.periferia.etheria.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResponseAgentDtoTest {

	@Test
	void testGettersAndSetters() {
		// Arrange
		ResponseAgentDto dto = new ResponseAgentDto();
		TitleRecordDto title = new TitleRecordDto();
		RecordDto recordDto = new RecordDto(null, null, null);

		// Act
		dto.setTitleDto(List.of(title));
		dto.setRecordDto(List.of(recordDto));

		// Assert
		assertNotNull(dto.getTitleDto());
		assertEquals(1, dto.getTitleDto().size());
		assertSame(title, dto.getTitleDto().get(0));

		assertNotNull(dto.getRecordDto());
		assertEquals(1, dto.getRecordDto().size());
		assertSame(recordDto, dto.getRecordDto().get(0));
	}

	@Test
	void testJsonIncludeAnnotation() throws Exception {
		ResponseAgentDto dto = new ResponseAgentDto();
		String json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(dto);

		assertFalse(json.contains("titleDto"));
		assertFalse(json.contains("recordDto"));
	}
}
