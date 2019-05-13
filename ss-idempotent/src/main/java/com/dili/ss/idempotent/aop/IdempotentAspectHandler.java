package com.dili.ss.idempotent.aop;

import com.dili.ss.idempotent.service.IdempotentTokenService;
import com.dili.ss.redis.service.RedisDistributedLock;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author asiam
 */
public interface IdempotentAspectHandler {

    Object aroundIdempotent(ProceedingJoinPoint point, RedisDistributedLock redisDistributedLock) throws Throwable;

    Object aroundToken(ProceedingJoinPoint point, IdempotentTokenService idempotentTokenService) throws Throwable;
}
