package com.periferia.etheria.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.periferia.etheria.dto.RecordDto;
import com.periferia.etheria.dto.TitleRecordDto;
import com.periferia.etheria.entity.RecordEntity;
import com.periferia.etheria.entity.TitleRecordEntity;

@ExtendWith(MockitoExtension.class)
class TitleRecordUtilTest {
	private static MockedStatic<RecordUtil> recordUtilMock;

	@BeforeAll
	static void setUpMock() {
		recordUtilMock = Mockito.mockStatic(RecordUtil.class);
	}

	@Test
	void testConvertTitleRecordEntityToDtoNoRecords() {
		TitleRecordEntity entity = new TitleRecordEntity();
		entity.setId(1L);
		entity.setTitle("Title 1");
		entity.setDateCreate(LocalDate.of(2025, 9, 2));
		entity.setIdUser("123");
		entity.setUuid("uuid-1");
		entity.setAgent("agent-1");
		entity.setRecordEntity(null);

		TitleRecordDto dto = TitleRecordUtil.convertTitleRecordEntityToDto(entity);

		assertEquals(1L, dto.getId());
		assertEquals("Title 1", dto.getTitle());
		assertEquals(LocalDate.of(2025, 9, 2), dto.getDateCreate());
		assertEquals("123", dto.getIdUser());
		assertEquals("uuid-1", dto.getUuid());
		assertEquals("agent-1", dto.getAgent());
		assertNull(dto.getRecordDto());
	}

	@Test
	void testConvertTitleRecordEntityToDtoWithRecords() {
		TitleRecordEntity entity = new TitleRecordEntity();
		entity.setId(2L);
		entity.setTitle("Title 2");
		entity.setDateCreate(LocalDate.of(2025, 9, 2));
		entity.setIdUser("456");
		entity.setUuid("uuid-2");
		entity.setAgent("agent-2");

		List<RecordEntity> records = new ArrayList<>();
		records.add(new RecordEntity());
		entity.setRecordEntity(records);

		List<RecordDto> mockRecordDtos = List.of(new RecordDto(1L, "Q1", "A1"));
		recordUtilMock.when(() -> RecordUtil.convertEntityToDtoList(records))
		.thenReturn(mockRecordDtos);

		TitleRecordDto dto = TitleRecordUtil.convertTitleRecordEntityToDto(entity);

		assertEquals(mockRecordDtos, dto.getRecordDto());
	}

	@Test
	void testConvertTitleRecordEntityListToDtoList() {
		TitleRecordEntity entity1 = new TitleRecordEntity();
		entity1.setId(1L);
		entity1.setTitle("T1");
		entity1.setDateCreate(LocalDate.of(2025, 9, 2));
		entity1.setIdUser("u1");
		entity1.setUuid("uuid1");
		entity1.setAgent("agent1");
		entity1.setRecordEntity(null);

		TitleRecordEntity entity2 = new TitleRecordEntity();
		entity2.setId(2L);
		entity2.setTitle("T2");
		entity2.setDateCreate(LocalDate.of(2025, 9, 2));
		entity2.setIdUser("u2");
		entity2.setUuid("uuid2");
		entity2.setAgent("agent2");
		List<RecordEntity> records = new ArrayList<>();
		entity2.setRecordEntity(records);

		List<RecordDto> mockRecordDtos = List.of(new RecordDto(1L, "Q1", "A1"));
		recordUtilMock.when(() -> RecordUtil.convertEntityToDtoList(records))
		.thenReturn(mockRecordDtos);

		List<TitleRecordDto> dtos = TitleRecordUtil.convertTitleRecordEntityListToDtoList(List.of(entity1, entity2));

		assertEquals(2, dtos.size());
		assertEquals(mockRecordDtos, dtos.get(1).getRecordDto());
	}
}