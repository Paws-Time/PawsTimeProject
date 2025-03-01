package com.pawstime.pawstime.domain.info.dto.resp;

import com.pawstime.pawstime.domain.info.entity.ShelterOperationInfo;
import lombok.Builder;

@Builder
public record GetShelterOperationInfoRespDto(
    Long shelterOperationId,
    String saveTrgtAnimal,
    String add2,
    String weekOprStime,
    String weekOprEtime,
    String closeDay,
    int vetPersonCnt,
    int specsPersonCnt,
    String weekCellStime,
    String weekCellEtime,
    int medicalCnt,
    int breedCnt,
    int quarantineCnt,
    int feedCnt,
    int transCarCnt,
    String weekendStime,
    String weekendEtime,
    String weekendCellStime,
    String weekendCellEtime
) {

  public static GetShelterOperationInfoRespDto from(ShelterOperationInfo info) {
    return GetShelterOperationInfoRespDto.builder()
        .shelterOperationId(info.getShelterOperationId())
        .saveTrgtAnimal(info.getSaveTrgtAnimal())
        .add2(info.getAdd2())
        .weekOprStime(info.getWeekOprStime())
        .weekOprEtime(info.getWeekOprEtime())
        .closeDay(info.getCloseDay())
        .vetPersonCnt(info.getVetPersonCnt())
        .specsPersonCnt(info.getSpecsPersonCnt())
        .weekCellStime(info.getWeekCellStime())
        .weekCellEtime(info.getWeekCellEtime())
        .medicalCnt(info.getMedicalCnt())
        .breedCnt(info.getBreedCnt())
        .quarantineCnt(info.getQuarantineCnt())
        .feedCnt(info.getFeedCnt())
        .transCarCnt(info.getTransCarCnt())
        .weekendStime(info.getWeekendStime())
        .weekendEtime(info.getWeekendEtime())
        .weekendCellStime(info.getWeekendCellStime())
        .weekendCellEtime(info.getWeekendCellEtime())
        .build();
  }
}

