package com.safetycampus.system.service;

import com.safetycampus.common.context.LoginUser;
import com.safetycampus.system.dto.LoginDTO;
import com.safetycampus.system.dto.LoginVO;

public interface AuthService {

    LoginVO login(LoginDTO loginDTO);

    void logout();

    LoginUser getCurrentUser();
}
