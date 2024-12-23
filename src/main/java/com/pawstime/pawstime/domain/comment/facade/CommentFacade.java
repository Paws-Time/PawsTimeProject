package com.pawstime.pawstime.domain.comment.facade;

import com.pawstime.pawstime.domain.comment.dto.req.CreateCommentReqDto;
import com.pawstime.pawstime.domain.comment.dto.resp.GetCommentRespDto;
import com.pawstime.pawstime.domain.comment.entity.Comment;
import com.pawstime.pawstime.domain.comment.service.CreateCommentService;
import com.pawstime.pawstime.domain.comment.service.ReadCommentService;
import com.pawstime.pawstime.domain.post.entity.Post;
import com.pawstime.pawstime.global.exception.InvalidException;
import com.pawstime.pawstime.global.exception.NotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Component
@RequiredArgsConstructor
public class CommentFacade {

  private final ReadCommentService readCommentService;
  private final CreateCommentService createCommentService;

  public void createComment(Long postId, CreateCommentReqDto req) {
    Post post = readCommentService.getPostById(postId);

    if (post == null) {
      throw new NotFoundException("존재하지 않는 게시글 ID입니다.");
      // isDelete = true로 삭제된 게시글도 여기에 걸림.
    }

    if (req.content() == null) {
      throw new InvalidException("댓글 내용은 필수 입력값입니다.");
    }

    createCommentService.createComment(req.of(post));
  }

  @Transactional(readOnly = true)
  public Page<GetCommentRespDto> getCommentAll(
      int pageNo, int pageSize, String sortBy, String direction
  ) {
    Pageable pageable = PageRequest
        .of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sortBy));

    return readCommentService.getCommentAll(pageable).map(GetCommentRespDto::from);
  }

  @Transactional(readOnly = true)
  public Page<GetCommentRespDto> getCommentByPost(
      Long postId, int pageNo, int pageSize, String sortBy, String direction
  ) {
    Post post = readCommentService.getPostById(postId);

    if (post == null) {
      throw new NotFoundException("존재하지 않는 게시글 ID입니다.");
    }

    Pageable pageable = PageRequest
        .of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sortBy));

    return readCommentService.getCommentByPost(postId, pageable).map(GetCommentRespDto::from);
  }

  public void deleteComment(Long commentId) {
    Comment comment = readCommentService.findById(commentId);

    if (comment == null) {
      throw new NotFoundException("존재하지 않는 댓글 ID입니다.");
    }

    if (comment.isDelete()) {
      throw new NotFoundException("이미 삭제된 댓글입니다.");
    }

    comment.softDelete();
    createCommentService.createComment(comment);
  }
}
