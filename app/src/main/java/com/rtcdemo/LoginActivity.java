package com.rtcdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.RongIMClient;

import static cn.rongcloud.rtc.core.voiceengine.BuildInfo.MANDATORY_PERMISSIONS;

public class LoginActivity extends Activity implements View.OnClickListener {

    private Button Btn1;
    private Button Btn2;
    private View view;
    private List<String> unGrantedPermissions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_layout);
        view = findViewById(R.id.token_select);
        Btn1 = (Button) findViewById(R.id.token1);
        Btn2 = (Button) findViewById(R.id.token2);
        Btn1.setOnClickListener(this);
        Btn2.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        String token = "";
        switch (v.getId()) {
            case R.id.token1:
                token = MyApp.token1;
                break;
            case R.id.token2:
                token = MyApp.token2;
                break;
        }
        view.setVisibility(View.GONE);
        checkPermissions(); //请求权限
        connectIM(token);

    }

    private void connectIM(String token) {
        RongIMClient.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                Toast.makeText(LoginActivity.this, "onTokenIncorrect()", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String s) {
                Toast.makeText(LoginActivity.this, "IM 连接成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Toast.makeText(LoginActivity.this, "onError()", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void checkPermissions() {
        unGrantedPermissions = new ArrayList();
        for (String permission : MANDATORY_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                unGrantedPermissions.add(permission);
            }
        }
        if (unGrantedPermissions.size() != 0) {//已经获得了所有权限，开始加入聊天室
            String[] array = new String[unGrantedPermissions.size()];
            ActivityCompat.requestPermissions(this, unGrantedPermissions.toArray(array), 0);
        }
    }

}
