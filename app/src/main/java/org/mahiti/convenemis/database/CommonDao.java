package org.mahiti.convenemis.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.mahiti.convenemis.utils.CommonForAllClasses;

public class CommonDao extends SQLiteOpenHelper {


    /**
     *
     * @param applicationContext
     */
    public CommonDao(Context applicationContext) {
        super(applicationContext, "Convene.sqlite", null, CommonForAllClasses.version);
    }


    /**
     *
     * @param database
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        /* Nothing to do in this method*/
    }


    /**
     *
     * @param database
     * @param versionOld
     * @param currentVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int versionOld, int currentVersion) {
        /* Nothing to do in this method*/
    }

}