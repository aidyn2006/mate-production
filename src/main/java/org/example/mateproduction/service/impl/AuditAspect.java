package org.example.mateproduction.service.impl;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.mateproduction.entity.AuditLog;
import org.example.mateproduction.helpers.Auditable;
import org.example.mateproduction.repository.AuditLogRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;

    @Around("@annotation(auditable)")
    public Object logAudit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";

        String methodName = joinPoint.getSignature().getName();

        String args = Arrays.stream(joinPoint.getArgs())
                .map(arg -> arg != null ? arg.toString() : "null")
                .collect(Collectors.joining(", "));

        String action = !auditable.action().isEmpty() ? auditable.action() : methodName;

        Object result = joinPoint.proceed();

        AuditLog log = new AuditLog();
        log.setEmail(email);
        log.setAction(action);
        log.setDetails("Args: [" + args + "]");

        auditLogRepository.save(log);

        return result;
    }
}
