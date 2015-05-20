package com.brnv.txmessenger;

import android.app.Activity;
import android.os.Bundle;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewFlipper;

import android.content.Intent;

import org.drinkless.td.libcore.telegram.*;

import android.util.Log;

public class RegistrationActivity extends Activity {

    public static RegistrationActivity instance;

    public ViewFlipper viewFlipper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        instance = this;

        this.viewFlipper = (ViewFlipper) findViewById(R.id.registration_views);

        this.processRegistration();
    }

    public void onResume() {
        super.onResume();
        this.processRegistration();
    }

    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    private void processRegistration() {
        switch (getIntent().getStringExtra("state")) {
            case "PHONE":
                this.showPhoneScreen();
                break;
            case "CODE":
                this.showCodeScreen();
                break;
            case "NAME":
                this.showNameScreen();
                break;
        }
    }

    private void flipLayout(final Integer id) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RegistrationActivity.instance.viewFlipper.setDisplayedChild(id);
            }
        });
    }

    private void showPhoneScreen() {
        this.SetActionBarTitle("Phone number");
        this.flipLayout(0);

        Button
            setPhoneButton = (Button) findViewById(R.id.button_set_phone);

        setPhoneButton.setOnClickListener(this.setPhoneButtonListener);
    }

    private void showNameScreen() {
        this.SetActionBarTitle("Name");
        this.flipLayout(1);

        Button
            setNameButton = (Button) findViewById(R.id.button_set_name);

        setNameButton.setOnClickListener(this.setNameButtonListener);
    }

    private void showCodeScreen() {
        this.SetActionBarTitle("Code");
        this.flipLayout(2);

        Button
            setCodeButton = (Button) findViewById(R.id.button_submit_code);

        setCodeButton.setOnClickListener(setCodeButtonListener);
    }

    private View.OnClickListener setPhoneButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            v.setEnabled(false);

            EditText
                phoneNumberInput = (EditText) findViewById(R.id.input_phone);
            String phone = phoneNumberInput.getText().toString();

            TdApiResultHandler.getInstance().Send(
                    new TdApi.AuthSetPhoneNumber(phone)
                    );
        }
    };

    private View.OnClickListener setNameButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            v.setEnabled(false);

            EditText
                firstNameInput = (EditText) findViewById(R.id.input_first_name);

            String firstName = firstNameInput.getText().toString();

            EditText
                lastNameInput = (EditText) findViewById(R.id.input_last_name);

            String lastName = lastNameInput.getText().toString();

            TdApiResultHandler.getInstance().Send(
                    new TdApi.AuthSetName(firstName, lastName)
                    );
        }
    };

    public View.OnClickListener setCodeButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            EditText
                codeInput = (EditText) findViewById(R.id.input_telegram_code);

            String code = codeInput.getText().toString();

            TdApiResultHandler.getInstance().Send(
                    new TdApi.AuthSetCode(code)
                    );
        }
    };

    public void SetActionBarTitle(final String title) {
        runOnUiThread(new Runnable() {
            public void run() {
                getActionBar().setTitle(title);
            }
        });
    }
}
