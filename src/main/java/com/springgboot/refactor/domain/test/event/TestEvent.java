package com.springgboot.refactor.domain.test.event;

import com.alibaba.fastjson2.JSONObject;
import com.springgboot.refactor.domain.test.entity.TestPojo;
import com.springgboot.refactor.util.HashUtil;
import com.springgboot.refactor.util.MD5Util;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 测试事件
 *
 * @author Elliot
 */
@Slf4j
@Getter
@Setter
public class TestEvent extends ApplicationEvent {

    private TestPojo testPojo;

    public TestEvent(TestPojo testPojo) {
        super(testPojo);
        this.testPojo = testPojo;
        // 打印事件日志
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("TestEvent_content:{}", JSONObject.toJSONString(testPojo));
    }

    public static void main(String[] args) throws Exception {
        String a = "abc", b = "acb", c = "bac", d = "bca", e = "cab", f = "cba";
        HashMap<Long, String> todoMap = new HashMap<>();
        todoMap.put(HashUtil.mixHash(MD5Util.getEncryptedPwd(a)), a);
        todoMap.put(HashUtil.mixHash(MD5Util.getEncryptedPwd(b)), b);
        todoMap.put(HashUtil.mixHash(MD5Util.getEncryptedPwd(c)), c);
        todoMap.put(HashUtil.mixHash(MD5Util.getEncryptedPwd(d)), d);
        todoMap.put(HashUtil.mixHash(MD5Util.getEncryptedPwd(e)), e);
        todoMap.put(HashUtil.mixHash(MD5Util.getEncryptedPwd(f)), f);
//        System.out.println(
//                JSONObject.toJSONString(
//                        todoMap
//                )
//        );
        System.out.println(
                JSONObject.toJSONString(
                        todoMap
                                .entrySet()
                                .stream()
                                .sorted(Map.Entry.<Long, String>comparingByKey().reversed())
//                                .sorted(Map.Entry.<Long, String>comparingByKey())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                )
        );
    }
}
