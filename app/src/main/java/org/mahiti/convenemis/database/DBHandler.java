package org.mahiti.convenemis.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.mahiti.convenemis.BeenClass.PreviewQuestionAnswerSet;
import org.mahiti.convenemis.BeenClass.StatusBean;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.PreferenceConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DBHandler extends SQLiteOpenHelper {
    private static final int VERSION = 12;
    public static SQLiteDatabase database;
    private final static String mName = "ENCRYPTED.db";
    private final static String TAG = "DbHndler";
    private String DATABASESECRETKEY = "test12345";
    private static final String ANSCODE = "ans_code";
    private static final String ANSTEXT = "ans_text";
    private static final String QTYPE = "qtype";


    private static final String SERVER_PRIMARY_KEY = "server_primary_key";
    private static final String SURVEY_TABLE= "Survey";
    private static final String GROUP_ID_KEY= "group_id";
    private static final String DATE_FORMAT= "yyyy-MM-dd HH:mm:ss";
    private String andUuidStr = " and uuid='";
    private String getServerPrimaryKeyStr = " getServerPrimaryKey";
    private String pendingSurveyQueryStr ="pendingSurveyQuery";


    /**
     * getting the dbhandler instance
     * @param applicationContext
     */
    public DBHandler(Context applicationContext) {
        super(applicationContext, mName, null, VERSION);
        try {
            if (database == null || !database.isOpen())
                database = this.getWritableDatabase(DATABASESECRETKEY);
        } catch (Exception e) {
            Logger.logE(TAG, "Exception in onCreate method", e);

        }
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        // CREATING THE Survey TABLE for sending the captured details to server
        // sectionId Integer column is treating as periodicity
        String query = "CREATE TABLE IF NOT EXISTS Survey(id INTEGER PRIMARY KEY, start_survey_status INTEGER, " +
                "user_id INTEGER, start_date DATETIME, end_date DATETIME,version_num TEXT, app_version FLOAT, " +
                "language_id INTEGER, latitude DOUBLE, longitude DOUBLE, survey_status INTEGER, " +
                "sync_status  INTEGER, sync_date DATETIME, mode_status TEXT, specimen_id TEXT, reason TEXT, " +
                "cluster_name TEXT, cluster_code TEXT, cluster_id INTEGER, survey_key INTEGER, paper_entry_reason TEXT," +
                " internet_status  TEXT, csv_filepath TEXT, mahiti_server INTEGER, last_qcode TEXT, " +
                "charge_percentage TEXT," +
                " gps_tracker INTEGER," +
                " consent_status TEXT," +
                " survey_ids TEXT, " +
                "cluster_key TEXT," +
                " extra_column1 TEXT," +
                " extra_column2 INTEGER," +
                "beneficiary_details TEXT," +
                "beneficiary_ids TEXT," +
                "facility_ids TEXT," +
                "capture_date DATETIME," +
                "uuid TEXT,fac_uuid,beneficiary_type_id TEXT,facility_type_id TEXT,server_primary_key INTEGER)";

        database.execSQL(query);

        query = "CREATE TABLE IF NOT EXISTS  Response(_id INTEGER PRIMARY KEY,survey_id INTEGER,q_id TEXT,ans_code TEXT," +
                "ans_text TEXT,pre_question INTEGER,next_question INTEGER,answered_on DATETIME,sub_questionId TEXT," +
                "q_code INTEGER,primarykey INTEGER, typology_code TEXT, group_id INTEGER, primary_id INTEGER, response_dump_pid INTEGER, qtype TEXT)";
        database.execSQL(query);


        /*query to create the table for RespSDump*/
        String responseDumpQuery = "CREATE TABLE IF NOT EXISTS  ResponseDump(_id INTEGER PRIMARY KEY,survey_id INTEGER,q_id TEXT, json_dump TEXT)";
        database.execSQL(responseDumpQuery);


        String tabDetails = "CREATE TABLE IF NOT EXISTS Tabdetails ("
                + "id INTEGER PRIMARY KEY," + "survey_id INTEGER,"
                + "CELL_ID TEXT," + "SIGNAL_STRENGTH TEXT," + "LAC TEXT,"
                + "MCC TEXT," + "MNC TEXT," + "LA TEXT," + "CARRIER TEXT,"
                + "NETWORK_TYPE TEXT," + "PHONE_NUMBER TEXT,"
                + "CHARGELEFT TEXT," + "IS_CONNECTED_TO_CHARGE TEXT,"
                + "SIM_SERIALNO TEXT," + "DEVICEID TEXT,"
                + "LAST_CHARGE_TIME DATETIME," + "sectionId INTEGER)";
        database.execSQL(tabDetails);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        String dropQuery = "";
        dropQuery = "DROP TABLE IF EXISTS Survey";
        arg0.execSQL(dropQuery);
        dropQuery = "DROP TABLE IF EXISTS Response";
        arg0.execSQL(dropQuery);
        dropQuery = "DROP TABLE IF EXISTS Tabdetails";
        arg0.execSQL(dropQuery);
        dropQuery = "DROP TABLE IF EXISTS ResponseDump";
        arg0.execSQL(dropQuery);
        arg0.execSQL(dropQuery);
        onCreate(arg0);
    }


    /**
     * method to add the device details to survey rable
     * @param queryValues
     * @return
     */
    public long insertSurveyDataToDB(Map<String, String> queryValues) {
        long insertedRecord = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(Constants.START_SURVEY_STATUS, queryValues.get(Constants.START_SURVEY_STATUS));
            values.put(Constants.USER_ID, queryValues.get("inv_id"));
            values.put("start_date", queryValues.get("start_date"));
            values.put(Constants.END_DATE, queryValues.get(Constants.END_DATE));
            values.put("version_num", queryValues.get("version_num"));
            values.put("app_version", queryValues.get("app_version"));
            values.put("language_id", queryValues.get("language"));
            values.put("latitude", queryValues.get("lat"));
            values.put("longitude", queryValues.get("long"));
            values.put(Constants.SURVEY_STATUS, queryValues.get(Constants.SURVEY_STATUS));
            values.put(Constants.SYNC_STATUS, queryValues.get(Constants.SYNC_STATUS));
            values.put(Constants.SYNC_DATE, queryValues.get(Constants.SYNC_DATE));
            values.put("mode_status", queryValues.get("mode_status"));
            values.put(Constants.SPECIMEN_ID, queryValues.get(Constants.SPECIMEN_ID));
            values.put(Constants.SURVEY_KEY, queryValues.get(Constants.SURVEY_KEY));
            values.put("paper_entry_reason", queryValues.get("paper_entry_reason"));
            values.put("last_qcode", queryValues.get("last_qcode"));
            values.put("gps_tracker", queryValues.get("gps_tracker"));
            values.put("survey_ids", queryValues.get("typology_code"));
            values.put("cluster_id", queryValues.get("cluster_id"));
            values.put("cluster_name", queryValues.get("clustername"));
            values.put("cluster_key", queryValues.get("clusterkey"));
            values.put("beneficiary_details", queryValues.get("beneficiary_details"));
            String benIdStr = "beneficiary_ids";
            values.put(benIdStr, queryValues.get(benIdStr));
            values.put("facility_ids", queryValues.get("facility_ids"));
            values.put("uuid", queryValues.get("uuid"));
            values.put("capture_date", queryValues.get("captured_date"));
            values.put("beneficiary_type_id", queryValues.get("beneficiary_type_id"));
            values.put("facility_type_id", queryValues.get("facility_type_id"));
            values.put(SERVER_PRIMARY_KEY, queryValues.get(SERVER_PRIMARY_KEY));
            if (database == null || !database.isOpen())
                database = this.getWritableDatabase(DATABASESECRETKEY);
            Logger.logD(TAG, "SurveyTable" + values.toString());

            insertedRecord = database.insert(SURVEY_TABLE, null, values);
            Logger.logD(TAG, "content values inserting into survey table " + values.toString());
        } catch (SQLException e) {
            Logger.logE(TAG, "Exception in insertSurveyDataToDB method", e);
            insertedRecord = 0;
        }
        return insertedRecord;
    }


    /**
     * @param queryValues
     * @return
     */
    public long insertResponseDataToDB(Map<String, String> queryValues) {
        if (database == null || !database.isOpen())
            database = this.getWritableDatabase(DATABASESECRETKEY);
        ContentValues values = new ContentValues();
        values.put(Constants.SURVEY_ID, queryValues.get(Constants.SURVEY_ID));
        values.put("q_id", queryValues.get("q_id"));
        values.put(ANSCODE, queryValues.get(ANSCODE));
        values.put(ANSTEXT, queryValues.get(ANSTEXT));
        values.put("pre_question", queryValues.get("pre_question"));
        values.put("next_question", queryValues.get("next_question"));
        values.put("answered_on", queryValues.get("answered_on"));
        values.put("sub_questionId", queryValues.get("sub_questionId"));
        values.put("q_code", queryValues.get("q_code"));
        values.put("primarykey", queryValues.get("primarykey"));
        values.put("typology_code", queryValues.get("typologyId"));
        values.put(GROUP_ID_KEY, queryValues.get(GROUP_ID_KEY));
        values.put("primary_id", queryValues.get("primaryID"));
        values.put(QTYPE, queryValues.get(QTYPE));
        Logger.logV(TAG, "responses " + values.toString());
        Logger.logV(TAG, "group_id " + queryValues.get(GROUP_ID_KEY));
        Logger.logV(TAG, "primaryID " + queryValues.get("primaryID"));
        Logger.logV(TAG, "Values in Response" + values.toString());
        return database.insertOrThrow("Response", null, values);

    }

    public SQLiteDatabase getdatabaseinstance() {
        if (database == null || !database.isOpen())
            database = this.getWritableDatabase(DATABASESECRETKEY);
        return database;
    }

    public SQLiteDatabase getdatabaseinstance_read() {
        if (database == null || !database.isOpen())
            database = this.getReadableDatabase(DATABASESECRETKEY);
        return database;
    }

    /**
     * from the server getting the primary key and storing in the survey table for EDIT functionality.
     *
     * @param surveyId
     * @param serverPrimaryKey
     * @return
     */
    public int updateSurveyDataToDB(int surveyId, int serverPrimaryKey) {

        SQLiteDatabase liteDatabase = this.getWritableDatabase(DATABASESECRETKEY);
        ContentValues values = new ContentValues();
        values.put(SERVER_PRIMARY_KEY, serverPrimaryKey);
        values.put(Constants.START_SURVEY_STATUS, "1");
        values.put(Constants.SURVEY_STATUS, "2");
        values.put(Constants.SYNC_STATUS, "2");
        values.put(Constants.END_DATE, new SimpleDateFormat(DATE_FORMAT).format(new Date()));
        values.put(Constants.SURVEY_KEY, surveyId);
        values.put(Constants.SYNC_DATE, new SimpleDateFormat(DATE_FORMAT).format(new Date()));
        return liteDatabase.update(SURVEY_TABLE, values, "id" + " = ?", new String[]{String.valueOf(surveyId)});
    }

    /**
     * @param surveyId
     * @param reason
     * @param date
     * @param reuse
     * @param bloodSample
     * @param charge
     * @return
     */
    public int exitSurvey_update(int surveyId, String reason, String date, int reuse, String bloodSample, String charge) {
        ContentValues values = new ContentValues();
        values.put(Constants.SURVEY_STATUS, "3");
        values.put(Constants.END_DATE, date);
        Logger.logV(TAG, String.valueOf(reuse));
        return database.update(SURVEY_TABLE, values, "id" + " = ?", new String[]{String.valueOf(surveyId)});
    }




    /**
     * @param surveyId
     * @param reuse
     * @param bloodSample
     * @param charge
     * @return
     */
    public int updateendSurvey_statusDataToDB(int surveyId, int reuse, String bloodSample, String charge) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase(DATABASESECRETKEY);
        ContentValues values = new ContentValues();
        if (reuse == 1) {
            values.put(Constants.SURVEY_STATUS, "1");
            values.put(Constants.END_DATE, new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(new Date()));
        } else if (reuse == 2) {
            values.put("survey_status1", "1");
            values.put("end_date_mini", new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(new Date()));
        } else {
            values.put("survey_status2", "1");
            values.put("end_date_sf12", new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(new Date()));
        }
        return sqLiteDatabase.update(SURVEY_TABLE, values, "id" + " = ?", new String[]{String.valueOf(surveyId)});
    }


    /**
     * @param queryValues
     * @param tableName
     * @return
     */
    public long insert_VenueDetails(Map<String, String> queryValues,String tableName) {
        long insertedRecord = 0;
        try {
            SQLiteDatabase databaseWritable = this.getWritableDatabase(DATABASESECRETKEY);
            ContentValues values = new ContentValues();
            Iterator it = queryValues.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                it.remove();
                // avoids a ConcurrentModificationException
                values.put(pairs.getKey().toString(), pairs.getValue()
                        .toString());
            }
            insertedRecord = databaseWritable.insertOrThrow(tableName, null, values);
        } catch (Exception e) {
            Logger.logE(TAG, "Exception in insert_VenueDetails method", e);
        }
        return insertedRecord;
    }


    /**
     * @param query
     * @return
     */
    public JSONObject getTabInfoRecords(String query) {
        JSONObject map = new JSONObject();
        SQLiteDatabase writableDatabase = this.getWritableDatabase(DATABASESECRETKEY);
        Cursor cursor = writableDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                try {
                    map = new JSONObject();
                    map.put("Cell_Id", cursor.getString(cursor.getColumnIndex("CELL_ID")));
                    map.put("Signal_Strength", cursor.getString(cursor.getColumnIndex("SIGNAL_STRENGTH")));
                    map.put("Lac", cursor.getString(cursor.getColumnIndex("LAC")));
                    map.put("Mcc", cursor.getString(cursor.getColumnIndex("MCC")));
                    map.put("Mnc", cursor.getString(cursor.getColumnIndex("MNC")));
                    map.put("La", cursor.getString(cursor.getColumnIndex("LA")));
                    map.put("Carrier", cursor.getString(cursor.getColumnIndex("CARRIER")));
                    map.put("Networktype", cursor.getString(cursor.getColumnIndex("NETWORK_TYPE")));
                    map.put("Phoneno", cursor.getString(cursor.getColumnIndex("PHONE_NUMBER")));
                    map.put("chargeleft", cursor.getString(cursor.getColumnIndex("CHARGELEFT")));
                    map.put("charge_connected", cursor.getString(cursor.getColumnIndex("IS_CONNECTED_TO_CHARGE")));
                    map.put("last_chargetime", cursor.getString(cursor.getColumnIndex("LAST_CHARGE_TIME")));
                    map.put("Sim_Serialnumber", cursor.getString(cursor.getColumnIndex("SIM_SERIALNO")));
                    map.put("Device_Id", cursor.getString(cursor.getColumnIndex("DEVICEID")));
                } catch (JSONException e) {
                    cursor.close();
                    Logger.logE(TAG, "Exception in getTabInfoRecords method", e);

                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return map;
    }


    /**
     * @param qId
     * @param database
     * @return
     */
    public static boolean getMandatoryQuestion(String qId, android.database.sqlite.SQLiteDatabase database) {
        int skipPage;
        String selectQuery = "select mandatory from Question where id= '" + qId + "'";
        boolean skipvalues = false;
        Cursor questionCursor = database.rawQuery(selectQuery, null);
        try {
            questionCursor.moveToFirst();
            if (!questionCursor.isAfterLast()) {
                skipPage = questionCursor.getInt(questionCursor.getColumnIndex("mandatory"));
                skipvalues = skipPage != 0;
                Logger.logV(TAG, "the Mandatory is" + skipPage);
            }
        } finally {
                questionCursor.close();
        }
        return skipvalues;
    }


    /**
     * @param previous
     * @param dbHandler
     * @return
     */
    public static String getAnswerDateFromPrevious(String previous, DBHandler dbHandler) {
        String answer = null;
        try {
            String selectQuery = "SELECT ans_text FROM Response where q_id =" + previous + " and survey_id = 9";
            SQLiteDatabase db = dbHandler.getdatabaseinstance_read();
            Cursor cursor = db.rawQuery(selectQuery, null);
            do {
                if (cursor.moveToFirst() && cursor.getCount() > 0) {
                    answer = cursor.getString(cursor.getColumnIndex(PreferenceConstants.ANS_TEXT));
                    Logger.logD(TAG, "getAnswerDateFromPrevious" + answer);
                }
            } while (cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            Logger.logD(TAG, "exception in getAnswerDateFromPrevious" + e);
        }
        return answer;
    }


    /**
     * @param surveyId
     * @return
     */
    public int updateEndSurveyStatusDataToDB(int surveyId) {
        SQLiteDatabase databaseWritableEnd = this.getWritableDatabase(DATABASESECRETKEY);
        ContentValues values = new ContentValues();
        values.put(Constants.SURVEY_STATUS, "1");
        values.put(Constants.END_DATE, new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(new Date()));
        return databaseWritableEnd.update(SURVEY_TABLE, values, "id" + " = ?", new String[]{String.valueOf(surveyId)});
    }

    /**
     * @param checkboxInsert
     * @return
     */
    public Long insertResponseJSONDump(HashMap<String, String> checkboxInsert) {
        if (database == null || !database.isOpen())
            database = this.getWritableDatabase(DATABASESECRETKEY);
        ContentValues values = new ContentValues();
        values.put(Constants.SURVEY_ID, checkboxInsert.get(Constants.SURVEY_ID));
        values.put("q_id", checkboxInsert.get("q_id"));
        values.put("json_dump", checkboxInsert.get("json_dump"));
        return database.insertOrThrow("ResponseDump", null, values);


    }

    /**
     * @param q_id
     * @param surveyPrimaryKeyId
     * @param getDumpPrimaryID
     */
    public void updatePrimaryidToResponse(String q_id, int surveyPrimaryKeyId, int getDumpPrimaryID) {
        String updateStmnt = "UPDATE  Response SET response_dump_pid = " + getDumpPrimaryID + " where survey_id=" + surveyPrimaryKeyId + " and q_id= " + q_id;
        database.execSQL(updateStmnt);
    }
    /**
     * @param table
     * @param columnNmae
     * @param dbHelper
     * @return
     */
    public String getLastUpDate(String table, String columnNmae, DBHandler dbHelper) {
        String date = "";
        String selectQuery = "SELECT MAX(" + columnNmae + ") AS " + columnNmae + " FROM " + table;
        Logger.logD(TAG, "current date--" + selectQuery);
        SQLiteDatabase db = dbHelper.getdatabaseinstance_read();
        Logger.logV(TAG, "The database path name Lastupdate==>" + db.getPath());
        Cursor cursor = db.rawQuery(selectQuery, null);
        Logger.logD(TAG, "current date--" + cursor.getCount());

        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
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
     * @param dbOpenHelper
     * @return
     */
    public int getSurveyNotSync(DBHandler dbOpenHelper) {

        int beneficiarynotSyncCount = 0;
        String query = "";

        try {
            query = "Select count(survey_status) from Survey where survey_status = 1";
            SQLiteDatabase db = dbOpenHelper.getdatabaseinstance_read();
            Cursor cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    beneficiarynotSyncCount = cursor.getInt(cursor.getColumnIndex("count(survey_status)"));
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
     * method will return the QuestionAnswerSet bean.
     *
     * @param surveyPrimaryKeyId survey Table primary-key.
     * @param dbOpenHelper       convene database helper .
     * @param languageId
     * @param surveysId
     * @return Questions Answer filled bean list.
     */
    public List<PreviewQuestionAnswerSet> getAttendedQuestion(int surveyPrimaryKeyId, ConveneDatabaseHelper dbOpenHelper, int languageId, int surveysId) {
        Logger.logD(TAG, " surveyPrimaryKeyId" + surveyPrimaryKeyId);

        List<PreviewQuestionAnswerSet> questionBean = new ArrayList<>();
        String query = "";
        try {

            query = "select _id, survey_id,q_id,qtype,ans_text,ans_code from Response where survey_id=" + surveyPrimaryKeyId + " ORDER BY q_id ASC";
            SQLiteDatabase db = getdatabaseinstance_read();
            Cursor cursor = db.rawQuery(query, null);
            Logger.logD(TAG, "Query Options" + query + "-->" + cursor.getCount());

            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    String answer = "";
                    int questionID = cursor.getInt(cursor.getColumnIndex("q_id"));
                    String getQuestion = dbOpenHelper.getQuestion(questionID,languageId,surveysId);
                    Logger.logD(TAG, " attened question id" + getQuestion);
                    String questionType = cursor.getString(cursor.getColumnIndex(QTYPE));
                    if (("T").equalsIgnoreCase(questionType) || ("D").equalsIgnoreCase(questionType)) {
                        answer = cursor.getString(cursor.getColumnIndex(ANSTEXT));
                    } else if (("R").equalsIgnoreCase(questionType) || ("S").equalsIgnoreCase(questionType)) {
                        String answerCode = cursor.getString(cursor.getColumnIndex(ANSCODE));
                        answer = dbOpenHelper.getOptionText(answerCode,1, questionID);
                    }
                    PreviewQuestionAnswerSet previourQuestionSet = new PreviewQuestionAnswerSet();
                    previourQuestionSet.setQuestion(getQuestion);
                    previourQuestionSet.setAnswer(answer);
                    questionBean.add(previourQuestionSet);
                }
                while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logE(TAG, "Exception in  the getAttendedQuestion ", e);
        }
        return questionBean;
    }


    /**
     * @param id
     * @param dbOpenHelper
     * @return
     */
    public List<StatusBean> getPauseCompletedRecords(int id, ExternalDbOpenHelper dbOpenHelper) {
        List<StatusBean> syncedList = new ArrayList<>();
        try {
            String pendingSurveyQuery = "SELECT id,end_date,survey_ids FROM Survey where survey_ids=" + id + " and survey_status=1";
            SQLiteDatabase db = getdatabaseinstance_read();
            Cursor cursor = db.rawQuery(pendingSurveyQuery, null);
            Logger.logD(TAG, " getPauseCompletedRecords" + pendingSurveyQuery + "-->" + cursor.getCount());
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    String getEndDate = cursor.getString(cursor.getColumnIndex("end_date"));
                    syncedList = dbOpenHelper.getDetails(getEndDate, id);

                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logV(TAG, "getPauseCompletedRecords from Survey table" + e);
        }
        return syncedList;
    }


    /**
     *
     * @param surveyPrimaryKey
     * @param uuid
     * @return
     */
    public int checkPrymaryExist(int surveyPrimaryKey, String uuid,int clusterId) {
        int getServerPrimaryKey = 0;
        String pendingSurveyQuery="";
        try {
            if(!uuid.isEmpty()){
                pendingSurveyQuery = "SELECT id,server_primary_key FROM Survey where survey_status=2 and survey_ids=" + surveyPrimaryKey + andUuidStr + uuid + "'";
            }else{
                pendingSurveyQuery = "SELECT id,server_primary_key FROM Survey where survey_status=2 and survey_ids=" + surveyPrimaryKey + " and cluster_id=" + clusterId;
            }
            SQLiteDatabase db = getdatabaseinstance_read();
            Cursor cursor = db.rawQuery(pendingSurveyQuery, null);
            Logger.logD(TAG, " checkPrymaryExist" + pendingSurveyQuery + "-->" + cursor.getCount());
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    getServerPrimaryKey = cursor.getInt(cursor.getColumnIndex("id"));
                    Logger.logD(TAG, getServerPrimaryKeyStr + getServerPrimaryKey);

                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logV(TAG, "checkPrymaryExist from Survey table" + e);

        }
        return getServerPrimaryKey;
    }

    /**
     *
     * @param surveyPrimaryKey
     * @param deleteFlag
     */
    public void deleteExistingSurveyRecord(String surveyPrimaryKey, int deleteFlag) {
        try {
            String pendingSurveyQuery = "";
            if (deleteFlag == 1)
                pendingSurveyQuery = "DELETE FROM Response WHERE survey_id=" + surveyPrimaryKey;
            else
                pendingSurveyQuery = "DELETE FROM Survey WHERE id=" + surveyPrimaryKey;
            SQLiteDatabase db = getdatabaseinstance_read();
            db.execSQL(pendingSurveyQuery);
            Logger.logD(TAG, "from the Survey Table" + surveyPrimaryKey);

        } catch (Exception e) {
            Logger.logV(TAG, "deleteExistingSurveyRecord from Survey table" + e);
        }
    }
    /**
     * @param surveyID
     * @param periodicityFlag
     * @param beneficiaryIds
     * @param facilityIds
     * @param uuid
     * @return
     */
    public int getPauseSurvey(String surveyID, String periodicityFlag, String beneficiaryIds, String facilityIds, String uuid) {
        int getServerPrimaryKey = 0;
        try {
            String pendingSurveyQuery = "SELECT id,server_primary_key FROM Survey where survey_status=0 and survey_ids=" + surveyID + andUuidStr + uuid + "'";
            SQLiteDatabase db = getdatabaseinstance_read();
            Cursor cursor = db.rawQuery(pendingSurveyQuery, null);
            Logger.logD(pendingSurveyQueryStr, " getPauseSurvey" + pendingSurveyQuery + "-->" + cursor.getCount());
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    getServerPrimaryKey = cursor.getInt(cursor.getColumnIndex("id"));
                    Logger.logD(pendingSurveyQueryStr, getServerPrimaryKeyStr + getServerPrimaryKey);

                } while (cursor.moveToNext());

            }
            cursor.close();
        } catch (Exception e) {
            Logger.logV("", "getPauseSurvey from Survey table" + e);
        }
        return getServerPrimaryKey;
    }


    /**
     * @param surveyID
     * @param uuid
     * @return
     */
    public int getLocationBasedPauseSurvey(String surveyID, int uuid) {
        int getServerPrimaryKey = 0;
        try {
            String pendingSurveyQuery = "SELECT id,server_primary_key FROM Survey where survey_status=0 and survey_ids=" + surveyID + " and cluster_id=" + uuid;
            SQLiteDatabase db = getdatabaseinstance_read();
            Cursor cursor = db.rawQuery(pendingSurveyQuery, null);
            Logger.logD(pendingSurveyQueryStr, " getPauseSurvey" + pendingSurveyQuery + "-->" + cursor.getCount());
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    getServerPrimaryKey = cursor.getInt(cursor.getColumnIndex("id"));
                    Logger.logD(pendingSurveyQueryStr, getServerPrimaryKeyStr + getServerPrimaryKey);

                } while (cursor.moveToNext());

            }
            cursor.close();
        } catch (Exception e) {
            Logger.logV("", "getPauseSurvey from Survey table" + e);
        }
        return getServerPrimaryKey;
    }


    /**
     * @param surveyPrimaryKey
     * @param clusterid
     * @return
     */
    public int checkPrimarySaveDraftExist(int surveyPrimaryKey, int clusterid) {
        int getServerPrimaryKey = 0;
        try {
            String pendingSurveyQuery = "SELECT id,server_primary_key FROM Survey where survey_status=1 and survey_ids=" + surveyPrimaryKey + " and cluster_id= " + clusterid;
            SQLiteDatabase db = getdatabaseinstance_read();
            Cursor cursor = db.rawQuery(pendingSurveyQuery, null);
            Logger.logD(pendingSurveyQueryStr, " checkPrymarySaveDraftExist" + pendingSurveyQuery + "-->" + cursor.getCount());
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    getServerPrimaryKey = cursor.getInt(cursor.getColumnIndex("id"));
                    Logger.logD(pendingSurveyQueryStr, getServerPrimaryKeyStr + getServerPrimaryKey);

                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logV("", "checkPrymarySaveDraftExist from Survey table" + e);

        }
        return getServerPrimaryKey;
    }
    /**
     * @param surveyPrimaryKey
     * @param uuid
     * @return
     */
    public int checkPrymarySaveDraftExist(int surveyPrimaryKey, String uuid) {
        int getServerPrimaryKey = 0;
        try {
            String pendingSurveyQuery = "SELECT id,server_primary_key FROM Survey where survey_status=1 and survey_ids=" + surveyPrimaryKey + andUuidStr + uuid + "'";
            SQLiteDatabase db = getdatabaseinstance_read();
            Cursor cursor = db.rawQuery(pendingSurveyQuery, null);
            Logger.logD(pendingSurveyQueryStr, " checkPrymarySaveDraftExist" + pendingSurveyQuery + "-->" + cursor.getCount());
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    getServerPrimaryKey = cursor.getInt(cursor.getColumnIndex("id"));
                    Logger.logD(pendingSurveyQueryStr, getServerPrimaryKeyStr + getServerPrimaryKey);

                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Logger.logV("", "checkPrymarySaveDraftExist from Survey table" + e);

        }
        return getServerPrimaryKey;
    }
    /**
     *
     * @param loginActivityhandler
     */
    public void deleteAllRecord(DBHandler loginActivityhandler) {
        SQLiteDatabase db = loginActivityhandler.getdatabaseinstance_read();
        String query="DELETE FROM Survey";
        Logger.logD("TAG_LOG","deleted  query are"+query);
        db.execSQL(query);
        query="DELETE FROM Response";
        Logger.logD("TAG_LOG","deleted  query are"+query);
        db.execSQL(query);
    }

    /**
     * getAnswerFromPreviousQuestion
     * @param previous previous
     * @param dbHandler dbHandler
     * @param surveyId surveyId
     * @return
     */

    public static String getAnswerFromPreviousQuestion(String previous, DBHandler dbHandler, String surveyId) {
        String answer="";
        net.sqlcipher.database.SQLiteDatabase db;
        try {
            String selectQuery = "SELECT ans_text FROM Response where q_id =" + previous + " and survey_id=" + surveyId ;
            db = dbHandler.getdatabaseinstance_read();
            Cursor cursor = db.rawQuery(selectQuery, null);
            do {
                if (cursor.moveToFirst() && cursor.getCount()>0) {
                    answer = cursor.getString(cursor.getColumnIndex(PreferenceConstants.ANS_TEXT));
                    Logger.logD("anstext","Answer of Previous Date" + answer);
                }
            }while (cursor.moveToNext());
            cursor.close();
        }catch (Exception e){
            Logger.logD("exception","exception in date fragment" + e);
        }
        return answer;
    }

    public static String getAnswerFromQuestionID(String previous, DBHandler dbHandler, String surveyId,String qtype) {
        String answer="";
        net.sqlcipher.database.SQLiteDatabase db;
        String selectQuery="";
        try {
            if (qtype.equalsIgnoreCase("R") || qtype.equalsIgnoreCase("S"))
                selectQuery = "SELECT * FROM Response where q_id =" + previous + " and survey_id=" + surveyId ;
            else
                selectQuery = "SELECT * FROM Response where q_id =" + previous + " and survey_id=" + surveyId ;
            db = dbHandler.getdatabaseinstance_read();
            Cursor cursor = db.rawQuery(selectQuery, null);
            do {
                if (cursor.moveToFirst() && cursor.getCount()>0) {
                    if (qtype.equalsIgnoreCase("R") || qtype.equalsIgnoreCase("S"))
                        answer = cursor.getString(cursor.getColumnIndex("ans_code"));
                    else
                        answer = cursor.getString(cursor.getColumnIndex(PreferenceConstants.ANS_TEXT));
                    Logger.logD("anstext","Answer of Previous Date" + answer);
                }
            }while (cursor.moveToNext());
            cursor.close();
        }catch (Exception e){
            Logger.logD("exception","exception in date fragment" + e);
        }
        return answer;
    }
}