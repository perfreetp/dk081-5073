package com.safetycampus.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {

    String schoolIdField() default "schoolId";
}
