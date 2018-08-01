package org.mahiti.convenemis.database;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mahiti.convenemis.BeenClass.Activitylist;
import org.mahiti.convenemis.BeenClass.Levelid;
import org.mahiti.convenemis.BeenClass.Project;
import org.mahiti.convenemis.BeenClass.ProjectList;
import org.mahiti.convenemis.BeenClass.QuestionAnswer;
import org.mahiti.convenemis.BeenClass.ResponsesData;
import org.mahiti.convenemis.BeenClass.StatusBean;
import org.mahiti.convenemis.BeenClass.SurveysBean;
import org.mahiti.convenemis.BeenClass.beneficiary.Address;
import org.mahiti.convenemis.BeenClass.beneficiary.Datum;
import org.mahiti.convenemis.BeenClass.beneficiary.GetBeneficiaryDetails;
import org.mahiti.convenemis.BeenClass.beneficiaryList.Beneficiary;
import org.mahiti.convenemis.BeenClass.facilities.FacilitiesList;
import org.mahiti.convenemis.BeenClass.facilities.Facility;
import org.mahiti.convenemis.BeenClass.facilitiesBeen.FacilitySubTypeBeen;
import org.mahiti.convenemis.BeenClass.parentChild.Level1;
import org.mahiti.convenemis.BeenClass.parentChild.LevelBeen;
import org.mahiti.convenemis.BeenClass.parentChild.LevelFive;
import org.mahiti.convenemis.BeenClass.parentChild.LevelFour;
import org.mahiti.convenemis.BeenClass.parentChild.LevelOne;
import org.mahiti.convenemis.BeenClass.parentChild.LevelSeven;
import org.mahiti.convenemis.BeenClass.parentChild.LevelSix;
import org.mahiti.convenemis.BeenClass.parentChild.LevelThree;
import org.mahiti.convenemis.BeenClass.parentChild.LevelTwo;
import org.mahiti.convenemis.BeenClass.parentChild.LinkagesList;
import org.mahiti.convenemis.BeenClass.parentChild.LocationSurveyBeen;
import org.mahiti.convenemis.BeenClass.parentChild.SurveyDetail;
import org.mahiti.convenemis.BeenClass.parentChild.SurveyListDetails;
import org.mahiti.convenemis.BeenClass.service.ServiceList;
import org.mahiti.convenemis.R;
import org.mahiti.convenemis.utils.CommonForAllClasses;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.PeriodicityUtils;
import org.mahiti.convenemis.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExternalDbOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "ExternalDbOpenHelper";
    private static final String DAILY = "Daily";

    // Path to the device folder with databases
    private static String dbPath;
    //singleton/ single instance reference of database instance
    private static ExternalDbOpenHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private String mainQuery = "SELECT * from Surveys where surveyid=";
    public SQLiteDatabase database;

    public final Context context;
    private static final String SELECT = "Select";
    private static final String HALF_YEARLY = "Half Yearly";
    private static final String WEEKLY = "Weekly";
    private static final String MONTHLY = "Monthly";

    private static final String QUARTERLY = "Quarterly";
    private static final String FACILITYNAME = "facility_name";
    private static final String FACILITYTYPE = "facility_type";
    private static final String BENEFICIARY_NAME = "beneficiary_name";
    public static final String FACILITY_TABLE_NAME = "Facility";
    private static final String CONTACT_NO = "contact_no";
    private static final String btype = "btype";
    private static final String PRIMARY = "primary";
    private static final String FACILITY_NAME_KEY = "facility_name";
    private static final String CHILD_ID = "child_id";
    private static final String level = "level";
    private static final String ID_FROM = "_id  from ";
    private static final String FROM_TABLE = " From ";
    private static final String ORDER_LEVELS = "order_levels";
    private static final String labels = "labels";
    private static final String TABLE_WHERE = " where ";
    private static final String DATA_BASE_NAME = Constants.DBNAME;
    private static final String BEN_UUID = " and bene_uuid='";
    private static final String SELECT_FROM = "SELECT * from ";
    private static final String AND_SURVEY_ID = "' and survey_id=";
    private static final String STR_TO_CAPTURE_DATE = "' OR strftime('%Y %m', date(date_capture))='";
    private static final String STRFTIME_MONTH = "(strftime('%m', date(date_capture))='";
    private static final String STRFTIME_YEAR_CAPTURE = "strftime('%Y', date(date_capture))='";
    public static final String SELECT_QUERY_BENEFICIARY_JOIN = "SELECT b.server_primary,b.gender,b.parent_id,b.least_location_id,b.beneficiary_type,b.address,b.beneficiary_type_id, b.beneficiary_name,b.age,b.status,b.sync_status,b.contact_no,b.last_modified,b.least_location_name,b.uuid FROM beneficiary b LEFT JOIN Periodicity p ON p.bene_uuid = b.uuid WHERE ";
    public static final String SELECT_QUERY_FACILITY_JOIN = "SELECT b.id,b.boundary_name,b.boundary_id,b.facility_type,b.facility_name, b.facility_type_id,b.modified,b.thematic_area,b.uuid FROM Facility b LEFT JOIN Periodicity p ON p.faci_uuid = b.uuid WHERE ";
    private static final String LEAST_LOCATION_ID = "least_location_id";
    private static final String ONLINE_STATUS = "Online";
    private static final String AND_ACTIVE = " and  active = 2";
    private static final String ANDACTIVE_WITH_QUOTES = "' and  active = 2";
    private static final String SERVICES_STRING = "services";
    private static final String ADDRESS_UNDERSCORE_STRING = "address_";
    private static final String ADDRESS_STRING = "address";
    private static final String ADDRESS_ID_KEY = "address_id";
    private static final String ADDRESS1_KEY = "address1";
    private static final String ADDRESS2_KEY = "address2";
    private static final String LEAST_LOCATION_NAME_KEY = "least_location_name";
    private static final String PARTNER_NAME_KEY = "partner_name";
    private static final String BENEFICIARYIDS_KEY = "beneficiary_ids";
    private static final String CURSOR_COUNT_VALUE = "Count ";
    private static final String AND_LOCATION_TYPE = " and location_type = '";
    private static final String SELECT_DISTINCT_NAME_LEVEL = "SELECT DISTINCT name,level";
    private static final String LEVEL7 = "Level7";
    private static final String PERIODICITY_FLAG = "PeriodicityFlag";
    private static final String ADDRESS_DUMP = "address_dump";
    private static final String SURVEYNAME = "surveyName";
    private static final String PINCODE_KEY = "pincode";
    private static final String BENEFICIARY_TYPE_ID_KEY = "beneficiary_type_id";
    private static final String BOUNDARY_ID_KEY = "boundary_id";
    private static final String BENEFICIARY_TYPE_KEY = "beneficiary_type";
    private static final String MODIFIED_DATE_KEY = "modified_date";
    private static final String ACTIVE_STATUS = "active";
    private static final String LOCATION_TYPE_KEY = "location_type";
    private static final String FACILITY_TYPE_ID_KEY = "facility_type_id";
    private static final String FACILITY_SUBTYPE_KEY = "facility_subtype";
    private static final String FACILITY_SUBTYPE_ID_KEY = "facility_subtype_id";
    private static final String THEMATIC_AREA_KEY = "thematic_area";
    private static final String THEMATIC_AREA_ID_KEY = "thematic_area_id";
    private static final String MODIFIED_KEY = "modified";
    private static final String BOUNDARY_NAME_KEY = "boundary_name";
    private static final String PERIODICITY_KEY = "Periodicity";
    private static final String FACILITIY_IDS_KEY = "facility_ids";
    private static final String PARENT_ID_KEY = "parent_id";
    private static final String LAST_MODIFIED_KEY = "last_modified";
    private static final String STATUS_KEY = "status";
    private static final String OFFLINE_STATUS_KEY = "Offline";
    private static final String BOUNDARY_LEVEL_KEY = "boundary_level";
    private static final String LOCATION_LEVEL_KEY = "location_level";
    private static final String PROOF_ID_KEY = "proof_id";
    private static final String PARENT_UUID_KEY = "parent_uuid";
    private static final String PARENT_KEY = "parent";
    public static final String DBNAME = "CryMaster.sqlite";
    public static final String AND_BENEFICIARY_TYPE_ID = "' and beneficiary_type_id = '";
    public static final String BENEFICIARY_ID_KEY = "beneficiary_id";
    public static final String PARTNER_ID_KEY = "partner_id";
    public static final String GENDER_KEY = "gender";
    public static final String SERVER_DATETIME_KEY = "server_datetime";
    public static final String AND_CLUSTER_ID = " and cluster_id=";
    public static final String YEARLY = "Yearly";
    private static final String DOB = "dob";
    private static final String DOB_OPTION_KEY = "dob_option";
    private static final String ALIAS_NAME_KEY = "alias_name";
    private String stateIdStr = "state_id";
    private String selectBenQuery = "SELECT * FROM Beneficiary where beneficiary_type_id = ";
    private String tagStr = "The database path name getBeneficairyListDetails==>";
    private String tagQueryListStr = "the query is in  list is";
    private String tagQueryNameListStr = "the query is in Survey names list is";
    private String selectStrarFrom = "SELECT * from  ";
    private String dateYy_Mm_Dd = "yyyy-MM-dd";
    private String selectFromPeriodicityQuery = "SELECT * FROM Periodicity WHERE  (strftime('%Y %m', date(date_capture))='";
    private String updateStr = "Update";
    private String levelStr = "level";


    /**
     * @param context
     * @param databaseName
     * @param uId
     */
    public ExternalDbOpenHelper(Context context, String databaseName, String uId) {
        super(context, DBNAME, null, CommonForAllClasses.version);
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Write a full path to the databases of your application
        String packageName = context.getPackageName();
        dbPath = String.format(context.getString(R.string.databasepath), packageName);
        close();
        openDataBase();

    }

    /**
     * @param context      Context for calling constructor
     * @param databaseName
     * @param uId
     * @return
     */
    public static ExternalDbOpenHelper getInstance(Context context, String databaseName, String uId) {
        Logger.logV(TAG, "the path of the db in the getinstance is" + databaseName);
        if (dbHelper == null) {
            dbHelper = new ExternalDbOpenHelper(context, databaseName, uId);
        }
        return dbHelper;
    }

    private SQLiteDatabase openDataBase() {
        String path = dbPath + DBNAME;
        Logger.logV(TAG, "the path of the db is" + path);
        try {
            createDataBase();
            database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (Exception e) {
            Logger.logE(TAG, "Exception in inopenDataBase method ", e);
        }
        return database;
    }


    private void createDataBase() {
        boolean dbExist = this.checkDataBase();
        Logger.logV(TAG, "the  external db value is exist or not " + dbExist);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        if (dbExist) {
            String e = prefs.getString("q_dbversion_" + prefs.getString("USERID", ""), "");
            Logger.logV(TAG, "the external db value is" + e);
        } else {
            this.getReadableDatabase();
            try {
                SharedPreferences.Editor e1 = prefs.edit();
                e1.putString("dbversion", prefs.getString("USERID", "") + "_" + 1);
                e1.apply();
                copyDataBase();
            } catch (Exception var4) {
                Logger.logE(TAG, "Exception in createDataBase method ", var4);
            }
        }
    }


    private boolean checkDataBase() {
        try {
            final String mPath = dbPath + DBNAME/*+ sharedPreferences.getString(DATA_BASE_NAME, "")*/;
            Logger.logV(TAG, "the check database path is ........" + mPath);
            final File file = new File(mPath);
            return file.exists();
        } catch (SQLiteException e) {
            Logger.logE(TAG, "Exception in checkDataBase method ", e);
            return false;
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void copyDataBase() {
        InputStream myInput;
        String outFileName = dbPath + DBNAME/*+ sharedPreferences.getString(DATA_BASE_NAME, "")*/;

        try (FileOutputStream myOutput = new FileOutputStream(outFileName)) {
            myInput = context.getAssets().open("SurveyLevels.sqlite");

            Logger.logV(TAG, "the check jar database path in copydatabase........" + myOutput + "the input is" + myInput);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
    }

    @Override
    public synchronized void close() {
        if (database != null) {
            database.close();
        }
        super.close();
    }


    /**
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        /*Nothing to do in this method      */
    }


    /**
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*Nothing to do in this method      */
    }


    /**
     * To get the Order levels for that particular survey
     *
     * @param surveyId
     * @return
     */
    public String getOrderLevels(int surveyId) {
        String orders = "";
        SQLiteDatabase db = openDataBase();
        String query = mainQuery + surveyId;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            String levels = cursor.getString(cursor.getColumnIndex(ORDER_LEVELS));
            cursor.close();
            return levels;
        }
        return orders;
    }

    /**
     * To get the Order levels for that particular survey
     *
     * @param surveyId
     * @return
     */
    public String getOrderlabels(int surveyId) {
        String orders = "";
        SQLiteDatabase db = openDataBase();
        String query = mainQuery + surveyId;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            String levels = cursor.getString(cursor.getColumnIndex(labels));
            cursor.close();
            return levels;
        }
        return orders;
    }


    /**
     * To get the child id
     *
     * @param orderLevel
     * @param level
     * @return
     */
    public int getId(String orderLevel, String level) {
        Logger.logV(TAG, "the level is............" + level);
        int id = 0;
        SQLiteDatabase db = openDataBase();
        String query = SELECT_FROM + orderLevel + TABLE_WHERE + "name='" + level + "'";
        Cursor cursor = db.rawQuery(query, null);
        Logger.logV(TAG, "the value is" + query);
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex(orderLevel + "_id"));
            Logger.logV(TAG, "the names are" + id);
            return id;
        }
        if (cursor != null) {
            cursor.close();
        }
        return id;
    }


    /**
     * To get the child names of the survey
     *
     * @param parentLevel
     * @param currentLevel
     * @param id
     * @param sharedpreferences
     * @return
     */
    public Map<Integer, LocationSurveyBeen> getChildNamesList(String parentLevel, String currentLevel, int id, SharedPreferences sharedpreferences) {
        String clusterIds = "";
        SQLiteDatabase db = openDataBase();
        String query;
        query = SELECT_FROM + currentLevel + TABLE_WHERE + parentLevel + "_id = " + id;
        Cursor cursor = db.rawQuery(query, null);
        Logger.logV(TAG, "the query is in get child names list is" + query);
        Map<Integer, LocationSurveyBeen> beanList = new HashMap<>();
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                if (!("").equals(sharedpreferences.getString(Constants.BENEFICIARY_TYPE, ""))) {
                    LocationSurveyBeen locationSurveyBeen = new LocationSurveyBeen();
                    locationSurveyBeen.setClusterId(cursor.getInt(cursor.getColumnIndex("id")));
                    locationSurveyBeen.setLocationName(cursor.getString(cursor.getColumnIndex("name")));
                    locationSurveyBeen.setLocationLevel(currentLevel);
                    locationSurveyBeen.setSurveyStatusFlag(-1);
                    beanList.put(cursor.getInt(cursor.getColumnIndex("id")), locationSurveyBeen);
                    clusterIds = MessageFormat.format("{0}{1}{2}", clusterIds, ",", String.valueOf(cursor.getInt(cursor.getColumnIndex("id"))));
                } else {
                    LocationSurveyBeen locationSurveyBeen = new LocationSurveyBeen();
                    locationSurveyBeen.setClusterId(cursor.getInt(cursor.getColumnIndex(currentLevel + "_id")));
                    locationSurveyBeen.setLocationName(cursor.getString(cursor.getColumnIndex("name")));
                    locationSurveyBeen.setLocationLevel(currentLevel);
                    locationSurveyBeen.setSurveyStatusFlag(-1);
                    beanList.put(cursor.getInt(cursor.getColumnIndex(currentLevel + "_id")), locationSurveyBeen);
                    clusterIds = MessageFormat.format("{0}{1}{2}", clusterIds, ",", String.valueOf(cursor.getInt(cursor.getColumnIndex(currentLevel + "_id"))));
                }
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        if (!clusterIds.isEmpty()) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("clusterIds", clusterIds.substring(1, clusterIds.length()));
            editor.apply();
        }
        return beanList;
    }


    /**
     * Read records related to the search term
     *
     * @param searchTerm
     * @return
     */

    public List<String> searchVillages(String searchTerm) {
        List<String> searchList = new ArrayList<>();
        database = openDataBase();
        String sql = "";
        sql += "SELECT * FROM Level7 ";
        sql += TABLE_WHERE + "name" + " LIKE '" + searchTerm + "%'";
        Cursor cursor = database.rawQuery(sql, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                searchList.add(name);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return searchList;
    }

    /**
     * To get the Order levels for that particular survey
     *
     * @param orderLevel
     * @param surveyId
     * @return
     */
    public String getChildIds(String orderLevel, int surveyId) {
        String result = "";
        try {
            SQLiteDatabase db = openDataBase();
            String query = "SELECT * from ParentChild where parent_id= " + surveyId + " AND order_level='" + orderLevel + "'";
            Cursor cursor = db.rawQuery(query, null);
            Logger.logV(TAG, "the query is" + query);
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    result = cursor.getString(cursor.getColumnIndex(CHILD_ID));
                }
                while (cursor.moveToNext());
                cursor.close();
                return result;
            }
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
        return result;
    }

    /**
     * @param tableName
     * @param columnName
     * @return
     */
    public List<LevelBeen> getLevelValuesFromDB(String tableName, String columnName) {
        List<LevelBeen> list = new ArrayList<>();
        SQLiteDatabase db = openDataBase();
        String query = "Select DISTINCT level2_id, " + columnName + FROM_TABLE + tableName + " where active = 2 ";
        list.add(new LevelBeen(0, SELECT));
        Cursor cursor = db.rawQuery(query, null);
        Logger.logV(TAG, "Query for cluster" + query);
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                LevelBeen levelBeen = new LevelBeen();
                levelBeen.setId(cursor.getInt(cursor.getColumnIndex(Constants.LEVEL2_ID)));
                levelBeen.setName(cursor.getString(cursor.getColumnIndex("name")));
                list.add(levelBeen);
            } while (cursor.moveToNext());
            cursor.close();

        }
        return list;
    }


    /**
     * @param tableName
     * @param columnName
     * @param stateId
     * @return
     */
    public List<LevelBeen> getLevelTalukValuesFromDB(String tableName, String columnName, String stateId, String slugName) {
        List<LevelBeen> list = new ArrayList<>();
        SQLiteDatabase db = openDataBase();
        String query = "Select DISTINCT level4_id, " + columnName + FROM_TABLE + tableName + " where level3_id = " + stateId + AND_LOCATION_TYPE + slugName + ANDACTIVE_WITH_QUOTES;
        Cursor cursor = db.rawQuery(query, null);
        Logger.logD(TAG, "Sql query for Taluk" + query);
        list.add(new LevelBeen(0, SELECT));
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                LevelBeen levelBeen = new LevelBeen();
                levelBeen.setId(cursor.getInt(cursor.getColumnIndex(Constants.LEVEL4_ID)));
                levelBeen.setName(cursor.getString(cursor.getColumnIndex("name")));
                list.add(levelBeen);
            } while (cursor.moveToNext());
            cursor.close();

        }
        return list;
    }


    /**
     * @param tableName
     * @param columnName
     * @param stateId
     * @return
     */
    public List<LevelBeen> getLevelGrampanchayatValuesFromDB(String tableName, String columnName, String stateId, String slugName) {
        List<LevelBeen> list = new ArrayList<>();
        SQLiteDatabase db = openDataBase();
        String query = "Select DISTINCT level5_id, " + columnName + FROM_TABLE + tableName + " where level4_id = " + stateId + AND_LOCATION_TYPE + slugName + "' and active = 2 ";
        Cursor cursor = db.rawQuery(query, null);
        Logger.logD(TAG, "Sql query for Grampanchayat" + query);
        list.add(new LevelBeen(0, SELECT));
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                LevelBeen levelBeen = new LevelBeen();
                levelBeen.setId(cursor.getInt(cursor.getColumnIndex(Constants.LEVEL5_ID)));
                levelBeen.setName(cursor.getString(cursor.getColumnIndex("name")));
                list.add(levelBeen);
            } while (cursor.moveToNext());
            cursor.close();

        }
        return list;
    }

    /**
     * @param tableName
     * @param columnName
     * @param stateId
     * @return
     */
    public List<LevelBeen> getLevelVillageValuesFromDB(String tableName, String columnName, String stateId, String slugName) {
        List<LevelBeen> list = new ArrayList<>();
        SQLiteDatabase db = openDataBase();
        String query = "Select level6_id, " + columnName + FROM_TABLE + tableName + " where level5_id = " + stateId + AND_LOCATION_TYPE + slugName + ANDACTIVE_WITH_QUOTES;
        Cursor cursor = db.rawQuery(query, null);
        Logger.logD(TAG, "Sql query for Village" + query);
        list.add(new LevelBeen(0, SELECT));
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                LevelBeen levelBeen = new LevelBeen();
                levelBeen.setId(cursor.getInt(cursor.getColumnIndex(Constants.LEVEL6_ID)));
                levelBeen.setName(cursor.getString(cursor.getColumnIndex("name")));
                list.add(levelBeen);
            } while (cursor.moveToNext());
            cursor.close();

        }
        return list;
    }


    /**
     * @param tableName
     * @param columnName
     * @param s
     * @return
     */
    public List<LevelBeen> getLevelHamletDetails(String tableName, String columnName, String s, String slugName) {
        List<LevelBeen> list = new ArrayList<>();
        try {
            String query = "Select level7_id, " + columnName + FROM_TABLE + tableName + " where level6_id = " + s + AND_LOCATION_TYPE + slugName + ANDACTIVE_WITH_QUOTES;
            Cursor cursor = database.rawQuery(query, null);
            Logger.logD(TAG, "Sql query for Hamlet" + query);
            list.add(new LevelBeen(0, SELECT));
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    LevelBeen levelBeen = new LevelBeen();
                    levelBeen.setId(cursor.getInt(cursor.getColumnIndex(Constants.LEVEL7_ID)));
                    levelBeen.setName(cursor.getString(cursor.getColumnIndex("name")));
                    levelBeen.setLocationLevel(7);
                    list.add(levelBeen);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "exception on getting the hamlet details", e);
        }
        return list;
    }

    /**
     * method to get the level id of all the level tables
     *
     * @param tableName
     * @param levelNo
     * @param leastLocationId
     * @return
     */

    public int getLevelIdFromLevels(String tableName, int levelNo, String levelname, int leastLocationId) {
        int levelId = 0;
        try {
            SQLiteDatabase db = openDataBase();
            String query = "SELECT level" + levelNo + ID_FROM + tableName + TABLE_WHERE + levelname + "id =" + leastLocationId + AND_ACTIVE;
            Cursor cursor = db.rawQuery(query, null);
            Logger.logV(TAG, "get levelId name query" + query);
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
                levelId = cursor.getInt(cursor.getColumnIndex(level + levelNo + "_id"));
            }
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
        return levelId;
    }

    /**
     * @param tableName
     * @param columnName
     * @param stateId
     * @return
     */
    public List<LevelBeen> getLevelDistrictValuesFromDB(String tableName, String columnName, String stateId) {
        List<LevelBeen> list = new ArrayList<>();
        SQLiteDatabase db = openDataBase();
        String query = "Select DISTINCT level3_id, " + columnName + FROM_TABLE + tableName + " where level2_id = " + stateId + AND_ACTIVE;
        Cursor cursor = db.rawQuery(query, null);
        Logger.logD(TAG, "Sql query for District" + query);
        list.add(new LevelBeen(0, SELECT));
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                LevelBeen levelBeen = new LevelBeen();
                levelBeen.setId(cursor.getInt(cursor.getColumnIndex(Constants.LEVEL3_ID)));
                levelBeen.setName(cursor.getString(cursor.getColumnIndex("name")));
                list.add(levelBeen);
            } while (cursor.moveToNext());
            cursor.close();

        }
        return list;
    }

    /**
     * @param levelId For getting left side reference of level to get id's
     * @param ids
     * @return
     */

    public List<LevelBeen> getLevelsValues(String levelId, String ids) {
        /*
          Bellow code is to remove [] brackets from the array which is converted to string
		 */
        SQLiteDatabase db = openDataBase();
        Logger.logV(TAG, "The database path name getlevelvalues==>" + db.getPath());
        String query = SELECT_FROM + levelId + " where  level2_id IN (" + sharedPreferences.getString(stateIdStr, "") + ") and active=2 order by name COLLATE NOCASE ASC ";
        Cursor cursor = db.rawQuery(query, null);
        Logger.logV(TAG, "The query to  getlevelvalues==>" + query);
        List<LevelBeen> levelBeenList = new ArrayList<>();
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                levelBeenList.add(new LevelBeen(cursor.getInt(cursor.getColumnIndex(levelId + "_id")), cursor.getString(cursor.getColumnIndex("name"))));
            } while (cursor.moveToNext());
            cursor.close();
            levelBeenList.add(new LevelBeen(0, SELECT));
        }
        return levelBeenList;
    }


    /**
     * @param listOfObjects
     */
    public void updateBeneficiary(GetBeneficiaryDetails listOfObjects) {
        try {

            database = this.getWritableDatabase();
            Logger.logV(TAG, "The database path name updateBeneficiary==>" + database.getPath());
            for (int i = 0; i < listOfObjects.getData().size(); i++) {
                JSONArray jsonArray = new JSONArray();
                for (int j = 0; j < listOfObjects.getData().get(i).getAddress().size(); j++) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(ADDRESS1_KEY, listOfObjects.getData().get(i).getAddress().get(j).getAddress1());
                    jsonObject.put(ADDRESS2_KEY, listOfObjects.getData().get(i).getAddress().get(j).getAddress2());
                    jsonObject.put(LEAST_LOCATION_NAME_KEY, listOfObjects.getData().get(i).getAddress().get(j).getLeastLocationName());
                    jsonObject.put(ADDRESS_ID_KEY, listOfObjects.getData().get(i).getAddress().get(j).getAddressId());
                    jsonObject.put(BOUNDARY_ID_KEY, listOfObjects.getData().get(i).getAddress().get(j).getBoundaryId());
                    jsonObject.put(PINCODE_KEY, listOfObjects.getData().get(i).getAddress().get(j).getPincode());
                    jsonObject.put(PRIMARY, listOfObjects.getData().get(i).getAddress().get(j).getPrimary());
                    jsonObject.put(LOCATION_LEVEL_KEY, listOfObjects.getData().get(i).getAddress().get(j).getLocationLevel());
                    jsonObject.put(PROOF_ID_KEY, listOfObjects.getData().get(i).getAddress().get(j).getProofId());
                    jsonArray.put(jsonObject);
                }
                if (didUuidExist(Constants.BENEFICIARY_TABLENAME, listOfObjects.getData().get(i).getUuid())) {
                    ContentValues cv = new ContentValues();
                    cv.put(Constants.SYNC_STATUS, 2);
                    cv.put(STATUS_KEY, "");
                    cv.put(Constants.SERVER_PRIMARY, listOfObjects.getData().get(i).getId());
                    cv.put(Constants.LAST_MODIFIED, listOfObjects.getData().get(i).getModified());
                    cv.put(SERVER_DATETIME_KEY, listOfObjects.getData().get(i).getModified());
                    cv.put(Constants.LAST_MODIFIED, listOfObjects.getData().get(i).getModified());
                    cv.put(PARENT_UUID_KEY, listOfObjects.getData().get(i).getParent_uuid());
                    cv.put(ALIAS_NAME_KEY, listOfObjects.getData().get(i).getAliasName());
                    cv.put(DOB, listOfObjects.getData().get(i).getDateOfBirth());
                    cv.put(ADDRESS_STRING, jsonArray.toString());
                    cv.put(DOB_OPTION_KEY, listOfObjects.getData().get(i).getDobOption());
                    cv.put(Constants.PARENT, listOfObjects.getData().get(i).getParent());
                    cv.put(Constants.PARENT_ID, listOfObjects.getData().get(i).getParentId());
                    cv.put(Constants.AGE, listOfObjects.getData().get(i).getAge());
                    cv.put(Constants.CREATED_DATE, listOfObjects.getData().get(i).getCreated());
                    cv.put(LEAST_LOCATION_ID, listOfObjects.getData().get(i).getAddress().get(0).getBoundaryId());
                    cv.put(LEAST_LOCATION_NAME_KEY, listOfObjects.getData().get(i).getAddress().get(0).getLeastLocationName());
                    database.update(Constants.BENEFICIARY_TABLENAME, cv, "uuid" + " = ?", new String[]{listOfObjects.getData().get(i).getUuid()});
                    Logger.logV(TAG, "Beneficiary record Updated ===>" + cv.toString());
                } else {
                    ContentValues cv = new ContentValues();
                    cv.put(Constants.SERVER_PRIMARY, listOfObjects.getData().get(i).getId());
                    cv.put(DOB_OPTION_KEY, listOfObjects.getData().get(i).getDobOption());
                    cv.put(Constants.LAST_MODIFIED, listOfObjects.getData().get(i).getModified());
                    cv.put(SERVER_DATETIME_KEY, listOfObjects.getData().get(i).getModified());
                    cv.put(PARENT_UUID_KEY, listOfObjects.getData().get(i).getParent_uuid());
                    cv.put(Constants.BENEFICIARY_NAME, listOfObjects.getData().get(i).getName());
                    cv.put(Constants.BENEFICIARY_TYPE, listOfObjects.getData().get(i).getBeneficiaryType());
                    cv.put(Constants.ACTIVE, listOfObjects.getData().get(i).getActive());
                    cv.put(Constants.LAST_MODIFIED, listOfObjects.getData().get(i).getModified());
                    cv.put(Constants.PARENT, listOfObjects.getData().get(i).getParent());
                    cv.put(Constants.PARENT_ID, listOfObjects.getData().get(i).getParentId());
                    cv.put(Constants.BENEFICIARY_TYPE_ID, listOfObjects.getData().get(i).getBeneficiaryTypeId());
                    cv.put(Constants.PARTNER_ID, listOfObjects.getData().get(i).getPartnerId());
                    cv.put(CONTACT_NO, listOfObjects.getData().get(i).getContactNo().toString());
                    cv.put(Constants.UUID, listOfObjects.getData().get(i).getUuid());
                    cv.put(DOB, listOfObjects.getData().get(i).getDateOfBirth());
                    cv.put(ALIAS_NAME_KEY, listOfObjects.getData().get(i).getAliasName());
                    cv.put(STATUS_KEY, "");
                    cv.put(LEAST_LOCATION_ID, listOfObjects.getData().get(i).getAddress().get(0).getBoundaryId());
                    cv.put(LEAST_LOCATION_NAME_KEY, listOfObjects.getData().get(i).getAddress().get(0).getLeastLocationName());
                    cv.put(ADDRESS_STRING, jsonArray.toString());
                    cv.put(PARTNER_NAME_KEY, listOfObjects.getData().get(i).getPartner());
                    cv.put(GENDER_KEY, listOfObjects.getData().get(i).getGender());
                    cv.put(Constants.SYNC_STATUS, 2);
                    cv.put(Constants.AGE, listOfObjects.getData().get(i).getAge());
                    cv.put(Constants.CREATED_DATE, listOfObjects.getData().get(i).getCreated());
                    cv = FilterUtils.getSevenLevels(cv, listOfObjects.getData().get(i).getAddress().get(0).getBoundaryId(), database);
                    database.insertWithOnConflict(Constants.BENEFICIARY_TABLENAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                    Logger.logV(TAG, "Beneficiary record inserted ===>" + cv.toString());


                }
            }
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
    }

    /**
     * @param beneficarytype
     * @param villageId
     * @return
     */
    public List<Datum> getFacilityNames(String beneficarytype, String villageId) {
        List<Datum> labelsStrings = new ArrayList<>();
        String beneficaiaryName = "";
        int beneficiaryId;
        String beneficiaryType;
        SQLiteDatabase db = openDataBase();
        try {
            Logger.logV(TAG, "The database path name getBeneficairynamess==>" + db.getPath());
            String query = "SELECT * from Facility where facility_type_id = '" + beneficarytype + "'";
            Cursor cursor = db.rawQuery(query, null);
            Logger.logV(TAG, "the Facility query " + query);
            Datum datum1 = new Datum();
            datum1.setName(Constants.SELECT);
            labelsStrings.add(datum1);
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    beneficaiaryName = cursor.getString(cursor.getColumnIndex(FACILITYNAME));
                    beneficiaryId = cursor.getInt(cursor.getColumnIndex(Constants.SERVER_PRIMARY));
                    beneficiaryType = cursor.getString(cursor.getColumnIndex(FACILITYTYPE));
                    Datum datum = new Datum();
                    datum.setName(beneficaiaryName);
                    datum.setBeneficiaryTypeId(beneficiaryId);
                    datum.setBeneficiaryType(beneficiaryType);
                    datum.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                    labelsStrings.add(datum);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }

        return labelsStrings;
    }

    /**
     * @param beneficarytype
     * @param villageId
     * @return
     */
    public List<Datum> getBeneficaryNames(String beneficarytype, String villageId) {
        List<Datum> labelsStrings = new ArrayList<>();
        String beneficaiaryName = "";
        int beneficiaryId;
        String beneficiaryType;
        SQLiteDatabase db = openDataBase();
        Logger.logV(TAG, "The database path name getBeneficairynamess==>" + db.getPath());
        String query = "SELECT * from Beneficiary where beneficiary_type_id = '" + beneficarytype + "'";
        Cursor cursor = db.rawQuery(query, null);
        Logger.logV(TAG, "the beneficiary query " + query);
        Datum datum1 = new Datum();
        datum1.setName(Constants.SELECT);
        labelsStrings.add(datum1);
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                beneficaiaryName = cursor.getString(cursor.getColumnIndex(BENEFICIARY_NAME));
                beneficiaryId = cursor.getInt(cursor.getColumnIndex(Constants.SERVER_PRIMARY));
                beneficiaryType = cursor.getString(cursor.getColumnIndex(BENEFICIARY_TYPE_KEY));
                Datum datum = new Datum();
                datum.setName(beneficaiaryName);
                datum.setBeneficiaryTypeId(beneficiaryId);
                datum.setBeneficiaryType(beneficiaryType);
                labelsStrings.add(datum);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return labelsStrings;
    }


    /**
     * To get the Order levels for that particular survey
     *
     * @param orderLevel
     * @param levelId
     * @param ids
     * @return
     */
    public List<Integer> getLevelIds(String orderLevel, String levelId, String ids) {

        List<Integer> list = new ArrayList<>();
        try {
            SQLiteDatabase db = openDataBase();
            Logger.logV(TAG, "The database path name getlevelidss==>" + db.getPath());
            String query;
            if (ids != null && !ids.isEmpty())
                query = "SELECT " + levelId + "_id from " + orderLevel + TABLE_WHERE + orderLevel + "_id IN (" + ids + ")";
            else
                query = "SELECT " + levelId + "_id from " + orderLevel;
            Cursor cursor = db.rawQuery(query, null);
            Logger.logV(TAG, "the query is in get level ids are" + query);
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    list.add(cursor.getInt(cursor.getColumnIndex(levelId + "_id")));
                }
                while (cursor.moveToNext());
                cursor.close();
                return list;
            }
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
        return list;
    }


    /**
     * @param surveyId
     */
    public void deleteSurveys(int surveyId) {
        String query;
        database = this.getWritableDatabase();
        query = "DELETE FROM Surveys where surveyId=" + surveyId;
        database.execSQL(query);
    }


    /**
     * @param listOfObjects
     */
    public void updateLevel1List(LevelOne listOfObjects) {

        try {
            ContentValues cv = new ContentValues();
            database = this.getWritableDatabase();
            Logger.logV(TAG, "The database path name updatelevel1==>" + database.getPath());
            for (int i = 0; i < listOfObjects.getLevel1().size(); i++) {
                cv.put(Constants.ID, listOfObjects.getLevel1().get(i).getLevel1Id());
                cv.put(Constants.LEVEL1_ID, listOfObjects.getLevel1().get(i).getLevel1Id());
                cv.put("name", listOfObjects.getLevel1().get(i).getName());
                cv.put(Constants.MODIFIED_DATE, listOfObjects.getLevel1().get(i).getModifiedDate());
                cv.put(Constants.ACTIVE, listOfObjects.getLevel1().get(i).getActive());
                cv.put(Constants.LOCATION_TYPE, listOfObjects.getLevel1().get(i).getLocationType());
                database.insertWithOnConflict("Level1", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                Logger.logV(TAG, "Adding country to level 1 table ==?" + cv.toString());
            }

        } catch (Exception e) {
            Logger.logE("", "", e);
        }
    }


    /**
     * @param listOfObjects
     */
    public void updateLevel2List(LevelTwo listOfObjects) {

        try {
            ContentValues cv = new ContentValues();
            database = this.getWritableDatabase();
            Logger.logV(TAG, "The database path name updatelevel2==>" + database.getPath());
            Log.v(TAG, "Answers size updateLevel2List : " + listOfObjects.getLevel2().size());
            for (int i = 0; i < listOfObjects.getLevel2().size(); i++) {
                cv.put("id", listOfObjects.getLevel2().get(i).getLevel2Id());
                cv.put(Constants.LEVEL1_ID, listOfObjects.getLevel2().get(i).getLevel1Id());
                cv.put(Constants.LEVEL2_ID, listOfObjects.getLevel2().get(i).getLevel2Id());
                cv.put("name", listOfObjects.getLevel2().get(i).getName());
                cv.put(MODIFIED_DATE_KEY, listOfObjects.getLevel2().get(i).getModifiedDate());
                cv.put(ACTIVE_STATUS, listOfObjects.getLevel2().get(i).getActive());
                cv.put(LOCATION_TYPE_KEY, listOfObjects.getLevel2().get(i).getLocationType());
                database.insertWithOnConflict("Level2", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                Logger.logV(TAG, "Adding state to level 2 table ==?" + cv.toString());
            }

        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
    }

    /**
     * @param listOfObjects
     */
    public void updateLevel3List(LevelThree listOfObjects) {

        try {
            ContentValues cv = new ContentValues();
            database = this.getWritableDatabase();
            Logger.logV(TAG, "The database path name updatelevel3==>" + database.getPath());
            Log.v(TAG, "Answers size : in level 3 is" + listOfObjects.getLevel3().size());
            for (int i = 0; i < listOfObjects.getLevel3().size(); i++) {
                Logger.logV(TAG, "the level3 id names are" + listOfObjects.getLevel3().get(i).getLevel3Id());
                Logger.logV(TAG, "the level3 level1 id names are" + listOfObjects.getLevel3().get(i).getLevel1Id());
                Logger.logV(TAG, "the level3 level2 id names are" + listOfObjects.getLevel3().get(i).getLevel2Id());
                Logger.logV(TAG, "the level3 level3 id names are" + listOfObjects.getLevel3().get(i).getLevel3Id());
                Logger.logV(TAG, "the level3 name are" + listOfObjects.getLevel3().get(i).getName());
                Logger.logV(TAG, "the level3 getModifiedDate are" + listOfObjects.getLevel3().get(i).getModifiedDate());
                Logger.logV(TAG, "the level3 getActive are" + listOfObjects.getLevel3().get(i).getActive());

                cv.put("id", listOfObjects.getLevel3().get(i).getLevel3Id());
                cv.put(Constants.LEVEL1_ID, listOfObjects.getLevel3().get(i).getLevel1Id());
                cv.put(Constants.LEVEL2_ID, listOfObjects.getLevel3().get(i).getLevel2Id());
                cv.put(Constants.LEVEL3_ID, listOfObjects.getLevel3().get(i).getLevel3Id());
                cv.put("name", listOfObjects.getLevel3().get(i).getName());
                cv.put(MODIFIED_DATE_KEY, listOfObjects.getLevel3().get(i).getModifiedDate());
                cv.put(ACTIVE_STATUS, listOfObjects.getLevel3().get(i).getActive());
                cv.put(LOCATION_TYPE_KEY, listOfObjects.getLevel3().get(i).getLocationType());
                database.insertWithOnConflict("Level3", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                Logger.logV(TAG, "Adding district to level 3 table ==?" + cv.toString());
            }

        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
    }


    /**
     * @param listOfObjects
     */
    public void updateLevel4List(LevelFour listOfObjects) {

        try {
            ContentValues cv = new ContentValues();
            database = this.getWritableDatabase();
            Logger.logV(TAG, "The database path name updatelevel4==>" + database.getPath());
            Log.v(TAG, "Answers size updateLevel4List : " + listOfObjects.getLevel4().size());
            for (int i = 0; i < listOfObjects.getLevel4().size(); i++) {
                cv.put("id", listOfObjects.getLevel4().get(i).getLevel4Id());
                cv.put(Constants.LEVEL1_ID, listOfObjects.getLevel4().get(i).getLevel1Id());
                cv.put(Constants.LEVEL2_ID, listOfObjects.getLevel4().get(i).getLevel2Id());
                cv.put(Constants.LEVEL3_ID, listOfObjects.getLevel4().get(i).getLevel3Id());
                cv.put(Constants.LEVEL4_ID, listOfObjects.getLevel4().get(i).getLevel4Id());
                cv.put("name", listOfObjects.getLevel4().get(i).getName());
                cv.put(MODIFIED_DATE_KEY, listOfObjects.getLevel4().get(i).getModifiedDate());
                cv.put(ACTIVE_STATUS, listOfObjects.getLevel4().get(i).getActive());
                cv.put(LOCATION_TYPE_KEY, listOfObjects.getLevel4().get(i).getLocationType());
                database.insertWithOnConflict("Level4", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                Logger.logV(TAG, "Adding taluk to level 4 table ==?" + cv.toString());
            }

        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
    }

    /**
     * @param levelId For getting left side reference of level to get id's
     * @param ids
     * @return
     */
    public JSONObject getLevels7Values(String levelId, String ids) {
        /*
		  Bellow code is to remove [] brackets from the array which is converted to string
		 */
        JSONObject level7Obj = new JSONObject();
        SQLiteDatabase db = openDataBase();
        String query = "SELECT * from Level7" + TABLE_WHERE + "level7_id =" + ids + " order by name COLLATE NOCASE ASC ";
        Cursor cursor = db.rawQuery(query, null);
        Logger.logV(TAG, "the ids in getLevels7Values are" + query);
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            try {
                do {
                    level7Obj.put("id", cursor.getInt(cursor.getColumnIndex("id")));
                    level7Obj.put(Constants.LEVEL1_ID, cursor.getInt(cursor.getColumnIndex(Constants.LEVEL1_ID)));
                    level7Obj.put(Constants.LEVEL2_ID, cursor.getInt(cursor.getColumnIndex(Constants.LEVEL2_ID)));
                    level7Obj.put(Constants.LEVEL3_ID, cursor.getInt(cursor.getColumnIndex(Constants.LEVEL3_ID)));
                    level7Obj.put(Constants.LEVEL4_ID, cursor.getInt(cursor.getColumnIndex(Constants.LEVEL4_ID)));
                    level7Obj.put(Constants.LEVEL5_ID, cursor.getInt(cursor.getColumnIndex(Constants.LEVEL5_ID)));
                    level7Obj.put(Constants.LEVEL6_ID, cursor.getInt(cursor.getColumnIndex(Constants.LEVEL6_ID)));
                    level7Obj.put(Constants.LEVEL7_ID, cursor.getInt(cursor.getColumnIndex(Constants.LEVEL7_ID)));
                    level7Obj.put("name", cursor.getString(cursor.getColumnIndex("name")));
                    level7Obj.put(MODIFIED_DATE_KEY, cursor.getString(cursor.getColumnIndex(MODIFIED_DATE_KEY)));
                    level7Obj.put(ACTIVE_STATUS, cursor.getInt(cursor.getColumnIndex(ACTIVE_STATUS)));

                } while (cursor.moveToNext());
                cursor.close();
            } catch (Exception e) {
                Logger.logE(TAG, "", e);
            }
        }
        return level7Obj;
    }


    /**
     * @param listOfObjects
     */
    public void updateLevel5List(LevelFive listOfObjects) {

        try {
            ContentValues cv = new ContentValues();
            database = this.getWritableDatabase();
            Logger.logV(TAG, "The database path name updatelevel5==>" + database.getPath());
            Log.v(TAG, "Answers size updateLevel5List: " + listOfObjects.getLevel5().size());
            for (int i = 0; i < listOfObjects.getLevel5().size(); i++) {
                cv.put("id", listOfObjects.getLevel5().get(i).getLevel5Id());
                cv.put(Constants.LEVEL1_ID, listOfObjects.getLevel5().get(i).getLevel1Id());
                cv.put(Constants.LEVEL2_ID, listOfObjects.getLevel5().get(i).getLevel2Id());
                cv.put(Constants.LEVEL3_ID, listOfObjects.getLevel5().get(i).getLevel3Id());
                cv.put(Constants.LEVEL4_ID, listOfObjects.getLevel5().get(i).getLevel4Id());
                cv.put(Constants.LEVEL5_ID, listOfObjects.getLevel5().get(i).getLevel5Id());
                cv.put("name", listOfObjects.getLevel5().get(i).getName());
                cv.put(MODIFIED_DATE_KEY, listOfObjects.getLevel5().get(i).getModifiedDate());
                cv.put(ACTIVE_STATUS, listOfObjects.getLevel5().get(i).getActive());
                cv.put(LOCATION_TYPE_KEY, listOfObjects.getLevel5().get(i).getLocationType());
                database.insertWithOnConflict("Level5", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                Logger.logV(TAG, "Adding level 5 table ==?" + cv.toString());
            }

        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
    }


    /**
     * @param listOfObjects
     */
    public void updateLevel6List(LevelSix listOfObjects) {

        try {
            ContentValues cv = new ContentValues();
            database = this.getWritableDatabase();
            Logger.logV(TAG, "The database path name updatelevel6==>" + database.getPath());
            for (int i = 0; i < listOfObjects.getLevel6().size(); i++) {
                cv.put("id", listOfObjects.getLevel6().get(i).getLevel6Id());
                cv.put(Constants.LEVEL1_ID, listOfObjects.getLevel6().get(i).getLevel1Id());
                cv.put(Constants.LEVEL2_ID, listOfObjects.getLevel6().get(i).getLevel2Id());
                cv.put(Constants.LEVEL3_ID, listOfObjects.getLevel6().get(i).getLevel3Id());
                cv.put(Constants.LEVEL4_ID, listOfObjects.getLevel6().get(i).getLevel4Id());
                cv.put(Constants.LEVEL5_ID, listOfObjects.getLevel6().get(i).getLevel5Id());
                cv.put(Constants.LEVEL6_ID, listOfObjects.getLevel6().get(i).getLevel6Id());
                cv.put("name", listOfObjects.getLevel6().get(i).getName());
                cv.put(MODIFIED_DATE_KEY, listOfObjects.getLevel6().get(i).getModifiedDate());
                cv.put(ACTIVE_STATUS, listOfObjects.getLevel6().get(i).getActive());
                cv.put(LOCATION_TYPE_KEY, listOfObjects.getLevel6().get(i).getLocationType());
                database.insertWithOnConflict("Level6", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                Logger.logV(TAG, "Adding level 6 table ==?" + cv.toString());
            }

        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
    }


    /**
     * updateLevel7List method
     *
     * @param listOfObjects
     */
    public void updateLevel7List(LevelSeven listOfObjects) {

        try {
            ContentValues cv = new ContentValues();
            database = this.getWritableDatabase();
            Logger.logV(TAG, "The database path name updatelevel7==>" + database.getPath());
            for (int i = 0; i < listOfObjects.getLevel7().size(); i++) {

                cv.put("id", listOfObjects.getLevel7().get(i).getLevel7Id());
                cv.put(Constants.LEVEL1_ID, listOfObjects.getLevel7().get(i).getLevel1Id());
                cv.put(Constants.LEVEL2_ID, listOfObjects.getLevel7().get(i).getLevel2Id());
                cv.put(Constants.LEVEL3_ID, listOfObjects.getLevel7().get(i).getLevel3Id());
                cv.put(Constants.LEVEL4_ID, listOfObjects.getLevel7().get(i).getLevel4Id());
                cv.put(Constants.LEVEL5_ID, listOfObjects.getLevel7().get(i).getLevel5Id());
                cv.put(Constants.LEVEL6_ID, listOfObjects.getLevel7().get(i).getLevel6Id());
                cv.put(Constants.LEVEL7_ID, listOfObjects.getLevel7().get(i).getLevel7Id());
                cv.put("name", listOfObjects.getLevel7().get(i).getName());
                cv.put(MODIFIED_DATE_KEY, listOfObjects.getLevel7().get(i).getModifiedDate());
                cv.put(ACTIVE_STATUS, listOfObjects.getLevel7().get(i).getActive());
                cv.put(LOCATION_TYPE_KEY, listOfObjects.getLevel7().get(i).getLocationType());
                database.insertWithOnConflict(LEVEL7, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                Logger.logV(TAG, "Adding level 7 table ==?" + cv.toString());
            }

        } catch (Exception e) {
            Logger.logE("", "", e);
        }
    }


    /**
     * @param serviceList
     */
    public void updateServices(ServiceList serviceList) {
        try {
            for (int i = 0; i < serviceList.getData().size(); i++) {
                ContentValues cv = new ContentValues();
                database = this.getWritableDatabase();
                Logger.logV(TAG, "The database path name updateServices==>" + database.getPath());
                cv.put("id", serviceList.getData().get(i).getId());
                cv.put(Constants.SERVICE_NAME, serviceList.getData().get(i).getName());
                cv.put("service_type_id", serviceList.getData().get(i).getServiceTypeId());
                cv.put("service_subtype_id", serviceList.getData().get(i).getServiceSubtypeId());
                cv.put(Constants.THEMATIC_AREA_ID, serviceList.getData().get(i).getThematicAreaId());
                cv.put(Constants.BENEFICIARY_ID, serviceList.getData().get(i).getBeneficiaryId());
                cv.put("service_type", serviceList.getData().get(i).getService_type());
                cv.put("service_subtype", serviceList.getData().get(i).getService_subtype());
                cv.put(Constants.THEMATIC_AREA_NAME, serviceList.getData().get(i).getThematic_area());
                cv.put(ACTIVE_STATUS, serviceList.getData().get(i).getActive());
                cv.put(Constants.MODIFIED, serviceList.getData().get(i).getModified());
                database.insertWithOnConflict("Service", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                Logger.logV(TAG, "the Service updated successfully===>" + cv.toString());
            }
        } catch (Exception e) {
            Logger.logE(TAG, "Exception while inserting into services table", e);
        }

    }


    /**
     * @param facilitiesList
     */
    public void updateFacilities(FacilitiesList facilitiesList) {
        try {
            for (int i = 0; i < facilitiesList.getData().size(); i++) {
                ContentValues cv = new ContentValues();
                database = this.getWritableDatabase();
                Logger.logV(TAG, "The database path name updateFacilities==>" + database.getPath());
                cv.put(FACILITYNAME, facilitiesList.getData().get(i).getName());
                cv.put(FACILITYTYPE, facilitiesList.getData().get(i).getFacilityType());
                cv.put(FACILITY_TYPE_ID_KEY, facilitiesList.getData().get(i).getFacilityTypeId());
                cv.put(FACILITY_SUBTYPE_KEY, facilitiesList.getData().get(i).getFacilitySubtype());
                cv.put(FACILITY_SUBTYPE_ID_KEY, facilitiesList.getData().get(i).getFacilitySubtypeId());
                cv.put(BENEFICIARY_ID_KEY, facilitiesList.getData().get(i).getBeneficiaryId());
                cv.put(btype, facilitiesList.getData().get(i).getBtype());
                cv.put(ACTIVE_STATUS, facilitiesList.getData().get(i).getActive());
                cv.put(THEMATIC_AREA_KEY, facilitiesList.getData().get(i).getThematicArea());
                cv.put(THEMATIC_AREA_ID_KEY, facilitiesList.getData().get(i).getThematicAreaId());
                cv.put(SERVER_DATETIME_KEY, facilitiesList.getData().get(i).getModified());
                cv.put(MODIFIED_KEY, facilitiesList.getData().get(i).getModified());
                cv.put(BOUNDARY_NAME_KEY, facilitiesList.getData().get(i).getBoundaryName());
                cv.put(BOUNDARY_ID_KEY, facilitiesList.getData().get(i).getBoundaryId());
                cv.put(PARTNER_NAME_KEY, facilitiesList.getData().get(i).getPartner());
                cv.put(ADDRESS1_KEY, facilitiesList.getData().get(i).getAddress1());
                cv.put(ADDRESS2_KEY, facilitiesList.getData().get(i).getAddress2());
                cv.put(PINCODE_KEY, facilitiesList.getData().get(i).getPincode());
                cv.put(SERVICES_STRING, String.valueOf(facilitiesList.getData().get(i).getServices()));
                cv.put(LOCATION_LEVEL_KEY, facilitiesList.getData().get(i).getBoundaryLevel());
                cv.put(Constants.SYNC_STATUS, 2);
                cv.put("uuid", facilitiesList.getData().get(i).getUuid());
                cv.put(STATUS_KEY, "");
                cv = FilterUtils.getFacilityServey(cv, facilitiesList.getData().get(i).getBoundaryLevel(), facilitiesList.getData().get(i).getBoundaryId(), database);
                cv.put(Constants.CREATED_DATE, facilitiesList.getData().get(i).getCreated());
                cv.put(Constants.SERVER_PRIMARY, facilitiesList.getData().get(i).getId());
                Logger.logV(TAG, "the Facility record Updated ===>" + cv.toString());
                // update or insert_replace
                if (didUuidExist(FACILITY_TABLE_NAME, facilitiesList.getData().get(i).getUuid())) {
                    database.update(FACILITY_TABLE_NAME, cv, "uuid" + " = ?", new String[]{facilitiesList.getData().get(i).getUuid()});
                } else {
                    database.insertWithOnConflict(FACILITY_TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                }
            }
        } catch (Exception e) {
            Logger.logE(TAG, "Exception while inserting into services table", e);
        }
    }


    /**
     * method to get the synced completed records from periodicity table
     *
     * @param uuid
     * @param surveyId
     * @param beneficiaryIds
     * @param facilityIds    @return
     */
    public List<StatusBean> getCompletedRecords(String uuid, int surveyId, String beneficiaryIds, String facilityIds) {
        String pendingSurveyQuery = "";
        if (!beneficiaryIds.equalsIgnoreCase(""))
            pendingSurveyQuery = "SELECT * FROM Periodicity where bene_uuid='" + uuid + AND_SURVEY_ID + surveyId;
        else
            pendingSurveyQuery = "SELECT * FROM Periodicity where faci_uuid='" + uuid + AND_SURVEY_ID + surveyId;

        return getCompletedListWithQuery(pendingSurveyQuery);
    }


    /**
     * method to get the synced completed records from periodicity table
     *
     * @param uuid
     * @param surveyId
     * @return
     */
    public List<StatusBean> getLocationBasedCompletedRecords(int uuid, int surveyId) {
        return getCompletedListWithQuery("SELECT * FROM Periodicity where cluster_id =" + uuid + " and survey_id=" + surveyId);
    }

    private List<StatusBean> getCompletedListWithQuery(String pendingSurveyQuery) {
        List<StatusBean> syncedList = new ArrayList<>();
        Cursor cursor;
        SQLiteDatabase db = openDataBase();
        cursor = db.rawQuery(pendingSurveyQuery, null);
        Logger.logD(TAG, "Get Periodiicty details from  Table" + pendingSurveyQuery);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("response_id"));
                int pendSurveyId = cursor.getInt(cursor.getColumnIndex("survey_id"));
                String dateCapture = cursor.getString(cursor.getColumnIndex(Constants.dateCaptureStr));
                syncedList = getPeriodicityFlag(pendSurveyId, dateCapture, id);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return syncedList;
    }

    /**
     * @param SurveyId
     * @param dateCapture
     * @return
     */
    private List<StatusBean> getPeriodicityFlag(int SurveyId, String dateCapture, int periodocityPid) {
        List<StatusBean> syncedList = new ArrayList<>();

        try {
            String pendingSurveyQuery = "SELECT PeriodicityFlag , surveyName   FROM Surveys where  surveyId=" + SurveyId;
            Cursor cursor;
            SQLiteDatabase db = openDataBase();
            cursor = db.rawQuery(pendingSurveyQuery, null);
            Logger.logD("blockquery", "Get Periodicity response from  Table" + pendingSurveyQuery);
            if (cursor.moveToFirst()) {
                do {
                    String PeriodicityFlag = cursor.getString(cursor.getColumnIndex(PERIODICITY_FLAG));
                    String surveyName = cursor.getString(cursor.getColumnIndex(SURVEYNAME));
                    StatusBean sb = new StatusBean();
                    sb.setPrimaryId(String.valueOf(periodocityPid));
                    sb.setName(surveyName);
                    sb.setLang(PeriodicityFlag); /// here setLang is holding the periodicity flag .
                    sb.setDate(dateCapture);
                    syncedList.add(sb);

                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logV("", "getPeriodicityFlag Responses from Survey table");
        }
        return syncedList;

    }


    /**
     * @param benefciaryType
     * @return
     */
    public List<SurveyDetail> getUpdatedSurveyList(String benefciaryType) {
        String query;
        SQLiteDatabase db = openDataBase();
        Logger.logV(TAG, "The database path name getUpdatedSurveyList==>" + db.getPath());
        query = "SELECT * from Surveys";
        Cursor cursor = db.rawQuery(query, null);
        Log.v("the cursor count is", "the c count is" + cursor.getCount());
        List<SurveyDetail> surveyDetailList = new ArrayList<>();
        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                try {
                    SurveyDetail surveyDetail = new SurveyDetail(cursor.getString(cursor.getColumnIndex(SURVEYNAME)),
                            cursor.getInt(cursor.getColumnIndex("pFuture")),
                            cursor.getInt(cursor.getColumnIndex(Constants.P_LIMIT)),
                            cursor.getInt(cursor.getColumnIndex(PERIODICITY_KEY)),
                            cursor.getString(cursor.getColumnIndex(labels)),
                            cursor.getString(cursor.getColumnIndex("vn")),
                            cursor.getString(cursor.getColumnIndex(BENEFICIARY_TYPE_KEY)),
                            cursor.getString(cursor.getColumnIndex(BENEFICIARYIDS_KEY)),
                            cursor.getInt(cursor.getColumnIndex("bConfig")),
                            cursor.getInt(cursor.getColumnIndex("reasonDisagree")),
                            cursor.getString(cursor.getColumnIndex(ORDER_LEVELS)),
                            cursor.getInt(cursor.getColumnIndex(Constants.SURVEY_ID_COLUMN)),
                            cursor.getInt(cursor.getColumnIndex("qConfig")),
                            cursor.getString(cursor.getColumnIndex("summaryQid")), null, cursor.getString(cursor.getColumnIndex(FACILITYTYPE)), cursor.getString(cursor.getColumnIndex(FACILITIY_IDS_KEY)),
                            cursor.getString(cursor.getColumnIndex(PERIODICITY_FLAG)),
                            cursor.getString(cursor.getColumnIndex("constraints")));
                    surveyDetailList.add(surveyDetail);
                } catch (Exception e) {
                    Logger.logE(TAG, "", e);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return surveyDetailList;
    }
    /**
     * @param surveyid
     * @return
     */
    public List<SurveyDetail> getUpdatedSurvey(String surveyid) {
        String query;
        SQLiteDatabase db = openDataBase();
        Logger.logV(TAG, "The database path name getUpdatedSurveyList==>" + db.getPath());
        query = "SELECT * from Surveys where Surveys.surveyId="+surveyid;
        Cursor cursor = db.rawQuery(query, null);
        Log.v("the cursor count is", "the c count is" + cursor.getCount());
        List<SurveyDetail> surveyDetailList = new ArrayList<>();
        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                try {
                    SurveyDetail surveyDetail = new SurveyDetail(cursor.getString(cursor.getColumnIndex(SURVEYNAME)),
                            cursor.getInt(cursor.getColumnIndex("pFuture")),
                            cursor.getInt(cursor.getColumnIndex(Constants.P_LIMIT)),
                            cursor.getInt(cursor.getColumnIndex(PERIODICITY_KEY)),
                            cursor.getString(cursor.getColumnIndex(labels)),
                            cursor.getString(cursor.getColumnIndex("vn")),
                            cursor.getString(cursor.getColumnIndex(BENEFICIARY_TYPE_KEY)),
                            cursor.getString(cursor.getColumnIndex(BENEFICIARYIDS_KEY)),
                            cursor.getInt(cursor.getColumnIndex("bConfig")),
                            cursor.getInt(cursor.getColumnIndex("reasonDisagree")),
                            cursor.getString(cursor.getColumnIndex(ORDER_LEVELS)),
                            cursor.getInt(cursor.getColumnIndex(Constants.SURVEY_ID_COLUMN)),
                            cursor.getInt(cursor.getColumnIndex("qConfig")),
                            cursor.getString(cursor.getColumnIndex("summaryQid")), null, cursor.getString(cursor.getColumnIndex(FACILITYTYPE)), cursor.getString(cursor.getColumnIndex(FACILITIY_IDS_KEY)),
                            cursor.getString(cursor.getColumnIndex(PERIODICITY_FLAG)),
                            cursor.getString(cursor.getColumnIndex("constraints")));
                    surveyDetailList.add(surveyDetail);
                } catch (Exception e) {
                    Logger.logE(TAG, "", e);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return surveyDetailList;
    }


    /**
     * @param surveyListDetails
     */
    public void updateSurveyList(SurveyListDetails surveyListDetails) {

        try {
            ContentValues cv = new ContentValues();
            database = this.getWritableDatabase();
            Logger.logV(TAG, "The database path name UpdatedSurveyList==>" + database.getPath());
            Log.v(TAG, "Answers size updateSurveyList: " + surveyListDetails.getSurveyDetails().size());
            for (int i = 0; i < surveyListDetails.getSurveyDetails().size(); i++) {
                int surveyId = surveyListDetails.getSurveyDetails().get(i).getSurveyId();
                Log.v(TAG, "Answers size SurveyId : " + surveyListDetails.getSurveyDetails().get(i).getSurveyId());
                deleteSurveys(surveyId);
                cv.put(SURVEYNAME, surveyListDetails.getSurveyDetails().get(i).getSurveyName());
                Log.v(TAG, "Answers size SURVEYNAME : " + surveyListDetails.getSurveyDetails().get(i).getSurveyName());

                cv.put("pFuture", surveyListDetails.getSurveyDetails().get(i).getPFeature());
                Log.v(TAG, "Answers size pFuture: " + surveyListDetails.getSurveyDetails().get(i).getPFeature());

                cv.put(Constants.P_LIMIT, surveyListDetails.getSurveyDetails().get(i).getPLimit());
                Log.v(TAG, "Answers size pLimit: " + surveyListDetails.getSurveyDetails().get(i).getPLimit());

                cv.put("qCode", 0);
                Log.v(TAG, "Answers size qCode: " + surveyListDetails.getSurveyDetails().get(i).getSurveyName());

                cv.put(PERIODICITY_KEY, surveyListDetails.getSurveyDetails().get(i).getPiriodicity());

                Log.v(TAG, "Answers size Periodicity: " + surveyListDetails.getSurveyDetails().get(i).getPiriodicity());

                cv.put(labels, surveyListDetails.getSurveyDetails().get(i).getLabels());
                Log.v(TAG, "Answers size labels : " + surveyListDetails.getSurveyDetails().get(i).getLabels());

                cv.put("vn", Float.parseFloat(surveyListDetails.getSurveyDetails().get(i).getVn()));
                Log.v(TAG, "Answers size :vn " + surveyListDetails.getSurveyDetails().get(i).getVn());

                cv.put("bConfig", surveyListDetails.getSurveyDetails().get(i).getBConfig());
                Log.v(TAG, "Answers size vn : " + surveyListDetails.getSurveyDetails().get(i).getBConfig());

                cv.put("reasonDisagree", surveyListDetails.getSurveyDetails().get(i).getReasonDisagree());
                Log.v(TAG, "Answers size reasonDisagree: " + surveyListDetails.getSurveyDetails().get(i).getReasonDisagree());


                cv.put(ORDER_LEVELS, surveyListDetails.getSurveyDetails().get(i).getOrderLevels());
                Log.v(TAG, "Answers size ORDER_LEVELS : " + surveyListDetails.getSurveyDetails().get(i).getOrderLevels());

                cv.put("pCode", surveyListDetails.getSurveyDetails().get(i).getPcode());
                Log.v(TAG, "Answers size pCode: " + surveyListDetails.getSurveyDetails().get(i).getPcode());

                cv.put(Constants.SURVEY_ID_COLUMN, surveyListDetails.getSurveyDetails().get(i).getSurveyId());
                Log.v(TAG, "Answers size surveyId : " + surveyListDetails.getSurveyDetails().get(i).getSurveyId());

                cv.put("qConfig", surveyListDetails.getSurveyDetails().get(i).getQConfig());
                Log.v(TAG, "Answers size qConfig: " + surveyListDetails.getSurveyDetails().get(i).getQConfig());

                cv.put(BENEFICIARY_TYPE_KEY, surveyListDetails.getSurveyDetails().get(i).getBeneficiaryType());
                Log.v(TAG, "Answers size BENEFICIARY_TYPE_KEY : " + surveyListDetails.getSurveyDetails().get(i).getBeneficiaryType());


                cv.put(BENEFICIARYIDS_KEY, surveyListDetails.getSurveyDetails().get(i).getBeneficiaryIds());
                Log.v(TAG, "Answers size BENEFICIARYIDS_KEY : " + surveyListDetails.getSurveyDetails().get(i).getBeneficiaryIds());

                cv.put("summaryQid", surveyListDetails.getSurveyDetails().get(i).getSummaryQid());
                Log.v(TAG, "Answers size summaryQid : " + surveyListDetails.getSurveyDetails().get(i).getSummaryQid());

                cv.put(FACILITIY_IDS_KEY, surveyListDetails.getSurveyDetails().get(i).getFacilityIds());
                Log.v(TAG, "Answers size :facility_ids " + surveyListDetails.getSurveyDetails().get(i).getFacilityIds());

                cv.put(PERIODICITY_FLAG, surveyListDetails.getSurveyDetails().get(i).getPiriodicityFlag());
                Log.v(TAG, "PeriodicityFlag PERIODICITY_FLAG size : " + surveyListDetails.getSurveyDetails().get(i).getPiriodicityFlag());

                cv.put(BENEFICIARY_TYPE_KEY, surveyListDetails.getSurveyDetails().get(i).getBeneficiaryType());
                Log.v(TAG, "PeriodicityFlag beneficiary size : " + surveyListDetails.getSurveyDetails().get(i).getBeneficiaryType());

                cv.put(FACILITYTYPE, surveyListDetails.getSurveyDetails().get(i).getFacilityType());
                Log.v(TAG, "PeriodicityFlag facility size : " + surveyListDetails.getSurveyDetails().get(i).getFacilityType());


                cv.put("category_name", surveyListDetails.getSurveyDetails().get(i).getCategoryName());
                Log.v(TAG, "category_name  : " + surveyListDetails.getSurveyDetails().get(i).getCategoryName());

                cv.put("category_id", surveyListDetails.getSurveyDetails().get(i).getCategoryId());
                Log.v(TAG, "category_id   : " + surveyListDetails.getSurveyDetails().get(i).getCategoryId());
                List<String> getPid = updateLinkageTable(surveyListDetails.getSurveyDetails().get(i).getLinkagesDetails(), database);
                Log.v(TAG, "linkages: " + getPid);

                cv.put("survey_type", getPid.toString());
                Log.v(TAG, "survey_type: " + getPid.toString());

                cv.put("constraints", surveyListDetails.getSurveyDetails().get(i).getConstraints());
                Log.v(TAG, "constraints: " + surveyListDetails.getSurveyDetails().get(i).getConstraints());
                Gson gson= new Gson();
                String getSubBeneficiaryIds=gson.toJson(surveyListDetails.getSurveyDetails().get(i).getParentLink());
                cv.put("sub_beneficiary_ids", getSubBeneficiaryIds);
                Log.v(TAG, "sub_beneficiary_ids: " + getSubBeneficiaryIds);
                database.insertWithOnConflict("Surveys", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            }

        } catch (Exception e) {
            Logger.logE("", "", e);
        }
    }

    private List<String> updateLinkageTable(List<LinkagesList> linkages, SQLiteDatabase database) {
        List<String> getformTypeIds = new ArrayList<>();
        long getInsertedID = -1;
        try {
            if (!linkages.isEmpty()) {
                ContentValues cvL = new ContentValues();
                for (int k = 0; k < linkages.size(); k++) {
                    LinkagesList linkagesList = linkages.get(k);
                    int rationalId = linkagesList.getRelation_id();
                    int form_type_id = linkagesList.getForm_type_id();
                    String uuid = linkagesList.getUuid();
                    String name = linkagesList.getName();
                    cvL.put("relation_id", rationalId);
                    cvL.put("form_type_id", form_type_id);
                    cvL.put("uuid", uuid);
                    cvL.put("name", name);

                    getInsertedID = database.insertWithOnConflict("SurveyLinkage", null, cvL, SQLiteDatabase.CONFLICT_REPLACE);
                    getformTypeIds.add(String.valueOf(form_type_id));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return getformTypeIds;
    }


    /**
     * @param table
     * @param columnNmae
     * @param dbHelper
     * @return
     */
    public String getLastUpDate(String table, String columnNmae, ExternalDbOpenHelper dbHelper) {
        String date = "";
        String selectQuery = "SELECT MAX(" + columnNmae + ") AS " + columnNmae + FROM_TABLE + table;
        Logger.logD(TAG, "current date--" + selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Logger.logV(TAG, "The database path name Lastupdate==>" + db.getPath());
        Cursor cursor = db.rawQuery(selectQuery, null);
        Logger.logD(TAG, "current date--" + cursor.getCount());

        if (cursor.moveToFirst()) {
            date = cursor.getString(cursor.getColumnIndex(columnNmae));
            cursor.close();
        }
        db.close();
        if (date == null)
            date = "";
        Logger.logD(TAG, "getLastUpDate--" + date);
        return date;
    }


    /**
     * To get the Order levels for that particular survey
     *
     * @param surveyId
     * @param level
     * @return
     */
    public String getListOrderLevels(int surveyId, String level) {
        String orders = "";
        SQLiteDatabase db = openDataBase();
        String query = "SELECT * from ParentChild where parent_id= " + surveyId + " and order_level='" + level + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            String levels = cursor.getString(cursor.getColumnIndex("order_level"));
            cursor.close();
            return levels;
        }
        return orders;
    }


    /**
     * @param btypeid
     * @param filterOrder
     * @return
     */
    public List<Datum> getBeneficiaryListFromDb(String btypeid, int filterOrder, String lastModifiedDate) {
        List<Datum> beneficiaryNameDatumList = new ArrayList<>();
        SQLiteDatabase db = openDataBase();
        String query = "";
        String beneficiaryQuery = selectBenQuery;
        Logger.logV(TAG, tagStr + db.getPath());
        switch (filterOrder) {
            case 0:
                query = beneficiaryQuery + btypeid + "   order by beneficiary_name COLLATE NOCASE ASC";
                break;
            case 1:
                query = beneficiaryQuery + btypeid + "   order by createdDate ASC  ";
                break;
            case 2:
                query = beneficiaryQuery + btypeid + "   order by createdDate DESC  ";
                break;
            case 5:
                query = beneficiaryQuery + btypeid + " GROUP BY least_location_id  ";
                break;
            default:
                query = beneficiaryQuery + btypeid + " order by createdDate DESC  ";
        }
        try {
            Cursor cursor = db.rawQuery(query, null);
            Logger.logV(TAG, "the query is in  list is ---->" + query + CURSOR_COUNT_VALUE + cursor.getCount());
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    Datum datum = new Datum();
                    datum.setId(cursor.getInt(cursor.getColumnIndex(Constants.SERVER_PRIMARY)));
                    datum.setParentId(cursor.getInt(cursor.getColumnIndex(PARENT_ID_KEY)));
                    datum.setBeneficiaryType(cursor.getString(cursor.getColumnIndex(BENEFICIARY_TYPE_KEY)));
                    datum.setBeneficiaryTypeId(cursor.getInt(cursor.getColumnIndex(BENEFICIARY_TYPE_ID_KEY)));
                    datum.setName(cursor.getString(cursor.getColumnIndex(BENEFICIARY_NAME)));
                    datum.setModified(cursor.getString(cursor.getColumnIndex(SERVER_DATETIME_KEY)));
                    datum.setParent(cursor.getString(cursor.getColumnIndex(PARENT_KEY)));
                    datum.setDateOfBirth(cursor.getString(cursor.getColumnIndex(DOB)));
                    datum.setDobOption(cursor.getString(cursor.getColumnIndex(DOB_OPTION_KEY)));
                    datum.setStatus("");
                    datum.setAliasName(cursor.getString(cursor.getColumnIndex(ALIAS_NAME_KEY)));
                    String addressString = cursor.getString(cursor.getColumnIndex(ADDRESS_STRING));
                    List<Address> addressList;
                    if (cursor.getInt(cursor.getColumnIndex(Constants.SYNC_STATUS)) == 2)
                        addressList = callOnlineParce(addressString);
                    else
                        addressList = callofflineParce(addressString);

                    datum.setAddress(addressList);
                    datum.setParent(cursor.getString(cursor.getColumnIndex(PARENT_KEY)));
                    datum.setPartner(cursor.getString(cursor.getColumnIndex(PARTNER_NAME_KEY)));
                    datum.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                    datum.setAge(cursor.getString(cursor.getColumnIndex("age")));
                    datum.setSyncStatus(cursor.getInt(cursor.getColumnIndex(Constants.SYNC_STATUS)));
                    datum.setGender(cursor.getString(cursor.getColumnIndex(GENDER_KEY)));
                    datum.setCreated(cursor.getString(cursor.getColumnIndex(Constants.CREATED_DATE)));
                    datum.setLeast_location_id(cursor.getInt(cursor.getColumnIndex(LEAST_LOCATION_ID)));
                    datum.setParent_uuid(cursor.getString(cursor.getColumnIndex(PARENT_UUID_KEY)));
                    datum.setLeast_location_name(cursor.getString(cursor.getColumnIndex(LEAST_LOCATION_NAME_KEY)));
                    String statusValue = cursor.getString(cursor.getColumnIndex(STATUS_KEY));
                    if (("").equals(statusValue) || (ONLINE_STATUS).equals(statusValue) || (null) == statusValue) {
                        datum.setStatus("");
                    } else {
                        datum.setStatus(OFFLINE_STATUS_KEY);
                    }
                    List<String> contactList = Arrays.asList(cursor.getString(cursor.getColumnIndex(CONTACT_NO)).split(","));
                    datum.setContactNo(contactList);
                    beneficiaryNameDatumList.add(datum);
                }
                while (cursor.moveToNext());
                cursor.close();
            } else {
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "Exception on getting the beneficiary list from Beneficiary table", e);
        }

        return beneficiaryNameDatumList;
    }

    private List<Address> callofflineParce(String addressString) {
        List<Address> tempList;
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject(addressString);
            for (int i = 0; i < jsonObject.length(); i++) {
                JSONObject tempJson = jsonObject.getJSONObject(ADDRESS_UNDERSCORE_STRING + i);
                jsonArray.put(tempJson);
            }
            tempList = callOnlineParce(String.valueOf(jsonArray));
        } catch (JSONException e) {
            e.printStackTrace();
            tempList = callOnlineParce(addressString);
        }
        Logger.logV(TAG, "GuruCheckOfter " + jsonArray);


        return tempList;
    }

    private List<Address> callOnlineParce(String addressString) {
        JSONArray jsonArray = null;
        List<Address> addressList = new ArrayList<>();
        try {
            jsonArray = new JSONArray(addressString);

            for (int j = 0; j < jsonArray.length(); j++) {
                Address address = new Address();
                JSONObject jsonobject = jsonArray.getJSONObject(j);
                if (jsonobject.has(ADDRESS1_KEY))
                    address.setAddress1(jsonobject.getString(ADDRESS1_KEY));
                if (jsonobject.has(ADDRESS2_KEY))
                    address.setAddress2(jsonobject.getString(ADDRESS2_KEY));
                if (jsonobject.has(LOCATION_LEVEL_KEY)) {
                    address.setLocationLevel(jsonobject.getInt(LOCATION_LEVEL_KEY));
                }
                if (jsonobject.has(LEAST_LOCATION_NAME_KEY)) {
                    address.setLeastLocationName(jsonobject.getString(LEAST_LOCATION_NAME_KEY));
                }

                if (jsonobject.has(BOUNDARY_ID_KEY)) {
                    address.setLeastLocationId(jsonobject.getInt(BOUNDARY_ID_KEY));
                }
                if (jsonobject.has(PRIMARY)) {
                    address.setPrimary(jsonobject.getInt(PRIMARY));
                }
                if (jsonobject.has(PROOF_ID_KEY))
                    address.setProofId(jsonobject.getString(PROOF_ID_KEY));
                if (jsonobject.has(PINCODE_KEY))
                    address.setPincode(jsonobject.getString(PINCODE_KEY));
                if (jsonobject.has(ADDRESS_ID_KEY))
                    address.setAddressId(jsonobject.getString(ADDRESS_ID_KEY));
                addressList.add(address);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return addressList;

    }


    /**
     * @param btype
     * @param filterOrder
     * @return
     */
    public List<org.mahiti.convenemis.BeenClass.facilities.Datum> getFacilityDetailsFromDB(String btype, int filterOrder, String lastModifiedDate) {
        List<org.mahiti.convenemis.BeenClass.facilities.Datum> facilityList = new ArrayList<>();
        List<Integer> servicesList = new ArrayList<>();
        SQLiteDatabase db = openDataBase();
        String query = "";
        String facilityQuery = "SELECT * FROM Facility where facility_type_id = '";
        Logger.logV(TAG, tagStr + db.getPath());
        switch (filterOrder) {
            case 0:
                query = facilityQuery + btype + "' order by facility_name COLLATE NOCASE ASC";
                break;
            case 1:
                query = facilityQuery + btype + "' order by  createdDate ASC ";
                break;
            case 2:
                query = facilityQuery + btype + "' order by  createdDate DESC";
                break;
            default:
                query = facilityQuery + btype + "' order by createdDate DESC ";
                break;
        }

        try {
            Cursor cursor = db.rawQuery(query, null);
            Logger.logV(TAG, tagQueryListStr + query + CURSOR_COUNT_VALUE + cursor.getCount());
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    org.mahiti.convenemis.BeenClass.facilities.Datum datum = new org.mahiti.convenemis.BeenClass.facilities.Datum();
                    datum.setBoundaryName(cursor.getString(cursor.getColumnIndex(BOUNDARY_NAME_KEY)));
                    datum.setBoundaryId(String.valueOf(cursor.getInt(cursor.getColumnIndex(BOUNDARY_ID_KEY))));
                    datum.setFacilityType(cursor.getString(cursor.getColumnIndex(FACILITYTYPE)));
                    datum.setName(cursor.getString(cursor.getColumnIndex(FACILITYNAME)));
                    datum.setFacilitySubtype(cursor.getString(cursor.getColumnIndex(FACILITY_SUBTYPE_KEY)));
                    datum.setFacilitySubtypeId(cursor.getInt(cursor.getColumnIndex(FACILITY_SUBTYPE_ID_KEY)));
                    datum.setFacilityTypeId(cursor.getInt(cursor.getColumnIndex(FACILITY_TYPE_ID_KEY)));
                    datum.setModified(cursor.getString(cursor.getColumnIndex(MODIFIED_KEY)));
                    datum.setCreated(cursor.getString(cursor.getColumnIndex(Constants.CREATED_DATE)));
                    datum.setThematicArea(cursor.getString(cursor.getColumnIndex(THEMATIC_AREA_KEY)));
                    datum.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                    datum.setId(cursor.getInt(cursor.getColumnIndex(Constants.SERVER_PRIMARY)));
                    datum.setAddress1(cursor.getString(cursor.getColumnIndex(ADDRESS1_KEY)));
                    datum.setAddress2(cursor.getString(cursor.getColumnIndex(ADDRESS2_KEY)));
                    datum.setPincode(cursor.getString(cursor.getColumnIndex(PINCODE_KEY)));
                    datum.setPartner(cursor.getString(cursor.getColumnIndex(PARTNER_NAME_KEY)));
                    datum.setSyncStatus(cursor.getInt(cursor.getColumnIndex(Constants.SYNC_STATUS)));
                    datum.setBoundaryLevel(cursor.getString(cursor.getColumnIndex(LOCATION_LEVEL_KEY)));
                    String service = cursor.getString(cursor.getColumnIndex(SERVICES_STRING));
                    Logger.logD(TAG, "Service getFacilityDetailsFromDB value " + service);
                    String statusValue = cursor.getString(cursor.getColumnIndex(STATUS_KEY));
                    Logger.logD(TAG, "getFacilityDetailsFromDB value " + statusValue);
                    if (("").equalsIgnoreCase(statusValue) || (null) == (statusValue) || (ONLINE_STATUS).equalsIgnoreCase(statusValue)) {
                        datum.setStatus("");
                    } else {
                        datum.setStatus(OFFLINE_STATUS_KEY);
                    }
                    String servicesSplitedString = service.replaceAll("\\[", "").replaceAll("\\]", "");
                    if (!servicesSplitedString.isEmpty()) {
                        String[] serviceList = servicesSplitedString.split(",");
                        servicesList = new ArrayList<>();
                        for (int i = 0; i < serviceList.length; i++) {
                            servicesList.add(Integer.parseInt(serviceList[i].trim()));
                        }
                        datum.setServices(servicesList);
                    } else {
                        datum.setServices(servicesList);
                    }
                    facilityList.add(datum);
                }
                while (cursor.moveToNext());
                cursor.close();
            } else {
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE("", "", e);
        }
        return facilityList;
    }


    private void setServicesToBean(String service, org.mahiti.convenemis.BeenClass.facilities.Datum datum, List<Integer> servicesList) {
        String servicesSplitedString = service.replaceAll("\\[", "").replaceAll("\\]", "");
        if (!servicesSplitedString.isEmpty()) {
            String[] serviceList = servicesSplitedString.split(",");
            servicesList = new ArrayList<>();
            for (String aServiceList : serviceList) {
                servicesList.add(Integer.parseInt(aServiceList.trim()));
            }
            datum.setServices(servicesList);
        } else {
            datum.setServices(servicesList);
        }
    }

    /**
     * @return while doing mother and child this HH selection is required
     */
    public List<Datum> getHouseHoldFromDb() {
        List<Datum> datumList = new ArrayList<>();
        SQLiteDatabase db = openDataBase();
        Datum datum1 = new Datum();
        datum1.setName(SELECT);
        datumList.add(datum1);
        Logger.logV(TAG, tagStr + db.getPath());
        String query = "SELECT *  FROM Beneficiary where  beneficiary_type_id = 2 order by server_datetime DESC";
        try {
            Cursor cursor = db.rawQuery(query, null);
            Logger.logV(TAG, tagQueryNameListStr + query);
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    Datum datum = new Datum();
                    datum.setId(cursor.getInt(cursor.getColumnIndex(Constants.SERVER_PRIMARY)));
                    datum.setName(cursor.getString(cursor.getColumnIndex(BENEFICIARY_NAME)));
                    datum.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                    datum.setParent_uuid(cursor.getString(cursor.getColumnIndex(PARENT_UUID_KEY)));
                    String addressString = cursor.getString(cursor.getColumnIndex(ADDRESS_STRING));
                    Logger.logD(TAG, "address String from database getHouseHoldFromDb" + addressString);
                    JSONArray jsonArray = new JSONArray(addressString);
                    List<Address> addressList = new ArrayList<>();
                    for (int j = 0; j < jsonArray.length(); j++) {
                        Address address = new Address();
                        JSONObject jsonobject = jsonArray.getJSONObject(j);
                        address.setAddress1(jsonobject.getString(ADDRESS1_KEY));
                        address.setAddress2(jsonobject.getString(ADDRESS2_KEY));
                        address.setLocationLevel(jsonobject.getInt(LOCATION_LEVEL_KEY));
                        address.setLeastLocationName(jsonobject.getString(LEAST_LOCATION_NAME_KEY));
                        address.setLeastLocationId(jsonobject.getInt(BOUNDARY_ID_KEY));
                        address.setPrimary(jsonobject.getInt(PRIMARY));
                        address.setPincode(jsonobject.getString(PINCODE_KEY));
                        address.setAddressId(jsonobject.getString(ADDRESS_ID_KEY));
                        if (jsonobject.has(PROOF_ID_KEY))
                            address.setProofId(jsonobject.getString(PROOF_ID_KEY));
                        addressList.add(address);
                    }
                    datum.setAddress(addressList);
                    List<String> contactList = Arrays.asList(cursor.getString(cursor.getColumnIndex(CONTACT_NO)).split(","));
                    datum.setContactNo(contactList);
                    datumList.add(datum);
                } while (cursor.moveToNext());

            }
            cursor.close();
        } catch (Exception e) {
            Logger.logE("", "", e);
        }
        return datumList;
    }


    /**
     * @return
     */
    public List<String> getSyncFacilityOfflineRecord() {

        List<String> syncPendingRecord = new ArrayList<>();
        try {
            String selectQuery = "SELECT * from Facility where sync_status=1 order by uuid DESC";
            SQLiteDatabase databases = this.getReadableDatabase();
            Cursor cursor = databases.rawQuery(selectQuery, null);
            if (cursor.moveToFirst() && cursor.getCount() > 0) {
                do {
                    syncPendingRecord.add(String.valueOf(cursor.getInt(cursor.getColumnIndex("uuid"))));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Logger.logE("", "", e);
        }
        Logger.logD(TAG, "offline meeting Records IDS Size===" + syncPendingRecord.size());

        return syncPendingRecord;
    }


    /**
     * @return
     */
    public List<String> getSyncBeneficiaryOfflineRecord() {

        List<String> syncPendingRecord = new ArrayList<>();

        try {
            String selectQuery = "SELECT * from Beneficiary where sync_status=1 order by uuid DESC";
            SQLiteDatabase databases = this.getReadableDatabase();
            Cursor cursor = databases.rawQuery(selectQuery, null);
            if (cursor.moveToFirst() && cursor.getCount() > 0) {
                do {
                    syncPendingRecord.add(cursor.getString(cursor.getColumnIndex("uuid")));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Logger.logE("", "", e);
        }
        Logger.logD(TAG, "offline meeting Records IDS Size===" + syncPendingRecord.size());

        return syncPendingRecord;
    }


    /**
     * @param tableName
     * @return
     */
    public List<org.mahiti.convenemis.BeenClass.service.Datum> getServiceName(String tableName) {
        List<org.mahiti.convenemis.BeenClass.service.Datum> serviceList = new ArrayList<>();
        SQLiteDatabase db = openDataBase();
        Logger.logV(TAG, tagStr + db.getPath());
        String query = SELECT_FROM + tableName + " ORDER by id";
        Cursor cursor = db.rawQuery(query, null);
        Logger.logV(TAG, "the query is in getservice name list is" + query + "Count->" + cursor.getCount());
        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                org.mahiti.convenemis.BeenClass.service.Datum datum = new org.mahiti.convenemis.BeenClass.service.Datum();
                datum.setName(cursor.getString(cursor.getColumnIndex("service_name")));
                datum.setService_type(cursor.getString(cursor.getColumnIndex("service_type")));
                datum.setServiceTypeId(cursor.getInt(cursor.getColumnIndex("service_type_id")));
                datum.setService_subtype(cursor.getString(cursor.getColumnIndex("service_subtype")));
                datum.setServiceTypeId(cursor.getInt(cursor.getColumnIndex("service_subtype_id")));
                datum.setBeneficiaryId(cursor.getInt(cursor.getColumnIndex(BENEFICIARY_ID_KEY)));
                datum.setThematic_area(cursor.getString(cursor.getColumnIndex(THEMATIC_AREA_KEY)));
                datum.setActive(cursor.getInt(cursor.getColumnIndex(ACTIVE_STATUS)));
                datum.setThematicAreaId(cursor.getInt(cursor.getColumnIndex(THEMATIC_AREA_ID_KEY)));
                datum.setModified(cursor.getString(cursor.getColumnIndex(MODIFIED_KEY)));
                datum.setId(cursor.getInt(cursor.getColumnIndex("id")));
                serviceList.add(datum);
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return serviceList;

    }

    public boolean didUuidExist(String tableName, String uuid) {

        String[] allColumns = {"uuid"};
        database = this.getReadableDatabase();

        Cursor cursor = database.query(tableName, allColumns,
                "uuid" + " = '" + uuid + "'", null, null, null,
                null);
        cursor.moveToFirst();
        boolean retBool = false;
        if (!cursor.isAfterLast()) {
            retBool = true;
        }
        cursor.close();
        return retBool;
    }


    /**
     * @param responseDump
     * @return
     */
    public long insertIntoBeneficiaryTemp(JSONObject responseDump) {
        SimpleDateFormat dateFormatYyMmDdHhMmSs = new SimpleDateFormat(Constants.DATE_FORMAT_YY_MM_DD_HH_MM_SS, Locale.US);
        long response_id = 0;
        try {
            ContentValues cv = new ContentValues();
            database = this.getWritableDatabase();
            cv.put(PARTNER_ID_KEY, responseDump.getString(PARTNER_ID_KEY));
            cv.put(PARENT_ID_KEY, responseDump.getString(PARENT_ID_KEY));
            cv.put(BENEFICIARY_NAME, responseDump.getString("name"));
            cv.put("age", responseDump.getString("age"));
            cv.put(GENDER_KEY, responseDump.getString(GENDER_KEY));
            cv.put(ADDRESS_STRING, responseDump.getString(ADDRESS_STRING));
            cv.put(BENEFICIARY_TYPE_KEY, responseDump.getString(btype));
            cv.put(PARENT_UUID_KEY, responseDump.getString(PARENT_UUID_KEY));
            cv.put(Constants.SYNC_STATUS, 1);
            cv.put(STATUS_KEY, responseDump.getString(STATUS_KEY));
            cv.put(BENEFICIARY_TYPE_ID_KEY, responseDump.getString(BENEFICIARY_TYPE_ID_KEY));
            cv.put(LAST_MODIFIED_KEY, "");
            cv.put(CONTACT_NO, responseDump.getString(CONTACT_NO));
            cv.put("uuid", responseDump.getString("UUID"));
            cv.put(DOB, responseDump.getString(DOB));
            cv.put(ALIAS_NAME_KEY, responseDump.getString(ALIAS_NAME_KEY));
            cv.put(PARENT_KEY, responseDump.getString(PARENT_KEY));
            try {
                JSONObject jsonObject = new JSONObject(responseDump.getString(ADDRESS_STRING));
                cv = FilterUtils.getSevenLevels(cv, jsonObject.getJSONObject(ADDRESS_UNDERSCORE_STRING + 0).getInt("boundary_id"), database);
            } catch (Exception e) {
                Logger.logE(TAG, e.getMessage(), e);
            }
            cv.put(Constants.CREATED_DATE, dateFormatYyMmDdHhMmSs.format(new Date()));
            response_id = database.insertWithOnConflict("Beneficiary", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            Logger.logV(TAG, "beneficiary details===>" + cv.toString());
            database.close();
        } catch (Exception e) {
            database.close();
            Logger.logE(TAG, "Error on inserting the beneficiary details to beneficiaryTemp table and database path" + database.getPath() + "whilw inserting db ath", e);
        }
        return response_id;
    }

    /**
     * @param jsonObject
     * @return
     */
    public long insertOfflineFacilityData(JSONObject jsonObject) {
        SimpleDateFormat dateFormatYyMmDdHhMmSs = new SimpleDateFormat(Constants.DATE_FORMAT_YY_MM_DD_HH_MM_SS, Locale.US);

        long response_id = 0;
        try {
            ContentValues cv = new ContentValues();
            database = this.getWritableDatabase();
            cv.put(BOUNDARY_NAME_KEY, jsonObject.getString(BOUNDARY_NAME_KEY));
            cv.put(BOUNDARY_ID_KEY, jsonObject.getString(BOUNDARY_ID_KEY));
            cv.put(FACILITYNAME, jsonObject.getString(FACILITYNAME));
            cv.put(FACILITYTYPE, jsonObject.getString(FACILITYTYPE));
            cv.put(FACILITY_TYPE_ID_KEY, jsonObject.getString(FACILITY_TYPE_ID_KEY));
            cv.put(FACILITY_SUBTYPE_KEY, jsonObject.getString(FACILITY_SUBTYPE_KEY));
            cv.put(FACILITY_SUBTYPE_ID_KEY, jsonObject.getString(FACILITY_SUBTYPE_ID_KEY));
            cv.put(THEMATIC_AREA_KEY, jsonObject.getString(THEMATIC_AREA_KEY));
            cv.put(THEMATIC_AREA_ID_KEY, jsonObject.getString(THEMATIC_AREA_ID_KEY));
            cv.put(LOCATION_TYPE_KEY, jsonObject.getString(LOCATION_TYPE_KEY));
            cv.put(Constants.ADDRESS1, jsonObject.getString(Constants.ADDRESS1));
            cv.put(Constants.ADDRESS2, jsonObject.getString(Constants.ADDRESS2));
            cv.put(SERVICES_STRING, jsonObject.getString(SERVICES_STRING));
            cv.put(Constants.PINCODE, jsonObject.getString(Constants.PINCODE));
            cv.put(MODIFIED_KEY, "");
            cv.put(Constants.SYNC_STATUS, 1);
            cv = FilterUtils.getFacilityServey(cv, jsonObject.getString(BOUNDARY_LEVEL_KEY), jsonObject.getString(BOUNDARY_ID_KEY), database);
            cv.put(STATUS_KEY, jsonObject.getString(STATUS_KEY));
            cv.put(LOCATION_LEVEL_KEY, jsonObject.getString(BOUNDARY_LEVEL_KEY));
            cv.put("uuid", jsonObject.getString("UUID"));
            cv.put(Constants.CREATED_DATE, dateFormatYyMmDdHhMmSs.format(new Date()));

            response_id = database.insertWithOnConflict(FACILITY_TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            Logger.logV(TAG, "Facility content values" + cv.toString());
            database.close();
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
        return response_id;
    }


    /**
     * @param levelNo
     * @param locationType
     * @return
     */
    public List<LevelBeen> getBeneficiaryBoundaryListFromDB(Integer levelNo, String locationType) {
        List<LevelBeen> beenArrayList = new ArrayList<>();
        SQLiteDatabase db = openDataBase();
        String query = "";
        String tableName = level + levelNo;
        if (locationType == null)
            locationType = "Rural";
        if ("level2".equalsIgnoreCase(tableName) || "level3".equalsIgnoreCase(tableName)) {
            query = selectStrarFrom + tableName + " where active = 2 ";
        } else {
            query = selectStrarFrom + tableName + " where location_type = '" + locationType + ANDACTIVE_WITH_QUOTES;
        }
        Cursor cursor = db.rawQuery(query, null);
        Logger.logV(TAG, "the query is in getboundarylist from dbSurvey names list is" + query);

        LevelBeen levelBeen1 = new LevelBeen();
        levelBeen1.setId(0);
        levelBeen1.setName("Select location");
        levelBeen1.setSelected(false);
        beenArrayList.add(levelBeen1);
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                LevelBeen levelBeen = new LevelBeen();
                levelBeen.setId(cursor.getInt(cursor.getColumnIndex(level + levelNo + "_id")));
                levelBeen.setName(cursor.getString(cursor.getColumnIndex("name")));
                levelBeen.setLocationLevel(levelNo);
                beenArrayList.add(levelBeen);
                Logger.logV(TAG, "the bean id" + cursor.getInt(cursor.getColumnIndex(level + levelNo + "_id")));
                Logger.logV(TAG, "the bean name" + cursor.getInt(cursor.getColumnIndex("name")));
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return beenArrayList;
    }

    /**
     * @param levelNo
     * @param locationType
     * @return
     */
    public List<LevelBeen> getBoundaryListFromDB(Integer levelNo, String locationType) {
        List<LevelBeen> levelBeenList = new ArrayList<>();
        SQLiteDatabase db = openDataBase();
        String query = "";
        String tableName = level + levelNo;
        if (locationType == null)
            locationType = "Rural";
        if ("level2".equalsIgnoreCase(tableName) || "level3".equalsIgnoreCase(tableName)) {
            query = selectStrarFrom + tableName + " where active = 2 and level2_id = " + sharedPreferences.getString(stateIdStr, "");
        } else {
            query = selectStrarFrom + tableName + " where location_type = '" + locationType + ANDACTIVE_WITH_QUOTES + " and level2_id = " + sharedPreferences.getString(stateIdStr, "");
        }
        Cursor cursor = db.rawQuery(query, null);
        Logger.logV(TAG, "the query is in getboundarylist from dbSurvey names list is" + query);

        LevelBeen levelBeen1 = new LevelBeen();
        levelBeen1.setId(0);
        levelBeen1.setName("Select location");
        levelBeen1.setSelected(false);
        levelBeenList.add(levelBeen1);
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                LevelBeen levelBeen = new LevelBeen();
                levelBeen.setId(cursor.getInt(cursor.getColumnIndex(level + levelNo + "_id")));
                levelBeen.setName(cursor.getString(cursor.getColumnIndex("name")));
                levelBeen.setLocationLevel(levelNo);
                levelBeenList.add(levelBeen);
                Logger.logV(TAG, "the bean id" + cursor.getInt(cursor.getColumnIndex(level + levelNo + "_id")));
                Logger.logV(TAG, "the bean name" + cursor.getInt(cursor.getColumnIndex("name")));
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return levelBeenList;
    }

    /**
     * @param
     * @return
     */
    public List<Beneficiary> getBeneficiaryDetails() {
        List<Beneficiary> datumList = new ArrayList<>();
        try {
            SQLiteDatabase db = openDataBase();
            String query = "Select * From Beneficiary where status = 'Update'";
            //getOfflineUpdatedBeneficiaryDetails(datumList);
            Cursor cursor = db.rawQuery(query, null);
            Logger.logD("ggggggggggggg", "" + query);
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    Beneficiary datum = new Beneficiary();
                    datum.setId(cursor.getInt(cursor.getColumnIndex(Constants.SERVER_PRIMARY)));
                    datum.setName(cursor.getString(cursor.getColumnIndex(BENEFICIARY_NAME)));
                    datum.setParentId(cursor.getInt(cursor.getColumnIndex(PARENT_ID_KEY)));
                    Logger.logD(TAG, "" + cursor.getInt(cursor.getColumnIndex(PARENT_ID_KEY)) + "");
                    datum.setPartnerId(String.valueOf(cursor.getInt(cursor.getColumnIndex(PARTNER_ID_KEY))));
                    datum.setBtype(cursor.getString(cursor.getColumnIndex(BENEFICIARY_TYPE_KEY)));
                    String addressDump = cursor.getString(cursor.getColumnIndex(ADDRESS_STRING));
                    datum.setAliasName(cursor.getString(cursor.getColumnIndex(ALIAS_NAME_KEY)));
                    datum.setDateOFBirth(cursor.getString(cursor.getColumnIndex(DOB)));
                    Logger.logD(TAG, "Address String before converting to json object" + addressDump);
                    datum.setSyncStatus(String.valueOf(cursor.getInt(cursor.getColumnIndex(Constants.SYNC_STATUS))));
                    datum.setAddress(addressDump);
                    Logger.logD(TAG, "json object of address on main table" + cursor.getString(cursor.getColumnIndex(ADDRESS_STRING)));
                    datum.setAge(cursor.getString(cursor.getColumnIndex("age")));
                    datum.setGender(cursor.getString(cursor.getColumnIndex(GENDER_KEY)));
                    datum.setBeneficiaryTypeId(cursor.getInt(cursor.getColumnIndex(BENEFICIARY_TYPE_ID_KEY)));
                    datum.setContactNo(cursor.getString(cursor.getColumnIndex(CONTACT_NO)));
                    datum.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                    datumList.add(datum);
                }
                while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE("", "", e);
        }
        return datumList;
    }

    /**
     * @param s
     * @return
     */
    public List<Beneficiary> getBeneficiaryTempDetails(String s) {
        List<Beneficiary> datumList = new ArrayList<>();
        try {
            SQLiteDatabase db = openDataBase();
            String query = "Select * From Beneficiary where sync_status = 1 and uuid = '" + s + "'";
            Cursor cursor = db.rawQuery(query, null);
            Logger.logD(TAG, "" + query);
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    Beneficiary datum = new Beneficiary();
                    datum.setId(cursor.getInt(cursor.getColumnIndex(Constants.SERVER_PRIMARY)));
                    datum.setName(cursor.getString(cursor.getColumnIndex(BENEFICIARY_NAME)));
                    datum.setParentId(cursor.getInt(cursor.getColumnIndex(PARENT_ID_KEY)));
                    Logger.logD(TAG, "" + cursor.getInt(cursor.getColumnIndex(PARENT_ID_KEY)) + "");
                    datum.setPartnerId(cursor.getString(cursor.getColumnIndex(PARTNER_ID_KEY)));
                    datum.setBtype(cursor.getString(cursor.getColumnIndex(BENEFICIARY_TYPE_KEY)));
                    datum.setAge(cursor.getString(cursor.getColumnIndex("age")));
                    datum.setAliasName(cursor.getString(cursor.getColumnIndex(ALIAS_NAME_KEY)));
                    datum.setDateOFBirth(cursor.getString(cursor.getColumnIndex(DOB)));
                    datum.setGender(cursor.getString(cursor.getColumnIndex(GENDER_KEY)));
                    JSONObject object = new JSONObject(cursor.getString(cursor.getColumnIndex(ADDRESS_STRING)));
                    for (int i = 0; i < object.length(); i++) {
                        object.getJSONObject(ADDRESS_UNDERSCORE_STRING + i).remove(PRIMARY);
                        object.getJSONObject(ADDRESS_UNDERSCORE_STRING + i).remove(LOCATION_LEVEL_KEY);
                        object.getJSONObject(ADDRESS_UNDERSCORE_STRING + i).remove(LEAST_LOCATION_NAME_KEY);
                    }
                    datum.setAddress(String.valueOf(object));
                    datum.setBeneficiaryTypeId(cursor.getInt(cursor.getColumnIndex(BENEFICIARY_TYPE_ID_KEY)));
                    datum.setContactNo(cursor.getString(cursor.getColumnIndex(CONTACT_NO)));
                    datum.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                    try {
                        datum.setCreatedDate(cursor.getString(cursor.getColumnIndex(Constants.CREATED_DATE)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    datumList.add(datum);
                }
                while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE("", "", e);
        }
        return datumList;
    }


    /**
     * @return
     */
    public List<Facility> getFacilityTempDetails() {
        List<Facility> datumList = new ArrayList<>();
        try {
            SQLiteDatabase db = openDataBase();
            String query = "Select * From Facility where sync_status = 1";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    Facility datum = new Facility();
                    datum.setName(cursor.getString(cursor.getColumnIndex(FACILITYNAME)));
                    datum.setAddress1(cursor.getString(cursor.getColumnIndex(Constants.ADDRESS1)));
                    datum.setAddress2(cursor.getString(cursor.getColumnIndex(Constants.ADDRESS2)));
                    datum.setFacilityTypeId(cursor.getInt(cursor.getColumnIndex(FACILITY_TYPE_ID_KEY)));
                    datum.setFacilitySubtypeId(cursor.getInt(cursor.getColumnIndex(FACILITY_SUBTYPE_ID_KEY)));
                    datum.setBoundaryId(cursor.getInt(cursor.getColumnIndex(BOUNDARY_ID_KEY)));
                    datum.setServices(cursor.getString(cursor.getColumnIndex(SERVICES_STRING)));
                    datum.setThematicAreaId(cursor.getInt(cursor.getColumnIndex(THEMATIC_AREA_ID_KEY)));
                    datum.setPincode(cursor.getInt(cursor.getColumnIndex(Constants.PINCODE)));
                    datum.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                    try {
                        datum.setCreatedDateFac(cursor.getString(cursor.getColumnIndex(Constants.CREATED_DATE)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    datumList.add(datum);
                }
                while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
        return datumList;
    }


    /**
     * @param uuId
     * @param ben_id
     * @param
     * @return
     */
    public String updateBeneficiaryDetails(String uuId, String tableName, int ben_id) {
        ContentValues values = new ContentValues();
        values.put(Constants.SYNC_STATUS, 2);
        values.put(STATUS_KEY, "");
        Logger.logD(TAG, "updated beneficiary table values" + values.toString());
        int insertedValue = database.update(tableName, values, "uuid" + " = ?", new String[]{uuId});
        ContentValues benValues = new ContentValues();
        benValues.put(PARENT_ID_KEY, ben_id);
        benValues.put(PARENT_UUID_KEY, uuId);
        benValues.put(Constants.SYNC_STATUS, 1);
        benValues.put(STATUS_KEY, "");
        int issueUpdate = database.update(tableName, benValues, PARENT_UUID_KEY + " = ?", new String[]{uuId});
        return MessageFormat.format("Beneficiary household offline--{0},--mother and child update{1}", insertedValue, issueUpdate);
    }


    /**
     * @param uuId
     * @return
     */
    public int updateEditBeneficiaryDetails(String uuId, String tableName) {
        ContentValues values = new ContentValues();
        values.put(Constants.SYNC_STATUS, 2);
        values.put(STATUS_KEY, "");
        Logger.logD(TAG, "updated beneficiary table values" + values.toString());
        return database.update(tableName, values, "uuid" + " = ?", new String[]{uuId});

    }

    /**
     * @param uuid
     * @param
     * @return
     */
    public int updateFacilityDetails(String uuid) {
        ContentValues values = new ContentValues();
        values.put(Constants.SYNC_STATUS, 2);
        values.put(STATUS_KEY, "");
        return database.update(FACILITY_TABLE_NAME, values, "uuid" + " = ?", new String[]{String.valueOf(uuid)});
    }


    /**
     * @return
     */
    public List<String> getFacilityIds() {
        List<String> facilityIds = new ArrayList<>();
        try {
            SQLiteDatabase db = openDataBase();
            String query = "SELECT Distinct facility_ids FROM Surveys where facility_ids != ''";
            Cursor cursor = db.rawQuery(query, null);
            Logger.logV(TAG, "the query is in getFacility list is" + query + CURSOR_COUNT_VALUE + cursor.getCount());
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    facilityIds.add(cursor.getString(cursor.getColumnIndex(FACILITIY_IDS_KEY)));
                } while (cursor.moveToNext());
                cursor.close();
            } else {
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "Exception on getting the facility ids from surveys", e);
        }
        return facilityIds;
    }

    /**
     * @param facilityTypeId
     * @return
     */
    public List<SurveysBean> getFacilityIdsNew(String facilityTypeId) {
        List<SurveysBean> locationbasedbeneficiaryName = new ArrayList<>();
        SQLiteDatabase db = openDataBase();
        String query = "";
        Logger.logV(TAG, tagStr + db.getPath());
        query = "SELECT  * FROM Surveys where facility_ids =" + facilityTypeId + " and beneficiary_ids=''";
        Cursor cursor = db.rawQuery(query, null);
        Logger.logV(TAG, tagStr + query);
        try {
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    SurveysBean datum = new SurveysBean();
                    datum.setId(cursor.getInt(cursor.getColumnIndex(Constants.SURVEY_ID_COLUMN)));
                    datum.setPeriodicity(cursor.getInt(cursor.getColumnIndex(PERIODICITY_KEY)));
                    datum.setPeriodicityFlag(cursor.getString(cursor.getColumnIndex(PERIODICITY_FLAG)));
                    datum.setSurveyName(cursor.getString(cursor.getColumnIndex(SURVEYNAME)));
                    datum.setBeneficiaryIds(cursor.getString(cursor.getColumnIndex(BENEFICIARYIDS_KEY)));
                    datum.setFacilityIds(cursor.getString(cursor.getColumnIndex(FACILITIY_IDS_KEY)));
                    datum.setSurveyDone(0);
                    locationbasedbeneficiaryName.add(datum);
                }
                while (cursor.moveToNext());
                cursor.close();
            } else {
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }

        return locationbasedbeneficiaryName;

    }


    /**
     * @return
     */
    public List<String> getBeneficiaryIdFromSurvey() {
        List<String> beneficiaryIds = new ArrayList<>();
        try {
            SQLiteDatabase db = openDataBase();
            String query = "SELECT  SurveyName, beneficiary_ids FROM Surveys where beneficiary_ids !=''";
            Cursor cursor = db.rawQuery(query, null);
            Logger.logV(TAG, "the query is in getBENeficiaryid list is" + query + CURSOR_COUNT_VALUE + cursor.getCount());
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    beneficiaryIds.add(cursor.getString(cursor.getColumnIndex(BENEFICIARYIDS_KEY)));
                } while (cursor.moveToNext());
                cursor.close();
            } else {
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "Exception on getting the beneficiary_ids from surveys", e);
        }
        return beneficiaryIds;
    }

    /**
     * @param beneficiaryTypeID
     * @return
     */
    public List<SurveysBean> getBeneficiaryIdFromSurveyNEW(String beneficiaryTypeID) {
        List<SurveysBean> locationbasedbeneficiaryName = new ArrayList<>();
        SQLiteDatabase db = openDataBase();
        Logger.logV(TAG, tagStr + db.getPath());
        String query = "SELECT  * FROM Surveys where beneficiary_ids =" + beneficiaryTypeID;
        Cursor cursor = db.rawQuery(query, null);
        Logger.logV(TAG, tagStr + query);
        try {
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    SurveysBean datum = new SurveysBean();
                    datum.setId(cursor.getInt(cursor.getColumnIndex(Constants.SURVEY_ID_COLUMN)));
                    datum.setPeriodicity(cursor.getInt(cursor.getColumnIndex(PERIODICITY_KEY)));
                    datum.setPeriodicityFlag(cursor.getString(cursor.getColumnIndex(PERIODICITY_FLAG)));
                    datum.setSurveyName(cursor.getString(cursor.getColumnIndex(SURVEYNAME)));
                    datum.setBeneficiaryIds(cursor.getString(cursor.getColumnIndex(BENEFICIARYIDS_KEY)));
                    datum.setFacilityIds(cursor.getString(cursor.getColumnIndex(FACILITIY_IDS_KEY)));
                    datum.setSurveyDone(0);
                    locationbasedbeneficiaryName.add(datum);
                }
                while (cursor.moveToNext());
                cursor.close();
            } else {
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }

        return locationbasedbeneficiaryName;
    }


    /**
     * @return
     */
    public List<SurveysBean> getBenAndFacTypeBasedSurvey() {
        List<SurveysBean> locationbasedbeneficiaryName = new ArrayList<>();
        SQLiteDatabase db = openDataBase();
        String query = "";
        Logger.logV(TAG, tagStr + db.getPath());
        query = "SELECT id, surveyName, beneficiary_ids, facility_ids FROM Surveys where beneficiary_ids !='' and facility_ids !='''";
        Cursor cursor = db.rawQuery(query, null);
        Logger.logV(TAG, "the query is in getbenandfacity based survey list is" + query + CURSOR_COUNT_VALUE + cursor.getCount());
        try {
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    SurveysBean datum = new SurveysBean();
                    datum.setSurveyName(cursor.getString(cursor.getColumnIndex(SURVEYNAME)));
                    datum.setBeneficiaryIds(cursor.getString(cursor.getColumnIndex(BENEFICIARYIDS_KEY)));
                    datum.setFacilityIds(cursor.getString(cursor.getColumnIndex(FACILITIY_IDS_KEY)));
                    datum.setPLimit(cursor.getInt(cursor.getColumnIndex(Constants.P_LIMIT)));
                    locationbasedbeneficiaryName.add(datum);
                }
                while (cursor.moveToNext());
                cursor.close();
            } else {
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }

        return locationbasedbeneficiaryName;
    }


    /**
     * @param beneficiary
     * @param beneficiaryType
     * @param columnName
     * @return
     */
    public List<SurveysBean> getTypeBasedSurvey(String beneficiary, String beneficiaryType, String columnName) {
        List<SurveysBean> locationbasedbeneficiaryName = new ArrayList<>();
        SQLiteDatabase db = openDataBase();
        String query = "";
        Logger.logV(TAG, tagStr + db.getPath());
        if ("Beneficiary".equalsIgnoreCase(beneficiary)) {
            query = "SELECT  * FROM Surveys where " + columnName + "=" + beneficiaryType;
        } else {
            query = "select * from Surveys where Surveys.beneficiary_ids="+beneficiaryType;
        }
        Cursor cursor = db.rawQuery(query, null);
        Logger.logV(TAG, tagStr + query);
        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                SurveysBean datum = new SurveysBean();
                datum.setId(cursor.getInt(cursor.getColumnIndex(Constants.SURVEY_ID_COLUMN)));
                datum.setPeriodicity(cursor.getInt(cursor.getColumnIndex(PERIODICITY_KEY)));
                datum.setPeriodicityFlag(cursor.getString(cursor.getColumnIndex(PERIODICITY_FLAG)));
                datum.setSurveyName(cursor.getString(cursor.getColumnIndex(SURVEYNAME)));
                datum.setBeneficiaryIds(cursor.getString(cursor.getColumnIndex(BENEFICIARYIDS_KEY)));
                datum.setFacilityIds(cursor.getString(cursor.getColumnIndex(FACILITIY_IDS_KEY)));
                datum.setPLimit(cursor.getInt(cursor.getColumnIndex(Constants.P_LIMIT)));
                datum.setSurveyDone(0);
                locationbasedbeneficiaryName.add(datum);
            }
            while (cursor.moveToNext());
            cursor.close();
        } else {
            cursor.close();
        }

        return locationbasedbeneficiaryName;
    }


    /**
     * @param surveyId
     * @return
     */
    public List<SurveysBean> getSingleTypeBasedSurvey(int surveyId) {
        List<SurveysBean> locationbasedbeneficiaryName = new ArrayList<>();
        SQLiteDatabase db = openDataBase();
        String query = "";
        query = "SELECT  * FROM Surveys where surveyId=" + surveyId;
        Cursor cursor = db.rawQuery(query, null);
        Logger.logV(TAG, tagStr + query);
        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                SurveysBean datum = new SurveysBean();
                datum.setId(cursor.getInt(cursor.getColumnIndex(Constants.SURVEY_ID_COLUMN)));
                datum.setPeriodicity(cursor.getInt(cursor.getColumnIndex(PERIODICITY_KEY)));
                datum.setPeriodicityFlag(cursor.getString(cursor.getColumnIndex(PERIODICITY_FLAG)));
                datum.setSurveyName(cursor.getString(cursor.getColumnIndex(SURVEYNAME)));
                datum.setBeneficiaryIds(cursor.getString(cursor.getColumnIndex(BENEFICIARYIDS_KEY)));
                datum.setFacilityIds(cursor.getString(cursor.getColumnIndex(FACILITIY_IDS_KEY)));
                datum.setPLimit(cursor.getInt(cursor.getColumnIndex(Constants.P_LIMIT)));
                datum.setSurveyDone(0);
                locationbasedbeneficiaryName.add(datum);
            }
            while (cursor.moveToNext());
            cursor.close();
        } else {
            cursor.close();
        }

        return locationbasedbeneficiaryName;
    }


    /**
     * @param mother
     * @param parentId
     * @return
     */
    public List<Datum> getMothersListBasedOnParent(String mother, String parentId) {
        List<Datum> mothersList = new ArrayList<>();
        try {
            String query;
            SQLiteDatabase db = openDataBase();
            mothersList = getOfflineMothersList(mother, parentId);
            query = "SELECT  * from Beneficiary where parent_uuid = '" + parentId + AND_BENEFICIARY_TYPE_ID + mother + "'";
            Cursor cursor = db.rawQuery(query, null);
            Logger.logV(TAG, "the query to get the  list for table" + mother + " and the query is " + query);
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    Datum datum = new Datum();
                    datum.setId(cursor.getInt(cursor.getColumnIndex(Constants.SERVER_PRIMARY)));
                    datum.setParentId(cursor.getInt(cursor.getColumnIndex(PARENT_ID_KEY)));
                    datum.setParent_uuid(cursor.getString(cursor.getColumnIndex(PARENT_UUID_KEY)));
                    datum.setBeneficiaryType(cursor.getString(cursor.getColumnIndex(BENEFICIARY_TYPE_KEY)));
                    datum.setBeneficiaryTypeId(cursor.getInt(cursor.getColumnIndex(BENEFICIARY_TYPE_ID_KEY)));
                    datum.setName(cursor.getString(cursor.getColumnIndex(BENEFICIARY_NAME)));
                    String addressString = cursor.getString(cursor.getColumnIndex(ADDRESS_STRING));
                    Logger.logD(TAG, "address String from database getMothersListBasedOnParent" + addressString);
                    JSONArray jsonArray = new JSONArray(addressString);
                    List<org.mahiti.convenemis.BeenClass.beneficiary.Address> addressList = new ArrayList<>();
                    for (int j = 0; j < jsonArray.length(); j++) {
                        Address address = new Address();
                        JSONObject jsonobject = jsonArray.getJSONObject(j);
                        address.setAddress1(jsonobject.getString(ADDRESS1_KEY));
                        address.setAddress2(jsonobject.getString(ADDRESS2_KEY));
                        address.setLocationLevel(jsonobject.getInt(LOCATION_LEVEL_KEY));
                        address.setLeastLocationName(jsonobject.getString(LEAST_LOCATION_NAME_KEY));
                        address.setLeastLocationId(jsonobject.getInt(BOUNDARY_ID_KEY));
                        address.setPrimary(jsonobject.getInt(PRIMARY));
                        address.setPincode(jsonobject.getString(PINCODE_KEY));
                        address.setAddressId(jsonobject.getString(ADDRESS_ID_KEY));
                        if (jsonobject.has(PROOF_ID_KEY))
                            address.setProofId(jsonobject.getString(PROOF_ID_KEY));
                        addressList.add(address);
                    }
                    datum.setAddress(addressList);
                    List<String> contactList = Arrays.asList(cursor.getString(cursor.getColumnIndex(CONTACT_NO)).split(","));
                    datum.setContactNo(contactList);
                    String statusValue = cursor.getString(cursor.getColumnIndex(STATUS_KEY));
                    if (("").equals(statusValue) || (ONLINE_STATUS).equals(statusValue) || (null) == statusValue) {
                        datum.setStatus("");
                    } else {
                        datum.setStatus(OFFLINE_STATUS_KEY);
                    }
                    datum.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                    datum.setDateOfBirth(cursor.getString(cursor.getColumnIndex(DOB)));
                    datum.setAliasName(cursor.getString(cursor.getColumnIndex(ALIAS_NAME_KEY)));
                    datum.setDobOption(cursor.getString(cursor.getColumnIndex(DOB_OPTION_KEY)));
                    datum.setAge(cursor.getString(cursor.getColumnIndex("age")));
                    datum.setGender(cursor.getString(cursor.getColumnIndex(GENDER_KEY)));
                    datum.setSyncStatus(cursor.getInt(cursor.getColumnIndex(Constants.SYNC_STATUS)));
                    mothersList.add(datum);
                } while (cursor.moveToNext());
                cursor.close();
            } else {
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE("", "", e);
        }
        return mothersList;
    }

    /**
     * @param mother
     * @param parentId
     */
    private List<Datum> getOfflineMothersList(String mother, String parentId) {
        List<Datum> mothersList = new ArrayList<>();
        String query;
        SQLiteDatabase db = openDataBase();
        try {
            query = "SELECT * FROM Beneficiary where  parent_uuid = '" + parentId + AND_BENEFICIARY_TYPE_ID + mother + "' and sync_status = 1";
            Cursor cursor = db.rawQuery(query, null);
            Logger.logV(TAG, "the query to get the  list for table and the query is " + query);
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    Datum datum = new Datum();
                    datum.setId(cursor.getInt(cursor.getColumnIndex(Constants.SERVER_PRIMARY)));
                    datum.setParentId(cursor.getInt(cursor.getColumnIndex(PARENT_ID_KEY)));
                    datum.setParent_uuid(cursor.getString(cursor.getColumnIndex(PARENT_UUID_KEY)));
                    datum.setBeneficiaryType(cursor.getString(cursor.getColumnIndex(BENEFICIARY_TYPE_KEY)));
                    datum.setBeneficiaryTypeId(cursor.getInt(cursor.getColumnIndex(BENEFICIARY_TYPE_ID_KEY)));
                    datum.setName(cursor.getString(cursor.getColumnIndex(BENEFICIARY_NAME)));
                    String addressDump = cursor.getString(cursor.getColumnIndex(ADDRESS_STRING));
                    datum.setSyncStatus(cursor.getInt(cursor.getColumnIndex(Constants.SYNC_STATUS)));
                    List<Address> addressList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(addressDump);
                    Iterator<String> keyList = jsonObject.keys();

                    while (keyList.hasNext()) {
                        String key = keyList.next();
                        jsonObject.get(key);
                        JSONObject object = jsonObject.getJSONObject(key);
                        Address address = new Address();
                        address.setAddress1(object.getString(ADDRESS1_KEY));
                        address.setAddress2(object.getString(ADDRESS2_KEY));
                        address.setLeastLocationId(object.getInt(BOUNDARY_ID_KEY));
                        if (object.has(PRIMARY)) {
                            address.setPrimary(object.getInt(PRIMARY));
                        }
                        if (object.has(LOCATION_LEVEL_KEY)) {
                            address.setLocationLevel(object.getInt(LOCATION_LEVEL_KEY));
                        }
                        address.setPincode(object.getString(PINCODE_KEY));
                        if (object.has(LEAST_LOCATION_NAME_KEY))
                            address.setLeastLocationName(object.getString(LEAST_LOCATION_NAME_KEY));
                        if (object.has(PROOF_ID_KEY))
                            address.setProofId(object.getString(PROOF_ID_KEY));
                        address.setAddressId("");
                        addressList.add(address);
                    }
                    datum.setAddress(addressList);
                    List<String> contactList = Arrays.asList(cursor.getString(cursor.getColumnIndex(CONTACT_NO)).split(","));
                    datum.setContactNo(contactList);
                    datum.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                    datum.setAge(cursor.getString(cursor.getColumnIndex("age")));
                    datum.setGender(cursor.getString(cursor.getColumnIndex(GENDER_KEY)));
                    datum.setAliasName(cursor.getString(cursor.getColumnIndex(ALIAS_NAME_KEY)));
                    String statusValue = cursor.getString(cursor.getColumnIndex(STATUS_KEY));
                    if (("").equals(statusValue) || (ONLINE_STATUS).equals(statusValue) || (null) == statusValue) {
                        datum.setStatus("");
                    } else {
                        datum.setStatus(OFFLINE_STATUS_KEY);
                    }
                    mothersList.add(datum);
                } while (cursor.moveToNext());
                cursor.close();
            } else {
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "get offline mother and child details from household", e);
        }
        return mothersList;

    }


    /**
     * @param periodicityTableResponse
     * @return
     */
    public long updatePeriodicityTable(String periodicityTableResponse) {
        long response_id = 0;
        try {
            Gson gson = new Gson();
            ResponsesData responsesData = gson.fromJson(periodicityTableResponse, ResponsesData.class);
            JSONObject jsonObject = new JSONObject(periodicityTableResponse);
            if (!("Data already sent").equalsIgnoreCase(jsonObject.getString("message"))) {
                for (int i = 0; i < responsesData.getResponsesData().size(); i++) {
                    ContentValues cv = new ContentValues();
                    database = this.getWritableDatabase();
                    cv.put("response_id", responsesData.getResponsesData().get(i).getResponseId());
                    cv.put(PERIODICITY_KEY, "");
                    cv.put("bene_uuid", responsesData.getResponsesData().get(i).getBene_uuid());
                    cv.put("faci_uuid", responsesData.getResponsesData().get(i).getFaci_uuid());
                    cv.put("survey_id", responsesData.getResponsesData().get(i).getSurveyId());
                    cv.put(Constants.dateCaptureStr, responsesData.getResponsesData().get(i).getCollectedDate());
                    cv.put(ACTIVE_STATUS, responsesData.getResponsesData().get(i).getActive());
                    cv.put(SERVER_DATETIME_KEY, responsesData.getResponsesData().get(i).getServerDateTime());
                    cv.put("uuid", "");
                    if (responsesData.getResponsesData().get(i).getCluster_id().isEmpty() && responsesData.getResponsesData().get(i).getCluster_level().isEmpty()) {
                        cv.put("cluster_id", 0);
                        cv.put("cluster_level", 7);
                    } else {
                        cv.put("cluster_id", Integer.parseInt(responsesData.getResponsesData().get(i).getCluster_id()));
                        cv.put("cluster_level", Integer.parseInt(responsesData.getResponsesData().get(i).getCluster_id()));
                    }
                    response_id = database.insertWithOnConflict(PERIODICITY_KEY, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                    Logger.logV(TAG, "Periodicity content values" + cv.toString());
                }
            }
            database.close();
        } catch (Exception e) {
            Logger.logE(TAG, "Exception in UpdatePeriodicityTable ", e);
        }
        return response_id;
    }



    public int getPeriodicityPreviousCountOnline(int surveyId,String periodicityFlag,Date date) {
        int getPeriodicityCount = 0;
        try {
            String query = "";
            String getCurrentDate = new SimpleDateFormat(dateYy_Mm_Dd, Locale.ENGLISH).format(date);
            SQLiteDatabase db = openDataBase();
            String[] splitMonth = getCurrentDate.split("-");
            if ((DAILY).equalsIgnoreCase(periodicityFlag)){
                query="select * from survey where date(sync_date)='"+getCurrentDate+"' and survey_ids="+surveyId;
                Logger.logV(TAG, "" + query + query);

            }
            /*if ((MONTHLY).equalsIgnoreCase(periodicityFlag) && (!("").equals(beneficiaryIDS) || !("").equals(facilityIDS))) {

                if (isBen)
                    query = "SELECT * FROM Periodicity WHERE  strftime('%Y %m', date(date_capture))='" + (splitMonth[0] + " " + splitMonth[1]) + "'  and survey_id=" + uid + BEN_UUID + uuid + "'";
                else
                    query = "SELECT * FROM Periodicity WHERE  strftime('%Y %m', date(date_capture))='" + (splitMonth[0] + " " + splitMonth[1]) + "'  and survey_id=" + uid + " and faci_uuid='" + uuid + "'";
            } else if ((QUARTERLY).equalsIgnoreCase(periodicityFlag)) {
                List<String> Quarterly = Utils.getQuarterlyMonth(splitMonth[1]);
                if (isBen)
                    query = selectFromPeriodicityQuery + (splitMonth[0] + " " + Quarterly.get(0)) + STR_TO_CAPTURE_DATE + (splitMonth[0] + " " + Quarterly.get(1)) + STR_TO_CAPTURE_DATE + (splitMonth[0] + " " + Quarterly.get(2)) + "')  and survey_id=" + uid + BEN_UUID + uuid + "' ";
                else
                    query = selectFromPeriodicityQuery + (splitMonth[0] + " " + Quarterly.get(0)) + STR_TO_CAPTURE_DATE + (splitMonth[0] + " " + Quarterly.get(1)) + STR_TO_CAPTURE_DATE + (splitMonth[0] + " " + Quarterly.get(2)) + "')  and survey_id='" + uid + "'  and  faci_uuid='" + uuid + "'";
            } else if ((HALF_YEARLY).equalsIgnoreCase(periodicityFlag)) {
                List<String> halfYearly = Utils.getHalfYearly(splitMonth[1]);
                if (isBen)
                    query = selectFromPeriodicityQuery + (splitMonth[0] + " " + halfYearly.get(0)) + STR_TO_CAPTURE_DATE + (splitMonth[0] + " " + halfYearly.get(1)) + STR_TO_CAPTURE_DATE + (splitMonth[0] + " " + halfYearly.get(2)) + STR_TO_CAPTURE_DATE + (splitMonth[0] + " " + halfYearly.get(3)) + STR_TO_CAPTURE_DATE + (splitMonth[0] + " " + halfYearly.get(4)) + STR_TO_CAPTURE_DATE + (splitMonth[0] + " " + halfYearly.get(5)) + "' ) and survey_id='" + uid + BEN_UUID + uuid + "' ";
                else
                    query = selectFromPeriodicityQuery + (splitMonth[0] + " " + halfYearly.get(0)) + STR_TO_CAPTURE_DATE + (splitMonth[0] + " " + halfYearly.get(1)) + STR_TO_CAPTURE_DATE + (splitMonth[0] + " " + halfYearly.get(2)) + STR_TO_CAPTURE_DATE + (splitMonth[0] + " " + halfYearly.get(3)) + STR_TO_CAPTURE_DATE + (splitMonth[0] + " " + halfYearly.get(4)) + STR_TO_CAPTURE_DATE + (splitMonth[0] + " " + halfYearly.get(5)) + "' ) and survey_id='" + uid + "'  and  faci_uuid='" + uuid + "'";
            } else if ((YEARLY).equalsIgnoreCase(periodicityFlag)) {
                String previousDate = String.valueOf(Integer.parseInt(splitMonth[0]) - 1);
                String nextDate = String.valueOf(Integer.parseInt(splitMonth[0]) + 1);
                String logic = "((strftime('%Y', date(date_capture))='" + splitMonth[0] + "' ) and CAST(strftime('%m', date(date_capture)) AS INTEGER)>=4 )or( (strftime('%Y', date(date_capture))='" + nextDate + "') and CAST(strftime('%m', date(date_capture)) AS INTEGER)<=3)";
                if (Integer.parseInt(splitMonth[1]) <= 3)
                    logic = "((strftime('%Y', date(date_capture))='" + splitMonth[0] + "' ) and CAST(strftime('%m', date(date_capture)) AS INTEGER)<=3) or (" + "(strftime('%Y', date(date_capture))='" + previousDate + "' ) and CAST(strftime('%m', date(date_capture)) AS INTEGER)>=4)";
                if (isBen)
                    query = "SELECT * FROM Periodicity WHERE  (" + logic + ") and survey_id=" + uid + BEN_UUID + uuid + "'";
                else
                    query = "SELECT * FROM Periodicity WHERE  (" + logic + ") and survey_id=" + uid + "  and  faci_uuid='" + uuid + "'";
            }*/
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                getPeriodicityCount = cursor.getCount();
            } else {
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
        return getPeriodicityCount;
    }


    /**
     * @param uid
     * @param beneficiaryIDS
     * @param uuid
     * @return
     */
    public int getPeriodicityPreviousSurveyCount(String uid, String beneficiaryIDS, String uuid) {
        int getPeriodicityCount = 0;

        String query = "";
        SQLiteDatabase db = openDataBase();
        if (!("").equalsIgnoreCase(beneficiaryIDS))
            query = "SELECT * FROM Periodicity WHERE survey_id=" + uid + BEN_UUID + uuid + "'";
        else
            query = "SELECT * FROM Periodicity WHERE survey_id=" + uid + " and faci_uuid='" + uuid + "'";

        Cursor cursor = db.rawQuery(query, null);
        Logger.logV(TAG, tagStr + query + CURSOR_COUNT_VALUE + cursor.getCount());
        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            getPeriodicityCount = cursor.getCount();
        } else {
            cursor.close();
        }
        return getPeriodicityCount;
    }


    /**
     * @param getType
     * @return
     */
    public int getBeneficiaryNotSync(int getType) {
        int beneficiarynotSyncCount = 0;
        String query = "";
        try {
            SQLiteDatabase db = openDataBase();
            if (getType == 1) {
                query = "Select count(sync_status) From Beneficiary where (sync_status = 1 or sync_status = 3)";
            } else {
                query = "Select count(sync_status) From Facility where (sync_status = 1 or sync_status = 3)";
            }
            Cursor cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    beneficiarynotSyncCount = cursor.getInt(cursor.getColumnIndex("count(sync_status)"));
                    Logger.logD(TAG, " from beneficiary Temp table" + beneficiarynotSyncCount);
                }
                while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }

        return beneficiarynotSyncCount;
    }


    /**
     * @param columnName=facilityids
     * @param columnName2=beneficiaryids
     * @return
     */
    public boolean checkBeneficiaryBasedSurvey(String columnName, String columnName2) {
        boolean isBeneficiary = false;
        try {
            SQLiteDatabase db = openDataBase();
            String query = "SELECT * FROM Surveys where " + columnName + " ='' and " + columnName2 + "!=''";
            Cursor cursor = db.rawQuery(query, null);
            Logger.logV(TAG, "" + query + "" + cursor.getCount());
            isBeneficiary = cursor.getCount() != 0 && cursor.moveToFirst();
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
        return isBeneficiary;
    }


    /**
     * @param userEnterText
     * @param btypeid
     * @return
     */
    public List<Datum> getBeneficiaryNameForFilter(String userEnterText, String btypeid, String modified) {
        List<Datum> beneficiaryNameDatumList = new ArrayList<>();
        SQLiteDatabase db = openDataBase();
        beneficiaryNameDatumList.clear();
        Logger.logV(TAG, tagStr + db.getPath());
        getOfflineBeneficiaryFromFilter(userEnterText, btypeid, beneficiaryNameDatumList, db);
        String query = "SELECT * FROM Beneficiary where last_modified <=  '" + modified + "'and beneficiary_type_id =" + btypeid + " and beneficiary_name like '%" + userEnterText + "%' ORDER BY  last_modified  DESC LIMIT 20";
        try {
            Cursor cursor = db.rawQuery(query, null);
            Logger.logV(TAG, "the query is in getBeneficiaryNameForFilter list is" + query + CURSOR_COUNT_VALUE + cursor.getCount());
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    Datum datum = new Datum();
                    datum.setId(cursor.getInt(cursor.getColumnIndex(Constants.SERVER_PRIMARY)));
                    datum.setParentId(cursor.getInt(cursor.getColumnIndex(PARENT_ID_KEY)));
                    datum.setBeneficiaryType(cursor.getString(cursor.getColumnIndex(BENEFICIARY_TYPE_KEY)));
                    datum.setBeneficiaryTypeId(cursor.getInt(cursor.getColumnIndex(BENEFICIARY_TYPE_ID_KEY)));
                    datum.setName(cursor.getString(cursor.getColumnIndex(BENEFICIARY_NAME)));
                    datum.setParent_uuid(cursor.getString(cursor.getColumnIndex(PARENT_UUID_KEY)));
                    datum.setModified(cursor.getString(cursor.getColumnIndex(LAST_MODIFIED_KEY)));
                    try {
                        datum.setCreated(cursor.getString(cursor.getColumnIndex(Constants.CREATED_DATE)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    datum.setLeast_location_name(cursor.getString(cursor.getColumnIndex(LEAST_LOCATION_NAME_KEY)));
                    datum.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                    datum.setAge(cursor.getString(cursor.getColumnIndex("age")));
                    datum.setSyncStatus(cursor.getInt(cursor.getColumnIndex(Constants.SYNC_STATUS)));
                    datum.setGender(cursor.getString(cursor.getColumnIndex(GENDER_KEY)));
                    String addressString = cursor.getString(cursor.getColumnIndex(ADDRESS_STRING));
                    String statusValue = cursor.getString(cursor.getColumnIndex(STATUS_KEY));
                    datum.setDateOfBirth(cursor.getString(cursor.getColumnIndex(DOB)));
                    datum.setAliasName(cursor.getString(cursor.getColumnIndex(ALIAS_NAME_KEY)));
                    datum.setDobOption(cursor.getString(cursor.getColumnIndex(DOB_OPTION_KEY)));
                    Logger.logD(TAG, "the status value of beneficiary from beneficiary table" + statusValue);
                    if (("").equals(statusValue) || (ONLINE_STATUS).equals(statusValue) || (null) == statusValue) {
                        datum.setStatus("");
                    } else {
                        datum.setStatus(updateStr);
                    }
                    Logger.logD(TAG, "the status value of beneficiary from bean" + datum.getStatus());
                    List<String> contactList = Arrays.asList(cursor.getString(cursor.getColumnIndex(CONTACT_NO)).split(","));
                    datum.setContactNo(contactList);
                    Logger.logD(TAG, "address String from database getBeneficiaryNameForFilter" + addressString);

                    JSONArray jsonArray = new JSONArray(addressString);
                    List<Address> addressList = new ArrayList<>();
                    for (int j = 0; j < jsonArray.length(); j++) {
                        Address address = new Address();
                        JSONObject jsonobject = jsonArray.getJSONObject(j);
                        address.setAddress1(jsonobject.getString(ADDRESS1_KEY));
                        address.setAddress2(jsonobject.getString(ADDRESS2_KEY));
                        if (jsonobject.has(LOCATION_LEVEL_KEY)) {
                            address.setLocationLevel(jsonobject.getInt(LOCATION_LEVEL_KEY));
                        }
                        if (jsonobject.has(LEAST_LOCATION_NAME_KEY)) {
                            address.setLeastLocationName(jsonobject.getString(LEAST_LOCATION_NAME_KEY));
                        }

                        if (jsonobject.has(BOUNDARY_ID_KEY)) {
                            address.setLeastLocationId(jsonobject.getInt(BOUNDARY_ID_KEY));
                        }
                        if (jsonobject.has(PRIMARY)) {
                            address.setPrimary(jsonobject.getInt(PRIMARY));
                        }
                        address.setProofId(jsonobject.getString(PROOF_ID_KEY));
                        address.setPincode(jsonobject.getString(PINCODE_KEY));
                        address.setAddressId(jsonobject.getString(ADDRESS_ID_KEY));
                        if (jsonobject.has(PROOF_ID_KEY))
                            address.setProofId(jsonobject.getString(PROOF_ID_KEY));
                        addressList.add(address);
                    }
                    datum.setAddress(addressList);
                    datum.setParent(cursor.getString(cursor.getColumnIndex(PARENT_KEY)));
                    beneficiaryNameDatumList.add(datum);
                }
                while (cursor.moveToNext());
                cursor.close();
            } else {
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }

        return beneficiaryNameDatumList;
    }

    private void getOfflineBeneficiaryFromFilter(String userEnterText, String btypeid, List<Datum> beneficiaryNameDatumList, SQLiteDatabase db) {
        try {
            String query = "SELECT * FROM beneficiary where  sync_status = 1 and  beneficiary_type_id =" + btypeid + " and beneficiary_name like '%" + userEnterText + "%' ORDER BY  last_modified  DESC";
            Cursor cursor = db.rawQuery(query, null);
            Logger.logV(TAG, tagQueryListStr + query + CURSOR_COUNT_VALUE + cursor.getCount());
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    Datum datum = new Datum();
                    datum.setId(cursor.getInt(cursor.getColumnIndex(Constants.SERVER_PRIMARY)));
                    datum.setAge(cursor.getString(cursor.getColumnIndex("age")));
                    datum.setBeneficiaryTypeId(cursor.getInt(cursor.getColumnIndex(BENEFICIARY_TYPE_ID_KEY)));
                    datum.setParentId(cursor.getInt(cursor.getColumnIndex(PARENT_ID_KEY)));
                    datum.setGender(cursor.getString(cursor.getColumnIndex(GENDER_KEY)));
                    datum.setAliasName(cursor.getString(cursor.getColumnIndex(ALIAS_NAME_KEY)));
                    datum.setParent_uuid(cursor.getString(cursor.getColumnIndex(PARENT_UUID_KEY)));
                    datum.setBeneficiaryType(cursor.getString(cursor.getColumnIndex(BENEFICIARY_TYPE_KEY)));
                    datum.setSyncStatus(cursor.getInt(cursor.getColumnIndex(Constants.SYNC_STATUS)));
                    datum.setDateOfBirth(cursor.getString(cursor.getColumnIndex(DOB)));
                    datum.setDobOption(cursor.getString(cursor.getColumnIndex(DOB_OPTION_KEY)));
                    datum.setName(cursor.getString(cursor.getColumnIndex(BENEFICIARY_NAME)));
                    String addressDump = cursor.getString(cursor.getColumnIndex(ADDRESS_STRING));
                    List<Address> addressList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(addressDump);
                    Iterator<String> keyList = jsonObject.keys();
                    while (keyList.hasNext()) {
                        String key = keyList.next();
                        jsonObject.get(key);
                        JSONObject object = jsonObject.getJSONObject(key);
                        Address address = new Address();
                        address.setAddress1(object.getString(ADDRESS1_KEY));
                        address.setAddress2(object.getString(ADDRESS2_KEY));
                        address.setLeastLocationId(object.getInt(BOUNDARY_ID_KEY));
                        address.setPincode(object.getString(PINCODE_KEY));
                        address.setProofId(object.getString(PROOF_ID_KEY));
                        address.setLeastLocationName(object.getString(LEAST_LOCATION_NAME_KEY));
                        if (object.has(PRIMARY)) {
                            address.setPrimary(object.getInt(PRIMARY));
                        } else {
                            address.setPrimary(0);
                        }
                        if (object.has(LOCATION_LEVEL_KEY)) {
                            address.setLocationLevel(object.getInt(LOCATION_LEVEL_KEY));
                        } else {
                            address.setLocationLevel(0);
                        }

                        if (object.has(ADDRESS_ID_KEY)) {
                            address.setAddressId(ADDRESS_ID_KEY);
                        } else {
                            address.setAddressId("");
                        }
                        if (object.has(PROOF_ID_KEY))
                            address.setProofId(object.getString(PROOF_ID_KEY));

                        addressList.add(address);
                    }
                    datum.setAddress(addressList);
                    List<String> contactList = Arrays.asList(cursor.getString(cursor.getColumnIndex(CONTACT_NO)).split(","));
                    datum.setContactNo(contactList);
                    datum.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                    datum.setGender(cursor.getString(cursor.getColumnIndex(GENDER_KEY)));
                    datum.setStatus(OFFLINE_STATUS_KEY);
                    beneficiaryNameDatumList.add(datum);
                }
                while (cursor.moveToNext());
                cursor.close();
            } else {
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "Exception on getting the list from beneficiary temp", e);
        }

    }


    /**
     * @param userEnterText
     * @param btypeid
     * @return
     */
    public List<org.mahiti.convenemis.BeenClass.facilities.Datum> getFacilityNameForFilter(String userEnterText, String btypeid, String modifiedDate) {
        List<org.mahiti.convenemis.BeenClass.facilities.Datum> facilityList = new ArrayList<>();
        List<Integer> servicesList = new ArrayList<>();
        SQLiteDatabase db = openDataBase();
        facilityList.clear();
        Logger.logV(TAG, tagStr + db.getPath());
        String query = "SELECT * FROM Facility where modified <=  '" + modifiedDate + "' and facility_type_id = " + btypeid + " and facility_name like '%" + userEnterText + "%' ORDER BY modified DESC";
        try {
            Cursor cursor = db.rawQuery(query, null);
            Logger.logV(TAG, "the query getFacilityNameForFilter is in  list is" + query + CURSOR_COUNT_VALUE + cursor.getCount());
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    org.mahiti.convenemis.BeenClass.facilities.Datum datum = new org.mahiti.convenemis.BeenClass.facilities.Datum();
                    datum.setBoundaryName(cursor.getString(cursor.getColumnIndex(BOUNDARY_NAME_KEY)));
                    datum.setBoundaryId(String.valueOf(cursor.getInt(cursor.getColumnIndex(BOUNDARY_ID_KEY))));
                    datum.setFacilityType(cursor.getString(cursor.getColumnIndex(FACILITYTYPE)));
                    datum.setName(cursor.getString(cursor.getColumnIndex(FACILITYNAME)));
                    datum.setFacilitySubtype(cursor.getString(cursor.getColumnIndex(FACILITY_SUBTYPE_KEY)));
                    datum.setFacilitySubtypeId(cursor.getInt(cursor.getColumnIndex(FACILITY_SUBTYPE_ID_KEY)));
                    datum.setFacilityTypeId(cursor.getInt(cursor.getColumnIndex(FACILITY_TYPE_ID_KEY)));
                    datum.setModified(cursor.getString(cursor.getColumnIndex(MODIFIED_KEY)));
                    datum.setThematicArea(cursor.getString(cursor.getColumnIndex(THEMATIC_AREA_KEY)));
                    datum.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                    datum.setId(cursor.getInt(cursor.getColumnIndex(Constants.SERVER_PRIMARY)));
                    datum.setAddress1(cursor.getString(cursor.getColumnIndex(ADDRESS1_KEY)));
                    datum.setAddress2(cursor.getString(cursor.getColumnIndex(ADDRESS2_KEY)));
                    datum.setPincode(cursor.getString(cursor.getColumnIndex(PINCODE_KEY)));
                    datum.setPartner(cursor.getString(cursor.getColumnIndex(PARTNER_NAME_KEY)));
                    try {
                        datum.setCreated(cursor.getString(cursor.getColumnIndex(Constants.CREATED_DATE)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    datum.setSyncStatus(cursor.getInt(cursor.getColumnIndex(Constants.SYNC_STATUS)));
                    datum.setBoundaryLevel(cursor.getString(cursor.getColumnIndex(LOCATION_LEVEL_KEY)));
                    String service = cursor.getString(cursor.getColumnIndex(SERVICES_STRING));
                    Logger.logD(TAG, "Service value " + service);
                    String statusValue = cursor.getString(cursor.getColumnIndex(STATUS_KEY));
                    Logger.logD(TAG, "status Value value " + statusValue);
                    if (("").equalsIgnoreCase(statusValue) || (null) == (statusValue) || (ONLINE_STATUS).equalsIgnoreCase(statusValue)) {
                        datum.setStatus("");
                    } else {
                        datum.setStatus(OFFLINE_STATUS_KEY);
                    }
                    setServicesToBean(service, datum, servicesList);
                    facilityList.add(datum);
                }
                while (cursor.moveToNext());
                cursor.close();
            } else {
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
        return facilityList;
    }


    /**
     * @param periodicityRange
     * @return
     */
    public String getPeriodicQuery(String periodicityRange) {
        String query = "";
        String getCurrentDate = new SimpleDateFormat(dateYy_Mm_Dd, Locale.ENGLISH).format(new Date());
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat(dateYy_Mm_Dd, Locale.ENGLISH);
        String formatted = format1.format(cal.getTime());
        String[] splitMonth = getCurrentDate.split("-");
        Logger.logD("getPeriodicQuery", "getPeriodicQuery ---periodicityRange  " + periodicityRange);
        switch (periodicityRange) {
            case "Daily":
                query = " strftime('%d', date(date_capture))='" + formatted + "'";
                break;
            case MONTHLY:
                query = " strftime('%m', date(date_capture))='" + splitMonth[1] + "'";
                break;
            case YEARLY:
                query = STRFTIME_YEAR_CAPTURE + splitMonth[0] + "'";
                break;
            case QUARTERLY:
                List<String> Quarterly = Utils.getQuarterlyMonth(splitMonth[1]);
                query = STRFTIME_MONTH + Quarterly.get(0) + STR_TO_CAPTURE_DATE + Quarterly.get(1) + STR_TO_CAPTURE_DATE + Quarterly.get(2) + "')";
                break;
            case HALF_YEARLY:
                List<String> halfYearly = Utils.getHalfYearly(splitMonth[1]);
                query = STRFTIME_MONTH + halfYearly.get(0) + STR_TO_CAPTURE_DATE + halfYearly.get(1) + STR_TO_CAPTURE_DATE + halfYearly.get(2) + STR_TO_CAPTURE_DATE + halfYearly.get(3) + STR_TO_CAPTURE_DATE + halfYearly.get(4) + STR_TO_CAPTURE_DATE + halfYearly.get(5) + "')";
                break;
            case WEEKLY:
                query = " (strftime('%W', date_capture) = strftime('%W', 'now'))";
                break;
            default:
                break;
        }
        Logger.logD("getPeriodicQuery", "getPeriodicQuery --- " + query);
        return query;
    }


    /**
     * @param query
     * @return
     */
    public List<Datum> getFilteredSurveyIdnew(String query) {
        List<Datum> beneficiaryNameDatumList = new ArrayList<>();
        String groupByQuery = " group by b.server_primary order by b.server_primary DESC";
        try {
            SQLiteDatabase db = openDataBase();
            Cursor cursor = db.rawQuery(query + groupByQuery, null);

            Logger.logV(TAG, "getFilteredSurveyIdnew the query is " + query);
            Logger.logV(TAG, tagQueryListStr + query + groupByQuery + CURSOR_COUNT_VALUE + cursor.getCount());
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    Datum datum = new Datum();
                    datum.setId(cursor.getInt(cursor.getColumnIndex(Constants.SERVER_PRIMARY)));
                    datum.setParentId(cursor.getInt(cursor.getColumnIndex(PARENT_ID_KEY)));
                    datum.setBeneficiaryType(cursor.getString(cursor.getColumnIndex(BENEFICIARY_TYPE_KEY)));
                    datum.setBeneficiaryTypeId(cursor.getInt(cursor.getColumnIndex(BENEFICIARY_TYPE_ID_KEY)));
                    datum.setName(cursor.getString(cursor.getColumnIndex(BENEFICIARY_NAME)));
                    datum.setAge(cursor.getString(cursor.getColumnIndex("age")));
                    datum.setSyncStatus(cursor.getInt(cursor.getColumnIndex(Constants.SYNC_STATUS)));
                    datum.setGender(cursor.getString(cursor.getColumnIndex(GENDER_KEY)));
                    datum.setModified(cursor.getString(cursor.getColumnIndex(LAST_MODIFIED_KEY)));
                    try {
                        datum.setCreated(cursor.getString(cursor.getColumnIndex(Constants.CREATED_DATE)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    datum.setLeast_location_name(cursor.getString(cursor.getColumnIndex(LEAST_LOCATION_NAME_KEY)));
                    datum.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                    String statusValue = cursor.getString(cursor.getColumnIndex(STATUS_KEY));
                    if (("").equals(statusValue) || (ONLINE_STATUS).equals(statusValue) || (null) == statusValue) {
                        datum.setStatus("");
                    } else {
                        datum.setStatus(updateStr);
                    }
                    Logger.logD(TAG, "the status value of beneficiary from bean getFilteredSurveyIdnew" + datum.getStatus());
                    List<String> contactList = Arrays.asList(cursor.getString(cursor.getColumnIndex(CONTACT_NO)).split(","));
                    datum.setContactNo(contactList);

                    String addressString = cursor.getString(cursor.getColumnIndex(ADDRESS_STRING));
                    datum.setStatus("");
                    Logger.logD(TAG, "address String from database getFilteredSurveyIdnew" + addressString);

                    JSONArray jsonArray = new JSONArray(addressString);
                    List<Address> addressList = new ArrayList<>();
                    for (int j = 0; j < jsonArray.length(); j++) {
                        Address address = new Address();
                        JSONObject jsonobject = jsonArray.getJSONObject(j);
                        address.setAddress1(jsonobject.getString(ADDRESS1_KEY));
                        address.setAddress2(jsonobject.getString(ADDRESS2_KEY));
                        if (jsonobject.has(LOCATION_LEVEL_KEY)) {
                            address.setLocationLevel(jsonobject.getInt(LOCATION_LEVEL_KEY));
                        }
                        if (jsonobject.has(LEAST_LOCATION_NAME_KEY)) {
                            address.setLeastLocationName(jsonobject.getString(LEAST_LOCATION_NAME_KEY));
                        }

                        if (jsonobject.has(BOUNDARY_ID_KEY)) {
                            address.setLeastLocationId(jsonobject.getInt(BOUNDARY_ID_KEY));
                        }
                        if (jsonobject.has(PRIMARY)) {
                            address.setPrimary(jsonobject.getInt(PRIMARY));
                        }
                        address.setPincode(jsonobject.getString(PINCODE_KEY));
                        address.setAddressId(jsonobject.getString(ADDRESS_ID_KEY));
                        if (jsonobject.has(PROOF_ID_KEY))
                            address.setProofId(jsonobject.getString(PROOF_ID_KEY));
                        addressList.add(address);
                    }
                    datum.setAddress(addressList);
                    beneficiaryNameDatumList.add(datum);
                }
                while (cursor.moveToNext());
                cursor.close();
            } else {
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
        return beneficiaryNameDatumList;
    }

    /**
     * @param query
     * @return
     */
    public List<org.mahiti.convenemis.BeenClass.facilities.Datum> getFilteredSurveyIdFacility(String query) {
        List<org.mahiti.convenemis.BeenClass.facilities.Datum> beneficiaryName = new ArrayList<>();
        String groupByQuery = query + " group by b.id order by b.id DESC";
        try {
            SQLiteDatabase db = openDataBase();
            Cursor cursor = db.rawQuery(groupByQuery, null);
            Logger.logV(TAG, "the query is in getFilteredFacilitySurveyIdsnew  list is" + groupByQuery + CURSOR_COUNT_VALUE + cursor.getCount());
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    org.mahiti.convenemis.BeenClass.facilities.Datum datum = new org.mahiti.convenemis.BeenClass.facilities.Datum();
                    datum.setBoundaryName(cursor.getString(cursor.getColumnIndex(BOUNDARY_NAME_KEY)));
                    datum.setBoundaryId(String.valueOf(cursor.getInt(cursor.getColumnIndex(BOUNDARY_ID_KEY))));
                    datum.setFacilityType(cursor.getString(cursor.getColumnIndex(FACILITYTYPE)));
                    datum.setName(cursor.getString(cursor.getColumnIndex(FACILITYNAME)));
                    datum.setFacilityTypeId(cursor.getInt(cursor.getColumnIndex(FACILITY_TYPE_ID_KEY)));
                    datum.setModified(cursor.getString(cursor.getColumnIndex(MODIFIED_KEY)));
                    try {
                        datum.setCreated(cursor.getString(cursor.getColumnIndex(Constants.CREATED_DATE)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    datum.setThematicArea(cursor.getString(cursor.getColumnIndex(THEMATIC_AREA_KEY)));
                    datum.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                    datum.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    datum.setStatus("");

                    beneficiaryName.add(datum);
                }
                while (cursor.moveToNext());
                cursor.close();
            } else {
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
        return beneficiaryName;
    }

    /**
     * @param surveyID
     * @return
     */

    public String getperiodicity(String surveyID) {

        String periodityTemp = "";
        SQLiteDatabase db = openDataBase();
        String query = "SELECT PeriodicityFlag FROM Surveys where surveyid=" + surveyID;
        Cursor cursor = db.rawQuery(query, null);
        Logger.logV(TAG, "the query is in getperiodicity Survey names list is" + query);
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                periodityTemp = cursor.getString(cursor.getColumnIndex(PERIODICITY_FLAG));
                Logger.logV(TAG, PERIODICITY_FLAG + cursor.getString(cursor.getColumnIndex(PERIODICITY_FLAG)));
            } while (cursor.moveToNext());
            cursor.close();

        }
        return periodityTemp;
    }

    /**
     * @param tableName
     * @param i
     * @param leastLocationId
     * @param slugName
     * @return
     */
    public List<LevelBeen> getLevelNamesFromLevels(String tableName, int i, Integer leastLocationId, String slugName) {
        List<LevelBeen> levelBeenList = new ArrayList<>();
        try {
            String query = "";
            if (tableName.equals(LEVEL7)) {
                query = SELECT_DISTINCT_NAME_LEVEL + i + ID_FROM + tableName + " where id =" + leastLocationId + AND_LOCATION_TYPE + slugName + ANDACTIVE_WITH_QUOTES;
            } else {
                query = SELECT_DISTINCT_NAME_LEVEL + i + ID_FROM + tableName + " where level" + i + "_id =" + leastLocationId + AND_LOCATION_TYPE + slugName + ANDACTIVE_WITH_QUOTES;
            }
            Cursor cursor = database.rawQuery(query, null);
            Logger.logV(TAG, tagQueryNameListStr + query);
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    LevelBeen levelBeen = new LevelBeen();
                    levelBeen.setId(cursor.getInt(cursor.getColumnIndex(levelStr + i + "_id")));
                    levelBeen.setName(cursor.getString(cursor.getColumnIndex("name")));
                    levelBeenList.add(levelBeen);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "exception on getLevelNamesFromLevels", e);
        }

        return levelBeenList;
    }

    /**
     * @param houseHoldId
     * @param
     */
    public List<Datum> getSelectedHouseHoldFromDb(String houseHoldId) {
        List<Datum> houseHoldList = new ArrayList<>();
        try {
            SQLiteDatabase db = openDataBase();

            String query = "SELECT * from Beneficiary where uuid = '" + houseHoldId + "'";
            getOfflineHouseHoldDetails(houseHoldId, houseHoldList);
            Cursor cursor = db.rawQuery(query, null);
            Logger.logV(TAG, tagQueryNameListStr + query);
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                Datum datum = new Datum();
                datum.setId(cursor.getInt(cursor.getColumnIndex(Constants.SERVER_PRIMARY)));
                datum.setName(cursor.getString(cursor.getColumnIndex(BENEFICIARY_NAME)));
                String addressString = cursor.getString(cursor.getColumnIndex(ADDRESS_STRING));
                datum.setParent_uuid(cursor.getString(cursor.getColumnIndex(PARENT_UUID_KEY)));
                datum.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                Logger.logD(TAG, "address String from database getSelectedHouseHoldFromDb" + addressString);
                JSONArray jsonArray = new JSONArray(addressString);
                List<Address> addressList = new ArrayList<>();
                for (int j = 0; j < jsonArray.length(); j++) {
                    Address address = new Address();
                    JSONObject jsonobject = jsonArray.getJSONObject(j);
                    address.setAddress1(jsonobject.getString(ADDRESS1_KEY));
                    address.setAddress2(jsonobject.getString(ADDRESS2_KEY));
                    address.setLocationLevel(jsonobject.getInt(LOCATION_LEVEL_KEY));
                    address.setLeastLocationName(jsonobject.getString(LEAST_LOCATION_NAME_KEY));
                    address.setLeastLocationId(jsonobject.getInt(BOUNDARY_ID_KEY));
                    address.setPrimary(jsonobject.getInt(PRIMARY));
                    address.setPincode(jsonobject.getString(PINCODE_KEY));
                    address.setAddressId(jsonobject.getString(ADDRESS_ID_KEY));
                    if (jsonobject.has(PROOF_ID_KEY))
                        address.setProofId(jsonobject.getString(PROOF_ID_KEY));
                    addressList.add(address);
                }
                datum.setAddress(addressList);
                List<String> contactList = Arrays.asList(cursor.getString(cursor.getColumnIndex(CONTACT_NO)).split(","));
                datum.setContactNo(contactList);
                houseHoldList.add(datum);
            }
            cursor.close();
        } catch (Exception e) {
            Logger.logE("", "Exception on getting the household name", e);
        }
        return houseHoldList;
    }

    public void getOfflineHouseHoldDetails(String uuid, List<Datum> datumList) {
        try {
            String query = "SELECT * from Beneficiary where uuid = '" + uuid + "' and sync_status = 1";
            Cursor cursor = database.rawQuery(query, null);
            Logger.logV(TAG, tagQueryNameListStr + query);
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                Datum datum = new Datum();
                datum.setId(cursor.getInt(cursor.getColumnIndex(Constants.SERVER_PRIMARY)));
                datum.setName(cursor.getString(cursor.getColumnIndex(BENEFICIARY_NAME)));
                String addressDump = cursor.getString(cursor.getColumnIndex(ADDRESS_STRING));
                datum.setParent_uuid(cursor.getString(cursor.getColumnIndex(PARENT_UUID_KEY)));
                datum.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                List<Address> addressList = new ArrayList<>();
                JSONObject jsonObject = new JSONObject(addressDump);
                Iterator<String> keyList = jsonObject.keys();

                while (keyList.hasNext()) {
                    String key = keyList.next();
                    jsonObject.get(key);
                    JSONObject object = jsonObject.getJSONObject(key);
                    Address address = new Address();
                    address.setAddress1(object.getString(ADDRESS1_KEY));
                    address.setAddress2(object.getString(ADDRESS2_KEY));
                    address.setLeastLocationId(object.getInt(BOUNDARY_ID_KEY));
                    address.setPrimary(object.getInt(PRIMARY));
                    address.setLocationLevel(object.getInt(LOCATION_LEVEL_KEY));
                    address.setPincode(object.getString(PINCODE_KEY));
                    if (object.has(PROOF_ID_KEY))
                        address.setProofId(object.getString(PROOF_ID_KEY));
                    address.setAddressId("");
                    addressList.add(address);
                }
                datum.setAddress(addressList);
                List<String> contactList = Arrays.asList(cursor.getString(cursor.getColumnIndex(CONTACT_NO)).split(","));
                datum.setContactNo(contactList);
                datumList.add(datum);
            }
            cursor.close();
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
    }


    /**
     * @param getEndDate
     * @param surveyID
     * @return
     */
    public StatusBean getDetails(String getEndDate, int surveyID) {

        StatusBean sb = new StatusBean();
        try {
            String pendingSurveyQuery = "SELECT surveyName,PeriodicityFlag,id FROM Surveys where surveyId=" + surveyID;
            Cursor cursor;
            SQLiteDatabase db = openDataBase();
            cursor = db.rawQuery(pendingSurveyQuery, null);
            Logger.logD("blockquery", "Get Periodiicty and surveys from  Table" + pendingSurveyQuery);

            if (cursor.moveToFirst()) {
                do {

                    String surveyName = cursor.getString(cursor.getColumnIndex(SURVEYNAME));
                    String PeriodicityFlag = cursor.getString(cursor.getColumnIndex(PERIODICITY_FLAG));

                    sb.setName(surveyName);
                    sb.setLanguage(PeriodicityFlag);
                    sb.setDate(getEndDate);
                //    syncedList.add(sb);

                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logV("", "Responses from Survey table" + e);
        }
        return sb;
    }


    /**
     * @param leastLocationId
     * @return
     */
    public String getLocationTypeFromDB(int leastLocationId) {
        String locationType = "";
        try {
            String pendingSurveyQuery = "select location_type from Level7 where id = " + leastLocationId + AND_ACTIVE;
            Cursor cursor;
            SQLiteDatabase db = openDataBase();
            cursor = db.rawQuery(pendingSurveyQuery, null);
            Logger.logD("", "Get location type from level 7" + pendingSurveyQuery);
            if (cursor.moveToFirst()) {
                locationType = cursor.getString(cursor.getColumnIndex(LOCATION_TYPE_KEY));
                cursor.close();
            } else {
                locationType = "";
            }
        } catch (Exception e) {
            Logger.logE("", "Exception on getting the location type", e);
        }
        return locationType;
    }


    /**
     * @param tableName
     * @param i
     * @param leastLocationId
     * @return
     */
    public List<LevelBeen> getDistrictNamesFromLevels(String tableName, int i, Integer leastLocationId) {
        List<LevelBeen> levelBeenList = new ArrayList<>();
        SQLiteDatabase db = openDataBase();
        String query = "";
        if (tableName.equals(LEVEL7)) {
            query = SELECT_DISTINCT_NAME_LEVEL + i + ID_FROM + tableName + " where id =" + leastLocationId + AND_ACTIVE;
        } else {
            query = SELECT_DISTINCT_NAME_LEVEL + i + ID_FROM + tableName + " where level" + i + "_id =" + leastLocationId + AND_ACTIVE;
        }
        Cursor cursor = db.rawQuery(query, null);
        Logger.logV(TAG, tagQueryNameListStr + query);
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                LevelBeen levelBeen = new LevelBeen();
                levelBeen.setId(cursor.getInt(cursor.getColumnIndex(levelStr + i + "_id")));
                levelBeen.setName(cursor.getString(cursor.getColumnIndex("name")));
                levelBeenList.add(levelBeen);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return levelBeenList;
    }


    /**
     * method to update the beneficiarytemp table
     *
     * @param responseDump
     * @param beneficiary
     */
    public void updateBeneficiaryData(JSONObject responseDump, String beneficiary, boolean isAddressUpdate) {
        ContentValues cv = new ContentValues();
        try {
            database = this.getWritableDatabase();
            JSONArray jsonArray = new JSONArray();
            cv.put(PARTNER_ID_KEY, responseDump.getString(PARTNER_ID_KEY));
            cv.put(PARENT_ID_KEY, responseDump.getString(PARENT_ID_KEY));
            cv.put(PARENT_UUID_KEY, responseDump.getString(PARENT_UUID_KEY));
            cv.put(BENEFICIARY_NAME, responseDump.getString("name"));
            cv.put(BENEFICIARY_TYPE_KEY, responseDump.getString(btype));
            String status = responseDump.getString(STATUS_KEY);
            Logger.logV(TAG, status);
            cv.put(STATUS_KEY, responseDump.getString(STATUS_KEY));
            if (("Offline").equalsIgnoreCase(responseDump.getString(STATUS_KEY)) || isAddressUpdate) {
                cv.put(Constants.SYNC_STATUS, 1);
                String addressDump = responseDump.getString(ADDRESS_STRING);
                Logger.logD("", "aadress dump of offline records" + addressDump);
                cv.put(ADDRESS_STRING, addressDump);
            } else {
                cv.put(Constants.SYNC_STATUS, 3);
                String addressDump = responseDump.getString(ADDRESS_STRING);
                Logger.logD("", "aadress dump" + addressDump);
                Logger.logD("", "uuid to update the table" + responseDump.getString("UUID"));
                JSONObject jsonObject = new JSONObject(addressDump);
                Iterator<String> keyList = jsonObject.keys();
                while (keyList.hasNext()) {
                    String key = keyList.next();
                    jsonObject.get(key);
                    JSONObject object = jsonObject.getJSONObject(key);
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put(ADDRESS1_KEY, object.getString(ADDRESS1_KEY));
                    jsonObject1.put(ADDRESS2_KEY, object.getString(ADDRESS2_KEY));
                    jsonObject1.put(PINCODE_KEY, object.getString(PINCODE_KEY));
                    jsonObject1.put(PRIMARY, object.getInt(PRIMARY));
                    jsonObject1.put(LOCATION_LEVEL_KEY, object.getInt(LOCATION_LEVEL_KEY));
                    jsonObject1.put(BOUNDARY_ID_KEY, object.getString(BOUNDARY_ID_KEY));
                    if (object.has(PROOF_ID_KEY))
                        jsonObject1.put(PROOF_ID_KEY, object.getString(PROOF_ID_KEY));
                    jsonObject1.put(LEAST_LOCATION_NAME_KEY, object.getString(LEAST_LOCATION_NAME_KEY));
                    if (object.has(ADDRESS_ID_KEY)) {
                        jsonObject1.put(ADDRESS_ID_KEY, object.getString(ADDRESS_ID_KEY));
                    } else {
                        jsonObject1.put(ADDRESS_ID_KEY, "");
                    }
                    jsonArray.put(jsonObject1);
                }
                Logger.logD(TAG, "Json Array response after updating in beneficiary table" + jsonArray.toString());
                cv.put(ADDRESS_STRING, jsonArray.toString());
            }
            cv.put("age", responseDump.getString("age"));
            String age = responseDump.getString("age");
            Logger.logV(TAG, age);
            cv.put(GENDER_KEY, responseDump.getString(GENDER_KEY));
            cv.put(DOB, responseDump.getString("dob"));
            String dob = responseDump.getString(DOB);
            Logger.logV(TAG, dob);
            cv.put(ALIAS_NAME_KEY, responseDump.getString(ALIAS_NAME_KEY));
            cv.put(BENEFICIARY_TYPE_ID_KEY, responseDump.getString(BENEFICIARY_TYPE_ID_KEY));
            cv.put(CONTACT_NO, responseDump.getString(CONTACT_NO));
            int result = database.update(beneficiary, cv, "uuid" + " = ?", new String[]{responseDump.getString("UUID")});
            Logger.logV(TAG, "result----> " + result + " beneficiary details after updating to table ===>" + cv.toString());
            database.close();

        } catch (Exception e) {
            Logger.logE(TAG, "Error on updating the beneficiary details to beneficiaryTemp table", e);
        }

    }

    public long updateOfflineFacilityData(JSONObject responseDump, String idString) {
        long record = 0;
        ContentValues cv = new ContentValues();
        try {
            database = this.getWritableDatabase();
            cv.put(FACILITY_NAME_KEY, responseDump.getString(FACILITY_NAME_KEY));
            cv.put(FACILITY_TYPE_ID_KEY, responseDump.getString(FACILITY_TYPE_ID_KEY));
            cv.put(FACILITY_SUBTYPE_ID_KEY, responseDump.getString(FACILITY_SUBTYPE_ID_KEY));
            cv.put(FACILITY_SUBTYPE_KEY, responseDump.getString(FACILITY_SUBTYPE_KEY));
            cv.put(THEMATIC_AREA_ID_KEY, responseDump.getString(THEMATIC_AREA_ID_KEY));
            cv.put(THEMATIC_AREA_KEY, responseDump.getString(THEMATIC_AREA_KEY));
            cv.put(SERVICES_STRING, responseDump.getString(SERVICES_STRING));
            cv.put(ADDRESS1_KEY, responseDump.getString(ADDRESS1_KEY));
            cv.put(ADDRESS2_KEY, responseDump.getString(ADDRESS2_KEY));
            cv.put(BOUNDARY_ID_KEY, responseDump.getString(BOUNDARY_ID_KEY));
            cv.put(BOUNDARY_NAME_KEY, responseDump.getString(BOUNDARY_NAME_KEY));
            cv.put(PINCODE_KEY, responseDump.getString(PINCODE_KEY));
            cv.put(LOCATION_LEVEL_KEY, responseDump.getString(BOUNDARY_LEVEL_KEY));
            cv.put(STATUS_KEY, responseDump.getString(STATUS_KEY));
            if ((updateStr).equalsIgnoreCase(responseDump.getString(STATUS_KEY))) {
                cv.put(Constants.SYNC_STATUS, 3);
            } else {
                cv.put(Constants.SYNC_STATUS, 1);
            }
            record = database.update(FACILITY_TABLE_NAME, cv, "uuid" + " = ?", new String[]{idString});
            Logger.logV(TAG, "Facility details===>" + cv.toString());
        } catch (Exception e) {
            Logger.logE(TAG, "Error on inserting the beneficiary details to beneficiaryTemp table", e);
        }
        return record;
    }

    public List<Facility> getFacilityDetails() {
        List<Facility> datumList = new ArrayList<>();
        try {
            SQLiteDatabase db = openDataBase();
            String query = "Select * From Facility where status ='Update'";
            getOfflineFacilityUpdateDetails(datumList, db);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    Facility datum = new Facility();
                    datum.setId(cursor.getInt(cursor.getColumnIndex(Constants.SERVER_PRIMARY)));
                    datum.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                    datum.setName(cursor.getString(cursor.getColumnIndex(FACILITYNAME)));
                    datum.setAddress1(cursor.getString(cursor.getColumnIndex(Constants.ADDRESS1)));
                    datum.setAddress2(cursor.getString(cursor.getColumnIndex(Constants.ADDRESS2)));
                    datum.setFacilityTypeId(cursor.getInt(cursor.getColumnIndex(FACILITY_TYPE_ID_KEY)));
                    datum.setFacilitySubtypeId(cursor.getInt(cursor.getColumnIndex(FACILITY_SUBTYPE_ID_KEY)));
                    datum.setBoundaryId(cursor.getInt(cursor.getColumnIndex(BOUNDARY_ID_KEY)));
                    datum.setServices(cursor.getString(cursor.getColumnIndex(SERVICES_STRING)));
                    datum.setThematicAreaId(cursor.getInt(cursor.getColumnIndex(THEMATIC_AREA_ID_KEY)));
                    datum.setPincode(cursor.getInt(cursor.getColumnIndex(Constants.PINCODE)));
                    datum.setSyncStatus(String.valueOf(cursor.getInt(cursor.getColumnIndex(Constants.SYNC_STATUS))));
                    datumList.add(datum);
                }
                while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
        return datumList;
    }

    private void getOfflineFacilityUpdateDetails(List<Facility> datumList, SQLiteDatabase db) {
        try {
            String query = "Select * from Facility where status ='Update'";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    Facility datum = new Facility();
                    datum.setName(cursor.getString(cursor.getColumnIndex(FACILITYNAME)));
                    datum.setAddress1(cursor.getString(cursor.getColumnIndex(Constants.ADDRESS1)));
                    datum.setAddress2(cursor.getString(cursor.getColumnIndex(Constants.ADDRESS2)));
                    datum.setFacilityTypeId(cursor.getInt(cursor.getColumnIndex(FACILITY_TYPE_ID_KEY)));
                    datum.setFacilitySubtypeId(cursor.getInt(cursor.getColumnIndex(FACILITY_SUBTYPE_ID_KEY)));
                    datum.setBoundaryId(cursor.getInt(cursor.getColumnIndex(BOUNDARY_ID_KEY)));
                    datum.setServices(cursor.getString(cursor.getColumnIndex(SERVICES_STRING)));
                    datum.setThematicAreaId(cursor.getInt(cursor.getColumnIndex(THEMATIC_AREA_ID_KEY)));
                    datum.setPincode(cursor.getInt(cursor.getColumnIndex(Constants.PINCODE)));
                    datum.setSyncStatus(String.valueOf(cursor.getInt(cursor.getColumnIndex(Constants.SYNC_STATUS))));
                    datumList.add(datum);
                }
                while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
    }

    public String getLocationTypeFacilityFromDB(int leastLocationId, String boundarylevelList) {
        String locationType = "";
        try {
            String pendingSurveyQuery = "select location_type from Level" + boundarylevelList + " where id = " + leastLocationId;
            Cursor cursor;
            SQLiteDatabase db = openDataBase();
            cursor = db.rawQuery(pendingSurveyQuery, null);
            Logger.logD("", "Get location type from level 7" + pendingSurveyQuery);
            if (cursor.moveToFirst()) {
                locationType = cursor.getString(cursor.getColumnIndex(LOCATION_TYPE_KEY));
                cursor.close();
            } else {
                locationType = "";
            }
        } catch (Exception e) {
            Logger.logE("", "Exception on getting the location type", e);
        }
        return locationType;

    }

    public List<FacilitySubTypeBeen> getFacilitySubType(String facilitySubTypes, int beneficiaryId) {
        List<FacilitySubTypeBeen> facilitiesTypesBeens = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(facilitySubTypes);
            FacilitySubTypeBeen setDefault = new FacilitySubTypeBeen(0, Constants.SUB_TYPE);
            facilitiesTypesBeens.add(setDefault);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (beneficiaryId == (object.getInt("id"))) {
                    JSONArray jsonArray1 = object.getJSONArray("sub_category");
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject jsonObject = jsonArray1.getJSONObject(j);
                        FacilitySubTypeBeen facilitySubTypeBeen = new FacilitySubTypeBeen();
                        facilitySubTypeBeen.setId(jsonObject.getInt("id"));
                        facilitySubTypeBeen.setName(jsonObject.getString("name"));
                        facilitiesTypesBeens.add(facilitySubTypeBeen);
                    }
                }
            }
        } catch (Exception e) {
            Logger.logE(TAG, "Exception on getting the category", e);
        }


        Logger.logD(TAG, "facilities type---" + facilitiesTypesBeens.size());
        return facilitiesTypesBeens;
    }


    public String getBeneficiaryLastModifiedDate(String beneficiaryTypeId, String onRefreshLastItemModifiedDate) {
        String lastModifiedDate = "";
        String query = "";
        SQLiteDatabase db = openDataBase();
        if (onRefreshLastItemModifiedDate.isEmpty()) {
            query = selectBenQuery + beneficiaryTypeId + " ORDER BY last_modified DESC LIMIT 1";
        } else {
            query = selectBenQuery + beneficiaryTypeId + " and last_modified < '" + onRefreshLastItemModifiedDate + "' ORDER BY last_modified DESC LIMIT 1";
        }
        Cursor cursor = db.rawQuery(query, null);
        Logger.logV(TAG, "query to get the last modiifed date fron beneficary" + query);
        try {
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                lastModifiedDate = cursor.getString(cursor.getColumnIndex(LAST_MODIFIED_KEY));
            } else {
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE("", "", e);
        }
        return lastModifiedDate;
    }

    public String getFacilityLastModifiedDate(String beneficiaryTypeId, String onRefreshLastItemModifiedDate) {
        SQLiteDatabase db = openDataBase();
        String modifiedDate = "";
        String offlineQuery = "";
        modifiedDate = getOfflineFacilityLastModifiedDate(beneficiaryTypeId, onRefreshLastItemModifiedDate);
        if (modifiedDate.isEmpty()) {
            if (onRefreshLastItemModifiedDate.isEmpty()) {
                offlineQuery = "SELECT * FROM Facility where facility_type_id = " + beneficiaryTypeId + " ORDER BY modified DESC LIMIT 1";
            } else {
                offlineQuery = "SELECT * FROM Facility where facility_type_id = " + beneficiaryTypeId + " and modified < '" + onRefreshLastItemModifiedDate + "' ORDER BY modified DESC LIMIT 1";
            }
            Cursor cursor = db.rawQuery(offlineQuery, null);
            Logger.logV(TAG, "query to get the last modiifed date fron Facility" + offlineQuery);
            try {
                if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                    modifiedDate = cursor.getString(cursor.getColumnIndex(MODIFIED_KEY));
                    Logger.logV(TAG, "get the last modiifed date fron Facility" + modifiedDate);

                } else {
                    cursor.close();
                }
            } catch (Exception e) {
                Logger.logE("", "", e);
            }
        }
        return modifiedDate;
    }

    private String getOfflineFacilityLastModifiedDate(String beneficiaryTypeId, String onRefreshLastItemModifiedDate) {
        String modifiedDate = "";
        String offlineQuery = "";
        SQLiteDatabase db = openDataBase();
        if (onRefreshLastItemModifiedDate.isEmpty()) {
            offlineQuery = "SELECT * FROM Facility where sync_status = 1  and facility_type_id = " + beneficiaryTypeId + " ORDER BY modified DESC LIMIT 1";
        } else {
            offlineQuery = "SELECT * FROM Facility where sync_status = 1  and facility_type_id = " + beneficiaryTypeId + " and modified < '" + onRefreshLastItemModifiedDate + "' ORDER BY modified DESC LIMIT 1";
        }
        Cursor cursor = db.rawQuery(offlineQuery, null);
        Logger.logV(TAG, "query to get the last modiifed date fron FacilityTemp" + offlineQuery);
        try {
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                modifiedDate = cursor.getString(cursor.getColumnIndex(MODIFIED_KEY));
                Logger.logV(TAG, "get the last modiifed date fron FacilityTemp" + modifiedDate);

            } else {
                cursor.close();
                modifiedDate = "";
            }
        } catch (Exception e) {
            Logger.logE("", "", e);
        }
        return modifiedDate;
    }

    public List<String> getBeneficiaryNotSynclist() {
        List<String> stringList = new ArrayList<>();
        try {
            SQLiteDatabase db = openDataBase();
            String query = "Select * From Beneficiary where sync_status = 1";
            Cursor cursor = db.rawQuery(query, null);
            Logger.logD(TAG, "" + query);
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {

                    stringList.add(cursor.getString(cursor.getColumnIndex("uuid")));
                }
                while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE("", "", e);
        }
        return stringList;
    }

    public void deleteAllRecords() {
        String query;
        database = this.getWritableDatabase();
        query = "DELETE FROM Facility";
        database.execSQL(query);
        Logger.logD(TAG, "deleted  query for facility " + query);
        query = "DELETE FROM Beneficiary";
        database.execSQL(query);
        Logger.logD(TAG, "deleted  query for beneficiary " + query);
        query = "DELETE FROM Periodicity";
        database.execSQL(query);
        Logger.logD(TAG, "deleted  query for periodicity " + query);
        query = "DELETE FROM Surveys";
        database.execSQL(query);
        Logger.logD(TAG, "deleted  query for surveys" + query);
    }

    public String getSingleBeneficiaryDetail(String uuid, String facId) {
        String modifyDate = "";

        String selectQuery = "select * from Beneficiary where  uuid= '" + uuid + "'";
        if (!facId.isEmpty())
            selectQuery = "select * from Facility where  uuid= '" + facId + "'";
        SQLiteDatabase db = openDataBase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Logger.logD("ggggggggggggg", "" + selectQuery + cursor.getCount());
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            if (!facId.isEmpty()) {
                modifyDate = cursor.getString(cursor.getColumnIndex(MODIFIED_KEY));
            } else {
                modifyDate = cursor.getString(cursor.getColumnIndex("last_modified"));
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        return modifyDate;
    }

    public Map<Integer, LocationSurveyBeen> getLocationOnlineSurveyRecords(String clusterIds, String surveyId, Map<Integer, LocationSurveyBeen> locationListBean, String periodicity, int pLimit) {
        String query = "SELECT * FROM Periodicity WHERE survey_id =" + surveyId + " and cluster_id IN (" + clusterIds + ") GROUP BY cluster_id ORDER BY date_capture DESC";
        SQLiteDatabase db = openDataBase();
        Cursor cursor = db.rawQuery(query, null);
        Logger.logV(TAG, "getLocationOnlineSurveyRecords query: " + query + " count " + cursor.getCount());
        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {

                int id = cursor.getInt(cursor.getColumnIndex("cluster_id"));
                LocationSurveyBeen tempBean = locationListBean.get(id);
                tempBean.setIsOnline(1);
                tempBean.setCaptureDate(cursor.getString(cursor.getColumnIndex(Constants.dateCaptureStr)));
                if (PeriodicityUtils.isCurrentPeriodicity(tempBean.getCaptureDate(), periodicity)) {
                    tempBean.setIsEditable(1);
                    tempBean.setSurveyStatusFlag(-2);
                } else if (PeriodicityUtils.isBelongsToPreviousPeriodicity(periodicity, tempBean.getCaptureDate())) {
                    tempBean.setIsEditable(2);
                    tempBean.setSurveyStatusFlag(1);
                } else
                    tempBean.setSurveyStatusFlag(2);
                locationListBean.put(id, tempBean);

            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return locationListBean;


    }

    public HashMap<String, List<Datum>> getSurveyHeading() {
        HashMap<String, List<Datum>> fillParentChild = new HashMap<>();
        List<String> stringList = new ArrayList<>();
        try {
            SQLiteDatabase db = openDataBase();
            String query = "select category_name,category_id from Surveys group by category_id";
            Cursor cursor = db.rawQuery(query, null);
            Logger.logD(TAG, "" + query);
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {

                    String getCategoryName = cursor.getString(cursor.getColumnIndex("category_name"));
                    String getCategoryID = cursor.getString(cursor.getColumnIndex("category_id"));
                    List<Datum> getChildList = getAllChiildReleated(getCategoryID);
                    Logger.logV(TAG, "category_name query: " + cursor.getString(cursor.getColumnIndex("category_name")));
                    if (!getChildList.isEmpty())
                        fillParentChild.put(getCategoryName, getChildList);
                }
                while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE("", "", e);
        }
        return fillParentChild;
    }

    public List<String> getHeading() {
        List<String> stringList = new ArrayList<>();
        try {
            SQLiteDatabase db = openDataBase();
            String query = "select category_name,category_id from Surveys group by category_id";
            Cursor cursor = db.rawQuery(query, null);
            Logger.logD(TAG, "" + query);
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {

                    String getCategoryName = cursor.getString(cursor.getColumnIndex("category_name"));
                    String getCategoryID = cursor.getString(cursor.getColumnIndex("category_id"));
                    stringList.add(getCategoryName);
                }
                while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE("", "", e);
        }
        return stringList;
    }

    private List<Datum> getAllChiildReleated(String getCategoryID) {
        List<Datum> childTempList = new ArrayList<>();
        try {
            SQLiteDatabase db = openDataBase();
            String query = "select * from Surveys where category_id=" + getCategoryID + " order by order_levels";
            Cursor cursor = db.rawQuery(query, null);
            Logger.logD(TAG, "" + query);
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    Datum datum = new Datum();
                    datum.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id"))));
                    datum.setName(cursor.getString(cursor.getColumnIndex("surveyName")));
                    datum.setBeneficiaryTypeId(cursor.getInt(cursor.getColumnIndex("beneficiary_ids")));
                    datum.setActive(cursor.getInt(cursor.getColumnIndex("category_id")));
                    childTempList.add(datum);
                }
                while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE("", "", e);
        }
        return childTempList;
    }

    public List<Level1> setStateSpinner(String orderLevel) {
        List<Level1> getLevelTemp = new ArrayList<>();
        Logger.logV(TAG, "the level is............" + level);
        SQLiteDatabase db = openDataBase();
        String query = SELECT_FROM + orderLevel;
        Cursor cursor = db.rawQuery(query, null);
        Logger.logV(TAG, "the value is" + query);
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                Logger.logV(TAG, "the names are" + name);
                Level1 level1 = new Level1(0, id, "", 0, name);
                getLevelTemp.add(level1);
            } while (cursor.moveToNext());
            if (cursor != null) {
                cursor.close();
            }
        }
        return getLevelTemp;
    }

    public List<Level1> setSpinnerByID(String previous, String tablename, int countryList) {
        List<Level1> getLevelTemp = new ArrayList<>();
        Logger.logV(TAG, "the level is............" + level);
        SQLiteDatabase db = openDataBase();
        String query = SELECT_FROM + tablename + " where " + previous + "=" + countryList + " and active=2";
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             Cursor cursor = db.rawQuery(query, null);
        Logger.logV(TAG, "the value is" + query);
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                Logger.logV(TAG, "the names are" + name);
                Level1 level1 = new Level1(0, id, "", 0, name);
                getLevelTemp.add(level1);
            } while (cursor.moveToNext());
            if (cursor != null) {
                cursor.close();
            }
        }
        return getLevelTemp;
    }
    public List<LevelBeen> setSpinnerD(String previous, String tablename, int countryList, SharedPreferences sharedpreferences) {
        List<LevelBeen> getLevelTemp = new ArrayList<>();
        LevelBeen levelBeendefault= new LevelBeen();
        levelBeendefault.setId(0);
        levelBeendefault.setName("Select");
        levelBeendefault.setLocationLevel(2);
        getLevelTemp.add(levelBeendefault);
        Logger.logV(TAG, "the level is............" + level);
        SQLiteDatabase db = openDataBase();
        String DynamicQuery="";
        if (sharedpreferences.getString(Constants.PROJECTFLOW,"").equalsIgnoreCase("1")){
            DynamicQuery= getDynamicQuery(sharedpreferences,tablename,previous,countryList);
        }else{
            DynamicQuery = SELECT_FROM + tablename + " where " + previous + "=" + countryList + " and active=2";
        }
        Cursor cursor = db.rawQuery(DynamicQuery, null);
        Logger.logV(TAG, "the value is" + DynamicQuery);
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                Logger.logV(TAG, "the names are" + name);
                LevelBeen level1 = new LevelBeen(id, name);
                getLevelTemp.add(level1);
            } while (cursor.moveToNext());
            if (cursor != null) {
                cursor.close();
            }
        }
        return getLevelTemp;
    }

    private String getDynamicQuery(SharedPreferences sharedpreferences, String tablename,
                                   String previous, int countryList) {
        String query="";
        switch (tablename){
            case "level1":
                break;
            case "level2":
                String getTaggedLocationLevel2=sharedpreferences.getString(Constants.LEVEL2_ID,"");
                query = SELECT_FROM + tablename + " where " + previous + "=" + countryList + " and id IN ("+getTaggedLocationLevel2+")  and active=2";
                break;
            case "level3":
                String getTaggedLocationLevel3=sharedpreferences.getString(Constants.LEVEL3_ID,"");
                query = SELECT_FROM + tablename + " where " + previous + "=" + countryList + " and id IN ("+getTaggedLocationLevel3+")  and active=2";
                break;
            case "level4":
                String getTaggedLocationLevel4=sharedpreferences.getString(Constants.LEVEL4_ID,"");
                query = SELECT_FROM + tablename + " where " + previous + "=" + countryList + " and id IN ("+getTaggedLocationLevel4+")  and active=2";
                break;
            case "level5":
                String getTaggedLocationLevel5=sharedpreferences.getString(Constants.LEVEL5_ID,"");
                query = SELECT_FROM + tablename + " where " + previous + "=" + countryList + " and id IN ("+getTaggedLocationLevel5+")  and active=2";
                break;
            case "level6":
                String getTaggedLocationLevel6=sharedpreferences.getString(Constants.LEVEL6_ID,"");
                query = SELECT_FROM + tablename + " where " + previous + "=" + countryList + " and id IN ("+getTaggedLocationLevel6+")  and active=2";
                break;
            case "level7":
                String getTaggedLocationLevel7=sharedpreferences.getString(Constants.LEVEL7_ID,"");
                query = SELECT_FROM + tablename + " where " + previous + "=" + countryList + " and id IN ("+getTaggedLocationLevel7+")  and active=2";
                break;
            default:
                break;
        }
        return query;
    }

    public String getSummaryQid(int survey_id, ExternalDbOpenHelper dbHelper) {
        String qid = "";
        String query = "SELECT * FROM Surveys  where surveyId = " + survey_id;
        Logger.logD("getSummaryQid", "getSummaryQid --" + query);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        Logger.logD("getSummaryQid", "getSummaryQid cursor count--" + cursor.getCount());
        if (cursor != null && cursor.moveToFirst()) {
            qid = cursor.getString(cursor.getColumnIndex("summaryQid"));
            cursor.close();
        }
        if (qid == null)
            qid = "";
        return qid;
    }


    public List<QuestionAnswer> getLinkageHeadings(String surveysId, ExternalDbOpenHelper dbHandler) {
        List<QuestionAnswer> getTempNames = new ArrayList<>();
        try {
            String selectQuery = "Select * from SurveyLinkage where SurveyLinkage.form_type_id IN (" + surveysId + ") group by name";
            SQLiteDatabase db = dbHandler.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    String holderName = cursor.getString(cursor.getColumnIndex("name"));
                    int relation_id = cursor.getInt(cursor.getColumnIndex("relation_id"));
                    Logger.logD("holderName", "holderName here " + holderName);
                    QuestionAnswer questionAnswer = new QuestionAnswer();
                    questionAnswer.setQuestionText(holderName);
                    questionAnswer.setRelationId(relation_id);
                    getTempNames.add(questionAnswer);

                } while (cursor.moveToNext());
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return getTempNames;
    }


    public String getGroupIds(int surveysId, ExternalDbOpenHelper dbHandler) {
        String getSelectedUUids = "";
        try {
            String pendingSurveyQuery = "Select survey_type from Surveys  where Surveys.surveyid=" + surveysId;
            SQLiteDatabase db = dbHandler.getWritableDatabase();
            Cursor cursor = db.rawQuery(pendingSurveyQuery, null);
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    String getChildUUId = cursor.getString(cursor.getColumnIndex("survey_type"));
                    String trimStart = getChildUUId.replace("[", "");
                    getSelectedUUids = trimStart.replace("]", "");

                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logV("", "checkPrymarySaveDraftExist from Survey table" + e);

        }

        return getSelectedUUids;
    }

    public String getQuestionids(String getGroupIds, ExternalDbOpenHelper dbOpenHelper) {
        String getSelectedUUids = "";
        try {
            String pendingSurveyQuery = "Select summaryQid from Surveys  where Surveys.surveyid=" + getGroupIds;
            SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(pendingSurveyQuery, null);
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    String getQids = cursor.getString(cursor.getColumnIndex("summaryQid"));
                    if (!getQids.equals("")) {
                        String[] getFirstQuestion = getQids.split(",");
                        getSelectedUUids = getFirstQuestion[0];
                    }


                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logV("", "checkPrymarySaveDraftExist from Survey table" + e);

        }

        return getSelectedUUids;
    }


    public List<LevelBeen> getLevelsrecords(String orderLeve, ExternalDbOpenHelper dbOpenHelper, SharedPreferences prefs) {
        List<LevelBeen> tempList= new ArrayList<>();
        LevelBeen levelBeendefault= new LevelBeen();
        levelBeendefault.setId(0);
        levelBeendefault.setName(" Select ");
        levelBeendefault.setLocationLevel(2);
        tempList.add(levelBeendefault);
        try {
            String pendingSurveyQuery="";
            if (prefs.getString(Constants.PROJECTFLOW,"").equalsIgnoreCase("1")){
                pendingSurveyQuery=createDynamicQuery(prefs,orderLeve);
            }else if (prefs.getString(Constants.PROJECTFLOW,"").equalsIgnoreCase("0")){
                pendingSurveyQuery = "select * from "+orderLeve+"";
            }

            SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(pendingSurveyQuery, null);
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String modified_date = cursor.getString(cursor.getColumnIndex("modified_date"));
                    int active = cursor.getInt(cursor.getColumnIndex("active"));
                    int previoudLevelIds = cursor.getInt(cursor.getColumnIndex(orderLeve+"_id"));
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    LevelBeen levelBeen= new LevelBeen();
                    levelBeen.setId(id);
                    levelBeen.setName(name);
                    levelBeen.setLocationLevel(previoudLevelIds);
                    tempList.add(levelBeen);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logV("", "checkPrymarySaveDraftExist from Survey table" + e);

        }

        return tempList;
    }

    private String createDynamicQuery(SharedPreferences prefs, String orderLeve) {
        String tempQuery="";
        switch (orderLeve){
            case "level1":

                break;
            case "level2":
                String getTaggedLocationLevel2=prefs.getString(Constants.LEVEL2_ID,"");
                tempQuery= "select * from "+orderLeve+" where  id IN ("+getTaggedLocationLevel2+")  and active=2";
                break;
            case "level3":
                String getTaggedLocationLevel3=prefs.getString(Constants.LEVEL3_ID,"");
                tempQuery= "select * from "+orderLeve+" where  id IN ("+getTaggedLocationLevel3+")  and active=2";
                break;
            case "level4":
                String getTaggedLocationLevel4=prefs.getString(Constants.LEVEL4_ID,"");
                tempQuery= "select * from "+orderLeve+" where  id IN ("+getTaggedLocationLevel4+")  and active=2";
                break;
            case "level5":
                String getTaggedLocationLevel5=prefs.getString(Constants.LEVEL5_ID,"");
                tempQuery= "select * from "+orderLeve+" where  id IN ("+getTaggedLocationLevel5+")  and active=2";
                break;
            case "level6":
                String getTaggedLocationLevel6=prefs.getString(Constants.LEVEL6_ID,"");
                tempQuery= "select * from "+orderLeve+" where  id IN ("+getTaggedLocationLevel6+")  and active=2";
                break;
            case "level7":
                String getTaggedLocationLevel7=prefs.getString(Constants.LEVEL7_ID,"");
                tempQuery= "select * from "+orderLeve+" where  id IN ("+getTaggedLocationLevel7+")  and active=2";
                break;


        }
        return tempQuery;
    }


    public void deleteLevels(String tableName) {
        String query;
        database = this.getWritableDatabase();
        query = "DELETE FROM "+tableName;
        database.execSQL(query);
    }

    public void deleteCompleteSurveys() {
        String query;
        database = this.getWritableDatabase();
        query = "DELETE FROM Surveys ";
        database.execSQL(query);
    }

    public void updateProjectResponse(Project project) {
        try {
            ContentValues cv = new ContentValues();
            database = this.getWritableDatabase();
            for (int i = 0; i < project.getProjectList().size(); i++) {
                cv.put("project_id", project.getProjectList().get(i).getProjectId());
                cv.put("project_name", project.getProjectList().get(i).getProjectName());
                cv.put("created_on", project.getProjectList().get(i).getCreatedOn());
                cv.put("modified_on", "");
                cv.put("active", "");
                if (!project.getProjectList().get(i).getActivitylist().isEmpty())
                      updateActiveDatabase(project.getProjectList().get(i).getProjectId(),
                            project.getProjectList().get(i).getActivitylist());
                database.insertWithOnConflict("Project", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            }

        } catch (Exception e) {
            Logger.logE("", "", e);
        }
    }

    private void updateActiveDatabase(Integer projectId, List<Activitylist> activitylist) {
        try {
            ContentValues cv = new ContentValues();
            database = this.getWritableDatabase();
            for (int i = 0; i < activitylist.size(); i++) {
                cv.put("project_id", projectId);
                cv.put("activity_name", activitylist.get(i).getActivityName());
                cv.put("activity_id", activitylist.get(i).getActivityId());
                cv.put("levels", activitylist.get(i).getOrderLevels());
                cv.put("lables", activitylist.get(i).getLabels());
                if (!activitylist.get(i).getOrderLevels().isEmpty()){
                    String[] getLevelsLables= activitylist.get(i).getOrderLevels().split(",");
                    fillLableIfExistLevels(getLevelsLables,cv,activitylist.get(i).getLevelids());
                }
                database.insertWithOnConflict("ProjectActivityTable", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            }

        } catch (Exception e) {
            Logger.logE("", "", e);
        }
    }

    private void fillLableIfExistLevels(String[] getLevelsLables, ContentValues cv, List<Levelid> levelids) throws JSONException {
    Gson gson= new Gson();
    String levelIds=gson.toJson(levelids);
        Logger.logD("levelids",levelIds);
        JSONArray jsonArray= new JSONArray(levelIds);
      for (int i=0;i<getLevelsLables.length;i++){
          switch (getLevelsLables[i]){
              case "level1":
                  JSONObject jsonObjectLevel1= jsonArray.getJSONObject(i);
                  cv.put("level1", jsonObjectLevel1.getString(getLevelsLables[i]));

                  break;
              case "level2":
                  JSONObject jsonObjectLevel2= jsonArray.getJSONObject(i);
                  cv.put("level2", jsonObjectLevel2.getString(getLevelsLables[i]));
                  break;
              case "level3":
                  JSONObject jsonObjectLevel3= jsonArray.getJSONObject(i);
                  cv.put("level3", jsonObjectLevel3.getString(getLevelsLables[i]));
                  break;
              case "level4":
                  JSONObject jsonObjectLevel4= jsonArray.getJSONObject(i);
                  cv.put("level4", jsonObjectLevel4.getString(getLevelsLables[i]));
                  break;
              case "level5":
                  JSONObject jsonObjectLevel5= jsonArray.getJSONObject(i);
                  cv.put("level5", jsonObjectLevel5.getString(getLevelsLables[i]));
                  break;
              case "level6":
                  JSONObject jsonObjectLevel6= jsonArray.getJSONObject(i);
                  cv.put("level6", jsonObjectLevel6.getString(getLevelsLables[i]));
                  break;
              case "level7":
                  JSONObject jsonObjectLevel7= jsonArray.getJSONObject(i);
                  cv.put("level7", jsonObjectLevel7.getString(getLevelsLables[i]));
                  break;


          }
      }
    }

    public void deleteProject() {
        String query;
        database = this.getWritableDatabase();
        query = "DELETE FROM Project";
        database.execSQL(query);
    }

    public void deleteProjectActivity() {
        String query;
        database = this.getWritableDatabase();
        query = "DELETE FROM ProjectActivityTable";
        database.execSQL(query);
    }

    public List<ProjectList> getAllProjectList(ExternalDbOpenHelper dbhelper) {
        List<ProjectList> tempList= new ArrayList<>();
        try {
            String pendingSurveyQuery = "select * from Project";
            SQLiteDatabase db = dbhelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(pendingSurveyQuery, null);
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    int projectId = cursor.getInt(cursor.getColumnIndex("project_id"));
                    String projectName = cursor.getString(cursor.getColumnIndex("project_name"));
                    String created_on = cursor.getString(cursor.getColumnIndex("created_on"));

                    ProjectList projectList= new ProjectList();
                    projectList.setProjectId(projectId);
                    projectList.setProjectName(projectName);
                    tempList.add(projectList);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logV("", "checkPrymarySaveDraftExist from Survey table" + e);

        }

        return tempList;
    }

    public List<SurveyDetail> getLocationActivityBasedProject(Integer projectId, ExternalDbOpenHelper dbhelper, int getFlag) {
        List<SurveyDetail> tempList= new ArrayList<>();
        try {
            String pendingSurveyQuery="";
            if (getFlag==200) {
                pendingSurveyQuery = "select Surveys.surveyName,Surveys.surveyId, Project.project_id,Project.project_name, Surveys.PeriodicityFlag ,\n" +
                       "Surveys.beneficiary_ids, Surveys.beneficiary_type, Surveys.category_name ,ProjectActivityTable.lables,ProjectActivityTable.levels, " +
                        "ProjectActivityTable.Level1,ProjectActivityTable.Level2,ProjectActivityTable.Level3,ProjectActivityTable.Level4,ProjectActivityTable.Level5,ProjectActivityTable.Level6,ProjectActivityTable.Level7\n" +
                       " from Surveys\n" +
                       "inner join ProjectActivityTable on ProjectActivityTable.activity_id= Surveys.surveyId\n" +
                       "inner join Project on ProjectActivityTable.project_id = Project.project_id\n" +
                       " where Surveys.beneficiary_ids=\"\" and Project.project_id=" + projectId;
           }else if (getFlag==201){
                pendingSurveyQuery = "select Surveys.surveyName,Surveys.surveyId, Project.project_id,Project.project_name, Surveys.PeriodicityFlag ,\n" +
                        "Surveys.beneficiary_ids, Surveys.beneficiary_type, Surveys.category_name ,ProjectActivityTable.lables,ProjectActivityTable.levels, " +
                        "ProjectActivityTable.Level1,ProjectActivityTable.Level2,ProjectActivityTable.Level3,ProjectActivityTable.Level4,ProjectActivityTable.Level5,ProjectActivityTable.Level6,ProjectActivityTable.Level7\n" +
                        " from Surveys\n" +
                       "inner join ProjectActivityTable on ProjectActivityTable.activity_id= Surveys.surveyId\n" +
                       "inner join Project on ProjectActivityTable.project_id = Project.project_id\n" +
                       " where Surveys.beneficiary_ids !=\"\" and Project.project_id=" + projectId;
           }
            SQLiteDatabase db = dbhelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(pendingSurveyQuery, null);
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    int surveyId = cursor.getInt(cursor.getColumnIndex("surveyId"));
                    int projectID = cursor.getInt(cursor.getColumnIndex("project_id"));
                    String beneficiary_ids = cursor.getString(cursor.getColumnIndex("beneficiary_ids"));
                    String surveyName = cursor.getString(cursor.getColumnIndex("surveyName"));
                    String projectName = cursor.getString(cursor.getColumnIndex("project_name"));
                    String periodicityFlag = cursor.getString(cursor.getColumnIndex("PeriodicityFlag"));
                    String beneficiaryType = cursor.getString(cursor.getColumnIndex("beneficiary_type"));
                    String lables = cursor.getString(cursor.getColumnIndex("lables"));
                    String levels = cursor.getString(cursor.getColumnIndex("levels"));
                    String level1 = cursor.getString(cursor.getColumnIndex("level1"));
                    String level2 = cursor.getString(cursor.getColumnIndex("level2"));
                    String level3 = cursor.getString(cursor.getColumnIndex("level3"));
                    String level4 = cursor.getString(cursor.getColumnIndex("level4"));
                    String level5 = cursor.getString(cursor.getColumnIndex("level5"));
                    String level6 = cursor.getString(cursor.getColumnIndex("level6"));
                    String level7 = cursor.getString(cursor.getColumnIndex("level7"));

                    SurveyDetail surveyDetail= new SurveyDetail();
                    surveyDetail.setSurveyName(surveyName);
                    surveyDetail.setSurveyId(surveyId);
                    surveyDetail.setProjectID(projectID);
                    surveyDetail.setProjectName(projectName);
                    surveyDetail.setPiriodicityFlag(periodicityFlag);
                    surveyDetail.setBeneficiaryType(beneficiaryType);
                    surveyDetail.setBeneficiaryIds(beneficiary_ids);
                    surveyDetail.setLabels(lables);
                    surveyDetail.setOrderLevels(levels);
                    surveyDetail.setLevel1(level1);
                    surveyDetail.setLevel2(level2);
                    surveyDetail.setLevel3(level3);
                    surveyDetail.setLevel4(level4);
                    surveyDetail.setLevel5(level5);
                    surveyDetail.setLevel6(level6);
                    surveyDetail.setLevel7(level7);

                    tempList.add(surveyDetail);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logV("", "checkPrymarySaveDraftExist from Survey table" + e);

        }

        return tempList;
    }

    public List<StatusBean> isSubBeneficiaryAvliable(int surveysId, ExternalDbOpenHelper dbhelper) {
        List<StatusBean> isaviliable=new ArrayList<>();
        try {
            String pendingSurveyQuery = "select Surveys.sub_beneficiary_ids,Surveys.summaryQid from Surveys where Surveys.surveyId="+surveysId;
            SQLiteDatabase db = dbhelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(pendingSurveyQuery, null);
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    String sub_beneficiary_ids = cursor.getString(cursor.getColumnIndex("sub_beneficiary_ids"));
                    String summaryQid = cursor.getString(cursor.getColumnIndex("summaryQid"));
                 JSONArray jsonArray= new JSONArray(sub_beneficiary_ids);
                 JSONObject jsonObject= jsonArray.getJSONObject(0);
                    StatusBean statusBean= new StatusBean();
                    statusBean.setClusterName(summaryQid);
                    statusBean.setSurveyId(String.valueOf(jsonObject.getInt("form_type_id")));
                    statusBean.setSummaryQids(jsonObject.getString("summary_qid"));
                    isaviliable.add(statusBean);
                } while (cursor.moveToNext());

                cursor.close();
            }
        } catch (Exception e) {
            Logger.logV("", "checkPrymarySaveDraftExist from Survey table" + e);

        }
        return isaviliable;
    }
}
