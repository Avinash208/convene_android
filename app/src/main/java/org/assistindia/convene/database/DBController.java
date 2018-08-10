package org.assistindia.convene.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.assistindia.convene.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class DBController extends SQLiteOpenHelper {
    private static final int dbVersion = 3;
    private String cellIdStr = "CellId";
    private String sStrengthStr = "sStrength";
    private String chargeStr = "Charge";
    private String lastChargeTimeStr = "LastChargeTime";
    private String simSNoStr = "SimSNo";
    private String imeiStr = "IMEI";
    private String freeSpaceStr = "FreeSpace";
    private String timeStampStr = "TimeStamp";
    private String latitudedStr = "latitude";
    private String longitudeStr = "longitude";
    private String userIdStr = "userId";
    private String appsStr = "apps";


    /**
     *
     * @param applicationContext
     */
    public DBController(Context applicationContext) {
        super(applicationContext, "IBBS_SurveyAnswerDataBase.db", null, dbVersion);
    }


    /**
     *
     * @param database
     */
    @Override
    public void onCreate(SQLiteDatabase database) {    //captured
        //query for creating the table with list of columns
        String query;
        query = "CREATE TABLE USERDETAILS("
                + "ID INTEGER PRIMARY KEY,"
                + "INV_ID TEXT NOT NULL,"
                + "USERNAME TEXT NOT NULL DEFAULT '',"
                + "ENCRYPTEDPASSWORD TEXT NOT NULL DEFAULT '',"
                + "LOGINSTRING TEXT NOT NULL DEFAULT '',"
                + "DOMAINDETAILS TEXT NOT NULL DEFAULT '')";
        database.execSQL(query);
        String tabDetails = "CREATE TABLE IF NOT EXISTS DeviceDetails ("
                + "id INTEGER PRIMARY KEY," + "CellId TEXT,"
                + "sStrength TEXT," + "Charge TEXT," + "LastChargeTime TEXT,"
                + "SimSNo TEXT," + "IMEI TEXT," + "FreeSpace TEXT," + "apps TEXT,"
                + "TimeStamp TEXT," + "latitude TEXT,"
                + "longitude TEXT," +"userId TEXT)";
        database.execSQL(tabDetails);
    }


    /**
     *
     * @param database
     * @param versionOld
     * @param currentVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int versionOld, int currentVersion) {
        String tabDetails = "CREATE TABLE IF NOT EXISTS DeviceDetails ("
                + "id INTEGER PRIMARY KEY," + "CellId TEXT,"
                + "sStrength TEXT," + "Charge TEXT," + "LastChargeTime TEXT,"
                + "SimSNo TEXT," + "IMEI TEXT," + "FreeSpace TEXT," + "apps TEXT,"
                + "TimeStamp TEXT," + "latitude TEXT,"
                + "longitude TEXT," +"userId TEXT)";
        database.execSQL(tabDetails);
        onCreate(database);
    }


    @Override
    public synchronized void close() {
        SQLiteDatabase database = this.getWritableDatabase();
        if (database != null) {
            database.close();
            super.close();
        }
    }

    /**
     * @param queryValues
     * @param tableName
     * @return
     */
    public long insertUserDetails(Map<String, String> queryValues, String tableName) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Iterator it = queryValues.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            it.remove();
            // avoids a ConcurrentModificationException
            values.put(pairs.getKey().toString(), pairs.getValue().toString());
        }
        long insertedRecord = database.insertOrThrow(tableName, null, values);
        database.close();
        return insertedRecord;
    }

    /**
     *
     * @param cellId
     * @param sigStrength
     * @param charge
     * @param lastCharage
     * @param simsno
     * @param imei
     * @param freespace
     * @param apps
     * @param timeStamp
     * @param latitude
     * @param longitude
     * @param uid
     */
    public void insertdeviceDetailsDB(String cellId, String sigStrength, String charge, String lastCharage, String simsno, String imei, String freespace, String apps, String timeStamp, String latitude, String longitude, String uid)
    {
        HashMap<String, String> dbvalues = new HashMap<>();
        dbvalues.put(cellIdStr, cellId);
        dbvalues.put(sStrengthStr, sigStrength);
        dbvalues.put(chargeStr, charge);
        dbvalues.put(lastChargeTimeStr, lastCharage);
        dbvalues.put(simSNoStr,simsno);
        dbvalues.put(imeiStr, imei);
        dbvalues.put(freeSpaceStr, freespace);
        dbvalues.put(appsStr, apps);
        dbvalues.put(timeStampStr, timeStamp);
        dbvalues.put(latitudedStr, latitude);
        dbvalues.put(longitudeStr, longitude);
        dbvalues.put(userIdStr, uid);
        insertDeviceDetails(dbvalues, "DeviceDetails");

    }
    //delete table values
    public void deleteTableDetails()
    {
        String query;
        SQLiteDatabase database = this.getWritableDatabase();
        query="DELETE FROM DeviceDetails";
        database.execSQL(query);
    }


//Insert DeviceDetails


    /**
     *
     * @param queryValues
     * @param tableName
     * @return
     */
    public long insertDeviceDetails(Map<String, String> queryValues, String tableName) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Iterator it = queryValues.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            it.remove();
            // avoids a ConcurrentModificationException
            values.put(pairs.getKey().toString(), pairs.getValue().toString());
        }
        long insertedRecord = database.insertOrThrow(tableName, null, values);
        database.close();
        return insertedRecord;
    }
    /**
     * @param Query
     * @return
     */
    public List<HashMap<String, String>> GetUserDetails(String Query)
    {
        List<HashMap<String, String>> wordList;
        wordList = new ArrayList<>();
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            HashMap<String, String> map = new HashMap<>();
            do {
                map.put("INV_ID", cursor.getString(cursor.getColumnIndex("INV_ID")));
                map.put("USERNAME", cursor.getString(cursor.getColumnIndex("USERNAME")));
                map.put("ENCRYPTEDPASSWORD", cursor.getString(cursor.getColumnIndex("ENCRYPTEDPASSWORD")));
                map.put("LOGINSTRING", cursor.getString(cursor.getColumnIndex("LOGINSTRING")));
                map.put("DOMAINDETAILS", cursor.getString(cursor.getColumnIndex("DOMAINDETAILS")));
                wordList.add(map);
            } while (cursor.moveToNext());
            cursor.close();
        }
        database.close();
        // return contact list
        return wordList;
    }
    public JSONArray getDeviceDetails()
    {
        String query="Select * From DeviceDetails";
        JSONObject jsonObj;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        JSONArray array = new JSONArray();
        if (cursor.moveToFirst()) {
            do {
                try {
                    jsonObj = new JSONObject();
                    jsonObj.put(cellIdStr, cursor.getString(cursor.getColumnIndex(cellIdStr)));
                    jsonObj.put(sStrengthStr, cursor.getString(cursor.getColumnIndex(sStrengthStr)));
                    jsonObj.put(chargeStr, cursor.getString(cursor.getColumnIndex(chargeStr)));
                    jsonObj.put(lastChargeTimeStr, cursor.getString(cursor.getColumnIndex(lastChargeTimeStr)));
                    jsonObj.put(simSNoStr, cursor.getString(cursor.getColumnIndex(simSNoStr)));
                    jsonObj.put(imeiStr, cursor.getString(cursor.getColumnIndex(imeiStr)));
                    jsonObj.put(freeSpaceStr, cursor.getString(cursor.getColumnIndex(freeSpaceStr)));
                    jsonObj.put(appsStr, cursor.getString(cursor.getColumnIndex(appsStr)));
                    jsonObj.put(timeStampStr, cursor.getString(cursor.getColumnIndex(timeStampStr)));
                    jsonObj.put(latitudedStr, cursor.getString(cursor.getColumnIndex(latitudedStr)));
                    jsonObj.put(longitudeStr, cursor.getString(cursor.getColumnIndex(longitudeStr)));
                    jsonObj.put(userIdStr, cursor.getString(cursor.getColumnIndex(userIdStr)));
                    array.put(jsonObj);
                }
                catch (JSONException e)
                {
                    Logger.logE(DBHandler.class.getSimpleName(), "Exception in getTabInfoRecords method", e);

                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        if(database.isOpen())
            database.close();
        return array;
    }


    /**
     * @param Query
     * @return
     */
    public int getCount(String Query) {
        SQLiteDatabase database = this.getWritableDatabase();
        int number = 0;
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {

                if (cursor.getCount() != 0)
                    number = cursor.getCount();
            } while (cursor.moveToNext());
            cursor.close();
        }
        database.close();
        // return contact list
        return number;
    }

    /**
     *
     * @param queryValues
     * @param userId
     * @param tableName
     * @param fieldChecking
     * @return
     */
    public int updateUserDetails(HashMap<String, String> queryValues, String userId, String tableName, String fieldChecking) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Iterator it = queryValues.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            it.remove();
            // avoids a ConcurrentModificationException
            values.put(pairs.getKey().toString(), pairs.getValue().toString());
        }
        return database.update(tableName, values, fieldChecking + " = ?",new String[]{String.valueOf(userId)});
    }
}
