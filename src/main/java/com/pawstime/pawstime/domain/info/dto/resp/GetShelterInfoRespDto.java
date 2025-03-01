package com.pawstime.pawstime.domain.info.dto.resp;

import com.pawstime.pawstime.domain.info.entity.ShelterInfo;
import lombok.Builder;

@Builder
public record GetShelterInfoRespDto(
    Long shelterId,
    String name,
    String type,
    String add1,
    Integer addNum,
    Double x,
    Double y,
    String tel
) {

  public static GetShelterInfoRespDto from(ShelterInfo info) {
    return GetShelterInfoRespDto.builder()
        .shelterId(info.getShelterId())
        .name(info.getName())
        .type(info.getType())
        .add1(info.getAdd1())
        .addNum(info.getAddNum())
        .x(Double.parseDouble(info.getX()))
        .y(Double.parseDouble(info.getY()))
        .tel(info.getTel())
        .build();
  }
}
