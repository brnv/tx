package com.brnv.telegram;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;

public class LoginActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    //public void onPause() {
    //    super.onPause();
    //}
}
