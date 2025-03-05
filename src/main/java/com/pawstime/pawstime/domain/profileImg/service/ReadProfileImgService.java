package com.pawstime.pawstime.domain.profileImg.service;

import com.pawstime.pawstime.domain.profileImg.entity.ProfileImg;
import com.pawstime.pawstime.domain.profileImg.entity.repository.ProfileRepository;
import com.pawstime.pawstime.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReadProfileImgService {

    private final ProfileRepository profileRepository;

    //유저의 프로필 이미지 조회
    public Optional<ProfileImg> findByUserId(Long userId){
        return profileRepository.findProfileImgByUserId(userId);
    }
}
