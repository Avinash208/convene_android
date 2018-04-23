package org.mahiti.convenemis.network;

import android.app.IntentService;
import android.content.Intent;

public class LangService extends IntentService {

    public LangService() {
        super("MyLangService");
    }

    @Override
    protected void onHandleIntent(Intent arg0) {

        Intent in= new Intent();
        in.setAction("LangService");
        sendBroadcast(in);


    }
}
