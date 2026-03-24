package com.stepanio.java.training.telegram.app.scheduler;

import com.stepanio.java.training.telegram.app.scheduler.task.IdentifiableTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskScheduler {
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public void scheduleDailyTask (LocalTime targetTime, IdentifiableTask task) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstTime = now.withHour(targetTime.getHour())
                .withMinute(targetTime.getMinute())
                .withSecond(0)
                .withNano(0);

        if (now.isAfter(firstTime)) {
            firstTime.plusDays(1);
        }

        long initialDelay = Duration.between(now, firstTime).toMillis();
        long period = TimeUnit.DAYS.toMillis(1);

        scheduledExecutorService.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    public void shutDown () {
        scheduledExecutorService.shutdown();
    }
}
