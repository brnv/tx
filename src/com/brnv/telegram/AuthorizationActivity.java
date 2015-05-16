package com.brnv.telegram;

import android.app.Activity;
import android.os.Bundle;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewFlipper;

import android.content.Intent;

import java.io.File;

import org.drinkless.td.libcore.telegram.*;

public class AuthorizationActivity extends Activity {

    public static AuthorizationActivity instance;

    public ViewFlipper viewFlipper;

    public Intent contactsActivityIntent;

    public Intent chatActivityIntent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorization);

        instance = this;
        viewFlipper = (ViewFlipper) findViewById(R.id.authorization_views);

        contactsActivityIntent =  new Intent(this, ContactsActivity.class);
        chatActivityIntent = new Intent(this, ChatActivity.class);

        this.initTG();

        setTitle("Authorization");

        TdApiResultHandler.getInstance().Send(new TdApi.AuthGetState());
    }

    private void initTG() {
        File cacheDir = this.getApplicationContext().getCacheDir();
        TG.setDir(cacheDir.toString());

        TG.setUpdatesHandler(TdApiResultHandler.getInstance());
    }

    public void SetPhoneNumber() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AuthorizationActivity.instance.viewFlipper.setDisplayedChild(0);
            }
        });

        Button
            setPhoneButton = (Button) findViewById(R.id.button_set_phone);

        setPhoneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.setEnabled(false);

                EditText
                    phoneNumberInput = (EditText) findViewById(R.id.input_phone);
                String phoneNumber = phoneNumberInput.getText().toString();

                Registration.getInstance().SubmitPhone(phoneNumber);
            }
        });
    }

    public void SetName() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AuthorizationActivity.instance.viewFlipper.setDisplayedChild(1);
            }
        });

        Button
            setNameButton = (Button) findViewById(R.id.button_set_name);

        setNameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.setEnabled(false);

                EditText
                    firstNameInput = (EditText) findViewById(R.id.input_first_name);

                String firstName = firstNameInput.getText().toString();

                EditText
                    lastNameInput = (EditText) findViewById(R.id.input_last_name);

                String lastName = lastNameInput.getText().toString();

                Registration.getInstance().SubmitName(firstName, lastName);
            }
        });
    }

    public void SetCode() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AuthorizationActivity.instance.viewFlipper.setDisplayedChild(2);
            }
        });

        Button
            setCodeButton = (Button) findViewById(R.id.button_submit_code);

        setCodeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.setEnabled(false);

                EditText
                    codeInput = (EditText) findViewById(R.id.input_telegram_code);

                String code = codeInput.getText().toString();

                Registration.getInstance().SubmitCode(code);
            }
        });

    }

    public void StartContactsActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(AuthorizationActivity.instance.contactsActivityIntent);
            }
        });
    }

    public void StartChatActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(AuthorizationActivity.instance.chatActivityIntent);
            }
        });
    }

    public void onPause() {
        super.onPause();
        this.finish();
    }
}
