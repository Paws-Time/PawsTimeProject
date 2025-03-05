package com.pawstime.pawstime.domain.profileImg.controller;

import com.pawstime.pawstime.domain.profileImg.facade.ProfileImgFacade;
import com.pawstime.pawstime.global.common.ApiResponse;
import com.pawstime.pawstime.global.enums.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = " ProfileImg", description = "프로필 이미지 API")
@RestController
@RequestMapping("/profileImg")
@RequiredArgsConstructor
public class ProfileImgController {

    private final ProfileImgFacade profileImgFacade;

    @Operation(summary = "프로필 이미지 변경", description = "프로필 이미지를 변경할 수 있습니다.")
    @PutMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> updateProfileImg(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file
            ){
        profileImgFacade.updateProfileImg(userId, file);
        return ApiResponse.generateResp(
                Status.UPDATE, "프로필 수정이 완료되었습니다.", null
        );
    }
}
