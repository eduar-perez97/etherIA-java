package com.periferia.etheria.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.periferia.etheria.dto.UserDto;
import com.periferia.etheria.entity.UserEntity;
import com.periferia.etheria.exception.UserException;
import com.periferia.etheria.repository.UserRepository;
import com.periferia.etheria.security.AuthEntraID;
import com.periferia.etheria.security.JwtService;
import com.periferia.etheria.service.impl.UserServiceImpl;
import com.periferia.etheria.util.Response;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private JwtService jwtService;
	@Mock
	private AuthEntraID authEntraID;
	@InjectMocks
	private UserServiceImpl userService;
	private UserDto userDto;
	private UserEntity userEntity;

	@BeforeEach
	void setUp() {
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

	@Test
	void testRegisterUserSuccess() {
		when(userRepository.existsById(userDto.getCedula())).thenReturn(false);

		Response<Boolean> response = userService.registerUser(userDto);

		assertEquals(201, response.getStatusCode());
		assertTrue(response.getData());
		assertEquals("Registro exitoso", response.getMessage());

		verify(userRepository, times(1)).save(any(UserEntity.class));
	}

	@Test
	void testRegisterUserAlreadyExists() {
		when(userRepository.existsById(userDto.getCedula())).thenReturn(true);

		Response<Boolean> response = userService.registerUser(userDto);

		assertEquals(400, response.getStatusCode());
		assertFalse(response.getData());

	}

	@Test
	void testLoginUserLocalSuccess() {
		when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(userEntity));
		when(jwtService.generateToken(anyString())).thenReturn("tokeFake-jwt");

		userEntity.setPassword(org.mindrot.jbcrypt.BCrypt.hashpw("password", org.mindrot.jbcrypt.BCrypt.gensalt()));

		Response<UserDto> response = userService.loginUser(userDto);

		assertEquals(200, response.getStatusCode());
		assertNotNull(response.getData().getToken());
		assertEquals("prueba@periferia.com", response.getData().getEmail());
	}

	@Test
	void testLoginUserLocalWrongPassword() {
		userEntity.setPassword(org.mindrot.jbcrypt.BCrypt.hashpw("otherpass", org.mindrot.jbcrypt.BCrypt.gensalt()));
		when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(userEntity));

		assertThrows(UserException.class, () -> userService.loginUser(userDto));
	}

	@Test
	void testLoginUserWithEntraIDSuccess() {
		userDto.setAuthType("ENTRAID");

		when(authEntraID.authenticatorEntraID(userDto.getEmail())).thenReturn(userDto);
		when(jwtService.generateToken(anyString())).thenReturn("fake-token");

		Response<UserDto> response = userService.loginUser(userDto);

		assertEquals(200, response.getStatusCode());
		assertNotNull(response.getData().getToken());
		assertEquals("usuario", response.getData().getRole());
	}

	@Test
	void testUpdateDataUserAsAdmin() {
		String token = "Bearer fake.jwt.token";
		userDto.setRole("ADMINISTRADOR");

		when(jwtService.jwtDecoder("fake.jwt.token")).thenReturn(userDto);
		when(jwtService.validateToken("fake.jwt.token")).thenReturn(true);
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
		when(userRepository.update(any(UserEntity.class))).thenReturn(userEntity);

		Response<UserDto> response = userService.updateDataUser(userDto, token);

		assertEquals(200, response.getStatusCode());
		assertEquals("prueba@periferia.com", response.getData().getEmail());
	}


	@Test
	void testUpdateDataUserNotAdmin() {
		String token = "Bearer fake.jwt.token";
		UserDto normalUser = new UserDto();
		normalUser.setRole("USUARIO");

		when(jwtService.jwtDecoder("fake.jwt.token")).thenReturn(normalUser);

		Response<UserDto> response = userService.updateDataUser(userDto, token);

		assertEquals(400, response.getStatusCode());
		assertNull(response.getData());
	}
}