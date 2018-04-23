package org.mahiti.convenemis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import org.mahiti.convenemis.BeenClass.facilities.FacilityListInterface;
import org.mahiti.convenemis.BeenClass.service.ServiceListInterface;
import org.mahiti.convenemis.api.BeneficiaryApis.BeneficaryTypeInterface;
import org.mahiti.convenemis.api.BeneficiaryApis.BeneficiaryTypeAsyncTask;
import org.mahiti.convenemis.api.BeneficiaryApis.GetLocationLevelAsyncTask;
import org.mahiti.convenemis.api.BeneficiaryApis.GetLocationLevelInterface;
import org.mahiti.convenemis.api.BeneficiaryApis.ServiceListAsyncTaskLoading;
import org.mahiti.convenemis.api.FacilitiesListAsyncTask;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.utils.CheckNetwork;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.ProgressUtils;
import org.mahiti.convenemis.utils.ToastUtils;
import org.mahiti.convenemis.utils.Utils;

public class LoginParentActivity extends BaseActivity implements BeneficaryTypeInterface, FacilityListInterface, ServiceListInterface, GetLocationLevelInterface {
    private static final int REQUESTCODE_SURVEYLISTLEVEL = 201;
    private static String checkConnectivity = "Please check Internet Connection";
    DBHandler loginHandler;
    private SharedPreferences loginPreferences;
    private int uID;
    android.app.ProgressDialog loginDialog;
    CheckNetwork checkNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // Fabric.with(this, new Crashlytics());
        setContentView(R.layout.typologyselection);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        loginPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        checkNetwork = new CheckNetwork(this);

        try {
            net.sqlcipher.database.SQLiteDatabase.loadLibs(LoginParentActivity.this);
            loginHandler = new DBHandler(this);

        } catch (Exception e) {
            Logger.logE(LoginParentActivity.class.getSimpleName(), "Exception in SurveyLoginActivity onCreate method ", e);
        }


        if (!"".equals(loginPreferences.getString("uId", "")) && !"0".equals(loginPreferences.getString("UID", ""))) {
            if (loginPreferences.getBoolean("MasterTable_Flag", false)) {
                Intent intent = new Intent(LoginParentActivity.this, HomeActivityNew.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Intent ins = new Intent(LoginParentActivity.this, UpdateMasterLoading.class);
                ins.putExtra("url", getString(R.string.main_url));
                ins.putExtra("uId", uID);
                startActivityForResult(ins, 206);
                finish();
            }


        } else {
            PackageManager manager = this.getPackageManager();
            PackageInfo info;
            try {
                info = manager.getPackageInfo(this.getPackageName(), 0);
                int version = info.versionCode;
                Logger.logD("Calling Home screen acticity", "calling login activity");
                Intent in = new Intent(LoginParentActivity.this, LoginActivity.class);
                in.putExtra("urlLogin", getString(R.string.main_url) + getString(R.string.appLogin));
                in.putExtra("appVersion", version);
                startActivityForResult(in, 200);
            } catch (PackageManager.NameNotFoundException e) {
                Logger.logE("", "", e);
            }

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("BackToParent", "onActivityResult, requestCode: " + requestCode + ", resultCode: " + resultCode + data);

        if(data==null){
            return;
        }
        if (requestCode == 200) {
            methodToRedirectToUploadingDb(data);
        }

        if (requestCode == REQUESTCODE_SURVEYLISTLEVEL) {
            methodToRedirectToSurveyLevels(data);
        }
        if (requestCode == 206) {
            try {
                ProgressUtils.CancelProgress(loginDialog);
                /*calling Service listing Api*/
                if (Utils.haveNetworkConnection(this)) {
                    new ServiceListAsyncTaskLoading(this, LoginParentActivity.this, this).execute();
                } else {
                    ToastUtils.displayToast(checkConnectivity, this);
                }
            } catch (Exception e) {
                Logger.logE("Exception", "Exception in SurveyListActivity", e);
            }
        }
    }

    /**
     * methodToRedirectToSurveyLevels method
     * @param data param
     *
     */
    private void methodToRedirectToSurveyLevels(Intent data) {
        try {

             /*  *//**//* *//**//**//**//**//**//**//**//*code to get the Questions based on Api*//**//**//**//**//**//**//**//**//**//**/
            Bundle bundle = data.getExtras();
            String surveylevelDBPath = bundle.getString("SurveyLevelDbPath");
            Logger.logV("the data file name is SurveylevelDBPath", "the db file SurveylevelDBPath" + surveylevelDBPath);
            SharedPreferences.Editor editor = loginPreferences.edit();
            editor.putString("SurveylevelDBPath", surveylevelDBPath);
            editor.apply();
            Utils.copyEncryptedDataBase(LoginParentActivity.this,loginPreferences);
            loginDialog = ProgressUtils.showProgress(LoginParentActivity.this, false, getString(R.string.surveyListProgress) + "...");
            loginDialog.show();
        } catch (Exception e) {
            Logger.logE("Exception", "Exception in SurveyListActivity", e);
        }
    }

    /**
     * methodToRedirectToUploadingDb method
     * @param data param
     */
    private void methodToRedirectToUploadingDb(Intent data) {
        try {
            BaseActivity.setLocality(loginPreferences.getInt("selectedLanguage", 0), LoginParentActivity.this);
            Logger.logV("Language selected ", "Language selected in login" + loginPreferences.getInt("selectedLanguage", 0));
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                uID = bundle.getInt("uId");
                String userName = bundle.getString("user_name");
                int partnerId = bundle.getInt("partner_id");
                Logger.logV("Username", "UserName" + userName);


                //  code to set the alaram serviec
                int alaramFrequency = 2;
                Logger.logV("the alaram value is", "the alarm value is" + alaramFrequency);
                SharedPreferences.Editor editor = loginPreferences.edit();
                editor.putInt("alaramFrequency", alaramFrequency);
                String alarmStarted = "AlarmStarted";
                editor.putBoolean(alarmStarted, true);
                editor.putString("userName", userName);
                editor.putInt("partner_id", partnerId);
                editor.apply();


                if (loginPreferences.getBoolean(alarmStarted, true)) {
                    Logger.logV("the alaram started ...........", "alaram staters.....................");
                    AlarmService recoAlarm = new AlarmService();
                    recoAlarm.SetAlarm(LoginParentActivity.this);
                    loginPreferences.edit().putBoolean(alarmStarted, true).apply();
                }
                Intent intent = new Intent(LoginParentActivity.this, UpdateMasterLoading.class);
                intent.putExtra("url", getString(R.string.main_url));
                intent.putExtra("urlBeneficiary", getString(R.string.main_Url));
                intent.putExtra("uId", uID);
                startActivityForResult(intent, REQUESTCODE_SURVEYLISTLEVEL);
                editor.putString("uId", String.valueOf(uID));
                editor.putString("UID", String.valueOf(uID));
                editor.apply();
                finish();
            }
        } catch (Exception e) {
            Logger.logE("", "", e);
            finish();
        }
    }



    @Override
    public void onSuccessFacilityUpdate(boolean flag) {
        if (flag && Utils.haveNetworkConnection(this)) {
            new GetLocationLevelAsyncTask(this, LoginParentActivity.this, this).execute();
        }
    }

    @Override
    public void onSuccessServiceUpdate(boolean flag) {
        if (flag && Utils.haveNetworkConnection(this)) {
            new FacilitiesListAsyncTask(this, this, this).execute();
        } else {
            ToastUtils.displayToast(checkConnectivity, this);
        }
    }

    @Override
    public void onSuccessLocationLevelResponse(boolean flag) {
        if (Utils.haveNetworkConnection(this)) {
            new BeneficiaryTypeAsyncTask(this, this, this).execute();
        } else {
            ToastUtils.displayToast(checkConnectivity, this);
        }
    }

    @Override
    public void onSuccessBeneficiaryResponse(String response,boolean flag) {
        Intent intent = new Intent(this, HomeActivityNew.class);
        startActivity(intent);
        finish();
    }


}