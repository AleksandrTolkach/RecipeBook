package by.toukach.recipebook.converter;

import by.toukach.recipebook.dto.IngredientCreatingDto;
import by.toukach.recipebook.dto.IngredientDto;
import by.toukach.recipebook.entity.Ingredient;
import by.toukach.recipebook.enumeration.MeasureUnit;
import by.toukach.recipebook.exception.ExceptionMessage;
import by.toukach.recipebook.exception.InvalidArgumentValueException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * A class for converting Ingredient entity to dto and vice versa.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IngredientConverter {

  /**
   * This method is responsible for converting Ingredient entity to IngredientDto.
   *
   * @param entity entity to be converted
   * @return IngredientDto
   */
  public static IngredientDto convertToDto(Ingredient entity) {
    return IngredientDto.builder()
        .id(entity.getId())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .ingredientName(entity.getIngredientName())
        .measureUnit(entity.getMeasureUnit())
        .quantity(entity.getQuantity())
        .build();
  }

  /**
   * This method is responsible for converting IngredientCreatingDto to Ingredient entity.
   *
   * @param dto dto to be converted
   * @return Ingredient entity
   */
  public static Ingredient convertToEntity(IngredientCreatingDto dto) {
    String measureUnitString = dto.getMeasureUnit();

    MeasureUnit measureUnit;
    try {
      measureUnit = MeasureUnit.valueOf(measureUnitString.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new InvalidArgumentValueException(ExceptionMessage.INVALID_MEASURE_UNIT);
    }

    return Ingredient.builder()
        .ingredientName(dto.getIngredientName())
        .measureUnit(measureUnit)
        .quantity(dto.getQuantity())
        .build();
  }
}
