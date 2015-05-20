package com.brnv.txmessenger;

import org.drinkless.td.libcore.telegram.*;
import org.drinkless.td.libcore.telegram.TdApi.TLFunction;

import android.util.Log;

public class TdApiResultHandler implements Client.ResultHandler {

    private static TdApiResultHandler mInstance = null;

    public static TdApiResultHandler getInstance(){
        if(mInstance == null) {
            mInstance = new TdApiResultHandler();
        }

        return mInstance;
    }

    public void Send(TLFunction function) {
        TG.getClientInstance().send(
            function, this
        );
    }

    public void onResult(TdApi.TLObject result) {
        Log.v("!!! TLObject class: ", result.getClass().getSimpleName());

        switch (result.getClass().getSimpleName()) {

        case "AuthStateOk":
            TdApiResultHandler.getInstance().Send(new TdApi.GetMe());
            break;

        case "AuthStateWaitSetPhoneNumber":
            MainActivity.instance.Register("PHONE");
            break;

        case "AuthStateWaitSetCode":
            MainActivity.instance.Register("CODE");
            break;

        case "AuthStateWaitSetName":
            MainActivity.instance.Register("NAME");
            break;

        case "Error":
            Log.v("!!! error: ", result.toString());
            break;

        case "Chats":
            TdApi.Chats chats = (TdApi.Chats) result;
            ChatsActivity.instance.ShowChats(chats);
            break;

        case "Chat":
            TdApi.Chat chat = (TdApi.Chat) result;
            ChatActivity.instance.currentChat = chat;
            ChatActivity.instance.chatUpdateMode = false;

            TdApiResultHandler.getInstance().Send(
                    new TdApi.GetChatHistory(
                        chat.id, chat.topMessage.id, -1,
                        ChatActivity.chatShowMessagesLimit)
                    );
            break;

        case "Messages":
            TdApi.Messages messages = (TdApi.Messages) result;

            if (!ChatActivity.instance.chatUpdateMode) {
                ChatActivity.instance.ShowChat(messages);
                ChatActivity.instance.chatUpdateMode = true;
            } else {
                ChatActivity.instance.UpdateChat(messages);
            }

            break;

        case "User":
            TdApi.User user = (TdApi.User) result;
            if (MainActivity.instance.currentUser == null) {
                MainActivity.instance.currentUser = user;
                MainActivity.instance.StartMessenger();
            } else {
                Users.ProcessUserStatus(user.id, user.status);
            }
            break;

        //case "Contacts":
        //    ContactsActivity.instance.ListContacts((TdApi.Contacts) result);
        //    break;

        case "Message":
            ChatActivity.instance.ShowMessage((TdApi.Message) result);
            break;

        case "UpdateNewMessage":
            TdApi.UpdateNewMessage messageObject = (TdApi.UpdateNewMessage) result;
            ChatActivity.instance.ShowMessage(messageObject.message);
            break;

        case "UpdateUserStatus":
            TdApi.UpdateUserStatus updateUserStatus = (TdApi.UpdateUserStatus) result;
            Users.ProcessUserStatus(updateUserStatus.userId, updateUserStatus.status);
            break;

        }
    }
}
