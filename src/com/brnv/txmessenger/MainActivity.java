package com.brnv.txmessenger;

import android.app.Activity;
import android.os.Bundle;

import android.content.Intent;

import java.io.File;

import org.drinkless.td.libcore.telegram.*;

import android.util.Log;

public class MainActivity extends Activity {

    static public MainActivity instance;

    public Intent registration;

    public Intent contacts;

    public Intent chats;

    public TdApi.User currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        contacts = new Intent(this, ContactsActivity.class);
        chats = new Intent(this, ChatsActivity.class);
        registration = new Intent(this, RegistrationActivity.class);


        this.init();
    }

    private void init() {
        File cacheDir = this.getApplicationContext().getCacheDir();
        TG.setDir(cacheDir.toString());
        TG.setUpdatesHandler(TdApiResultHandler.getInstance());
        this.checkAuth();
    }

    public void onResume() {
        super.onResume();
        this.init();
    }

    public void StartMessenger() {
        startIntent(chats);
    }

    public void Register(String state) {
        registration.putExtra("state", state);
        registration.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startIntent(registration);
    }

    private void checkAuth() {
        TdApiResultHandler.getInstance().Send(new TdApi.AuthGetState());
    }

    private void startIntent(final Intent intent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
            }
        });
    }
}
