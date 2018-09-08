package org.fwwb.convene.convenecode.api.BeneficiaryApis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.fwwb.convene.convenecode.AutoSyncActivity;
import org.fwwb.convene.convenecode.BeenClass.beneficiaryList.Beneficiary;
import org.fwwb.convene.convenecode.MyIntentServiceBeneficiary;
import org.fwwb.convene.convenecode.database.ExternalDbOpenHelper;
import org.fwwb.convene.convenecode.network.DirectUrlCall;
import org.fwwb.convene.convenecode.utils.ClusterInfo;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.convenecode.utils.RestUrl;
import org.fwwb.convene.convenecode.utils.ToastUtils;
import org.fwwb.convene.convenecode.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SaveBeneficiary extends AsyncTask<Context, Integer, String> {
    /*
     * Declaring all the variables and views
     */
    private DirectUrlCall resturl;
    private Context context;
    String result = "";
    private RestUrl addBeneRestUrl;
    private String apiName;
    private ExternalDbOpenHelper dbOpenHelper;
    private SharedPreferences sharedPreferences;

    Activity activity;
    private String beneficiaryName="";



    /*
     * Calling categories task constructor
     */
    public SaveBeneficiary(Context context, String nameOfApi) {
        this.context = context;
        resturl = new DirectUrlCall(context);
        apiName = nameOfApi;
        addBeneRestUrl=new RestUrl(context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        dbOpenHelper = ExternalDbOpenHelper.getInstance(context, sharedPreferences.getString(Constants.DBNAME, ""), sharedPreferences.getString("inv_id", ""));
    }

    @Override
    protected String doInBackground(Context... params) {
        if (!Utils.haveNetworkConnection(context)) {
            return "";
        }
        try {
            List<String>uuidStringList=dbOpenHelper.getBeneficiaryNotSynclist();
            for(int i = 0; i< uuidStringList.size(); i++){
                List<Beneficiary> datumList = dbOpenHelper.getBeneficiaryTempDetails(uuidStringList.get(i));
                List<NameValuePair>  paramsL = new ArrayList<>();
                String beneficiaryType = datumList.get(0).getBtype();
                if(("Household").equals(beneficiaryType)){
                    paramsL.add(new BasicNameValuePair("parent_id", ""));
                }else {
                    paramsL.add(new BasicNameValuePair("parent_id", String.valueOf(datumList.get(0).getParentId())));
                }
                paramsL.add(new BasicNameValuePair("partner_id",String.valueOf(sharedPreferences.getInt("partner_id",0))));
                beneficiaryName=datumList.get(0).getName();
                paramsL.add(new BasicNameValuePair("name",beneficiaryName ));
                paramsL.add(new BasicNameValuePair("age", datumList.get(0).getAge()));
                paramsL.add(new BasicNameValuePair("gender", datumList.get(0).getGender()));
                paramsL.add(new BasicNameValuePair("address", datumList.get(0).getAddress()));
                paramsL.add(new BasicNameValuePair("btype","Child"));
                paramsL.add(new BasicNameValuePair("contact_no", datumList.get(0).getContactNo()));
                paramsL.add(new BasicNameValuePair("date_of_birth",datumList.get(0).getDateOFBirth()));
                paramsL.add(new BasicNameValuePair("alias_name",datumList.get(0).getAliasName()));
                paramsL.add(new BasicNameValuePair("source","android"));
                paramsL.add(new BasicNameValuePair("createdDate",datumList.get(0).getCreatedDate()));
                String uuid = datumList.get(0).getUuid();
                paramsL.add(new BasicNameValuePair("uuid", uuid));
                paramsL.add(new BasicNameValuePair("beneficiary_type_id",String.valueOf(datumList.get(0).getBeneficiaryTypeId())));
                result = resturl.restUrlServerCall(context, apiName, "post", paramsL, "");
                Logger.logV("the parameters are", "the Beneficiary values params" + paramsL);
                JSONObject object = new JSONObject(result);
                if(object.getInt("status")==2){
                    String getUpdate=dbOpenHelper.updateBeneficiaryDetails(uuid,"Beneficiary",object.getInt("ben_id"));
                    Logger.logV("CallIntentServeice","CallIntentServeice to Update Beneficiary UI -->" + getUpdate);

                }else {
                    ToastUtils.displayToast(object.getString("msg"),context);
                }
            }
        } catch (Exception e) {
            ToastUtils.displayToastUi(sharedPreferences.getString(ClusterInfo.probOccurredWhileDataSend, ""), context);
            Logger.logE(AutoSyncActivity.class.getSimpleName(), "Exception in Adding beneficiary ", e);
            addBeneRestUrl.writeToTextFile(Log.getStackTraceString(e), "", "AddBeneficiary");
        }
        return result;
    }



    @Override
    protected void onPostExecute(String categoryList) {
        super.onPostExecute(categoryList);
        try {
            JSONObject object = new JSONObject(result);
            if(object.getInt("status")==2){
                ToastUtils.displayToast(beneficiaryName + " added successfully",context);
                Intent intent = new Intent(context, MyIntentServiceBeneficiary.class);
                context.startService(intent);

            }else{
                ToastUtils.displayToast(object.getString("msg"),context);
            }
        }catch (Exception e){
         Logger.logE("","Exception on saving the beneficiary",e);
        }
    }


    public void parseResponse(String result) {
        Logger.logV("the house hold response is", "the response is" + result);
    }

}
