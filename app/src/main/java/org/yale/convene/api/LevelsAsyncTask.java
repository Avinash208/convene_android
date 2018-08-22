package org.yale.convene.api;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.yale.convene.BeenClass.parentChild.LevelFive;
import org.yale.convene.BeenClass.parentChild.LevelFour;
import org.yale.convene.BeenClass.parentChild.LevelOne;
import org.yale.convene.BeenClass.parentChild.LevelSeven;
import org.yale.convene.BeenClass.parentChild.LevelSix;
import org.yale.convene.BeenClass.parentChild.LevelThree;
import org.yale.convene.BeenClass.parentChild.LevelTwo;
import org.yale.convene.database.ExternalDbOpenHelper;
import org.yale.convene.network.ClusterToTypo;
import org.yale.convene.utils.CheckNetwork;
import org.yale.convene.utils.Constants;
import org.yale.convene.utils.Logger;
import org.yale.convene.utils.ProgressUtils;
import org.yale.convene.utils.RestUrl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LevelsAsyncTask extends AsyncTask<Context, Integer, String> {
    private static final String TAG = "LevelsAsyncTask";
    /*
     * Declaring all the variables and views
     */
    private SharedPreferences levelsSharedPreferences;
    private RestUrl resturl;
    private Context context;
    CheckNetwork chNetwork;
    Activity activity;
    ExternalDbOpenHelper levelsDbhelper;
    String result = "";
    private android.app.ProgressDialog loginDialog;
    private int currentPosition = 0;
    private int totalPosition = 0;
    private ClusterToTypo typoObj;
    private int userId;
    private String statusStr = "the status";

    /*
     * Calling categories task constructor
     */
    public LevelsAsyncTask(Context context, Activity activity, ClusterToTypo typo, int uId) {
        this.context = context;
        resturl = new RestUrl(context);
        chNetwork = new CheckNetwork(context);
        this.activity = activity;
        levelsSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        levelsDbhelper =  ExternalDbOpenHelper.getInstance(context, levelsSharedPreferences.getString(Constants.DBNAME,""),String.valueOf(uId));
        typoObj = typo;
        this.userId=uId;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        loginDialog = ProgressUtils.showProgress(activity, false, "Loading master data" + "...");
        loginDialog.show();
    }

    @Override
    protected String doInBackground(Context... params) {
        if (!chNetwork.checkNetwork()) {
            return "";

        }
        try {
            String levels[] = levelsSharedPreferences.getString("app_Levels", "").split(",");
            Logger.logV(TAG,"the levels are"+ Arrays.toString(levels));
            for (int i = 0; i < levels.length; i++)
            {
                totalPosition = totalPosition + 1;
            }
            for (int i = 0; i < levels.length; i++)
            {
                currentPosition=currentPosition+1;
                publishProgress();
                List<NameValuePair> paramsL = new ArrayList<>();
                paramsL.add(new BasicNameValuePair("uid", String.valueOf(userId)));
                paramsL.add(new BasicNameValuePair("modified_date", levelsDbhelper.getLastUpDate(levels[i], "modified_date", levelsDbhelper)));
                Logger.logV(TAG, "URL-->" + levels[i]);
                 char splitLevel= levels[i].charAt(levels[i].length()-1);
                result = resturl.restUrlServerCall(activity, "level/"+splitLevel+ "/", "post", paramsL, "");
                Logger.logV(TAG, "the url" + "level/"+splitLevel+ "/");
                Logger.logV(TAG, "the params" + paramsL);
                parseResponse(result, levels[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String categoryList) {
        super.onPostExecute(categoryList);
        try {
            ProgressUtils.CancelProgress(loginDialog);
            new BeneficiaryAsyncTask(context, activity, typoObj,String.valueOf(userId)).execute();
        }catch (Exception e){
            Logger.logE("","",e);
        }


    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (currentPosition != 0)
            loginDialog.setMessage("Please wait loading master data  " + (currentPosition) + " out of  " + totalPosition);
    }

    public void parseResponse(String result, String levels) {
        try {
            Logger.logV(TAG,"the levels are in parseResponse  "+levels);
            levelsDbhelper =new ExternalDbOpenHelper(context, levelsSharedPreferences.getString(Constants.DBNAME,""), levelsSharedPreferences.getString("uId",""));
            final JSONObject json = new JSONObject(result);
            int status = json.getInt("status");
            if (status == 2)
            {
                Gson gson = new Gson();
                checKLevelAndUpdateTable(levels,gson);
            } else {
                Logger.logV(statusStr, statusStr + status);
            }
            if (status == 2) {
                int pageCount = json.getInt("page_count");
                Logger.logV("the page count is","the page count number is"+pageCount);
                if(pageCount>1)
                {
                    for (int i = 2; i <=pageCount; i++)
                    {
                        Logger.logV("the page count is","the page count number is"+i);
                        List<NameValuePair> paramsL = new ArrayList<>();
                        paramsL.add(new BasicNameValuePair("uid", String.valueOf(userId)));
                         String secondResult = resturl.restUrlServerCall(activity,levels+"-list" + "/?page="+ i, "post", paramsL, "");
                        Logger.logV(TAG, "the params in the second time is" + paramsL);
                        parseResponsePagination(secondResult, levels);
                    }
                }
            } else {
                Logger.logV(statusStr, statusStr + status);
            }
        } catch (Exception e) {
           Logger.logE("","",e);
        }
    }

    private void checKLevelAndUpdateTable(String levels, Gson gson) {
        if("Level1".equalsIgnoreCase(levels))
        {
            LevelOne level1List = gson.fromJson(result, LevelOne.class);
            levelsDbhelper.updateLevel1List(level1List);
        }
        if("Level2".equalsIgnoreCase(levels))
        {
            LevelTwo level1List = gson.fromJson(result, LevelTwo.class);
            levelsDbhelper.updateLevel2List(level1List);
        }
        if("Level3".equalsIgnoreCase(levels))
        {
            LevelThree level1List = gson.fromJson(result, LevelThree.class);
            levelsDbhelper.updateLevel3List(level1List);
        }
        if("Level4".equalsIgnoreCase(levels))
        {
            LevelFour level1List = gson.fromJson(result, LevelFour.class);
            levelsDbhelper.updateLevel4List(level1List);
        }
        if("Level5".equalsIgnoreCase(levels))
        {
            LevelFive level1List = gson.fromJson(result, LevelFive.class);
            levelsDbhelper.updateLevel5List(level1List);
        }
        if("Level6".equalsIgnoreCase(levels))
        {
            LevelSix level1List = gson.fromJson(result, LevelSix.class);
            levelsDbhelper.updateLevel6List(level1List);
        }
        if("Level7".equalsIgnoreCase(levels))
        {
            LevelSeven level1List = gson.fromJson(result, LevelSeven.class);
            levelsDbhelper.updateLevel7List(level1List);

        }
        if("Level8".equalsIgnoreCase(levels))
        {
            LevelSeven level1List = gson.fromJson(result, LevelSeven.class);
            levelsDbhelper.updateLevel7List(level1List);

        }
        if("Level9".equalsIgnoreCase(levels))
        {
            LevelSeven level1List = gson.fromJson(result, LevelSeven.class);
            levelsDbhelper.updateLevel7List(level1List);
        }
    }

    private void parseResponsePagination(String result, String levels) {
        try {
            levelsDbhelper =new ExternalDbOpenHelper(context, levelsSharedPreferences.getString(Constants.DBNAME,""), levelsSharedPreferences.getString("uId",""));
            final JSONObject json = new JSONObject(result);
            int status = json.getInt("status");
            if (status == 2)
            {
                Gson gson = new Gson();
                checKLevelAndUpdateTable(levels,gson);
            } else {
                Logger.logV(statusStr, statusStr + status);
            }
        } catch (Exception e) {
            Logger.logE("","",e);
        }
    }
}
