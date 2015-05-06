package com.brnv.telegram;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;

import java.io.File;

import org.drinkless.td.libcore.telegram.*;

public class LoginActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent registrationIntent = new Intent(this, RegistrationActivity.class);

        startActivity(registrationIntent);

        this.initTG();
    }

    private void initTG() {
        File cacheDir = this.getApplicationContext().getCacheDir();
        TG.setDir(cacheDir.toString());
    }

    //public void onPause() {
    //    super.onPause();
    //}
}
