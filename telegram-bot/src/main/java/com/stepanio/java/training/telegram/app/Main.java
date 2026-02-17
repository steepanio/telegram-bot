package com.stepanio.java.training.telegram.app;

import com.stepanio.java.training.telegram.app.app.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        String token = System.getenv("BOT_TOKEN");

        if (token == null) {
            log.error("Token not found");
        } else {
            log.info("Token loaded");
        }

        Application app = new Application();
        app.run();
    }
}
