package com.periferia.etheria;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.periferia.etheria.dto.UserDto;
import com.periferia.etheria.security.ReadSecret;
import com.periferia.etheria.service.impl.UserServiceImpl;
import com.periferia.etheria.util.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserHandlerFullTest {

	private static MockedStatic<ReadSecret> readSecretMock;

	@Mock
	private UserServiceImpl userServiceImpl;

	private UserHandler handler;

	private ObjectMapper mapper;

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
		handler.userServiceImpl = userServiceImpl;
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
	void testLogin_Success() throws Exception {
		UserDto userDto = new UserDto("123456", "Test", "User", "test@test.com", "password", "USUARIO", "fakeImage", "LOCAL");
		Response<UserDto> mockResponse = new Response<UserDto>(null, null, null);
		mockResponse.setStatusCode(200);
		mockResponse.setData(userDto);
		mockResponse.setMessage("Login successful");

		when(userServiceImpl.loginUser(any(UserDto.class))).thenReturn(mockResponse);

		var request = buildRequest("login", Map.of(
				"cc", "123456",
				"firstName", "Test",
				"lastName", "User",
				"email", "test@test.com",
				"password", "password",
				"role", "USUARIO",
				"image", "fakeImage",
				"authType", "LOCAL"
				), null);

		var response = handler.handleRequest(request, null);
		assertEquals(200, response.getStatusCode());
		assertTrue(response.getBody().contains("Login successful"));
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
