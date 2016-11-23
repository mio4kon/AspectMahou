package com.mio.aspect.internal;

import java.util.concurrent.TimeUnit;

/**
 * Created by mio4kon on 16/11/22.
 */

public class TimeWatcher {
    private long startTime;
    private long endTime;
    private long elapsedTime;

    public void start() {
        reset();
        startTime = System.nanoTime();
    }

    private void reset() {
        startTime = 0;
        endTime = 0;
        elapsedTime = 0;
    }

    public void stop() {
        if (startTime != 0) {
            endTime = System.nanoTime();
            elapsedTime = endTime - startTime;
        } else {
            reset();
        }
    }

    public long getTotalTimeMillis() {
        return (elapsedTime != 0) ? TimeUnit.NANOSECONDS.toMillis(endTime - startTime) : 0;
    }
}
