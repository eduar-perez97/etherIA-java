package com.periferia.etheria.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FilesDtoTest {

	@Test
	void testSettersAndGetters() {
		FilesDto dto = new FilesDto();

		dto.setFile("archivo-base64");
		dto.setFileName("documento.pdf");

		assertEquals("archivo-base64", dto.getFile());
		assertEquals("documento.pdf", dto.getFileName());
	}
}