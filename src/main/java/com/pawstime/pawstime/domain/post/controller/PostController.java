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
import org.springframework.http.ResponseEntity;
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
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<Void>> createPost(@RequestBody CreatePostReqDto req) {
        try {
            postFacade.createPost(req);
            return ApiResponse.generateResp(Status.CREATE, "게시글 생성이 완료되었습니다.", null);
        } catch (CustomException e) {
            Status status = Status.valueOf(e.getClass().getSimpleName().replace("Exception", "").toUpperCase());
            // 예외 이름을 이용해서 Enum타입의 Status를 가져옴. ex) InvalidException => INVALID
            log.info("** {} **", status);
            return ApiResponse.generateResp(status, e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.generateResp(Status.ERROR, "게시글 생성 중 오류가 발생하였습니다 : " + e.getMessage(), null);
        }
    }

    @Operation(summary = "게시글 수정", description = "게시글을 수정할 수 있습니다.")
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePost(@Valid @PathVariable Long postId,
                                                        @RequestBody UpdatePostReqDto req) {
        try {
            // Facade에서 예외를 던지도록 처리
            postFacade.updatePost(postId, req);
            return ApiResponse.generateResp(Status.UPDATE, "게시글 수정이 완료되었습니다.", null);
        } catch (CustomException e) {
            Status status = Status.valueOf(e.getClass().getSimpleName().replace("Exception", "").toUpperCase());
            log.info("** {} **", status);
            return ApiResponse.generateResp(status, e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.generateResp(Status.ERROR, "게시글 수정 중 오류가 발생하였습니다: " + e.getMessage(), null);
        }
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제할 수 있습니다.")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long postId) {
        try {
            postFacade.deletePost(postId);
            return ApiResponse.generateResp(Status.DELETE, "게시글 삭제가 완료되었습니다.", null);
        } catch (NotFoundException e) {
            // NotFoundException이 발생하면 해당 게시글이 존재하지 않거나 이미 삭제된 경우 처리
            return ApiResponse.generateResp(Status.NOTFOUND, e.getMessage(), null);
        } catch (Exception e) {
            // 기타 예외 처리
            return ApiResponse.generateResp(Status.ERROR, "게시글 삭제 중 오류가 발생하였습니다: " + e.getMessage(), null);
        }
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글 ID로 상세 조회를 할 수 있습니다.")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<GetDetailPostRespDto>> getDetailPost(@PathVariable Long postId) {
        try {
            GetDetailPostRespDto postRespDto = postFacade.getDetailPost(postId);
            return ApiResponse.generateResp(Status.SUCCESS, "게시글 상세 조회 성공", postRespDto);
        } catch (CustomException e) {
            Status status = Status.valueOf(e.getClass().getSimpleName().replace("Exception", "").toUpperCase());
            return ApiResponse.generateResp(status, e.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.generateResp(Status.ERROR, "게시글 조회 중 오류가 발생했습니다: " + e.getMessage(), null);
        }
    }

    @Operation(summary = "게시글 목록 조회", description = "게시글 목록 조회를 할 수 있습니다.")
    @GetMapping()
    public ResponseEntity<ApiResponse<List<GetListPostRespDto>>> getPosts(
            @RequestParam(required = false) Long boardId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        try {
            // Pageable 객체 생성
            Pageable pageable = createPageable(sort, page, size);

            // Facade 호출: 게시글 목록 조회
            Page<GetListPostRespDto> posts = postFacade.getPostList(boardId, keyword, pageable);

            // 성공 응답
            return ApiResponse.generateResp(Status.SUCCESS, "게시글 목록 조회 성공", posts.getContent());
        } catch (NotFoundException e) {
            // 존재하지 않는 게시판 ID 예외 처리
            return ApiResponse.generateResp(Status.NOTFOUND, e.getMessage(), null);
        } catch (Exception e) {
            // 기타 예외 처리
            return ApiResponse.generateResp(Status.ERROR, "게시글 목록 조회 중 오류가 발생했습니다. " + e.getMessage(), null);
        }
    }

    public Pageable createPageable(String sort, int page, int size) {
        // sort 파라미터를 ','로 구분하여 정렬 조건을 설정
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortBy = Sort.by(direction, sortParams[0]);

        // Pageable 객체 생성
        return PageRequest.of(page, size, sortBy);
    }
}