package org.mahiti.convenemis.api.BeneficiaryApis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.mahiti.convenemis.BeenClass.facilities.Facility;
import org.mahiti.convenemis.MyIntentServiceFacility;
import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.network.DirectUrlCall;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.ToastUtils;
import org.mahiti.convenemis.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SaveFacility extends AsyncTask<Context, Integer, String> {
    /*
     * Declaring all the variables and views
     */
    private DirectUrlCall resturl;
    private Context context;
    Activity activity;
    String result = "";

    private String apiName;
    private ExternalDbOpenHelper facilityDbOpenHelper;
    SharedPreferences facilityPreferences;
    private String facilityType;
    private static final String TAG="SaveFacility";
    /*
     * Calling categories task constructor
     */
    public SaveFacility(Context context, String nameOfApi) {
        this.context = context;
        resturl = new DirectUrlCall(context);
        apiName = nameOfApi;
        facilityPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        facilityDbOpenHelper = ExternalDbOpenHelper.getInstance(context, facilityPreferences.getString(Constants.DBNAME, ""), facilityPreferences.getString("inv_id", ""));
    }


    @Override
    protected String doInBackground(Context... params) {
        if (!Utils.haveNetworkConnection(context)) {
            return "";
        }
        try {
            List<NameValuePair> beneficiaryNameValuePairs = new ArrayList<>();
            List<Facility> savedFacilitydatumList= facilityDbOpenHelper.getFacilityTempDetails();
            for(int i=0;i<savedFacilitydatumList.size();i++){
                facilityType=savedFacilitydatumList.get(i).getFacilityType();
                beneficiaryNameValuePairs.add(new BasicNameValuePair("partner_id",String.valueOf(facilityPreferences.getInt("partner_id",0))));
                beneficiaryNameValuePairs.add(new BasicNameValuePair("name", savedFacilitydatumList.get(i).getName()));
                facilityType=savedFacilitydatumList.get(i).getName();
                beneficiaryNameValuePairs.add(new BasicNameValuePair("facility_type_id", String.valueOf(savedFacilitydatumList.get(i).getFacilityTypeId())));
                beneficiaryNameValuePairs.add(new BasicNameValuePair("facility_subtype_id", String.valueOf(savedFacilitydatumList.get(i).getFacilitySubtypeId())));
                beneficiaryNameValuePairs.add(new BasicNameValuePair("thematic_area_id", String.valueOf(savedFacilitydatumList.get(i).getThematicAreaId())));
                beneficiaryNameValuePairs.add(new BasicNameValuePair("services", String.valueOf(savedFacilitydatumList.get(i).getServices())));
                beneficiaryNameValuePairs.add(new BasicNameValuePair("address1", savedFacilitydatumList.get(i).getAddress1()));
                beneficiaryNameValuePairs.add(new BasicNameValuePair("address2", savedFacilitydatumList.get(i).getAddress2()));
                beneficiaryNameValuePairs.add(new BasicNameValuePair("boundary_id", String.valueOf(savedFacilitydatumList.get(i).getBoundaryId())));
                beneficiaryNameValuePairs.add(new BasicNameValuePair("pincode", String.valueOf(savedFacilitydatumList.get(i).getPincode())));
                beneficiaryNameValuePairs.add(new BasicNameValuePair("source","android"));
                beneficiaryNameValuePairs.add(new BasicNameValuePair("uuid",savedFacilitydatumList.get(i).getUuid()));
                beneficiaryNameValuePairs.add(new BasicNameValuePair("createdDate",savedFacilitydatumList.get(i).getCreatedDateFac()));
                result = resturl.restUrlServerCall(context, apiName, "post", beneficiaryNameValuePairs, "");
                Logger.logV(TAG, "the params" + beneficiaryNameValuePairs);
                methodToUpdateTheView(result,savedFacilitydatumList,i);

            }
        } catch (Exception e) {
           Logger.logE(TAG,"",e);
        }

        return result;
    }

    private void methodToUpdateTheView(String result, List<Facility> savedFacilitydatumList, int i) {
        try {
            JSONObject object = new JSONObject(result);
            if(object.getInt("status")==2){
                int updateRecord= facilityDbOpenHelper.updateFacilityDetails(savedFacilitydatumList.get(i).getUuid());
                Logger.logV(TAG,"FacilityTemp table has been updated" +  updateRecord);
                Logger.logV(TAG,"CallIntentServeice to Update Beneficiary UI -->");
                Intent facilityIntent = new Intent(context, MyIntentServiceFacility.class);
                context.startService(facilityIntent);
            }else{
                ToastUtils.displayToast(object.getString("msg"),context);
            }
        } catch (JSONException e) {
            Logger.logE(TAG,"",e);
        }
    }


    @Override
    protected void onPostExecute(String categoryList) {
        super.onPostExecute(categoryList);
        try {
            JSONObject object = new JSONObject(result);
            if(object.getInt("status")==2){
                ToastUtils.displayToast(facilityType + " added successfully",context);
            }else {
                ToastUtils.displayToast(object.getString("msg"),context);

            }
        }catch (Exception e){
            Logger.logE("","",e);
        }

    }
}
