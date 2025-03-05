package com.pawstime.pawstime.domain.profileImg.service;

import com.pawstime.pawstime.domain.profileImg.entity.ProfileImg;
import com.pawstime.pawstime.domain.profileImg.entity.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateProfileImgService {

    private final ProfileRepository profileRepository;

    @Transactional
    public ProfileImg save(ProfileImg profileImg){
        return profileRepository.save(profileImg);
    }

}
