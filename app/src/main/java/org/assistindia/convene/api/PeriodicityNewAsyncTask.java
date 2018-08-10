package org.assistindia.convene.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.assistindia.convene.MyIntentService;
import org.assistindia.convene.database.ExternalDbOpenHelper;
import org.assistindia.convene.utils.Constants;
import org.assistindia.convene.utils.DirectUrlCall;
import org.assistindia.convene.utils.Logger;
import org.assistindia.convene.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class PeriodicityNewAsyncTask extends AsyncTask<Context, Integer, String> {
    private Context context;
    private DirectUrlCall periodicityResturl;
    private SharedPreferences sharedPreferences;
    private ExternalDbOpenHelper externalDbOpenHelper;
    private String modifiedDate="";
    private static final String TABLE_NAME="Periodicity";
    private static final String SERVER_DATETIME="server_datetime";

    public PeriodicityNewAsyncTask(Context context) {
        this.context = context;
        periodicityResturl = new DirectUrlCall(context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }


    @Override
    protected String doInBackground(Context... params) {
        if (!Utils.haveNetworkConnection(context)) {
            return "";
        }
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        externalDbOpenHelper =new ExternalDbOpenHelper(context, sharedPreferences.getString(Constants.DBNAME,""), sharedPreferences.getString("uId",""));
        modifiedDate = externalDbOpenHelper.getLastUpDate(TABLE_NAME,SERVER_DATETIME, externalDbOpenHelper);
        nameValuePairs.add(new BasicNameValuePair("userid", sharedPreferences.getString("uId","")));
        nameValuePairs.add(new BasicNameValuePair("serverdatetime", modifiedDate));
        String periodicityResult = periodicityResturl.restUrlServerCall("api/responses-list/", "post", nameValuePairs);
        Logger.logV("the responses-list/", "the responses-list/i" + nameValuePairs);
        parseResponse(periodicityResult);
        return periodicityResult;

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            JSONObject periodicityJsonObject=new JSONObject(result);
            int status=periodicityJsonObject.getInt("status");
            if(status==2){
                Intent intent = new Intent(context, MyIntentService.class);
                context.startService(intent);
            }
        }catch (Exception e){
            Logger.logE("","",e);
        }

    }

    private void parseResponse(String result) {
     try {
         JSONObject jsonObject = new JSONObject(result);
         Logger.logD("BeneficiaryAsyncTask ResponseMessage->","Periodicity response........."+result);
         int status = jsonObject.getInt("status");
         if(status==2 && jsonObject.has("ResponsesData")){
                 externalDbOpenHelper =new ExternalDbOpenHelper(context, sharedPreferences.getString(Constants.DBNAME,""), sharedPreferences.getString("uId",""));
                 externalDbOpenHelper.updatePeriodicityTable(result);
                 if(!modifiedDate.equalsIgnoreCase(externalDbOpenHelper.getLastUpDate(TABLE_NAME,SERVER_DATETIME, externalDbOpenHelper))) {
                         List<NameValuePair> paramsL = new ArrayList<>();
                         paramsL.add(new BasicNameValuePair("userid", sharedPreferences.getString("uId", "")));
                         paramsL.add(new BasicNameValuePair("serverdatetime", externalDbOpenHelper.getLastUpDate(TABLE_NAME, SERVER_DATETIME, externalDbOpenHelper)));
                         String secondResult = periodicityResturl.restUrlServerCall("api/responses-list/", "post", paramsL);
                         Logger.logV("the parameters are", "the Periodicity params" + paramsL);
                         parseResponse(secondResult);

             }
         }
     }catch (Exception e){
         Logger.logE("","", e);
     }
    }
}

