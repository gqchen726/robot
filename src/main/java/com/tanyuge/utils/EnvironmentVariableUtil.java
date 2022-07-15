package com.tanyuge.utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * description
 *
 * @author guoqing.chen01@hand-china.com 2022/07/05 14:10
 */
public class EnvironmentVariableUtil {

    private static final Logger logger = LoggerFactory.getLogger(EnvironmentVariableUtil.class);

    public static String getSystemProperty(String key) {
        return System.getProperty(key);
    }

    public static String getSystemEnvironmentVariable(String key) {
        return System.getenv(key);
    }

    public static String getCustomEnvironmentVariable(String key) throws IOException {
        String command = "cmd /c set " + key;
        logger.info("cmd控制台执行命令：{}", command);
        Process process = Runtime.getRuntime().exec(command);
        InputStreamReader inputStreamReader =
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(inputStreamReader);
        String line = null;
        StringBuilder result = new StringBuilder();
        while((line = br.readLine()) != null) {
            result.append(line);
        }
        br.close();
        inputStreamReader.close();

        InputStreamReader inputStreamReader1 =
                new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8);
        String encoding = inputStreamReader1.getEncoding();
        logger.info("cmd控制台输出编码: {}", encoding);
        BufferedReader br1 = new BufferedReader(inputStreamReader1);
        String line1 = null;
        StringBuilder result1 = new StringBuilder();
        while((line1 = br1.readLine()) != null) {
            result.append(line1);
        }
        br1.close();
        inputStreamReader1.close();
        if (StringUtils.isNoneBlank(result1)) {
            logger.error("cmd控制台执行命令出错：{}", result1);
        }
        return result.toString();
    }

    @Test
    public void getCustomEnvironmentVariableTest() {
        try {
            String imcUser = getCustomEnvironmentVariable("ImcUser");
            String ImcPassword = getCustomEnvironmentVariable("ImcPassword");
            String ImcUrl = getCustomEnvironmentVariable("ImcUrl");
            logger.info("读取结果: {} {} {} ", imcUser, ImcPassword, ImcUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
