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
@Table(name = "profile_img")
public class ProfileImg extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_img_id", nullable = false, updatable = false)
    private Long profileImgId;

    @OneToOne
    @JoinColumn(name="user_id", unique = true)
    private User user;

    @Column(nullable = false)
    private String profileImgUrl;

    public void updateProfileImgUrl(String newProfileImgUrl){
        this.profileImgUrl = newProfileImgUrl;
    }

}
