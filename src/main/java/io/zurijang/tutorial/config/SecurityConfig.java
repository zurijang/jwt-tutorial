package io.zurijang.tutorial.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // h2-console 하위 모든 요청들과 파비콘 고나련 요청은 Spring Security 로직을 수행하지 않도록 configure 메소드를 오버라이드하여 내용 추가


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
                .authorizeRequests() // HttpServletRequests를 사용하는 요청들에 대한 객체 접근제한 설정
                .antMatchers("/api/hello").permitAll() // api/hello에 대한 요청은 인증없이 허용
                .anyRequest().authenticated(); // 나머지 요청들은 모두 인증
    }
}
