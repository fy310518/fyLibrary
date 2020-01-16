package com.fy.baselibrary.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程相关 工具类
 * Created by fangs on 2018/5/25.
 */
public class ThreadUtils {

    /**
     * 一般说来,大家认为线程池的大小经验值应该这样设置:(其中N为CPU的核数)
            如果是IO密集型应用,则线程池大小设置为2N+1
            如果是CPU密集型应用,则线程池大小设置为N+1

     那么我们的 Android 应用是属于哪一种应用呢?看下他们的定义。

     I/O密集型
        I/O bound 指的是系统的CPU效能相对硬盘/内存的效能要好很多,此时,系统运作,
        大部分的状况是 CPU 在等 I/O (硬盘/内存) 的读/写,此时 CPU Loading 不高。
     CPU-bound
        CPU bound 指的是系统的 硬盘/内存 效能 相对 CPU 的效能 要好很多,此时,系统运作,
        大部分的状况是 CPU Loading 100%,CPU 要读/写 I/O (硬盘/内存),
        I/O在很短的时间就可以完成,而 CPU 还有许多运算要处理,CPU Loading 很高。

     我们的Android 应用的话应该是属于IO密集型应用,所以数量一般设置为 2N+1。
     */

    //参数初始化
    public static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    //核心线程数量大小
    public static final int corePoolSize = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    //线程池最大容纳线程数
    public static final int maximumPoolSize = CPU_COUNT * 2 + 1;
    //线程空闲后的存活时长
    public static final int keepAliveTime = 30;
    //任务过多后,存储任务的一个阻塞队列
    BlockingQueue workQueue = new SynchronousQueue<>();

    private ThreadUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    //线程的创建工厂
    ThreadFactory threadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);
        public Thread newThread(Runnable r) {
            return new Thread(r, "AdvacnedAsyncTask #" + mCount.getAndIncrement());
        }
    };

    //线程池任务满载后采取的任务拒绝策略
    RejectedExecutionHandler rejectHandler = new ThreadPoolExecutor.DiscardOldestPolicy();

    //线程池对象,创建线程
    ThreadPoolExecutor mExecute = new ThreadPoolExecutor(
            corePoolSize,
            maximumPoolSize,
            keepAliveTime,
            TimeUnit.SECONDS,
            workQueue,
            threadFactory,
            rejectHandler
    );
}
