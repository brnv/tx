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

import se.marteinn.ui.InteractiveScrollView;

import java.util.concurrent.Callable;

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
        this.flipLayout(0);
        this.setActionBarTitle("Chats");
        this.setHomeButtonEnabled(false);
        this.setDisplayHomeAsUpEnabled(false);

        final LinearLayout
            chatsListLayout = (LinearLayout) findViewById(R.id.layout_chats_list);

        this.clearView(chatsListLayout);

        this.addViewToLayout(chatsListLayout, this.getChatsEntriesView(chats));
    }

    private LinearLayout getChatsEntriesView(TdApi.Chats chats) {
        LinearLayout chatsEntriesView = new LinearLayout(ChatsActivity.instance);

        chatsEntriesView.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < chats.chats.length; i++) {
            chatsEntriesView.addView(this.getChatsEntryView(chats.chats[i]));
        }

        return chatsEntriesView;
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
                        chat.id, chat.topMessage.id, 0,
                        ChatsActivity.chatShowMessagesLimit)
                    );
        }
    };

    private View getChatsEntryView(TdApi.Chat chat) {
        TextView
            chatsEntryView = (TextView) View.inflate(
                ChatsActivity.instance, R.layout.chats_entry, null
            );

        TdApi.PrivateChatInfo
            chatInfo = (TdApi.PrivateChatInfo) chat.type;

        TdApi.User user = chatInfo.user;

        chatsEntryView.setText(user.firstName + " " + user.lastName);

        chatsEntryView.setOnClickListener(new ChatsEntryClickListener(chat));

        return chatsEntryView;
    }

    public void ShowChat(final TdApi.Messages messages) {
        this.flipLayout(1);

        TdApi.PrivateChatInfo
            chatInfo = (TdApi.PrivateChatInfo) ChatsActivity.instance.currentChat.type;

        TdApi.User user = chatInfo.user;

        this.setActionBarTitle(user.firstName + " " + user.lastName);
        this.setHomeButtonEnabled(true);
        this.setDisplayHomeAsUpEnabled(true);

        final LinearLayout
            chatShowLayout = (LinearLayout) findViewById(R.id.layout_chat_show);

        this.clearView(chatShowLayout);

        //this.addViewToLayout(chatShowLayout, this.getChatMessagesView(messages));
    }


    //private View getChatMessagesView(TdApi.Messages messages) {
    //    //TextView
    //    //    chatsEntryView = (TextView) View.inflate(
    //    //        ChatsActivity.instance, R.layout.chats_entry, null
    //    //    );

    //    //TdApi.PrivateChatInfo
    //    //    chatInfo = (TdApi.PrivateChatInfo) chat.type;

    //    //TdApi.User user = chatInfo.user;

    //    //chatsEntryView.setText(user.firstName + " " + user.lastName);

    //    //chatsEntryView.setOnClickListener(new ChatsEntryClickListener(chat));

    //    //return chatsEntryView;
    //}


    //    Log.v("!!!", messages.toString());
    //    if (messages.messages.length == 0) {
    //        return ;
    //    }

    //    final long chatId = messages.messages[0].chatId;

    //    ChatsActivity.instance.showChatScreen(ChatsActivity.instance.currentChat);

    //    final LinearLayout
    //        chatContentView = (LinearLayout) findViewById(R.id.chat_messages_view);

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

    ////@TODO: omg
    //// refactor this code
    //public LinearLayout ShowMessage(TdApi.Message message) {
    //    LinearLayout
    //        messageView = new LinearLayout(ChatsActivity.instance);

    //    messageView.setPadding(0, 5, 0, 0);

    //    messageView.setLayoutParams(
    //            new LayoutParams(
    //                LayoutParams.FILL_PARENT,
    //                LayoutParams.WRAP_CONTENT)
    //            );

    //    messageView.setOrientation(LinearLayout.HORIZONTAL);

    //    ImageView
    //        userImage = new ImageView(ChatsActivity.instance);

    //    userImage.setLayoutParams(
    //            new LayoutParams(
    //                LayoutParams.FILL_PARENT,
    //                LayoutParams.WRAP_CONTENT, 3f)
    //            );

    //    messageView.addView(userImage);

    //    LinearLayout
    //        messageContent = new LinearLayout(ChatsActivity.instance);

    //    messageContent.setLayoutParams(
    //            new LayoutParams(
    //                LayoutParams.FILL_PARENT,
    //                LayoutParams.WRAP_CONTENT, 1f)
    //            );

    //    messageContent.setOrientation(LinearLayout.VERTICAL);

    //    LinearLayout
    //        messageContentInfo = new LinearLayout(ChatsActivity.instance);

    //    messageContentInfo.setLayoutParams(
    //            new LayoutParams(
    //                LayoutParams.FILL_PARENT,
    //                LayoutParams.FILL_PARENT, 1f)
    //            );

    //    messageContentInfo.setOrientation(LinearLayout.HORIZONTAL);

    //    TdApi.PrivateChatInfo
    //        chatInfo = (TdApi.PrivateChatInfo) ChatsActivity.instance.currentChat.type;

    //    final TdApi.User user = chatInfo.user;

    //    TextView
    //        userName = new TextView(ChatsActivity.instance);

    //    userName.setLayoutParams(
    //            new LayoutParams(
    //                LayoutParams.WRAP_CONTENT,
    //                LayoutParams.WRAP_CONTENT)
    //            );

    //    userName.setPadding(0, 0, 5, 0);

    //    if (message.fromId == user.id) {
    //        userName.setText(user.firstName + " " + user.lastName);
    //    } else {
    //        userName.setText("Me");
    //    }

    //    messageContentInfo.addView(userName);

    //    TextView
    //        messageTime = new TextView(ChatsActivity.instance);

    //    messageTime.setLayoutParams(
    //            new LayoutParams(
    //                LayoutParams.WRAP_CONTENT,
    //                LayoutParams.WRAP_CONTENT)
    //            );

    //    messageTime.setText("2:09 AM");

    //    messageContentInfo.addView(messageTime);

    //    LinearLayout
    //        messageContentMessage = new LinearLayout(ChatsActivity.instance);

    //    messageContentMessage.setLayoutParams(
    //            new LayoutParams(
    //                LayoutParams.FILL_PARENT,
    //                LayoutParams.FILL_PARENT, 1f)
    //            );

    //    messageContentMessage.setPadding(0, 5, 0, 10);

    //    messageContentMessage.setOrientation(LinearLayout.HORIZONTAL);

    //    switch (message.message.getClass().getSimpleName()) {
    //        case "MessageText":
    //            TdApi.MessageText messageText = (TdApi.MessageText) message.message;
    //            TextView
    //                messageTextMessage = new TextView(ChatsActivity.instance);

    //            messageTextMessage.setText(messageText.text);
    //            messageContentMessage.addView(messageTextMessage);

    //            break;
    //    }

    //    messageContent.addView(messageContentInfo);

    //    messageContent.addView(messageContentMessage);

    //    messageView.addView(messageContent);

    //    return messageView;
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
