package com.pawstime.pawstime.domain.post.controller;

import com.pawstime.pawstime.domain.post.dto.req.CreatePostReqDto;
import com.pawstime.pawstime.domain.post.facade.PostFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
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
  public ResponseEntity<String> createPost(@RequestBody @Valid CreatePostReqDto req, BindingResult bindingResult){
    // 유효성 검사 오류가 있을 경우 처리
    if (bindingResult.hasErrors()) {
      StringBuilder errorMessages = new StringBuilder();
      bindingResult.getAllErrors().forEach(error ->
          errorMessages.append(error.getDefaultMessage()).append("\n")
      );
      return ResponseEntity.badRequest().body("유효성 검사 실패: \n" + errorMessages.toString());
    }
    try{
      postFacade.createPost(req);
      return ResponseEntity.ok().body("게시글 생성이 완료되었습니다.");
    }catch (Exception e){
      return ResponseEntity.badRequest().body("게시글 생성 중 오류가 발생했습니다. "+ e.getMessage());
    }
  }
}