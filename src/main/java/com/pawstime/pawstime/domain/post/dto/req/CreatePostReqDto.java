package com.pawstime.pawstime.domain.post.dto.req;

import com.pawstime.pawstime.domain.board.entity.Board;
import com.pawstime.pawstime.domain.post.entity.Post;
import com.pawstime.pawstime.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePostReqDto(
        @Schema(description = "게시글 제목", example = "우리 집 구리 보러오세요")
        @NotBlank(message = "제목은 필수 입력값입니다.")
        String title,           // 제목
        @Schema(description = "게시글 내용", example = "일상 내용")
        @NotBlank(message = "내용은 필수 입력값입니다.")
        String content,         // 내용
        @NotNull(message = "게시판 ID는 필수 입력값입니다.")
        Long boardId,           // 게시판 ID
        int likesCount          // 좋아요 수
) {
    public Post toEntity(Board board, User user) {
        return Post.builder()
                .title(this.title)
                .content(this.content)
                .board(board)
                .user(user)
                .likesCount(likesCount)  // likesCount 설정
                .views(0)  // 기본 조회수 설정
                .build();
    }
}