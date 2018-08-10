package org.assistindia.convene.api.BeneficiaryApis;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.assistindia.convene.network.DirectUrlCall;
import org.assistindia.convene.utils.Constants;
import org.assistindia.convene.utils.Logger;
import org.assistindia.convene.utils.ProgressUtils;
import org.assistindia.convene.utils.StartSurvey;
import org.assistindia.convene.utils.ToastUtils;
import org.assistindia.convene.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ResponseUpdateAPI extends AsyncTask<Context, Integer, String> {
    /*
     * Declaring all the variables and views
     */
    private DirectUrlCall resturl;
    private Context context;
    String result = "";
    private android.app.ProgressDialog loginDialog;
    String apiName;
    SharedPreferences preferences;
    List<NameValuePair>  paramsL;
    private String serverPid="";
    Activity activity;
    private int locationid;
    private String locationName;
    private String beneficiaryArray;
    private String boundaryLevel;
    private int surveyid;
    private int languageId;

    /*
     * Calling categories task constructor
     */
    public ResponseUpdateAPI(Activity activity, Context context, String nameOfApi, String getServerPid,
                             int ilocationID,String locationName, String beneficiaryArray,String boundaryLevel,int surveyID) {
        this.context = context;
        this.activity=activity;
        resturl = new DirectUrlCall(context);
        apiName = nameOfApi;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.locationid=ilocationID;
        this.serverPid=getServerPid;
        this.locationName=locationName;
        this.beneficiaryArray=beneficiaryArray;
        this.boundaryLevel=boundaryLevel;
        this.surveyid=surveyID;
    }
    protected void onPreExecute() {
        super.onPreExecute();
        this.loginDialog = ProgressUtils.showProgress(this.activity, false, "Loading ...");
        this.loginDialog.show();

    }

    @Override
    protected String doInBackground(Context... params) {

        if (!Utils.haveNetworkConnection(context)) {
            return "";
        }
        try {
            paramsL = new ArrayList<>();
            paramsL.add(new BasicNameValuePair("userid", preferences.getString("UID","")));
            paramsL.add(new BasicNameValuePair("responseid",serverPid));

            result = resturl.restUrlServerCall(activity,apiName, "post", paramsL, "");
            Logger.logV("the parameters are", "the params" + paramsL);
            Logger.logV("the parameters are", "result" + result);
            parseResponse(result);

        } catch (Exception e) {
            Logger.logE("","",e);
        }

        return result;
    }



    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        ProgressUtils.CancelProgress(loginDialog);
        try {
            JSONObject jsonObject=new JSONObject(result);
            languageId=jsonObject.getInt("language_id");
            SharedPreferences.Editor editor= preferences.edit();
            editor.putString("serverSurveyPID",serverPid);
            editor.putString("serverResponseResult",result);
            editor.putInt(Constants.SURVEY_ID,surveyid);
            editor.apply();
            new StartSurvey(context, activity,surveyid, locationid, locationName, beneficiaryArray, boundaryLevel,serverPid,languageId).execute();
        }catch (Exception e){
            Logger.logE("","",e);
            ToastUtils.displayToast("Unable to fetch the information, Please try later",context);
        }

    }
    public void parseResponse(String result) {
        Logger.logV("the house hold response is", "the response is" + result);
    }

}
