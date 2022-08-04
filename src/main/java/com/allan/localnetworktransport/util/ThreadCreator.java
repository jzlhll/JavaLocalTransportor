package com.allan.localnetworktransport.util;

import java.util.concurrent.atomic.AtomicInteger;

public class ThreadCreator {
    private static final AtomicInteger threadNumber = new AtomicInteger(1);
    private static final String namePrefix = "localTransport-";

    public static Thread newThread(Runnable r) {
        Thread t = new Thread(null, r,
                namePrefix + threadNumber.getAndIncrement(),
                0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        //t.setUncaughtExceptionHandler(ExDefaultThreadFactory::uncaughtException);
        return t;
    }
}
