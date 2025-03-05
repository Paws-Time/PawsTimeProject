package com.pawstime.pawstime.domain.profileImg.entity.repository;

import com.pawstime.pawstime.domain.profileImg.entity.ProfileImg;
import com.pawstime.pawstime.domain.user.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileImg, Long> {

    @Query("SELECT p FROM ProfileImg p WHERE p.user.userId = :userId")
    Optional<ProfileImg> findProfileImgByUserId(@Param("userId") Long userId);

}
