package com.team03.monew.service;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3LogUploader {

  private final S3Client s3Client;

  @Value("${AWS_S3_BUCKET}")
  private String bucket;

  public S3LogUploader(S3Client s3Client) {
    this.s3Client = s3Client;
  }

  public void uploadLogFile(Path logFilePath) throws IOException {
    String key = "logs/" + logFilePath.getFileName().toString();
    s3Client.putObject(
        PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build(),
        RequestBody.fromFile(logFilePath)
    );
  }
}
