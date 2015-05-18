package com.brnv.txmessenger;

import android.app.Activity;
import android.os.Bundle;

import org.drinkless.td.libcore.telegram.*;

import android.widget.Button;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.MenuItem;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ViewFlipper;
import android.widget.ScrollView;

import se.marteinn.ui.InteractiveScrollView;

import java.util.Date;

public class ChatsActivity extends Activity {

    public static ChatsActivity instance;

    public ViewFlipper viewFlipper;

    static public int chatShowMessagesLimit = 20;
    //static public int defaultMessagesUpdateLimit = 8;

    public TdApi.Chat currentChat;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chats);

        instance = this;

        this.viewFlipper = (ViewFlipper) findViewById(R.id.chat_activity_views);

        this.processChatsList();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
        case android.R.id.home:
            this.processChatsList();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }


    private void processChatsList() {
        TdApiResultHandler.getInstance().Send(new TdApi.GetChats(0, 3));
    }

    private void flipLayout(final Integer id) {
        runOnUiThread(new Runnable() {
            public void run() {
                ChatsActivity.instance.viewFlipper.setDisplayedChild(id);
            }
        });
    }

    private void setActionBarTitle(final String title) {
        runOnUiThread(new Runnable() {
            public void run() {
                getActionBar().setTitle(title);
            }
        });
    }

    private void setHomeButtonEnabled(final boolean enabled) {
        runOnUiThread(new Runnable() {
            public void run() {
                getActionBar().setHomeButtonEnabled(enabled);
            }
        });
    }

    private void setDisplayHomeAsUpEnabled(final boolean enabled) {
        runOnUiThread(new Runnable() {
            public void run() {
                getActionBar().setDisplayHomeAsUpEnabled(enabled);
            }
        });
    }

    private void clearView(final ViewGroup view) {
        runOnUiThread(new Runnable() {
            public void run() {
                view.removeAllViews();
            }
        });
    }

    public void ShowChats(TdApi.Chats chats) {
        final LinearLayout
            chatsListLayout = (LinearLayout) findViewById(R.id.layout_chats_list);

        this.clearView(chatsListLayout);

        for (int i = 0; i < chats.chats.length; i++) {
            this.addViewToLayout(chatsListLayout, this.getChatsEntryView(chats.chats[i]));
        }

        this.setActionBarTitle("Chats");
        this.setHomeButtonEnabled(false);
        this.setDisplayHomeAsUpEnabled(false);

        this.flipLayout(0);
    }

    class ChatsEntryClickListener implements View.OnClickListener {
        private TdApi.Chat chat;

        ChatsEntryClickListener(TdApi.Chat chat) {
            this.chat = chat;
        }

        public void onClick(View v) {
            ChatsActivity.instance.currentChat = chat;

            TdApiResultHandler.getInstance().Send(
                    new TdApi.GetChatHistory(
                        chat.id, chat.topMessage.id, -1,
                        ChatsActivity.chatShowMessagesLimit)
                    );
        }
    };

    private View getChatsEntryView(TdApi.Chat chat) {
        LinearLayout
            chatsEntryView = (LinearLayout) View.inflate(
                    ChatsActivity.instance, R.layout.chats_entry, null
                    );

        TdApi.PrivateChatInfo
            chatInfo = (TdApi.PrivateChatInfo) chat.type;

        TdApi.User user = chatInfo.user;

        TextView
            chatsEntryContactName = (TextView)
            chatsEntryView.findViewById(R.id.contact_name);

        chatsEntryContactName.setText(user.firstName + " " + user.lastName);

        TextView
            chatsEntryTextInCircle = (TextView)
            chatsEntryView.findViewById(R.id.text_in_circle);

        chatsEntryTextInCircle.setText(
                String.valueOf(user.firstName.charAt(0)) +
                String.valueOf(user.lastName.charAt(0)));

        TextView
            chatsEntryTopMessageTime = (TextView)
            chatsEntryView.findViewById(R.id.top_message_time);

        Date time = new Date((long) chat.topMessage.date * 1000);

        chatsEntryTopMessageTime.setText(
                String.format("%d:%02d", time.getHours(), time.getMinutes())
                );

        TextView
            chatsEntryTopMessage = (TextView)
            chatsEntryView.findViewById(R.id.top_message);

        switch (chat.topMessage.message.getClass().getSimpleName()) {
            case "MessageText":
                TdApi.MessageText messageText = (TdApi.MessageText) chat.topMessage.message;
                chatsEntryTopMessage.setText(messageText.text);
                break;
        }

        chatsEntryView.setOnClickListener(new ChatsEntryClickListener(chat));

        return chatsEntryView;
    }

    public void ShowChat(final TdApi.Messages messages) {
        final LinearLayout
            chatShowLayout = (LinearLayout) findViewById(R.id.layout_chat_show);

        this.clearView(chatShowLayout);

        TdApi.PrivateChatInfo
            chatInfo = (TdApi.PrivateChatInfo) ChatsActivity.instance.currentChat.type;

        TdApi.User user = chatInfo.user;

        for (int i = messages.messages.length - 1; i >= 0; i--) {
            this.addViewToLayout(chatShowLayout, this.getChatMessageView(messages.messages[i]));
        }

        this.setActionBarTitle(user.firstName + " " + user.lastName);
        this.setHomeButtonEnabled(true);
        this.setDisplayHomeAsUpEnabled(true);

        this.flipLayout(1);
    }

    private View getChatMessageView(TdApi.Message message) {
        View
            chatMessageView = (View) View.inflate(
                    ChatsActivity.instance, R.layout.chat_message, null
                    );

        TextView
            chatMessageUsername = (TextView) chatMessageView.findViewById(R.id.message_username);

        TdApi.PrivateChatInfo
            chatInfo = (TdApi.PrivateChatInfo) ChatsActivity.instance.currentChat.type;

        TdApi.User user = chatInfo.user;

        if (message.fromId == user.id) {
            chatMessageUsername.setText(user.firstName + " " + user.lastName);
        } else {
            chatMessageUsername.setText("Me");
        }

        TextView
            chatMessageTime = (TextView) chatMessageView.findViewById(R.id.message_time);

        Date time = new Date((long) message.date * 1000);

        chatMessageTime.setText(
                String.format("%d:%02d", time.getHours(), time.getMinutes())
                );

        switch (message.message.getClass().getSimpleName()) {
            case "MessageText":
                TextView
                    chatMessageContent = (TextView) chatMessageView.findViewById(R.id.message_content);

                TdApi.MessageText messageText = (TdApi.MessageText) message.message;

                chatMessageContent.setText(messageText.text);
                chatMessageContent.setVisibility(View.VISIBLE);
                break;
        }

        return chatMessageView;
    }

    //    runOnUiThread(new Runnable() {
    //        @Override
    //        public void run() {
    //            chatContentView.removeAllViews();

    //            for (int i = messages.messages.length - 1; i >= 0 ; i--) {
    //                LinearLayout messageView = ChatsActivity.instance.ShowMessage(
    //                    messages.messages[i]
    //                );

    //                ChatsActivity.instance.addViewToLayout(chatContentView, messageView);
    //            }

    //            final InteractiveScrollView
    //                chatScrollView = (InteractiveScrollView) findViewById(R.id.chat_scroll_view);

    //            if (messages.messages.length <= ChatsActivity.defaultMessagesLimit) {
    //                chatScrollView.post(new Runnable() {
    //                    @Override
    //                    public void run() {
    //                        chatScrollView.fullScroll(View.FOCUS_DOWN);
    //                    }
    //                });
    //            }

    //            if (messages.messages.length < ChatsActivity.defaultMessagesLimit) {
    //                chatScrollView.setOnTopReachedListener(null);
    //            } else {
    //                chatScrollView.setOnTopReachedListener(
    //                    new InteractiveScrollView.OnTopReachedListener() {
    //                        @Override
    //                        public void onTopReached() {
    //                            int nextChunkSize =
    //                                messages.messages.length + ChatsActivity.defaultMessagesUpdateLimit;

    //                            TdApiResultHandler.getInstance().Send(
    //                                new TdApi.GetChatHistory(
    //                                    ChatsActivity.instance.currentChat.id,
    //                                    ChatsActivity.instance.currentChat.topMessage.id, 0,
    //                                    nextChunkSize)
    //                            );
    //                        }
    //                    }
    //                );
    //            }
    //        }
    //    });

    //    Button
    //        sendMessageButton = (Button) findViewById(R.id.button_send_message);

    //    sendMessageButton.setOnClickListener(new View.OnClickListener() {
    //        public void onClick(View v) {
    //            EditText
    //                messageInput = (EditText) findViewById(R.id.input_message);

    //            TdApi.InputMessageText message = new TdApi.InputMessageText();

    //            message.text = messageInput.getText().toString();

    //            messageInput.setText("");

    //            TdApiResultHandler.getInstance().Send(
    //                new TdApi.SendMessage(currentChat.id, message)
    //            );
    //        }
    //    });
    //}

    private void addViewToLayout(final ViewGroup rootLayout, final View view) {
         runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rootLayout.addView(view);
            }
        });
    }
}
