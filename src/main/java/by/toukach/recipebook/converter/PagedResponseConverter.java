package by.toukach.recipebook.converter;

import by.toukach.recipebook.dto.PagedResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

/**
 * A class for converting Page to PageResponse.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PagedResponseConverter {

  /**
   * This method is responsible for converting Page to PagedResponse.
   *
   * @param page page with content
   * @param content content of the page
   * @param <E> entity
   * @param <D> dto
   * @return PageResponse with content
   */
  public static <E, D> PagedResponse<D> convertToResponse(Page<E> page, List<D> content) {
    return PagedResponse.<D>builder()
        .size(page.getSize())
        .page(page.getNumber())
        .content(content)
        .last(page.isLast())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .build();
  }
}
