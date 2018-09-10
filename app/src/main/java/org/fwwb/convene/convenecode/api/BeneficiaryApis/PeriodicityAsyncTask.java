package org.fwwb.convene.convenecode.api.BeneficiaryApis;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.fwwb.convene.convenecode.database.ExternalDbOpenHelper;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.DirectUrlCall;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.convenecode.utils.RestUrl;
import org.fwwb.convene.convenecode.utils.Utils;

import java.util.ArrayList;
import java.util.List;



public class PeriodicityAsyncTask extends AsyncTask<Context, Integer, String> {
    Context context;
    DirectUrlCall resturl;
    Activity activity;
    private RestUrl peridiocityRestUrl;
    SharedPreferences preferences;
    private PeriodicTypeInterface facilityTypeInterface;
    private ExternalDbOpenHelper dbOpenHelper;
    private String globalModifiedDate ="";
    private static final String TABLE_NAME="Periodicity";
    private static final String SERVER_DATETIME="server_datetime";

    /**
     * PeriodicityAsyncTask method
     * @param context param
     * @param activity param
     * @param typeInterface param
     */
    public PeriodicityAsyncTask(Context context, Activity activity, PeriodicTypeInterface typeInterface) {
        this.context = context;
        resturl = new DirectUrlCall(context);
        this.activity = activity;
        peridiocityRestUrl=new RestUrl(context);
        this.facilityTypeInterface=typeInterface;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }


    @Override
    protected String doInBackground(Context... params) {
        if (!Utils.haveNetworkConnection(context)) {
            return "";

        }
        List<NameValuePair> paramsL = new ArrayList<>();
        dbOpenHelper=new ExternalDbOpenHelper(context, preferences.getString(Constants.DBNAME,""),preferences.getString("uId",""));
        globalModifiedDate = dbOpenHelper.getLastUpDate(TABLE_NAME,SERVER_DATETIME,dbOpenHelper);
        paramsL.add(new BasicNameValuePair("userid", preferences.getString("uId","")));
        paramsL.add(new BasicNameValuePair("serverdatetime", globalModifiedDate));
        String result = resturl.restUrlServerCall("api/responses-list/", "post", paramsL);
        Logger.logV("the responses-list/", "the responses-list/i" + paramsL);
        parseResponse(result);
        return result;

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try{
            if((result).contains(Constants.HTMLSTRING)){
                facilityTypeInterface.onSuccessPeriodicResponse(result,false);
            }else{
                facilityTypeInterface.onSuccessPeriodicResponse(result,true);
            }
        }catch (Exception e){
           Logger.logE("","",e);
            facilityTypeInterface.onSuccessPeriodicResponse(result,false);
        }


    }

    /**
     * parseResponse method
     * @param result param
     */
    private void parseResponse(String result) {
     try {
         JSONObject jsonObject = new JSONObject(result);
         Logger.logD("BeneficiaryAsyncTask ResponseMessage->","Periodicity response........."+result);
         if(jsonObject.getInt("status")==2 && jsonObject.has("ResponsesData")){
             dbOpenHelper=new ExternalDbOpenHelper(context, preferences.getString(Constants.DBNAME,""),preferences.getString("uId",""));
             dbOpenHelper.updatePeriodicityTable(result);
             String tempModifyDate = dbOpenHelper.getLastUpDate(TABLE_NAME,SERVER_DATETIME,dbOpenHelper);
             if(!globalModifiedDate.equalsIgnoreCase(tempModifyDate)) {
                 Logger.logV("", "globalModifiedDate" + globalModifiedDate);
                 globalModifiedDate = tempModifyDate;
                 List<NameValuePair> paramsL = new ArrayList<>();
                 paramsL.add(new BasicNameValuePair("userid", preferences.getString("uId", "")));
                 paramsL.add(new BasicNameValuePair("serverdatetime", tempModifyDate));
                 String secondResult = resturl.restUrlServerCall("api/responses-list/", "post", paramsL);
                 Logger.logV("the parameters are", "the Periodicity params" + paramsL);
                 parseResponse(secondResult);
             }else{
                 Logger.logD("PeriodicityAsyncTask","Exception on calling Periodiciity Api");
                 peridiocityRestUrl.writeToTextFile("Conflict on Periodicity Date",dbOpenHelper.getLastUpDate(TABLE_NAME, SERVER_DATETIME, dbOpenHelper),"GetPeriodicity");
             }
         }


     }catch (Exception e){
         Logger.logE("","", e);
     }
    }
}

