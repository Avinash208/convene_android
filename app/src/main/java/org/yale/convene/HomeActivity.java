package org.yale.convene;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.yale.convene.BeenClass.beneficiary.Datum;
import org.yale.convene.adapter.ExpandableListAdapterDataCollection;
import org.yale.convene.api.BeneficiaryApis.BeneficaryTypeInterface;
import org.yale.convene.api.BeneficiaryApis.FacilityTypeAsyncTask;
import org.yale.convene.api.BeneficiaryApis.FacilityTypeInterface;
import org.yale.convene.api.BeneficiaryApis.ServiceTypeInterface;
import org.yale.convene.api.FacilitiesListAsyncTask;
import org.yale.convene.api.MeetingAPIs.BeneficiaryAsyncTask;
import org.yale.convene.database.DBHandler;
import org.yale.convene.database.ExternalDbOpenHelper;
import org.yale.convene.location.GPSTracker;
import org.yale.convene.network.ClusterToTypo;
import org.yale.convene.utils.CheckNetwork;
import org.yale.convene.utils.Constants;
import org.yale.convene.utils.Logger;
import org.yale.convene.utils.ProgressUtils;
import org.yale.convene.utils.ToastUtils;
import org.yale.convene.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class HomeActivity extends BaseActivity implements BeneficaryTypeInterface, View.OnClickListener, FacilityTypeInterface, ServiceTypeInterface, PopupMenu.OnMenuItemClickListener, ClusterToTypo {

    private long backPressed;
    Intent intent;
    Toolbar toolbar;
    ExpandableListAdapterDataCollection listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    ExternalDbOpenHelper dbhelper;
    ImageView imageMenu;
    HashMap<String, List<Datum>> listDataChild;
    TextView helpTexts;
    SharedPreferences syncSurveyPreferences;
    TextView logOut;
    TextView userNameLabel;
    private static final String FACILITY_TYPES_PREF_KEY = "FACILITY_TYPES_UID";
    private static final String UPDATE_DATETIMESTAMP_KEY = "UPDATE_DATETIMESTAMP";
    private static final String DUE = "DUE";
    private static final String TAG = "HomeActivity";
    List<Datum> beneficiaryTypesList = new ArrayList<>();
    List<Datum> facilityTypesList = new ArrayList<>();
    List<Datum> serviceTypesList = new ArrayList<>();
    ProgressDialog loginDialog;
    private TextView dueCountLable;
    private DBHandler dbHandler;
    IntentFilter filter;
    IntentFilter intentFilter;
    IntentFilter filterBeneficiary;
    Myreceiver reMyreceive;
    Context context;
    SharedPreferences.Editor syncSurveyEditor;
    private LinearLayout pressBack;
    private LinearLayout dataformsLinear;
    private GPSTracker gpsTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home3);
        this.context = HomeActivity.this;
        initVariables();
        reMyreceive = new Myreceiver();
        filter = new IntentFilter("Survey");
        filterBeneficiary = new IntentFilter("BeneficiaryIntentReceiver");
        intentFilter = new IntentFilter("FacilityIntentReceiver");
        Logger.logD("Current page is homescreen", "curent page is homescreen");
        dbHandler = new DBHandler(this);
        dbhelper = ExternalDbOpenHelper.getInstance(HomeActivity.this, defaultPreferences.getString(Constants.DBNAME, ""), defaultPreferences.getString("inv_id", ""));
        gpsTracker=GPSTracker.getInstance(this);
        int getDUECount = updateDueCount(dbhelper, dbHandler);
        if (getDUECount > 0) {
            dueCountLable.setVisibility(View.VISIBLE);
            dueCountLable.setText(String.format("%s%s", DUE, String.valueOf(getDUECount)));
        } else
            dueCountLable.setVisibility(View.GONE);
        checkAPKVersionUpdate();

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        listDataHeader.add("Beneficiaries");
        listDataHeader.add("Facilities");

        /*
          this method will check the previous updated date to database.
         */
        checkMasterUpdateUpdates();
        methodTosetAdapter();
        String beneficiaryTypes = defaultPreferences.getString("BENEFICIARY_TYPES_UID", "");
        beneficiaryTypesList = getBeneficiaryTypesFromResponse(beneficiaryTypes);
        String facilityTypes = defaultPreferences.getString(FACILITY_TYPES_PREF_KEY, "");
        facilityTypesList = getBeneficiaryTypesFromResponse(facilityTypes);
        prepareListData();

        pressBack.setOnClickListener(this);
        helpTexts.setOnClickListener(this);
        logOut.setOnClickListener(this);
        logOut.setOnClickListener(this);
        dataformsLinear.setOnClickListener(this);
        dueCountLable.setOnClickListener(this);
        imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpMenu();
            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousItem = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousItem) {
                    expListView.collapseGroup(previousItem);
                }
                previousItem = groupPosition;
            }
        });
    }

    private void checkAPKVersionUpdate() {
        try {
            int appVersionNumber;
            PackageManager manager = this.getPackageManager();
            PackageInfo info = null;
                info = manager.getPackageInfo(this.getPackageName(), 0);
                appVersionNumber = info.versionCode;
                if (defaultPreferences.getInt(Constants.UPDATEDAPKVERSION, appVersionNumber)>appVersionNumber){
                    Logger.logD(Constants.UPDATEDAPKVERSION, "Update APK aviliable");
                    displayUpdateDialogBox(defaultPreferences.getString("UPDATE_APK_RESPONSE", ""));

                }else{
                    Logger.logD(Constants.UPDATEDAPKVERSION, "Update APK is not aviliable");
                }
        } catch (Exception e) {
            Logger.logE("e","e in the PackageManager",e);
        }
    }

    private void displayUpdateDialogBox(String apkUpdateResult) {
        try {
            JSONObject apkResponseObject = new JSONObject(apkUpdateResult);
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.custom_layout_apk_update);
            dialog.setTitle("Update app");
            TextView text = dialog.findViewById(R.id.textDialog);
            text.setText(apkResponseObject.getString("updateMessage"));
            final String updatepathlink= apkResponseObject.getString("link");
            final String forceUpdate = apkResponseObject.getString("forceUpdate");
            Button btnUpdate = dialog.findViewById(R.id.btnUpdateNew);
            dialog.show();
            Logger.logD("updated", "updated dialog==" + "==" + apkUpdateResult);
            Button btnCancel = dialog.findViewById(R.id.btnCancel);
            //TODO while release make setCancelable as false
            dialog.setCancelable(true);
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String appPackageName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(updatepathlink)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        Logger.logE("curnt","current date and time ",anfe);
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            });
            if (!forceUpdate.equalsIgnoreCase("true"))
            {
                btnCancel.setVisibility(View.VISIBLE);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
            else
                btnCancel.setVisibility(View.GONE);
        } catch (Exception e) {
            Logger.logE("curnt","current date and time ",e);
        }
    }




    /**
     * method to set the expandable adapter for facility,beneficiary and location based survey
     */
    private void methodTosetAdapter() {
        String facilityTypes = defaultPreferences.getString(FACILITY_TYPES_PREF_KEY, "");
        facilityTypesList = getBeneficiaryTypesFromResponse(facilityTypes);
        listDataChild.put(listDataHeader.get(0), beneficiaryTypesList);
        listDataChild.put(listDataHeader.get(1), facilityTypesList);
        listAdapter = new ExpandableListAdapterDataCollection(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        expListView.expandGroup(0);
    }


    /**
     * method to update the master data based on timestamp configuration from backend and set to preferenece
     */
    private void checkMasterUpdateUpdates() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        if (Utils.haveNetworkConnection(HomeActivity.this)){
            try {
                Logger.logD(UPDATE_DATETIMESTAMP_KEY, "last updated date"+syncSurveyPreferences.getString(UPDATE_DATETIMESTAMP_KEY,""));
                Date curDate = new Date();
                Date currentDate = simpleDateFormat.parse(simpleDateFormat.format(curDate));
                Date previousUpdateDate = simpleDateFormat.parse(syncSurveyPreferences.getString(UPDATE_DATETIMESTAMP_KEY,""));
                long getDateDifferent= Utils.printDifference(previousUpdateDate,currentDate);
                Logger.logD(" Different TIme configured from the server",syncSurveyPreferences.getInt("TableUpdateTime",0)+"");
                if (getDateDifferent==syncSurveyPreferences.getInt("TableUpdateTime",0)){
                    Utils.updateMasterDatabase(this,syncSurveyPreferences);
                    Date curDateNow = new Date();
                    SharedPreferences.Editor editorStoreTimeStamp = syncSurveyPreferences.edit();
                    Logger.logD(TAG,"settind time for next Update"+simpleDateFormat.format(curDateNow));
                    editorStoreTimeStamp.putString(UPDATE_DATETIMESTAMP_KEY, simpleDateFormat.format(curDateNow));
                    editorStoreTimeStamp.apply();
                }
            } catch (ParseException e) {
                Logger.logE(TAG,"checkMasterUpdateUpdates - ",e);
            }
        }
    }

    /**
     * initializing all the field values
     */
    private void initVariables() {
        toolbar = findViewById(R.id.toolbar1);
        imageMenu = findViewById(R.id.imageMenu);
        imageMenu.setVisibility(View.GONE);
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        toolbarTitle.setTypeface(customFont);
        toolbarTitle.setText(getString(R.string.Home));
        syncSurveyPreferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        pressBack = findViewById(R.id.backPress);
        pressBack.setVisibility(View.GONE);
        userNameLabel = findViewById(R.id.userNameLabel);
        String userName = defaultPreferences.getString("user_name", "");
        userNameLabel.setText(String.format("Logged in as: %s", userName));
        userNameLabel.setTypeface(customFont);
        helpTexts = findViewById(R.id.helptext);
        logOut = findViewById(R.id.logout);
        dataformsLinear = findViewById(R.id.dataformsLinear);
        expListView = findViewById(R.id.expandableListview);
        TextView contentUpdateView = findViewById(R.id.update_content);
        contentUpdateView.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(reMyreceive, filter);
        registerReceiver(reMyreceive, filterBeneficiary);
        registerReceiver(reMyreceive, intentFilter);
        if(!new CheckNetwork(getApplicationContext()).checkNetwork())
            ToastUtils.displayToast("Application is working in offline",getApplicationContext());

         gpsTracker.stopUsingGPS();
    }



    /**
     * method to set the due count which need to sync to backend when connectivity connects
     *
     * @param dbhelper
     * @param dbOpenHelper
     * @return
     */
    private int updateDueCount(ExternalDbOpenHelper dbhelper, DBHandler dbOpenHelper) {
        int sumBenCountFacilitySurvey;
        int getBeneficiaryNotSyncRecord = dbhelper.getBeneficiaryNotSync(1);
        int getFacilityNotSyncRecord = dbhelper.getBeneficiaryNotSync(2);
        int getSurveyNotSyncRecord = dbOpenHelper.getSurveyNotSync(dbOpenHelper);
        sumBenCountFacilitySurvey = getBeneficiaryNotSyncRecord + getFacilityNotSyncRecord + getSurveyNotSyncRecord;
        return sumBenCountFacilitySurvey;
    }


    /**
     * method to display the menu popup
     */
    private void popUpMenu() {
        Context wrapper = new ContextThemeWrapper(getApplication(), R.style.AppTheme);
        PopupMenu popup = new PopupMenu(wrapper, findViewById(R.id.imageMenu));
        popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
        popup.getMenu().getItem(0).setTitle("Change Language");
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }


    /**
     * method to set the expandable adapter
     */
    private void prepareListData() {
        listDataChild.put(listDataHeader.get(0), beneficiaryTypesList); // Header, Child data
        listDataChild.put(listDataHeader.get(1), facilityTypesList);
        listAdapter = new ExpandableListAdapterDataCollection(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        expListView.expandGroup(0);

    }


    /**
     * method to get the all the beneficiary and facility types from preference
     *
     * @param beneficiaryTypes
     * @return
     */
    private List<Datum> getBeneficiaryTypesFromResponse(String beneficiaryTypes) {
        List<Datum> beneficiaryTypesCollection = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(beneficiaryTypes);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Datum datum = new Datum();
                datum.setId(jsonObject.getInt("id"));
                datum.setName(jsonObject.getString("name"));
                beneficiaryTypesCollection.add(datum);
            }
        } catch (Exception e) {
            Logger.logE(TAG, "Exception on HomeActivity get benefficiary types", e);
        }
        return beneficiaryTypesCollection;
    }

    /**
     * method back press from device functionality which will exit from app
     */
    @Override
    public void onBackPressed() {
        if (backPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else
            Toast.makeText(getBaseContext(), getString(R.string.pressToExit), Toast.LENGTH_SHORT).show();
        backPressed = System.currentTimeMillis();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.logout) {
            Utils.callDialogConformation(this,this);
        }else{
            Utils.updateMasterDatabase(this,syncSurveyPreferences);
        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.helptext:
                PopUpShow.showingErrorPopUp(HomeActivity.this, syncSurveyPreferences.getString("UID", ""));
                break;
            case R.id.logout:
                Utils.callDialogConformation(this,this);
                break;
            case R.id.dataformsLinear:
                setToPreferenceCallNextActivity(view);
                break;
            case R.id.update_content:
                Utils.contentUpdateConformation(this);
                break;
            default:
                break;
        }
    }

    private void setToPreferenceCallNextActivity(View view) {
        // displaying clickable animation
//        AnimationUtils.viewAnimation(view);
        // saving location based and not to false in default shared preference object
        syncSurveyEditor = syncSurveyPreferences.edit();
        syncSurveyEditor.putBoolean("isLocationBased", false);
        syncSurveyEditor.putBoolean("isNotLocationBased", false);
        syncSurveyEditor.apply();

        // calling data collection forms listing screen
        startActivity(new Intent(HomeActivity.this, DataCollectionListingActivity.class));
    }


    /**
     * method to sync the data when connectivity is available
     */
    public void syncFunction() {
        if (Utils.haveNetworkConnection(context)) {
            if (syncSurveyPreferences.getBoolean("sendingSurvey", false)) {
                ToastUtils.displayToast(getString(R.string.syncingPendingData), HomeActivity.this);
                AutoSyncActivity autoSyncObj = new AutoSyncActivity(HomeActivity.this);
                autoSyncObj.callingAutoSync(1);
            }
        } else {
            Toast.makeText(this, getString(R.string.checkNet), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * on sucess response of facility
     */
    @Override
    public void onSuccessFaciltyResponse(boolean flag) {
        String facilityTypes = defaultPreferences.getString(FACILITY_TYPES_PREF_KEY, "");
        facilityTypesList = getBeneficiaryTypesFromResponse(facilityTypes);
        listDataChild.put(listDataHeader.get(0), beneficiaryTypesList);
        listDataChild.put(listDataHeader.get(1), facilityTypesList);
        listAdapter = new ExpandableListAdapterDataCollection(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        expListView.expandGroup(0);
        ProgressUtils.CancelProgress(this.loginDialog);
    }

    /**
     * on success response if service and set to prefrence
     */
    @Override
    public void onSuccessServiceResponse() {
        String serviceTypes = defaultPreferences.getString("SERVICE_TYPES_UID", "");
        serviceTypesList = getBeneficiaryTypesFromResponse(serviceTypes);
    }

    @Override
    public void onSuccessBeneficiaryResponse(String response,boolean flag) {
        String beneficiaryTypes = defaultPreferences.getString("BENEFICIARY_TYPES_UID", "");
        beneficiaryTypesList = getBeneficiaryTypesFromResponse(beneficiaryTypes);
        if (Utils.haveNetworkConnection(this)) {
            new FacilityTypeAsyncTask(this, HomeActivity.this, this).execute();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        if (item.getItemId() == R.id.changeLanguage) {
            Utils.chooseLanguage(defaultPreferences,HomeActivity.this);
            return true;
        }
        return false;
    }

    @Override
    public void callTypoScreen(boolean flag) {
        Logger.logD(TAG,"Response from BeneficiaryListing");
    }

    @Override
    public void surveyListSuccess(boolean flag) {
        Logger.logD(TAG,"Response from BeneficiaryListing and setting the adapter");
    }

    public class Myreceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                int getDUECount = updateDueCount(dbhelper, dbHandler);
                if (getDUECount > 0) {
                    dueCountLable.setVisibility(View.VISIBLE);
                    dueCountLable.setText(String.format("%s%s", DUE + " - ", String.valueOf(getDUECount)));
                } else {
                    dueCountLable.setVisibility(View.GONE);
                    if (("BeneficiaryIntentReceiver").equals(intent.getAction())) {
                        Logger.logD(TAG, "Broad caste reciver listing");
                        new BeneficiaryAsyncTask(context, HomeActivity.this, HomeActivity.this, defaultPreferences.getString("uId", "")).execute();
                    } else {
                        new FacilitiesListAsyncTask(context, HomeActivity.this, null).execute();
                    }
                }
            } catch (Exception e) {
                Logger.logE(TAG, "Exception in SyncSurveyActivity  Myreceiver class  ", e);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(reMyreceive);
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
    }
}