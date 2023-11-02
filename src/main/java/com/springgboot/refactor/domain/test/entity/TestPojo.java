package com.springgboot.refactor.domain.test.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测试监听器用的实体
 *
 * @author Elliot
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestPojo {
    private String testContent;
}
