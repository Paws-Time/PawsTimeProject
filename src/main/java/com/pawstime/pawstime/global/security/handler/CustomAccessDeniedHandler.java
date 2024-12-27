package com.pawstime.pawstime.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawstime.pawstime.global.dto.ErrorResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {

    log.error("No Authorities", accessDeniedException);

    ErrorResponseDto errorResponseDto = new ErrorResponseDto(
        HttpStatus.FORBIDDEN, accessDeniedException.getMessage(), LocalDateTime.now());

    String responseBody = objectMapper.writeValueAsString(errorResponseDto);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(responseBody);
  }
}