package com.junior.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class RepositoryLogAspect {

    @Pointcut("execution(* com.junior.repository..*.*(..))")
    private void repository() {
    }

    @Around("repository()")
    public Object queryExecutionLog(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        log.debug("[{}] 쿼리 실행 시작: {}", className, methodName);

        Object result = joinPoint.proceed();

        log.debug("[{}] 쿼리 실행 종료: {}", className, methodName);

        return result;
    }
}
