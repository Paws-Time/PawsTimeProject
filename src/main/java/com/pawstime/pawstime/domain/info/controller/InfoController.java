package com.pawstime.pawstime.domain.info.controller;


import com.pawstime.pawstime.domain.info.dto.resp.GetHospitalInfoRespDto;
import com.pawstime.pawstime.domain.info.dto.resp.GetShelterInfoRespDto;
import com.pawstime.pawstime.domain.info.dto.resp.GetShelterOperationInfoRespDto;
import com.pawstime.pawstime.domain.info.facade.InfoFacade;
import com.pawstime.pawstime.global.common.ApiResponse;
import com.pawstime.pawstime.global.enums.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@Tag(name = "info", description = "정보게시판 API")
@RestController
@RequestMapping("/info")
@RequiredArgsConstructor
public class InfoController {

  private final InfoFacade infoFacade;

  @Operation(summary = "지역별 동물 병원 정보 목록 조회")
  @GetMapping("/hospitals/{addNum}")
  public ResponseEntity<ApiResponse<List<GetHospitalInfoRespDto>>> getHospitalInfo(
      @RequestParam(defaultValue = "0") int pageNo,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(defaultValue = "name") String sortBy,
      @RequestParam(defaultValue = "DESC") String direction,
      @PathVariable int addNum
  ) {
      return ApiResponse.generateResp(
          Status.SUCCESS, null, infoFacade.readHospitalInfo(pageNo, pageSize, sortBy, direction, addNum).getContent());
  }

  @Operation(summary = "지역별 동물 보호소 정보 목록 조회 (필수정보 조회 기능)")
  @GetMapping("/shelters/{addNum}")
  public ResponseEntity<ApiResponse<List<GetShelterInfoRespDto>>> getShelterInfo(
      @RequestParam(defaultValue = "0") int pageNo,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(defaultValue = "name") String sortBy,
      @RequestParam(defaultValue = "DESC") String direction,
      @PathVariable int addNum
  ) {
    return ApiResponse.generateResp(
        Status.SUCCESS, null, infoFacade.readShelterInfo(pageNo, pageSize, sortBy, direction, addNum).getContent());
  }
}
