package org.assistindia.convene.network;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.assistindia.convene.utils.CheckNetwork;
import org.assistindia.convene.utils.Logger;

import java.util.List;

/**
 * Created by mahiti on 30/5/17.
 */

public class SaveDataToServer extends AsyncTask<Context, Integer, String> {
    /*
     * Declaring all the variables and views
     */
    private DirectUrlCall resturl;
    CheckNetwork chNetwork;
    Activity activity;
    String result = "";
    android.app.ProgressDialog loginDialog;
    String apiName;
    List<NameValuePair> listOfParams;
    SaveDataInterface dataToServer;
    int token;
    /*
     * Calling categories task constructor
     */
    public SaveDataToServer(Context context, Activity activity, String nameOfApi, List<NameValuePair> paramsData, SaveDataInterface saveData) {
        resturl = new DirectUrlCall(context);
        chNetwork = new CheckNetwork(context);
        this.activity = activity;
        dataToServer=saveData;
        apiName=nameOfApi;
        listOfParams=paramsData;
    }

    @Override
    protected String doInBackground(Context... params) {
        if (!chNetwork.checkNetwork()) {
            return "";

        }
        try {
            result = resturl.restUrlServerCall(activity,apiName, "post", listOfParams, "");
            Logger.logV("the parameters are", "the params" + listOfParams);
            parseResponse(result);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String categoryList) {
        super.onPostExecute(categoryList);
        dataToServer.saveData(true);


    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    public void parseResponse(String result)
    {

        Logger.logV("the Service Response","the response is-->"+result);
    }

}

