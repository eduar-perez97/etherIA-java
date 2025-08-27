package com.periferia.etheria.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryAgentDto {

	private String model;
	private String agentId;
	private String question;
	private String response;
	private String uuid;
	private String cedula;
	private String title;
	
}
