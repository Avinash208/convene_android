package org.fwwb.convene.convenecode.api.BeneficiaryApis;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.fwwb.convene.convenecode.BeenClass.service.Datum;
import org.fwwb.convene.convenecode.BeenClass.service.ServiceList;
import org.fwwb.convene.convenecode.BeenClass.service.ServiceListInterface;
import org.fwwb.convene.R;
import org.fwwb.convene.convenecode.database.ExternalDbOpenHelper;
import org.fwwb.convene.convenecode.network.DirectUrlCall;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.convenecode.utils.ToastUtils;
import org.fwwb.convene.convenecode.utils.Utils;

import java.util.ArrayList;
import java.util.List;



public class ServiceListAsyncTaskLoading extends AsyncTask<Context, Integer, String> {
    private DirectUrlCall resturl;
    private Context context;
    Activity activity;
    String result = "";
    private ServiceListInterface serviceListInterface;
    private List<Datum> facilityListBeen=new ArrayList<>();
    private ExternalDbOpenHelper serviceListDbhelper;
    private SharedPreferences surveyListPreferences;
    private static final String TAG="ServiceListAsyncTaskLoading";

    /**
     * ServiceListAsyncTaskLoading method
     * @param context param
     * @param activity param
     * @param houseHoldActivity param
     */
    public ServiceListAsyncTaskLoading(Context context, Activity activity, ServiceListInterface houseHoldActivity) {
        this.context = context;
        resturl = new DirectUrlCall(context);
        this.activity = activity;
        serviceListInterface = houseHoldActivity;
        surveyListPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        serviceListDbhelper =  ExternalDbOpenHelper.getInstance(context, surveyListPreferences.getString(Constants.DBNAME,""), surveyListPreferences.getString("UID",""));
    }

    @Override
    public String doInBackground(Context... params) {
        if (!Utils.haveNetworkConnection(context)) {
            return "";
        }
        try {
            List<NameValuePair> paramsL = new ArrayList<>();
            result = resturl.restUrlServerCall(activity, context.getString(R.string.main_Url)+"service/servicelistingwithpagination/", "post", paramsL, "");
            Logger.logV(TAG, "the params" + paramsL);
            parseResponse(result);

        } catch (Exception e) {
           Logger.logE("","",e);
        }

        return result;
    }

    @Override
    protected void onPostExecute(String categoryList) {
        super.onPostExecute(categoryList);
        try {
            if((categoryList).contains(Constants.HTMLSTRING)){
                serviceListInterface.onSuccessServiceUpdate(false);
            }else {

                JSONObject jsonObject = new JSONObject(categoryList);
                int status = jsonObject.getInt("status");
                if (status == 2) {
                    serviceListInterface.onSuccessServiceUpdate(true);
                } else {
                        ToastUtils.displayToast("Something went Wrong", context);
                }

            }
        }catch (Exception e){
            Logger.logE("","",e);
            serviceListInterface.onSuccessServiceUpdate(false);

        }


    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    /**
     * parseResponse method
     * @param result param
     */
    private void parseResponse(String result) {
        try {
            Logger.logD("HouseholdResponseMessage->", "Service result" + result);
            JSONObject jsonObject = new JSONObject(result);
            int status = jsonObject.getInt("status");
            if (status == 2) {
                Logger.logD("HouseholdResponseMessage->", "HouseHoldMessageSuccessfull");

                if (!"".equals(result)) {
                    Gson gson = new Gson();
                    ServiceList serviceList = gson.fromJson(result, ServiceList.class);
                    Logger.logD("houseHoldList->", serviceList.getMessage());
                    Logger.logD("Passing->", "Passing the Response bean to Interface");
                    serviceListDbhelper.updateServices(serviceList);
                }
            }
            if (status == 2) {
                try {
                    JSONArray blockArray = jsonObject.getJSONArray("data");
                    Logger.logV(TAG, "the block data is" + blockArray.length());
                    if (blockArray.length() > 0) {
                        for (int i = 0; i < blockArray.length(); i++) {
                            JSONObject obj = blockArray.getJSONObject(i);
                            Datum dataList = new Datum();
                            dataList.setName(obj.getString("name"));
                            dataList.setId(obj.getInt("id"));
                            facilityListBeen.add(dataList);
                        }
                    }
                } catch (Exception e) {
                    Logger.logE("","",e);
                }
                String nextRequest = jsonObject.getString("next");
                if (!nextRequest.isEmpty()) {
                    List<NameValuePair> paramsL = new ArrayList<>();
                    String secondResult = resturl.restUrlServerCall(activity, nextRequest, "post", paramsL, "");
                    Logger.logV(TAG, "the params in the second time is" + paramsL);
                    parseResponse(secondResult);
                } else {
                    Logger.logV(TAG, "the questionsArray lenth is " + nextRequest);
                }
            }
        }
        catch (Exception e) {
           Logger.logE("","",e);
        }
    }
}

