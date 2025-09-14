package com.dayaeyak.performance.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    String key(); // 락 키
    long waitTime() default 3000L; // 락 획득 대기시간 (ms)
    long leaseTime() default 5000L; // 락 보유시간 (ms)
}
