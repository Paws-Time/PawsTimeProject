package com.pawstime.pawstime.domain.image.service;

import com.pawstime.pawstime.aws.s3.service.S3Service;
import com.pawstime.pawstime.domain.image.entity.Image;
import com.pawstime.pawstime.domain.image.entity.repository.ImageRepository;
import com.pawstime.pawstime.domain.post.entity.Post;
import com.pawstime.pawstime.global.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UpdateImageService {

    private final ImageRepository imageRepository;
    private final S3Service s3Service;

    @Transactional
    public List<Long> deleteImagesFromPost(List<Long> deletedImageIds, Post post) {
        List<Long> deletedImageIdsList = new ArrayList<>();

        for (Long imageId : deletedImageIds) {
            Image image = imageRepository.findById(imageId)
                    .orElseThrow(() -> new NotFoundException("존재하지 않는 이미지 ID입니다."));

            String imageUrl = image.getImageUrl();
            String fileName = extractFileNameFromUrl(imageUrl);

            // S3에서 파일 삭제
            try {
                s3Service.deleteFile(fileName);
            } catch (Exception e) {
                throw new RuntimeException("S3 파일 삭제 실패: " + fileName, e);
            }

            post.getImages().remove(image);
            imageRepository.delete(image);
            deletedImageIdsList.add(imageId);
        }

        return deletedImageIdsList;
    }

    @Transactional
    public List<Map<String, Object>> addNewImagesToPost(List<MultipartFile> newImages, Post post) {
        List<Map<String, Object>> addedImages = new ArrayList<>();

        for (MultipartFile newImage : newImages) {
            String uploadedUrl;
            try {
                uploadedUrl = s3Service.uploadFile(Collections.singletonList(newImage)).get(0);
            } catch (Exception e) {
                throw new RuntimeException("S3 업로드 실패", e);
            }

            Image image = Image.builder()
                    .imageUrl(uploadedUrl)
                    .post(post)
                    .build();

            imageRepository.save(image);
            post.getImages().add(image);

            Map<String, Object> imageInfo = new HashMap<>();
            imageInfo.put("imageId", image.getImageId());
            imageInfo.put("imageUrl", uploadedUrl);
            addedImages.add(imageInfo);
        }

        return addedImages;
    }

    public String extractFileNameFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            String path = url.getPath();
            return path.substring(path.lastIndexOf("/") + 1);
        } catch (MalformedURLException e) {
            throw new RuntimeException("잘못된 URL입니다: " + imageUrl, e);
        }
    }
}
