package by.toukach.recipebook.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * A class representing page response with content.
 *
 * @param <T> content type
 */
@Data
@Builder
@AllArgsConstructor
public class PagedResponse<T> {
  private int size;
  private int page;
  private List<T> content;
  private boolean last;
  private long totalElements;
  private int totalPages;
}
