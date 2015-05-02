package com.brnv.telegram;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import android.content.Context;
import java.io.File;

import org.drinkless.td.libcore.telegram.*;
import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi.TLFunction;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        MyHandler handler = new MyHandler();

        TG.setUpdatesHandler(handler);

        Context context = this.getApplicationContext();
        File cacheDir = context.getCacheDir();

        TG.setDir(cacheDir.toString());

        Client client = TG.getClientInstance();

        TLFunction setPhoneNumber = new TdApi.AuthSetPhoneNumber("+79231710953");
        client.send(setPhoneNumber, handler);
    }

    public class MyHandler implements Client.ResultHandler {
        public void onResult(TdApi.TLObject object) {
            Log.v("!!!", object.getClass().toString());
            Log.v("!!!", object.toString());
        }
    }
}
