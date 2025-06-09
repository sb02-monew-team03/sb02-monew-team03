package com.team03.monew.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class MDCLoggingFilter extends OncePerRequestFilter {

  private static final String MDC_REQUEST_ID = "requestId";
  private static final String MDC_CLIENT_IP = "clientIp";

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    try {
      String requestId = UUID.randomUUID().toString();
      String clientIp = getClientIp(request);

      MDC.put(MDC_REQUEST_ID, requestId);
      MDC.put(MDC_CLIENT_IP, clientIp);

      // 응답 헤더에도 포함
      response.addHeader("X-Request-ID", requestId);
      response.addHeader("X-Client-IP", clientIp);

      filterChain.doFilter(request, response);
    } finally {
      MDC.clear(); // 메모리 누수 방지
    }
  }

  private String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isBlank()) {
      ip = request.getRemoteAddr();
    } else {
      ip = ip.split(",")[0]; // 여러 개면 첫 번째
    }
    return ip;
  }
}
