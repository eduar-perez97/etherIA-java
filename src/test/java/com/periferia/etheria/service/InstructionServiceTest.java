package com.periferia.etheria.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.periferia.etheria.dto.InstructionDto;
import com.periferia.etheria.entity.InstructionEntity;
import com.periferia.etheria.exception.UserException;
import com.periferia.etheria.repository.InstructionRepository;
import com.periferia.etheria.security.JwtService;
import com.periferia.etheria.service.impl.InstructionServiceImpl;
import com.periferia.etheria.util.Response;

@ExtendWith(MockitoExtension.class)
public class InstructionServiceTest {

	@Mock
	private InstructionRepository instructionRepository;
	@Mock
	private JwtService jwtService;

	@InjectMocks
	private InstructionServiceImpl instructionService;
	private InstructionDto instructionDto;
	private String token;

	@BeforeEach
	void setUp() {
		token = " Bearer fake-jwt-token";

		instructionDto = new InstructionDto();
		instructionDto.setId(1L);
		instructionDto.setName("Instrucciones para el agente claude");
		instructionDto.setInstruction("Te comportarás como un agente experto en finanzas");
		instructionDto.setDescription("Evaluarás cada archivo financiero y darás una respuesta conciza y preciza");
		instructionDto.setGeneral(false);
		instructionDto.setIdUser("1088978987");
		instructionDto.setAction(null);

	}

	@Test
	void testDeleteInstructionSucces() {
		instructionDto.setAction("delete");
		when(jwtService.validateToken(token.substring(7))).thenReturn(true);

		Response<Object> response = instructionService.interactueInstruction(instructionDto, token);

		assertEquals(response.getStatusCode(), 200);
		assertTrue(response.getMessage().contains("con exito"));
		assertEquals(true, response.getData());
		verify(instructionRepository).deleteInstruction(instructionDto.getId(), instructionDto.getIdUser());

	}

	@Test
	void testCreateInstructionSucces() {
		instructionDto.setAction("create");
		when(jwtService.validateToken(token.substring(7))).thenReturn(true);
		when(instructionRepository.getInstructionsGeneral(anyList())).thenAnswer(instructions -> null);

		Response<Object> response = instructionService.interactueInstruction(instructionDto, token);

		assertEquals(200, response.getStatusCode());
		assertTrue(response.getMessage().contains("con exito"));
		assertEquals(true, response.getData());
		verify(instructionRepository).createInstruction(any(InstructionEntity.class));

	}

	@Test
	void testCreateInstructionExist() {
		instructionDto.setAction("create");
		InstructionEntity instructiongeneral = new InstructionEntity();
		instructiongeneral.setName("Instrucciones para el agente claude");

		when(jwtService.validateToken(token.substring(7))).thenReturn(true);

		doAnswer(invocation -> {
			List<InstructionEntity> list = invocation.getArgument(0);
			list.add(instructiongeneral);
			return null;
		}).when(instructionRepository).getInstructionsGeneral(anyList());

		UserException ex = assertThrows(UserException.class, () -> 
		instructionService.interactueInstruction(instructionDto, token));

		assertTrue(ex.getMessage().contains("Ya existe en las instucciones generales"));
		verify(instructionRepository).getInstructionsGeneral(anyList());
		verify(instructionRepository, never()).createInstruction(any());

	}

	@Test
	void testUpdateistructionSucces() {
		instructionDto.setAction("update");
		when(jwtService.validateToken(token.substring(7))).thenReturn(true);

		Response<Object> response = instructionService.interactueInstruction(instructionDto, token);

		assertEquals(200, response.getStatusCode());
		assertTrue(response.getMessage().contains("con exito"));
		assertEquals(true, response.getData());
		verify(instructionRepository).updateInstruction(any(InstructionEntity.class));

	}

	@Test
	void testGetInstructionSucces() {
		instructionDto.setAction("get");
		when(jwtService.validateToken(token.substring(7))).thenReturn(true);
		when(instructionRepository.getInstructions(instructionDto.getIdUser())).thenReturn(Collections.emptyList());

		Response<Object> response = instructionService.interactueInstruction(instructionDto, token);

		assertEquals(200, response.getStatusCode());
		assertTrue(response.getMessage().contains("con exito"));
		verify(instructionRepository).getInstructions(instructionDto.getIdUser());

	}

	@Test
	void testActionInvalidThrowException() {
		instructionDto.setAction("otherAction");
		when(jwtService.validateToken(token.substring(7))).thenReturn(true);

		assertThrows(UserException.class, () -> 
		instructionService.interactueInstruction(instructionDto, token));

	}

	@Test
	void testInvalidTokenThrowException() {
		when(jwtService.validateToken(token.substring(7))).thenReturn(false);

		assertThrows(UserException.class, () -> 
		instructionService.interactueInstruction(instructionDto, token));
	}

}
