package shop.jpashop.jwt.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import shop.jpashop.domain.member.entity.Member;
import shop.jpashop.jwt.service.JwtService;
import shop.jpashop.jwt.util.PasswordUtil;
import shop.jpashop.repository.member.MemberJpaRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private static final String NO_CHECK_URL = "/members/login";

    private final JwtService jwtService;
    private final MemberJpaRepository memberJpaRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("-------------------------------------------경계");

        if(request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = jwtService.extractRefreshToken(request)
            .filter(jwtService::isTokenValid)
            .orElse(null);

        // 재발급
        if (refreshToken != null) {
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            return;
        }

        // AccessToken이 없거나 유효하지 않다면, 인증 객체가 담기지 않은 상태로 다음 필터로 넘어가기 때문에 403 에러 발생
        if (refreshToken == null) {
            checkAccessTokenAndAuthentication(request, response, filterChain);
        }
    }

    public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        memberJpaRepository.findByRefreshToken(refreshToken)
            .ifPresent(user -> {
                String reIssuedRefreshToken = reIssueRefreshToken(user);
                jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(user.getEmail()),
                    reIssuedRefreshToken);
            });
    }

    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {
        log.info("checkAccessTokenAndAuthentication() 호출");
        jwtService.extractAccessToken(request)
            .filter(jwtService::isTokenValid).flatMap(jwtService::extractEmail)
            .flatMap(memberJpaRepository::findByEmail)
            .ifPresent(this::saveAuthentication);

        filterChain.doFilter(request, response);
    }

    public void saveAuthentication(Member myUser) {
        String password = myUser.getPassword();
        if (password == null) { // 소셜 로그인 유저의 비밀번호 임의로 설정 하여 소셜 로그인 유저도 인증 되도록 설정
            password = PasswordUtil.generateRandomPassword();
        }

        UserDetails userDetailsUser = User.builder()
            .username(myUser.getEmail())
            .password(password)
            .roles(myUser.getRole().name())
            .build();

        Authentication authentication =
            new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    //리프레시 토큰 재발급 & DB에 리프레시 토큰 업데이트
    private String reIssueRefreshToken(Member member) {
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        member.updateRefreshToken(reIssuedRefreshToken);
        memberJpaRepository.saveAndFlush(member);
        return reIssuedRefreshToken;
    }
}
