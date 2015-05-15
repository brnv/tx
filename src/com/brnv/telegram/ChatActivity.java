package com.brnv.telegram;

import android.app.Activity;
import android.os.Bundle;

import org.drinkless.td.libcore.telegram.*;
import org.drinkless.td.libcore.telegram.TdApi.Chats;
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
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class ChatActivity extends Activity {

    public static ChatActivity instance;

    public ViewFlipper viewFlipper;

    public TdApi.Chats chats;

    public TdApi.Chat currentChat;

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
            TdApiResultHandler.getInstance().Send(new TdApi.GetChats(0, 3));
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public void ListChats(TdApi.Chats chats) {
        ChatActivity.instance.showChatListScreen();

        ChatActivity.instance.chats = chats;

        final LinearLayout
            chatListView = (LinearLayout) findViewById(R.id.layout_chat_list);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatListView.removeAllViews();
            }
        });

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
                }
            });

            ChatActivity.instance.addViewToLayout(chatListView, chatEntryView);
        }
    }

    private void showChatScreen(TdApi.Chat chat) {
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

    public void ShowChat(final TdApi.Messages messages) {
        final long chatId = messages.messages[0].chatId;

        for (int i = 0; i < ChatActivity.instance.chats.chats.length; i++) {
            if (ChatActivity.instance.chats.chats[i].id == chatId) {
               ChatActivity.instance.currentChat = ChatActivity.instance.chats.chats[i];
               break;
            }
        }

        ChatActivity.instance.showChatScreen(ChatActivity.instance.currentChat);

        final LinearLayout
            chatContentView = (LinearLayout) findViewById(R.id.chat_messages_view);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatContentView.removeAllViews();

                for (int i = messages.messages.length - 1; i >= 0 ; i--) {
                    LinearLayout messageView = ChatActivity.instance.ShowMessage(
                        messages.messages[i]
                    );

                    ChatActivity.instance.addViewToLayout(chatContentView, messageView);
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
                    new TdApi.SendMessage(currentChat.id, message)
                );
            }
        });
    }

    //@TODO: omg
    // refactor this code
    public LinearLayout ShowMessage(TdApi.Message message) {
        LinearLayout
            messageView = new LinearLayout(ChatActivity.instance);

        messageView.setPadding(0, 5, 0, 0);

        messageView.setLayoutParams(
                new LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT)
                );

        messageView.setOrientation(LinearLayout.HORIZONTAL);

        ImageView
            userImage = new ImageView(ChatActivity.instance);

        userImage.setLayoutParams(
                new LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT, 3f)
                );

        messageView.addView(userImage);

        LinearLayout
            messageContent = new LinearLayout(ChatActivity.instance);

        messageContent.setLayoutParams(
                new LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT, 1f)
                );

        messageContent.setOrientation(LinearLayout.VERTICAL);

        LinearLayout
            messageContentInfo = new LinearLayout(ChatActivity.instance);

        messageContentInfo.setLayoutParams(
                new LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.FILL_PARENT, 1f)
                );

        messageContentInfo.setOrientation(LinearLayout.HORIZONTAL);

        TdApi.PrivateChatInfo
            chatInfo = (TdApi.PrivateChatInfo) ChatActivity.instance.currentChat.type;

        final TdApi.User user = chatInfo.user;

        TextView
            userName = new TextView(ChatActivity.instance);

        userName.setLayoutParams(
                new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT)
                );

        userName.setPadding(0, 0, 5, 0);

        if (message.fromId == user.id) {
            userName.setText(user.firstName + " " + user.lastName);
        } else {
            userName.setText("Me");
        }

        messageContentInfo.addView(userName);

        TextView
            messageTime = new TextView(ChatActivity.instance);

        messageTime.setLayoutParams(
                new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT)
                );

        messageTime.setText("2:09 AM");

        messageContentInfo.addView(messageTime);

        LinearLayout
            messageContentMessage = new LinearLayout(ChatActivity.instance);

        messageContentMessage.setLayoutParams(
                new LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.FILL_PARENT, 1f)
                );

        messageContentMessage.setPadding(0, 5, 0, 10);

        messageContentMessage.setOrientation(LinearLayout.HORIZONTAL);

        switch (message.message.getClass().getSimpleName()) {
            case "MessageText":
                TdApi.MessageText messageText = (TdApi.MessageText) message.message;
                TextView
                    messageTextMessage = new TextView(ChatActivity.instance);

                messageTextMessage.setText(messageText.text);
                messageContentMessage.addView(messageTextMessage);

                break;
        }

        messageContent.addView(messageContentInfo);

        messageContent.addView(messageContentMessage);

        messageView.addView(messageContent);

        return messageView;
    }

    private void addViewToLayout(final LinearLayout rootLayout, final View view) {
         runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rootLayout.addView(view);
            }
        });
    }
}
