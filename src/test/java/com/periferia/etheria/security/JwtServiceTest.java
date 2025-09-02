package com.periferia.etheria.security;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.periferia.etheria.dto.UserDto;
import com.periferia.etheria.exception.UserException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

class JwtServiceTest {

	private JwtService jwtService;
	private static final String SECRET = "0123456789_0123456789_0123456789_012345";

	@BeforeEach
	void setUp() {
		jwtService = new JwtService(SECRET);
	}

	@Test
	void testGenerateTokenAndValidate_Success() {
		String email = "test@example.com";
		String token = jwtService.generateToken(email);

		assertNotNull(token);
		assertTrue(jwtService.validateToken(token));
	}

	@Test
	void testValidateToken_InvalidToken() {
		String invalidToken = "xxx.yyy.zzz";
		UserException exception = assertThrows(UserException.class,
				() -> jwtService.validateToken(invalidToken));

		assertEquals(400, exception.getErrorCode());
		assertTrue(exception.getMessage().contains("Error: El token ha expirado"));
	}

	@Test
	void testJwtDecoder_Success() {
		String payload = "{\"blob\":\"Correo: juan@example.com Nombres: Juan Apellidos: Pérez Cedula: 123456 Rol: ADMIN\",\"iat\":1700000000}";

		Key key = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

		String token = Jwts.builder()
				.setPayload(payload)
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();

		UserDto user = jwtService.jwtDecoder(token);

		assertNotNull(user);
		assertEquals("123456", user.getCedula());
		assertEquals("Juan", user.getFirstName());
		assertEquals("Pérez", user.getLastName());
		assertEquals("juan@example.com", user.getEmail());
		assertEquals("ADMIN", user.getRole());
	}

	@Test
	void testJwtDecoder_InvalidToken_Structure() {
		String invalidToken = "xxx.yyy.zzz";
		assertThrows(UserException.class, () -> jwtService.jwtDecoder(invalidToken));
	}

	@Test
	void testJwtDecoder_InvalidPayload() {
		String badPayload = "{\"blob\":\"Correo: test@example.com Nombres: John Apellidos: Doe Cedula: 999999\",\"iat\":1700000000}";

		Key key = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

		String token = Jwts.builder()
				.setPayload(badPayload)
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();

		UserException exception = assertThrows(UserException.class, () -> jwtService.jwtDecoder(token));
		assertEquals(400, exception.getErrorCode());
		assertTrue(exception.getMessage().contains("Error: No se puede decodificar el token: "));
	}
}
