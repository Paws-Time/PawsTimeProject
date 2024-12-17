package com.pawstime.pawstime.domain.post.controller;

import com.pawstime.pawstime.domain.post.dto.req.CreatePostReqDto;
import com.pawstime.pawstime.domain.post.dto.req.UpdatePostReqDto;
import com.pawstime.pawstime.domain.post.facade.PostFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Post", description = "게시글 API")
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

  private final PostFacade postFacade;

  @Operation(summary = "게시글 생성", description = "새로운 게시글을 생성할 수 있습니다.")
  @PostMapping("/posts")
  public ResponseEntity<String> createPost(@RequestBody @Valid CreatePostReqDto req,
      BindingResult bindingResult) {
    // 유효성 검사 오류가 있을 경우 처리
    if (bindingResult.hasErrors()) {
      StringBuilder errorMessages = new StringBuilder();
      bindingResult.getAllErrors().forEach(error ->
          errorMessages.append(error.getDefaultMessage()).append("\n")
      );
      return ResponseEntity.badRequest().body("유효성 검사 실패: \n" + errorMessages.toString());
    }
    try {
      postFacade.createPost(req);
      return ResponseEntity.ok().body("게시글 생성이 완료되었습니다.");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("게시글 생성 중 오류가 발생했습니다. " + e.getMessage());
    }
  }

  @Operation(summary = "게시글 수정", description = "게시글을 수정할 수 있습니다.")
  @PutMapping("/posts/{postId}")
  public ResponseEntity<String> updatePost(@PathVariable Long postId,
      @RequestBody UpdatePostReqDto req, BindingResult bindingResult) {
    {
      // 유효성 검사 오류 처리
      if (bindingResult.hasErrors()) {
        String errorMessage = bindingResult.getFieldErrors().stream()
            .map(error -> error.getDefaultMessage())
            .findFirst()
            .orElse("유효하지 않은 입력입니다.");
        return ResponseEntity.badRequest().body(errorMessage);
      }
      try {
        postFacade.updatePost(postId, req);
        return ResponseEntity.ok().body("게시글 수정이 완료되었습니다.");
      } catch (Exception e) {
        return ResponseEntity.badRequest().body("게시글 수정 중 오류가 발생했습니다. " + e.getMessage());
      }
    }
  }
}