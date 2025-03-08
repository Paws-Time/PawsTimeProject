package com.pawstime.pawstime.domain.user.entity;

import com.pawstime.pawstime.domain.post.entity.Post;
import com.pawstime.pawstime.domain.profileImg.entity.ProfileImg;
import com.pawstime.pawstime.domain.user.enums.Role;
import com.pawstime.pawstime.global.entity.BaseEntity;
import jakarta.persistence.*;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long userId;

  private String email;

  private String password;

  private String nick;

  @Enumerated(value = EnumType.STRING)
  private Role role;

  @OneToMany(mappedBy = "user")
  private List<Post> posts;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private ProfileImg profileImg;

  @Column(name = "is_deleted", nullable = false)
  private boolean isDeleted = false; // 기본값 false (활성 상태)

}
