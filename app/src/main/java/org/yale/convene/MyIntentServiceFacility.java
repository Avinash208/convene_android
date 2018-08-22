package org.yale.convene;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Aviansh Raj  on 08/08/17.
 */
public class MyIntentServiceFacility extends IntentService {
    public MyIntentServiceFacility() {
        super("MyIntentServiceFacility");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent in= new Intent();
        in.setAction("FacilityIntentReceiver");
        in.putExtra("facility","facilityType");
        sendBroadcast(in);
    }
}
