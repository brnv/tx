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
            ChatsActivity.instance.currentChat = chat;
            ChatsActivity.instance.chatUpdateMode = false;

            TdApiResultHandler.getInstance().Send(
                    new TdApi.GetChatHistory(
                        chat.id, chat.topMessage.id, -1,
                        ChatsActivity.chatShowMessagesLimit)
                    );
            break;


        case "Messages":
            TdApi.Messages messages = (TdApi.Messages) result;

            if (!ChatsActivity.instance.chatUpdateMode) {
                ChatsActivity.instance.ShowChat(messages);
                ChatsActivity.instance.chatUpdateMode = true;
            } else {
                ChatsActivity.instance.UpdateChat(messages);
            }

            break;

        case "User":
            TdApi.User user = (TdApi.User) result;
            MainActivity.instance.currentUser = user;
            MainActivity.instance.StartMessenger();
            break;

        //case "Contacts":
        //    ContactsActivity.instance.ListContacts((TdApi.Contacts) result);
        //    break;

        case "UpdateNewMessage":
            TdApi.UpdateNewMessage messageObject = (TdApi.UpdateNewMessage) result;
            TdApi.Message message = messageObject.message;

            ChatsActivity.instance.ShowMessage(message);

            break;
        }
    }
}
