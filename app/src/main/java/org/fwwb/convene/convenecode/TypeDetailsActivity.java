package org.fwwb.convene.convenecode;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import net.sqlcipher.database.SQLiteDatabase;

import org.fwwb.convene.R;
import org.fwwb.convene.convenecode.adapter.PagerAdapter;
import org.fwwb.convene.convenecode.api.MeetingAPIs.BeneficiaryAsyncTask;
import org.fwwb.convene.convenecode.database.ConveneDatabaseHelper;
import org.fwwb.convene.convenecode.database.DataBaseMapperClass;
import org.fwwb.convene.convenecode.network.ClusterToTypo;
import org.fwwb.convene.convenecode.network.LangService;
import org.fwwb.convene.convenecode.utils.ClusterInfo;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.convenecode.utils.ToastUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TypeDetailsActivity extends BaseActivity implements View.OnClickListener,ClusterToTypo,PopupMenu.OnMenuItemClickListener
{
    TextView toolbarTitle;
    Toolbar toolbar;
    LinearLayout backPress;
    LinearLayout layout;
    String beneficiaryArray;
    String beneficiaryName;
    String locationName;
    String locationId;
    String btype;
    String beneficiaryType;
    String parentId;
    LinearLayout linearContainer;
    String id;
    View view;
    String beneficiaryTypeId;
    String boundaryLevel;
    ImageView backArrowImageView;
    boolean isStartSurvey=false;
    private static final String TAG = "TypeDetailActivity";
    SharedPreferences.Editor editor;
    private static final String BENEFICIARIES_TITLE ="Beneficiaries";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Bundle bl;
    AlertDialog alert1;
    private TextView beneficiaryNameTextView;
    private BeneficiaryReceiver beneficiryReceiver;
    private IntentFilter filter;
    private SharedPreferences defaultPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);
        initVariables();
        SQLiteDatabase.loadLibs(getApplicationContext());
        beneficiryReceiver = new BeneficiaryReceiver();
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        toolbarTitle.setTypeface(customFont);
        isStartSurvey=false;
        tabLayout.addTab(tabLayout.newTab().setText("Data Collection Forms"));
        tabLayout.addTab(tabLayout.newTab().setText("Details"));
        filter = new IntentFilter("BeneficiaryIntentReceiver");
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        setAllViews();
        editor= defaultPreferences.edit();
        editor.putBoolean("UpdateFacilityUi",false);
        editor.putBoolean("UpdateUi",false);
        editor.apply();
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),bl);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
             Logger.logV(TAG,"tab1 not selected");
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Logger.logV(TAG,"tab2 not selected");
            }
        });
        backArrowImageView.setOnClickListener(view -> onBackPressed());
    }

    public void popUpMenu() {
        Context wrapper = new ContextThemeWrapper(getApplication(), R.style.AppTheme);
        PopupMenu popup = new PopupMenu(wrapper, findViewById(R.id.imageMenu));
        popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
        popup.getMenu().getItem(0).setTitle("Choose Language");
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }


    private void initVariables() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.pager);
        linearContainer= findViewById(R.id.linearContainer);
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        backArrowImageView= findViewById(R.id.imageBack);
        toolbar= findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        toolbarTitle= findViewById(R.id.toolbarTitle);
        beneficiaryNameTextView= findViewById(R.id.beneficiaryName);
        ImageView menuOption = findViewById(R.id.imageMenu);
        menuOption.setOnClickListener(view -> popUpMenu());
    }

    @Override
    protected void onResume() {
        super.onResume();
        copySurveyDetails();
        setAllViews();
        registerReceiver(beneficiryReceiver, filter);
    }

    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(beneficiryReceiver);
            super.onDestroy();
        }catch (Exception e){
            Logger.logE(TAG," Exception on unregisterReceiver ",e);
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void copySurveyDetails() {
        SharedPreferences myAppPreference = PreferenceManager.getDefaultSharedPreferences(TypeDetailsActivity.this);
        Logger.logD(TAG, "Save data==" + myAppPreference.getBoolean(Constants.SAVE_DATA, false));
        if (myAppPreference.getBoolean(Constants.SAVE_DATA, false)) {
            String packageName = getApplicationContext().getPackageName();
            String dbPath = String.format(getString(R.string.databasepath), packageName);
            String dbName = "ENCRYPTED.db";
            final String inFileName = dbPath + dbName;
            File dbFile = new File(inFileName);
            if (dbFile.exists()) {
                String outFileName = getFilename();
                    try(FileInputStream fis = new FileInputStream(dbFile);FileOutputStream output = new FileOutputStream(outFileName)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) > 0) {
                            output.write(buffer, 0, length);
                        }
                        SupportClass supportClass=new SupportClass();
                        supportClass.setSaveData(false, TypeDetailsActivity.this);
                        output.flush();
                        output.close();
                        fis.close();
                        Toast.makeText(getApplicationContext(), defaultPreferences.getString(ClusterInfo.Data_copied_successfully, ""), Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                      Logger.logE("","",e);
                    }
            }
        }
    }

    @Override
    public void callTypoScreen(boolean flag) {
        Logger.logD(TAG,"Beneficiary listing api has been called and updated");
    }

    @Override
    public void surveyListSuccess(boolean flag) {
        Logger.logD(TAG,"Beneficiary listing api in typedetails activity has been called and updated");
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.changeLanguage:
                chooseLanguage();
                break;
            default:
                break;
        }
        return false;
    }

    private void chooseLanguage() {
        try{
            final SharedPreferences.Editor syncSurveyEditor = defaultPreferences.edit();
            ConveneDatabaseHelper conveneDatabaseHelper = ConveneDatabaseHelper.getInstance(this, defaultPreferences.getString(Constants.CONVENE_DB, ""), defaultPreferences.getString("uId", ""));
            android.database.sqlite.SQLiteDatabase homepageDatabase = conveneDatabaseHelper.getWritableDatabase();
           final List<String> getRegionalLanguage= DataBaseMapperClass.getRegionalLanguage(homepageDatabase, -1);
            List<String> getLanguageName=new ArrayList<>();
            if (!getRegionalLanguage.isEmpty()) {
                for (int i = 0; i < getRegionalLanguage.size(); i++) {
                    String[] getlanguageNameTemp = getRegionalLanguage.get(i).split("@");
                    getLanguageName.add(getlanguageNameTemp[1]);
                }
            }else{
                getLanguageName.add("English");
            }
            DialogInterface.OnClickListener dialogInterface = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setLanguageSelection(which,getRegionalLanguage,syncSurveyEditor);
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.select));
            builder.setSingleChoiceItems(getLanguageName.toArray(new String[getRegionalLanguage.size()]), defaultPreferences.getInt(Constants.SELECTEDVALUE,0), dialogInterface);
            alert1 = builder.create();
            alert1.show();
        }catch (Exception e){
            Logger.logE("ExceptionTag","ExceptionTag",e);
        }

    }

    private void setLanguageSelection(int which, List<String> getRegionalLanguage,SharedPreferences.Editor syncSurveyEditor) {
        String language = getRegionalLanguage.get(which);
        String[] languageSpilt= language.split("@");
        syncSurveyEditor.putInt(Constants.SELECTEDLANGUAGE, Integer.parseInt(languageSpilt[0]));
        syncSurveyEditor.putInt(Constants.SELECTEDVALUE,which);
        syncSurveyEditor.putString(Constants.SELECTEDLANGUAGELABEL,languageSpilt[1]);
        BaseActivity.setLocality(1,this);
        alert1.cancel();
        syncSurveyEditor.apply();
        ToastUtils.displayToastUi(languageSpilt[1] + " is selected",this);
        Intent intent = new Intent(this, LangService.class);
        startService(intent);
    }





    public class BeneficiaryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                new BeneficiaryAsyncTask(context, TypeDetailsActivity.this, TypeDetailsActivity.this, defaultPreferences.getString("uId", "")).execute();            } catch (Exception e) {
                Logger.logE(ListingActivity.class.getSimpleName(), "Exception in SyncSurveyActivity  Myreceiver class  ", e);
            }
        }
    }

    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, "DataBase_Backup");
        if (!file.exists()) {
            file.mkdirs();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateAndTime = sdf.format(new Date());

        return file.getAbsolutePath() + "/database_1.9_" + currentDateAndTime + ".db";
    }

    private void setAllViews() {
        try {
            bl= getIntent().getExtras();
            String beneficiaryTypeValue;
            if(bl!=null){
                beneficiaryArray=bl.getString("beneficiaryArray");
                if(beneficiaryArray==null){
                    beneficiaryArray="";
                }
                beneficiaryName=bl.getString("beneficiaryName");
                locationName=bl.getString("locationName");
                parentId=bl.getString("parent_id");
                id=bl.getString("id");
                beneficiaryTypeId=bl.getString("beneficiary_type_id");
                locationId=bl.getString("location_id");
                boundaryLevel=bl.getString("boundary_level");
                btype=bl.getString("typeHeaderName");
                beneficiaryType=bl.getString("beneficiary_type");
                beneficiaryTypeValue = bl.getString("typeValue");
            }else{
                beneficiaryArray= defaultPreferences.getString(Constants.BENEFICIARY_ARRAY,"");
                boundaryLevel= defaultPreferences.getString(Constants.CLUSTER_NAME,"");
                locationId= defaultPreferences.getString(Constants.LOCATION_ID,"");
                locationName= defaultPreferences.getString(Constants.CLUSTER_NAME,"");
                beneficiaryName= defaultPreferences.getString(Constants.BENEFICIARY_NAME,"");
                beneficiaryType= defaultPreferences.getString("beneficiary_type","");
                btype= defaultPreferences.getString("typeName","");
                beneficiaryTypeValue = defaultPreferences.getString(Constants.TYPE_VALUE,"");
                if((BENEFICIARIES_TITLE).equals(btype)){
                    beneficiaryTypeId= defaultPreferences.getString("beneficiary_type_id","");
                }else{
                    beneficiaryTypeId= defaultPreferences.getString("facility_type_id","");
                }
            }
            toolbarTitle.setText(beneficiaryTypeValue + " - " + beneficiaryName);
            beneficiaryNameTextView.setText(beneficiaryName);
            beneficiaryNameTextView.setTypeface(Typeface.DEFAULT_BOLD);
            Logger.logV(TAG,"Selected beneficiary type is ==> " + beneficiaryType);
        }catch (Exception e){
            Logger.logE("","" , e);
        }

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.backPress)
            onBackPressed();
    }

}
