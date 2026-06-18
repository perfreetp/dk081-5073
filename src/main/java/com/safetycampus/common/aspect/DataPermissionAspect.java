package com.safetycampus.common.aspect;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.safetycampus.common.annotation.DataPermission;
import com.safetycampus.common.context.UserContext;
import com.safetycampus.common.enums.RoleTypeEnum;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class DataPermissionAspect {

    @Around("@annotation(dataPermission)")
    public Object around(ProceedingJoinPoint joinPoint, DataPermission dataPermission) throws Throwable {
        RoleTypeEnum roleType = UserContext.getRoleType();
        Long schoolId = UserContext.getSchoolId();
        String schoolIdField = dataPermission.schoolIdField();

        if (roleType == RoleTypeEnum.SCHOOL_SECURITY && schoolId != null) {
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof QueryWrapper<?>) {
                    QueryWrapper<?> wrapper = (QueryWrapper<?>) arg;
                    wrapper.eq(schoolIdField, schoolId);
                }
            }
        }

        return joinPoint.proceed();
    }

    private DataPermission getDataPermission(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(DataPermission.class);
    }
}
