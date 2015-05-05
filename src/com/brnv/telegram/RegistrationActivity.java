package com.brnv.telegram;

import android.app.Activity;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;

import android.view.View;
import android.util.Log;

import android.content.Context;
import java.io.File;

import org.drinkless.td.libcore.telegram.*;
import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi.TLFunction;

public class RegistrationActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        final Button registrationButton = (Button) findViewById(R.id.registration_button);

        final EditText phoneNumber = (EditText) findViewById(R.id.phone_number);

        registrationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String number = phoneNumber.getText().toString();
                if (RegistrationActivity.this.IsPhoneNumberValid(number)) {
                    RegistrationActivity.this.ProcessRegistration(number);
                };
            }
        });
    }

    private void ProcessRegistration(String number) {
        MyHandler handler = new MyHandler();

        TG.setUpdatesHandler(handler);

        Context context = this.getApplicationContext();
        File cacheDir = context.getCacheDir();

        TG.setDir(cacheDir.toString());

        Client client = TG.getClientInstance();

        TLFunction setPhoneNumber = new TdApi.AuthSetPhoneNumber(number);

        client.send(setPhoneNumber, handler);
    }

    public class MyHandler implements Client.ResultHandler {
        public void onResult(TdApi.TLObject object) {
            Log.v("!!!", object.getClass().toString());
            Log.v("!!!", object.toString());
        }
    }

    private boolean IsPhoneNumberValid(String number) {
        //@TODO: implement later
        return true;
    }
}
