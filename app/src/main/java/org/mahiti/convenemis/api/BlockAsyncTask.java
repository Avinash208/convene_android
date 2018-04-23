package org.mahiti.convenemis.api;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mahiti.convenemis.beansClassSetQuestion.BlockBeen;
import org.mahiti.convenemis.beansClassSetQuestion.CallApis;
import org.mahiti.convenemis.database.ConveneDatabaseHelper;
import org.mahiti.convenemis.utils.CheckNetwork;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.RestUrl;
import org.mahiti.convenemis.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;



public class BlockAsyncTask extends AsyncTask<Context, Integer, String> {
    private static final String TAG = "BlockAsyncTask";
    /*
     * Declaring all the variables and views
     */
    private SharedPreferences preferences;
    private RestUrl resturl;
    private Context context;
    CheckNetwork chNetwork;
    Activity activity;
    ConveneDatabaseHelper dbhelper;
    String result = "";
    private CallApis blockApi;
    private String globalModifiedDate ="";
    private static final String TABLE_NAME="Block";

    /**
     * BlockAsyncTask method
     * @param context param
     * @param activity param
     * @param block param
     */
    /*
     * Calling categories task constructor
     */
    public BlockAsyncTask(Context context, Activity activity,CallApis block) {
        this.context = context;
        resturl = new RestUrl(context);
        chNetwork = new CheckNetwork(context);
        this.activity = activity;
        blockApi=block;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        dbhelper = ConveneDatabaseHelper.getInstance(context, preferences.getString(Constants.CONVENE_DB,""),preferences.getString("UID",""));
    }


    @Override
    protected String doInBackground(Context... params) {
        if (!chNetwork.checkNetwork()) {
            return "";

        }
        try {
            List<NameValuePair> paramsL = new ArrayList<>();
            globalModifiedDate =dbhelper.getLastUpDate(TABLE_NAME, Constants.UPDATED_TIME);
            paramsL.add(new BasicNameValuePair("uId", preferences.getString("UID","")));
            paramsL.add(new BasicNameValuePair("updatedtime", globalModifiedDate));
            paramsL.add(new BasicNameValuePair("count", String.valueOf(dbhelper.getCursorCount(TABLE_NAME, Constants.UPDATED_TIME))));
            result = resturl.restUrlServerCall(activity,"block-list" + "/", "post", paramsL, "");
            Logger.logV("the parameters are", "the params" + paramsL);
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
                blockApi.blockApi(1,false);
            }else{
                blockApi.blockApi(1,true);
            }
        }catch (Exception e){
            Logger.logE("","",e);
            blockApi.blockApi(1,false);
        }



    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    /**
     * parseResponse method
     * @param result param
     *
     */
    public void parseResponse(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            Logger.logV(TAG,"the block data is"+jsonObject);
            int status = jsonObject.getInt("status");
            if (status == 2) {
                    methodToCallPagination(jsonObject);
                    validateNextSetOfPagination(jsonObject);
            }
        } catch (Exception e) {
            Logger.logE("","",e);
        }
    }

    /**
     * validateNextSetOfPagination method
     * @param jsonObject param
     *
     */
    private void validateNextSetOfPagination(JSONObject jsonObject) {
        //Modify by guru
        String tempModifyDate = dbhelper.getLastUpDate(TABLE_NAME, Constants.UPDATED_TIME);
        if(!globalModifiedDate.equalsIgnoreCase(tempModifyDate)) {
            try {
                if (!("Data already sent").equalsIgnoreCase(jsonObject.getString("message"))){
                        globalModifiedDate=tempModifyDate;
                        List<NameValuePair> paramsL = new ArrayList<>();
                        paramsL.add(new BasicNameValuePair("uId", preferences.getString("UID", "")));
                        paramsL.add(new BasicNameValuePair("updatedtime", tempModifyDate));
                        paramsL.add(new BasicNameValuePair("count", String.valueOf(dbhelper.getCursorCount(TABLE_NAME, Constants.UPDATED_TIME))));
                        result = resturl.restUrlServerCall(activity, "block-list" + "/", "post", paramsL, "");
                        Logger.logV("the parameters are", "the params in the second time is" + paramsL);
                        parseResponse(result);
                }
            } catch (JSONException e) {
                Logger.logE("","",e);
            }
        }else{
            Logger.logD(TAG,"Exception on calling Block Api");
            resturl.writeToTextFile("Conflict on Block date", tempModifyDate,"getBlocklist");
        }
    }

    /**
     * methodToCallPagination method
     * @param jsonObject param
     */
    private void methodToCallPagination(JSONObject jsonObject) {
        try {
            if (!("Data already sent").equalsIgnoreCase(jsonObject.getString("message"))){
                JSONArray blockArray = jsonObject.getJSONArray(TABLE_NAME);
                Logger.logV(TAG,"the block data is"+blockArray.length());
                if (blockArray.length() > 0)
                {
                    dbhelper = ConveneDatabaseHelper.getInstance(context, preferences.getString(Constants.CONVENE_DB,""),preferences.getString("UID",""));
                    Gson gson = new Gson();
                    BlockBeen level1List = gson.fromJson(result, BlockBeen.class);
                    level1List.getBlock();
                    dbhelper.updateBlock(level1List);
                }
                else
                {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("BlockDbUpdated", false);
                    editor.apply();
                    ToastUtils.displayToastUi("Updated successfully", context);

                }
            }

        } catch (Exception e) {
            Logger.logE("","",e);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("BlockDbUpdated", false);
            editor.apply();
        }
    }
}
