package com.brnv.txmessenger;

import org.drinkless.td.libcore.telegram.*;

import android.util.Log;

public class Users {

    static public void SetUserOffline(int userId) {
        if (ChatActivity.instance.currentChat == null) {
            return ;
        }

        TdApi.PrivateChatInfo
            chatInfo = (TdApi.PrivateChatInfo) ChatActivity.instance.currentChat.type;

        TdApi.User user = chatInfo.user;
        if (user.id == userId) {
            ChatsActivity.instance.SetActionBarTitle(user.firstName + " " + user.lastName + " (offline)");
        }
    }

    static public void SetUserOnline(int userId) {
        if (ChatActivity.instance.currentChat == null) {
            return ;
        }

        TdApi.PrivateChatInfo
            chatInfo = (TdApi.PrivateChatInfo) ChatActivity.instance.currentChat.type;

        TdApi.User user = chatInfo.user;
        if (user.id == userId) {
            ChatsActivity.instance.SetActionBarTitle(user.firstName + " " + user.lastName + " (online)");
        }
    }

    static public void ProcessUserStatus(int userId, TdApi.UserStatus status) {
        Log.v("!!!", status.getClass().getSimpleName());

        switch (status.getClass().getSimpleName()) {
            case "UserStatusOnline":
                SetUserOnline(userId);
                break;

            case "UserStatusOffline":
                SetUserOffline(userId);
                break;
        }
    }
}
