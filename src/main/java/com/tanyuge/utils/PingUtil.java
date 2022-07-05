package com.tanyuge.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * description
 *
 * @author guoqing.chen01@hand-china.com 2022/06/24 18:38
 */
public class PingUtil {
    private static Logger logger = LoggerFactory.getLogger(PingUtil.class);

    /**ping  ipaddress 完整返回信息*/
    public static String executeLinuxCmd(String ipAddress, int pingTimes, int timeOut) {
        Runtime run = Runtime.getRuntime();
        String pingCommand;
        try {
            String osName = System.getProperty("os.name");
            if(osName.contains("Windows")){
                pingCommand = "ping " + ipAddress + " -n " + pingTimes    + " -w " + timeOut;
            }else{
                pingCommand = "ping " + " -c " + "4" + " -w " + "2 " + ipAddress;
            }
            Process process = run.exec(pingCommand);
            InputStream in = process.getInputStream();
            BufferedReader bs = new BufferedReader(new InputStreamReader(in, Charset.forName("GBK")));
            StringBuffer out = new StringBuffer();
            String content = null;
            while ((content = bs.readLine()) != null) {
                out.append(content + "\n");
            }
            in.close();
            process.destroy();
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**ping  ipaddress 完整返回true在线 false离线*/
    public static boolean ping(String ipAddress, int pingTimes, int timeOut) {
        BufferedReader in = null;
        String pingCommand;
        Runtime r = Runtime.getRuntime();
        String osName = System.getProperty("os.name");
        if(osName.contains("Windows")){
            pingCommand = "ping " + ipAddress + " -n " + pingTimes    + " -w " + timeOut;
        }else{
            pingCommand = "ping " + " -c " + "4" + " -w " + "2 " + ipAddress;
        }
        try {
            Process p = r.exec(pingCommand);
            if (p == null) {
                return false;
            }
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            int connectedCount = 0;
            String line;
            while ((line = in.readLine()) != null) {
                connectedCount += getCheckResult(line,osName);
            }
            return connectedCount >= 2 ? true : false;
        } catch (Exception ex) {
            ex.printStackTrace(); //出现异常则返回假
            return false;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static int getCheckResult(String line,String osName) {
        if(osName.contains("Windows")){
            if(line.contains("TTL=")){
                return 1;
            }
        }else{
            if(line.contains("ttl=")){
                return 1;
            }
        }
        return 0;
    }
}