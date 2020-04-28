package com.yifei.reptile.util.yyk99;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author yifei
 * @date 2020/4/25
 */
public class Yyk99Properties {
    private static final Logger logger = LoggerFactory.getLogger(Yyk99Properties.class);
    private static Properties prop = null;
    /**
     * properties文件路径
     */
    private static final String PROPERTIES_FILE_PATH = "yyk99.properties";

    static {
        loadProperties();
    }

    private Yyk99Properties() {
    }

    /**
     * 获取配置值
     *
     * @param key key
     * @return String
     */
    public static String getProperty(String key) {
        if (prop == null) {
            loadProperties();
        }
        return prop.getProperty(key);
    }

    /**
     * 获取配置值
     *
     * @param key        key
     * @param defaultVal 默认值
     * @return String
     */
    public static String getProperty(String key, String defaultVal) {
        String val = getProperty(key);
        return StringUtils.isBlank(val) ? defaultVal : val;
    }

    /**
     * 加载properties文件
     */
    private synchronized static void loadProperties() {
        try {
            prop = new Properties();
            prop.load(Yyk99Properties.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_PATH));
        } catch (Exception e) {
            logger.error("加载【{}】出现异常：", PROPERTIES_FILE_PATH, e);
        }
    }
}
