package com.rtcdemo;

import android.app.Application;

import io.rong.imlib.RongIMClient;

public class MyApp extends Application {
    public static final String appkey="sfci50a7s4q5i";
    public static final String token1="+XhpDeWs1dj/CtJqNBSHha5VbzBm9J5AteeZN+MbeIuyHPNP/QFxndTpzYmVzZKSTFPVWauINNqV8GB7+3x38w==";
    public static final String token2="pa2MrRa848XreUpBbIDNKt/apXzcNtCFru7N98jVVrZb6mtUtJnrh6cWPS6Lu8ursDTJE5M5nto=";
    @Override
    public void onCreate() {
        super.onCreate();
        RongIMClient.init(this, appkey, false);
    }
}
