package org.mahiti.convenemis;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Aviansh Raj  on 08/08/17.
 */
public class MyIntentPeriodicity extends IntentService {
    public MyIntentPeriodicity() {
        super("MyIntentPeriodicity");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent in= new Intent();
        in.setAction("PeriodicIntentReceiver");
        sendBroadcast(in);
    }
}
