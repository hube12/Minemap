package kaptainwutax.minemap.init;

import kaptainwutax.minemap.MineMap;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import static kaptainwutax.minemap.MineMap.LOG_DIR;


public class Logger {
    public static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(MineMap.class.getName());

    public static void registerLogger() {
        try {
            FileHandler fh = new FileHandler(LOG_DIR + File.separatorChar + "error%u%g.log", 1000000, 10);
            LOGGER.setUseParentHandlers(false);
            LOGGER.addHandler(fh);
            fh.setFormatter(new SimpleFormatter() {
                private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

                @Override
                public synchronized String format(LogRecord lr) {
                    return String.format(format,
                            new Date(lr.getMillis()),
                            lr.getLevel().getLocalizedName(),
                            lr.getMessage()
                    );
                }
            });
            LOGGER.info(String.format("Minemap started on %s",
                    new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z").
                            format(new Date(System.currentTimeMillis()))));
            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();

            Map<String, String> systemProperties = runtimeBean.getSystemProperties();
            Set<String> keys = systemProperties.keySet();
            String username = "";
            if (keys.contains("user.name")) {
                username = systemProperties.get("user.name");
            }

            for (String key : keys) {
                LOGGER.info(String.format("[%s] = %s.", key, systemProperties.get(key).
                        replace(username, "XANONX").
                        replaceAll("Users\\\\.*?\\\\", "Users\\\\ANONYM\\\\").
                        replace("\r\n", "CRLF").
                        replace("\n", "LF")
                ));
            }
        } catch (IOException e) {
            Logger.LOGGER.severe(e.toString());
            e.printStackTrace();
        }
    }
}
