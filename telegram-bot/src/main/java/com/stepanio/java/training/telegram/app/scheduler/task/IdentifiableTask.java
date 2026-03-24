package com.stepanio.java.training.telegram.app.scheduler.task;

public interface IdentifiableTask extends Runnable{
    Long getId();
}
