package com.pawstime.pawstime.domain.post.controller;

import com.pawstime.pawstime.aws.s3.service.S3Service;
import com.pawstime.pawstime.domain.image.dto.resp.GetImageRespDto;
import com.pawstime.pawstime.domain.like.facade.LikeFacade;
import com.pawstime.pawstime.domain.post.dto.req.CreatePostReqDto;
import com.pawstime.pawstime.domain.post.dto.req.UpdatePostReqDto;
import com.pawstime.pawstime.domain.post.dto.resp.GetDetailPostRespDto;
import com.pawstime.pawstime.domain.post.dto.resp.GetListPostRespDto;
import com.pawstime.pawstime.domain.post.facade.PostFacade;
import com.pawstime.pawstime.global.common.ApiResponse;
import com.pawstime.pawstime.global.enums.Status;
import com.pawstime.pawstime.global.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@Tag(name = "Post", description = "게시글 API")
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

  private final PostFacade postFacade;
  private final S3Service s3Service;

  @Operation(summary = "게시글 생성", description = "게시글을 생성할 수 있습니다.")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<ApiResponse<Long>> createPost(
      @RequestBody CreatePostReqDto req,
      HttpServletRequest request) {
    Long postId = postFacade.createPost(req, request);
    return ApiResponse.generateResp(Status.CREATE, "게시글 생성이 완료되었습니다. ", postId);
  }

  @Operation(summary = "게시글 이미지 업로드", description = "이미지 업로드 후 게시글과 연결합니다.")
  @PostMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<Void>> uploadImages(
      @PathVariable Long postId,
      @RequestPart(value = "images", required = false) List<MultipartFile> images) {

    List<String> imageUrls = new ArrayList<>();

    // 이미지가 존재하면 S3에 업로드 후 URL 리스트 받기
    if (images != null && !images.isEmpty()) {
      imageUrls = s3Service.uploadFile(images);
    }
    // 게시글에 이미지 추가 (기본 이미지 처리 포함)
    postFacade.addImagesToPost(postId, imageUrls);
    return ApiResponse.generateResp(Status.CREATE, "게시글과 이미지가 성공적으로 업로드되었습니다.", null);
  }

  @Operation(summary = "게시글 수정", description = "게시글을 수정할 수 있습니다.")
  @PutMapping("/{postId}")
  public ResponseEntity<ApiResponse<Void>> updatePost(
      @PathVariable Long postId,
      @RequestBody UpdatePostReqDto req,
      HttpServletRequest httpServletRequest
  ) {
    postFacade.updatePost(postId, req, httpServletRequest);
    return ApiResponse.generateResp(Status.UPDATE, "게시글 수정이 완료되었습니다.", null);
  }

  @Operation(summary = "게시글 이미지 수정", description = "기존 이미지를 삭제하거나 새로운 이미지를 추가합니다.")
  @PutMapping(value = "/{postId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<Void>> updatePostImages(
      @PathVariable Long postId,
      @RequestParam(value = "deletedImageIds", required = false) List<Long> deletedImageIds,
      @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages,
      HttpServletRequest httpServletRequest) {

    postFacade.updatePostImages(postId, deletedImageIds, newImages, httpServletRequest);
    return ApiResponse.generateResp(Status.UPDATE, "게시글 이미지가 수정되었습니다.", null);
  }

  @Operation(summary = "게시글 삭제", description = "게시글을 삭제할 수 있습니다.")
  @DeleteMapping("/{postId}")
  public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long postId, HttpServletRequest httpServletRequest) {
      postFacade.deletePost(postId, httpServletRequest);
    return ApiResponse.generateResp(Status.DELETE, "게시글 삭제가 완료되었습니다.", null);
  }

  @Operation(summary = "게시글 상세 조회", description = "게시글 ID로 상세 조회를 할 수 있습니다.")
  @GetMapping("/{postId}")
  public ResponseEntity<ApiResponse<GetDetailPostRespDto>> getDetailPost(@PathVariable Long postId) {
    GetDetailPostRespDto postRespDto = postFacade.getDetailPost(postId);
    return ApiResponse.generateResp(Status.SUCCESS, "게시글 상세 조회 성공", postRespDto);
  }

  @Operation(summary = "게시글 목록 조회", description = "게시글 목록 조회를 할 수 있습니다.")
  @GetMapping
  public ResponseEntity<ApiResponse<List<GetListPostRespDto>>> getPosts(
      @RequestParam(required = false) Long boardId,
      @RequestParam(required = false) String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "createdAt,desc") String sort) {

    // Pageable 객체 생성
    Pageable pageable = createPageable(sort, page, size);
    // Facade 호출: 게시글 목록 조회
    Page<GetListPostRespDto> posts = postFacade.getPostList(boardId, keyword, pageable, sort.split(",")[0], sort.split(",")[1]);
    return ApiResponse.generateResp(Status.SUCCESS, "게시글 목록 조회 성공", posts.getContent());
  }

  @Operation(summary = "게시글별 대표 이미지 조회")
  @GetMapping("/{postId}/thumbnail")
  public ResponseEntity<ApiResponse<List<GetImageRespDto>>> getThumbnail(@PathVariable Long postId) {
    try {
      return ApiResponse.generateResp(Status.SUCCESS, null, postFacade.getThumbnail(postId).getContent());
    } catch (CustomException e) {
      Status status = Status.valueOf(e.getClass()
          .getSimpleName()
          .replace("Exception", "")
          .toUpperCase());
      return ApiResponse.generateResp(status, e.getMessage(), null);
    } catch (Exception e) {
      return ApiResponse.generateResp(
          Status.ERROR, "썸네일 이미지 조회 중 오류가 발생하였습니다 : " + e.getMessage(), null);
      }
  }

  @Operation(summary = "게시글별 이미지 전체 조회")
  @GetMapping("/{postId}/images")
  public ResponseEntity<ApiResponse<List<GetImageRespDto>>> getImages(@PathVariable Long postId) {
    try {
      return ApiResponse.generateResp(Status.SUCCESS, null, postFacade.getImages(postId));
    } catch (CustomException e) {
      Status status = Status.valueOf(e.getClass()
          .getSimpleName()
          .replace("Exception", "")
          .toUpperCase());
      return ApiResponse.generateResp(status, e.getMessage(), null);
    } catch (Exception e) {
      return ApiResponse.generateResp(
          Status.ERROR, "게시글별 이미지 전체 조회 중 오류가 발생하였습니다 : " + e.getMessage(), null);
      }
  }

  @Operation(summary = "메인페이지에서 사용할 랜덤 이미지 조회")
  @GetMapping("/images/random")
  public ResponseEntity<ApiResponse<List<GetImageRespDto>>> getRandomImages() {
    try {
      return ApiResponse.generateResp(Status.SUCCESS, null, postFacade.getRandomImages().getContent());
    } catch (CustomException e) {
      Status status = Status.valueOf(e.getClass()
          .getSimpleName()
          .replace("Exception", "")
          .toUpperCase());
      return ApiResponse.generateResp(status, e.getMessage(), null);
    } catch (Exception e) {
      return ApiResponse.generateResp(
          Status.ERROR, "랜덤 이미지 조회 중 오류가 발생하였습니다 : " + e.getMessage(), null);
    }
  }

  // Pageable 객체 생성 (정렬 처리)
  public Pageable createPageable(String sort, int page, int size) {
    String[] sortParams = sort.split(",");

    // 기본값 처리: sort 파라미터가 잘못된 경우 'createdAt'으로 기본 설정
    String sortField = sortParams.length > 0 ? sortParams[0] : "createdAt";
    String direction = sortParams.length > 1 ? sortParams[1] : "desc";

    // Direction 설정
    Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

    // Pageable 객체 생성
    return PageRequest.of(page, size, Sort.by(sortDirection, sortField));
  }
}