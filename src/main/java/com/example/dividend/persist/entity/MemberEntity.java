package com.example.dividend.persist.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Entity(name = "MEMBER")
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberEntity implements UserDetails { //스프링시큐리티 기능 사용하기 위해 UserDetails 상속받음
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @JsonIgnore
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;


    @Override
    // 사용자의 권한 정보를 가져오는 메서드
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new) //스프링 시큐리티의 role 관련 기능 쓰기 위함
                .collect(Collectors.toList());
    }

    @Override
    // 사용자 계정이 만료되었는지 여부를 확인하는 메서드
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    // 사용자 계정이 잠겨있는지 여부를 확인하는 메서드
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    // 사용자의 인증 정보(비밀번호)가 만료되었는지 여부를 확인하는 메서드
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    // 사용자 계정이 활성화되어 있는지 여부를 확인하는 메서드
    public boolean isEnabled() {
        return false;
    }
}
