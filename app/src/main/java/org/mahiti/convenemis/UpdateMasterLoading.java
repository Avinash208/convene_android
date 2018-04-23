package org.mahiti.convenemis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;
import org.mahiti.convenemis.BeenClass.facilities.FacilityListInterface;
import org.mahiti.convenemis.BeenClass.facilitiesBeen.FacilitiesAreaInterface;
import org.mahiti.convenemis.BeenClass.service.ServiceListInterface;
import org.mahiti.convenemis.api.AssessmentsAsyncTask;
import org.mahiti.convenemis.api.BeneficiaryApis.AddressProofAsyncTask;
import org.mahiti.convenemis.api.BeneficiaryApis.AddressProofsInterface;
import org.mahiti.convenemis.api.BeneficiaryApis.BeneficaryTypeInterface;
import org.mahiti.convenemis.api.BeneficiaryApis.BeneficiaryAsyncTaskLoading;
import org.mahiti.convenemis.api.BeneficiaryApis.BeneficiaryTypeAPIInterface;
import org.mahiti.convenemis.api.BeneficiaryApis.BeneficiaryTypeAsyncTask;
import org.mahiti.convenemis.api.BeneficiaryApis.FacilitiesListAsyncTaskLoading;
import org.mahiti.convenemis.api.BeneficiaryApis.FacilityTypeAsyncTask;
import org.mahiti.convenemis.api.BeneficiaryApis.FacilityTypeInterface;
import org.mahiti.convenemis.api.BeneficiaryApis.GetLocationLevelAsyncTask;
import org.mahiti.convenemis.api.BeneficiaryApis.GetLocationLevelInterface;
import org.mahiti.convenemis.api.BeneficiaryApis.LevelsAsyncTask;
import org.mahiti.convenemis.api.BeneficiaryApis.PeriodicTypeInterface;
import org.mahiti.convenemis.api.BeneficiaryApis.PeriodicityAsyncTask;
import org.mahiti.convenemis.api.BeneficiaryApis.RegionalLanguageInterface;
import org.mahiti.convenemis.api.BeneficiaryApis.ServiceListAsyncTaskLoading;
import org.mahiti.convenemis.api.BeneficiaryApis.SurveyListAsyncTask;
import org.mahiti.convenemis.api.BeneficiaryApis.ThematicAreaAsyncTask;
import org.mahiti.convenemis.api.BeneficiaryApis.UpdateRegionalLanguge;
import org.mahiti.convenemis.api.BeneficiaryApis.UpdatedTablesAsynTask;
import org.mahiti.convenemis.api.BlockAsyncTask;
import org.mahiti.convenemis.api.LanguageAssessmentAsyncTask;
import org.mahiti.convenemis.api.LanguageBlockAsyncTask;
import org.mahiti.convenemis.api.LanguageLabelsAsyncTask;
import org.mahiti.convenemis.api.LanguageOptionsAsyncTask;
import org.mahiti.convenemis.api.LanguageQuestionsAsyncTask;
import org.mahiti.convenemis.api.OptionsAsyncTask;
import org.mahiti.convenemis.api.QuestionsAsyncTask;
import org.mahiti.convenemis.api.SkipMandatoryAsyncTask;
import org.mahiti.convenemis.api.SkipRulesAsyncTask;
import org.mahiti.convenemis.beansClassSetQuestion.CallApis;
import org.mahiti.convenemis.beansClassSetQuestion.UpdatedTablesInerface;
import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.network.ClusterToTypo;
import org.mahiti.convenemis.utils.CheckNetwork;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.FileUtils;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.ToastUtils;
import org.mahiti.convenemis.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateMasterLoading extends BaseActivity implements ClusterToTypo ,BeneficiaryTypeAPIInterface,UpdatedTablesInerface,CallApis,
        ServiceListInterface,FacilityListInterface,GetLocationLevelInterface,BeneficaryTypeInterface,PeriodicTypeInterface,FacilitiesAreaInterface,FacilityTypeInterface,RegionalLanguageInterface,View.OnClickListener,AddressProofsInterface {
    UpdateMasterLoading activity;
    Context context;
    String mainUrl;
    int uId;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ExternalDbOpenHelper dbOpenHelper;
    private String urlBeneficiary;
    CheckNetwork network;
    private ProgressBar progressBarLocation;
    private ProgressBar progressBarBeneficiary;
    private TextView locationStatus;
    private TextView beneficiaryStatus;
    private ProgressBar beneficiaryDatacolledtionProgressbar;
    private ProgressBar otherProgressBar;
    private TextView beneficiaryDatacolledtionText;
    private boolean setUpdateTabelLoopFlag=true;
    ProgressBar bar;
    int value = 0;
    Handler handler = new Handler();
    //................ for update Mater data
    String[] keys = {"Block", "LanguageBlock", "Question", "LanguageQuestion", Constants.ASSESSMENT,
            "LanguageAssessment", "SkipMandatory", "SkipRules", "Options", "LanguageLabels","LanguageOptions"};
    int currentAPINum = 0;
    JSONObject globalObj;
    private TextView otherStatus;
    private Animation translatebu;
    private ImageView locationimageStatus;
    private ImageView beneficaryimagestatus;
    private ImageView datacollectionimagestatus;
    private ImageView ohterimagestatus;
    private static final String DB_FILE="ConveneDB.sqlite";
    private static final String LOADING_COLOR="#098759";
    private static final String LOADING_TEXT_COLOR="#d24645";
    private static final String TAG="UpdateMasterLoading";
    private static final String CHECK_CONNECTIVITY="Please check Internet Connection";
    private static final String RETRY_CONNECTIVITY="Internet is required. Please check internet connection and retry.";
    private static final String RESPONSE_ERROR="Something went wrong. Please Retry.";
    private static String dbName=Constants.DBNAME;
    private SurveyListAsyncTask surveyListAsyncTask;
    private LevelsAsyncTask levelsAsyncTask;
    private ServiceListAsyncTaskLoading serviceListAsyncTaskLoading;
    private UpdatedTablesAsynTask updatedTablesAsynTask;
    private AddressProofAsyncTask addressProofAsyncTask;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_loading);
        initializeVariables();

        progressBarLocation = findViewById(R.id.location_progressbar);
        progressBarLocation.setMax(100);
        locationStatus= findViewById(R.id.location_status);
        locationimageStatus= findViewById(R.id.locationimageStatus);

        beneficiaryStatus= findViewById(R.id.beneficiary_status);
        progressBarBeneficiary = findViewById(R.id.beneficiary_progressbar);
        beneficaryimagestatus= findViewById(R.id.beneficaryimageStatus);
        progressBarBeneficiary.setMax(100);

        beneficiaryDatacolledtionText= findViewById(R.id.datacollection_status);
        beneficiaryDatacolledtionProgressbar = findViewById(R.id.datacollection_progressbar);
        datacollectionimagestatus= findViewById(R.id.datacollectionimageStatus);
        beneficiaryDatacolledtionProgressbar.setMax(100);
        otherStatus= findViewById(R.id.other_status);
        otherProgressBar = findViewById(R.id.other_progressbar);
        ohterimagestatus= findViewById(R.id.ohterimageStatus);
        otherProgressBar.setMax(100);

        context = UpdateMasterLoading.this;
        translatebu= AnimationUtils.loadAnimation(context, R.anim.animationfile);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (getIntent() != null) {
            mainUrl = sharedPreferences.getString("url",getString(R.string.main_url));
            urlBeneficiary = sharedPreferences.getString("urlBeneficiary",getString(R.string.main_url_Beneficiary));
            uId=Integer.parseInt(sharedPreferences.getString("UID",""));
        }
        editor=sharedPreferences.edit();
        editor.putString("mainUrl",mainUrl);
        editor.putString("urlBeneficiary",urlBeneficiary);

        String dbFile ="Survey_C"+uId+"_"+ Constants.DB_NAME;
        editor.putString(dbName,dbFile);
        editor.putString("uId",String.valueOf(uId));
        editor.putString("mainUrl", mainUrl);

        String dbFile2 = "Survey_C" + uId + "_" + Constants.DB_NAMES;
        editor.putString(Constants.CONVENE_DB, dbFile2);
        editor.putBoolean("MASTERTABLEUPDATE_FLAG", false);
        editor.putBoolean("CheckBeneficiaryUpdateFlag", false);
        editor.putString("UID", String.valueOf(uId));
        editor.apply();
        dbOpenHelper= ExternalDbOpenHelper.getInstance(context, sharedPreferences.getString(dbName,""),String.valueOf(uId));
        network= new CheckNetwork(context);
        methodToNavigateHome();
        runProgressBar();
    }

    /**
     * initializeVariables method
     *
     */
    private void initializeVariables() {
        TextView helptextTextView = findViewById(R.id.helptext);
        TextView logoutTextView = findViewById(R.id.logout);
        helptextTextView.setOnClickListener(this);
        logoutTextView.setOnClickListener(this);
    }

    /**
     * methodToNavigateHome method
     *
     */
    private void methodToNavigateHome() {
        if(network.checkNetwork()){
            // call all async tasks
            callAsyncTasks(uId);
        }else{
            setMasterDatabaseUpdateTimeStamp();
            // Launching home activity after
            Intent intent=new Intent(context,HomeActivityNew.class);
            context.startActivity(intent);
            finish();
        }
    }

    /**
     * callAsyncTasks method
     * @param uId param
     */
    public void callAsyncTasks( final int uId) {
        new Thread(() -> runOnUiThread(() -> {
            if(Utils.haveNetworkConnection(context)) {
                surveyListAsyncTask= new SurveyListAsyncTask(UpdateMasterLoading.this, UpdateMasterLoading.this, UpdateMasterLoading.this, String.valueOf(uId), progressBarLocation, locationStatus);
                surveyListAsyncTask.execute();
            }else{
                showAlertErrorInternetConnectivity(context,1,RETRY_CONNECTIVITY, true);
            }
        })).start();
    }
    @Override
    public void callTypoScreen(boolean flag) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBarBeneficiary.getProgressDrawable().setColorFilter(Color.parseColor(LOADING_COLOR), PorterDuff.Mode.SRC_IN);
        }
        beneficaryimagestatus.setVisibility(View.VISIBLE);
        beneficaryimagestatus.setBackgroundResource(R.drawable.done);
        beneficiaryStatus.setTextColor(Color.parseColor(LOADING_COLOR));
        beneficiaryStatus.setText(getString(R.string.completed));
        progressBarBeneficiary.setProgress(100);
        beneficiaryStatus.setAnimation(translatebu);
        Logger.logD(TAG,"Back frm Beneficiary  API");
        editor = sharedPreferences.edit();
        editor.putString("SurveylevelDBPath", sharedPreferences.getString(dbName,""));
        editor.putString("DB_FLAG", "1");
        editor.apply();
        FileUtils.copyEncryptedDataBase(context);
        if(flag) {
            if (network.checkNetwork()) {
                updatedTablesAsynTask = new UpdatedTablesAsynTask(context, activity, UpdateMasterLoading.this, beneficiaryDatacolledtionProgressbar, beneficiaryDatacolledtionText);
                updatedTablesAsynTask.execute();
            } else {
                showAlertErrorInternetConnectivity(context,4,RETRY_CONNECTIVITY,true);
            }
        }else{
            showAlertErrorInternetConnectivity(context,3,RESPONSE_ERROR, false);
        }
    }

    @Override
    public void surveyListSuccess(boolean flag) {
        Logger.logD(TAG,"Back frm location API");
        if(!flag){
            showAlertErrorInternetConnectivity(context,1,RESPONSE_ERROR, false);
            return;
        }
        if(!network.checkNetwork()){
            showAlertErrorInternetConnectivity(context,2,RETRY_CONNECTIVITY, true);
            return;
        }
        new Thread(() -> runOnUiThread(() -> {
            levelsAsyncTask=  new LevelsAsyncTask(UpdateMasterLoading.this, UpdateMasterLoading.this,UpdateMasterLoading.this,uId,progressBarLocation,UpdateMasterLoading.this,locationStatus);
            levelsAsyncTask.execute();
        })).start();

    }
    @Override
    public void onSuccessfulBeneficiary(boolean flag) {
        locationStatus.setTextColor(Color.parseColor(LOADING_COLOR));
        locationStatus.setText(getString(R.string.completed));
        locationStatus.setAnimation(translatebu);
        locationimageStatus.setImageResource(R.drawable.done);
        beneficaryimagestatus.setVisibility(View.VISIBLE);
        FileUtils.copyEncryptedDataBase(context);
        if(flag) {
            if (Utils.haveNetworkConnection(this)) {
                callTypoScreen(true);
            } else {
                showAlertErrorInternetConnectivity(context, 3, RETRY_CONNECTIVITY, true);
            }
        }
        else{
            showAlertErrorInternetConnectivity(context,2,RESPONSE_ERROR, false);
        }

    }

    @Override
    public void updatedTablesList(JSONObject tables,boolean flag) {
        FileUtils.copyEncryptedDataBaseForMasterData(context, sharedPreferences.getString(Constants.CONVENE_DB, ""), DB_FILE);
        if(flag){
            try {
                globalObj = tables;
                callAPIs(currentAPINum, flag);
                beneficiaryDatacolledtionProgressbar.setProgress(20);
            } catch (Exception ex) {
                Logger.logE("UpdateTable Activity","Exception", ex);
            }
        }else{
            showAlertErrorInternetConnectivity(context,4,RESPONSE_ERROR, false);
        }
    }


    /**
     * callAPIs method
     * @param currentAPINumber param
     * @param flag param
     */
    public void callAPIs(int currentAPINumber, boolean flag) {
        FileUtils.copyEncryptedDataBase(context);
        try {
            for (int i = currentAPINumber; i < globalObj.length(); i++) {
                currentAPINumber = i;
                try {
                    if (i<keys.length) {
                        if (globalObj.getBoolean(keys[i])) {
                            Logger.logV("Calling next", "call APi" + i);
                            callServer(i,flag);
                            currentAPINum = i;
                            break;
                        }
                    }else{
                        break;
                    }
                } catch (Exception e) {
                    Logger.logE("","",e);
                }
            }
        } catch (Exception e) {
            Logger.logE("","",e);
        } finally
        {
            if ((currentAPINumber == 11 || currentAPINumber == 10) && setUpdateTabelLoopFlag)
            {
                setUpdateTabelLoopFlag=false;
                FileUtils.copyEncryptedDataBaseForMasterData(context, sharedPreferences.getString(Constants.CONVENE_DB, ""), DB_FILE);
                ohterimagestatus.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    beneficiaryDatacolledtionProgressbar.getProgressDrawable().setColorFilter(Color.parseColor(LOADING_COLOR), PorterDuff.Mode.SRC_IN);
                    beneficiaryDatacolledtionProgressbar.setProgress(100);
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("TablesUpdated", "true");
                beneficiaryDatacolledtionText.setTextColor(Color.parseColor(LOADING_COLOR));
                beneficiaryDatacolledtionText.setText(getString(R.string.completed));
                beneficiaryDatacolledtionText.setAnimation(translatebu);
                beneficaryimagestatus.setVisibility(View.VISIBLE);
                beneficaryimagestatus.setImageResource(R.drawable.done);
                try {
                    //calling Service listing Api
                    if(Utils.haveNetworkConnection(this)){
                        otherProgressBar.setProgress(10);
                        otherStatus.setText(R.string.loading_service);
                        setMasterDatabaseUpdateTimeStamp();
                        Intent intent=new Intent(context,HomeActivityNew.class);
                        startActivity(intent);
                        finish();
                    }else{
                        ToastUtils.displayToast(CHECK_CONNECTIVITY, this);
                    }
                } catch (Exception e) {
                    Logger.logE("Exception", "Exception in SurveyListActivity", e);
                }

            }
        }
    }

    /**
     * callServer method
     * @param apiCount param
     *
     * @param flag param
     */
    public void callServer(int apiCount, boolean flag) {
        switch (apiCount) {
            case 0:
                runAllAPIs(flag,R.string.loading_block,35,apiCount,5);
                break;
            case 1:
                runAllAPIs(flag,R.string.loading_block,40,apiCount,6);
                break;
            case 2:
                runAllAPIs(flag,R.string.loading_question,45,apiCount,7);
                break;
            case 3:
                runAllAPIs(flag,R.string.loading_question,50,apiCount,8);
                break;
            case 4:
                runAllAPIs(flag,R.string.loading_assessment,55,apiCount,9);
                break;
            case 5:
                runAllAPIs(flag,R.string.loading_assessment,60,apiCount,10);
                break;
            case 6:
                runAllAPIs(flag,R.string.loading_skip_mandatory,65,apiCount,11);
                break;
            case 7:
                runAllAPIs(flag,R.string.loading_skip_rules,70,apiCount,12);
                break;
            case 8:
                runAllAPIs(flag,R.string.loading_options,75,apiCount,13);
                break;
            case 9:
                runAllAPIs(flag,R.string.loading_regional_langauge,80,apiCount,14);
                break;
            case 10:
                runAllAPIs(flag,R.string.loading_options,99,apiCount,15);
                break;
            default:
                break;
        }
    }

    public void runAllAPIs(final boolean flag, final int stringId, final int percentage, final int apiCount, final int nextSetApiCount ){
        UpdateMasterLoading.this.runOnUiThread(() -> {
            beneficiaryDatacolledtionText.setTextColor(Color.parseColor(LOADING_TEXT_COLOR));
            beneficiaryDatacolledtionText.setText(getString(stringId));
            beneficiaryDatacolledtionText.setAnimation(translatebu);
            beneficiaryDatacolledtionProgressbar.setProgress(percentage);
            if(!flag) {
                showAlertErrorInternetConnectivity(context,nextSetApiCount,RESPONSE_ERROR, false);
                return;
            }
            if (!Utils.haveNetworkConnection(context)) {
                showAlertErrorInternetConnectivity(context, nextSetApiCount, RETRY_CONNECTIVITY, true);
            }
            callSwitchCaseAPIS(apiCount);
        });

    }

    public void callSwitchCaseAPIS(int apiCountNumber){
        switch (apiCountNumber)
        {
            case 0:
                BlockAsyncTask blockAsyncTask = new BlockAsyncTask(context, activity, UpdateMasterLoading.this);
                blockAsyncTask.execute();
                break;
            case 1:
                LanguageBlockAsyncTask languageBlockAsyncTask = new LanguageBlockAsyncTask(context, activity, UpdateMasterLoading.this);
                languageBlockAsyncTask.execute();
                break;
            case 2:
                QuestionsAsyncTask questionsAsyncTask = new QuestionsAsyncTask(context, activity, UpdateMasterLoading.this);
                questionsAsyncTask.execute();
                break;
            case 3:
                LanguageQuestionsAsyncTask languageQuestionsAsyncTask = new LanguageQuestionsAsyncTask(context, activity, UpdateMasterLoading.this);
                languageQuestionsAsyncTask.execute();
                break;
            case 4:
                AssessmentsAsyncTask assessmentsAsyncTask = new AssessmentsAsyncTask(context, activity, UpdateMasterLoading.this);
                assessmentsAsyncTask.execute();
                break;
            case 5:
                LanguageAssessmentAsyncTask languageAssessmentAsyncTask = new LanguageAssessmentAsyncTask(context, activity, UpdateMasterLoading.this);
                languageAssessmentAsyncTask.execute();
                break;
            case 6:
                SkipMandatoryAsyncTask skipMandatoryAsyncTask = new SkipMandatoryAsyncTask(context, activity, UpdateMasterLoading.this);
                skipMandatoryAsyncTask.execute();
                break;
            case 7:
                SkipRulesAsyncTask skipRulesAsyncTask = new SkipRulesAsyncTask(context, activity, UpdateMasterLoading.this);
                skipRulesAsyncTask.execute();
                break;
            case 8:
                OptionsAsyncTask optionsAsyncTask = new OptionsAsyncTask(context, activity, UpdateMasterLoading.this);
                optionsAsyncTask.execute();
                break;
            case 9:
                LanguageLabelsAsyncTask languageLabelsAsyncTask = new LanguageLabelsAsyncTask(context, activity, UpdateMasterLoading.this);
                languageLabelsAsyncTask.execute();
                break;
            case 10:
                LanguageOptionsAsyncTask languageOptionsAsyncTask = new LanguageOptionsAsyncTask(context, activity, UpdateMasterLoading.this);
                languageOptionsAsyncTask.execute();
                break;
            default:
                break;
        }
    }

    @Override
    public void blockApi(int needToCall,boolean flag) {
        callAPIs(needToCall, flag);
    }

    @Override
    public void languageBlock(int needToCall,boolean flag) {
        callAPIs(needToCall, flag);
    }

    @Override
    public void questionApi(int needToCall,boolean flag) {
        callAPIs(needToCall, flag);
    }

    @Override
    public void languageQuestionApi(int needToCall,boolean flag) {
        callAPIs(needToCall,flag);
    }

    @Override
    public void assessmentApi(int needToCall,boolean flag) {
        callAPIs(needToCall, flag);
    }

    @Override
    public void languageAssessmentApi(int needToCall,boolean flag) {
        callAPIs(needToCall, flag);
    }

    @Override
    public void skipMandatoryApi(int needToCall,boolean flag) {
        callAPIs(needToCall, flag);
    }

    @Override
    public void skipRulesApi(int needToCall,boolean flag) {
        callAPIs(needToCall, flag);
    }

    @Override
    public void optionsApi(int needToCall,boolean flag) {
        callAPIs(needToCall, flag);
    }

    @Override
    public void languageLabelsApi(int needToCall,boolean flag) {
        callAPIs(needToCall, flag);
    }

    @Override
    public void languageOptions(int needToCall,boolean flag) {
        callAPIs(needToCall, flag);
    }

    @Override
    public void onSuccessServiceUpdate(boolean flag) {
        datacollectionimagestatus.setVisibility(View.VISIBLE);
        datacollectionimagestatus.setBackgroundResource(R.drawable.done);
        otherProgressBar.setProgress(20);
        otherStatus.setText(R.string.loading_facility);
        Logger.logD(TAG,"back from service to Facilities");
        if(flag) {
            if (Utils.haveNetworkConnection(this)) {
                FacilitiesListAsyncTaskLoading facilitiesListAsyncTaskLoading = new FacilitiesListAsyncTaskLoading(this, this, this);
                facilitiesListAsyncTaskLoading.execute();
            } else {
                showAlertErrorInternetConnectivity(context,16,RETRY_CONNECTIVITY, true);
            }
        }else{
            showAlertErrorInternetConnectivity(context,24,RESPONSE_ERROR, false);
        }
    }

    @Override
    public void onSuccessFacilityUpdate(boolean flag) {
        otherProgressBar.setProgress(40);
        otherStatus.setText(R.string.loading_location);
        if(flag) {
            if(Utils.haveNetworkConnection(this)){
                callLocationTypeApi();
            }else{
                showAlertErrorInternetConnectivity(context,17,RETRY_CONNECTIVITY, true);
            }
        }else{
            showAlertErrorInternetConnectivity(context,16,RESPONSE_ERROR, false);
        }

    }

    /**
     * callLocationTypeApi method
     *
     */
    private void callLocationTypeApi() {
        otherProgressBar.setProgress(60);
        otherStatus.setText(R.string.location_location_level);
        if(Utils.haveNetworkConnection(this)){
            GetLocationLevelAsyncTask getLocationLevelAsyncTask = new GetLocationLevelAsyncTask(this, UpdateMasterLoading.this, this);
            getLocationLevelAsyncTask.execute();
        }else{
            showAlertErrorInternetConnectivity(context,18,RETRY_CONNECTIVITY, true);
        }
    }

    @Override
    public void onSuccessLocationLevelResponse(boolean flag) {
        otherProgressBar.setProgress(70);
        otherStatus.setText(R.string.loading_beneficiary);
        if(flag) {
            if (Utils.haveNetworkConnection(this)) {
                BeneficiaryTypeAsyncTask beneficiaryTypeAsyncTask = new BeneficiaryTypeAsyncTask(this, this, this);
                beneficiaryTypeAsyncTask.execute();
            } else {
                showAlertErrorInternetConnectivity(context, 19, RETRY_CONNECTIVITY, true);
            }
        }else{
            showAlertErrorInternetConnectivity(context,18,RESPONSE_ERROR, false);
        }


    }

    @Override
    public void onSuccessBeneficiaryResponse(String response,boolean flag) {
        ohterimagestatus.setVisibility(View.VISIBLE);
        otherProgressBar.setProgress(80);
        otherStatus.setText(R.string.facility_loading);
        if(flag) {
            if (Utils.haveNetworkConnection(this)) {
                FacilityTypeAsyncTask facilityTypeAsyncTask = new FacilityTypeAsyncTask(this, UpdateMasterLoading.this, this);
                facilityTypeAsyncTask.execute();
            } else {
                showAlertErrorInternetConnectivity(context, 20, RETRY_CONNECTIVITY, true);
            }
        }else{
            showAlertErrorInternetConnectivity(context,19,RESPONSE_ERROR, false);
        }
    }

    @Override
    public void onSuccessPeriodicResponse(String periodicResponse,boolean flag) {
        setMasterDatabaseUpdateTimeStamp();
        otherProgressBar.setProgress(90);
        otherStatus.setText(R.string.loading_thematic);
        ohterimagestatus.setVisibility(View.VISIBLE);
        if(flag) {
            if (Utils.haveNetworkConnection(this)) {
                ThematicAreaAsyncTask thematicAreaAsyncTask = new ThematicAreaAsyncTask(this, UpdateMasterLoading.this, this);
                thematicAreaAsyncTask.execute();
            } else {
                showAlertErrorInternetConnectivity(context, 22, RETRY_CONNECTIVITY, true);
            }
        }else{
            showAlertErrorInternetConnectivity(context,21,RESPONSE_ERROR, false);
        }

    }

    /**
     * setMasterDatabaseUpdateTimeStamp method
     *  this method will set the record Updated table time to shared Preference .
     *
  */
    private void setMasterDatabaseUpdateTimeStamp() {
        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        SharedPreferences.Editor editorStoreTimeStamp = sharedPreferences.edit();
        editorStoreTimeStamp.putString("UPDATE_DATETIMESTAMP", format.format(curDate));
        editorStoreTimeStamp.putBoolean("MasterTable_Flag", true);
        editorStoreTimeStamp.apply();
    }
    /**
     * getThematicAreaResponse method
     * @param flag param
     *
     */
    @Override
    public void getThematicAreaResponse(boolean flag) {
        ohterimagestatus.setVisibility(View.VISIBLE);
        otherProgressBar.setProgress(96);
        if(flag) {
            if (Utils.haveNetworkConnection(this)) {
                UpdateRegionalLanguge updateRegionalLanguge = new UpdateRegionalLanguge(UpdateMasterLoading.this, getString(R.string.regionalLanguageURLS), UpdateMasterLoading.this, otherProgressBar);
                updateRegionalLanguge.execute();
            } else {
                showAlertErrorInternetConnectivity(context, 23, RETRY_CONNECTIVITY, true);
            }
        }else{
            showAlertErrorInternetConnectivity(context,22,RESPONSE_ERROR, false);
        }
    }

    /**
     * onSuccessFaciltyResponsemethod
     * @param flag param
     *
     */
    @Override
    public void onSuccessFaciltyResponse(boolean flag) {
        if(flag) {
            if (Utils.haveNetworkConnection(this)) {
                PeriodicityAsyncTask periodicityAsyncTask = new PeriodicityAsyncTask(this, this, this);
                periodicityAsyncTask.execute();
            } else {
                showAlertErrorInternetConnectivity(context, 21, RETRY_CONNECTIVITY, true);
            }
        }else{
            showAlertErrorInternetConnectivity(context,20,RESPONSE_ERROR, false);
        }
    }

    /**
     * onSuccessfullRegionalLanguage method
     * @param result param
     * @param flag param
     *
     */
    @Override
    public void onSuccessfullRegionalLanguage(String result,boolean flag) {
        ohterimagestatus.setVisibility(View.VISIBLE);
        ohterimagestatus.setBackgroundResource(R.drawable.done);
        otherProgressBar.setProgress(98);
        otherStatus.setText(getString(R.string.completed));
        if(flag) {
            if(Utils.haveNetworkConnection(this)){
                addressProofAsyncTask=new AddressProofAsyncTask(context,this,this);
                addressProofAsyncTask.execute();
            }else{
                showAlertErrorInternetConnectivity(context,24,RETRY_CONNECTIVITY,true);
            }

        }else{
            showAlertErrorInternetConnectivity(context,23,RESPONSE_ERROR,false);
        }
    }

    /**
     * showAlertErrorInternetConnectivity method
     * @param context param
     * @param apiCount param
     * @param message param
     * @param flag param
     *
     */
    public  void showAlertErrorInternetConnectivity(final Context context, final int apiCount, String message, final boolean flag){

        try{
            String title = "";
            if (flag)
                title = "No Internet";
            else
                title = "Unable to process";
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle(title);
            builder.setMessage(message);

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            builder.setPositiveButton("Retry", (dialog, which) -> {
                dialog.dismiss();
                if (Utils.haveNetworkConnection(context)) {
                    sendApiCount(apiCount);
                } else {
                    showAlertErrorInternetConnectivity(context, apiCount, RETRY_CONNECTIVITY, true);
                }
            });
            if(isFinishing()||context!=null){
                AlertDialog dialog = builder.create(); // calling builder.create after adding buttons
                dialog.show();
            }
        }catch (Exception e){
            Logger.logE("","Exception on displaying the popup",e);
        }
    }

    /**
     * sendApiCount method
     * @param apiCount param
     *
     */
    private  void sendApiCount(int apiCount) {
        Logger.logV(TAG,"apiCount"+apiCount);
        switch (apiCount){
            case 1:
                new SurveyListAsyncTask(UpdateMasterLoading.this, UpdateMasterLoading.this, UpdateMasterLoading.this, String.valueOf(uId), progressBarLocation, locationStatus).execute();
                break;
            case 2:
                new LevelsAsyncTask(UpdateMasterLoading.this, UpdateMasterLoading.this,UpdateMasterLoading.this,uId,progressBarLocation,UpdateMasterLoading.this,locationStatus).execute();
                break;
            case 3:
                new BeneficiaryAsyncTaskLoading(context, activity, UpdateMasterLoading.this, String.valueOf(uId), progressBarBeneficiary).execute();
                break;
            case 4:
                updatedTablesAsynTask = new UpdatedTablesAsynTask(context, activity, UpdateMasterLoading.this, beneficiaryDatacolledtionProgressbar, beneficiaryDatacolledtionText);
                updatedTablesAsynTask.execute();
                break;
            case 5:
                new BlockAsyncTask(context, activity, UpdateMasterLoading.this).execute();
                break;
            case 6:
                new LanguageBlockAsyncTask(context, activity, UpdateMasterLoading.this).execute();
                break;
            case 7:
                new QuestionsAsyncTask(context, activity, UpdateMasterLoading.this).execute();
                break;
            case 8:
                new LanguageQuestionsAsyncTask(context, activity, UpdateMasterLoading.this).execute();
                break;
            case 9:
                new AssessmentsAsyncTask(context, activity, UpdateMasterLoading.this).execute();
                break;
            case 10:
                new LanguageAssessmentAsyncTask(context, activity, UpdateMasterLoading.this).execute();
                break;
            case 11:
                new SkipMandatoryAsyncTask(context, activity, UpdateMasterLoading.this).execute();
                break;
            case 12:
                new SkipRulesAsyncTask(context, activity, UpdateMasterLoading.this).execute();
                break;
            case 13:
                new OptionsAsyncTask(context, activity, UpdateMasterLoading.this).execute();
                break;
            case 14:
                new LanguageLabelsAsyncTask(context, activity, UpdateMasterLoading.this).execute();
                break;
            case 15:
                new LanguageOptionsAsyncTask(context, activity, UpdateMasterLoading.this).execute();
                break;
            case 16:
                new FacilitiesListAsyncTaskLoading(this, this, this).execute();
                break;
            case 17:
                callLocationTypeApi();
                break;
            case 18:
                new GetLocationLevelAsyncTask(this,UpdateMasterLoading.this,this).execute();
                break;
            case 19:
                new BeneficiaryTypeAsyncTask(this, this, this).execute();
                break;
            case 20:
                new FacilityTypeAsyncTask(this,UpdateMasterLoading.this,this).execute();
                break;
            case 21:
                new PeriodicityAsyncTask(this,this,this).execute();
                break;
            case 22:
                new ThematicAreaAsyncTask(this,UpdateMasterLoading.this,this).execute();
                break;
            case 23:
                new UpdateRegionalLanguge(UpdateMasterLoading.this, getString(R.string.regionalLanguageURLS), UpdateMasterLoading.this,otherProgressBar).execute();
                break;
            case 24:
                serviceListAsyncTaskLoading= new ServiceListAsyncTaskLoading(this,UpdateMasterLoading.this,this);
                serviceListAsyncTaskLoading.execute();
                break;
            case 25:
                addressProofAsyncTask=new AddressProofAsyncTask(this,this,this);
                addressProofAsyncTask.execute();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        try{
            switch (view.getId()){
                case R.id.helptext:
                    PopUpShow.showingErrorPopUp(UpdateMasterLoading.this, defaultPreferences.getString("UID", ""));
                    break;
                case R.id.logout:
                    Utils.callDialogConformation(this,this);
                    break;
                default:
                    break;
            }
        }catch (Exception e){
            Logger.logE(TAG,"click listener ---",e);
        }
    }

    /**
     * saveAddressToPreference method
     * @param flag param
     */
    @Override
    public void saveAddressToPreference(boolean flag) {
        if(flag){
            setMasterDatabaseUpdateTimeStamp();
            Intent intent = new Intent(this, HomeActivityNew.class);
            startActivity(intent);
            finish();
        }else{
            showAlertErrorInternetConnectivity(context,24,RESPONSE_ERROR,flag);
        }
    }

    public void runProgressBar() {
        bar = findViewById(R.id.progressBar1);
        new Thread(() -> {
            while (value < 100) {
                value++;
                try {
                    Thread.sleep(20);
                } catch (Exception e) {
                    Logger.logE(TAG,"runProgressBar ---",e);
                }
                handler.post(() -> bar.setProgress(value));
            }
        }).start();
    }
}