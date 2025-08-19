package com.periferia.etheria.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

	private String cedula;
	private String idUser;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String token;
	private String role;
	private String image;
	private String authType;
	private List<RecordDto> recordDto;

	public UserDto() {}
	
	public UserDto(String cedula, String firstName, String lastName, String email, String password, String role, String image, String authType) {
		this.cedula = cedula;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.role = role;
		this.image = image;
		this.authType = authType;
	}
}
