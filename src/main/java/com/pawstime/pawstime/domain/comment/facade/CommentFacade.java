package com.pawstime.pawstime.domain.comment.facade;

import com.pawstime.pawstime.domain.comment.dto.req.CreateCommentReqDto;
import com.pawstime.pawstime.domain.comment.dto.req.UpdateCommentReqDto;
import com.pawstime.pawstime.domain.comment.dto.resp.CreateCommentRespDto;
import com.pawstime.pawstime.domain.comment.dto.resp.GetCommentRespDto;
import com.pawstime.pawstime.domain.comment.entity.Comment;
import com.pawstime.pawstime.domain.comment.entity.repository.CommentRepository;
import com.pawstime.pawstime.domain.comment.service.CreateCommentService;
import com.pawstime.pawstime.domain.comment.service.ReadCommentService;
import com.pawstime.pawstime.domain.post.dto.resp.GetListPostRespDto;
import com.pawstime.pawstime.domain.post.entity.Post;
import com.pawstime.pawstime.domain.user.entity.User;
import com.pawstime.pawstime.domain.user.service.read.ReadUserService;
import com.pawstime.pawstime.global.exception.ForbiddenException;
import com.pawstime.pawstime.global.exception.InvalidException;
import com.pawstime.pawstime.global.exception.NotFoundException;
import com.pawstime.pawstime.global.exception.UnauthorizedException;
import com.pawstime.pawstime.global.jwt.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
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
  private final JwtUtil jwtUtil;
  private final ReadUserService readUserService;

  public CreateCommentRespDto createComment(Long postId, CreateCommentReqDto req, HttpServletRequest httpServletRequest) {
    Long userId = jwtUtil.getUserIdFromToken(httpServletRequest);

    if (userId == null) {
      throw new UnauthorizedException("로그인해주세요.");
    }

    User user = readUserService.findUserByUserIdQuery(userId);

    Post post = readCommentService.getPostById(postId);

    if (post == null) {
      throw new NotFoundException("존재하지 않는 게시글 ID입니다.");
    }

    // 게시판의 댓글 및 신고 기능 허용 여부 확인
    if (!post.getBoard().getBoardType().isAllowComments()) {
      throw new InvalidException("이 게시판에서는 댓글을 작성할 수 없습니다.");
    }

    if (req.content() == null) {
      throw new InvalidException("댓글 내용은 필수 입력값입니다.");
    }

    // CreateCommentReqDto에서 Comment 객체로 변환
    Comment comment = req.of(post, user); // req.of(post)는 CreateCommentReqDto에서 Comment 엔티티로 변환하는 로직

    // 새로운 댓글 생성
    Comment createdComment = createCommentService.createComment(comment);

    // 생성된 댓글을 기반으로 응답 DTO 생성
    return CreateCommentRespDto.from(createdComment); // 응답 DTO 반환
  }

  @Transactional(readOnly = true)
  public Page<GetCommentRespDto> getCommentAll(
      int pageNo, int pageSize, String sortBy, String direction
  ) {
    Pageable pageable = PageRequest
        .of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sortBy));

    return readCommentService.getCommentAll(pageable).map(comment -> GetCommentRespDto.from(comment, comment.getPost().getBoard().getBoardId()));
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

    return readCommentService.getCommentByPost(postId, pageable).map(comment -> GetCommentRespDto.from(comment, comment.getPost().getBoard().getBoardId()));
  }

  public void deleteComment(Long postId, Long commentId, HttpServletRequest httpServletRequest) {
    Comment comment = readCommentService.findById(commentId);

    if (!comment.getUser().getUserId().equals(jwtUtil.getUserIdFromToken(httpServletRequest))) {
      if (!jwtUtil.getUserRoleFromToken(httpServletRequest).equals("ADMIN")) {
        throw new ForbiddenException("권한이 없습니다.");
      }
    }

    if (comment == null) {
      throw new NotFoundException("존재하지 않는 댓글 ID입니다.");
    }

    if (comment.isDelete()) {
      throw new NotFoundException("이미 삭제된 댓글입니다.");
    }

    if (!comment.getPost().getPostId().equals(postId)) {
      throw new InvalidException("잘못된 요청입니다. 해당 댓글이 지정된 게시글에 존재하지 않습니다.");
    }

    comment.softDelete();
    createCommentService.createComment(comment);
  }

  public void updateComment(Long postId, Long commentId, UpdateCommentReqDto req, HttpServletRequest httpServletRequest){
    //입력받은 commentId로 해당 댓글 조회
    Comment comment = readCommentService.findById(commentId);

    if (!comment.getUser().getUserId().equals(jwtUtil.getUserIdFromToken(httpServletRequest))) {
      if (!jwtUtil.getUserRoleFromToken(httpServletRequest).equals("ADMIN")) {
        throw new ForbiddenException("권한이 없습니다.");
      }
    }

    if (comment == null) {
      throw new NotFoundException("존재하지 않는 댓글입니다.");
    }
    // 댓글이 삭제 상태인지 확인
    if(comment.isDelete()){
      throw new NotFoundException("이미 삭제된 댓글입니다.");
    }

    if (!comment.getPost().getPostId().equals(postId)) {
      throw new InvalidException("잘못된 요청입니다. 해당 댓글이 지정된 게시글에 존재하지 않습니다.");
    }

    //수정된 내용 반영
    comment.updateComment(req.content());

    //변경된 댓글 저장
    createCommentService.createComment(comment);
  }

  public Page<GetCommentRespDto> getCommentListByUser(int pageNo, int pageSize, String sortBy, String direction, HttpServletRequest httpServletRequest) {
    Long userId = jwtUtil.getUserIdFromToken(httpServletRequest);
    User user = readUserService.findUserByUserIdQuery(userId);

    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sortBy));

    return readCommentService.findByUser(pageable, user).map(comment -> GetCommentRespDto.from(comment, comment.getPost().getBoard().getBoardId()));
  }
}
