package io.zurijang.tutorial.config;

import io.zurijang.tutorial.jwt.JwtAccessDeniedHandler;
import io.zurijang.tutorial.jwt.JwtAuthenticationEntryPoint;
import io.zurijang.tutorial.jwt.JwtSecurityConfig;
import io.zurijang.tutorial.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // @PreAuthorize 어노테이션을 메소드단위로 추가하기위해 적용
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(
            TokenProvider tokenProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // h2-console 하위 모든 요청들과 파비콘 관련 요청은 Spring Security 로직을 수행하지 않도록 configure 메소드를 오버라이드하여 내용 추가
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers(
                        "/h2-console/**"
                        ,"/favicon.ico"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // 토큰을 사용할 때는 csrf 설정을 disable
                .exceptionHandling() // Exception을 핸들링할때 우리가 만들었던 클래스를 ㅜㅊ가
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and() // H2-console을 위한 설정 추가
                .headers()
                .frameOptions()
                .sameOrigin()

                .and() // 세션을 사용하지 않기때문에 세션 설정 STATELESS 설정
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests() // HttpServletRequests를 사용하는 요청들에 대한 객체 접근제한 설정
                .antMatchers("/api/hello").permitAll() // api/hello에 대한 요청은 인증없이 허용
                .antMatchers("/api/authenticate").permitAll() // 로그인 및 회원가입 API는 토큰이 없는 상태에서 요청이 들어오기 때문에 permitAll 설정
                .antMatchers("/api/signup").permitAll()
                .anyRequest().authenticated() // 나머지 요청들은 모두 인증

                .and() // JwtFilter를 addFilterBefore 로 등록했떤 JwtSecurityConfig 클래스 적용
                .apply(new JwtSecurityConfig(tokenProvider));


    }
}
