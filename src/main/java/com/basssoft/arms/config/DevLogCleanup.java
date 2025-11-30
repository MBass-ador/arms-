package com.basssoft.arms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import jakarta.annotation.PreDestroy;
import java.io.File;
import java.util.Objects;

/**
 * Development Log Cleanup Component

 *  wipes log files on application shutdown
 *  in "dev" mode only

 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@Component
@Profile("dev")
public class DevLogCleanup {


    @Value("${LOG_HOME:logs}")
    private String logDir;

    @PreDestroy
    public void wipeLogsOnShutdown() {
        File dir = new File(logDir);
        if (dir.exists() && dir.isDirectory()) {
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (file.isFile() && file.getName().endsWith(".log")) {
                    file.delete();
                }
            }
        }
        System.out.println("DevLogCleanup: wiped logs on shutdown.");
    }
}
