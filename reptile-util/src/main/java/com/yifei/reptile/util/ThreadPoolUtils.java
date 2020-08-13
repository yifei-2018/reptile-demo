package com.yifei.reptile.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工具类
 *
 * @author luqs
 * @version v1.0
 * @date 2020/8/13 12:29
 */
public class ThreadPoolUtils {
    private ThreadPoolUtils() {
    }

    /**
     * 线程池（注：设置线程池参数时，请详细了解ThreadPoolExecutor的相关知识，以免出现OOM）
     */
    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20, 30, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1024), new ThreadFactoryBuilder().setNameFormat("threadPool_%d").build(),
            new ThreadPoolExecutor.DiscardPolicy());

    /**
     * 获取线程池执行器
     *
     * @return ThreadPoolExecutor
     */
    public static ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

}
