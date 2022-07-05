package com.tanyuge.controller;

import com.tanyuge.utils.EnvironmentVariableUtil;
import com.tanyuge.utils.PingUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * eip系统-HRMS系统
 * <p>
 * 1.通过curl请求构造代码
 * 2.cookie如何自动获取替换
 * 3.获取返回的json数据进行解析
 * 4.触发邮件发送
 * 5.推送企业微信通知
 *
 * @author chunlin.qi@hand-china.com
 * @version 1.0
 * @description
 * @date 2021/12/27
 */
@RestController
@RequestMapping("/robot")
public class RobotController {
    /**
     * 只创建一次 SimpleDateFormat 对象，避免不必要的资源消耗
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Logger log = LoggerFactory.getLogger(RobotController.class);
    private static final String logStart = "---自动登录程序开始---";
    private static final String logEnd = "---自动登录程序结束---";
    private static final String chromeDriverName = "chromedriver.exe";
    private static final String WEBDRIVER_CHROME_DRIVER = "webdriver.chrome.driver";
    @Value("${project.base.path}")
    private String basePath;
    @Value("${imc.username}")
    private String username;
    @Value("${imc.password}")
    private String password;
    @Value("${imc.url}")
    private String url;


    public WebDriver driver;


    /**
     * 周期定时执行，根据ping结果决定是否重连网络
     */
//    @Scheduled(cron = "${robot.schedule.cron}")
    @GetMapping("/connect/of/ping/timeout")
    public void connectOfPingTimeout() {
        this.setParams();
        String ipaddr = "110.42.157.115";
        int pingTimes = 5;
        int pingTimeout = 1000;
        boolean pingResult = PingUtil.ping(ipaddr, pingTimes, pingTimeout);
        log.info("ping {}(pingTimes: {}, pingTimeout: {}) result:{}",
                ipaddr, pingTimes, pingTimeout, pingResult);
        log.info("base path is {}", basePath);
        if (!pingResult) {
            log.info(logStart);
            String driverPath = basePath + chromeDriverName;
            File file = new File(driverPath);
            System.setProperty(WEBDRIVER_CHROME_DRIVER, file.getAbsolutePath());
            if (ObjectUtils.isEmpty(driver)) {
                ChromeOptions chromeOptions = new ChromeOptions();
                driver = new ChromeDriver(chromeOptions);
            }
            driver.manage().window().maximize();
//            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            driver.get(url);

            // 先登出
            driver.findElement(By.id("id_lable_logoutbutton_auth")).click();
            driver.findElement(By.id("id_userName")).sendKeys(username);
            driver.findElement(By.id("id_userPwd")).sendKeys(password);

            // 再登入
            driver.findElement(By.id("id_lable_loginbutton_auth")).click();

            driver.close();
            log.info(logEnd);
        }
    }

    /**
     * 周期性执行
     */
//    @Scheduled(cron = "${robot.schedule.cron1}")
    @GetMapping("/connect/of/timing")
    public void connectOfTiming() {
        this.setParams();
        log.info(logStart);
        String driverPath = basePath + chromeDriverName;
        File file = new File(driverPath);
        System.setProperty(WEBDRIVER_CHROME_DRIVER, file.getAbsolutePath());
        if (ObjectUtils.isEmpty(driver)) {
            ChromeOptions chromeOptions = new ChromeOptions();
            driver = new ChromeDriver(chromeOptions);
        }
        driver.manage().window().maximize();
//            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(url);

        // 先登出
        driver.findElement(By.id("id_lable_logoutbutton_auth")).click();
        driver.findElement(By.id("id_userName")).sendKeys(username);
        driver.findElement(By.id("id_userPwd")).sendKeys(password);

        // 再登入
        driver.findElement(By.id("id_lable_loginbutton_auth")).click();

        driver.close();
        log.info(logEnd);
    }

    /**
     * 定时执行一次
     */
    @Scheduled(cron = "${robot.schedule.cron2}")
    @Scheduled(cron = "${robot.schedule.cron4}")
    @GetMapping("/connect/of/cycle")
    public void connectOfCycle() {
        this.setParams();
        log.info(logStart);
        String driverPath = basePath + chromeDriverName;
        File file = new File(driverPath);
        System.setProperty(WEBDRIVER_CHROME_DRIVER, file.getAbsolutePath());
        if (ObjectUtils.isEmpty(driver)) {
            ChromeOptions chromeOptions = new ChromeOptions();
            driver = new ChromeDriver(chromeOptions);
        }
        driver.manage().window().maximize();
//            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(url);

        // 先登出
        driver.findElement(By.id("id_lable_logoutbutton_auth")).click();
        driver.findElement(By.id("id_userName")).sendKeys(username);
        driver.findElement(By.id("id_userPwd")).sendKeys(password);

        // 再登入
        driver.findElement(By.id("id_lable_loginbutton_auth")).click();

        driver.close();
        log.info(logEnd);
    }

    private void setParams() {
        String usernameOfKey = "imc_username";
        String passwordOfKey = "imc_password";
        String urlOfKey = "imc_url";
        String imcUsername = EnvironmentVariableUtil.getSystemEnvironmentVariable(usernameOfKey);
        String imcPassword = EnvironmentVariableUtil.getSystemEnvironmentVariable(passwordOfKey);
        String imcUrl = EnvironmentVariableUtil.getSystemEnvironmentVariable(urlOfKey);
        if (StringUtils.isBlank(username) && StringUtils.isNoneBlank(imcUsername)) {
            username = imcUsername;
        }
        if (StringUtils.isBlank(password) && StringUtils.isNoneBlank(imcPassword)) {
            password = imcPassword;
        }
        if (StringUtils.isBlank(url) && StringUtils.isNoneBlank(imcUrl)) {
            url = imcUrl;
        }

        if (
                StringUtils.isBlank(username) ||
                StringUtils.isBlank(password) ||
                StringUtils.isBlank(url)
        ) {
            log.error("请在系统中配置环境变量: 用户名：{}, 密码：{}, 目标网络地址{}",
                    usernameOfKey, passwordOfKey, urlOfKey);
        }
    }

}