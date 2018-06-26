package org.mahiti.convenemis.api.BeneficiaryApis;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.mahiti.convenemis.BeenClass.parentChild.LevelFive;
import org.mahiti.convenemis.BeenClass.parentChild.LevelFour;
import org.mahiti.convenemis.BeenClass.parentChild.LevelOne;
import org.mahiti.convenemis.BeenClass.parentChild.LevelSeven;
import org.mahiti.convenemis.BeenClass.parentChild.LevelSix;
import org.mahiti.convenemis.BeenClass.parentChild.LevelThree;
import org.mahiti.convenemis.BeenClass.parentChild.LevelTwo;
import org.mahiti.convenemis.R;
import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.network.ClusterToTypo;
import org.mahiti.convenemis.utils.CheckNetwork;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.ProgressUtils;
import org.mahiti.convenemis.utils.RestUrl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class LevelsAsyncTask extends AsyncTask<Context, Integer, String> {
    /*
     * Declaring all the variables and views
     */
    private SharedPreferences preferences;
    private RestUrl resturl;
    private Context context;
    private CheckNetwork chNetwork;
    Activity activity;
    private ExternalDbOpenHelper dbhelper;
    String result = "";
    private android.app.ProgressDialog loginDialog;
    private int currentPosition = 0;
    private int totalPosition = 0;
    private int userId;
    private ProgressBar progressBar;
    private TextView locationStatus;
    private BeneficiaryTypeAPIInterface beneficiaryTypeAPIInterface;
    private static final String LEVEL="level/";
    private static final String MODIFIED_DATE="modified_date";
    private String globleDate = "";
    private String statusStr = "the status";
    private static final String LOADING_COLOR="#098759";

    /**
     * LevelsAsyncTask constructor
     * @param context param
     * @param activity param
     * @param typo param
     * @param uId param
     * @param progressBar param
     * @param beneficiaryTypeAPIInterface param
     * @param locationStatus param
     *
     */
    /*
     * Calling categories task constructor
     */
    public LevelsAsyncTask(Context context, Activity activity, ClusterToTypo typo, int uId, ProgressBar progressBar,BeneficiaryTypeAPIInterface beneficiaryTypeAPIInterface,TextView locationStatus) {
        this.context = context;
        resturl = new RestUrl(context);
        chNetwork = new CheckNetwork(context);
        this.activity = activity;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        dbhelper =  ExternalDbOpenHelper.getInstance(context, preferences.getString(Constants.DBNAME,""),String.valueOf(uId));
        this.userId=uId;
        this.beneficiaryTypeAPIInterface = beneficiaryTypeAPIInterface;
        this.userId=uId;
        this.progressBar=progressBar;
        this.locationStatus=locationStatus;
    }

    /**
     * onPreExecute method
     *
     */
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setProgress(30);
    }

    @Override
    protected String doInBackground(Context... params) {
        if (!chNetwork.checkNetwork()) {
            return "";

        }
        try {
            String levels[] = preferences.getString("app_Levels", "").split(",");
            Logger.logV("the levels are","the levels are"+ Arrays.toString(levels));
            for (int i = 0; i < levels.length; i++)
            {
                totalPosition = totalPosition + 1;
            }
            for (int i = 0; i < levels.length; i++)
            {
                currentPosition=currentPosition+1;
                publishProgress();

                globleDate = dbhelper.getLastUpDate(levels[i], MODIFIED_DATE,dbhelper);
                List<NameValuePair> paramsL = new ArrayList<>();
                paramsL.add(new BasicNameValuePair("uid", String.valueOf(userId)));
                paramsL.add(new BasicNameValuePair(MODIFIED_DATE,""));
                Logger.logV("", "URL-->" + levels[i]);
                char splitLevel= levels[i].charAt(levels[i].length()-1);
                result = resturl.restUrlServerCall(activity, LEVEL+splitLevel+ "/", "post", paramsL, "");
                Logger.logV("", "the url" + LEVEL+splitLevel+ "/");
                Logger.logV("", "the params" + paramsL);
                parseResponse(result, levels[i]);
            }
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
                ProgressUtils.CancelProgress(loginDialog);
                beneficiaryTypeAPIInterface.onSuccessfulBeneficiary(false);
            }else {
                ProgressUtils.CancelProgress(loginDialog);
                beneficiaryTypeAPIInterface.onSuccessfulBeneficiary(true);
            }
        }catch (Exception e){
            Logger.logE("","",e);
            ProgressUtils.CancelProgress(loginDialog);
            beneficiaryTypeAPIInterface.onSuccessfulBeneficiary(false);
        }


    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (currentPosition != 0)
            Logger.logV("","the levels progress status  "+currentPosition);
        Animation translatebu= AnimationUtils.loadAnimation(context, R.anim.animationfile);
        switch (currentPosition){
            case 1:
                progressBar.setProgress(50);
                locationStatus.setText(R.string.loading_level1);
               // locationStatus.startAnimation(translatebu);

                break;
            case 2:
                progressBar.setProgress(60);
                locationStatus.setText(R.string.loading_level2);
            //    locationStatus.startAnimation(translatebu);
                break;
            case 3:
                progressBar.setProgress(70);
                locationStatus.setText(R.string.loading_level3);
            //    locationStatus.startAnimation(translatebu);
                break;
            case 4:
                progressBar.setProgress(80);
                locationStatus.setText(R.string.loading_level4);
             //   locationStatus.startAnimation(translatebu);
                break;
            case 5:
                progressBar.setProgress(90);
                locationStatus.setText(R.string.loading_level5);
               // locationStatus.startAnimation(translatebu);
                break;
            case 6:
                progressBar.setProgress(100);
                locationStatus.setText(R.string.loading_level6);
              //  locationStatus.startAnimation(translatebu);
               // progressBar.setMinimumHeight(10);
               // progressBar.getProgressDrawable().setColorFilter(Color.parseColor(LOADING_COLOR), PorterDuff.Mode.SRC_IN);
                progressBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor(LOADING_COLOR)));
                break;
        }

    }

    /**
     * parseResponse method
     * @param result param
     * @param levels param
     */
    public void parseResponse(String result, String levels) {
        try {
            Logger.logV("","the levels are in parseResponse  "+levels);
            dbhelper=new ExternalDbOpenHelper(context, preferences.getString(Constants.DBNAME,""),preferences.getString("uId",""));
            final JSONObject json = new JSONObject(result);
            int status = json.getInt("status");
            if (status == 2)
            {
                Gson gson = new Gson();
                if("Level1".equalsIgnoreCase(levels))
                {
                    LevelOne level1List = gson.fromJson(result, LevelOne.class);
                    dbhelper.deleteLevels("Level1");
                    dbhelper.updateLevel1List(level1List);
                }
                if("Level2".equalsIgnoreCase(levels))
                {
                    LevelTwo level1List = gson.fromJson(result, LevelTwo.class);
                    dbhelper.deleteLevels("Level2");
                    dbhelper.updateLevel2List(level1List);
                }
                if("Level3".equalsIgnoreCase(levels))
                {
                    LevelThree level1List = gson.fromJson(result, LevelThree.class);
                    dbhelper.deleteLevels("Level3");
                    dbhelper.updateLevel3List(level1List);
                }
                if("Level4".equalsIgnoreCase(levels))
                {
                    LevelFour level1List = gson.fromJson(result, LevelFour.class);
                    dbhelper.deleteLevels("Level4");
                    dbhelper.updateLevel4List(level1List);
                }
                if("Level5".equalsIgnoreCase(levels))
                {
                    LevelFive level1List = gson.fromJson(result, LevelFive.class);
                    dbhelper.deleteLevels("Level5");
                    dbhelper.updateLevel5List(level1List);
                }
                if("Level6".equalsIgnoreCase(levels))
                {
                    LevelSix level1List = gson.fromJson(result, LevelSix.class);
                    dbhelper.deleteLevels("Level6");
                    dbhelper.updateLevel6List(level1List);
                }
                if("Level7".equalsIgnoreCase(levels))
                {
                    LevelSeven level1List = gson.fromJson(result, LevelSeven.class);
                    dbhelper.deleteLevels("Level7");
                    dbhelper.updateLevel7List(level1List);

                }
                if("Level8".equalsIgnoreCase(levels))
                {
                    LevelSeven level1List = gson.fromJson(result, LevelSeven.class);
                    dbhelper.updateLevel7List(level1List);

                }
                if("Level9".equalsIgnoreCase(levels))
                {
                    LevelSeven level1List = gson.fromJson(result, LevelSeven.class);
                    dbhelper.updateLevel7List(level1List);
                }

            } else {
                Logger.logV(statusStr, statusStr + status);
            }
            if (status == 2) {
                int pageCount = json.getInt("flag");
                if(pageCount>1)
                {
                    //Modify by guru
                    String modifyTempDate = dbhelper.getLastUpDate(levels, MODIFIED_DATE,dbhelper);
                    if (!globleDate.equalsIgnoreCase(modifyTempDate)) {
                        globleDate = modifyTempDate;
                        Logger.logV("", "the page count number is" + pageCount);
                        List<NameValuePair> paramsL = new ArrayList<>();
                        paramsL.add(new BasicNameValuePair("uid", String.valueOf(userId)));
                        paramsL.add(new BasicNameValuePair(MODIFIED_DATE, ""));
                        Logger.logV("", "URL-->" + levels);
                        char splitLevel = levels.charAt(levels.length() - 1);
                        result = resturl.restUrlServerCall(activity, LEVEL + splitLevel + "/", "post", paramsL, "");
                        Logger.logV("", "the url" + LEVEL + splitLevel + "/");
                        Logger.logV("", "the params" + paramsL);
                        Logger.logV("", "the Result for next time " + result);
                        parseResponse(result, levels);
                    }

                }
            } else {
                Logger.logV(statusStr, statusStr + status);
            }
        } catch (Exception e) {
            Logger.logE("","",e);
        }
    }
}
