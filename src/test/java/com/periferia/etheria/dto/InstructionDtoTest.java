package com.periferia.etheria.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InstructionDtoTest {

	@Test
	void testSettersAndGetters() {
		InstructionDto dto = new InstructionDto();

		dto.setId(1L);
		dto.setName("Instrucción 1");
		dto.setInstruction("Hacer algo");
		dto.setDescription("Descripción de la instrucción");
		dto.setGeneral(true);
		dto.setIdUser("123456");
		dto.setAction("crear");

		assertEquals(1L, dto.getId());
		assertEquals("Instrucción 1", dto.getName());
		assertEquals("Hacer algo", dto.getInstruction());
		assertEquals("Descripción de la instrucción", dto.getDescription());
		assertTrue(dto.getGeneral());
		assertEquals("123456", dto.getIdUser());
		assertEquals("crear", dto.getAction());
	}
}