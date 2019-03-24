package com.rtcdemo;

import android.app.Application;

import io.rong.imlib.RongIMClient;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RongIMClient.setServerInfo("http://navqa.cn.ronghub.com", "http://navxq.rongcloud.net");
        RongIMClient.init(this, "c9kqb3rdkbb8j", false);
    }
}
