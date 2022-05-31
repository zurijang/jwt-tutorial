package io.zurijang.tutorial.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests() // HttpServletRequests를 사용하는 요청들에 대한 객체 접근제한 설정
                .antMatchers("/api/hello").permitAll() // api/hello에 대한 요청은 인증없이 허용
                .anyRequest().authenticated(); // 나머지 요청들은 모두 인증
    }
}
