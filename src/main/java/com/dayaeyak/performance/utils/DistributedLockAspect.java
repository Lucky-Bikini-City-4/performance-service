package com.dayaeyak.performance.utils;

import com.dayaeyak.performance.common.exception.CustomException;
import com.dayaeyak.performance.domain.performance.exception.PerformanceErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1) // 트랜잭션보다 먼저 실행되도록
public class DistributedLockAspect {

    private final RedissonClient redissonClient;
    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        String lockKey = generateLockKey(joinPoint, distributedLock.key());
        RLock lock = redissonClient.getLock(lockKey);

        boolean acquired = false;
        try {
            // 락 획득 시도
            acquired = lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), TimeUnit.MILLISECONDS);

            if (!acquired) {
                log.warn("분산 락 획득 실패: {}", lockKey);
                throw new CustomException(PerformanceErrorCode.LOCK_ACQUISITION_FAILED);
            }

            log.debug("분산 락 획득 성공: {}", lockKey);
            return joinPoint.proceed();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException(PerformanceErrorCode.LOCK_INTERRUPTED);
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("분산 락 해제: {}", lockKey);
            }
        }
    }

    private String generateLockKey(ProceedingJoinPoint joinPoint, String keyExpression) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();

        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        Expression expression = parser.parseExpression(keyExpression);
        return "LOCK:" + expression.getValue(context, String.class);
    }
}
