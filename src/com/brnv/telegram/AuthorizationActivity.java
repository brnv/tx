package com.brnv.telegram;

import android.app.Activity;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;

import android.view.View;

import java.io.File;

import android.util.Log;

import org.drinkless.td.libcore.telegram.*;
import org.drinkless.td.libcore.telegram.TdApi.TLFunction;

public class AuthorizationActivity extends Activity {

    public static AuthorizationActivity instance;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        this.initTG();

        TLFunction getAuth = new TdApi.AuthGetState();

        TG.getClientInstance().send(
            getAuth, TdApiResultHandler.getInstance()
        );
    }

    private void initTG() {
        File cacheDir = this.getApplicationContext().getCacheDir();
        TG.setDir(cacheDir.toString());

        TG.setUpdatesHandler(TdApiResultHandler.getInstance());
    }

    public void SetPhoneNumber() {
        Log.v("!!!", "set phone");

        setContentView(R.layout.form_auth_phone);

        Button
            setPhoneButton = (Button) findViewById(R.id.button_set_phone);

        setPhoneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.setEnabled(false);

                Registration registration = Registration.getInstance();

                EditText
                    phoneNumberInput = (EditText) findViewById(R.id.input_phone);
                String phoneNumber = phoneNumberInput.getText().toString();

                registration.SubmitPhone(phoneNumber);
            }
        });
    }

    public void SetName() {
        Log.v("!!!", "set name");

        setContentView(R.layout.form_auth_name);
    }

    public void SetCode() {
        Log.v("!!!", "set code");

        setContentView(R.layout.form_auth_code);
    }

    public void onPause() {
        super.onPause();
        this.finish();
    }
}
