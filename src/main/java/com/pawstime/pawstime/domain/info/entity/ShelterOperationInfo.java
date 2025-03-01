package com.pawstime.pawstime.domain.info.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "shelters_operation")
public class ShelterOperationInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "shelter_operation_id")
  private Long ShelterOperationId;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "shelter_id", nullable = false)
  private ShelterInfo shelterInfo;

  @Column(name = "save_trgt_animal")
  private String saveTrgtAnimal;  // 구조 대상 동물

  @Column(name = "add2")
  private String add2;  // 구주소

  @Column(name = "week_opr_stime")
  private String weekOprStime;  // 평일 운영 시작 시각

  @Column(name = "week_opr_etime")
  private String weekOprEtime;  // 평일 운영 종료 시각

  @Column(name = "close_day")
  private String closeDay;  // 휴무일

  @Column(name = "vet_person_cnt")
  private int vetPersonCnt;  // 수의사 인원 수

  @Column(name = "specs_person_cnt")
  private int specsPersonCnt;  // 사양관리사 인원 수

  @Column(name = "week_cell_stime")
  private String weekCellStime;  // 평일 분양 시작 시각

  @Column(name = "week_cell_etime")
  private String weekCellEtime;  // 평일 분양 종료 시각

  @Column(name = "medical_cnt")
  private int medicalCnt;  // 진료실 수

  @Column(name = "breed_cnt")
  private int breedCnt;  // 사육실 수

  @Column(name = "quarantine_cnt")
  private int quarantineCnt;  // 격리실 수

  @Column(name = "feed_cnt")
  private int feedCnt;  // 사료 보관실 수

  @Column(name = "trans_car_cnt")
  private int transCarCnt;  // 구조 운반용 차량 보유 대수

  @Column(name = "weekend_stime")
  private String weekendStime;  // 주말 운영 시작 시각

  @Column(name = "weekend_etime")
  private String weekendEtime;  // 주말 운영 종료 시각

  @Column(name = "weekend_cell_stime")
  private String weekendCellStime;  // 주말 분양 시작 시각

  @Column(name = "weekend_cell_etime")
  private String weekendCellEtime;  // 주말 분양 종료 시각
}
