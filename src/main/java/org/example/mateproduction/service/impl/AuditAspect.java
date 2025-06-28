package org.example.mateproduction.service.impl;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.mateproduction.entity.AuditLog;
import org.example.mateproduction.helpers.Auditable;
import org.example.mateproduction.repository.AuditLogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;

    @Around("@annotation(auditable)")
    public Object logAudit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var email = (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";
        var action = auditable.action().isEmpty() ? joinPoint.getSignature().getName() : auditable.action();
        var args = Arrays.stream(joinPoint.getArgs())
                .map(arg -> arg == null ? "null" : arg.toString())
                .collect(Collectors.joining(", "));

        var result = joinPoint.proceed();

        auditLogRepository.save(AuditLog.builder()
                .email(email)
                .action(action)
                .details("Args: [" + args + "]")
                .build());

        return result;
    }
}
