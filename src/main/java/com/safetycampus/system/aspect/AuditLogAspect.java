package com.safetycampus.system.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetycampus.common.annotation.SysLog;
import com.safetycampus.common.context.LoginUser;
import com.safetycampus.common.context.UserContext;
import com.safetycampus.system.entity.SysAuditLog;
import com.safetycampus.system.mapper.SysAuditLogMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class AuditLogAspect {

    @Resource
    private SysAuditLogMapper sysAuditLogMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Around("@annotation(sysLog)")
    public Object around(ProceedingJoinPoint joinPoint, SysLog sysLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Integer status = 1;
        String resultStr = null;

        try {
            result = joinPoint.proceed();
            if (result != null) {
                resultStr = objectMapper.writeValueAsString(result);
                if (resultStr.length() > 2000) {
                    resultStr = resultStr.substring(0, 2000) + "...";
                }
            }
        } catch (Throwable e) {
            status = 0;
            resultStr = "异常: " + e.getMessage();
            throw e;
        } finally {
            try {
                saveAuditLog(joinPoint, sysLog, resultStr, status);
            } catch (Exception ignored) {
            }
        }

        return result;
    }

    private void saveAuditLog(ProceedingJoinPoint joinPoint, SysLog sysLog, String result, Integer status) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        SysAuditLog auditLog = new SysAuditLog();
        auditLog.setModule(sysLog.module());
        auditLog.setOperation(sysLog.operation());
        auditLog.setStatus(status);
        auditLog.setResult(result);
        auditLog.setCreatedAt(LocalDateTime.now());

        if (request != null) {
            auditLog.setIp(getClientIp(request));
            auditLog.setUserAgent(request.getHeader("User-Agent"));
        }

        LoginUser loginUser = UserContext.getLoginUser();
        if (loginUser != null) {
            auditLog.setUserId(loginUser.getUserId());
            auditLog.setUsername(loginUser.getUsername());
        }

        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                StringBuilder params = new StringBuilder();
                for (Object arg : args) {
                    if (arg != null && !isRequestOrResponse(arg)) {
                        String argStr = objectMapper.writeValueAsString(arg);
                        if (argStr.length() > 500) {
                            argStr = argStr.substring(0, 500) + "...";
                        }
                        params.append(argStr).append("; ");
                    }
                }
                auditLog.setParams(params.toString());
            }
        } catch (Exception ignored) {
        }

        sysAuditLogMapper.insert(auditLog);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private boolean isRequestOrResponse(Object obj) {
        return obj instanceof HttpServletRequest || obj instanceof jakarta.servlet.http.HttpServletResponse;
    }
}
