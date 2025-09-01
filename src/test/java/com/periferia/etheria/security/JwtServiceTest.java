package com.periferia.etheria.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.periferia.etheria.dto.UserDto;
import com.periferia.etheria.exception.UserException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtServiceTest {
	@Mock
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

		assertTrue(exception.getMessage().contains("Error: El token ha expirado: Malformed JWT JSON: �"));
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
	void testJwtDecoder_InvalidToken() {
		String invalidToken = "xxx.yyy.zzz";
		assertThrows(UserException.class, () -> jwtService.jwtDecoder(invalidToken));
	}
}