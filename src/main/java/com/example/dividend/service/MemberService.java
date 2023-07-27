package com.example.dividend.service;

import com.example.dividend.model.Auth;
import com.example.dividend.persist.entity.MemberEntity;
import com.example.dividend.persist.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService  {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; //패스워드 암호화

    public MemberEntity loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("couldn't find user -> " + username));
    }

    public MemberEntity register(Auth.SignUp member) { //회원가입(중복확인)
        boolean exists = this.memberRepository.existsByUsername(member.getUsername());
        if (exists) { //사용중이면
            throw new RuntimeException("이미 사용 중인 아이디 입니다.");
        }

        //사용중 아니면 비밀번호 암호화 해서 저성
        member.setPassword(this.passwordEncoder.encode(member.getPassword()));
        var result = this.memberRepository.save(member.toEntity());

        return result;
    }

    public MemberEntity authenticate(Auth.SignIn member) {
        var user = this.memberRepository.findByUsername(member.getUsername())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 ID 입니다."));

        if (!this.passwordEncoder.matches(member.getPassword(), user.getPassword())) {//멤버의 패스워드와 저장된 패스워드 일치하는지 검증
            throw new RuntimeException("비밀번호가 일치하지 않습니다");
        }

        return user;
    }
}
