package org.yale.convene.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DirectUrlCall
{
    private Context mContext;
    private static final int MY_SOCKET_TIMEOUT = 5000;

    public DirectUrlCall(Context mContext) {
        this.mContext = mContext;
    }

    public String restUrlServerCall(String urlname, String methodname, List<NameValuePair> params) {
        Logger.logD(RestUrl.class.getSimpleName(), "post Parameters : " + params);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        HttpClient httpclient = null;

        try {
            MySSLSocketFactory post = new MySSLSocketFactory(null);
            int connectionTimeOut = 0;
            int timeoutSocket = 0;
            httpclient = post.getNewHttpClient(connectionTimeOut, timeoutSocket);
        } catch (KeyManagementException var17) {
            Logger.logE(RestUrl.class.getSimpleName(), "KeyManagementException in restUrlServerCall ", var17);
        } catch (UnrecoverableKeyException var18) {
            Logger.logE(RestUrl.class.getSimpleName(), "UnrecoverableKeyException in restUrlServerCall ", var18);
        } catch (NoSuchAlgorithmException var19) {
            Logger.logE(RestUrl.class.getSimpleName(), "NoSuchAlgorithmException in restUrlServerCall ", var19);
        } catch (KeyStoreException var20) {
            Logger.logE(RestUrl.class.getSimpleName(), "KeyStoreException in restUrlServerCall ", var20);
        }

        String rootNormal = preferences.getString("urlBeneficiary", "");
        String homeurl = rootNormal + urlname;
        Logger.logD("url", "main Url : " + homeurl);
        if("post".equalsIgnoreCase(methodname)) {
            HttpPost post1 = new HttpPost(homeurl);

            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
                post1.setEntity(entity);
            } catch (UnsupportedEncodingException var16) {
                Logger.logE(RestUrl.class.getSimpleName(), "Exception in restUrlServerCall method first catch checking the codition post", var16);
            }

            try {
                if(httpclient == null) {
                    return null;
                } else {
                    HttpResponse e = httpclient.execute(post1);
                    HttpEntity entity1 = e.getEntity();
                    if(entity1 != null) {
                        InputStream inStream = entity1.getContent();
                        String result = this.convertStreamToString(inStream);
                        inStream.close();
                        Logger.logV(RestUrl.class.getSimpleName(), "The calling url is" + homeurl);
                        Logger.logV(RestUrl.class.getSimpleName(), "The result is" + result);
                        return result;
                    } else {
                        return null;
                    }
                }
            } catch (UnknownHostException var13) {
                Logger.logE(RestUrl.class.getSimpleName(), "Exception in restUrlServerCall method second catch checking the codition post", var13);
                return null;
            } catch (ClientProtocolException var14) {
                Logger.logE(RestUrl.class.getSimpleName(), "Exception in restUrlServerCall method third catch checking the codition post", var14);
                return null;
            } catch (IOException var15) {
                Logger.logE(RestUrl.class.getSimpleName(), "Exception in restUrlServerCall method fourth catch checking the codition post", var15);
                return null;
            }
        } else {
            final String[] Response = {""};
            StringRequest postRequest = new StringRequest(Request.Method.GET, homeurl,
                    response -> {
                        Logger.logV("the Response are", "the response is" + response);
                        Response[0] =response;

                    },
                    error -> {
                        Logger.logE("","",error);
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> params = new HashMap<String, String>();
                    return params;
                }
            };
            postRequest.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(mContext).add(postRequest);
            return Response[0];
        }
    }


    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 2048);
        StringBuilder sb = new StringBuilder(2048);
        String line = null;

        try {
            while((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException var14) {
            Logger.logE(RestUrl.class.getSimpleName(), "Exception in convertStreamToString method first catch", var14);
        } finally {
            try {
                is.close();
            } catch (IOException var13) {
                Logger.logE(RestUrl.class.getSimpleName(), "Exception in convertStreamToString method second catch", var13);
            }

        }
        return sb.toString();
    }

}
