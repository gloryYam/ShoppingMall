package shop.jpashop.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import shop.jpashop.domain.member.entity.MemberRole;
import shop.jpashop.domain.member.filter.CustomJsonUsernamePasswordAuthenticationFilter;
import shop.jpashop.domain.member.handle.LoginFailureHandler;
import shop.jpashop.domain.member.handle.LoginSuccessHandler;
import shop.jpashop.domain.member.service.MemberSecurityService;
import shop.jpashop.jwt.filter.JwtAuthenticationProcessingFilter;
import shop.jpashop.jwt.service.JwtService;
import shop.jpashop.oauth.handle.OAuth2LoginFailureHandler;
import shop.jpashop.oauth.handle.OAuth2LoginSuccessHandler;
import shop.jpashop.oauth.service.CustomOAuthUserService;
import shop.jpashop.repository.member.MemberJpaRepository;

@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebSecurityConfig {

    private final MemberSecurityService memberSecurityService;
    private final JwtService jwtService;
    private final MemberJpaRepository memberJpaRepository;
    private final ObjectMapper objectMapper;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomOAuthUserService customOAuthUserService;
    private final MemberSecurityService loginService;

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .httpBasic().disable()
            .csrf().disable()
            .headers().frameOptions().disable()
            .and()
            .authorizeRequests() //요청에 의한 보안 검사 시작
            .antMatchers("/", "/css/**", "/img/**", "/js/**", "/vendor/**", "/files/**",
                "/members/new", "/members/email", "/members/login", "/members/logout", "/members/vendor/**",
                "/items", "/items/{id}", "/items/vendor/**", "/qna", "/notices", "/notices/{noticeId}").permitAll()
            .antMatchers("/members/update", "/members/delete", "/carts", "/carts/{cartItemId}/update", "/carts/{cartItemId}/delete",
                "/qna/new", "/qna/{questionId}", "/qna/{questionId}/update", "/qna/{questionId}/delete",
                "/orders", "/orders/new", "/orders/{orderId}", "/payment", "/payment/kakao").authenticated()
            .antMatchers("/items/new", "/items/{id}/update", "/items/{id}/delete",
                "/answers", "/answers/new/{questionId}", "/answers/{answerId}/update", "/answers/delete/{answerId}",
                "/notices/new", "/notices/{noticeId}/update", "/notices/{noticeId}/delete").hasRole(MemberRole.ADMIN.name())
            .antMatchers("/api/**").hasRole(MemberRole.USER.name())

            .anyRequest().authenticated() //어떤 요청에도 보안 검사를 실생
            .and()
            .formLogin()
            .loginPage("/members/login")
            .defaultSuccessUrl("/")
            .and()
            .logout()
            .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
            .logoutSuccessUrl("/")
            .invalidateHttpSession(true)
            .and()
            .oauth2Login()
            .successHandler(oAuth2LoginSuccessHandler)
            .failureHandler(oAuth2LoginFailureHandler)
            .userInfoEndpoint().userService(customOAuthUserService);

        http.addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class);
        http.addFilterBefore(jwtAuthenticationProcessingFilter(), CustomJsonUsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(loginService);
        return new ProviderManager(provider);
    }

    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(jwtService, memberJpaRepository);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    @Bean
    public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter() throws Exception {
        CustomJsonUsernamePasswordAuthenticationFilter filter
            = new CustomJsonUsernamePasswordAuthenticationFilter(objectMapper);

        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(loginSuccessHandler());
        filter.setAuthenticationFailureHandler(loginFailureHandler());
        return filter;
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
        JwtAuthenticationProcessingFilter jwtAuthenticationFilter
            = new JwtAuthenticationProcessingFilter(jwtService, memberJpaRepository);

        return jwtAuthenticationFilter;
    }
}
