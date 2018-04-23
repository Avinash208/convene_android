package org.mahiti.convenemis.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mahiti on 30/1/17.
 */

public class UserDetailsDb extends SQLiteOpenHelper {

    private static final String TAG = "UserDetailsDb";
    private static final String DB_NAME = "ConveneLogin.db";
    private static final int DB_VERSION = 2;
    private SQLiteDatabase db;
    private static final String WHERE = " WHERE ";
    private static final String FROM = " FROM ";
    private static final String SELECT = "SELECT ";


    public UserDetailsDb(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        String createQueryUserTable = "CREATE TABLE IF NOT EXISTS " + Constants.CONVENE_USER_TABLE + "(" +
                Constants.CONVENE_USER_ID + " INTEGER PRIMARY KEY NOT NULL ," +
                Constants.CONVENE_USER_NAME + " TEXT ," + Constants.CONVENE_MD5 + " TEXT ," +
                Constants.CONVENE_LOGIN_DATA + " TEXT" + ")";
        Logger.logD(TAG, "create table==" + createQueryUserTable);
        db.execSQL(createQueryUserTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
         * nothing to do
         */
    }


    /**
     *
     * @param userId
     * @param md5
     * @param userName
     * @param loginData
     */

    public void insertUserDetails(int userId, String md5, String userName, String loginData) {
        db = this.getWritableDatabase();
        if (db != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Constants.CONVENE_USER_ID, userId);
            contentValues.put(Constants.CONVENE_USER_NAME, userName);
            contentValues.put(Constants.CONVENE_MD5, md5);
            contentValues.put(Constants.CONVENE_LOGIN_DATA, loginData);
            db.insertWithOnConflict(Constants.CONVENE_USER_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            Logger.logD(TAG, "insert user--" + contentValues);
        }
    }


    /**
     *
     * @param md5
     * @return
     */
    public List<HashMap<String, String>> getUserDetails(String md5) {
        String getDetails = "SELECT * FROM " + Constants.CONVENE_USER_TABLE + WHERE + Constants.CONVENE_MD5 + "='" + md5 + "'";
        List<HashMap<String, String>> userDetailsList = new ArrayList<>();
        db = this.getWritableDatabase();
        Logger.logD(TAG, "getUserDetails==" + getDetails);
        Cursor cursor = db.rawQuery(getDetails, null);
        if (cursor.moveToFirst()) {
            HashMap<String, String> map = new HashMap<>();
            do {
                map.put(Constants.CONVENE_USER_ID, String.valueOf(cursor.getInt(cursor.getColumnIndex(Constants.CONVENE_USER_ID))));
                map.put(Constants.CONVENE_USER_NAME, cursor.getString(cursor.getColumnIndex(Constants.CONVENE_USER_NAME)));
                map.put(Constants.CONVENE_MD5, cursor.getString(cursor.getColumnIndex(Constants.CONVENE_MD5)));
                map.put(Constants.CONVENE_LOGIN_DATA, cursor.getString(cursor.getColumnIndex(Constants.CONVENE_LOGIN_DATA)));
                userDetailsList.add(map);
            } while (cursor.moveToNext());
            cursor.close();
        }
        Logger.logD(TAG, "getUserDetails---" + userDetailsList.size());
        return userDetailsList;
    }


    /**
     *
     * @param tableName
     * @param md5
     * @return
     */
    public String getUserId(String tableName, String md5) {
        String countQuery = SELECT + Constants.CONVENE_USER_ID + FROM + tableName + WHERE + Constants.CONVENE_MD5 + "='" + md5 + "'";
        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        String uid = null;
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                uid = cursor.getString(cursor.getColumnIndex(Constants.CONVENE_USER_ID));
            } while (cursor.moveToNext());
            cursor.close();

        }
        cursor.close();
        return uid;
    }


    /**
     *
     * @param tableName
     * @param md5
     * @return
     */
    public String getLoginData(String tableName, String md5) {
        String countQuery = SELECT + Constants.CONVENE_LOGIN_DATA + FROM + tableName + WHERE + Constants.CONVENE_MD5 + "='" + md5 + "'";
        db = this.getWritableDatabase();
        Logger.logD("get id","get id=="+countQuery);
        Cursor cursor = db.rawQuery(countQuery, null);
        String loginData = null;
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                loginData = cursor.getString(cursor.getColumnIndex(Constants.CONVENE_LOGIN_DATA));
            } while (cursor.moveToNext());
            cursor.close();

        }
        cursor.close();

        return loginData;
    }

    /**
     *
     * @param tableName
     * @param md5
     * @return
     */
    public String getUserName(String tableName, String md5) {
        String countQuery = SELECT + Constants.CONVENE_USER_NAME + FROM + tableName + WHERE + Constants.CONVENE_MD5 + "='" + md5 + "'";
        db = this.getWritableDatabase();
        Logger.logD("get id","get id=="+countQuery);
        Cursor cursor = db.rawQuery(countQuery, null);
        String uName = null;
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                uName = cursor.getString(cursor.getColumnIndex(Constants.CONVENE_USER_NAME));
            } while (cursor.moveToNext());
            cursor.close();

        }
        cursor.close();

        return uName;
    }

    /**
     *
     * @param userID
     * @return
     */
    public void deleteLoginRecord(String userID) {
        try{
            db = this.getWritableDatabase();
            Logger.logD(TAG, "deleting user detail of userID" + userID);
            String getDetails = "Delete from "+ Constants.CONVENE_USER_TABLE  +" where "+ Constants.CONVENE_USER_ID +"="+userID;
            Logger.logD(TAG, "deleting Query---" + getDetails);
            db.execSQL(getDetails);
        }catch (Exception e){
            Logger.logD("Exception","Exception in the deleting the userID"+e);
        }

    }
}
