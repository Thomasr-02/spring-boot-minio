package com.minio.poc;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import io.minio.messages.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MinioService {

    public static void upload(Path source, InputStream file, String contentType)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint("http://127.0.0.1:9000")
                            .credentials("minio-access-key", "minio-secret-key")
                            .build();

            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket("teste").build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("teste").build());
            } else {
                System.out.println("Bucket 'teste' already exists.");
            }
/*
            List<LifecycleRule> rules = new LinkedList<>();

            rules.add(
                    new LifecycleRule(
                            Status.ENABLED,
                            null,
                            new Expiration((ZonedDateTime) null, 1, null),
                            new RuleFilter("teste"),
                            "rule1",
                            null,
                            null,
                            null));
            LifecycleConfiguration config = new LifecycleConfiguration(rules);
            minioClient.setBucketLifecycle(
                    SetBucketLifecycleArgs.builder().bucket("teste").config(config).build());
*/

            System.out.println(source);
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket("teste")
                            .object(String.valueOf(source))
                            .filename("/home/tra/"+source)
                            //.retention(new Retention(RetentionMode.COMPLIANCE, ZonedDateTime.now().plusSeconds(30)))
                            .build());




            // Get presigned URL of an object for HTTP method, expiry time and custom request parameters.
            String url =
                    minioClient.getPresignedObjectUrl(
                            GetPresignedObjectUrlArgs.builder()
                                    .method(Method.GET)
                                    .bucket("teste")
                                    .object("teste.txt")
                                    .expiry(20, TimeUnit.SECONDS)
                                    .build());
            System.out.println(url);





            System.out.println(
                    "'/home/teste.txt' is successfully uploaded as "
                            + "object 'meu-teste.txt' to bucket 'teste'.");
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
        }
    }

    public InputStream getFile(Path path) {
        try {
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint("http://127.0.0.1:9000")
                            .credentials("minio-access-key", "minio-secret-key")
                            .build();
            GetObjectArgs args = GetObjectArgs.builder()
                    .bucket("teste")
                    .object(path.toString()) //'/download/file.txt'
                    .build();
            return minioClient.getObject(args);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }


    public void getAndSave(String fileName) {
        try {
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint("http://127.0.0.1:9000")
                            .credentials("minio-access-key", "minio-secret-key")
                            .build();


            DownloadObjectArgs args = DownloadObjectArgs.builder()
                    .bucket("teste")
                    .object(fileName)
                    .filename("storage/"+fileName)
                    .build();

            minioClient.downloadObject(args);

        }catch (Exception e) {
            System.out.println(e);
        }
    }


    public void remove(String fileName) {
        try {
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint("http://127.0.0.1:9000")
                            .credentials("minio-access-key", "minio-secret-key")
                            .build();

            //Remove object.
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket("teste").object(fileName).build());


        }catch (Exception e) {
            System.out.println(e);
        }
    }


}
