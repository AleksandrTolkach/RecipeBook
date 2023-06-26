package by.toukach.recipebook.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * A class that represents a base dto with basic fields.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseDto {
  private Long id;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
