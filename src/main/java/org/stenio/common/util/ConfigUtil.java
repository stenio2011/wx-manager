package org.stenio.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class ConfigUtil {

    private static final Logger logger = LoggerFactory.getLogger(ConfigUtil.class);

    private static final String CONFIG_LOCATION = "conf/config.properties";

    private static Properties props;

    static {
        init();
    }

    private static void init() {
        load();
    }

    private static synchronized void load() {
        if (props == null) {
            InputStream in = null;
            InputStreamReader reader = null;
            try {
                in = ConfigUtil.class.getClassLoader().getResourceAsStream(CONFIG_LOCATION);
                reader = new InputStreamReader(in, "UTF-8");
                props = new Properties();
                props.load(reader);
            } catch (Exception e) {
                logger.error("read configuration file error, file path : {}", CONFIG_LOCATION);
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                }
            }
        }
    }

    public static void reload() throws IOException {
        load();
    }

    public static String getString(String key) {
        return props.getProperty(key);
    }

    public static Integer getInt(String key) {
        return Integer.parseInt(getString(key));
    }

    public static Long getLong(String key) {
        return Long.parseLong(getString(key));
    }

    public static Double getDouble(String key) {
        return Double.parseDouble(getString(key));
    }

    public static Float getFloat(String key) {
        return Float.parseFloat(getString(key));
    }

    public static Boolean getBoolean(String key) {
        return Boolean.parseBoolean(getString(key));
    }

}
