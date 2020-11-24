package com.github.yanghyu.gifu.data.server.leaf.controller;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;


public class IdControllerTest {

    private final OkHttpClient okHttpClient = new OkHttpClient();

    @Test
    public void next() throws IOException {
        long start = System.currentTimeMillis();
        String url = "http://localhost:8080/ids/next?key=default1";
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        String body = Objects.requireNonNull(call.execute().body()).string();
        System.out.println("结果:" + body + "\n");
        long end = System.currentTimeMillis();
        System.out.println("耗时:" + (end - start) + "\n");
    }

    @Test
    public void largeFlow() throws IOException {
        long start = System.currentTimeMillis();
        int num = 10000;
        for (int i = 0; i < num; i ++) {
            next();
        }
        long end = System.currentTimeMillis();
        System.out.println(Thread.currentThread().getName() + "线程耗时:" + (end - start) + "\n");
    }

    public static void main(String[] args) {
        concurrent();
    }

    public static void concurrent() {
        int threadNum = 100;
        for (int i = 0; i < threadNum; i ++) {
            Thread thread = new Thread(() -> {
                try {
                    new IdControllerTest().largeFlow();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
    }

}