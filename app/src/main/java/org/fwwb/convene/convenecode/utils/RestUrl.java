package org.fwwb.convene.convenecode.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RestUrl {
    String TAG = "RestUrl";

    private boolean haveConnectedWifi = false;
    private boolean haveConnectedMobile = false;
    private boolean haveConnectedActive = false;
    private int code;
    private String status;
    private SharedPreferences sharedPreferences;
    private Context mContext;

    public RestUrl(Context mContext) {
        this.mContext = mContext;

    }

    public String checkAndReturnResult() {
        Boolean networkFlag = checkNetwork();
        if(code==0 || code==1){
            if (networkFlag)// Wifi or Mobile Data
                status = "error 0";
            else
                status = "error 1".concat(",").concat("Please Check the Network Connection");
        }
        return status;
    }



    public void writeToTextFile(String errorMessage, String postParameters, String section) {
        UploadError errorObj = new UploadError();
        errorObj.passingData(errorMessage, postParameters, section);
        errorObj.execute();
    }

    private class UploadError extends AsyncTask<String, Integer, String> {
        String section = "";
        String errorMessage = "";
        String postParameters = "";

        void passingData(String errorMessage, String postParameters, String section) {
            this.section = section;
            this.errorMessage = errorMessage;
            this.postParameters = postParameters;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject=new JSONObject(s);
                boolean statusFlag=jsonObject.getBoolean("status");
                if(statusFlag){
                    getErrorFile();
                }
            }catch (Exception e){
                Logger.logE("","",e);
            }

        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> postparameter = new ArrayList<>();
            String status = "";
            try {
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                NetworkInfo conMan = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
                InetAddress ownIP = null;
                try {
                    ownIP = InetAddress.getLocalHost();
                } catch (Exception e) {
                    Logger.logE(RestUrl.class.getSimpleName(), "Exception in uploaderror class", e);
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Section : ")
                        .append(section)
                        .append(",")
                        .append("Date : ")
                        .append(new Date().toLocaleString())
                        .append(",")
                        .append("Uid : ")
                        .append(sharedPreferences.getString("inv_id", ""))
                        .append(",")
                        .append("Application : ")
                        .append("CRY")
                        .append(",")
                        .append("NetWorkType & NetwortStatus : ")
                        .append(conMan.getTypeName().concat(": ").concat(conMan.getState().toString())).append(",")
                        .append("Ip Address : ").append(ownIP).append(",")
                        .append(android.os.Build.MANUFACTURER).append(",")
                        .append(android.os.Build.MODEL).append(",")
                        .append(android.os.Build.VERSION.SDK_INT).append(",")
                        .append("Error Message : ").append(errorMessage).append(",")
                        .append("postparameters : ").append(postParameters)
                        .append("\n");
                writeToFile(sb.toString());
                postparameter.add(new BasicNameValuePair("uId", sharedPreferences.getString("UID", "")));
                postparameter.add(new BasicNameValuePair("ErrorLog", sb.toString()));
                postparameter.add(new BasicNameValuePair("sToken", sharedPreferences.getString("UID", "")));
                status = checkAndReturnResult();
                if ("error 0".equals(status)) {
                    return restUrlServerCall(mContext, "feederrorlog/", "post", postparameter, sharedPreferences.getString("training", ""));
                }
            } catch (Exception e) {
                Logger.logE(RestUrl.class.getSimpleName(), "Exception in uploaderror class outside loop", e);
            }
            return status;
        }
    }

    private void getErrorFile() {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        File sdCard = new File(Environment.getExternalStorageDirectory(), "/Error/");
        File dir = new File(sdCard.getAbsolutePath());
        if(dir.exists()){
            try {
                for (File f : sdCard.listFiles()) {
                    if (f.isFile())
                    {
                        if(f.getName().contains(date)){
                            File fromFile = new File(dir, "1_" + date +".txt");
                            File toFile=new File(dir,"2_" + date + ".txt");
                            if(fromFile.exists()){
                                fromFile.renameTo(fromFile);
                            }else{
                                fromFile.renameTo(toFile);
                            }
                        }else{

                        }

                    }
                }
            } catch (Exception e) {
                Logger.logE(RestUrl.class.getSimpleName(), "Exception in writeToFile method", e);

            }
        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void writeToFile(String message) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        File sdCard = new File(Environment.getExternalStorageDirectory() + "/Error/");
        File dir = new File(sdCard.getAbsolutePath());


        try {
            if(!dir.exists()) {
                dir.mkdirs();
            }else{
                dir.delete();
            }
            FileOutputStream fileOutputStream;
            File file = new File(dir, "1_" + date +".txt");
            fileOutputStream = new FileOutputStream(file, true);
            try (PrintStream p = new PrintStream(fileOutputStream)) {
                p.print(message);
                p.close();
                fileOutputStream.close();
            } catch (Exception ex) {
                Logger.logE(RestUrl.class.getSimpleName(), "Exception in writeToFile method print stream", ex);

            }
        } catch (Exception e) {
            Logger.logE(RestUrl.class.getSimpleName(), "Exception in writeToFile method", e);

        }

    }

    public final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            Logger.logE(RestUrl.class.getSimpleName(), "Exception in md5 method ", e);
        }
        return "";
    }

    public String restUrlServerCall(Context activity, String url, String methodname, List<NameValuePair> params, String mode_status) {


        Logger.logD(RestUrl.class.getSimpleName(), "post Parameters : " + params);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        HttpClient httpclient = null;
        try {
            MySSLSocketFactory SocketFactory = new MySSLSocketFactory(null);
            int connectionTimeOut = 0;
            int timeoutSocket = 0;
            httpclient = SocketFactory.getNewHttpClient(connectionTimeOut, timeoutSocket);

        } catch (KeyManagementException e2) {
            Logger.logE(RestUrl.class.getSimpleName(), "KeyManagementException in restUrlServerCall ", e2);
        } catch (UnrecoverableKeyException e2) {
            Logger.logE(RestUrl.class.getSimpleName(), "UnrecoverableKeyException in restUrlServerCall ", e2);
        } catch (NoSuchAlgorithmException e2) {
            Logger.logE(RestUrl.class.getSimpleName(), "NoSuchAlgorithmException in restUrlServerCall ", e2);
        } catch (KeyStoreException e2) {
            Logger.logE(RestUrl.class.getSimpleName(), "KeyStoreException in restUrlServerCall ", e2);
        }
        //rootNormal = mContext.getString(R.string.main_url);
        String rootNormal = sharedPreferences.getString("mainUrl", "");
        String homeurl = rootNormal + url;
        Logger.logD("url", "main Url : " + homeurl);
        if ("post".equalsIgnoreCase(methodname)) {
            HttpPost post = new HttpPost(homeurl);
            UrlEncodedFormEntity entity;
            try {
                entity = new UrlEncodedFormEntity(params, "UTF-8");
                post.setEntity(entity);
            } catch (UnsupportedEncodingException e1) {
                Logger.logE(RestUrl.class.getSimpleName(), "Exception in restUrlServerCall method first catch checking the codition post", e1);
            }

            try {
                if (httpclient == null)
                    return null;
                HttpResponse response = httpclient.execute(post);
                HttpEntity entity1 = response.getEntity();
                if (entity1 != null) {
                    InputStream inStream = entity1.getContent();
                    String result = convertStreamToString(inStream);
                    inStream.close();
                    Logger.logV(RestUrl.class.getSimpleName(), "The calling url is" + homeurl);
                    Logger.logV(RestUrl.class.getSimpleName(), "The result is" + result);
                    return result;
                } else {
                    return null;
                }

            } catch (UnknownHostException e) {
                Logger.logE(RestUrl.class.getSimpleName(), "Exception in restUrlServerCall method second catch checking the codition post", e);
                return null;
            } catch (ClientProtocolException e) {
                Logger.logE(RestUrl.class.getSimpleName(), "Exception in restUrlServerCall method third catch checking the codition post", e);
                return null;
            } catch (IOException e) {
                Logger.logE(RestUrl.class.getSimpleName(), "Exception in restUrlServerCall method fourth catch checking the codition post", e);
                return null;
            }

        }
        return null;

    }

    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 2 * 1024);
        StringBuilder sb = new StringBuilder(2 * 1024);
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            Logger.logE(RestUrl.class.getSimpleName(), "Exception in convertStreamToString method first catch", e);

        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Logger.logE(RestUrl.class.getSimpleName(), "Exception in convertStreamToString method second catch", e);

            }
        }

        return sb.toString();

    }

    public boolean checkNetwork() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        code = sharedPreferences.getInt("NetworkSetting", 0);
        ConnectivityManager connectionManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo activeNetworkInfo = connectionManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            haveConnectedActive = true;
        }
        if (wifi.isAvailable()) {
            haveConnectedWifi = true;
        }
        try {
            if (mobile.isAvailable()) {
                haveConnectedMobile = true;
            }
        } catch (Exception e) {
            Logger.logE(RestUrl.class.getSimpleName(), "Exception in checkNetwork method first catch", e);
        }
        getCheck();

        return haveConnectedActive;
    }

    public void getCheck(){
        switch (code) {
            case 0:
                haveConnectedActive = (haveConnectedWifi || haveConnectedMobile) && haveConnectedActive;
                break;
            case 1:
                // if network is connected only to Wifi network return true ,else false
                haveConnectedActive = haveConnectedWifi && haveConnectedActive;
                break;
            default:
        }
    }
}
