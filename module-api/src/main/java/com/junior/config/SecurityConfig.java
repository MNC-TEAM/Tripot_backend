package com.junior.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junior.domain.member.MemberRole;
import com.junior.security.exceptionhandler.CustomAuthenticationEntryPoint;
import com.junior.security.JwtUtil;
import com.junior.security.filter.JsonUsernamePasswordAuthenticationFilter;
import com.junior.security.filter.JwtValidExceptionHandlerFilter;
import com.junior.security.filter.JWTFilter;
import com.junior.security.handler.LoginFailureHandler;
import com.junior.security.handler.LoginSuccessJwtProviderHandler;
import com.junior.security.provider.CustomDaoAuthenticationProvider;
import com.junior.service.security.UserDetailsServiceImpl;
import com.junior.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)


                .sessionManagement((session) -> session.
                        sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //filter
                .addFilterAfter(new JWTFilter(jwtUtil, userDetailsService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtValidExceptionHandlerFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)

                //403 예외 처리
                .exceptionHandling((authenticationManager) -> authenticationManager
                        .authenticationEntryPoint(customAuthenticationEntryPoint))

                //uri 권한 설정
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/api/v1/login/**").permitAll()
                        .requestMatchers("/api/v2/login/**").permitAll()
                        .requestMatchers("/api/v1/reissue").permitAll()
                        .requestMatchers("/api/v1/story/list").permitAll()
                        //닉네임 중복 여부 확인
                        .requestMatchers("/api/v1/members/nicknames/check-valid").permitAll()
                        .requestMatchers("/swagger-ui/**",
                                "/swagger-resources/**",
                                "/v3/api-docs/**").permitAll()

                        //admin 관련 설정
                        .requestMatchers("/api/v1/admin/**").hasRole(MemberRole.ADMIN.name())
                        .requestMatchers("/createTestMember").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .anyRequest().authenticated());


        return httpSecurity.build();

    }

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() throws Exception {
        DaoAuthenticationProvider daoAuthenticationProvider = new CustomDaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {//AuthenticationManager 등록
        DaoAuthenticationProvider provider = daoAuthenticationProvider();//DaoAuthenticationProvider 사용
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter() throws Exception {
        JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter = new JsonUsernamePasswordAuthenticationFilter(objectMapper);
        jsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
        jsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessJWTProvideHandler());
        jsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
        return jsonUsernamePasswordLoginFilter;
    }

    @Bean
    public LoginSuccessJwtProviderHandler loginSuccessJWTProvideHandler(){
        return new LoginSuccessJwtProviderHandler(jwtUtil, redisUtil);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler(){
        return new LoginFailureHandler();
    }


}
