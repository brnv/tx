package com.brnv.telegram;

import org.drinkless.td.libcore.telegram.*;
import org.drinkless.td.libcore.telegram.TdApi.TLFunction;


public class Registration {

    private static Registration mInstance = null;

    public static Registration getInstance() {
        if(mInstance == null) {
            mInstance = new Registration();
        }

        return mInstance;
    }

    public boolean SubmitPhone(String number) {
        if (!this.isPhoneValid(number)) {
            return false;
        };

        TLFunction setPhone = new TdApi.AuthSetPhoneNumber(number);

        TG.getClientInstance().send(
            setPhone, TdApiResultHandler.getInstance()
        );

        return true;
    }

    private boolean isPhoneValid(String number) {
        //@TODO: implement later
        return true;
    }
}
