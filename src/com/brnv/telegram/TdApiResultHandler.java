package com.brnv.telegram;

import org.drinkless.td.libcore.telegram.*;

import android.util.Log;

public class TdApiResultHandler implements Client.ResultHandler {

    private static TdApiResultHandler mInstance = null;

    public static TdApiResultHandler getInstance(){
        if(mInstance == null) {
            mInstance = new TdApiResultHandler();
        }

        return mInstance;
    }

    public void onResult(TdApi.TLObject object) {
        //@TODO: errors checking?
        Log.v("!!!", object.getClass().getSimpleName());

        switch (object.getClass().getSimpleName()) {

        case "AuthStateWaitSetPhoneNumber":
            AuthorizationActivity.instance.SetPhoneNumber();
            break;

        case "AuthStateWaitSetName":
            AuthorizationActivity.instance.SetName();
            break;

        case "AuthStateWaitSetCode":
            AuthorizationActivity.instance.SetCode();
            break;
        }
    }
}
