package org.mahiti.convenemis.api.BeneficiaryApis;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.ProgressBar;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.mahiti.convenemis.UpdateMasterLoading;
import org.mahiti.convenemis.database.ConveneDatabaseHelper;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.DirectUrlCall;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aviansh Raj  on 10/08/17.
 */
public class UpdateRegionalLanguge extends AsyncTask<Context, Integer, String> {

    private DirectUrlCall resturl;
    private Context context;
    Activity activity;
    String result = "";
    private String apiURL;
    private RegionalLanguageInterface regionalLanguageInterface;
    private SharedPreferences preferences;
    ConveneDatabaseHelper dbhelper;
    private static final String TAG="UpdateRegionalLanguge";
    ProgressBar progressBar;

    /**
     * UpdateRegionalLanguge constructor
     * @param loginParentActivity param
     * @param regionalLanguageURL param
     * @param loginParentActivity1 param
     * @param progressBar param
     */
    public UpdateRegionalLanguge(Activity loginParentActivity, String regionalLanguageURL, UpdateMasterLoading loginParentActivity1, ProgressBar progressBar) {
        this.context = loginParentActivity;
        resturl = new DirectUrlCall(context);
        this.activity = loginParentActivity;
        apiURL=regionalLanguageURL;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.regionalLanguageInterface=loginParentActivity1;
        this.dbhelper=ConveneDatabaseHelper.getInstance(context, preferences.getString(Constants.CONVENE_DB, ""), preferences.getString("UID", ""));
        this.progressBar= progressBar;


    }
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setProgress(5);
    }
    @Override
    protected String doInBackground(Context... contexts) {
        try {
            dbhelper.deleteLanguageALl();
            String regionalLanguage = dbhelper.getregionallLanguageUpdates("language","updated_time");
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
            List<NameValuePair> paramsL = new ArrayList<>();
            paramsL.add(new BasicNameValuePair("uid", preferences.getString("uId", "")));
            paramsL.add(new BasicNameValuePair("updatedtime", regionalLanguage));
            result = resturl.restUrlServerCall("api/language-list/", "post", paramsL);
            publishProgress();
            Logger.logV(TAG, "the params of regional " + paramsL);
            parseResponse(result);
        } catch (Exception e) {
            Logger.logE(TAG,"Exception on do in background Regional language listing",e);
        }
        return result;
    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressBar.setProgress(70);
    }

    /**
     * parseResponse method
     * @param result param
     */
    public void parseResponse(String result) {
       Logger.logD("Regionallanguage","the Regional language result from Server"+result);
        JSONObject jsonObject= null;
        try {
            jsonObject = new JSONObject(result);
            int status=jsonObject.getInt("status");
            if(status==2){
                dbhelper.UpdateRegionalLanguageTodatabase(result);
            }else{
                ToastUtils.displayToast("Regional language Response Invalid",context);
            }
        } catch (JSONException e) {
           Logger.logE(TAG,"in",e);
        }
    }
    @Override
    protected void onPostExecute(String meetingListResponse) {
        super.onPostExecute(meetingListResponse);
         Logger.logD(TAG,"the Regional language result in Post "+meetingListResponse);
         try{
             if((meetingListResponse).contains(Constants.HTMLSTRING)){
                 regionalLanguageInterface.onSuccessfullRegionalLanguage(meetingListResponse,false);
             }else{
                 JSONObject jsonObject= null;
                 try {
                     jsonObject = new JSONObject(meetingListResponse);
                     int status=jsonObject.getInt("status");
                     if(status==2){
                         regionalLanguageInterface.onSuccessfullRegionalLanguage(meetingListResponse,true);
                     }else{
                         ToastUtils.displayToast("API Response Invalid",context);
                         regionalLanguageInterface.onSuccessfullRegionalLanguage(meetingListResponse,true);
                     }
                 } catch (JSONException e) {
                     Logger.logE(TAG,"in",e);
                     ToastUtils.displayToast("Regional language fail to load",context);
                 }
             }
         }catch (Exception e){
             Logger.logE(TAG,"in",e);
             regionalLanguageInterface.onSuccessfullRegionalLanguage(meetingListResponse,false);
         }



    }
}
