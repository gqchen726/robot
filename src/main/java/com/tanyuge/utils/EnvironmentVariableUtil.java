package com.tanyuge.utils;

import java.util.List;

/**
 * description
 *
 * @author guoqing.chen01@hand-china.com 2022/07/05 14:10
 */
public class EnvironmentVariableUtil {

    public static String getSystemProperty(String key) {
        return System.getProperty(key);
    }

    public static String getSystemEnvironmentVariable(String key) {
        return System.getenv(key);
    }
}
