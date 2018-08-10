package org.assistindia.convene.api;

import android.content.Context;
import android.os.Environment;

import org.assistindia.convene.R;
import org.assistindia.convene.utils.Constants;
import org.assistindia.convene.utils.FileUtils;
import org.assistindia.convene.utils.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by mahiti on 28/3/16.
 */
public class UploadFile
{

    private final static OkHttpClient client = new OkHttpClient();
    private static final String TAG = "MultiPartRestClient";

    private UploadFile() {
        Logger.logV(Constants.DO_NOTHING,Constants.DO_NOTHING);
    }

    public static  String sendDataToServerGetResponse(Context context, String uId)
    {
        Logger.logV("the data is","the data");
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date());

        builder.addFormDataPart("uId", uId);
        String outPutPath = Environment.getExternalStorageDirectory()+"/reg_"+uId+time+".zip";
        String inputPath = Environment.getExternalStorageDirectory()+"/DataBase_Backup";
        FileUtils.makeDirs(outPutPath);
        List<String> fileList = FileUtils.getFilesList(inputPath);
        if(fileList==null) {
            try {
                File new_file = new File(Environment.getExternalStorageDirectory()+"/empty.zip");
                if (!new_file.exists())
                    new_file.createNewFile();
                builder.addFormDataPart("dbfile", new_file.getName(), RequestBody.create( MediaType.parse("text/x-markdown; charset=utf-8"), new_file));
            } catch (Exception e) {
                Logger.logE(UploadFile.class.getSimpleName(), "Exception in fetchDatabaseValues", e);
            }
        }
        else {
            FileUtils.zipIt(outPutPath, inputPath, fileList);
            try {
                File imageFile = new File(outPutPath);
                if (imageFile.exists()) {
                    builder.addFormDataPart("dbfile", imageFile.getName(), RequestBody.create( MediaType.parse("text/x-markdown; charset=utf-8"), imageFile));
                }
            } catch (Exception e) {
                Logger.logE(UploadFile.class.getSimpleName(), "Exception in fetchDatabaseValues", e);
            }
        }

        String responseBody = null;

        try {

            Request request = new Request.Builder()
                    .addHeader("Accept", "application/json")
                    .url(context.getString(R.string.main_url)+"store-db/")
                    .post(builder.build())
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return response.message();
                } else {
                    return response.body().string();
                }
            }catch (Exception e)
            {
                Logger.logE(TAG, e.getMessage(), e); 
            }    
                Logger.logD(TAG, " Response : " + responseBody);
        } catch (Exception e)
        {
            Logger.logE(TAG, "sendDataToServerGetResponse", e);
        }


        return null;
    }


}
