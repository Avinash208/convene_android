package org.fwwb.convene.convenecode.network;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.fwwb.convene.convenecode.database.ExternalDbOpenHelper;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.Logger;

import java.util.HashMap;
import java.util.Map;


public class SchoolTypeApi {

    private static String modifiedOnStr = "modified_on";

    private SchoolTypeApi() {
        Logger.logV(Constants.DO_NOTHING,Constants.DO_NOTHING);
    }

    public static void callSchoolTypeApi(final Context context, String url, final SchoolTypeDetails typeDetails, final ExternalDbOpenHelper dbhelper) {
        Logger.logD(SchoolTypeApi.class.getName(), "login url--" + url + "-" + url);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.v("the result is", "the result is" + response);
                            SQLiteDatabase database=dbhelper.getWritableDatabase();
                            insertIntoSchoolTypeDB(response,database);
                            typeDetails.onSuccess();
                        } catch (Exception e) {
                            Logger.logE("SchoolType API", "SchoolType API==", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        typeDetails.onFailure();
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(modifiedOnStr, /*dbhelper.getLastUpDate("SchoolType",modifiedOnStr)*/"2017-05-18 06:26:40.229090");
                return params;
            }
        };
        Volley.newRequestQueue(context).add(postRequest);
    }

    private static void insertIntoSchoolTypeDB(String response, SQLiteDatabase database) {
        try{
            ContentValues cv=new ContentValues();
            JSONArray jsonArray=new JSONArray(response);
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonobject = jsonArray.getJSONObject(i);
                String modifiedOn = jsonobject.getString(modifiedOnStr);
                String active = jsonobject.getString("active");
                int id=jsonobject.getInt("id");
                String name=jsonobject.getString("name");
                cv.put("id",id);
                cv.put("name",name);
                cv.put("active",active);
                cv.put(modifiedOnStr,modifiedOn);
                database.insertWithOnConflict("SchoolType", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            }
            database.close();
        }catch(Exception e){
            Logger.logE("Exception","Exception in SchoolType table", e);
        }
    }



}


