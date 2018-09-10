package org.fwwb.convene.convenecode.api.BeneficiaryApis;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.fwwb.convene.convenecode.BeenClass.beneficiary.GetBeneficiaryDetails;
import org.fwwb.convene.convenecode.database.ExternalDbOpenHelper;
import org.fwwb.convene.convenecode.network.ClusterToTypo;
import org.fwwb.convene.convenecode.utils.CheckNetwork;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.DirectUrlCall;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.convenecode.utils.ProgressUtils;
import org.fwwb.convene.convenecode.utils.RestUrl;

import java.util.ArrayList;
import java.util.List;


public class BeneficiaryAsyncTaskLoading extends AsyncTask<Context, Integer, String> {
    Context context;
    DirectUrlCall resturl;
    private RestUrl beneficiaryRestUrl;
    private CheckNetwork chNetwork;
    Activity activity;
    SharedPreferences preferences;
    private ClusterToTypo typoObj;
    private String userId;
    private ProgressDialog loginDialog;
    private static final String TABLE_NAME="Beneficiary";
    private ExternalDbOpenHelper dbOpenHelper;
    private String result;
    private String globalModifiedDate ="";
    private ProgressBar progressBar;
    private static final String PARTNER_ID="partner_id";

    /**
     * BeneficiaryAsyncTaskLoading constructor
     * @param context param
     * @param activity param
     * @param typo param
     * @param uId param
     * @param progressBar param
     */
    public BeneficiaryAsyncTaskLoading(Context context, Activity activity, ClusterToTypo typo, String uId,ProgressBar progressBar) {
        this.context = context;
        this.resturl = new DirectUrlCall(context);
        this.chNetwork = new CheckNetwork(context);
        this.activity = activity;
        beneficiaryRestUrl=new RestUrl(context);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.dbOpenHelper = ExternalDbOpenHelper.getInstance(context, this.preferences.getString(Constants.DBNAME, ""), this.preferences.getString("uId", ""));
        this.typoObj = typo;
        this.userId = uId;
        this.progressBar=progressBar;
    }

    /**
     * onPreExecute method
     *
     */
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setProgress(10);
    }

    /**
     * doInBackground method
     * @param params PARAMS
     * @return result
     */
    protected String doInBackground(Context... params) {
        if(!chNetwork.checkNetwork()) {
            return "";
        } else {
            dbOpenHelper = new ExternalDbOpenHelper(context, preferences.getString(Constants.DBNAME, ""), preferences.getString("uId", ""));
            Logger.logV("the db path is", "the path of the db is" + this.dbOpenHelper.getReadableDatabase());
            List<NameValuePair> paramsL = new ArrayList<>();
                globalModifiedDate =dbOpenHelper.getLastUpDate(TABLE_NAME, "server_datetime", this.dbOpenHelper);

                paramsL.add(new BasicNameValuePair("modified_date", globalModifiedDate));
                paramsL.add(new BasicNameValuePair("user_id", userId));
                paramsL.add(new BasicNameValuePair(PARTNER_ID,String.valueOf(preferences.getInt(PARTNER_ID,0))));
                result = resturl.restUrlServerCall("beneficiary/datewiselisting/", "post", paramsL);
                publishProgress(50);
                Logger.logV("the parameters are", "the params" + paramsL);
                parseResponse(result);
            return result;
        }
    }

    /**
     * onPostExecute method
     * @param result result
     */
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            if((result).contains(Constants.HTMLSTRING)){
                ProgressUtils.CancelProgress(loginDialog);
                this.typoObj.callTypoScreen(false);
            }else{
                ProgressUtils.CancelProgress(loginDialog);
                JSONObject jsonObject;
                jsonObject = new JSONObject(result);
                int e = jsonObject.getInt("status");
                if(e == 2) {
                    this.typoObj.callTypoScreen(true);
                } else {
                    Toast.makeText(this.context, "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            }
        }catch (Exception e){
            Logger.logE("","",e);
            ProgressUtils.CancelProgress(loginDialog);
            this.typoObj.callTypoScreen(false);
        }

    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressBar.setProgress(90);
    }


    /**
     * parseResponse method
     * @param mainResult param
     *
     */
    private void parseResponse(String mainResult) {
        try {
            Logger.logD("childHold ResponseMessage->", "childHold MessageSuccessfull response........." + mainResult);
            JSONObject e = new JSONObject(mainResult);
            int status = e.getInt("status");

            if(status == 2) {
                try {
                    Gson e1 = new Gson();
                    GetBeneficiaryDetails level1List = e1.fromJson(mainResult, GetBeneficiaryDetails.class);
                    dbOpenHelper = new ExternalDbOpenHelper(context, preferences.getString(Constants.DBNAME, ""), preferences.getString("uId", ""));
                    Logger.logV("childHold ResponseMessage->", "childHold MessageSuccessfull response........." + this.dbOpenHelper.getDatabaseName());
                    dbOpenHelper.updateBeneficiary(level1List);
                        if(!level1List.getData().isEmpty()){
                            //Modify by guru
                            String tempModifiedMax = dbOpenHelper.getLastUpDate(TABLE_NAME, "server_datetime", this.dbOpenHelper);
                            Logger.logV("update date", "the after option api modified  "+tempModifiedMax);
                            if(!globalModifiedDate.equalsIgnoreCase(tempModifiedMax))
                            {
                                globalModifiedDate = tempModifiedMax;
                                List<NameValuePair> paramsL = new ArrayList<>();
                                paramsL.add(new BasicNameValuePair("modified_date", tempModifiedMax));
                                paramsL.add(new BasicNameValuePair("user_id", userId));
                                paramsL.add(new BasicNameValuePair(PARTNER_ID,String.valueOf(preferences.getInt(PARTNER_ID,0))));
                                result = this.resturl.restUrlServerCall("beneficiary/datewiselisting/", "post", paramsL);
                                Logger.logV("the parameters are", "the params" + paramsL);
                                parseResponse(result);
                            }else{
                                Logger.logD("BeneficiaryAsynctask","Exception on Calling Beneficiary Async Task");
                                beneficiaryRestUrl.writeToTextFile("Conflict on Beneficiary date",tempModifiedMax,"getBeneficiarylist");
                            }
                        }
                } catch (Exception var9) {
                    Logger.logE("","" , var9);
                }
            }
        } catch (Exception var10) {
            Logger.logE("Exception", "Exception in benefficiary async task", var10);
        }
    }
}
