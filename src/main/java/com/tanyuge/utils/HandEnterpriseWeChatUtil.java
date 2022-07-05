package com.tanyuge.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 企业微信请求工具类
 *
 * @author chunlin.qi@hand-china.com
 * @version 1.0
 * @description
 * @date 2022/1/6
 */
@Slf4j
public class HandEnterpriseWeChatUtil {

    /**
     * 发送企业微信机器人通知
     * 参考链接：https://work.weixin.qq.com/api/doc/90000/90136/91770
     *
     * @param webHookUrl  机器人特有的webhookurl
     * @param mobileList  需要通知的人的手机号码
     * @param sendContent 发送企业微信的内容
     * @return
     */
    public static int sendEnterpriseWeChatRobotNotice(String webHookUrl, List<String> mobileList, String sendContent) {
        // 1、创建一个httpClient客户端对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            //某个群组添加机器人之后，创建者可以在机器人详情页看的该机器人特有的webhookurl
            // 2、创建一个HttpPost请求
            HttpPost httpPost = new HttpPost(webHookUrl);
            //httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
            httpPost.setHeader("Content-Type", " text/html; charset=UTF-8");

            //data数据格式：{"msgtype":"text","text":{"content":"广州今日天气：29度，大部分多云，降雨概率：60%","mentioned_list":["wangqing","@all"],"mentioned_mobile_list":["13800001111","@all"]}}
            Map<String, Object> dataMap = new HashMap<>(2);
            dataMap.put("msgtype", "text");

            Map<String, Object> textMap = new HashMap<>(2);
            textMap.put("content", sendContent);

            //"mentioned_mobile_list":["13800001111","@all"]
            textMap.put("mentioned_mobile_list", mobileList);

            dataMap.put("text", textMap);
            log.info("发送企业微信通知参数：{}", JSONObject.toJSONString(dataMap));

            //放参数进post请求里面 json类型/UTF-8
            httpPost.setEntity(new StringEntity(JSONObject.toJSONString(dataMap), Charset.forName("UTF-8")));

            // 3、执行post请求操作，并拿到结果
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            log.info("机器人推送企业微信事件执行结果：{}", statusCode);
            return statusCode;
        } catch (Exception e) {
            log.error("机器人推送企业微信执行失败,具体原因：{}", e.getMessage());
            e.printStackTrace();
        } finally {

        }
        return 500;
    }
}
