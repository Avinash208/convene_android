package org.fwwb.convene.convenecode;

import android.app.IntentService;
import android.content.Intent;

import org.fwwb.convene.convenecode.utils.Logger;

public class MyIntentService extends IntentService {
	
	public MyIntentService() {
        super("MyIntentService");
    }
 
    @Override
    protected void onHandleIntent(Intent arg0) {
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            Logger.logE(MyIntentService.class.getSimpleName(), "Exception in onHandleIntent method", e);
        }
         
        Intent in= new Intent();
        in.setAction("Survey");
        sendBroadcast(in);
      
     
    }

}
