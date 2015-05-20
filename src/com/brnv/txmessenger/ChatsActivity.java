package com.brnv.txmessenger;

import android.app.Activity;
import android.os.Bundle;

import org.drinkless.td.libcore.telegram.*;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.MenuItem;
import android.widget.ViewFlipper;

import android.graphics.Rect;

import android.view.MotionEvent;

import java.util.Date;

import android.graphics.drawable.GradientDrawable;

import android.content.Intent;

import android.util.Log;

public class ChatsActivity extends Activity {

    public static ChatsActivity instance;

    public ViewFlipper viewFlipper;

    public Intent chatActivity;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chats);

        instance = this;

        chatActivity = new Intent(this, ChatActivity.class);

        this.viewFlipper = (ViewFlipper) findViewById(R.id.chat_activity_views);

        this.processChatsList();
    }

    public void onResume() {
        super.onResume();
        this.processChatsList();
    }

    private void processChatsList() {
        TdApiResultHandler.getInstance().Send(new TdApi.GetChats(0, 3));
    }

    //private void flipLayout(final Integer id) {
    //    runOnUiThread(new Runnable() {
    //        public void run() {
    //            ChatsActivity.instance.viewFlipper.setDisplayedChild(id);
    //        }
    //    });
    //}

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

    private void addViewToLayout(final ViewGroup rootLayout, final View view) {
         runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rootLayout.addView(view);
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

        ChatActivity.instance.currentChat = null;
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
                    chatActivity.putExtra("chat_id", chat.id);
                    startActivity(ChatsActivity.instance.chatActivity);
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

        chatsEntryUserInitials.setText(
                String.valueOf(user.firstName.charAt(0)) +
                String.valueOf(user.lastName.charAt(0)));

        GradientDrawable
            userInitialsShape = (GradientDrawable) chatsEntryUserInitials.getBackground();

        userInitialsShape.setColor(this.getUserColor(user));

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
}
