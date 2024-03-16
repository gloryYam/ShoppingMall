package shop.jpashop.domain.member.service;

import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import shop.jpashop.domain.member.entity.Member;
import shop.jpashop.domain.member.entity.MemberRole;
import shop.jpashop.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberSecurityService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("해당 이메일이 존재하지 않습니다."));

        List<GrantedAuthority> role = new ArrayList<>();

        if("dudrhkd4179@naver.com".equals(email)) {
            role.add(new SimpleGrantedAuthority(MemberRole.ADMIN.getKey()));
        } else {
            role.add(new SimpleGrantedAuthority(MemberRole.USER.getKey()));
        }

        return User.builder()
            .username(member.getEmail())
            .password(member.getPassword())
            .roles(member.getRole().name())
            .authorities(role)
            .build();
    }
}
