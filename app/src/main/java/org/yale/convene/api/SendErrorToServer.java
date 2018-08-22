package org.yale.convene.api;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.yale.convene.utils.ClusterInfo;
import org.yale.convene.utils.Logger;
import org.yale.convene.utils.RestUrl;
import org.yale.convene.utils.ToastUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;


public class SendErrorToServer extends Activity {
    SharedPreferences preferences;
    Context aContext;
    NetworkInfo conMan;
    private RestUrl resturl;
    private ProgressDialog progDialog;

    public SendErrorToServer(Context context) {
        this.aContext = context;
        resturl = new RestUrl(aContext);
        preferences = PreferenceManager.getDefaultSharedPreferences(aContext);
        conMan = ((ConnectivityManager) aContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        progDialog = new ProgressDialog(aContext);
        progDialog.setMessage(preferences.getString(ClusterInfo.SendDebugData, ""));
        progDialog.setCancelable(false);
    }

    public void SendErrorLog(String Logtext) {
        new uploaderror().execute(Logtext);
    }


    public class uploaderror extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SendErrorToServer.this.runOnUiThread(() -> {
                try {
                    if (progDialog != null) {
                        progDialog.show();
                    }
                } catch (Exception e) {
                    Logger.logE(SendErrorToServer.class.getSimpleName(), "Exception in uploaderror class onPostExecute method first catch", e);
                }
            });
        }

        protected String doInBackground(String... params) {
            List<NameValuePair> postparameter = new ArrayList<>();
            resturl = new RestUrl(aContext);
            postparameter.add(new BasicNameValuePair("uId", preferences.getString("uId", "")));
            postparameter.add(new BasicNameValuePair("ErrorLog", params[0]));
            String Status = resturl.checkAndReturnResult();
            if ("error 0".equals(Status)) {
                return resturl.restUrlServerCall(aContext, "logsentbyuser/", "post", postparameter, preferences.getString("training", ""));
            }
            return Status;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            SendErrorToServer.this.runOnUiThread(() -> {
                try {
                    if (progDialog != null) {
                        progDialog.cancel();
                    }
                    JSONObject resultObj = new JSONObject(result);
                    ToastUtils.displayToast(resultObj.getString("message"), aContext);
                } catch (Exception e) {
                    Logger.logE(SendErrorToServer.class.getSimpleName(), "Exception in onPostExecute method first catch", e);
                }
            });
        }
    }

    public static String SendErrorLog() {
        StringBuilder build = new StringBuilder();
        String finalLines = "";
        File file = new File(Environment.getExternalStorageDirectory(), "Survey_Error.txt");
        if (!file.exists())
            return finalLines;
        try(RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            StringBuilder builder = new StringBuilder();
            builder.setLength(0);
            long length = file.length();
            build.setLength(0);
            long len;
            len = length - (132 * 100);
            if (len < 0)
                len = 0;
            for (long seek = len; seek <= length; seek++) {
                randomAccessFile.seek(seek);
                char c = (char) randomAccessFile.read();
                builder.append(c);
                if (c == '\n') {
                    build.append(builder.toString());
                    builder.setLength(0);
                }
            }
            finalLines = build.toString();
            randomAccessFile.close();
        } catch (FileNotFoundException e) {
            Logger.logE("", "Exception in SyncSurveyActivity  SendErrorLog method first catch", e);

        } catch (IOException e) {
            Logger.logE("", "Exception in SyncSurveyActivity  SendErrorLog method second catch", e);

        } catch (Exception e) {
            Logger.logE("", "Exception in SyncSurveyActivity  SendErrorLog method third catch", e);

        }
        return finalLines;
    }
}