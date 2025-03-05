package com.pawstime.pawstime.aws.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${aws.s3.bucket-name}")
    private String bucket;

    private final AmazonS3 amazonS3;


    public List<String> uploadFile(List<MultipartFile> multipartFiles) {
        List<String> fileUrlList = new ArrayList<>();
        for (MultipartFile file : multipartFiles) {
            try {
                // 파일 이름 정리
                String originalFileName = sanitizeFileName(file.getOriginalFilename());
                String fileName = createFileName(originalFileName);
                System.out.println("파일 이름 정리");

                // 메타데이터 설정
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentLength(file.getSize());
                objectMetadata.setContentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
                System.out.println("메타데이터 설정");

                // S3 업로드
                try (InputStream inputStream = file.getInputStream()) {
                    amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
                    System.out.println("s3upload");
                }

                // 업로드된 파일 URL 저장
                String fileUrl = amazonS3.getUrl(bucket, fileName).toString();
                fileUrlList.add(fileUrl);

            } catch (IOException e) {
                System.err.println("파일 업로드 실패: " + file.getOriginalFilename());
            }
        }
        return fileUrlList;
    }
    // 기본 이미지를 S3에 업로드하고 URL을 반환하는 메서드
    public String uploadDefaultImageToS3(String imagePath) {
        try {
            // 파일 경로가 정확한지 확인
            File file = new File(getClass().getClassLoader().getResource(imagePath).toURI());
            String fileName = createFileName(file.getName());

            // 메타데이터 설정
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.length());
            objectMetadata.setContentType("image/jpeg");  // 기본 이미지의 타입을 설정

            // S3 업로드
            try (InputStream inputStream = new FileInputStream(file)) {
                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            }

            // 업로드된 파일 URL 반환
            String fileUrl = amazonS3.getUrl(bucket, fileName).toString();
            log.info("Default image uploaded to S3: {}", fileUrl);
            return fileUrl;
        } catch (IOException | URISyntaxException e) {
            log.error("Failed to upload default image to S3", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload default image", e);
        }
    }

    // 파일 이름 난수화
    public String createFileName(String fileName) {
        System.out.println("파일 이름 난수화");
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    // 파일 이름 정리
    private String sanitizeFileName(String fileName) {
        if (fileName.contains(";type=")) {
            fileName = fileName.split(";type=")[0]; // ';type=' 이전만 남김
            System.out.println("파일 이름 파일 이름 정리");
        }
        System.out.println("파일 이름 파일 이름 정리22");
        return fileName.replaceAll("[^a-zA-Z0-9._-]", ""); // 허용되지 않은 문자 제거

    }

    // 파일 확장자 추출
    private String getFileExtension(String fileName) {
        try {
            System.out.println("파일 이름 확장자추출");
            return fileName.substring(fileName.lastIndexOf("."));

        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 파일 형식: " + fileName);
        }
    }

    public void deleteFile(String fileName) {
        // S3에서 파일 삭제
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, String.valueOf(fileName)));
    }
}