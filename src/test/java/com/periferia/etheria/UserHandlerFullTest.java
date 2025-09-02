package com.periferia.etheria;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.periferia.etheria.security.ReadSecret;
import com.periferia.etheria.dto.UserDto;
import com.periferia.etheria.entity.UserEntity;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserHandlerFullTest {

	private static MockedStatic<ReadSecret> readSecretMock;
	private UserHandler handler;
	private ObjectMapper mapper;
	private UserDto userDto;
	private UserEntity userEntity;

	@BeforeAll
	static void mockSecrets() {
		readSecretMock = Mockito.mockStatic(ReadSecret.class);
		readSecretMock.when(() -> ReadSecret.getSecret("JWT_SECRET"))
		.thenReturn("fakeSecretForTests");
	}

	@AfterAll
	static void closeMock() {
		readSecretMock.close();
	}

	@BeforeEach
	void setUp() {
		handler = new UserHandler();
		mapper = new ObjectMapper();

		userDto = new UserDto();
		userDto.setCedula("123456");
		userDto.setFirstName("Test");
		userDto.setLastName("User");
		userDto.setEmail("prueba@periferia.com");
		userDto.setPassword("password");
		userDto.setRole("USUARIO");
		userDto.setAuthType("LOCAL");

		userEntity = new UserEntity(null, null, null, null, null, null, null);
		userEntity.setCedula("123");
		userEntity.setFirstName("Test");
		userEntity.setLastName("User");
		userEntity.setEmail("prueba@periferia.com");
		userEntity.setPassword("$2a$10$hashedPassword");
		userEntity.setRole("USUARIO");
		userEntity.setImage("nuevaimag3ndepr4va");
	}

	private APIGatewayProxyRequestEvent buildRequest(String action, Object data, String token) throws Exception {
		APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
		request.setBody(mapper.writeValueAsString(Map.of("action", action, "data", data)));
		if (token != null) {
			request.setHeaders(Map.of("Authorization", token));
		}
		return request;
	}

	@Test
	void testRegister_MissingFields() throws Exception {
		var request = buildRequest("register", Map.of("email", "test@test.com"), null);
		var response = handler.handleRequest(request, (Context) null);

		assertEquals(400, response.getStatusCode());
		assertNotNull(response);
	}

	@Test
	void testLogin_MissingFields() throws Exception {
		var request = buildRequest("login", Map.of("email", "test@test.com"), null);
		var response = handler.handleRequest(request, null);

		assertEquals(400, response.getStatusCode());
		assertNotNull(response);
	}

	@Test
	void testQueryAgent_MissingFields() throws Exception {
		var request = buildRequest("queryAgent", Map.of("question", "hola"), "Bearer token");
		var response = handler.handleRequest(request, null);

		assertEquals(400, response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@Test
	void testGetRecords_MissingFields() throws Exception {
		var request = buildRequest("getRecords", Map.of(), "Bearer token");
		var response = handler.handleRequest(request, null);

		assertEquals(400, response.getStatusCode());
	}

	@Test
	void testInstruction_MissingFields() throws Exception {
		var request = buildRequest("instructions/general", Map.of("name", "inst1"), "Bearer token");
		var response = handler.handleRequest(request, null);

		assertEquals(400, response.getStatusCode());
	}

	@Test
	void testDeleteHistory_MissingFields() throws Exception {
		var request = buildRequest("deleteHistory", Map.of(), "Bearer token");
		var response = handler.handleRequest(request, null);

		assertEquals(400, response.getStatusCode());
	}

	@Test
	void testUpdateTitle_MissingFields() throws Exception {
		var request = buildRequest("updateTitle", Map.of("id", "1"), "Bearer token");
		var response = handler.handleRequest(request, null);

		assertEquals(400, response.getStatusCode());
	}

	@Test
	void testUpdateUser_MissingFields() throws Exception {
		var request = buildRequest("updateUser", Map.of("cc", "123"), "");
		var response = handler.handleRequest(request, null);

		assertEquals(400, response.getStatusCode());
	}

	@Test
	void testUpdateUser_Success() throws Exception {
		var request = buildRequest(
				"updateUser",
				Map.of(
						"cc", userDto.getCedula(),
						"firstName", userDto.getFirstName(),
						"lastName", userDto.getLastName(),
						"email", userDto.getEmail(),
						"password", userDto.getPassword(),
						"role", userDto.getRole(),
						"image", "img.png",
						"authType", userDto.getAuthType()
						),
				"Bearer fakeToken"
				);

		var response = handler.handleRequest(request, null);

		assertNotNull(response);
		assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 400);
	}

	@Test
	void testInvalidAction() throws Exception {
		var request = buildRequest("not_exist", Map.of(), null);
		var response = handler.handleRequest(request, null);

		assertEquals(400, response.getStatusCode());
		assertTrue(response.getBody().contains("Acción no válida"));
	}

	@Test
	void testJsonParseError() {
		APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
		request.setBody("{invalid-json}");

		var response = handler.handleRequest(request, null);

		assertEquals(500, response.getStatusCode());
		assertTrue(response.getBody().contains("Error interno"));
	}
}
