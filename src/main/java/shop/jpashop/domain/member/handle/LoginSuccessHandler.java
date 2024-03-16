package shop.jpashop.domain.member.handle;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import shop.jpashop.domain.member.entity.Member;
import shop.jpashop.jwt.service.JwtService;
import shop.jpashop.repository.member.MemberJpaRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final MemberJpaRepository memberJpaRepository;

    @Value("${jwt.access.expiration}")
    private String accessTokenExpiration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String email = extractUsername(authentication);
        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken();

        log.info("엑세스 토큰 {}", accessToken);
        log.info("리프레쉬 토큰 {}", refreshToken);

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        memberJpaRepository.findByEmail(email)
            .ifPresent(
                member -> {
                    member.updateRefreshToken(refreshToken);
                    memberJpaRepository.saveAndFlush(member);
                });

        log.info("로그인에 성공하였습니다. 이메일 : {}", email);
        log.info("로그인에 성공하였습니다. AccessToken : {}", accessToken);
        log.info("발급된 AccessToken 만료 기간 : {}", accessTokenExpiration);
    }

    private String extractUsername(Authentication authentication) {
        UserDetails userDetials = (UserDetails) authentication.getPrincipal();
        return userDetials.getUsername();
    }
}
