package com.pawstime.pawstime.domain.user.entity.repository;

import com.pawstime.pawstime.domain.profileImg.entity.ProfileImg;
import com.pawstime.pawstime.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

  User findUserByEmail(String email);

  @Query("SELECT u FROM User u WHERE u.userId = :userId AND u.isDelete = false")
  User findUserByUserIdQuery(@Param("userId") Long userId);



}
