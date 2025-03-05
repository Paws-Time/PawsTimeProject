package com.pawstime.pawstime.domain.profileImg.facade;

import com.pawstime.pawstime.aws.s3.service.S3Service;
import com.pawstime.pawstime.domain.profileImg.entity.ProfileImg;
import com.pawstime.pawstime.domain.profileImg.service.CreateProfileImgService;
import com.pawstime.pawstime.domain.profileImg.service.ReadProfileImgService;

import com.pawstime.pawstime.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

@Slf4j
@Transactional
@Component
@RequiredArgsConstructor
public class ProfileImgFacade {

    private final ReadProfileImgService readProfileImgService;
    private final CreateProfileImgService createProfileImgService;
    private final S3Service s3Service;

    @Value("${default.profile-img-url}")
    private String defaultProfileImgUrl;

    //1.프로필 이미지 변경
    public void updateProfileImg(Long userId, MultipartFile file){
        ProfileImg profileImg = readProfileImgService.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        //s3에 이미지 업로드 후 url 반환
        String newImgUrl = s3Service.uploadFile(Collections.singletonList(file)).get(0);

        //기존 프로필 이미지 업데이트
        profileImg.updateProfileImgUrl(newImgUrl);
        createProfileImgService.save(profileImg);


    }

/*    @Transactional
    public void deleteProfileImg(Long userId) {
        ProfileImg profileImg = readProfileImgService.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("프로필을 찾을 수 없습니다."));

        profileImg.updateProfileImgUrl(defaultProfileImgUrl); // 기본 이미지로 변경
        createProfileImgService.save(profileImg);
    }*/
}
