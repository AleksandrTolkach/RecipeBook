package by.toukach.recipebook.enumeration;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Enum represents available units.
 */
public enum MeasureUnit {
  MILLILITER,
  GRAM,
  PCS,
  LITER,
  MILLIGRAM,
  KILOGRAM;

  /**
   * This method is responsible for finding all MeasureUnit's name.
   *
   * @return array of available names
   */
  public static String getNames() {
    return Arrays.stream(MeasureUnit.class.getEnumConstants()).map(Enum::name)
        .collect(Collectors.joining(", "));
  }
}
