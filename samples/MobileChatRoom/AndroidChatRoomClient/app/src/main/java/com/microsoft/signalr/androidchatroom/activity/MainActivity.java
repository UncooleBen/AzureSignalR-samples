package com.microsoft.signalr.androidchatroom.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.microsoft.signalr.androidchatroom.R;
import com.microsoft.signalr.androidchatroom.service.FirebaseService;
import com.microsoft.signalr.androidchatroom.service.NotificationService;
import com.microsoft.signalr.androidchatroom.view.ChatFragment;
import com.microsoft.signalr.androidchatroom.view.LoginFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static MainActivity mainActivity;
    public static Boolean isVisible = false;

    private NotificationService notificationService;

    private LoginFragment loginFragment;
    private final ServiceConnection notificationServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            NotificationService.NotificationServiceBinder notificationServiceBinder = (NotificationService.NotificationServiceBinder) service;
            notificationService = notificationServiceBinder.getService();
            loginFragment.setDeviceUuid(notificationService.getDeviceUuid());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            notificationService = null;
        }
    };
    private ChatFragment chatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bindNotificationService();
        FirebaseService.createNotificationChannel(getApplicationContext());
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }

    public void bindNotificationService() {
        Intent intent = new Intent(this, NotificationService.class);
        bindService(intent, notificationServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onBackPressed() {
        if (chatFragment != null) {
            chatFragment.onBackPressed();
            chatFragment = null;
        }
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }

    public void setLoginFragment(LoginFragment loginFragment) {
        this.loginFragment = loginFragment;
    }

    public void setChatFragment(ChatFragment chatFragment) {
        this.chatFragment = chatFragment;
    }
}