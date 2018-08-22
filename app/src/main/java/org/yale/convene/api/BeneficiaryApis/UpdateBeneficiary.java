package org.yale.convene.api.BeneficiaryApis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.yale.convene.AutoSyncActivity;
import org.yale.convene.BeenClass.beneficiaryList.Beneficiary;
import org.yale.convene.MyIntentServiceBeneficiary;
import org.yale.convene.database.ExternalDbOpenHelper;
import org.yale.convene.network.DirectUrlCall;
import org.yale.convene.utils.ClusterInfo;
import org.yale.convene.utils.Constants;
import org.yale.convene.utils.Logger;
import org.yale.convene.utils.RestUrl;
import org.yale.convene.utils.ToastUtils;
import org.yale.convene.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class UpdateBeneficiary extends AsyncTask<Context, Integer, String> {
    /*
     * Declaring all the variables and views
     */
    private DirectUrlCall directUrlCall;
    private Context context;
    String result = "";
    private RestUrl addBeneRestUrl;
    private String updateApiName;
    private ExternalDbOpenHelper externalDbOpenHelper;
    private SharedPreferences sharedPreferences;
    private String beneficiaryName="";
    private String updateBenStr = "UpdateBeneficiary";


    /*
     * Calling categories task constructor
     */
    public UpdateBeneficiary(Context context, String nameOfApi) {
        this.context = context;
        directUrlCall = new DirectUrlCall(context);
        updateApiName = nameOfApi;
        addBeneRestUrl=new RestUrl(context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        externalDbOpenHelper = ExternalDbOpenHelper.getInstance(context, sharedPreferences.getString(Constants.DBNAME, ""), sharedPreferences.getString("inv_id", ""));
    }


    @Override
    protected String doInBackground(Context... params) {
        if (!Utils.haveNetworkConnection(context)) {
            return "";
        }
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            List<Beneficiary> datumList = externalDbOpenHelper.getBeneficiaryDetails();
            for(int i = 0; i< datumList.size(); i++){
                if(("Household").equals(datumList.get(i).getBtype())){
                    nameValuePairs.add(new BasicNameValuePair("parent_id", ""));
                }else {
                    nameValuePairs.add(new BasicNameValuePair("parent_id", String.valueOf(datumList.get(i).getParentId())));
                }
                nameValuePairs.add(new BasicNameValuePair("partner_id",String.valueOf(sharedPreferences.getInt("partner_id",0))));
                beneficiaryName=datumList.get(i).getName();
                nameValuePairs.add(new BasicNameValuePair("name",beneficiaryName ));
                nameValuePairs.add(new BasicNameValuePair("age", datumList.get(i).getAge()));
                nameValuePairs.add(new BasicNameValuePair("gender", datumList.get(i).getGender()));
                String addressDump=datumList.get(i).getAddress();
                /*JSONArray addressJsonObject=new JSONArray(addressDump);
                JSONObject mainJsonObject=new JSONObject();
                for(int j=0;j<addressJsonObject.length();j++){
                    JSONObject jsonObject=addressJsonObject.getJSONObject(j);
                    jsonObject.put("address1",jsonObject.getString("address1"));
                    jsonObject.put("address2",jsonObject.getString("address2"));
                    jsonObject.put("boundary_id",jsonObject.getString("boundary_id"));
                    jsonObject.put("least_location_name",jsonObject.getString("least_location_name"));
                    jsonObject.put("pincode",jsonObject.getString("pincode"));
                    jsonObject.put("address_id",jsonObject.getString("address_id"));
                    jsonObject.put("primary",jsonObject.getString("primary"));
                    jsonObject.put("location_level",jsonObject.getString("location_level"));
                    mainJsonObject.put("address_" + j +"",jsonObject);
                }*/
                nameValuePairs.add(new BasicNameValuePair("address",addressDump));
                nameValuePairs.add(new BasicNameValuePair("btype","Child"));
                nameValuePairs.add(new BasicNameValuePair("alias_name",datumList.get(i).getAliasName()));
                nameValuePairs.add(new BasicNameValuePair("date_of_birth",datumList.get(i).getDateOFBirth()));
                nameValuePairs.add(new BasicNameValuePair("contact_no", datumList.get(i).getContactNo()));
                nameValuePairs.add(new BasicNameValuePair("beneficiary_type_id",String.valueOf(datumList.get(i).getBeneficiaryTypeId())));
                if(("Child").equals(datumList.get(i).getBtype())){
                    nameValuePairs.add(new BasicNameValuePair("father_name", datumList.get(i).getFatherName()));
                }else{
                    nameValuePairs.add(new BasicNameValuePair("guardian_name", datumList.get(i).getFatherName()));
                }
                nameValuePairs.add(new BasicNameValuePair("id",String.valueOf(datumList.get(i).getId())));
                result = directUrlCall.restUrlServerCall(context, updateApiName, "post", nameValuePairs, "");
                Logger.logV("the parameters are", "the Beneficiary updated values params" + nameValuePairs);
                JSONObject object = new JSONObject(result);
                if(object.getInt("status")==2){
                    int updateBeneficiaryDetails= externalDbOpenHelper.updateEditBeneficiaryDetails(datumList.get(i).getUuid(),"Beneficiary");
                    Logger.logV("CallIntentServeice","CallIntentServeice to Update Beneficiary UI -->" + updateBeneficiaryDetails);
                    Intent updateBeneficiaryIntent = new Intent(context, MyIntentServiceBeneficiary.class);
                    context.startService(updateBeneficiaryIntent);
                }else {
                    ToastUtils.displayToast(object.getString("msg"),context);
                }
            }
        } catch (Exception e) {
            ToastUtils.displayToastUi(sharedPreferences.getString(ClusterInfo.probOccurredWhileDataSend, ""), context);
            Logger.logE(AutoSyncActivity.class.getSimpleName(), "Exception in Updating beneficiary ", e);
            addBeneRestUrl.writeToTextFile(Log.getStackTraceString(e), "", updateBenStr);
        }
        return result;
    }




    @Override
    protected void onPostExecute(String categoryList) {
        super.onPostExecute(categoryList);
        try {
            if(!categoryList.isEmpty()) {
                JSONObject object = new JSONObject(categoryList);
                if (object.getInt("status") == 2) {
                    ToastUtils.displayToast(beneficiaryName + " updated successfully", context);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putBoolean("HouseholdUpdateApiStatus",true);

                    if("FACILITY".equalsIgnoreCase(sharedPreferences.getString("SELECTED_PAGE", ""))){
                        editor.putBoolean(updateBenStr,false);
                        editor.putBoolean("isEditBeneficiary",false);
                        editor.putBoolean("isEditFacility",true);
                        editor.putBoolean("UpdateFacility",true);
                        editor.putBoolean("UpdateFacilityUi",true);
                    }else{
                        editor.putBoolean(updateBenStr,true);
                        editor.putBoolean("isEditBeneficiary",true);
                        editor.putBoolean("isEditFacility",false);
                        editor.putBoolean("UpdateFacility",false);
                        editor.putBoolean("UpdateFacilityUi",false);
                    }
                    editor.apply();
                } else {
                    ToastUtils.displayToast(object.getString("msg"), context);
                }
            }
        }catch (Exception e){
         Logger.logE("","Exception on saving the beneficiary",e);
        }
    }


    public void parseResponse(String result) {
        Logger.logV("the house hold response is", "the response is" + result);
    }

}
