package com.github.yanghyu.gifu.foundation.net.json;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * JSON请求发送与接收
 *
 * @author yanghongyu
 * @since 2020-06-09
 */
public class JsonHttpClient {

    private static final Logger log = LoggerFactory.getLogger(JsonHttpClient.class);

    /**
     * 连接超时时间
     */
    private static final int CONNECT_TIMEOUT = 8000;

    /**
     * 字符编码格式
     */
    private static final String CHARSET_UTF_8_NAME = "UTF-8";

    /**
     * 发送请求
     *
     * @param path 链接
     * @param method 请求方法
     * @param data 发送数据(会序列化为JSON数据串)
     * @param headers 头信息
     * @param callback 结果回调
     */
    public static void request(String path, String method, Object data, Map<String, String> headers, ResultCallback callback) {
        HttpURLConnection connection = null;
        OutputStream outputStream = null;
        try {
            URL url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();
            /* 设置请求方式 */
            connection.setRequestMethod(method);
            /* 设置编码格式 */
            connection.setRequestProperty("Charset", CHARSET_UTF_8_NAME);
            /* 设置内容格式 */
            connection.setRequestProperty("Content-type", "application/json;charset=utf-8");
            /* 设置接收类型 */
            connection.setRequestProperty("Accept", "application/json");
            /* 设置容许输出 */
            connection.setDoOutput(true);
            /* 连接超时时间为8s */
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            /* 传递头部携带信息 */
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    connection.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            if (data != null) {
                /* 传递请求参数*/
                outputStream = connection.getOutputStream();
                if (data instanceof String) {
                    outputStream.write(((String) data).getBytes(StandardCharsets.UTF_8));
                } else {
                    String jsonStr = JSON.toJSONString(data);
                    outputStream.write(jsonStr.getBytes(StandardCharsets.UTF_8));
                }
                outputStream.close();
            }
            resolveResult(connection, callback);
        } catch (IOException e) {
            callback.fail("{ \"resultCode\": \"FAIL\", \"resultMessage\": \"请求发送失败！\" }");
            log.error("发送请求失败！", e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static void resolveResult(HttpURLConnection connection, ResultCallback callback) throws IOException {
        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        byte[] cache = new byte[2048];
        int len;
        try {
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                byteArrayOutputStream = new ByteArrayOutputStream();
                while ((len = inputStream.read(cache)) > 0) {
                    byteArrayOutputStream.write(cache, 0, len);
                }
                String responseData = byteArrayOutputStream.toString(CHARSET_UTF_8_NAME);
                byteArrayOutputStream.close();
                inputStream.close();
                callback.success(responseData);
            } else {
                inputStream = connection.getErrorStream();
                byteArrayOutputStream = new ByteArrayOutputStream();
                while ((len = inputStream.read(cache)) > 0) {
                    byteArrayOutputStream.write(cache, 0, len);
                }
                String responseData = byteArrayOutputStream.toString(CHARSET_UTF_8_NAME);
                byteArrayOutputStream.close();
                inputStream.close();
                callback.fail(responseData);
            }
        } finally {
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
