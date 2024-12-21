package com.pawstime.pawstime.domain.post.controller;

import com.pawstime.pawstime.domain.board.service.ReadBoardService;
import com.pawstime.pawstime.domain.post.dto.req.CreatePostReqDto;
import com.pawstime.pawstime.domain.post.dto.req.UpdatePostReqDto;
import com.pawstime.pawstime.domain.post.dto.resp.GetDetailPostRespDto;
import com.pawstime.pawstime.domain.post.dto.resp.GetListPostRespDto;
import com.pawstime.pawstime.domain.post.facade.PostFacade;
import com.pawstime.pawstime.domain.post.service.GetListPostService;
import com.pawstime.pawstime.global.common.ApiResponse;
import com.pawstime.pawstime.global.enums.Status;
import com.pawstime.pawstime.global.exception.CustomException;
import com.pawstime.pawstime.global.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Tag(name = "Post", description = "게시글 API")
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostFacade postFacade;
    private final GetListPostService getListPostService;
    private final ReadBoardService readBoardService;

    @Operation(summary = "게시글 생성", description = "게시글을 생성 할 수 있습니다.")
    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> createPost(@RequestBody CreatePostReqDto req, BindingResult bindingResult) {
        // 요청 값 검증
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ApiResponse.generateResp(Status.INVALID, errorMessage, null);
        }

        // boardId 검증
        if (req.boardId() == null || req.boardId().toString().trim().isEmpty()) {
            return ApiResponse.generateResp(Status.INVALID, "게시판 ID는 빈 값일 수 없습니다.", null);
        }

        try {
            postFacade.createPost(req);
            return ApiResponse.generateResp(Status.CREATE, "게시글 생성이 완료되었습니다.", null);
        } catch (Exception e) {
            return ApiResponse.generateResp(Status.ERROR, "게시글 생성 중 오류가 발생하였습니다 : " + e.getMessage(), null);
        }
    }


    @Operation(summary = "게시글 수정", description = "게시글을 수정할 수 있습니다.")
    @PutMapping("/posts/{postId}")
    public ApiResponse<Void> updatePost(@PathVariable Long postId,
                                        @RequestBody UpdatePostReqDto req,
                                        BindingResult bindingResult) {
        // 유효성 검사 오류 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .findFirst()
                    .orElse("유효하지 않은 입력입니다.");
            return ApiResponse.generateResp(Status.INVALID, errorMessage, null);
        }
        try {
            // Facade에서 예외를 던지도록 처리
            postFacade.updatePost(postId, req);
            return ApiResponse.generateResp(Status.UPDATE, "게시글 수정이 완료되었습니다.", null);
        } catch (CustomException e) {
            // 예외 이름을 이용해서 Enum타입의 Status를 가져옴. ex) InvalidException => INVALID
            Status status = Status.valueOf(e.getClass().getSimpleName().replace("Exception", "").toUpperCase());
            log.info("** {} **", status);  // 예외가 발생한 상태 로깅
            return ApiResponse.generateResp(status, e.getMessage(), null);
        } catch (Exception e) {
            // 기타 예외를 처리하는 로직
            return ApiResponse.generateResp(Status.ERROR, "게시글 수정 중 오류가 발생하였습니다: " + e.getMessage(), null);
        }
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제할 수 있습니다.")
    @DeleteMapping("/posts/{postId}")
    public ApiResponse<Void> deletePost(@PathVariable Long postId) {
        try {
            postFacade.deletePost(postId);  // Facade에서 예외 처리 후 비즈니스 로직 실행
            return ApiResponse.generateResp(Status.DELETE, "게시글 삭제가 완료되었습니다.", null);
        } catch (NotFoundException e) {
            // NotFoundException이 발생하면, 해당 게시글이 존재하지 않거나 이미 삭제된 경우 처리
            return ApiResponse.generateResp(Status.NOTFOUND, e.getMessage(), null);
        } catch (Exception e) {
            // 기타 예외 처리
            return ApiResponse.generateResp(Status.ERROR, "게시글 삭제 중 오류가 발생하였습니다: " + e.getMessage(), null);
        }
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글 ID로 상세 조회를 할 수 있습니다.")
    @GetMapping("/posts/{postId}")
    public ApiResponse<GetDetailPostRespDto> getDetailPost(@PathVariable Long postId) {
        try {
            // 게시글 상세 조회
            GetDetailPostRespDto postRespDto = postFacade.getDetailPost(postId);
            return ApiResponse.generateResp(Status.SUCCESS, "게시글 상세 조회 성공", postRespDto);
        } catch (NotFoundException e) {
            // 게시글이 존재하지 않거나 삭제된 경우 처리
            log.error("게시글 상세 조회 실패: {}", e.getMessage());
            return ApiResponse.generateResp(Status.NOTFOUND, e.getMessage(), null);
        } catch (Exception e) {
            // 기타 예외 처리
            log.error("게시글 조회 중 오류가 발생했습니다: {}", e.getMessage());
            return ApiResponse.generateResp(Status.ERROR, "게시글 조회 중 오류가 발생했습니다: " + e.getMessage(), null);
        }
    }


    @Operation(summary = "게시글 목록 조회", description = "게시글 목록 조회를 할 수 있습니다.")
    @GetMapping("/posts")
    public ApiResponse<List<GetListPostRespDto>> getPosts(
            @RequestParam(required = false) Long boardId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        try {
            // 게시판 ID가 주어진 경우 해당 게시판이 존재하는지 확인
            if (boardId != null && !readBoardService.existsById(boardId)) {
                throw new NotFoundException("존재하지 않는 게시판 ID입니다.");
            }

            // 정렬 및 페이징 처리
            Pageable pageable = createPageable(sort, page, size);

            // 서비스 호출: 게시글 목록 조회
            Page<GetListPostRespDto> posts = getListPostService.getPostList(boardId, keyword, sort, pageable);

            // 게시글 목록 조회 성공
            return ApiResponse.generateResp(Status.SUCCESS, "게시글 목록 조회 성공", posts.getContent());
        } catch (NotFoundException e) {
            // 존재하지 않는 게시판 ID 예외 처리
            return ApiResponse.generateResp(Status.NOTFOUND, e.getMessage(), null);
        } catch (Exception e) {
            // 기타 예외 처리
            return ApiResponse.generateResp(Status.ERROR, "게시글 목록 조회 중 오류가 발생했습니다. " + e.getMessage(), null);
        }
    }

    private Pageable createPageable(String sort, int page, int size) {
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(direction, sortField));
    }
}