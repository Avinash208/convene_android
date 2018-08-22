package org.yale.convene.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import org.yale.convene.R;
import org.yale.convene.utils.Constants;
import org.yale.convene.utils.Logger;

/**
 * Created by mahiti on 31/5/17.
 */

public class Utilities
{
    ExternalDbOpenHelper dbhelper;
    SharedPreferences preferences;

    public static boolean isSdPresent() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }


    /**
     *
     * @param context
     * @param id
     * @param table
     * @return
     */
    public  String getName(Context context,int id,String table)
    {
        String name="";
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        dbhelper = ExternalDbOpenHelper.getInstance(context, preferences.getString(Constants.DBNAME,""),preferences.getString("inv_id", ""));
         SQLiteDatabase database= dbhelper.getReadableDatabase();
        String selectQuery = "SELECT name from  "+table+" where "+table+"_id= " + id;
        Logger.logD("current date","current date--"+ selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor!=null && cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex("name"));
            cursor.close();
        }

        database.close();
        Logger.logD("getLastUpDate", "getLastUpDate--" + name);
        return name;
    }

    public static void setSurveyStatus(SharedPreferences sharedPreferences, String aNew) {
        SharedPreferences.Editor editList= sharedPreferences.edit();
        editList.putString(Constants.SURVEYSTATUSTYPR,aNew);
        editList.apply();

    }

    public static void setLocationSurveyFlag(SharedPreferences sharedPreferences, String locationFlag) {
        SharedPreferences.Editor editList= sharedPreferences.edit();
        editList.putString(Constants.LocationSurveyflag,locationFlag);
        editList.apply();
    }

    public static void setGender(ImageView imgUser, String gender, final Context context) {
        imgUser.setTag(gender);
        if ("Male".equalsIgnoreCase(gender))
        {
            imgUser.setImageDrawable(context.getDrawable(R.drawable.profile_male));

        }
        else if ("Female".equalsIgnoreCase(gender))
        {
            imgUser.setImageDrawable(context.getDrawable(R.drawable.profile_female));
        }else
        {
            imgUser.setImageDrawable(context.getDrawable(R.drawable.profile_none));
        }
    }


}


