package by.toukach.recipebook.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * An interface for managing files.
 */
public interface S3Service {

  /**
   * This method is responsible for saving file in S3 bucket.
   *
   * @param objectFile received file
   * @return file name in storage
   */
  String putObject(MultipartFile objectFile);

  /**
   * This method is responsible for generating file's access URL.
   *
   * @param objectName The name of specific file
   * @return generated URL
   */
  String getObjectUrl(String objectName);

  /**
   * This method is responsible for deleting file from S3 bucket.
   *
   * @param objectName The name of specific file.
   */
  void deleteObject(String objectName);
}
