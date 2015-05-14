package com.brnv.telegram;

import android.app.Activity;
import android.os.Bundle;

import org.drinkless.td.libcore.telegram.*;
import org.drinkless.td.libcore.telegram.TdApi.MessageText;

import android.widget.Button;
import android.view.View;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.RelativeLayout;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout.LayoutParams;

import android.widget.ViewFlipper;

public class ChatActivity extends Activity {

    public static ChatActivity instance;

    public ViewFlipper viewFlipper;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        instance = this;
        viewFlipper = (ViewFlipper) findViewById(R.id.chat_activity_views);

        TdApiResultHandler.getInstance().Send(new TdApi.GetChats(0, 3));
    }

    private void showChatListScreen() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActionBar().setTitle("Chats");
                getActionBar().setHomeButtonEnabled(false);
                getActionBar().setDisplayHomeAsUpEnabled(false);
                ChatActivity.instance.viewFlipper.setDisplayedChild(0);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
        case android.R.id.home:
            ChatActivity.instance.showChatListScreen();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public void ListChats(TdApi.Chats chats) {
        ChatActivity.instance.showChatListScreen();

        for (int i = 0; i < chats.chats.length; i++) {
            final TdApi.Chat chat = chats.chats[i];

            TextView
                chatEntryView = new TextView(ChatActivity.instance);

            TdApi.PrivateChatInfo chatInfo = (TdApi.PrivateChatInfo) chat.type;
            TdApi.User user = chatInfo.user;

            chatEntryView.setText(user.firstName + " " + user.lastName);

            chatEntryView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    TdApiResultHandler.getInstance().Send(
                        new TdApi.GetChatHistory(chat.id, chat.topMessage.id, 0, 1000)
                    );

                    TdApi.PrivateChatInfo chatInfo = (TdApi.PrivateChatInfo) chat.type;
                    final TdApi.User user = chatInfo.user;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getActionBar().setTitle(user.firstName + " " + user.lastName);
                            getActionBar().setHomeButtonEnabled(true);
                            getActionBar().setDisplayHomeAsUpEnabled(true);
                            ChatActivity.instance.viewFlipper.setDisplayedChild(1);
                        }
                    });
                }
            });

            this.addViewToLayout(R.id.layout_chat_list, chatEntryView);
        }
    }

    public void ShowChat(final TdApi.Messages messages) {
        final long chatId = messages.messages[0].chatId;

        final LinearLayout
            chatContentView = (LinearLayout) findViewById(R.id.chat_messages_view);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatContentView.removeAllViews();

                for (int i = messages.messages.length - 1; i >= 0 ; i--) {
                    ChatActivity.instance.ShowMessage(messages.messages[i]);
                }
            }
        });

        Button
            sendMessageButton = (Button) findViewById(R.id.button_send_message);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText
                    messageInput = (EditText) findViewById(R.id.input_message);

                TdApi.InputMessageText message = new TdApi.InputMessageText();

                message.text = messageInput.getText().toString();

                messageInput.setText("");

                TdApiResultHandler.getInstance().Send(
                    new TdApi.SendMessage(chatId, message)
                );
            }
        });
    }

    public void ShowMessage(TdApi.Message message) {
        switch (message.message.getClass().getSimpleName()) {
            case "MessageText":
                TextView
                    messageView = new TextView(ChatActivity.instance);
                TdApi.MessageText messageText = (TdApi.MessageText) message.message;
                messageView.setText(messageText.text);
                this.addViewToLayout(R.id.chat_messages_view, messageView);
                break;
        }
    }

    private void addViewToLayout(final int layoutId, final View view) {
         runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout
                    rootLayout = (LinearLayout) findViewById(layoutId);

                rootLayout.addView(view);
            }
        });
    }
}
