package by.toukach.recipebook.service.impl;

import by.toukach.recipebook.exception.ExceptionMessage;
import by.toukach.recipebook.exception.InvalidArgumentValueException;
import by.toukach.recipebook.exception.MinioPutObjectException;
import by.toukach.recipebook.service.S3Service;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * A service class for managing files.
 */
@Service
@RequiredArgsConstructor
public class MinioS3ServiceImpl implements S3Service {

  @Value("${spring.minio.bucket}")
  private String bucketName;
  private final MinioClient minioClient;

  @Override
  public String putObject(MultipartFile objectFile) {
    String contentType = objectFile.getContentType();
    if (contentType == null) {
      throw new InvalidArgumentValueException(String.format(ExceptionMessage.INVALID_CONTENT_TYPE,
          objectFile.getName()));
    }
    String objectName = UUID.randomUUID().toString();
    try {
      PutObjectArgs putObjectArgs = PutObjectArgs.builder()
          .object(objectName)
          .contentType(contentType)
          .bucket(bucketName)
          .stream(objectFile.getInputStream(), -1, 51200000)
          .build();
      minioClient.putObject(putObjectArgs);

    } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException
        | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException
        | ServerException | XmlParserException e) {
      throw new MinioPutObjectException(ExceptionMessage.MINIO_PUT_OBJECT_ERROR);
    }
    return objectName;
  }

  @Override
  public String getObjectUrl(String objectName) {
    GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
        .bucket(bucketName)
        .object(objectName)
        .expiry(7, TimeUnit.DAYS)
        .method(Method.GET)
        .build();
    try {

      return minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);

    } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException
        | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException
        | ServerException | XmlParserException e) {
      throw new MinioPutObjectException(ExceptionMessage.MINIO_PUT_OBJECT_ERROR);
    }
  }

  @Override
  public void deleteObject(String objectName) {
    RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
        .object(objectName)
        .bucket(bucketName)
        .build();

    try {

      minioClient.removeObject(removeObjectArgs);

    } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException
        | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException
        | ServerException | XmlParserException e) {
      throw new MinioPutObjectException(ExceptionMessage.MINIO_PUT_OBJECT_ERROR);
    }
  }
}
