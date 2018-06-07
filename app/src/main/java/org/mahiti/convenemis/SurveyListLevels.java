package org.mahiti.convenemis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.mahiti.convenemis.BeenClass.parentChild.SurveyDetail;
import org.mahiti.convenemis.api.LevelsAsyncTask;
import org.mahiti.convenemis.api.SurveyListAsyncTask;
import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.network.ClusterToTypo;
import org.mahiti.convenemis.utils.CheckNetwork;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;

import java.util.List;


public class SurveyListLevels extends Activity implements ClusterToTypo {
    SurveyListLevels activity;
    Context context;
    String mainUrl;
    int uId;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int resultsOK =201;
    ExternalDbOpenHelper dbOpenHelper;
    private String urlBeneficiary;
    CheckNetwork network;
    private static String dbName=Constants.DBNAME;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_list_levels);
        context = SurveyListLevels.this;
        if (getIntent() != null) {
            mainUrl = getIntent().getStringExtra("url");
            urlBeneficiary = getIntent().getStringExtra("urlBeneficiary");
            uId=getIntent().getIntExtra("uId",0);
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        network= new CheckNetwork(context);
        editor= sharedPreferences.edit();
        editor.putString("mainUrl",mainUrl);
        editor.putString("urlBeneficiary",urlBeneficiary);
        String dbFile =String.valueOf("Survey_C"+ uId +"_"+ Constants.DB_NAME);
        Logger.logV("the data file name is","the db file name si"+dbFile);
        editor.putString(dbName,dbFile);
        editor.putString("uId",String.valueOf(uId));
        editor.apply();
        dbOpenHelper= ExternalDbOpenHelper.getInstance(context, sharedPreferences.getString(dbName,""),String.valueOf(uId));
        if(network.checkNetwork()){
            callAsyncTasksRunOnUiThread();
        }else{
            Intent intent=new Intent(context,HomeActivity.class);
            context.startActivity(intent);
        }
     }

    public void callAsyncTasksRunOnUiThread() {
        runOnUiThread(() -> {new SurveyListAsyncTask(SurveyListLevels.this,SurveyListLevels.this, SurveyListLevels.this,String.valueOf(uId)).execute();});
    }


    /**
     *
     * @param flag
     */
    @Override
    public void callTypoScreen(boolean flag)
    {
        SharedPreferences sharedPreferences1=PreferenceManager.getDefaultSharedPreferences(context);
        Intent intent= new Intent();
        intent.putExtra("updated",flag);
        intent.putExtra("SurveyLevelDbPath",sharedPreferences1.getString(dbName,""));
        setResult(resultsOK,intent);
        finish();
    }

    /**
     *
     * @param con
     * @param dbName
     * @param uid
     * @param beneficiary
     * @return
     */
   public static List<SurveyDetail> getSurveyList(Context con, String dbName, String uid, String beneficiary)
   {
       ExternalDbOpenHelper dbHelpers= ExternalDbOpenHelper.getInstance(con, dbName,uid);
       return dbHelpers.getUpdatedSurveyList(beneficiary);
   }
   /**
     *
     * @param con
     * @param dbName
     * @param uid
     * @param surveyid
     * @return
     */
   public static List<SurveyDetail> getSurveyDetails(Context con, String dbName, String uid, String surveyid)
   {
       ExternalDbOpenHelper dbHelpers= ExternalDbOpenHelper.getInstance(con, dbName,uid);
       return dbHelpers.getUpdatedSurvey(surveyid);
   }

    @Override
    public void surveyListSuccess(boolean flag)
    {
        if(!flag)
            return;

        runOnUiThread(() -> {new LevelsAsyncTask(SurveyListLevels.this, SurveyListLevels.this,SurveyListLevels.this,uId).execute();});
    }
}