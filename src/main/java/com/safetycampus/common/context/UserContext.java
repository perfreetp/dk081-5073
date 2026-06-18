package com.safetycampus.common.context;

import com.safetycampus.common.enums.RoleTypeEnum;

public class UserContext {

    private static final ThreadLocal<LoginUser> USER_HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void setLoginUser(LoginUser loginUser) {
        USER_HOLDER.set(loginUser);
    }

    public static LoginUser getLoginUser() {
        return USER_HOLDER.get();
    }

    public static Long getUserId() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getUserId() : null;
    }

    public static String getUsername() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getUsername() : null;
    }

    public static RoleTypeEnum getRoleType() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getRoleType() : null;
    }

    public static Long getSchoolId() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getSchoolId() : null;
    }

    public static void remove() {
        USER_HOLDER.remove();
    }
}
