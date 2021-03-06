package org.fwwb.convene.convenecode.api;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;

import org.json.JSONObject;
import org.fwwb.convene.convenecode.utils.CheckNetwork;
import org.fwwb.convene.convenecode.utils.ClusterInfo;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.FileUtils;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.convenecode.utils.ProgressDialog;
import org.fwwb.convene.convenecode.utils.ToastUtils;

import java.io.File;
import java.util.List;

/**
 * Created by mahiti on 28/3/16.
 */
public class UploadFileApi {
    private UploadFileApi() {
        Logger.logV(Constants.DO_NOTHING,Constants.DO_NOTHING);
    }

    public static class UploadFiles extends AsyncTask<Context, String, String> {
        Activity mContext;
        String fromValue;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialog.show(mContext, false);
        }

        public UploadFiles(Activity context, String fromValues) {
            Logger.logV("the data is", "Entering into executing async task");

            mContext = context;
            fromValue = fromValues;
        }

        @Override
        protected String doInBackground(Context... arg0) {
            Logger.logV("the databackground", "the background");
            String data = UploadFile.sendDataToServerGetResponse(mContext, fromValue);
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ProgressDialog.cancelDialog();
            try {
                JSONObject result = new JSONObject(s);
                ToastUtils.displayToast(result.getString("message"), mContext);
            } catch (Exception e) {
                Logger.logE("MultiPartRestClient", "sendDataToServerGetResponse", e);
            }
        }


    }

    public static void missingData(final Activity activity) {
        CheckNetwork checkNetwork = new CheckNetwork(activity);
        final SharedPreferences syncSurveyPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        try {
            String inputPath = Environment.getExternalStorageDirectory() + "/DataBase_Backup";
            File sdCardRoot = new File(inputPath);
            if (!sdCardRoot.exists() || sdCardRoot.length() == 0) {
                ToastUtils.displayToast("No data available", activity);
                return;
            }

                List<String> list = FileUtils.getFilesList(inputPath);
                if (list == null) {
                    ToastUtils.displayToast("No data available", activity);
                    return;
                }
            if (checkNetwork.checkNetwork())
                activity.runOnUiThread(() -> new UploadFiles(activity, syncSurveyPreferences.getString("uId", "")).execute());
            else
                ToastUtils.displayToast(syncSurveyPreferences.getString(ClusterInfo.connectionProb, ""), activity);
        } catch (Exception e) {
            Logger.logE("File upload", "File upload==", e);
        }
    }
}
