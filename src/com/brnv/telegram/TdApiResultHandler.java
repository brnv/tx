package com.brnv.telegram;

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

    public void onResult(TdApi.TLObject object) {
        //@TODO: errors checking?
        Log.v("!!!", object.getClass().getSimpleName());

        switch (object.getClass().getSimpleName()) {

        case "AuthStateOk":
            AuthorizationActivity.instance.StartChatActivity();
            break;

        case "AuthStateWaitSetPhoneNumber":
            AuthorizationActivity.instance.SetPhoneNumber();
            break;

        case "AuthStateWaitSetName":
            AuthorizationActivity.instance.SetName();
            break;

        case "AuthStateWaitSetCode":
            AuthorizationActivity.instance.SetCode();
            break;

        case "Contacts":
            ContactsActivity.instance.ListContacts((TdApi.Contacts) object);
            break;

        case "Error":
            Log.v("!!! error: ", object.toString());
            break;

        case "User":
            TdApi.User user = (TdApi.User) object;
            Log.v("!!! user: ", user.toString());
            break;

        case "Chat":
            //TdApi.Chat chat = (TdApi.Chat) object;
            //ChatActivity.instance.ShowChat(chat);
            break;

        case "Messages":
            TdApi.Messages messages = (TdApi.Messages) object;
            ChatActivity.instance.ShowChat(messages);
            break;

        case "Chats":
            TdApi.Chats chats = (TdApi.Chats) object;
            ChatActivity.instance.ListChats(chats);
            break;

        case "UpdateNewMessage":
            TdApi.UpdateNewMessage messageObject = (TdApi.UpdateNewMessage) object;
            TdApi.Message message = messageObject.message;
            ChatActivity.instance.ShowMessage(message);
            break;
        }
    }
}
