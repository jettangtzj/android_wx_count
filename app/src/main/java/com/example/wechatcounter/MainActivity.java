package com.example.wechatcounter;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    String weChatAccount,username;
    public EditText weChatAccountET, usernameET;
    public Button saveBT;
    public SharedPreferences sp;
    public SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!isEnabled()) {
            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "监控器开关已打开", Toast.LENGTH_SHORT);
            toast.show();
        }
        initView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        weChatAccount = weChatAccountET.getText().toString();
        username = usernameET.getText().toString();
        Intent intent = new Intent(this,NotificationMonitor.class);
        intent.putExtra("weChatAccount",weChatAccount);
        intent.putExtra("username",username);
        startService(intent);
    }

    private void initView() {
        weChatAccountET = findViewById(R.id.weChatAccountET);
        usernameET = findViewById(R.id.usernameET);
        saveBT = findViewById(R.id.save);
        isAccountEntered();
        saveBT.setOnClickListener(this);
        sp = getSharedPreferences("wechatCounter_Account", 0);
        editor = sp.edit();
        weChatAccountET.setText(sp.getString("weChatAccount", ""));
        usernameET.setText(sp.getString("username", ""));
    }

    private void isAccountEntered() {
        if (weChatAccountET.getText().toString().isEmpty()) {
            Toast.makeText(this, "请输入微信账号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (usernameET.getText().toString().isEmpty()) {
            Toast.makeText(this, "请输入管理后台账号", Toast.LENGTH_SHORT).show();
        }
    }

    // 判断是否打开了通知监听权限
    private boolean isEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                saveAccount();
                break;
        }
    }

    private void saveAccount() {
        if (null == usernameET.getText().toString() && null == weChatAccountET.getText().toString()) {
            Toast.makeText(this, "请先输入", Toast.LENGTH_SHORT);
        }
        if (null != usernameET.getText().toString()) {
            editor.putString("username", usernameET.getText().toString());
        } else {
            Toast.makeText(this, "管理系统账号为空", Toast.LENGTH_SHORT).show();
        }
        if (null != weChatAccountET.getText().toString()) {
            editor.putString("weChatAccount", weChatAccountET.getText().toString());

        } else {
            Toast.makeText(this, "微信账号为空", Toast.LENGTH_SHORT).show();
        }

        if (editor.commit()) {
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        }
    }
}
