package com.example.wechatcounter;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NotificationMonitor extends NotificationListenerService {
    String weChatAccount,username;
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        Log.i("onNotificationPosted", "posted");
        Bundle extras = sbn.getNotification().extras;
        String notificationPkg = sbn.getPackageName();
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        String notificationText = extras.getString(Notification.EXTRA_TEXT);

        //XSL_Test: Notification posted 雨 & 请求添加你为朋友 & com.tencent.mm
        Log.i("XSL_Test", "Notification posted " + notificationTitle + " & " + notificationText + " & " + notificationPkg);
        if (notificationPkg.equals("com.tencent.mm")) {
            if ("请求添加你为朋友".equals(notificationText)) {
                sendCounter(notificationTitle);
            }
        }
    }

    private void sendCounter(String nickName) {
        OkHttpClient client = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("account", weChatAccount)
                .add("nickName", nickName)
                .add("username", username)
                .build();
        final Request request = new Request.Builder()
                .url("http://182.61.174.46:18080/post")
                .post(formBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.code() == 200) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "统计成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationPosted(sbn, rankingMap);
        String[] orderKeys = rankingMap.getOrderedKeys();
        for (String orderKey : orderKeys) {
            Log.i("OrderedKeys", orderKey);
        }
        Log.i("describeContents", rankingMap.describeContents() + "");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        weChatAccount = intent.getStringExtra("weChatAccount");
        username = intent.getStringExtra("username");
        return super.onStartCommand(intent, flags, startId);

    }
}