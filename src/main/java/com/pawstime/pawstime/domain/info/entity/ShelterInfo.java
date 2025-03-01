package com.pawstime.pawstime.domain.info.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shelters")
public class ShelterInfo {

  @Id
  @Column(name = "shelter_id")
  private Long shelterId;

  private String name;  // 동물 보호 센터명

  private String type;  // 동물 보호 센터 유형

  private String add1;  // 신주소

  @Column(name = "add_num")
  private int addNum;   // 지역 번호

  private String tel;   // 전화 번호

  private String x;

  private String y;
}
