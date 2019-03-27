package com.rtcdemo;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.rtc.RongRTCEngine;
import cn.rongcloud.rtc.engine.view.RongRTCVideoView;
import cn.rongcloud.rtc.events.RongRTCEventsListener;
import cn.rongcloud.rtc.media.http.RTCErrorCode;
import cn.rongcloud.rtc.proxy.JoinRoomUICallBack;
import cn.rongcloud.rtc.proxy.RongRTCResultUICallBack;
import cn.rongcloud.rtc.room.RongRTCRoom;
import cn.rongcloud.rtc.stream.MediaType;
import cn.rongcloud.rtc.stream.local.RongRTCCapture;
import cn.rongcloud.rtc.stream.remote.RongRTCAVInputStream;
import cn.rongcloud.rtc.user.RongRTCLocalUser;
import cn.rongcloud.rtc.user.RongRTCRemoteUser;
import io.rong.imlib.RongIMClient;

import static cn.rongcloud.rtc.core.voiceengine.BuildInfo.MANDATORY_PERMISSIONS;

public class MainActivity extends Activity implements RongRTCEventsListener {
    private static final String TAG = "MainActivity";
    private RongRTCVideoView local;
    private LinearLayout remotes;
    private String mToken = "";         //用户token
    private String mRoomId = "roomId04"; //自己可以随意修改
    private RongRTCRoom mRongRTCRoom;
    private RongRTCLocalUser mLocalUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        setContentView(R.layout.main_activity_layout);
        initView();
    }

    private void initView() {
        local = (RongRTCVideoView) findViewById(R.id.local);
        remotes = (LinearLayout) findViewById(R.id.remotes);
    }

    private void connectIM(String token) {
        RongIMClient.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onSuccess(String s) {
                Toast.makeText(MainActivity.this, "连接服务器成功", Toast.LENGTH_SHORT).show();
                RongRTCEngine.getInstance().joinRoom(mRoomId, new JoinRoomUICallBack() {
                    @Override
                    protected void onUiSuccess(RongRTCRoom rongRTCRoom) {
                        mRongRTCRoom = rongRTCRoom;
                        mLocalUser = rongRTCRoom.getLocalUser();
                        setEventListener();
                        addRemoteUsersView();
                        subscribeAll();
                        publishDefaultStream();
                    }

                    @Override
                    protected void onUiFailed(RTCErrorCode rtcErrorCode) {

                    }
                });
                RongRTCCapture.getInstance().setRongRTCVideoView(local);
                RongRTCCapture.getInstance().startCameraCapture();

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Toast.makeText(MainActivity.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onTokenIncorrect() {
                Toast.makeText(MainActivity.this, "token 非法！", Toast.LENGTH_SHORT).show();
            }

        });
    }

    /**
     * 注册监听
     */
    private void setEventListener() {
        if (mRongRTCRoom != null) {
            mRongRTCRoom.registerEventsListener(this);
        }
    }

    private void removeListener() {
        if (mRongRTCRoom != null) {
            mRongRTCRoom.unRegisterEventsListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeListener();
        RongRTCEngine.getInstance().quitRoom(mRoomId, new RongRTCResultUICallBack() {
            @Override
            public void onUiSuccess() {
                Toast.makeText(MainActivity.this, "离开房间成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUiFailed(RTCErrorCode rtcErrorCode) {
                Toast.makeText(MainActivity.this, "离开房间失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 添加远端用户View
     */
    private void addRemoteUsersView() {
        if (mRongRTCRoom != null) {
            for (RongRTCRemoteUser remoteUser : mRongRTCRoom.getRemoteUsers().values()) {
                for (RongRTCAVInputStream inputStream : remoteUser.getRemoteAVStreams()) {
                    if (inputStream.getMediaType() == MediaType.VIDEO) {
                        inputStream.setRongRTCVideoView(getNewVideoView());
                    }
                }
            }
        }

    }

    /**
     * 订阅所有当前在房间发布资源的用户
     */
    private void subscribeAll() {
        if (mRongRTCRoom != null) {
            for (RongRTCRemoteUser remoteUser : mRongRTCRoom.getRemoteUsers().values()) {
                remoteUser.subscribeAvStream(remoteUser.getRemoteAVStreams(), new RongRTCResultUICallBack() {
                    @Override
                    public void onUiSuccess() {
                        Toast.makeText(MainActivity.this, "订阅资源成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onUiFailed(RTCErrorCode rtcErrorCode) {
                        Toast.makeText(MainActivity.this, "订阅资源成功", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }
    }

    /**
     * 发布资源
     */
    private void publishDefaultStream() {
        if (mLocalUser != null) {
            mLocalUser.publishDefaultAVStream(new RongRTCResultUICallBack() {
                @Override
                public void onUiSuccess() {
                    Toast.makeText(MainActivity.this, "发布资源成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onUiFailed(RTCErrorCode rtcErrorCode) {
                    Toast.makeText(MainActivity.this, "发布资源失败", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private RongRTCVideoView getNewVideoView() {
        RongRTCVideoView videoView = RongRTCEngine.getInstance().createVideoView(this);
        remotes.addView(videoView,new LinearLayout.LayoutParams(300,300));
        remotes.bringToFront();
        return videoView;
    }


    @Override
    public void onRemoteUserPublishResource(RongRTCRemoteUser rongRTCRemoteUser, List<RongRTCAVInputStream> list) {
        for (RongRTCAVInputStream inputStream : rongRTCRemoteUser.getRemoteAVStreams()) {
            inputStream.setRongRTCVideoView(getNewVideoView());
        }
    }

    @Override
    public void onRemoteUserModifyResource(RongRTCRemoteUser rongRTCRemoteUser, List<RongRTCAVInputStream> list) {

    }

    @Override
    public void onRemoteUserUnPublishResource(RongRTCRemoteUser rongRTCRemoteUser, List<RongRTCAVInputStream> list) {

    }

    @Override
    public void onUserJoined(RongRTCRemoteUser rongRTCRemoteUser) {

    }

    @Override
    public void onUserLeft(RongRTCRemoteUser rongRTCRemoteUser) {
        for (RongRTCAVInputStream inputStream : rongRTCRemoteUser.getRemoteAVStreams()) {
            if(inputStream.getMediaType() == MediaType.VIDEO){
                remotes.removeView(inputStream.getRongRTCVideoView());
            }
        }
    }

    @Override
    public void onUserOffline(RongRTCRemoteUser rongRTCRemoteUser) {

    }

    @Override
    public void onTrackAdd(String s, String s1) {

    }

    @Override
    public void onFirstFrameDraw(String s, String s1) {

    }

    @Override
    public void onExceptionalquit() {

    }

    private List<String> unGrantedPermissions;
    private void checkPermissions() {
        unGrantedPermissions = new ArrayList();
        for (String permission : MANDATORY_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                unGrantedPermissions.add(permission);
            }
        }
        if (unGrantedPermissions.size() == 0) {//已经获得了所有权限，开始加入聊天室
            Utils.getTokenNew(this, new Utils.TokenListener() {
                @Override
                public void onTokenSuccess(String token) {
                    Log.i(TAG, "onTokenSuccess token: " + token);
                    mToken = token;
                    connectIM(mToken);
                }
            });
        } else {//部分权限未获得，重新请求获取权限
            String[] array = new String[unGrantedPermissions.size()];
            ActivityCompat.requestPermissions(this, unGrantedPermissions.toArray(array), 0);
        }
    }
}
