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

import android.graphics.Rect;

import android.view.MotionEvent;

import java.util.Date;
import java.util.Calendar;
import java.util.Locale;

import android.view.Menu;

import android.text.TextWatcher;
import android.text.Editable;

import android.graphics.drawable.GradientDrawable;

import android.content.Intent;

public class ChatActivity extends Activity {

    public Intent chatsActivity;

    public TdApi.Chat currentChat;

    public static ChatActivity instance;

    static public int chatShowMessagesLimit = 20;
    static public int chatUpdateMessageLimit = 5;

    //public TdApi.Chat currentChat;
    public int currentChatOldestMessageId;

    public boolean chatUpdateMode = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chats);

        chatsActivity = new Intent(this, ChatsActivity.class);

        instance = this;

        TdApiResultHandler.getInstance().Send(new TdApi.GetChat(getIntent().getLongExtra("chat_id", 0)));
    }

    public void onResume() {
        super.onResume();
        TdApiResultHandler.getInstance().Send(new TdApi.GetChat(getIntent().getLongExtra("chat_id", 0)));
    }

    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    private void clearView(final ViewGroup view) {
        runOnUiThread(new Runnable() {
            public void run() {
                view.removeAllViews();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
        case android.R.id.home:
            startActivity(chatsActivity);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public void ShowChat(final TdApi.Messages messages) {
        final LinearLayout
            chatShowLayout = (LinearLayout) findViewById(R.id.layout_chat_show);

        this.clearView(chatShowLayout);

        TdApi.PrivateChatInfo
            chatInfo = (TdApi.PrivateChatInfo) ChatActivity.instance.currentChat.type;

        TdApi.User user = chatInfo.user;

        TdApiResultHandler.getInstance().Send(new TdApi.GetUser(user.id));

        for (int i = messages.messages.length - 1; i >= 0; i--) {
            this.addViewToLayout(chatShowLayout, this.getChatMessageView(messages.messages[i]));

            if (i > 0) {
                if (!this.isSameDayMessages(messages.messages[i], messages.messages[i-1])) {
                    this.addViewToLayout(chatShowLayout, this.getChatDateView(messages.messages[i-1]));
                }
            }

            if (i == 0) {
                this.addViewToLayoutTop(
                        chatShowLayout, this.getChatDateView(messages.messages[messages.messages.length - 1])
                        );
            }
        }

        ChatActivity.instance.currentChatOldestMessageId = messages.messages[messages.messages.length-1].id;

        this.SetActionBarTitle(user.firstName + " " + user.lastName);
        this.setHomeButtonEnabled(true);
        this.setDisplayHomeAsUpEnabled(true);

        this.scrollChatToBottom();

        final InteractiveScrollView
            chatScrollView = (InteractiveScrollView) findViewById(R.id.chat_scroll_view);

        chatScrollView.setOnTopReachedListener(new ChatScrollOnTopListener(
                    messages.messages[0].chatId
                    ));

        EditText
            messageInput = (EditText) findViewById(R.id.input_message);

        Button
            sendMessageButton = (Button) findViewById(R.id.button_send_message);

        messageInput.addTextChangedListener(new ChatMessageInputTextWatcher(sendMessageButton));

        sendMessageButton.setOnClickListener(new ChatMessageSendButtonOnClickListener(messageInput));

    }

    public void UpdateChat(TdApi.Messages messages) {
        final LinearLayout
            chatShowLayout = (LinearLayout) findViewById(R.id.layout_chat_show);

        for (int i = 0; i < messages.messages.length; i++) {
            View messageView = this.getChatMessageView(messages.messages[i]);

            this.addViewToLayoutTop(chatShowLayout, messageView);

            if (i < messages.messages.length - 1) {
                if (!this.isSameDayMessages(messages.messages[i], messages.messages[i+1])) {
                    this.addViewToLayoutTop(chatShowLayout, this.getChatDateView(messages.messages[i]));
                }
            }
        }

        ChatActivity.instance.currentChatOldestMessageId = messages.messages[messages.messages.length-1].id;
    }

    private View getChatMessageView(TdApi.Message message) {
        View
            chatMessageView = (View) View.inflate(
                    ChatsActivity.instance, R.layout.chat_message, null
                    );

        TextView
            chatMessageUsername = (TextView) chatMessageView.findViewById(R.id.message_username);

        TdApi.PrivateChatInfo
            chatInfo = (TdApi.PrivateChatInfo) ChatActivity.instance.currentChat.type;

        TdApi.User user = chatInfo.user;

        TextView
            chatMessageUserInitials = (TextView)
            chatMessageView.findViewById(R.id.user_initials);

        GradientDrawable
            userInitialsShape = (GradientDrawable) chatMessageUserInitials.getBackground();

        if (message.fromId == user.id) {
            chatMessageUsername.setText(user.firstName + " " + user.lastName);

            chatMessageUserInitials.setText(
                    String.valueOf(user.firstName.charAt(0)) +
                    String.valueOf(user.lastName.charAt(0)));

            userInitialsShape.setColor(this.getUserColor(user));
        }

        if (message.fromId == MainActivity.instance.currentUser.id) {
            chatMessageUsername.setText(
                    MainActivity.instance.currentUser.firstName + " " +
                    MainActivity.instance.currentUser.lastName
                    );

            chatMessageUserInitials.setText(
                    String.valueOf(MainActivity.instance.currentUser.firstName.charAt(0)) +
                    String.valueOf(MainActivity.instance.currentUser.lastName.charAt(0)));

            userInitialsShape.setColor(this.getUserColor(MainActivity.instance.currentUser));
        }

        TextView
            chatMessageTime = (TextView) chatMessageView.findViewById(R.id.message_time);

        Date messageTime = new Date((long) message.date * 1000);

        chatMessageTime.setText(
                String.format("%d:%02d", messageTime.getHours(), messageTime.getMinutes())
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

    public void ShowMessage(TdApi.Message message) {
        if (ChatActivity.instance == null) {
            return ;
        }

        if (ChatActivity.instance.currentChat == null) {
            startActivity(chatsActivity);
            return ;
        }

        if (message.chatId != ChatActivity.instance.currentChat.id) {
            return ;
        }

        final LinearLayout
            chatShowLayout = (LinearLayout) findViewById(R.id.layout_chat_show);

        this.addViewToLayout(chatShowLayout, this.getChatMessageView(message));

        this.scrollChatToBottom();
    }

    private void addViewToLayout(final ViewGroup rootLayout, final View view) {
         runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rootLayout.addView(view);
            }
        });
    }

    private void addViewToLayoutTop(final ViewGroup rootLayout, final View view) {
         runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rootLayout.addView(view, 0);
            }
        });
    }

    private int getUserColor(TdApi.User user) {
        Integer index = (
                user.firstName.charAt(0) * (
                    user.lastName.charAt(0)+user.firstName.charAt(0))) % 7;

        Integer resourceId = R.color.tx_chat_list_entry_circle_background_0;

        switch (index) {
            case 0:
                resourceId = R.color.tx_chat_list_entry_circle_background_0;
                break;
            case 1:
                resourceId = R.color.tx_chat_list_entry_circle_background_1;
                break;
            case 2:
                resourceId = R.color.tx_chat_list_entry_circle_background_2;
                break;
            case 3:
                resourceId = R.color.tx_chat_list_entry_circle_background_3;
                break;
            case 4:
                resourceId = R.color.tx_chat_list_entry_circle_background_4;
                break;
            case 5:
                resourceId = R.color.tx_chat_list_entry_circle_background_5;
                break;
            case 6:
                resourceId = R.color.tx_chat_list_entry_circle_background_6;
                break;
        }

        return getResources().getColor(resourceId);
    }

    private void scrollChatToBottom() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final InteractiveScrollView
                    chatScrollView = (InteractiveScrollView) findViewById(R.id.chat_scroll_view);

                chatScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        chatScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });
    }

    private boolean isSameDayMessages(TdApi.Message first, TdApi.Message second) {
        if (first.date / (24*60*60) == second.date / (24*60*60)) {
            return true;
        }

        return false;
    }

    private View getChatDateView(TdApi.Message message) {
        View
            chatDateView = (View) View.inflate(
                    ChatsActivity.instance, R.layout.chat_day, null
                    );

        TextView
            chatMessageDate = (TextView) chatDateView.findViewById(R.id.message_date);

        Calendar messageDate = Calendar.getInstance();

        messageDate.setTime(new Date((long) message.date * 1000));

        String month = messageDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());

        chatMessageDate.setText(
                String.format("%d %s", messageDate.get(Calendar.DAY_OF_MONTH), month)
                );

        return chatDateView;
    }

    public void SetActionBarTitle(final String title) {
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

    class ChatScrollOnTopListener implements InteractiveScrollView.OnTopReachedListener {
        private long chatId;

        ChatScrollOnTopListener(long chatId) {
            this.chatId = chatId;
        }

        @Override
        public void onTopReached() {
            TdApiResultHandler.getInstance().Send(
                    new TdApi.GetChatHistory(
                        chatId,
                        ChatActivity.instance.currentChatOldestMessageId, 0,
                        ChatActivity.chatUpdateMessageLimit
                        )
                    );
        }
    };

    class ChatMessageInputTextWatcher implements TextWatcher {

        private Button sendButton;

        ChatMessageInputTextWatcher(Button sendButton) {
            this.sendButton = sendButton;
        }

        public void afterTextChanged(Editable s) {
            if (s.toString().length() > 0) {
                sendButton.setVisibility(View.VISIBLE);
            } else {
                sendButton.setVisibility(View.GONE);
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    };

    class ChatMessageSendButtonOnClickListener implements View.OnClickListener {

        private EditText messageInput;

        ChatMessageSendButtonOnClickListener(EditText messageInput) {
            this.messageInput = messageInput;
        }

        public void onClick(View v) {
            TdApi.InputMessageText message = new TdApi.InputMessageText();

            message.text = messageInput.getText().toString();

            messageInput.setText("");

            TdApiResultHandler.getInstance().Send(
                    new TdApi.SendMessage(
                        ChatActivity.instance.currentChat.id, message
                        )
                    );
        }
    }
}
