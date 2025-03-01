package com.pawstime.pawstime.domain.info.service;

import com.pawstime.pawstime.domain.info.entity.ShelterInfo;
import com.pawstime.pawstime.domain.info.entity.ShelterOperationInfo;
import com.pawstime.pawstime.domain.info.entity.repository.ShelterInfoRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReadShelterInfoService {

  private final ShelterInfoRepository infoRepository;

  public Page<ShelterInfo> readAllShelter(Pageable pageable, int addNum) {
    return infoRepository.findAllQuery(pageable, addNum);
  }
}
