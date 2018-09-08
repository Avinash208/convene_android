package org.fwwb.convene.convenecode;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Aviansh Raj  on 08/08/17.
 */
public class MyIntentServiceBeneficiary extends IntentService {
    public MyIntentServiceBeneficiary() {
        super("MyIntentServiceBeneficiary");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent in= new Intent();
        in.setAction("BeneficiaryIntentReceiver");
        in.putExtra("beneficiary","beneficiaryType");
        sendBroadcast(in);
    }
}
