package org.yale.convene;

import android.app.IntentService;
import android.content.Intent;

public class MyIntentLinkage extends IntentService {

	public MyIntentLinkage() {
        super("MyIntentService");
    }
 
    @Override
    protected void onHandleIntent(Intent arg0) {
        Intent in= new Intent();
        in.setAction("Linkage");
        sendBroadcast(in);
      
     
    }

}
