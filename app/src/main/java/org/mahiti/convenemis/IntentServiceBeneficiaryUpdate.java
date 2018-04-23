package org.mahiti.convenemis;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Aviansh Raj  on 08/08/17.
 */
public class IntentServiceBeneficiaryUpdate extends IntentService {
    public IntentServiceBeneficiaryUpdate() {
        super("IntentServiceBeneficiaryUpdate");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent in= new Intent();
        in.setAction("BeneficiaryUpdateIntentReceiver");
        in.putExtra("beneficiary","beneficiaryType");
        sendBroadcast(in);
    }
}
