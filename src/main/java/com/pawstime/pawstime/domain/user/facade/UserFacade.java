package com.pawstime.pawstime.domain.user.facade;

import com.pawstime.pawstime.domain.tokenBlacklist.service.TokenBlacklistService;
import com.pawstime.pawstime.domain.user.entity.User;
import com.pawstime.pawstime.domain.user.entity.repository.UserRepository;
import com.pawstime.pawstime.domain.user.service.create.CreateUserService;
import com.pawstime.pawstime.domain.user.service.dto.CustomUserInfoDto;
import com.pawstime.pawstime.domain.user.service.read.ReadUserService;
import com.pawstime.pawstime.global.exception.DuplicateException;
import com.pawstime.pawstime.global.exception.NotFoundException;
import com.pawstime.pawstime.global.exception.UnauthorizedException;
import com.pawstime.pawstime.global.jwt.util.JwtUtil;
import com.pawstime.pawstime.global.security.user.CustomUserDetails;
import com.pawstime.pawstime.web.api.user.dto.req.LoginUserReqDto;
import com.pawstime.pawstime.web.api.user.dto.req.UserCreateReqDto;
import com.pawstime.pawstime.web.api.user.dto.resp.GetUserRespDto;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class UserFacade {

  private final CreateUserService createUserService;
  private final ReadUserService readUserService;
  private final PasswordEncoder encoder;
  private final JwtUtil jwtUtil;
  private final TokenBlacklistService tokenBlacklistService;
  private final UserRepository userRepository;


  public void createUser(UserCreateReqDto req) {
    try {
      User user = readUserService.findUserByEmail(req.email());

      if (user == null) {
        User newUser = req.of(encoder.encode(req.password()));
        createUserService.createUser(newUser);
      } else {
        throw new DuplicateException("존재하는 이메일 입니다.");
      }
    } catch (Exception e) {
      throw new DuplicateException("존재하는 이메일 입니다123.");
    }
  }

  public String login(LoginUserReqDto req) {
    User user = readUserService.findUserByEmail(req.email());

    if (user == null) {
      throw new NotFoundException("존재하지 않는 이메일 입니다.");
    }

    if (!encoder.matches(req.password(), user.getPassword())) {
      throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
    }

    CustomUserInfoDto customUserInfoDto = CustomUserInfoDto.of(user);
    return jwtUtil.createAccessToken(customUserInfoDto);
  }

  public void logout(Authentication authentication, HttpServletRequest request) {
    if (authentication == null) {
      throw new UnauthorizedException("인증 실패 : authentication is Null");
    }

    if (request.getHeader("Authorization") == null ||
        !request.getHeader("Authorization").startsWith("Bearer ")) {
      throw new UnauthorizedException("인증 실패 : Authorization 헤더가 없거나 형식이 올바르지 않습니다.");
    }

    String name = authentication.getName();
    log.info("** 로그아웃 요청한 사용자 : {} **", name);

    String token = request.getHeader("Authorization").substring(7);
    log.info("** token : {} **", token);

    Claims claims = jwtUtil.parseClaims(token);

    LocalDateTime expTime = claims.getExpiration()
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime();
    log.info("** expTime : {} **", expTime);

    tokenBlacklistService.createTokenBlacklist(token, expTime);
    request.getSession().removeAttribute("cart");
  }

  public GetUserRespDto getUserFromUserId(Long userId) {
    User user = readUserService.findUserByUserIdQuery(userId);
    if (user == null) {
      throw new NotFoundException("존재하지 않는 사용자입니다.");
    }
    return GetUserRespDto.from(user);
  }

  public User getCurrentUser(){

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if(authentication == null || !authentication.isAuthenticated()){

      throw new UnauthorizedException("로그인이 필요합니다.");
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return readUserService.findUserByUserIdQuery(userDetails.getUser().userId());

  }

  public void deleteUser(Long userId, Authentication authentication, HttpServletRequest request) {

    //1.로그인 상태 확인
    if(authentication == null || !authentication.isAuthenticated()){
      throw new UnauthorizedException("로그인이 필요합니다.");
    }

    //2. 현재 로그인한 유저 정보 가져오기
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    User currentUser = readUserService.findUserByUserIdQuery(userDetails.getUser().userId());

    //3.요청한 userId와 로그인한 userId가 같은지 확인
    if(!currentUser.getUserId().equals(userId)){
      throw new UnauthorizedException("본인 계정만 탈퇴할 수 있습니다.");
    }
    //4.유저 데이터 조회(소프트 딜리트 적용)
    User user = readUserService.findUserByUserIdQuery(userId);
    if (user==null){
      throw new NotFoundException("존재하지 않는 사용자입니다.");
    }
    userRepository.delete(user);
    //  JWT 블랙리스트에 현재 토큰 등록 (자동 로그아웃)
    String token = request.getHeader("Authorization").substring(7);
    Claims claims = jwtUtil.parseClaims(token);
    LocalDateTime expTime = claims.getExpiration()
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    tokenBlacklistService.createTokenBlacklist(token, expTime);

    // 세션에서 정보 제거
    request.getSession().removeAttribute("cart");

    // SecurityContextHolder 초기화 (로그아웃 처리)
    SecurityContextHolder.clearContext();
  }
}
