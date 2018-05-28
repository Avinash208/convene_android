package org.mahiti.convenemis.utils;

import android.content.Context;

import org.mahiti.convenemis.R;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by mahiti on 12/1/16.
 */
public class MultiPartRestClient {
    public static final String TAG = "MultiPartRestClient";
    private final static OkHttpClient client = new OkHttpClient();

    /**
     *
     * @param context to get the url
     * @param requestBody - post parameters which will be sent to server
     * @return returning result
     */
    public static String run(Context context, MultipartBody.Builder requestBody) {
        try {
            Logger.logD(TAG, "response URL"+ context.getString(R.string.answer_upload_url));
            Request request = new Request.Builder()
                    .addHeader("Accept", "application/json")
                    .url(context.getString(R.string.answer_upload_url))
                //    .url("http://192.168.2.109:8000/api/add-survey-answers/")


                    .post(requestBody.build())
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful())
                {
                    return response.message();
                }
                else{
                    return response.body().string();
                }
            } catch (Exception e) {
                Logger.logE(TAG, "response new call exception", e);
            }
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
        return "";
    }
}
