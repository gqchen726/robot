package com.tanyuge.controller;

import com.tanyuge.utils.EnvironmentVariableUtil;
import com.tanyuge.utils.PingUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
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
    private static final String ID_LABLE_LOGOUTBUTTON_AUTH = "id_lable_logoutbutton_auth";
    private static final String ID_USERNAME = "id_userName";
    private static final String ID_USERPWD = "id_userPwd";
    private static final String ID_LABLE_LOGINBUTTON_AUTH = "id_lable_loginbutton_auth";
    @Value("${project.base.path}")
    private String basePath;
    private String username;
    private String password;
    private String url;
    @Value("${project.base.targetHost}")
    private String targetHost;
    @Value("${project.base.urlOfKey}")
    private String urlOfKey;
    @Value("${project.base.usernameOfKey}")
    private String usernameOfKey;
    @Value("${project.base.passwordOfKey}")
    private String passwordOfKey;


    public WebDriver driver;


    /**
     * 周期定时执行，根据ping结果决定是否重连网络
     */
    @Scheduled(cron = "${robot.schedule.cron}")
    @GetMapping("/connect/of/ping/timeout")
    public void connectOfPingTimeout() throws InterruptedException {
        try {
            this.setParams();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isBlank(targetHost)) {
            log.info("PING目标地址未配置，请检查配置文件！");
        }
        int pingTimes = 5;
        int pingTimeout = 1000;
        boolean pingResult = PingUtil.ping(targetHost, pingTimes, pingTimeout);
        log.info("ping {}(pingTimes: {}, pingTimeout: {}) result:{}",
                targetHost, pingTimes, pingTimeout, pingResult);
        log.info("base path is {}", basePath);
        if (!pingResult) {
            this.autoConnect();
        }
    }

    /**
     * 定时执行一次
     */
//    @Scheduled(cron = "${robot.schedule.cron4}")
//    @Scheduled(cron = "${robot.schedule.cron5}")
//    @Scheduled(cron = "${robot.schedule.cron6}")
    @GetMapping("/connect/of/timing")
    public void connectOfTiming() throws InterruptedException {
        this.autoConnect();
    }

    private void autoConnect() throws InterruptedException {
        try {
            this.setParams();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info(logStart);
        String driverPath = basePath + chromeDriverName;
        File file = new File(driverPath);
        System.setProperty(WEBDRIVER_CHROME_DRIVER, file.getAbsolutePath());
        if (ObjectUtils.isEmpty(driver)) {
            ChromeOptions chromeOptions = new ChromeOptions();
            driver = new ChromeDriver(chromeOptions);
            if (ObjectUtils.isEmpty(driver)) {
                log.info("浏览器驱动创建成功");
            }
        }
        driver.manage().window().maximize();
//        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(1, TimeUnit.SECONDS);
        driver.get(url);
        log.info("浏览器访问地址: {}", url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(2));
        // 查找id为“id_lable_logoutbutton_auth"的元素是否加载出来了（已经在页面DOM中存在）
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(ID_LABLE_LOGOUTBUTTON_AUTH)));
        wait.until(ExpectedConditions.elementToBeClickable(By.id(ID_LABLE_LOGOUTBUTTON_AUTH)));
        // 先登出
        driver.findElement(By.id(ID_LABLE_LOGOUTBUTTON_AUTH)).click();
        Thread.sleep(500);
        // 点击弹出框的确定
        // 首先需要切换到弹出框中，获取Alert对象。
        Alert alert = driver.switchTo().alert();
        alert.accept();
        log.info("登出成功");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(ID_USERNAME)));
        driver.findElement(By.id(ID_USERNAME)).sendKeys(username);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(ID_USERPWD)));
        driver.findElement(By.id(ID_USERPWD)).sendKeys(password);
        Thread.sleep(500);

        // 再登入
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(ID_LABLE_LOGINBUTTON_AUTH)));
        wait.until(ExpectedConditions.elementToBeClickable(By.id(ID_LABLE_LOGINBUTTON_AUTH)));
        driver.findElement(By.id(ID_LABLE_LOGINBUTTON_AUTH)).click();
        log.info("登入成功");

        driver.close();
        log.info(logEnd);
    }

    /**
     * 周期性执行
     */
//    @Scheduled(cron = "${robot.schedule.cron1}")
    @GetMapping("/connect/of/cycle")
    public void connectOfCycle() throws InterruptedException {
        this.autoConnect();
    }

    /**
     * 读取环境变量中配置的参数
     * @throws Exception
     */
    private void setParams() throws Exception {
        if (
                StringUtils.isBlank(usernameOfKey) ||
                StringUtils.isBlank(passwordOfKey) ||
                StringUtils.isBlank(urlOfKey)
        ) {
            throw new RuntimeException("环境变量名未配置，usernameOfKey 或 passwordOfKey 或 urlOfKey");
        }
        String imcUsername = EnvironmentVariableUtil.getCustomEnvironmentVariable(usernameOfKey);
        String imcPassword = EnvironmentVariableUtil.getCustomEnvironmentVariable(passwordOfKey);
        String imcUrl = EnvironmentVariableUtil.getCustomEnvironmentVariable(urlOfKey);
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
        } else {
            log.info("读取环境变量完成");
        }
    }

    /**
     * 单元测试
     */
    @Test
    public void autoConnectionTest() {
        basePath = "C:/dev/env/browerDriver/";
        targetHost = "www.baidu.com";
        urlOfKey = "ImcUrl";
        usernameOfKey = "ImcUser";
        passwordOfKey = "ImcPassword";
        try {
            this.connectOfPingTimeout();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
