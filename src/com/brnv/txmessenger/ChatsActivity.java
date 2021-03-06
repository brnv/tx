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
import android.view.Menu;
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

import android.text.TextWatcher;
import android.text.Editable;

import android.graphics.drawable.GradientDrawable;

public class ChatsActivity extends Activity {

    public static ChatsActivity instance;

    public ViewFlipper viewFlipper;

    static public int chatShowMessagesLimit = 20;
    static public int chatUpdateMessageLimit = 5;

    public TdApi.Chat currentChat;
    public int currentChatOldestMessageId;

    public boolean chatUpdateMode = false;

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
            case R.id.clear_history:
                this.clearChatHistory(ChatsActivity.instance.currentChat.id);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);

        return true;
    }

    private void processChatsList() {
        TdApiResultHandler.getInstance().Send(new TdApi.GetChats(0, 10));
    }

    private void clearChatHistory(long chatId) {
        TdApiResultHandler.getInstance().Send(new TdApi.DeleteChatHistory(chatId));

        final LinearLayout
            chatShowLayout = (LinearLayout) findViewById(R.id.layout_chat_show);

        this.clearView(chatShowLayout);
    }

    private void flipLayout(final Integer id) {
        runOnUiThread(new Runnable() {
            public void run() {
                ChatsActivity.instance.viewFlipper.setDisplayedChild(id);
            }
        });
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

    private void clearView(final ViewGroup view) {
        runOnUiThread(new Runnable() {
            public void run() {
                view.removeAllViews();
            }
        });
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

    public void ShowChats(TdApi.Chats chats) {
        final LinearLayout
            chatsListLayout = (LinearLayout) findViewById(R.id.layout_chats_list);

        this.clearView(chatsListLayout);

        for (int i = 0; i < chats.chats.length; i++) {
            this.addViewToLayout(chatsListLayout, this.getChatsEntryView(chats.chats[i]));
        }

        this.SetActionBarTitle("Messages");
        this.setHomeButtonEnabled(false);
        this.setDisplayHomeAsUpEnabled(false);

        ChatsActivity.instance.currentChat = null;

        this.flipLayout(0);
        this.hideChatMenu();
    }

    class ChatsEntryTouchListener implements View.OnTouchListener {
        private TdApi.Chat chat;

        ChatsEntryTouchListener(TdApi.Chat chat) {
            this.chat = chat;
        }

        private Rect rect;

        private boolean touchIsOverView;

        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                touchIsOverView = true;
                rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
            }

            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (touchIsOverView &&
                        !rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())
                   ) {
                    touchIsOverView = false;
                   }
            }

            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                if (touchIsOverView) {
                    TdApiResultHandler.getInstance().Send(new TdApi.GetChat(chat.id));
                }
            }

            return true;
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
            chatsEntryUserInitials = (TextView)
            chatsEntryView.findViewById(R.id.user_initials);

        if (user.lastName.length() != 0) {
            chatsEntryUserInitials.setText(
                    String.valueOf(user.firstName.charAt(0)) +
                    String.valueOf(user.lastName.charAt(0)));
        } else {
            chatsEntryUserInitials.setText(
                    String.valueOf(user.firstName.charAt(0)));
        }

        GradientDrawable
            userInitialsShape = (GradientDrawable) chatsEntryUserInitials.getBackground();

        userInitialsShape.setColor(this.getUserColor(user));

        if (chat.topMessage.id != 0) {
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
        }

        chatsEntryView.setOnTouchListener(new ChatsEntryTouchListener(chat));

        if (chat.unreadCount != 0) {
            TextView
                chatsEntryUnreadCount = (TextView)
                chatsEntryView.findViewById(R.id.unread_count);

            GradientDrawable
                unreadCountShape = (GradientDrawable) chatsEntryUnreadCount.getBackground();

            unreadCountShape.setColor(this.getUserColor(user));
            chatsEntryUnreadCount.setText(Integer.toString(chat.unreadCount));
            chatsEntryUnreadCount.setVisibility(View.VISIBLE);
        }

        return chatsEntryView;
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
                        ChatsActivity.instance.currentChatOldestMessageId, 0,
                        ChatsActivity.chatUpdateMessageLimit
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
                        ChatsActivity.instance.currentChat.id, message
                        )
                    );
        }
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

    private void hideChatMenu() {
         runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (findViewById(R.id.chat_menu) != null) {
                    findViewById(R.id.chat_menu).setVisibility(View.GONE);
                }
            }
        });
    }

    private void showChatMenu() {
         runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (findViewById(R.id.chat_menu) != null) {
                    findViewById(R.id.chat_menu).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void ShowChat(final TdApi.Messages messages) {
        final LinearLayout
            chatShowLayout = (LinearLayout) findViewById(R.id.layout_chat_show);

        this.clearView(chatShowLayout);

        TdApi.PrivateChatInfo
            chatInfo = (TdApi.PrivateChatInfo) ChatsActivity.instance.currentChat.type;

        TdApi.User user = chatInfo.user;

        TdApiResultHandler.getInstance().Send(new TdApi.GetUser(user.id));

        if (messages.messages.length > 0) {
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

            ChatsActivity.instance.currentChatOldestMessageId = messages.messages[messages.messages.length-1].id;

            this.scrollChatToBottom();

            final InteractiveScrollView
                chatScrollView = (InteractiveScrollView) findViewById(R.id.chat_scroll_view);

            chatScrollView.setOnTopReachedListener(new ChatScrollOnTopListener(
                        messages.messages[0].chatId
                        ));
        }

        this.SetActionBarTitle(user.firstName + " " + user.lastName);
        this.setHomeButtonEnabled(true);
        this.setDisplayHomeAsUpEnabled(true);

        EditText
            messageInput = (EditText) findViewById(R.id.input_message);

        Button
            sendMessageButton = (Button) findViewById(R.id.button_send_message);

        messageInput.addTextChangedListener(new ChatMessageInputTextWatcher(sendMessageButton));

        sendMessageButton.setOnClickListener(new ChatMessageSendButtonOnClickListener(messageInput));

        this.flipLayout(1);
        this.showChatMenu();
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

        ChatsActivity.instance.currentChatOldestMessageId = messages.messages[messages.messages.length-1].id;
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
        if (ChatsActivity.instance.currentChat == null) {
            this.processChatsList();
            return ;
        }

        if (message.chatId != ChatsActivity.instance.currentChat.id) {
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
        Integer index = 0;

        if (user.lastName.length() != 0) {
            index = (
                    user.firstName.charAt(0) * (
                        user.lastName.charAt(0)+user.firstName.charAt(0))) % 7;
        } else {
            index = user.firstName.charAt(0) % 7;
        }

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

}
