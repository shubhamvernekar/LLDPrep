import java.util.*;

enum LEVEL {
    INFO,
    DEBUG,
    ERROR
}

abstract class Logger {
    LEVEL level;
    Logger nextLogger;

    Logger(LEVEL level, Logger nextLogger) {
        this.level = level;
        this.nextLogger = nextLogger;
    }

    public void log(LEVEL level, String message) {
        if(this.level == level) {
            this.write(message);
            return;
        }
        if(nextLogger != null) {
            nextLogger.log(level, message);
        }
    }

    abstract public void write(String message);
}

class InfoLogger extends Logger {
    InfoLogger(Logger nextLogger) {
        super(LEVEL.INFO, nextLogger);
    }

    public void write(String message) {
        System.out.println("Info Log : " + message);
    }
}

class DebugLogger extends Logger {
    DebugLogger(Logger nextLogger) {
        super(LEVEL.DEBUG, nextLogger);
    }

    public void write(String message) {
        System.out.println("Debug Log : " + message);
    }
}

class ErrorLogger extends Logger {
    ErrorLogger(Logger nextLogger) {
        super(LEVEL.ERROR, nextLogger);
    }

    public void write(String message) {
        System.out.println("Error Log : " + message);
    }
}

class LoggerSystem {
    private static Logger logger;
    private static LoggerSystem instance;

    private LoggerSystem() {
        logger = new InfoLogger(new DebugLogger(new ErrorLogger(null)));
    }

    public static LoggerSystem getInstance() {
        if(instance == null) {
            instance = new LoggerSystem();
        }
         return instance;
    }

    public void Log(LEVEL level, String message) {
        logger.log(level, message);
    }
}

class LoggeingSystem {
    public static void main(String[] args) {

        LoggerSystem.getInstance().Log(LEVEL.INFO, "normal testing");
        LoggerSystem.getInstance().Log(LEVEL.DEBUG, "normal testing");
        LoggerSystem.getInstance().Log(LEVEL.ERROR, "normal testing");
    }
}
