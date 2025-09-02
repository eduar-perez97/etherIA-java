package com.periferia.etheria.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class UserDtoTest {

	@Test
	void testGettersAndSetters() {
		UserDto dto = new UserDto();
		RecordDto record = new RecordDto(null, null, null);

		dto.setCedula("12345");
		dto.setIdUser("u001");
		dto.setFirstName("John");
		dto.setLastName("Doe");
		dto.setEmail("john.doe@example.com");
		dto.setPassword("secret");
		dto.setToken("jwt-token");
		dto.setRole("ADMIN");
		dto.setImage("profile.png");
		dto.setAuthType("ENTRA");
		dto.setRecordDto(List.of(record));

		assertEquals("12345", dto.getCedula());
		assertEquals("u001", dto.getIdUser());
		assertEquals("John", dto.getFirstName());
		assertEquals("Doe", dto.getLastName());
		assertEquals("john.doe@example.com", dto.getEmail());
		assertEquals("secret", dto.getPassword());
		assertEquals("jwt-token", dto.getToken());
		assertEquals("ADMIN", dto.getRole());
		assertEquals("profile.png", dto.getImage());
		assertEquals("ENTRA", dto.getAuthType());
		assertNotNull(dto.getRecordDto());
		assertEquals(1, dto.getRecordDto().size());
	}

	@Test
	void testAllArgsConstructor() {
		UserDto dto = new UserDto(
				"12345", 
				"Jane", 
				"Smith", 
				"jane.smith@example.com", 
				"password123", 
				"USER", 
				"avatar.png", 
				"LOCAL"
				);

		assertEquals("12345", dto.getCedula());
		assertEquals("Jane", dto.getFirstName());
		assertEquals("Smith", dto.getLastName());
		assertEquals("jane.smith@example.com", dto.getEmail());
		assertEquals("password123", dto.getPassword());
		assertEquals("USER", dto.getRole());
		assertEquals("avatar.png", dto.getImage());
		assertEquals("LOCAL", dto.getAuthType());

		assertNull(dto.getIdUser());
		assertNull(dto.getToken());
		assertNull(dto.getRecordDto());
	}

	@Test
	void testJsonIncludeAnnotation() throws Exception {
		UserDto dto = new UserDto();
		dto.setCedula("9999");

		String json = new ObjectMapper().writeValueAsString(dto);

		assertTrue(json.contains("cedula"));
		assertFalse(json.contains("idUser"));
		assertFalse(json.contains("firstName"));
		assertFalse(json.contains("lastName"));
		assertFalse(json.contains("email"));
		assertFalse(json.contains("password"));
		assertFalse(json.contains("token"));
		assertFalse(json.contains("role"));
		assertFalse(json.contains("image"));
		assertFalse(json.contains("authType"));
		assertFalse(json.contains("recordDto"));
	}
}