package com.periferia.etheria.security;

import com.periferia.etheria.dto.UserDto;
import com.periferia.etheria.exception.UserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.ConnectException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AuthEntraIDServiceTest {

	private AuthEntraID authEntraID;

	@Mock
	private HttpClient mockClient;

	@Mock
	private HttpResponse<String> mockResponse;

	@BeforeEach
	void setUp() {
		authEntraID = new AuthEntraID(mockClient);
		System.setProperty("URL_ENTRAID", "http://fake-url.com");
	}

	@Test
	void testAuthenticatorEntraID_Success200WithData() throws Exception {
		String jsonResponse = "{\"data\":{\"email\":\"test@example.com\",\"firstName\":\"Ana\"}}";
		Mockito.when(mockResponse.statusCode()).thenReturn(200);
		Mockito.when(mockResponse.body()).thenReturn(jsonResponse);
		Mockito.when(mockClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
		.thenReturn(mockResponse);

		UserDto user = authEntraID.authenticatorEntraID("test@example.com");

		assertNotNull(user);
		assertEquals("test@example.com", user.getEmail());
		assertEquals("Ana", user.getFirstName());
	}

	@Test
	void testAuthenticatorEntraID_Success404WithoutData() throws Exception {
		String jsonResponse = "{\"data\":null}";
		Mockito.when(mockResponse.statusCode()).thenReturn(404);
		Mockito.when(mockResponse.body()).thenReturn(jsonResponse);
		Mockito.when(mockClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
		.thenReturn(mockResponse);

		UserDto user = authEntraID.authenticatorEntraID("test@example.com");

		assertNull(user);
	}

	@Test
	void testAuthenticatorEntraID_Error500() throws Exception {
		String jsonResponse = "{\"detail\":\"Internal server error\"}";
		Mockito.when(mockResponse.statusCode()).thenReturn(500);
		Mockito.when(mockResponse.body()).thenReturn(jsonResponse);
		Mockito.when(mockClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
		.thenReturn(mockResponse);

		UserException ex = assertThrows(UserException.class,
				() -> authEntraID.authenticatorEntraID("fail@example.com"));

		assertTrue(ex.getMessage().contains("Internal server error"));
	}

	@Test
	void testAuthenticatorEntraID_ConnectException() throws Exception {
		Mockito.when(mockClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
		.thenThrow(new ConnectException("Connection refused"));

		UserException ex = assertThrows(UserException.class,
				() -> authEntraID.authenticatorEntraID("x@example.com"));

		assertTrue(ex.getMessage().contains("No se pudo conectar con EntraID"));
	}

	@Test
	void testAuthenticatorEntraID_InterruptedException() throws Exception {
		Mockito.when(mockClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
		.thenThrow(new InterruptedException("Interrupted"));

		UserException ex = assertThrows(UserException.class,
				() -> authEntraID.authenticatorEntraID("y@example.com"));

		assertNotNull(ex.getMessage());
	}

	@Test
	void testAuthenticatorEntraID_IOException() throws Exception {
		Mockito.when(mockClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
		.thenThrow(new java.io.IOException("IO boom"));

		UserException ex = assertThrows(UserException.class,
				() -> authEntraID.authenticatorEntraID("z@example.com"));

		assertTrue(ex.getMessage().contains("Error de entrada/salida al comunicarse con EntraID"));
	}
}
