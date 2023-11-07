package com.springgboot.refactor.api.miniApp.login;

import com.springgboot.refactor.infrastructure.response.WebResponse;
import com.springgboot.refactor.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("miniApp")
@RequiredArgsConstructor
public class MiniAppLoginApi {

    /**
     * 小程序登陆
     */
    @PostMapping("/login")
    public WebResponse<String> login() {
        // 生成token前做什么事情(校验用户信息)
        String token = JwtUtil.createToken("123");
        // 生成token后做什么事情(存redis)
        return WebResponse.success(token);
    }
}
