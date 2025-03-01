package com.pawstime.pawstime.domain.profileImg.entity;

import com.pawstime.pawstime.domain.user.entity.User;
import com.pawstime.pawstime.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ProfileImg extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_img")
    private Long profileImgId;

    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    @Column(nullable = false)
    private String imgUrl;

}
