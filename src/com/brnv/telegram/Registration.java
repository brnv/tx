package com.brnv.telegram;

import org.drinkless.td.libcore.telegram.*;


public class Registration {

    private static Registration instance = null;

    public static Registration getInstance() {
        if (instance == null) {
            instance = new Registration();
        }

        return instance;
    }

    public void SubmitName(String firstName, String lastName) {
        TdApiResultHandler.getInstance().Send(
            new TdApi.AuthSetName(firstName, lastName)
        );
    }

    public void SubmitPhone(String number) {
        if (!this.isPhoneValid(number)) {
            return ;
        };

        TdApiResultHandler.getInstance().Send(
            new TdApi.AuthSetPhoneNumber(number)
        );
    }

    private boolean isPhoneValid(String number) {
        //@TODO: implement later
        return true;
    }

    public void SubmitCode(String code) {
        TdApiResultHandler.getInstance().Send(
            new TdApi.AuthSetCode(code)
        );
    }
}
