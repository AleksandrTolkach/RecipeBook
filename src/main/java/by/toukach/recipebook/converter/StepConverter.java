package by.toukach.recipebook.converter;

import by.toukach.recipebook.dto.StepDto;
import by.toukach.recipebook.entity.Step;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * A class for converting Step entity to dto and vice versa.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StepConverter {

  /**
   * This method is responsible for converting Step entity to StepDto.
   *
   * @param entity step entity to be converted
   * @return StepDto
   */
  public static StepDto convertToDto(Step entity) {

    return StepDto.builder()
        .id(entity.getId())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .description(entity.getDescription())
        .build();
  }
}
