package com.brnv.telegram;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;

import org.drinkless.td.libcore.telegram.*;

import android.widget.ViewFlipper;

import android.view.View;
import android.widget.Button;

import android.widget.EditText;

public class ContactsActivity extends Activity {

    public static ContactsActivity instance;

    public ViewFlipper viewFlipper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);

        instance = this;
        viewFlipper = (ViewFlipper) findViewById(R.id.contacts_views);

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
            ContactsActivity.instance.AddContact();
            return true;
        case android.R.id.home:
            TdApiResultHandler.getInstance().Send(new TdApi.GetContacts());
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public void onPause() {
        super.onPause();
        this.finish();
    }

    public void AddContact() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActionBar().setTitle("New contact");
                getActionBar().setDisplayHomeAsUpEnabled(true);
                ContactsActivity.instance.viewFlipper.setDisplayedChild(1);
            }
        });

        Button
            addContactButton = (Button) findViewById(R.id.button_add_contact);

        addContactButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.setEnabled(false);

                EditText
                    firstNameInput = (EditText) findViewById(R.id.input_contact_first_name);

                String firstName = firstNameInput.getText().toString();

                EditText
                    lastNameInput = (EditText) findViewById(R.id.input_contact_last_name);

                String lastName = lastNameInput.getText().toString();

                EditText
                    phoneInput = (EditText) findViewById(R.id.input_contact_phone);

                String phone = phoneInput.getText().toString();

                Log.v("!!! add contact", firstName + " " + lastName + " " + phone);
            }
        });
    }

    public void ListContacts(TdApi.Contacts contacts) {
        final TextView product = new TextView(this);
        product.setText("test");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActionBar().setTitle("Contacts");
                getActionBar().setDisplayHomeAsUpEnabled(false);

                ContactsActivity.instance.viewFlipper.setDisplayedChild(0);

                LinearLayout
                    rootLayout = (LinearLayout) findViewById(R.id.layout_contacts);

                rootLayout.addView(product);

                //@TODO:iterate contacts.users and add views
            }
        });
    }
}
