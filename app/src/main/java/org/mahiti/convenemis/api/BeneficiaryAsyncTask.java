package org.mahiti.convenemis.api;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.mahiti.convenemis.BeenClass.beneficiary.GetBeneficiaryDetails;
import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.network.ClusterToTypo;
import org.mahiti.convenemis.utils.CheckNetwork;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.DirectUrlCall;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.ProgressUtils;

import java.util.ArrayList;
import java.util.List;



public class BeneficiaryAsyncTask extends AsyncTask<Context, Integer, String> {
    Context context;
    DirectUrlCall resturl;
    CheckNetwork chNetwork;
    Activity activity;
    private SharedPreferences preferences;
    private ClusterToTypo typoObj;
    android.app.ProgressDialog loginDialog;
    private ExternalDbOpenHelper dbOpenHelper;
    String globalDate = "";
    private String result="";


    public BeneficiaryAsyncTask(Context context, Activity activity, ClusterToTypo typo, String uId) {
        this.context = context;
        resturl = new DirectUrlCall(context);
        chNetwork = new CheckNetwork(context);
        this.activity = activity;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        dbOpenHelper=ExternalDbOpenHelper.getInstance(context,preferences.getString(Constants.DBNAME,""),preferences.getString("uId",""));
        typoObj=typo;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        loginDialog= ProgressUtils.showProgress(activity, false, "Loading master data" + "...");
        loginDialog.show();
    }
    @Override
    protected String doInBackground(Context... params) {
        if (!chNetwork.checkNetwork()) {
            return "";

        }

        dbOpenHelper=new ExternalDbOpenHelper(context, preferences.getString(Constants.DBNAME,""),preferences.getString("uId",""));

        Logger.logV("the db path is","the path of the db is"+dbOpenHelper.getReadableDatabase());
        List<NameValuePair> paramsL = new ArrayList<>();
        globalDate = dbOpenHelper.getLastUpDate("Beneficiary","server_datetime",dbOpenHelper);
        paramsL.add(new BasicNameValuePair("modified_date", globalDate));
        paramsL.add(new BasicNameValuePair("user_id", preferences.getString("uId", "")));
        result = resturl.restUrlServerCall("beneficiary/datewiselisting/", "post", paramsL);
        Logger.logV("the parameters are", "the params for datewiseapi" + paramsL);
        parseResponse(result);
        return result;

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            if((result).contains(Constants.HTMLSTRING)){
                typoObj.callTypoScreen(false);
            }else{
                ProgressUtils.CancelProgress(loginDialog);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    if (status == 2) {
                        typoObj.callTypoScreen(true);
                    }else{
                        Toast.makeText(context,"Something went wrong",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Logger.logE("","Exception on Beneficary task", e);
                }
            }
        }catch (Exception e){
            Logger.logE("","",e);
            typoObj.callTypoScreen(false);
        }


    }

    private void parseResponse(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            Logger.logD("BeneficiaryAsyncTask ResponseMessage->","BeneficiaryAsyncTask response........."+result);
            int status = jsonObject.getInt("status");
            if (status == 2) {
                try {
                    Gson gson = new Gson();
                    GetBeneficiaryDetails level1List = gson.fromJson(result, GetBeneficiaryDetails.class);

                    dbOpenHelper=new ExternalDbOpenHelper(context, preferences.getString(Constants.DBNAME,""),preferences.getString("uId",""));
                    Logger.logV("BeneficiaryAsyncTask ResponseMessage->","BeneficiaryAsyncTask MessageSuccessfull response........."+dbOpenHelper.getDatabaseName());

                    dbOpenHelper.updateBeneficiary(level1List);
                    if(!level1List.getData().isEmpty()){
                        String modifiedDate=dbOpenHelper.getLastUpDate("Beneficiary","server_datetime",dbOpenHelper);
                        Logger.logV("modifiedDate","modifiedDate"+modifiedDate);
                        //Modify by Guru
                        if (!globalDate.equalsIgnoreCase(modifiedDate)) {
                            globalDate = modifiedDate;
                            List<NameValuePair> paramsL = new ArrayList<>();
                            paramsL.add(new BasicNameValuePair("modified_date", modifiedDate));
                            paramsL.add(new BasicNameValuePair("user_id", preferences.getString("uId", "")));
                            String secondResult = resturl.restUrlServerCall("beneficiary/datewiselisting/", "post", paramsL);
                            Logger.logV("the parameters are", "the params" + paramsL);
                            parseResponse(secondResult);
                        }
                    }else {
                        Logger.logV("the status", "the questionsArray lenth is " + level1List.getData().size());
                    }
                } catch (Exception e) {
                    Logger.logE("","Exception in benefficiary async task",e);
                }

            }
        } catch (Exception e) {
            Logger.logE("Exception","Exception in benefficiary async task",e);
        }
    }
}
