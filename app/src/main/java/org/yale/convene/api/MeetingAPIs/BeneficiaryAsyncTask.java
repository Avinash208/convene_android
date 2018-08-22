package org.yale.convene.api.MeetingAPIs;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.yale.convene.BeenClass.beneficiary.GetBeneficiaryDetails;
import org.yale.convene.database.ExternalDbOpenHelper;
import org.yale.convene.network.ClusterToTypo;
import org.yale.convene.utils.CheckNetwork;
import org.yale.convene.utils.Constants;
import org.yale.convene.utils.DirectUrlCall;
import org.yale.convene.utils.Logger;
import org.yale.convene.utils.ProgressUtils;
import org.yale.convene.utils.RestUrl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class BeneficiaryAsyncTask extends AsyncTask<Context, Integer, String> {
    private Context context;
    private DirectUrlCall resturl;
    private RestUrl beneficiaryRestUrl;
    private CheckNetwork chNetwork;
    private Activity activity;
    private SharedPreferences preferences;
    private ClusterToTypo typoObj;
    private String userId;
    private ProgressDialog loginDialog;
    private ExternalDbOpenHelper dbOpenHelper;
    private String lastModifiedDate="";
    private static final String TABLE_NAME="Beneficiary";
    private static final String PARTNER_ID="partner_id";

    public BeneficiaryAsyncTask(Context context, Activity activity, ClusterToTypo typo, String uId) {
        this.context = context;
        this.resturl = new DirectUrlCall(context);
        this.chNetwork = new CheckNetwork(context);
        beneficiaryRestUrl=new RestUrl(context);
        this.activity = activity;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.dbOpenHelper = ExternalDbOpenHelper.getInstance(context, this.preferences.getString(Constants.DBNAME, ""), this.preferences.getString("uId", ""));
        this.typoObj = typo;
        this.userId = uId;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        this.loginDialog = ProgressUtils.showProgress(this.activity, false, "Loading...");
        this.loginDialog.show();
    }

    protected String doInBackground(Context... params) {
        if(!chNetwork.checkNetwork()) {
            return "";
        } else {
            dbOpenHelper = new ExternalDbOpenHelper(this.context, this.preferences.getString(Constants.DBNAME, ""), this.preferences.getString("uId", ""));
            Logger.logV("the db path is", "the path of the db is" + this.dbOpenHelper.getReadableDatabase());
            List<NameValuePair> paramsL = new ArrayList<>();
            lastModifiedDate=dbOpenHelper.getLastUpDate(TABLE_NAME, Constants.LAST_MODIFIED, this.dbOpenHelper);
            paramsL.add(new BasicNameValuePair("modified_date", lastModifiedDate));
            paramsL.add(new BasicNameValuePair("user_id", userId));
            paramsL.add(new BasicNameValuePair(PARTNER_ID,String.valueOf(preferences.getInt(PARTNER_ID,0))));
            String result = resturl.restUrlServerCall("beneficiary/datewiselisting/", "post", paramsL);
            Logger.logV("the parameters are", "the params" + paramsL);
            parseResponse(result);
            return result;
        }
    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        JSONObject jsonObject;
        try {
            ProgressUtils.CancelProgress(loginDialog);
            if (!(result == null)){
                jsonObject = new JSONObject(result);
                int e = jsonObject.getInt("status");
                if(e == 2) {
                    copyEncryptedDataBase(this.context);
                    typoObj.callTypoScreen(true);
                } else {
                    Toast.makeText(this.context, "Something went wrong", Toast.LENGTH_SHORT).show();

                }
            }else{
                Logger.logV("BeneficiaryAsyncTask","Exception reached the last");
                typoObj.callTypoScreen(true);
            }

        } catch (Exception var4) {
            Logger.logE("","",var4);
        }

    }

    private void parseResponse(String result) {
        try {
            JSONObject e = new JSONObject(result);
            Logger.logD("childHold ResponseMessage->", "childHold MessageSuccessfull response........." + result);
            int status = e.getInt("status");
            JSONArray jsonData= e.getJSONArray("data");
            if(status == 2 && jsonData.length()>0) {
                try {
                    Gson e1 = new Gson();
                    GetBeneficiaryDetails level1List = e1.fromJson(result, GetBeneficiaryDetails.class);
                    dbOpenHelper = new ExternalDbOpenHelper(this.context, this.preferences.getString(Constants.DBNAME, ""), this.preferences.getString("uId", ""));
                    Logger.logV("childHold ResponseMessage->", "childHold MessageSuccessfull response........." + this.dbOpenHelper.getDatabaseName());
                    dbOpenHelper.updateBeneficiary(level1List);

                    if(!lastModifiedDate.equalsIgnoreCase(dbOpenHelper.getLastUpDate(TABLE_NAME, Constants.LAST_MODIFIED, this.dbOpenHelper))){
                        String modifiedDate = this.dbOpenHelper.getLastUpDate(TABLE_NAME, Constants.LAST_MODIFIED, this.dbOpenHelper);
                        List<NameValuePair> paramsL = new ArrayList<>();
                        paramsL.add(new BasicNameValuePair("modified_date", modifiedDate));
                        paramsL.add(new BasicNameValuePair("user_id", userId));
                        paramsL.add(new BasicNameValuePair(PARTNER_ID,String.valueOf(preferences.getInt(PARTNER_ID,0))));
                        String secondResult = this.resturl.restUrlServerCall("beneficiary/datewiselisting/", "post", paramsL);
                        Logger.logV("the parameters are", "the params" + paramsL);
                        parseResponse(secondResult);
                    }else{
                        Logger.logV("BeneficiaryAsyncTask","send error conflict on BeneficiaryAsyncTask");
                        beneficiaryRestUrl.writeToTextFile("Conflict on Beneficiary Date",lastModifiedDate,"");

                    }

                    this.copyEncryptedDataBase(this.context);
                } catch (Exception var9) {
                    Logger.logE("","",var9);
                }
            }
        } catch (Exception var10) {
            Logger.logE("Exception", "Exception in benefficiary async task", var10);
        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void copyEncryptedDataBase(Context con) {
        String packageName = con.getApplicationContext().getPackageName();
        String DB_PATH = String.format("/data/data/%s/databases/", packageName);
        String inFileName = DB_PATH + this.preferences.getString(Constants.DBNAME, "");
        Logger.logV("the input file is", "the path of the file is" + inFileName);
        File dbFile = new File(inFileName);
        if(dbFile.exists()) {
            String e = Environment.getExternalStorageDirectory().getPath() + "/surveylevels.sqlite";
            try(FileInputStream fis = new FileInputStream(dbFile);FileOutputStream output = new FileOutputStream(e)) {
                Logger.logV("teh file path is", "the file path is sd card is" + Environment.getExternalStorageDirectory().getPath() + "/levels");
                byte[] buffer = new byte[1024];

                int length;
                while((length = fis.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }

                output.flush();
                output.close();
                fis.close();
            } catch (FileNotFoundException var11) {
                Logger.logE("", "Exception in copyEncryptedDataBase method", var11);
            } catch (IOException var12) {
                Logger.logE("", "Exception in copyEncryptedDataBase method second catch", var12);
            }
        }
    }
}
