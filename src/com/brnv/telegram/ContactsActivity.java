package com.brnv.telegram;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.view.LayoutInflater;

import android.content.Context;

import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;

import org.drinkless.td.libcore.telegram.*;

public class ContactsActivity extends Activity {

    public static ContactsActivity instance;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);
        getActionBar().setTitle("Contacts");

        instance = this;

        TdApiResultHandler.getInstance().Send(new TdApi.GetContacts());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
        case R.id.contacts_add:
            Log.v("!!!", "add contact");
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public void onPause() {
        super.onPause();
        this.finish();
    }

    public void ListContacts(TdApi.Contacts contacts) {
        final TextView product = new TextView(this);
        product.setText("test");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout
                    rootLayout = (LinearLayout) findViewById(R.id.layout_contacts);

                rootLayout.addView(product);

                //@TODO:iterate contacts.users and add views
            }
        });
    }
}
