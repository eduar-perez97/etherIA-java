package com.periferia.etheria.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.periferia.etheria.dto.InstructionDto;
import com.periferia.etheria.entity.InstructionEntity;

@ExtendWith(MockitoExtension.class)
public class InstructionUtilTest {

	@Test
	void testConvertDtoToEntity() {
		InstructionDto dto = new InstructionDto();
		dto.setId(1L);
		dto.setName("Inst1");
		dto.setDescription("Desc1");
		dto.setInstruction("Do something");
		dto.setGeneral(true);
		dto.setIdUser("user1");

		InstructionEntity entity = InstructionUtil.convertDtoToEntity(dto);

		assertEquals(dto.getId(), entity.getId());
		assertEquals(dto.getName(), entity.getName());
		assertEquals(dto.getDescription(), entity.getDescription());
		assertEquals(dto.getInstruction(), entity.getInstruction());
		assertEquals(dto.getGeneral(), entity.getGeneral());
		assertEquals(dto.getIdUser(), entity.getIdUser());
	}

	@Test
	void testConvertEntityToDto() {
		InstructionEntity entity = new InstructionEntity();
		entity.setId(2L);
		entity.setName("Inst2");
		entity.setDescription("Desc2");
		entity.setInstruction("Do other thing");
		entity.setGeneral(false);
		entity.setIdUser("user2");

		InstructionDto dto = InstructionUtil.convertEntityToDto(entity);

		assertEquals(entity.getId(), dto.getId());
		assertEquals(entity.getName(), dto.getName());
		assertEquals(entity.getDescription(), dto.getDescription());
		assertEquals(entity.getInstruction(), dto.getInstruction());
		assertEquals(entity.getGeneral(), dto.getGeneral());
		assertEquals(entity.getIdUser(), dto.getIdUser());
	}

	@Test
	void testConvertEntityListToDtoList() {
		InstructionEntity entity1 = new InstructionEntity();
		entity1.setId(1L);
		entity1.setName("Inst1");
		entity1.setDescription("Desc1");
		entity1.setInstruction("Do A");
		entity1.setGeneral(true);
		entity1.setIdUser("user1");

		InstructionEntity entity2 = new InstructionEntity();
		entity2.setId(2L);
		entity2.setName("Inst2");
		entity2.setDescription("Desc2");
		entity2.setInstruction("Do B");
		entity2.setGeneral(false);
		entity2.setIdUser("user2");

		List<InstructionEntity> entityList = new ArrayList<>();
		entityList.add(entity1);
		entityList.add(entity2);

		List<InstructionDto> dtoList = InstructionUtil.convertEntityListToDtoList(entityList);

		assertEquals(2, dtoList.size());

		assertEquals(entity1.getId(), dtoList.get(0).getId());
		assertEquals(entity1.getName(), dtoList.get(0).getName());
		assertEquals(entity1.getDescription(), dtoList.get(0).getDescription());
		assertEquals(entity1.getInstruction(), dtoList.get(0).getInstruction());
		assertEquals(entity1.getGeneral(), dtoList.get(0).getGeneral());
		assertEquals(entity1.getIdUser(), dtoList.get(0).getIdUser());

		assertEquals(entity2.getId(), dtoList.get(1).getId());
		assertEquals(entity2.getName(), dtoList.get(1).getName());
		assertEquals(entity2.getDescription(), dtoList.get(1).getDescription());
		assertEquals(entity2.getInstruction(), dtoList.get(1).getInstruction());
		assertEquals(entity2.getGeneral(), dtoList.get(1).getGeneral());
		assertEquals(entity2.getIdUser(), dtoList.get(1).getIdUser());
	}
}