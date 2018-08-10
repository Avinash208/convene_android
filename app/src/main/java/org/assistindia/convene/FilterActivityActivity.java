package org.assistindia.convene;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import org.assistindia.convene.BeenClass.boundarylevel.LocationType;
import org.assistindia.convene.BeenClass.parentChild.LevelBeen;
import org.assistindia.convene.adapter.spinnercustomadapter.CustomSpinnerAdapter;
import org.assistindia.convene.database.ExternalDbOpenHelper;
import org.assistindia.convene.utils.AddBeneficiaryUtils;
import org.assistindia.convene.utils.Constants;
import org.assistindia.convene.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class FilterActivityActivity extends AppCompatActivity {
    private static final String TAG = "FilterActivityActivity: ";
    private Typeface face;
    private Activity activity;
    private ExternalDbOpenHelper dbhelper;
    private TextView statedynamicLabel;
    private Spinner stateSpinner;
    private TextView districtDynamicLabel;
    private Spinner districtSpinner;
    private TextView talukDynamicLabel;
    private Spinner talukSpinner;
    private TextView grampanchayatDynamicLabel;
    private Spinner grampanchayatSpinner;
    private TextView villageDynamicLabel;
    private Spinner villageSpinner;
    private TextView hamletdynamicLabel;
    private TextView errorState;
    private TextView errorDIstrict;
    private Spinner hamletDynamicSpinner;
    private TextView errorTaluk;
    private String slugName="";
    private List<LevelBeen> stateList=new ArrayList<>();
    private SharedPreferences sharedPreferences1;

    private AddBeneficiaryUtils addBeneficiaryUtils;
    private Spinner locationLevelSpinner;
    private static String levelSeven = "Level7";
    private static String levelFive = "Level5";
    private static String levelFour = "Level4";
    private static String levelThree = "Level3";
    private static String levelSix = "Level6";
    private static String levelTwo = "Level2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_activity);
        activity = this;
        sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(activity);
        addBeneficiaryUtils=new AddBeneficiaryUtils(activity);
        dbhelper = ExternalDbOpenHelper.getInstance(activity, sharedPreferences1.getString(Constants.DBNAME, ""), sharedPreferences1.getString("inv_id", ""));

        bindUi();
        setListners();
    }

    private void setListners() {
        String locationLevelString = sharedPreferences1.getString("LOCATION_LEVELTYPE_UID", "");
        List<LocationType> locationList= addBeneficiaryUtils.setLocationLevelAdapter(activity,locationLevelString, locationLevelSpinner);

        stateList = dbhelper.getLevelValuesFromDB(levelTwo, "name");
        CustomSpinnerAdapter dataAdapter3 = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, stateList);
        stateSpinner.setAdapter(dataAdapter3);
        locationLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                LocationType locationType = (LocationType) adapterView.getSelectedItem();
                resetLocationLevelAdapter(locationType);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Logger.logV(TAG, "nothing is selected in dynamic spinner selection");
            }
        });


        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LevelBeen levelBeen = (LevelBeen) parent.getSelectedItem();
                Logger.logV(TAG, "State selected Id" + levelBeen.getId());

                    addBeneficiaryUtils.setStateSpinner(levelThree,districtSpinner,levelBeen);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Logger.logV(TAG, " state dynamic onNothingSelected");
            }
        });
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                LevelBeen levelBeen1 = (LevelBeen) adapterView.getSelectedItem();

                    addBeneficiaryUtils.setDistrictToSpinner(levelFour,levelBeen1,slugName,talukSpinner);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Logger.logV(TAG, "onNothingSelected");
            }
        });
        talukSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                LevelBeen levelBeen2 = (LevelBeen) adapterView.getSelectedItem();
                    addBeneficiaryUtils.setGrampanchayatToSpinner(levelFive,grampanchayatSpinner,levelBeen2,slugName);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Logger.logV(TAG, "the house hold dynamic taluk nothing selected");
            }
        });
        grampanchayatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                LevelBeen levelBeen4 = (LevelBeen) adapterView.getSelectedItem();

                    addBeneficiaryUtils.setGrampanachayatLevelToSpinner(levelSix,villageSpinner,slugName,levelBeen4);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Logger.logV(TAG, "the house hold dynamic grampanchat nothing selected");
            }
        });
        villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                LevelBeen levelBeen5 = (LevelBeen) adapterView.getSelectedItem();

                    addBeneficiaryUtils.setHamletLevelListToSpinner(hamletDynamicSpinner,levelBeen5,slugName);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Logger.logV(TAG, "the house hold dynamic village nothing selected");
            }
        });
        hamletDynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Logger.logV(TAG, "the house hold dynamic hamlet onitem selected");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Logger.logV(TAG, "the house hold dynamic hamlet");
            }
        });

    }

    private void resetLocationLevelAdapter(LocationType locationType) {
        slugName = locationType.getName();
        addBeneficiaryUtils.setClusterLabels(statedynamicLabel, districtDynamicLabel, talukDynamicLabel, grampanchayatDynamicLabel, villageDynamicLabel, hamletdynamicLabel, slugName);
        List<Spinner> spinnerList=new ArrayList<>();
        spinnerList.add(districtSpinner);
        spinnerList.add(talukSpinner);
        spinnerList.add(grampanchayatSpinner);
        spinnerList.add(villageSpinner);
        spinnerList.add(hamletDynamicSpinner);
        addBeneficiaryUtils.setEditedStateAdapter(spinnerList);


    }

    private void bindUi() {
        statedynamicLabel = findViewById(R.id.stateLabel);
        stateSpinner = findViewById(R.id.housrholdstate);
        districtDynamicLabel = findViewById(R.id.districtLabel);
        districtSpinner = findViewById(R.id.housrholddistrict);
        talukDynamicLabel = findViewById(R.id.talukLabel);
        talukSpinner = findViewById(R.id.housrholdtaluk);
        grampanchayatDynamicLabel = findViewById(R.id.grampanchayatLabel);
        grampanchayatSpinner = findViewById(R.id.housrholdgrampanchayat);
        villageDynamicLabel = findViewById(R.id.villageLabel);
        villageSpinner = findViewById(R.id.housrholdvillage);
        hamletdynamicLabel = findViewById(R.id.hamletLabel);
        errorState= findViewById(R.id.errorTextState);
        errorDIstrict= findViewById(R.id.errorTextDistrict);
        errorTaluk= findViewById(R.id.errorTextTaluk);
        locationLevelSpinner = findViewById(R.id.locationlevel);
        hamletDynamicSpinner = findViewById(R.id.housrholdhamlet);

    }
}
