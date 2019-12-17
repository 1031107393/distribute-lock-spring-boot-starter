package com.yzw.mro.distributelock.service;

import com.yzw.mro.distributelock.constant.Constant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

class LockServiceTest {

    @Autowired
    LockService lockService;

    @Test
    void getLock() {
        String value = lockService.getLock(Constant.DISTRIBUTE_LOCK_KEY, 1000l, TimeUnit.MILLISECONDS);
        System.out.println(value);
    }

    @Test
    void unlock() {
        String value = "";
        lockService.unlock(Constant.DISTRIBUTE_LOCK_KEY, value);
    }
}