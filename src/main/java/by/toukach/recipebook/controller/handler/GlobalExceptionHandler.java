package by.toukach.recipebook.controller.handler;

import by.toukach.recipebook.exception.BaseError;
import by.toukach.recipebook.exception.EntityExistsException;
import by.toukach.recipebook.exception.EntityNotFoundException;
import by.toukach.recipebook.exception.InvalidArgumentValueException;
import by.toukach.recipebook.exception.OwnerException;
import by.toukach.recipebook.exception.TokenRefreshException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/**
 * A class that handles exceptions on controller side.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * This method is responsible for handling EntityNotFoundException.
   *
   * @param ex the occurred EntityNotFoundException
   * @return BaseError
   */
  @ExceptionHandler({EntityNotFoundException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public BaseError handleUserNotFoundException(EntityNotFoundException ex) {
    BaseError responseBody = new BaseError();
    responseBody.setMessage(ex.getMessage());
    return responseBody;
  }

  /**
   * This method is responsible for handling EntityExistsException.
   *
   * @param ex the occurred EntityExistsException
   * @return BaseError
   */
  @ExceptionHandler({EntityExistsException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.CONFLICT)
  public BaseError handleUserExistsException(EntityExistsException ex) {
    BaseError responseBody = new BaseError();
    responseBody.setMessage(ex.getMessage());
    return responseBody;
  }

  /**
   * The method is responsible for handling TokenRefreshException.
   *
   * @param ex the occurred TokenRefreshException
   * @return BaseError
   */
  @ExceptionHandler({TokenRefreshException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public BaseError handleTokenRefreshException(TokenRefreshException ex) {
    BaseError responseBody = new BaseError();
    responseBody.setMessage(ex.getMessage());
    return responseBody;
  }

  /**
   * The method is responsible for handling OwnerException.
   *
   * @param ex the occurred OwnerException
   * @return BaseError
   */
  @ExceptionHandler({OwnerException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public BaseError handleOwnerException(OwnerException ex) {
    BaseError responseBody = new BaseError();
    responseBody.setMessage(ex.getMessage());
    return responseBody;
  }

  /**
   * The method is responsible for handling InvalidArgumentValueException.
   *
   * @param ex the occurred InvalidArgumentValueException
   * @return BaseError
   */
  @ExceptionHandler({InvalidArgumentValueException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public BaseError handleInvalidArgumentValueException(InvalidArgumentValueException ex) {
    BaseError responseBody = new BaseError();
    responseBody.setMessage(ex.getMessage());
    return responseBody;
  }

  /**
   * The method is responsible for handling HttpMediaTypeNotSupportedException.
   *
   * @param ex the occurred HttpMediaTypeNotSupportedException
   * @return BaseError
   */
  @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public BaseError handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
    BaseError responseBody = new BaseError();
    responseBody.setMessage(ex.getMessage());
    return responseBody;
  }

  /**
   * The method is responsible for handling MissingServletRequestPartException.
   *
   * @param ex the occurred MissingServletRequestPartException
   * @return BaseError
   */
  @ExceptionHandler({MissingServletRequestPartException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public BaseError handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
    BaseError responseBody = new BaseError();
    responseBody.setMessage(ex.getMessage());
    return responseBody;
  }

  /**
   * The method is responsible for handling MultipartException.
   *
   * @param ex the occurred MultipartException
   * @return BaseError
   */
  @ExceptionHandler({MultipartException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public BaseError handleMultipartException(MultipartException ex) {
    BaseError responseBody = new BaseError();
    responseBody.setMessage(ex.getMessage());
    return responseBody;
  }

  /**
   * This method is responsible for handling MethodArgumentNotValidException exception.
   *
   * @param ex the occurred MethodArgumentNotValidException
   * @return BaseError
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Map<String, List<String>>> handleValidationErrors(
      MethodArgumentNotValidException ex) {
    Map<String, Map<String, List<String>>> errorsMap = new HashMap<>();
    ex.getBindingResult().getFieldErrors()
        .forEach(fieldError -> {
          String fieldName = fieldError.getField();
          String errorMessage = fieldError.getDefaultMessage();

          Map<String, List<String>> fieldErrorMap = errorsMap
              .computeIfAbsent(fieldName, k -> new HashMap<>());

          List<String> errorList = fieldErrorMap
              .computeIfAbsent("messages", k -> new ArrayList<>());

          errorList.add(errorMessage);
        });

    return errorsMap;
  }
}
