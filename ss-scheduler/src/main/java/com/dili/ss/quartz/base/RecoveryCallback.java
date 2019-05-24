package com.dili.ss.quartz.base;

import com.dili.ss.quartz.domain.ScheduleJob;
import com.github.rholder.retry.Attempt;

/**
 * 兜底回调接口
 */
public interface RecoveryCallback {
    /**
     *  兜底方法
     * @param attempt 一次执行任务
     * @param scheduleJob
     */
    void recover(Attempt attempt, ScheduleJob scheduleJob);
}
