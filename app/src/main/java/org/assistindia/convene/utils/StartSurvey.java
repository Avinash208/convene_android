package org.assistindia.convene.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.assistindia.convene.BeenClass.Response;
import org.assistindia.convene.ShowSurveyPreview;
import org.assistindia.convene.SurveyQuestionActivity;
import org.assistindia.convene.database.ConveneDatabaseHelper;
import org.assistindia.convene.database.DBHandler;
import org.assistindia.convene.database.DataBaseMapperClass;
import org.assistindia.convene.location.GPSTracker;
import org.assistindia.convene.location.MyCurrentLocationTracker;
import org.assistindia.convene.network.InsertResponseTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mahiti on 2/12/16.
 */
public class StartSurvey extends AsyncTask<Context, Integer, String> {
    private String captureDate = "";
    private SharedPreferences prefs;
    Context context;
    private Map<String, String> values;
    private SharedPreferences preferences;
    Activity activity;
    private GPSTracker gpsTracker;
    private android.app.ProgressDialog syncSurveyProgDialog;
    private float charge = 0;
    private CheckNetwork chckNework;
    private DBHandler syncSurveyHandler;
    private RestUrl resturl;
    private String syncSurveyAnsCode;
    private int selectedClusterId = 0;
    private String selectedLevel = "";
    private String orderLabel = "";
    private static final String MY_PREFS_NAME = "MyPrefs";
    private static final String MY_PREFERENCES = "MyPrefs";
    private String beneficiaryDetails;
    private ConveneDatabaseHelper dbOpenHelper;
    public android.database.sqlite.SQLiteDatabase database;
    private String facility_id = "0";
    private String beneficiary_id = "0";
    private String uuid = "";
    private int typeId = 0;
    private int surveyId = 0;
    private int languageId = 0;
    private DBHandler surveyHandler;
    private String responsePrimaryID = "";
    private static final String BENEFICIARY_IDS_KEY = "beneficiary_ids";
    private static final String FACILITY_IDS_KEY = "facility_ids";
    private static final String TAG = "StartSurvey";
    private static final String LOADING = "Loading...";
    private static final String BENEFICIARY_TYPE_ID = "beneficiary_type_id";
    private String dateFormatStr = "yyyy-MM-dd HH:mm:ss";
    private String facilityTypeIdStr = "facility_type_id";


    /**
     * @param con
     * @param act
     * @param id
     * @param orderLabelString
     * @param selectedLevelString
     * @param surveyId
     */
    public StartSurvey(Context con, Activity act, int id, String orderLabelString, String selectedLevelString, String surveyId, String previousDate) {

        context = con;
        activity = act;
        this.surveyId = Integer.parseInt(surveyId);
        values = new HashMap<>();
        prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        syncSurveyProgDialog = new android.app.ProgressDialog(activity);
        syncSurveyProgDialog.setProgressStyle(android.app.ProgressDialog.STYLE_SPINNER);
        syncSurveyProgDialog.setMessage(LOADING);
        this.beneficiaryDetails = "";
        Operator op = new Operator(context);
        charge = op.getBatteryLevel(context);
        syncSurveyHandler = new DBHandler(context);
        chckNework = new CheckNetwork(context);
        resturl = new RestUrl(context);
        this.captureDate = previousDate;
        selectedClusterId = id;
        selectedLevel = selectedLevelString;
        orderLabel = orderLabelString;
        this.responsePrimaryID = "";
        dbOpenHelper = ConveneDatabaseHelper.getInstance(context, preferences.getString(Constants.CONVENE_DB, ""), preferences.getString("UID", ""));
    }

    /**
     * @param surveySummaryReport
     * @param activity
     * @param surveyId
     * @param levelId
     * @param locationName
     * @param beneficiaryDetails
     * @param clusterLevelName
     * @param serverResponseID
     * @param captureDate
     */
    public StartSurvey(Context surveySummaryReport, Activity activity, int surveyId, int levelId, String locationName, String beneficiaryDetails, String clusterLevelName
            , String serverResponseID, String captureDate) {

        this.context = surveySummaryReport;
        this.activity = activity;
        this.selectedClusterId = levelId;
        this.surveyId = surveyId;
        this.selectedLevel = locationName;
        this.beneficiaryDetails = beneficiaryDetails;
        values = new HashMap<>();
        this.captureDate = captureDate;
        prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        syncSurveyProgDialog = new android.app.ProgressDialog(activity);
        syncSurveyProgDialog.setProgressStyle(android.app.ProgressDialog.STYLE_SPINNER);
        syncSurveyProgDialog.setMessage(LOADING);
        Operator op = new Operator(context);
        charge = op.getBatteryLevel(context);
        syncSurveyHandler = new DBHandler(context);
        chckNework = new CheckNetwork(context);
        resturl = new RestUrl(context);
        orderLabel = clusterLevelName;

        surveyHandler = new DBHandler(activity);
        this.responsePrimaryID = serverResponseID;
        dbOpenHelper = ConveneDatabaseHelper.getInstance(context, preferences.getString(Constants.CONVENE_DB, ""), preferences.getString("UID", ""));
    }

    /**
     * @param surveySummaryReport
     * @param activity
     * @param surveyId
     * @param levelId
     * @param locationName
     * @param beneficiaryDetails
     * @param clusterLevelName
     * @param serverResponseID
     * @param languageId
     */
    public StartSurvey(Context surveySummaryReport, Activity activity, int surveyId, int levelId, String locationName, String beneficiaryDetails, String clusterLevelName
            , String serverResponseID, int languageId) {

        this.context = surveySummaryReport;
        this.activity = activity;
        this.selectedClusterId = levelId;
        this.surveyId = surveyId;
        this.selectedLevel = locationName;
        this.beneficiaryDetails = beneficiaryDetails;
        values = new HashMap<>();
        prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        syncSurveyProgDialog = new android.app.ProgressDialog(activity);
        syncSurveyProgDialog.setProgressStyle(android.app.ProgressDialog.STYLE_SPINNER);
        syncSurveyProgDialog.setMessage(LOADING);
        Operator op = new Operator(context);
        charge = op.getBatteryLevel(context);
        syncSurveyHandler = new DBHandler(context);
        chckNework = new CheckNetwork(context);
        resturl = new RestUrl(context);
        orderLabel = clusterLevelName;
        this.languageId = languageId;
        surveyHandler = new DBHandler(activity);
        this.responsePrimaryID = serverResponseID;
        dbOpenHelper = ConveneDatabaseHelper.getInstance(context, preferences.getString(Constants.CONVENE_DB, ""), preferences.getString("UID", ""));
    }

    /**
     * @param typeDetailsActivity
     * @param typeDetailsActivity1
     * @param surveyId
     * @param levelId
     * @param locationName
     * @param beneficiaryArray
     * @param boundaryLevel
     * @param selectedBeneficiaryId
     * @param capture_date
     */
    public StartSurvey(Context typeDetailsActivity, Activity typeDetailsActivity1, int surveyId, int levelId, String locationName, String beneficiaryArray,
                       String boundaryLevel, int selectedBeneficiaryId, String capture_date) {

        this.context = typeDetailsActivity;
        this.activity = typeDetailsActivity1;
        this.selectedClusterId = levelId;
        this.selectedLevel = locationName;
        this.beneficiaryDetails = beneficiaryArray;
        this.surveyId = surveyId;
        values = new HashMap<>();
        prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        syncSurveyProgDialog = new android.app.ProgressDialog(activity);
        syncSurveyProgDialog.setProgressStyle(android.app.ProgressDialog.STYLE_SPINNER);
        syncSurveyProgDialog.setMessage(LOADING);
        Operator op = new Operator(context);
        charge = op.getBatteryLevel(context);
        syncSurveyHandler = new DBHandler(context);
        chckNework = new CheckNetwork(context);
        resturl = new RestUrl(context);
        orderLabel = boundaryLevel;
        typeId = selectedBeneficiaryId;
        this.responsePrimaryID = "";
        this.captureDate = capture_date;
        dbOpenHelper = ConveneDatabaseHelper.getInstance(context, preferences.getString(Constants.CONVENE_DB, ""), preferences.getString("UID", ""));
    }
    @Override
    protected void onPreExecute() {
        syncSurveyProgDialog.show();
        gpsTracker = new GPSTracker(activity);

    }

    /**
     * method
     */
    private void setLocation() {
        String stringLatitude = "0.0";
        String stringLongitude = "0.0";
        if (Double.doubleToRawLongBits(gpsTracker.latitude) == 0 || Double.doubleToRawLongBits(gpsTracker.longitude) == 0) {
            MyCurrentLocationTracker tracker = new MyCurrentLocationTracker(activity, null, null);
            Location loc = tracker.getLocation(null, null);
            if (loc != null) {
                stringLatitude = String.valueOf(loc.getLatitude());
                stringLongitude = String.valueOf(loc.getLongitude());
            }
        } else {
            stringLatitude = String.valueOf(gpsTracker.latitude);
            stringLongitude = String.valueOf(gpsTracker.longitude);
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("UserRegisteredState", "");
        editor.putString("UserRegisteredDistrict", "");
        editor.putString("LATITUDE", stringLatitude);
        editor.putString("LONGITUDE", stringLongitude);
        editor.putString("pending_specimen_id", "");
        editor.apply();
    }

    @Override
    protected String doInBackground(Context... arg0) {
        try {
            setLocation();
            SharedPreferences sharedpreferences = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

            Logger.logV(TAG, "Value of Version number " + prefs.getString(Constants.VERSION, ""));
            Float versionNumber = Float.valueOf("1.0");
            values.put("start_survey_status", "0");
            values.put("inv_id", preferences.getString("uId", ""));
            if ("".equals(captureDate))
                captureDate = PeriodicityUtils.getToday();
            values.put("captured_date", captureDate);

            if (!"".equals(preferences.getString("recentPreviewRecord", "")))
                values.put("start_date", "");
            else
                values.put("start_date", new SimpleDateFormat(dateFormatStr, Locale.ENGLISH).format(new Date()));
            values.put("end_date", "0");
            values.put("version_num", String.valueOf(versionNumber));
            values.put("app_version", "");
            values.put("language", String.valueOf(preferences.getInt(Constants.SELECTEDLANGUAGE, 1)));
            values.put("lat", preferences.getString("LATITUDE", ""));
            values.put("long", preferences.getString("LONGITUDE", ""));
            values.put("survey_status", "0");
            values.put("sync_status", "0");
            values.put("sync_date", "0");
            values.put("mode_status", "");
            values.put("specimen_id", String.valueOf(getUtcTimeInMillis(new SimpleDateFormat(dateFormatStr, Locale.ENGLISH).format(new Date()))));
            values.put("survey_key", "0");
            values.put("reason", "");
            values.put("paper_entry_reason", "");
            values.put("reason_off_survey", "2");
            values.put("last_qcode", "");
            values.put("survey_status2", "0");
            values.put("survey_status1", "0");
            values.put("domain_id", "");
            values.put("p1_charge", String.valueOf(charge));
            values.put("gps_tracker", String.valueOf(gpsTracker.gps_tracker));
            values.put("consent_status", "");
            values.put("server_primary_key", responsePrimaryID);
            values.put("level1", "");
            values.put("level2", "");
            values.put("level3", "");
            values.put("level4", "");
            values.put("level5", "");
            values.put("level6", "");
            values.put("level7", "");
            if (surveyId == 0) {
                values.put("typology_code", String.valueOf(prefs.getInt(Constants.SURVEY_ID, 0)));
            } else {
                values.put("typology_code", String.valueOf(surveyId));
            }

            values.put("typocodes", "");
            values.put("sectionId", "");
            values.put("cluster_id", String.valueOf(selectedClusterId));
            values.put("clustername", selectedLevel);
            values.put("clusterkey", "Hamlet");
            if (("").equalsIgnoreCase(beneficiaryDetails)) {

                values.put("beneficiary_details", "");
                values.put(BENEFICIARY_IDS_KEY, "");
                values.put(FACILITY_IDS_KEY, "0");
                values.put(BENEFICIARY_TYPE_ID, "0");
                values.put(facilityTypeIdStr, "0");
                values.put("uuid", UUID.randomUUID().toString());
            } else {
                values.put("beneficiary_details", "");
                values.put(BENEFICIARY_IDS_KEY, beneficiaryDetails);
                values.put(FACILITY_IDS_KEY, "0");
                values.put(BENEFICIARY_TYPE_ID, "0");
                values.put(facilityTypeIdStr, "0");
                values.put("uuid", UUID.randomUUID().toString());
            }

            boolean checkNetwork = chckNework.checkNetwork();
            if (checkNetwork) {
                values.put("extra_column", "1");
            } else {
                values.put("extra_column", "0");
            }
            Logger.logV(TAG, "the response is" + values.toString());
            String responseUUID = syncSurveyHandler.insertSurveyDataToDB(values);
            callNextActivity(responseUUID);

        } catch (Exception e) {
            Logger.logE(TAG, "", e);
            if (syncSurveyProgDialog != null && syncSurveyProgDialog.isShowing()) {
                syncSurveyProgDialog.cancel();
            }
            resturl.writeToTextFile(Log.getStackTraceString(e),
                    String.valueOf(values),
                    "SyncSurveyActivity_Startsurvey");
        }
        return syncSurveyAnsCode;
    }

    /**
     * method
     *
     * @param beneficiaryDetails
     */
    private void setBeneficiaryDataToValues(String beneficiaryDetails) {
        try {
            JSONArray jsonArray = new JSONArray(beneficiaryDetails);
            String getType = jsonArray.getJSONObject(0).getString("type");
            uuid = jsonArray.getJSONObject(0).getString("uuid");
            if ("Facility".equalsIgnoreCase(getType)) {
                facility_id = jsonArray.getJSONObject(0).getString("ben_id");
                beneficiary_id = String.valueOf(typeId);
            } else if ("Beneficiary".equalsIgnoreCase(getType)) {
                beneficiary_id = jsonArray.getJSONObject(0).getString("uuid");
                facility_id = jsonArray.getJSONObject(0).getString("fac_uuid");
            }
        } catch (JSONException e) {
            Logger.logE(TAG, "Exception on Beneficiary and facility based survey", e);
        }
    }

    /**
     * method
     *
     * @param datetime
     * @return
     */
    //to get the 10 digit time in mili seconds
    private long getUtcTimeInMillis(String datetime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormatStr);
            Date date = sdf.parse(datetime);

            // getInstance() provides TZ info which can be used to adjust to UTC
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            // Get timezone offset then use it to adjust the return value
            return cal.getTimeInMillis() / 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * method
     *
     * @param response
     */
    private void callNextActivity(String response) {
        if (!response.equals("")) {
            Intent intent = new Intent(activity, SurveyQuestionActivity.class);
            intent.putExtra("SurveyId", response);
            intent.putExtra(Constants.SURVEY_ID, String.valueOf(prefs.getInt(Constants.SURVEY_ID, 0)));
            context.startActivity(intent);
            if (syncSurveyProgDialog != null && syncSurveyProgDialog.isShowing()) {
                syncSurveyProgDialog.cancel();
            }
        } else {
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, preferences.getString(ClusterInfo.plsClickAgain, ""), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * method
     *
     * @param getResponseResult
     * @param surveyPrimaryKey
     */
    private void UpdateServerResponsetoDatabase(String getResponseResult, long surveyPrimaryKey) {
        final int surveyResponseID = (int) surveyPrimaryKey;
        try {
            final SharedPreferences contextSharedPreferences = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            List<Response> collectResponse = new ArrayList<>();
            JSONObject jsonObjectResponse = new JSONObject(getResponseResult);
            JSONObject jsonObjectGetQuestion = jsonObjectResponse.getJSONObject("response");

            Iterator<String> iter = jsonObjectGetQuestion.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                Logger.logD(TAG, "-->" + key);
                String getQuestionType = dbOpenHelper.getQuestionType(key);
                Object value = jsonObjectGetQuestion.get(key);
                Logger.logD(TAG, "-->" + value);
                if (("R").equalsIgnoreCase(getQuestionType) || ("S").equalsIgnoreCase(getQuestionType)) {
                    Response response = new Response(key, "", String.valueOf(value), "", Integer.parseInt(key), 0, "", 0, 0, getQuestionType);
                    collectResponse.add(response);
                } else if (("T").equalsIgnoreCase(getQuestionType) || ("D").equalsIgnoreCase(getQuestionType)) {
                    Response response = new Response(key, String.valueOf(value), "", "", Integer.parseInt(key), 0, "", 0, 0, getQuestionType);
                    collectResponse.add(response);
                }
            }
            new InsertResponseTask().insertResponseTask(collectResponse, surveyHandler, String.valueOf(surveyResponseID));
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("serverSurveyPID", "");
            editor.putString("serverResponseResult", "");
            editor.putBoolean("SaveDraftButtonFlag", false);
            editor.apply();
            if (!"".equals(preferences.getString("recentPreviewRecord", ""))) {
                if (languageId == preferences.getInt(Constants.SELECTEDLANGUAGE, 0)) {
                    SharedPreferences.Editor editor1 = preferences.edit();
                    editor1.putInt(Constants.SELECTEDLANGUAGE, languageId);
                    editor1.putInt(Constants.SELECTEDVALUE, languageId - 1);
                    editor1.apply();
                    Intent startIntert = new Intent(activity, ShowSurveyPreview.class);
                    startIntert.putExtra("surveyPrimaryKey", String.valueOf(surveyResponseID));
                    startIntert.putExtra("responseID", String.valueOf(surveyResponseID));
                    startIntert.putExtra(Constants.SURVEY_ID, String.valueOf(preferences.getInt(Constants.SURVEY_ID, 0)));
                    context.startActivity(startIntert);
                } else {
                    activity.runOnUiThread(() -> showLangugeChangePopUp(surveyResponseID, languageId));
                }
            } else {
                Intent intent = new Intent(activity, SurveyQuestionActivity.class);
                intent.putExtra("SurveyId", String.valueOf(surveyResponseID));
                Logger.logD(TAG, "-->" + contextSharedPreferences.getInt(Constants.SURVEY_ID, 0));
                intent.putExtra(Constants.SURVEY_ID, String.valueOf(preferences.getInt(Constants.SURVEY_ID, 0)));
                context.startActivity(intent);
            }
            if (syncSurveyProgDialog != null && syncSurveyProgDialog.isShowing()) {
                syncSurveyProgDialog.cancel();
            }

        } catch (JSONException e) {
            Logger.logE("", "", e);
        }

    }

    /**
     * method
     *
     * @param surveyResponseID
     * @param languageId
     */
    private void showLangugeChangePopUp(final int surveyResponseID, int languageId) {
        ConveneDatabaseHelper conveneDatabaseHelper = ConveneDatabaseHelper.getInstance(context, preferences.getString(Constants.CONVENE_DB, ""), preferences.getString("uId", ""));
        android.database.sqlite.SQLiteDatabase homepageDatabase = conveneDatabaseHelper.getWritableDatabase();
        final List<String> getRegionalLanguage = DataBaseMapperClass.getRegionalLanguage(homepageDatabase, languageId);

        String[] selectedLanguage = getRegionalLanguage.get(0).split("@");
        AlertDialog.Builder contentUpdateConfirmDialog = new AlertDialog.Builder(activity);
        contentUpdateConfirmDialog.setTitle("Language Change Alert").setMessage("Response submitted language is different, Are you sure you want to continue with " + selectedLanguage[1].toLowerCase() + " ?").setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor1 = preferences.edit();
                editor1.putInt(Constants.SELECTEDLANGUAGE, StartSurvey.this.languageId);
                editor1.putString(Constants.SELECTEDLANGUAGELABEL, selectedLanguage[1]);
                editor1.apply();
                Intent startIntert = new Intent(activity, ShowSurveyPreview.class);
                startIntert.putExtra("surveyPrimaryKey", String.valueOf(surveyResponseID));
                startIntert.putExtra("responseID", String.valueOf(surveyResponseID));
                startIntert.putExtra(PreferenceConstants.SURVEY_ID, String.valueOf(preferences.getInt(Constants.SURVEY_ID, 0)));
                context.startActivity(startIntert);
            }
        }).setNegativeButton("Cancel", (dialog, which) -> Logger.logD("setNegativeButton", "DialogInterface ")).show();
    }
}
