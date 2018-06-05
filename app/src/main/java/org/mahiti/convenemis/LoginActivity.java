package org.mahiti.convenemis;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.mahiti.convenemis.api.LoginApi;
import org.mahiti.convenemis.api.SignInDetails;
import org.mahiti.convenemis.database.ConveneDatabaseHelper;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.database.UserDetailsDb;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.GetMD5Key;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.ProgressUtils;
import org.mahiti.convenemis.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, SignInDetails {
    EditText edtUserName;
    EditText edtPassword;
    TextView btnLogin;
    String url = "";
    ProgressDialog progressDialog;
    private static final String USER_DETAILS_TABLE ="UserDetailsTable";
    private static final String dateformat = "yyyy-MM-dd HH:mm:ss";
    List<String> list;
    int value;
    TextView loginDetailsLabel;
    SharedPreferences preferences;
    private long backPressed =0;
    private int appVerstion;
    private boolean isUserConfirm=true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        initVariables();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        progressDialog = ProgressUtils.showProgress(this, false, "Please wait...");
        list = new ArrayList<>();
        value = preferences.getInt(Constants.SELECTEDLANGUAGE, 0);
        Logger.logV("the languages are", "the languages " + list);
        if(getIntent() != null) {
            url = getIntent().getStringExtra("urlLogin");
        }
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    this.getPackageName(), 0);
            appVerstion = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.logD(" sc card ","E"+e);
        }
    }

   private void initVariables() {
        Typeface face = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Light.ttf");
        edtUserName = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        edtPassword.setTransformationMethod(new PasswordTransformationMethod());
        edtUserName.setTypeface(face);
        edtPassword.setTypeface(face);

        this.btnLogin = findViewById(R.id.btnLogin);
       if (btnLogin != null) {
            btnLogin.setOnClickListener(this);
        }
    }

    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.btnLogin) {
            callLoginButton();
        }
    }
    @Override
    public void onBackPressed() {
        if (backPressed + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        else
            Toast.makeText(getBaseContext(), getString(R.string.pressToExit), Toast.LENGTH_SHORT).show();
        backPressed = System.currentTimeMillis();
    }



    private void callLoginButton() {
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt(Constants.SELECTEDLANGUAGE,1);
        editor.putString(Constants.SELECTEDLANGUAGELABEL,"English");
        editor.apply();
        if(Utils.haveNetworkConnection(this)) {
            signInDetails();
        } else {
            UserDetailsDb userDetailsDb = new UserDetailsDb(LoginActivity.this);

            String md5 = edtUserName.getText().toString().trim().concat(edtPassword.getText().toString().trim());
            String md5Key = new GetMD5Key(LoginActivity.this).md5(md5);
            List<HashMap<String, String>> userList = userDetailsDb.getUserDetails(md5Key);
            if (!userList.isEmpty()) {
                Logger.logD("====", "userDetailsDb.getUserId(Constants.CONVENE_USER_TABLE, md5)" + userDetailsDb.getUserId(Constants.CONVENE_USER_TABLE, md5Key));
                if (!userList.isEmpty()){
                    HashMap<String, String> offlineLoginDetail=userList.get(0);
                    String loginResult=offlineLoginDetail.get("loginData");
                    callActivity(userDetailsDb.getUserName(USER_DETAILS_TABLE, md5Key),loginResult);
                }else{
                    Toast.makeText(this, "Please check internet connection", Toast.LENGTH_SHORT).show();
                }

            } else
                Toast.makeText(this, "Please check internet connection", Toast.LENGTH_SHORT).show();

        }

    }

    protected void signInDetails() {
        String name = this.edtUserName.getText().toString().trim();
        String passwordEdit = this.edtPassword.getText().toString().trim();
        if(TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Enter valid user name", Toast.LENGTH_LONG).show();
        } else if(TextUtils.isEmpty(passwordEdit)) {
            Toast.makeText(this, "Enter valid password", Toast.LENGTH_LONG).show();
        } else if(passwordEdit.length() <= 3) {
            Toast.makeText(this, "Enter minimum 3 characters", Toast.LENGTH_LONG).show();
        } else {
            Logger.logD("login", "url -- " + this.url);
            LoginApi.callLoginApi(this.url, name, passwordEdit, this, this.progressDialog, this.preferences, this);
        }
    }

    public void callActivity(String userName, String response) {
        Logger.logV("the Login Result", "LoginResult"+response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int userActiveStatus = jsonObject.getInt("activeStatus");
            int forceLogoutStatus = jsonObject.getInt("forceLogout");
            if (userActiveStatus == 2 && forceLogoutStatus==0) {
                Logger.logV("LOG_TAG", "perform from home page ");
                JSONObject jsonobjectapkresponse = new JSONObject(response);
                JSONObject UpdateAPKOBject=jsonobjectapkresponse.getJSONObject("updateAPK");
                boolean updateLeveflag = false;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("UPDATE_LEVEL_FLAG", updateLeveflag);
                if (UpdateAPKOBject.getInt(Constants.APPVERSION)>appVerstion){
                    editor.putInt(Constants.UPDATEDAPKVERSION,UpdateAPKOBject.getInt(Constants.APPVERSION));
                    editor.putString("UPDATE_APK_RESPONSE",UpdateAPKOBject.toString());
                    checkandSetPreference(UpdateAPKOBject,editor);
                }else{
                    Logger.logD(Constants.UPDATEDAPKVERSION,"No Version change");
                    editor.putInt(Constants.UPDATEDAPKVERSION,appVerstion);
                }
                editor.apply();
                isUserExist(String.valueOf(jsonObject.getInt("uId")),userName,jsonObject.getInt(Constants.PARTNER_ID));
            }else{
                Toast.makeText(getApplicationContext(),"User not activated", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Logger.logD("Exception","E"+e);
        }
    }

    private boolean isUserExist(final String presentUserId,final String userName, final int partnerId) {
        try{
            Logger.logD("presentUserID","presentUserID"+presentUserId);
            final String[] getPreviousUserNameandID=preferences.getString("UsernamePartnerID","").split("#");
            if (getPreviousUserNameandID.length>1) {
                Logger.logD("previousUserID", "previousUserID" + getPreviousUserNameandID[1]);
                if (!String.valueOf(partnerId).equals(getPreviousUserNameandID[1])) {
                    Logger.logD("AlertNeed", "User differ");
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this,R.style.AlertDialogCustom);
                    alertDialogBuilder.setTitle("User account change, will remove previous login offline records").setMessage("").setPositiveButton(R.string.CLEAR, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            isUserConfirm = false;
                            storePreviousUserTablesToSD(getPreviousUserNameandID[0]);
                            callNextFunctionality(presentUserId,partnerId, userName);
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            isUserConfirm = true;
                        }
                    }).show();
                } else {
                    isUserConfirm = true;
                    callNextFunctionality(presentUserId,partnerId,userName);
                }
            }else{
                callNextFunctionality(presentUserId,partnerId,userName);
            }
        }catch (Exception e){
            Logger.logD("isUserExist","in the "+e);
        }
        return isUserConfirm;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void storePreviousUserTablesToSD(String previousUserName) {
        boolean isCopied=Utils.previousUserCopyEncryptedDataBase(LoginActivity.this,"SurveyLoading",preferences.getString(Constants.DBNAME,""),previousUserName,dateformat);
        Logger.logD("LoginActivity","backup of database" + isCopied);
        boolean isDbHandlerCopied=Utils.previousUserCopyEncryptedDataBase(LoginActivity.this,"SurveyLoading","ENCRYPTED.db",previousUserName,dateformat);
        if (isCopied && isDbHandlerCopied) {
            DBHandler loginActivityhandler = new DBHandler(this);
            loginActivityhandler.deleteAllRecord(loginActivityhandler);
            ExternalDbOpenHelper externalDbOpenHelper=ExternalDbOpenHelper.getInstance(this,preferences.getString(Constants.DBNAME,""),preferences.getString("uId",""));
            externalDbOpenHelper.deleteAllRecords();
            ConveneDatabaseHelper conveneDatabaseHelper=ConveneDatabaseHelper.getInstance(this,preferences.getString(Constants.CONVENE_DB,""),preferences.getString("uId",""));
            conveneDatabaseHelper.deleteAllRecords();
        }

    }


    private void callNextFunctionality(String presentUserId, int partnerId, String userName) {
        int alaramFrequency = 2;
        Logger.logV("the alaram value is", "the alarm value is" + alaramFrequency);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("alaramFrequency", alaramFrequency);
        editor.putBoolean("AlarmStarted", true);
        editor.putString("user_name",edtUserName.getText().toString());
        editor.putString("uId", presentUserId);
        editor.putString("UID", presentUserId);
        editor.putInt(Constants.PARTNER_ID,partnerId);
        editor.putString("UserName", edtUserName.getText().toString());
        editor.putInt("selectedLangauge", 1);
        editor.putString("urlBeneficiary", getString(R.string.main_url_Beneficiary));
        editor.putString("url", getString(R.string.main_url));
        editor.apply();


        Intent intent = new Intent(LoginActivity.this, UpdateMasterLoading.class);
        intent.putExtra("url", getString(R.string.main_url));
        intent.putExtra("uId", presentUserId);
        intent.putExtra("user_name",userName);
        intent.putExtra(Constants.PARTNER_ID,partnerId);
        intent.putExtra("urlBeneficiary", getString(R.string.main_url_Beneficiary));
        intent.putExtra("uId", preferences.getString("uId",""));
        intent.putExtra("selectedLanguage", 1);
        startActivity(intent);
        finish();
    }

    private void checkandSetPreference(JSONObject updateAPKOBject, SharedPreferences.Editor editor) {
        try{
            if ("True".equalsIgnoreCase(updateAPKOBject.getString("forceUpdate"))) {
                editor.putBoolean("UPDATE_APK",true);
            }else{
                editor.putBoolean("UPDATE_APK",false);
            }
        }catch (Exception e){
            Logger.logD("checkandSetPreference","in the "+e);
        }
    }


    public void signingDetails(int uid, int languagesId, String userName, int partnerId, String response) {
        callActivity(userName,response);
    }
}