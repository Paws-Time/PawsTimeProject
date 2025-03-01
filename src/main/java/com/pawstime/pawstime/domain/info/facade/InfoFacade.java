package com.pawstime.pawstime.domain.info.facade;

import com.pawstime.pawstime.domain.info.dto.resp.GetHospitalInfoRespDto;
import com.pawstime.pawstime.domain.info.dto.resp.GetShelterInfoRespDto;
import com.pawstime.pawstime.domain.info.dto.resp.GetShelterOperationInfoRespDto;
import com.pawstime.pawstime.domain.info.entity.ShelterOperationInfo;
import com.pawstime.pawstime.domain.info.service.ReadHospitalInfoService;
import com.pawstime.pawstime.domain.info.service.ReadShelterInfoService;
import com.pawstime.pawstime.global.exception.NotFoundException;
import java.net.ContentHandler;
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
public class InfoFacade {

    private final ReadHospitalInfoService readHospitalInfoService;
    private final ReadShelterInfoService readShelterInfoService;

    public Page<GetHospitalInfoRespDto> readHospitalInfo(int pageNo, int pageSize, String sortBy, String direcrion, int addNum) {

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direcrion), sortBy));

        return readHospitalInfoService.readAllHospital(pageable, addNum).map(GetHospitalInfoRespDto::from);
    }

  public Page<GetShelterInfoRespDto> readShelterInfo(int pageNo, int pageSize, String sortBy, String direction, int addNum) {

      Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sortBy));

      return readShelterInfoService.readAllShelter(pageable, addNum).map(GetShelterInfoRespDto::from);
  }
}