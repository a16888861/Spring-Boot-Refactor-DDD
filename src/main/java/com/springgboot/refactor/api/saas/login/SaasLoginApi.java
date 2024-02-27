package com.springgboot.refactor.api.saas.login;

import com.springgboot.refactor.infa.annotation.IgnorePcSecurity;
import com.springgboot.refactor.infa.response.WebResponse;
import com.springgboot.refactor.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("saas")
@RequiredArgsConstructor
public class SaasLoginApi {

    /**
     * Saas登陆
     */
    @IgnorePcSecurity
    @PostMapping("/login")
    public WebResponse<String> login() {
        // 生成token前做什么事情(校验用户信息)
        String token = JwtUtil.createToken("123");
        // 生成token后做什么事情(此处可以将token塞到redis中 value塞一些用户信息之类的)
        return WebResponse.success(token);
    }

}
