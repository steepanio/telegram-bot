package com.stepanio.java.training.telegram.app.scheduler;

import com.stepanio.java.training.telegram.app.scheduler.task.IdentifiableTask;
package com.stepanio.java.training.telegram.app.scheduler;

import com.stepanio.java.training.telegram.app.scheduler.task.IdentifiableTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.*;

public class TaskScheduler {

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    // 👉 храним задачи по userId
    private final Map<Long, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();


    public void scheduleOrReplace(LocalTime targetTime, IdentifiableTask task) {
        Long userId = task.getId();

        cancelTask(userId);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstTime = now.withHour(targetTime.getHour())
                .withMinute(targetTime.getMinute())
                .withSecond(0)
                .withNano(0);

        if (now.isAfter(firstTime)) {
            firstTime = firstTime.plusDays(1);
        }

        long initialDelay = Duration.between(now, firstTime).toMillis();
        long period = TimeUnit.DAYS.toMillis(1);

        ScheduledFuture<?> future = executor.scheduleAtFixedRate(
                task,
                initialDelay,
                period,
                TimeUnit.MILLISECONDS
        );

        tasks.put(userId, future);
    }


    public void cancelTask(Long userId) {
        ScheduledFuture<?> future = tasks.get(userId);

        if (future != null) {
            future.cancel(false);
            tasks.remove(userId);
        }
    }


    public void shutdown() {
        executor.shutdown();
    }
}
