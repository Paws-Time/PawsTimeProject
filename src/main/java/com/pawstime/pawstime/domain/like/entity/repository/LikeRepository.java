package com.pawstime.pawstime.domain.like.entity.repository;


import com.pawstime.pawstime.domain.like.entity.Like;
import com.pawstime.pawstime.domain.post.entity.Post;
import com.pawstime.pawstime.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    // 특정 게시글과 사용자의 좋아요 관계를 찾는 쿼리
    Optional<Like> findByPost(Post post);

    // 특정 게시글에 대한 좋아요 수를 가져오는 쿼리
    long countByPost(Post post);
}
