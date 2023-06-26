package by.toukach.recipebook.controller.listener;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementation of MultipartFile.
 * Responsible for transfer images' content in ApplicationStateListener.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class LocalMultipartFileImpl implements MultipartFile {

  private final String name;
  private final String originalFilename;
  @Nullable
  private final String contentType;
  private final byte[] content;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getOriginalFilename() {
    return originalFilename;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public boolean isEmpty() {
    return content.length == 0;
  }

  @Override
  public long getSize() {
    return content.length;
  }

  @Override
  public byte[] getBytes() throws IOException {
    return this.content;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return new ByteArrayInputStream(content);
  }

  @Override
  public void transferTo(File dest) throws IOException, IllegalStateException {
    FileCopyUtils.copy(content, dest);
  }
}
