package com.brnv.telegram;

import android.app.Activity;
import android.os.Bundle;

import org.drinkless.td.libcore.telegram.*;

import android.widget.Button;

import android.view.View;

import android.util.Log;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.RelativeLayout;

import android.widget.LinearLayout.LayoutParams;

public class ChatActivity extends Activity {

    public static ChatActivity instance;

    long chatId;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        instance = this;

        this.chatId = 0;

        ChatActivity.instance.ShowChat();
        //TdApiResultHandler.getInstance().Send(new TdApi.GetChat(this.chatId));
    }

    public void ShowChat() {
        Button
            sendMessageButton = (Button) findViewById(R.id.button_send_message);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText
                    messageInput = (EditText) findViewById(R.id.input_message);

                String message = messageInput.getText().toString();

                messageInput.setText("");

                ChatActivity.instance.ShowMessage(message);
            }
        });
    }

    public void ShowMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final TextView messageView = new TextView(ChatActivity.instance);
                messageView.setText(message);

                LinearLayout
                    rootLayout = (LinearLayout) findViewById(R.id.layout_chat);

                rootLayout.addView(messageView);
            }
        });
    }
}
