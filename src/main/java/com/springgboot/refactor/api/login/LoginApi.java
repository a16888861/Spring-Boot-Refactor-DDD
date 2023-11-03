package com.springgboot.refactor.api.login;

import com.springgboot.refactor.infrastructure.response.WebResponse;
import com.springgboot.refactor.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("secure")
@RequiredArgsConstructor
public class LoginApi {

    // todo 待完成
    @PostMapping("/login")
    public WebResponse<String> login() {
        return WebResponse.success(
                JwtUtil.createToken("123")
        );
    }
}
