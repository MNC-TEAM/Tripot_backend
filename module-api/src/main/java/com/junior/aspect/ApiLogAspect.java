package com.junior.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Aspect
@Component
@Slf4j
public class ApiLogAspect {

    // com.aop.controller 이하 패키지의 모든 클래스 이하 모든 메서드에 적용
    @Pointcut("execution(* com.junior.controller..*.*(..))")
    private void controller() {
    }

    @Pointcut("execution(* com.junior.service..*.*(..))")
    private void service() {
    }

    @Pointcut("execution(* com.junior.repository..*.*(..))")
    private void repository() {
    }

    @Around("controller() || service() || repository()")
    public Object inAndOutLog(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        log.debug("[{}] 메서드 시작: {}", className, methodName);

        Object result = joinPoint.proceed();

        log.debug("[{}] 메서드 종료: {}", className, methodName);

        return result;
    }

    @Before("controller()")
    public void beforeParameterLog(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        String username = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "None";
        String controllerName = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Map<String, Object> params = new HashMap<>();

        try {
            String decodedRequestURI = URLDecoder.decode(request.getRequestURI(), StandardCharsets.UTF_8);
            params.put("controller", controllerName);
            params.put("method", methodName);
            params.put("http_method", request.getMethod());
            params.put("request_uri", decodedRequestURI);
            params.put("user", username);
        } catch (Exception e) {
            log.error("LogAspect Error", e);
        }

        log.info("Request - HTTP: {} | URI: {} | User: {} | Method: {}.{}",
                params.get("http_method"),
                params.get("request_uri"),
                params.get("user"),
                params.get("controller"),
                params.get("method"));
    }


}