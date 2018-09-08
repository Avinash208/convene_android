package org.fwwb.convene.convenecode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.fwwb.convene.R;
import org.json.JSONObject;
import org.fwwb.convene.convenecode.BeenClass.parentChild.Level7;
import org.fwwb.convene.convenecode.BeenClass.parentChild.LevelBeen;
import org.fwwb.convene.convenecode.BeenClass.parentChild.LocationSurveyBeen;
import org.fwwb.convene.convenecode.adapter.LocationBasedFormAdapter;
import org.fwwb.convene.convenecode.database.ConveneDatabaseHelper;
import org.fwwb.convene.convenecode.database.DataBaseMapperClass;
import org.fwwb.convene.convenecode.database.ExternalDbOpenHelper;
import org.fwwb.convene.convenecode.database.SurveyControllerDbHelper;
import org.fwwb.convene.convenecode.location.GPSTracker;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.convenecode.utils.PreferenceConstants;
import org.fwwb.convene.convenecode.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by mahiti on 1/12/16.
 */
public class LevelsActivityNew extends BaseActivity implements AdapterView.OnItemSelectedListener, PopupMenu.OnMenuItemClickListener {
    SharedPreferences sharedPreferences;
    SharedPreferences levelsSharedpreferences;
    ExternalDbOpenHelper dbhelper;
    Button continueButton;
    Activity activity;
    Context con;
    String levels;
    String labels;
    String surveyId;
    ArrayAdapter<LevelBeen> adapter;
    TextView level1Text;
    TextView level2Text;
    TextView level3Text;
    TextView level4Text;
    TextView level5Text;
    TextView level6Text;
    Spinner level1Spinner;
    Spinner level2Spinner;
    Spinner level3Spinner;
    Spinner level4Spinner;
    Spinner level5Spinner;
    Spinner level6Spinner;

    String[] orderLabels;
    private String[] orderLeves;
    List<LevelBeen> list = new ArrayList<>();
    String levelASCDSC = "";
    List<Spinner> spinnerList = new ArrayList<>();
    List<TextView> lablesList = new ArrayList<>();
    String checkAvailable = "";
    GPSTracker gpsTracker;
    Level7 level1List;

    List<String> villageList;
    private static final String MY_PREFS_NAME = "MyPrefs";
    private SharedPreferences prefs;
    Spinner beneficiarySpinner;

    private static final String MYPREFERENCES = "MyPrefs";

    private LinearLayout levelayout3;
    String typeName;
    private AlertDialog alertDialog;
    private ListView hamletListview;
    private TextView emptyTextView;
    Bundle extras;
    private LocationBasedFormAdapter locationBasedFormAdapter;
    private Map<Integer ,LocationSurveyBeen> locationListBean = new HashMap<>();
    private List<LocationSurveyBeen> locationBenListForAdapter  = new ArrayList<>();

    PeriodicReceiver periodicReceiver;
    IntentFilter periodicityFilter;

    LevelBeen selectedBeen;
    ProgressBar progressBarLoc;

    public TextView selectedLangText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.levelsnew);
        fillIds();
        con = LevelsActivityNew.this;
        activity = LevelsActivityNew.this;
        periodicReceiver=new PeriodicReceiver();
        periodicityFilter = new IntentFilter("Survey");
        progressBarLoc = findViewById(R.id.progressBarLoc);
        selectedLangText= findViewById(R.id.selectedLangText);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LevelsActivityNew.this);
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        levelsSharedpreferences = getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        Logger.logD("Tittle", "survey tittle selected" + prefs.getString("Survey_tittle", null));
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(prefs.getString("Survey_tittle", null));
        ImageView pressBack = findViewById(R.id.imageBack);
        pressBack.setOnClickListener(v -> onBackPressed());
        extras = getIntent().getExtras();
        if (extras != null) {
            typeName = extras.getString("typeName");
            surveyId=String.valueOf(extras.getInt("survey_id"));
        }else{
            surveyId = String.valueOf(prefs.getInt("survey_id", 0));
        }

        Logger.logV("LevelActivity", "Survey id of selected survey" + surveyId);
        dbhelper = ExternalDbOpenHelper.getInstance(LevelsActivityNew.this, sharedPreferences.getString(Constants.DBNAME, ""), sharedPreferences.getString("inv_id", ""));
      //  levels = dbhelper.getOrderLevels(Integer.parseInt(surveyId));
        levels = "level5,level6";
     //   labels = dbhelper.getOrderlabels(Integer.parseInt(surveyId));
        labels = "gramapanchayath,village";
        villageList = new ArrayList<>();
        orderLabels = labels.split(",");
        orderLeves = levels.split(",");
        String surveyLevels = Arrays.toString(orderLeves);
        String levelsOrders = surveyLevels.substring(1, surveyLevels.length() - 1);

        if (!("").equals(levelsOrders)) {
            setSpinners(surveyId);
        } else {
            Toast.makeText(LevelsActivityNew.this, "Sorry! Levels are empty", Toast.LENGTH_SHORT).show();
            finish();
        }
        ImageView menuOption = findViewById(R.id.imageMenu);
        menuOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpMenu();
            }
        });

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PreferenceConstants.typologyId, sharedPreferences.getString(PreferenceConstants.parentTypologyId, ""));
        editor.putBoolean(PreferenceConstants.surveySkipFlag, false);
        editor.apply();
        setLanguage();
        continueButton.setOnClickListener(v -> runOnUiThread(() -> callNextActivity()));

    }

    private void setLanguage() {
        String selectedLang = defaultPreferences.getString(Constants.SELECTEDLANGUAGELABEL,"English");
        selectedLangText.setText(getString(R.string.selected_language_str) + selectedLang);
    }

    public class PeriodicReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                    if (selectedBeen != null) {
                        progressBarLoc.setVisibility(View.VISIBLE);
                        new LocationSurveyAsyncTask(selectedBeen.getId(), "level6", "level5", 2).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
            }
             catch (Exception e) {
                Logger.logE(ListingActivity.class.getSimpleName(), "Exception in SyncSurveyActivity  Myreceiver class  ", e);
            }
        }
    }

    private void callNextActivity() {
        try {
            calligGPS();
            if (!gpsTracker.canGetLocation()) {
                gpsTracker.showSettingsAlert();
            } else {
                if (checkSelection()) {
                    if (("DSC").equalsIgnoreCase(levelASCDSC)) {
                        Spinner sp = getLevelsSpinner(orderLabels.length - 1);
                        LevelBeen been = (LevelBeen) sp.getSelectedItem();
                        JSONObject result = dbhelper.getLevels7Values(orderLeves[orderLabels.length - 1], String.valueOf(been.getId()));
                        String level7Been = result.toString();
                        Gson gson = new Gson();
                        level1List = gson.fromJson(level7Been, Level7.class);
                        Logger.logV("Clusterid", "Cluster id value ==>" + orderLabels[0].substring(0, 1).toUpperCase());
                        SharedPreferences.Editor editor11 = sharedPreferences.edit();
                        editor11.putBoolean("isLocationBased", true);
                        editor11.apply();

                    } else {
                        Spinner sp = getLevelsSpinner(orderLeves.length - 1);
                        LevelBeen been = (LevelBeen) sp.getSelectedItem();
                        JSONObject result = dbhelper.getLevels7Values(orderLeves[orderLabels.length - 1], String.valueOf(been.getId()));
                        String level7Been = result.toString();
                        Gson gson = new Gson();
                        level1List = gson.fromJson(level7Been, Level7.class);
                        Logger.logV("Survey table Response value", been.getId() + "---->" + orderLabels[orderLabels.length - 1].substring(0, 1).toUpperCase() + orderLabels[orderLabels.length - 1].substring(1) + "---->" + sp.getSelectedItem().toString() + "--->" + level1List);
                    }
                }
            }
        } catch (Exception e) {
            Logger.logE("LevelsActivity", "Exception on click of continue", e);
        }

    }

    public void popUpMenu() {
        Context wrapper = new ContextThemeWrapper(getApplication(), R.style.AppTheme);
        PopupMenu popup = new PopupMenu(wrapper, findViewById(R.id.imageMenu));
        popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
        popup.getMenu().getItem(0).setTitle("Choose Language");
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    public void calligGPS() {
        gpsTracker = new GPSTracker(LevelsActivityNew.this);
    }

    public Boolean checkSelection() {
        for (int i = 0; i < orderLeves.length; i++) {
            Spinner sp = getLevelsSpinner(i);
            try {
                if (("Select").equals(sp.getSelectedItem().toString())) {
                    ToastUtils.displayToast("Choose " + orderLabels[i].substring(0, 1).toUpperCase() + orderLabels[i].substring(1) + " Cluster", con);
                    return false;
                }
            } catch (Exception e) {
                Logger.logE("", "", e);
            }
        }
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        setLanguage();
        registerReceiver(periodicReceiver, periodicityFilter);
        this.setTitle(prefs.getString("surveyName", null));
        if(!locationBenListForAdapter.isEmpty()){
            locationBasedFormAdapter.notifyDataSetChanged();
        }
    }

    /**
     * @param surveyId
     */
    public void setSpinners(String surveyId) {

        Logger.logV("the levels", "the levels ........." + orderLeves.length);
        Logger.logV("the orders are", "the level orders are" + Arrays.toString(orderLeves));
        /*
          Bellow method will tell us the first level is a child or parent (ASC or DSC)
          If empty its ACS else DSC
         */
        checkAvailable = dbhelper.getListOrderLevels(Integer.parseInt(surveyId), orderLeves[0]);
        levelASCDSC = "ASC";
        checkAvailable = orderLeves[orderLeves.length - 1];

        /*
          Bellow mentioned for loop is to make visible of Spinners and TextViews
         */
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto-Light.ttf");
        try {
            if (orderLeves.length > 0) {
                for (int i = 0; i < orderLeves.length; i++) {
                    lablesList.get(i).setTypeface(tf);
                    lablesList.get(i).setText(orderLabels[0].substring(0, 1).toUpperCase() + orderLabels[0].substring(1));
                    if (orderLeves.length == 3)
                        levelayout3.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            Logger.logE("", "", e);
        }
        String result = dbhelper.getChildIds(checkAvailable, Integer.parseInt(surveyId));

        Logger.logV("the levels are", "the ids are" + result);
        List<Integer> firstSpinnerIds = dbhelper.getLevelIds(checkAvailable, orderLeves[0], result);
        list = dbhelper.getLevelsValues(orderLeves[0], firstSpinnerIds.toString());
        Spinner spinner = getLevelsSpinner(0);
        adapter = new ArrayAdapter<>(LevelsActivityNew.this, R.layout.spinnerrow, list);
        spinner.setAdapter(adapter);
        spinner.setSelection(list.size() - 1);
    }





    /**
     * @param index its a name of level which is coming from backend
     * @return
     */
    public Spinner getLevelsSpinner(int index) {
        return spinnerList.get(index);
    }

    public void fillIds() {
        levelayout3 = findViewById(R.id.level2Linear);
        level1Text = findViewById(R.id.levelOne);
        level2Text = findViewById(R.id.levelTwo);
        level3Text = findViewById(R.id.levelThree);
        level4Text = findViewById(R.id.levelFour);
        level5Text = findViewById(R.id.levelfive);
        level6Text = findViewById(R.id.levelsix);
        level1Spinner = findViewById(R.id.levelOneSpinner);
        beneficiarySpinner = findViewById(R.id.spinner);
        level2Spinner = findViewById(R.id.levelTwoSpinner);
        level3Spinner = findViewById(R.id.levelThreeSpinner);
        level4Spinner = findViewById(R.id.levelFourSpinner);
        level5Spinner = findViewById(R.id.levelfiveSpinner);
        level6Spinner = findViewById(R.id.levelsixSpinner);
        continueButton = findViewById(R.id.continueButton);
        level1Spinner.setOnItemSelectedListener(this);
        level2Spinner.setOnItemSelectedListener(this);
        level3Spinner.setOnItemSelectedListener(this);
        level4Spinner.setOnItemSelectedListener(this);
        level5Spinner.setOnItemSelectedListener(this);
        level6Spinner.setOnItemSelectedListener(this);
        spinnerList.add(level1Spinner);
        spinnerList.add(level2Spinner);
        spinnerList.add(level3Spinner);
        spinnerList.add(level4Spinner);
        spinnerList.add(level5Spinner);
        spinnerList.add(level6Spinner);
        lablesList.add(level1Text);
        lablesList.add(level2Text);
        lablesList.add(level3Text);
        lablesList.add(level4Text);
        lablesList.add(level5Text);
        lablesList.add(level6Text);
        hamletListview= findViewById(R.id.hamletListview);
        emptyTextView= findViewById(R.id.emptytextview);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try{

            Logger.logV("the positions are", "the postions are" + position);
            if(parent.getId()==R.id.levelOneSpinner){
                LevelBeen levelBeen=(LevelBeen)parent.getSelectedItem();
                if(!(Constants.SELECT).equalsIgnoreCase(levelBeen.getName())) {
//                    setVillageAdapter(levelBeen.getId(), "level6", "level5");
                    selectedBeen  = levelBeen;
                    progressBarLoc.setVisibility(View.VISIBLE);
                    new LocationSurveyAsyncTask(levelBeen.getId(), "level6", "level5",1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }else{
                    level2Text.setVisibility(View.GONE);
                    hamletListview.setVisibility(View.GONE);
                    emptyTextView.setVisibility(View.VISIBLE);
                }
            }
        }catch (Exception e){
           Logger.logE("","",e);
        }

    }

    private void setVillageAdapter(int id, String parentLevel, String childLevel) {
//        childNamesList = dbhelper.getChildNamesList(childLevel, parentLevel, id, levelsSharedpreferences);

        if(locationBenListForAdapter.isEmpty()){
            level2Text.setVisibility(View.GONE);
            hamletListview.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        }else {
            level2Text.setVisibility(View.VISIBLE);
            lablesList.get(1).setText(orderLabels[1].substring(0, 1).toUpperCase() + orderLabels[1].substring(1));
            emptyTextView.setVisibility(View.GONE);
            hamletListview.setVisibility(View.VISIBLE);
            locationBasedFormAdapter = new LocationBasedFormAdapter(this,this, locationBenListForAdapter,extras,orderLabels,orderLeves );
            hamletListview.setAdapter(locationBasedFormAdapter);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.survey_selection_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.changeLanguage:
                chooseLanguagePopUp();
                break;
            default:
                break;
        }
        return false;
    }

    private void chooseLanguagePopUp() {
        try {
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            ConveneDatabaseHelper databaseHelper1 = ConveneDatabaseHelper.getInstance(this, sharedPreferences.getString(Constants.CONVENE_DB, ""), sharedPreferences.getString("uId", ""));
            android.database.sqlite.SQLiteDatabase homepageDatabase = databaseHelper1.getWritableDatabase();
            final List<String> regionalLanguage = DataBaseMapperClass.getRegionalLanguage(homepageDatabase, -1);
            List<String> getLanguageName = new ArrayList<>();
            if (!regionalLanguage.isEmpty()) {
                for (int i = 0; i < regionalLanguage.size(); i++) {
                    String[] getlanguageNameTemp = regionalLanguage.get(i).split("@");
                    getLanguageName.add(getlanguageNameTemp[1]);
                }
            } else {
                getLanguageName.add("English");
            }
            DialogInterface.OnClickListener dialogInterface = (dialog, which) -> setSelectedLanguageToPrefernce(which, regionalLanguage, editor);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.select));
            builder.setSingleChoiceItems(getLanguageName.toArray(new String[regionalLanguage.size()]), sharedPreferences.getInt(Constants.SELECTEDVALUE, 0), dialogInterface);
            alertDialog = builder.create();
            alertDialog.show();
        } catch (Exception e) {
            Logger.logE("ExceptionTag", "ExceptionTag", e);
        }

    }

    private void setSelectedLanguageToPrefernce(int which, List<String> getRegionalLanguage, SharedPreferences.Editor editor) {
        String language = getRegionalLanguage.get(which);
        String[] languageSpilt = language.split("@");
        editor.putInt(Constants.SELECTEDLANGUAGE, Integer.parseInt(languageSpilt[0]));
        editor.putInt(Constants.SELECTEDVALUE, which);
        editor.putString(Constants.SELECTEDLANGUAGELABEL, languageSpilt[1]);
        BaseActivity.setLocality(1, this);
        alertDialog.cancel();
        editor.apply();
        ToastUtils.displayToastUi(languageSpilt[1] + " is selected", this);
        setLanguage();
    }

    public class LocationSurveyAsyncTask extends AsyncTask<String, Integer, String> {

        private final int id;
        private final String parentLevel;
        private final String childLevel;
        private final int isFirstTime;

        public LocationSurveyAsyncTask(int id, String parentLevel, String childLevel,int isFirstTime)
        {
            this.id = id;
            this.parentLevel = parentLevel;
            this.childLevel = childLevel;
            this.isFirstTime = isFirstTime;
        }
        @Override
        protected String doInBackground(String... strings) {
            locationListBean = dbhelper.getChildNamesList(childLevel, parentLevel, id, levelsSharedpreferences);
            String clusterIds = levelsSharedpreferences.getString("clusterIds","-1");
            locationListBean = dbhelper.getLocationOnlineSurveyRecords(clusterIds,surveyId,locationListBean,extras.getString(Constants.PERIODICITY),extras.getInt(Constants.P_LIMIT));
            locationListBean = new SurveyControllerDbHelper(con).getLocationOfflineSurveyRecords(clusterIds,surveyId,locationListBean,extras.getString(Constants.PERIODICITY),extras.getInt(Constants.P_LIMIT));
            locationBenListForAdapter = getListToAdapter(locationListBean);
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBarLoc.setVisibility(View.GONE);
            Logger.logV("completedOnlinneList ",locationListBean.size()+"");
            if (isFirstTime == 1)
                setVillageAdapter(id,parentLevel,childLevel);
            else {
                //Need to Understand
                locationBasedFormAdapter.updateList(locationBenListForAdapter);
                locationBasedFormAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(periodicReceiver);
    }

    private List<LocationSurveyBeen> getListToAdapter(Map<Integer, LocationSurveyBeen> locationListBean) {
        List<LocationSurveyBeen> tempList = new ArrayList<>();
        Iterator it = locationListBean.entrySet().iterator();
        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry)it.next();
            tempList.add((LocationSurveyBeen ) pair.getValue());
        }

        return tempList;
    }




}
