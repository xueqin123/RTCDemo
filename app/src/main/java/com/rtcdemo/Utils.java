package com.rtcdemo;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.rongcloud.rtc.media.http.HttpClient;
import cn.rongcloud.rtc.media.http.Request;
import cn.rongcloud.rtc.media.http.RequestMethod;
import cn.rongcloud.rtc.utils.FinLog;
import io.rong.imlib.common.DeviceUtils;

public class Utils {
    private static final String TAG = "Utils";
    private static final String BASE_URL = "http://apiqa.rongcloud.net/";
    private static final String URL_GET_TOKEN_NEW = "user/get_token_new";
    interface TokenListener{
        void onTokenSuccess(String token);
    }

    public static void getTokenNew(Context context,final TokenListener listener) {
        JSONObject loginInfo = new JSONObject();
        try {
            loginInfo.put("id", DeviceUtils.getDeviceId(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Request request = new Request.Builder()
                .url(BASE_URL + URL_GET_TOKEN_NEW)
                .method(RequestMethod.POST)
                .body(loginInfo.toString())
                .build();
        HttpClient.getDefault().request(request, new HttpClient.ResultCallback() {
            @Override
            public void onResponse(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        jsonObject = jsonObject.getJSONObject("result");
                        final String token = jsonObject.getString("token");
                        listener.onTokenSuccess(token);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                FinLog.d(TAG, "getToken error = " + errorCode);
            }

            @Override
            public void onError(IOException exception) {
                FinLog.d(TAG, "getToken IOException = " + exception.getMessage());
            }
        });
    }
}
