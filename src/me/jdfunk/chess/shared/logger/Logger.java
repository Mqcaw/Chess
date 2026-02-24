package me.jdfunk.chess.shared.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final String name;
    private static LogLevel minimumLevel = LogLevel.INFO;

    private Logger(String name) {
        this.name = name;
    }

    // Factory method to get a logger for a class
    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz.getSimpleName());
    }

    // Factory method to get a logger with a custom name
    public static Logger getLogger(String name) {
        return new Logger(name);
    }

    // Set the minimum log level globally
    public static void setMinimumLevel(LogLevel level) {
        minimumLevel = level;
    }

    // Log methods
    public void trace(String message) {
        log(LogLevel.TRACE, message, null);
    }

    public void debug(String message) {
        log(LogLevel.DEBUG, message, null);
    }

    public void info(String message) {
        log(LogLevel.INFO, message, null);
    }

    public void warn(String message) {
        log(LogLevel.WARN, message, null);
    }

    public void warn(String message, Throwable throwable) {
        log(LogLevel.WARN, message, throwable);
    }

    public void error(String message) {
        log(LogLevel.ERROR, message, null);
    }

    public void error(String message, Throwable throwable) {
        log(LogLevel.ERROR, message, throwable);
    }

    // Core logging method
    private void log(LogLevel level, String message, Throwable throwable) {
        if (level.ordinal() < minimumLevel.ordinal()) {
            return;
        }

        String timestamp = LocalDateTime.now().format(TIME_FORMAT);
        String logMessage = String.format("[%s] [%s] [%s] %s",
                timestamp,
                level.name(),
                name,
                message
        );

        // Print to appropriate stream
        if (level == LogLevel.ERROR) {
            System.err.println(level.getColor() + logMessage + LogLevel.RESET);
        } else {
            System.out.println(level.getColor() + logMessage + LogLevel.RESET);
        }

        // Print stack trace if throwable is provided
        if (throwable != null) {
            throwable.printStackTrace(System.err);
        }
    }

    // Log level enum
    public enum LogLevel {
        TRACE("\u001B[37m"),   // White
        DEBUG("\u001B[36m"),   // Cyan
        INFO("\u001B[32m"),    // Green
        WARN("\u001B[33m"),    // Yellow
        ERROR("\u001B[31m");   // Red

        private static final String RESET = "\u001B[0m";
        private final String color;

        LogLevel(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }
    }
}