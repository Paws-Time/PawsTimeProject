package com.pawstime.pawstime.domain.info.entity.repository;

import com.pawstime.pawstime.domain.info.entity.ShelterInfo;
import com.pawstime.pawstime.domain.info.entity.ShelterOperationInfo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShelterInfoRepository extends JpaRepository<ShelterInfo, Long> {

  @Query("SELECT s FROM ShelterInfo s WHERE s.addNum = :addNum")
  Page<ShelterInfo> findAllQuery(Pageable pageable, int addNum);
}
