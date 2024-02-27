package com.springgboot.refactor.infa.annotation;


import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义忽略验证注解
 * 小程序存在该注解的方法忽略校验 - 方便调试
 *
 * @author Elliot
 */
@Target(ElementType.METHOD) //指明该类型的注解可以注解的程序元素的范围
@Retention(RetentionPolicy.RUNTIME) //指明了该Annotation被保留的时间长短
@Component
public @interface IgnoreMiniAppSecurity {

}
