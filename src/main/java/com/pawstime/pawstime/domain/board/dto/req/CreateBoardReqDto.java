package com.pawstime.pawstime.domain.board.dto.req;

import com.pawstime.pawstime.domain.board.entity.Board;
import com.pawstime.pawstime.domain.board.enums.BoardType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateBoardReqDto(
        @Schema(description = "게시판 제목", example = "일상 게시판")
    String title,

        @Schema(description = "게시판 설명", example = "일상 게시판을 활용하여 반려동물과의 일상을 기록해보세요.")
    String description,

        @Schema(description = "게시판 유형", example = "GENERAL")
        BoardType boardType  // BoardType 추가
) {

  public Board of() {
    return Board.builder()
        .title(this.title)
        .description(this.description)
        .boardType(this.boardType)
        .allowComments(this.boardType.isAllowComments()) // BoardType에 따라 댓글 허용 여부 설정
        .allowReports(this.boardType.isAllowReports())
        .build();
  }
}
