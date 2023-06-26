package by.toukach.recipebook.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A class that represents a configuration for application.
 */
@Configuration
public class Config {
  @Value("${spring.minio.url}")
  private String minioUrl;
  @Value("${spring.minio.bucket}")
  private String bucketName;
  @Value("${spring.minio.access-key}")
  private String accessKey;
  @Value("${spring.minio.secret-key}")
  private String secretKey;

  /**
   * Defines minio client bean.
   *
   * @return minio client.
   */
  @Bean
  public MinioClient minioClient()
      throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
      NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
      XmlParserException, InternalException {

    MinioClient minioClient = MinioClient.builder()
        .endpoint(minioUrl)
        .credentials(accessKey, secretKey)
        .build();

    BucketExistsArgs bucket = BucketExistsArgs.builder().bucket(bucketName).build();

    if (!minioClient.bucketExists(bucket)) {
      MakeBucketArgs bucketArgs = MakeBucketArgs.builder()
          .bucket(bucketName)
          .objectLock(false)
          .build();
      minioClient.makeBucket(bucketArgs);
    }
    return minioClient;
  }
}
