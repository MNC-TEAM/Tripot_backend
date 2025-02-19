package com.junior.security.filter;

import com.junior.security.JwtUtil;
import com.junior.service.security.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 헤더에서 access키에 담긴 토큰을 꺼냄
        String preAccessToken = request.getHeader("Authorization");

        // 토큰이 없거나 유효하지 않다면 다음 필터로 넘김
        if (preAccessToken == null || !preAccessToken.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = preAccessToken.split(" ")[1];


        // username, role 값을 획득
        String username = jwtUtil.getUsername(accessToken);


        //username->member->userDetails(userPrincipal)으로, userDetailsService에서 꺼내도 무방
        //attributes 내용들은 회원가입 시점에 저장되므로 신경 안써도 될듯?
        UserDetails customUserDetails = userDetailsService.loadUserByUsername(username);

        //JWT 방식이므로 여기서 1회용 회원 세션 저장
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);

    }
}
