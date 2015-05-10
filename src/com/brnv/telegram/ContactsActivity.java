package com.brnv.telegram;

import android.app.Activity;
import android.os.Bundle;

import android.util.Log;

public class ContactsActivity extends Activity {

    public static ContactsActivity instance;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);

        instance = this;
    }

    public void onPause() {
        super.onPause();
        this.finish();
    }
}

