package com.example.dividend.security;

import com.example.dividend.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;


@Component
@RequiredArgsConstructor
public class TokenProvider {

    /*
    1초는 1000밀리초 (1초 = 1000ms)
    1분은 60초 (1분 = 60s)
    1시간은 60분 (1시간 = 60분)
     */
    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1시간(1000 * 60 * 60)
    private static final String KEY_ROLES = "roles";

    private final MemberService memberService;

    @Value("{spring.jwt.secret}")
    private String secretKey;

    /**
     * 토큰 생성(발급)
     *
     * @param username 사용자 이름
     * @param roles    사용자 역할 정보 리스트
     * @return 생성된 JWT 토큰
     */
    public String generateToken(String username, List<String> roles) { // 토큰 생성
        // 사용자 이름을 기반으로 JWT 클레임 객체 생성
        Claims claims = Jwts.claims().setSubject(username);
        // 클레임 객체에 사용자의 역할 정보를 추가
        claims.put(KEY_ROLES, roles);

        // 현재 시간과 유효기간을 설정하여 JWT 토큰 생성
        var now = new Date();
        var expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME); // 얼마나 토큰을 유효하게 할건지 (1시간)

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 토근 생성 시간
                .setExpiration(expiredDate) // 토큰 만료 시간
                .signWith(SignatureAlgorithm.HS512, this.secretKey) // 사용할 암호화 알고리즘, 비밀키
                .compact();
    }

    /**
     * JWT 토큰에서 사용자 정보를 추출하여 Authentication 객체로 반환
     *
     * @param jwt JWT 토큰
     * @return 사용자 인증 정보를 담고 있는 Authentication 객체
     */
    public Authentication getAuthentication(String jwt) { // 인증정보 가져옴
        // 사용자 이름으로 UserDetails를 조회하여 인증 정보를 생성
        UserDetails userDetails = this.memberService.loadUserByUsername(this.getUsername(jwt));
        // 인증 정보를 담은 Authentication 객체를 반환
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * JWT 토큰에서 사용자 이름을 추출
     *
     * @param token JWT 토큰
     * @return 사용자 이름
     */
    public String getUsername(String token) {
        // JWT 토큰을 파싱하여 사용자 이름 추출
        return this.parseClaims(token).getSubject();
    }

    /**
     * JWT 토큰이 유효한지 확인
     *
     * @param token JWT 토큰
     * @return 토큰 유효 여부 (유효하면 true, 만료되었거나 올바르지 않으면 false)
     */
    public boolean validateToken(String token) {
        // 토큰이 비어있는지 확인
        if (!StringUtils.hasText(token)) return false;

        // JWT 토큰의 클레임을 파싱하여 토큰의 만료 시간과 현재 시간 비교하여 유효 여부 반환
        var claims = this.parseClaims(token);
        return !claims.getExpiration().before(new Date()); // 토큰이 유효한지 (만료시간이 현재보다 이전인지 아닌지)
    }

    /**
     * JWT 토큰의 클레임 파싱
     *
     * @param token JWT 토큰
     * @return JWT 토큰의 클레임 객체
     */
    private Claims parseClaims(String token) { // 토큰 파싱
        try {
            // JWT 토큰의 서명 키를 사용하여 클레임 객체 파싱
            return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰인 경우 예외 처리 후 클레임 반환
            return e.getClaims();
        }
    }
}
