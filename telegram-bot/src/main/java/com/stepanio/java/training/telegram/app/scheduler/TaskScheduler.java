package com.stepanio.java.training.telegram.app.scheduler;

import com.stepanio.java.training.telegram.app.scheduler.task.IdentifiableTask;

import java.time.*;
import java.util.Map;
import java.util.concurrent.*;


    public class TaskScheduler {

        private final ScheduledExecutorService executor =
                Executors.newScheduledThreadPool(5);

        private final Map<Long, ScheduledFuture<?>> tasks =
                new ConcurrentHashMap<>();

        public void scheduleOrReplace(LocalTime targetTime,
                                      ZoneId zoneId,
                                      IdentifiableTask task) {

            Long userId = task.getId();
            cancelTask(userId);
            ZonedDateTime now = ZonedDateTime.now(zoneId);

            ZonedDateTime firstRun = now
                    .withHour(targetTime.getHour())
                    .withMinute(targetTime.getMinute())
                    .withSecond(0)
                    .withNano(0);

            if (now.isAfter(firstRun)) {
                firstRun = firstRun.plusDays(1);
            }

            long initialDelay = Duration.between(now, firstRun).toMillis();
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
            ScheduledFuture<?> future = tasks.remove(userId);

            if (future != null) {
                future.cancel(false);
            }
        }

        public void shutdown() {
            executor.shutdown();
        }
    }