package us.ppgs.mirror;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.MinioException;

public class MinioTest {

	public static void main(String[] args)
		      throws IOException, NoSuchAlgorithmException, InvalidKeyException {
		    try {
		      // Create a minioClient with the MinIO server playground, its access key and secret key.
		      MinioClient minioClient =
		          MinioClient.builder()
		              .endpoint("http://localhost:9000")
		              .credentials("minioadmin", "minioadmin")
		              .build();

		      // Make 'asiatrip' bucket if not exist.
		      boolean found =
		          minioClient.bucketExists(BucketExistsArgs.builder().bucket("asiatrip").build());
		      if (!found) {
		        // Make a new bucket called 'asiatrip'.
		        minioClient.makeBucket(MakeBucketArgs.builder().bucket("asiatrip").build());
		      } else {
		        System.out.println("Bucket 'asiatrip' already exists.");
		      }

		      // Upload '/home/user/Photos/asiaphotos.zip' as object name 'asiaphotos-2015.zip' to bucket
		      // 'asiatrip'.
		      minioClient.uploadObject(
		          UploadObjectArgs.builder()
		              .bucket("asiatrip")
		              .object("README.md2")
		              .filename("README.md")
		              .build());
		      System.out.println(
		          "'/home/user/Photos/asiaphotos.zip' is successfully uploaded as "
		              + "object 'asiaphotos-2015.zip' to bucket 'asiatrip'.");
		    } catch (MinioException e) {
		      System.out.println("Error occurred: " + e);
		      System.out.println("HTTP trace: " + e.httpTrace());
		    }
		  }}
