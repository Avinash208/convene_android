package org.yale.convene.api;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.yale.convene.database.UserDetailsDb;
import org.yale.convene.utils.Constants;
import org.yale.convene.utils.GetMD5Key;
import org.yale.convene.utils.Logger;
import org.yale.convene.utils.ProgressUtils;
import org.yale.convene.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mahiti on 23/1/17.
 */

public class LoginApi {

    private LoginApi() {
        Logger.logV(Constants.DO_NOTHING,Constants.DO_NOTHING);
    }

    public static void callLoginApi(String url, final String userName, final String password, final Activity activity, final ProgressDialog progressDialog, final SharedPreferences preferences, final SignInDetails details) {
        Logger.logD(LoginApi.class.getName(), "login url--" + url + "-" + userName + "-" + password);
       progressDialog.setMessage("verifying...");
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        ProgressUtils.CancelProgress(progressDialog);
                        try {
                            Logger.logV("the languageBlockResult is", "the languageBlockResult is" + response);
                            JSONObject e = new JSONObject(response);
                            if(e.getInt("responseType") == 2) {
                                LoginApi.fillUserDb(activity, e.getInt("uId"), userName, userName.trim().concat(password.trim().trim()), response);
                                SetUpdateTimeToSharedPreference(e.getInt("updates"),preferences);
                                details.signingDetails(preferences.getInt("selectedLanguage", 0), e.getInt("uId"),userName,e.getInt("partner_id"),response);

                            } else {
                                ToastUtils.displayToastUi(e.getString("message"), activity);
                            }
                        } catch (JSONException var3) {
                            Logger.logE("Login API", "Login API==", var3);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ProgressUtils.CancelProgress(progressDialog);
                        ToastUtils.displayToastUi("slow internet connection",activity);
                        error.printStackTrace();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userId", userName);
                params.put("password", password);
                return params;
            }
        };
        Volley.newRequestQueue(activity).add(postRequest);
    }


    private static void SetUpdateTimeToSharedPreference(int updates, SharedPreferences preferences) {
        SharedPreferences.Editor loginEditor= preferences.edit();
        loginEditor.putInt("TableUpdateTime",updates);
        loginEditor.apply();
    }

    public static void fillUserDb(Context context, int userId, String userName, String md5, String loginData) {
        UserDetailsDb userDetailsDb = new UserDetailsDb(context);
        Logger.logD(LoginApi.class.getName(), "filling data--" + md5 + "--" + new GetMD5Key(context).md5(md5));
        userDetailsDb.insertUserDetails(userId, new GetMD5Key(context).md5(md5), userName, loginData);
    }
}
