package com.example.dividend.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/*
포스트맨으로 API 호출 시 먼저 필터를 거치고 서블릿을 거치고 인터셉터를 거치고 aop 레이어를 거친 다음 컨트롤러로 들어감 , 응답은 반대로
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer "; //jwt토큰 사용하는 경우 Bearer + 한칸띄고 붙임

    private final TokenProvider tokenProvider;

    @Override //요청이들어오면 필터가 먼저 실행되어 헤더에 토큰있는지 확인 후 유효하면 콘텍스트에 담고, 아니면 바로 실행
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = this.resolveTokenFromRequest(request);
        if (StringUtils.hasText(token) && this.tokenProvider.validateToken(token)) {
            //토큰 유효성 검증
            Authentication auth = this.tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);

        }

        filterChain.doFilter(request, response); //스프링의 필터
    }

    public String resolveTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader((TOKEN_HEADER)); // token : key에 대한 header의 value

        if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) { //토큰형태 포함
            return token.substring(TOKEN_PREFIX.length()); //실제토큰 부위
        }

        return null;
    }
}
