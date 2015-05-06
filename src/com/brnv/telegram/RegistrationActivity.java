package com.brnv.telegram;

import android.app.Activity;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;

import android.view.View;

import org.drinkless.td.libcore.telegram.*;
import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi.TLFunction;

import android.content.Intent;

public class RegistrationActivity extends Activity {

	Intent telegramCodeIntent;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

		this.initRegistrationButton();

		telegramCodeIntent = new Intent(this, TelegramCodeActivity.class);
    }

    public class SubmitPhoneResultHandler implements Client.ResultHandler {
        public void onResult(TdApi.TLObject object) {
			//@TODO: errors checking?
			startActivity(telegramCodeIntent);
        }
    }

    private void initRegistrationButton() {
        Button
			registrationButton = (Button) findViewById(R.id.button_register);

        registrationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
				v.setEnabled(false);
				RegistrationActivity.this.submitPhone();
            }
        });
    }

    private void submitPhone() {
		String number = this.getPhoneNumber();

		if (RegistrationActivity.this.IsPhoneValid(number) == false) {
			return ;
		}

        SubmitPhoneResultHandler
			handler = new SubmitPhoneResultHandler();

        TG.setUpdatesHandler(handler);

        TLFunction setPhone = new TdApi.AuthSetPhoneNumber(number);

        TG.getClientInstance().send(setPhone, handler);
    }

	private String getPhoneNumber() {
		EditText
			phoneNumberInput = (EditText) findViewById(R.id.input_phone);

		return phoneNumberInput.getText().toString();
	}

    private boolean IsPhoneValid(String number) {
        //@TODO: implement later
        return true;
    }
}
