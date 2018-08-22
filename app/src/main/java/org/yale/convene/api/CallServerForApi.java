package org.yale.convene.api;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.yale.convene.R;
import org.yale.convene.utils.Logger;

import java.util.Map;


/**
 * Created by mahiti on 23/1/17.
 */

public class CallServerForApi {


    public static void callServerApi(final Activity activity, final PushingResultsInterface pushingBirthdayResultsInterface, final Map<String, String> params, final ProgressDialog progressDialog,final int code) {

        String URL = activity.getResources().getString(R.string.main_Url) + params.get("URL");
        Log.v("the result is", "the result is" + URL);
        StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // ProgressUtils.CancelProgress(progressDialog);
                        Log.v("the result is", "the result is" + response);
                        if (pushingBirthdayResultsInterface != null)
                            pushingBirthdayResultsInterface.setResults(response, code);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Logger.logD("Tag", "Params" + params.toString());
                return params;
            }


           /* @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> paramsHeader = new HashMap<>();
                paramsHeader.put("un", "WYPO");
                paramsHeader.put("pw", "VD0+)&lrYlUiUcl^8%a~");
                return paramsHeader;
            }*/

        };
        try {
            postRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(activity).add(postRequest);
        } catch (Exception e) {
            Logger.logE("exception", "call server api ", e);


        }

    }
}
