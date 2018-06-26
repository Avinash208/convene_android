package org.mahiti.convenemis;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.mahiti.convenemis.BeenClass.parentChild.LevelBeen;
import org.mahiti.convenemis.BeenClass.parentChild.LocationSurveyBeen;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.database.Utilities;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.StartSurvey;
import org.mahiti.convenemis.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mahiti.convenemis.utils.Constants.SURVEY_ID;

public class LocationBasedActivity extends AppCompatActivity{

    private static final String TAG = "LocationBasedActivity";
    private static final String MY_PREFS_NAME = "MyPrefs";
    private LocationBasedActivity activity;
    private SharedPreferences sharedPreferences;
    private ExternalDbOpenHelper dbhelper;
    private DBHandler handler;
    private String levels;
    private String labels;
    private String[] orderLabels;
    private String[] orderLeves;
    private String typeName = "";
    private String surveyId = "";
    private SharedPreferences prefs;
    List<Spinner> storeAllDynamicSpinner = new ArrayList<>();
    private List<LocationSurveyBeen> locationcaptureList = new ArrayList<>();
    private net.sqlcipher.database.SQLiteDatabase db;


    private LinearLayout relativeLayout;
    private FloatingActionButton createNewButton;
    private String  updateListLocation="";
    private int  clusterID=0;
    private LinearLayout activityContainer;
    private String surveyName="";
    private TextView toolbarTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_based_new);
        initVariables();
        getPreviousFromIntent();
        getDynamicLabelsandTypes();

        createNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (!updateListLocation.equalsIgnoreCase("")){
                   Utilities.setSurveyStatus(sharedPreferences,"new");
                   Utilities.setLocationSurveyFlag(sharedPreferences,"new");
                   new StartSurvey(activity,activity,prefs.getInt(Constants.SURVEY_ID, 0), clusterID, updateListLocation, "", "","", "").execute();
               }else{
                   ToastUtils.displayToast("Please select upto least level",activity);
               }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void getPreviousFromIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            typeName = extras.getString("typeName");
            surveyId = String.valueOf(extras.getInt("survey_id"));
            surveyName = String.valueOf(extras.getInt("survey_name"));
            toolbarTitle.setText("Farm Bund");
            SharedPreferences.Editor locationEditor= prefs.edit();
            locationEditor.putInt(SURVEY_ID,Integer.parseInt(surveyId));
            locationEditor.apply();
        } else {
            surveyId = String.valueOf(prefs.getInt("survey_id", 0));
        }
    }

    private void getDynamicLabelsandTypes() {
        dbhelper = ExternalDbOpenHelper.getInstance(activity, sharedPreferences.getString(Constants.DBNAME, ""), sharedPreferences.getString("inv_id", ""));
        levels = dbhelper.getOrderLevels(Integer.parseInt(surveyId));
        labels = dbhelper.getOrderlabels(Integer.parseInt(surveyId));


        orderLabels = labels.split(",");
        orderLeves = levels.split(",");
        if (orderLabels.length > 0 && orderLeves.length > 0) {

            createDynamicSpinnerAndLabel(orderLabels, orderLeves);
        } else {
            Toast.makeText(activity, "Sorry! Levels are empty", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void displayActivityIfexist() {
        Spinner getLastSpinner= storeAllDynamicSpinner.get(storeAllDynamicSpinner.size()-1);
        LevelBeen levelBeen= (LevelBeen) getLastSpinner.getSelectedItem();
        if (levelBeen!=null &&  !levelBeen.getName().equalsIgnoreCase(" Select ")){
               new LocationSurveyAsyncTask(levelBeen.getId(), levelBeen.getName()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            setUpdateListLocation(levelBeen.getName(),levelBeen.getId());
        }else{
            updateListLocation="";
            clusterID=0;
            activityContainer.removeAllViews();
        }
    }


    private void createDynamicSpinnerAndLabel(String[] orderLabels, String[] orderLeves) {
        relativeLayout.removeAllViews();
        storeAllDynamicSpinner.clear();
        try {
            for (int i = 0; i < orderLeves.length; i++) {
                View child = this.getLayoutInflater().inflate(R.layout.dropdown, relativeLayout, false);//child.xml
                TextView mainQuestionspinner = child.findViewById(R.id.mainQuestionspinner);
                Spinner dynamicSpinner = child.findViewById(R.id.spinner);
                dynamicSpinner.setTag(i);
                dynamicSpinner.setId(i);
                mainQuestionspinner.setText(orderLabels[i]);
                setValuesToSpinner(orderLeves[i], dynamicSpinner);
                storeAllDynamicSpinner.add(dynamicSpinner);
                relativeLayout.addView(child);
                setOnclickListnerDynamic(dynamicSpinner, orderLeves[i]);

            }

        } catch (Exception e) {
            Logger.logE(TAG, "createDynamicSpinnerAndLabel", e);
        }
    }

    private void setOnclickListnerDynamic(Spinner dynamicSpinner, String orderLeve) {
        dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Logger.logD(TAG, "clicked Ites" + adapterView.getId());
                updateValuesDynamic(adapterView.getId(), orderLeve);

            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void updateValuesDynamic(int selectedLevel, String orderLeve) {


        try {
            String getStringLevel=orderLeve.substring(5,6);
            int getLevel= Integer.valueOf(getStringLevel);
            for (int i = selectedLevel; i < storeAllDynamicSpinner.size()-1; i++) {

                    Logger.logD(TAG, "clicked spinner" + selectedLevel);
                    Spinner getStoredSpinner = storeAllDynamicSpinner.get(selectedLevel);
                    Spinner nextSpinner = storeAllDynamicSpinner.get(i + 1);
                    Logger.logD(TAG, "clicked next spinner" + selectedLevel);
                    LevelBeen levelBeen = (LevelBeen) getStoredSpinner.getSelectedItem();
                    getLevel++;
                    List<LevelBeen> stateList = dbhelper.setSpinnerD("level" + (getLevel - 1) + "_id", String.valueOf("level"+ getLevel), levelBeen.getId());

                    if (!stateList.isEmpty()) {
                        ArrayAdapter<LevelBeen> spinnerArrayAdapter = new ArrayAdapter(this, R.layout.spinner_multi_row_textview, stateList);
                        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_multi_row_textview);// The drop down view
                        nextSpinner.setAdapter(spinnerArrayAdapter);



                    } else {
                        List<LevelBeen> emptyStateList = new ArrayList<>();
                        LevelBeen level1 = new LevelBeen(0, " Select ");
                        emptyStateList.add(level1);
                        ArrayAdapter<LevelBeen> spinnerArrayAdapter = new ArrayAdapter(this, R.layout.spinner_multi_row_textview, emptyStateList);
                        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_multi_row_textview);// The drop down view
                        nextSpinner.setAdapter(spinnerArrayAdapter);


                    }
                }
                displayActivityIfexist();


        } catch (Exception e) {
            Logger.logE(TAG, "updateValuesDynamic", e);
        }
    }

    private void setValuesToSpinner(String orderLeve, Spinner dynamicSpinner) {
        List<LevelBeen> getLevelsrecords = dbhelper.getLevelsrecords(orderLeve, dbhelper);
        if (!getLevelsrecords.isEmpty()) {
            ArrayAdapter<LevelBeen> spinnerArrayAdapter = new ArrayAdapter(this, R.layout.spinner_multi_row_textview, getLevelsrecords);
            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_multi_row_textview);// The drop down view
            dynamicSpinner.setAdapter(spinnerArrayAdapter);
        }
    }

    private void initVariables() {
        activity = LocationBasedActivity.this;
        handler = new DBHandler(activity);
        db = handler.getdatabaseinstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        relativeLayout = (LinearLayout) findViewById(R.id.relativeLayout);
        activityContainer = (LinearLayout) findViewById(R.id.dymaicactivitydisplay);
        createNewButton = findViewById(R.id.createNewButton);
        toolbarTitle = (TextView)findViewById(R.id.toolbarTitle);


    }

    public void setUpdateListLocation(String updateLocation, int id) {
        updateListLocation = updateLocation;
        clusterID=id;

    }



    public class LocationSurveyAsyncTask extends AsyncTask<String, Integer, String> {

        private final int clusterIds;
        private final String leaseLevel;


        public LocationSurveyAsyncTask(int id, String leastLevel)
        {
            this.clusterIds = id;
            this.leaseLevel = leastLevel;

        }
        @Override
        protected String doInBackground(String... strings) {
            locationcaptureList = handler.getLeastLocationRecords(leaseLevel);
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
         //   progressBarLoc.setVisibility(View.GONE);
            Logger.logV("completedOnlinneList ",locationcaptureList.size()+"");
            if (!locationcaptureList.isEmpty())
                setVillageAdapter();
        }
    }

    private void setVillageAdapter() {
        activityContainer.removeAllViews();
      for (int i=0;i<locationcaptureList.size();i++){
          View child = this.getLayoutInflater().inflate(R.layout.beneficiaryinstitution_detail_row, activityContainer, false);//child.xml
            LinearLayout cv = (LinearLayout) child.findViewById(R.id.cv);
            CardView cardview = (CardView) child.findViewById(R.id.card_view);
            TextView pending = (TextView) child.findViewById(R.id.pending);
            TextView periodicityTextview = (TextView) child.findViewById(R.id.periodicityTextview);
            TextView periodicityTextviewLabel = (TextView) child.findViewById(R.id.periodicityTextviewLabel);
            TextView villageName = (TextView) child.findViewById(R.id.villageName);
            TextView villageNamelabel = (TextView) child.findViewById(R.id.villageNamelabel);
            if (!locationcaptureList.get(i).getLocationName().isEmpty())
                villageName.setText(locationcaptureList.get(i).getLocationName());

          activityContainer.addView(child);
      }


    }

}
