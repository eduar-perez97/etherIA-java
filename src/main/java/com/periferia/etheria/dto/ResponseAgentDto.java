package com.periferia.etheria.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseAgentDto {
	private List<TitleRecordDto> titleDto;
	private List<RecordDto> recordDto;
}
