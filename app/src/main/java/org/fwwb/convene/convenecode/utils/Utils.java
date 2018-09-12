package org.fwwb.convene.convenecode.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.fwwb.convene.convenecode.BaseActivity;
import org.fwwb.convene.convenecode.BeenClass.beneficiary.Address;
import org.fwwb.convene.convenecode.BeenClass.boundarylevel.Boundary;
import org.fwwb.convene.convenecode.BeenClass.boundarylevel.LocationType;
import org.fwwb.convene.convenecode.BeenClass.boundarylevel.LocationTypeList;
import org.fwwb.convene.convenecode.LoginParentActivity;
import org.fwwb.convene.R;
import org.fwwb.convene.convenecode.UpdateMasterLoading;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by mahiti on 24/1/17.
 */

public class Utils {


    private static String belongTo = " belong to";

    private Utils() {
        Logger.logV(Constants.DO_NOTHING,Constants.DO_NOTHING);
    }

    private static final String TAG="Utils";
    private static final String LOCATION_LEVEL_KEY="location_level";
    private static final String PRIMARY_KEY="primary";
    private static final String LEAST_LOCATION_NAME_KEY="least_location_name";
    private static final String ADDRESS_ID_KEY="address_id";
    private static AlertDialog  alert1 = null;
    private static final String CRY = "/cry/";



    public static boolean haveNetworkConnection(Context context) {
        boolean isConnected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork;
            if (cm != null) {
                activeNetwork = cm.getActiveNetworkInfo();
                isConnected = ((activeNetwork != null) && (activeNetwork.isConnectedOrConnecting()));
            }

        } catch (Exception e) {
           Logger.logE(TAG,"Exception on checking the inetrnet connection",e);
        }
        return isConnected;
    }

    public static String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }
        Integer ageInt = age;

        return ageInt.toString();
    }

    public static Age calculateAge(Date birthDate)
    {
        int years = 0;
        int months = 0;
        int days = 0;
        //create calendar object for birth day
        Calendar birthDay = Calendar.getInstance();
        birthDay.setTimeInMillis(birthDate.getTime());
        //create calendar object for current day
        long currentTime = System.currentTimeMillis();
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(currentTime);
        //Get difference between years
        years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        int currMonth = now.get(Calendar.MONTH) + 1;
        int birthMonth = birthDay.get(Calendar.MONTH) + 1;
        //Get difference between months
        months = currMonth - birthMonth;
        //if month difference is in negative then reduce years by one and calculate the number of months.
        if (months < 0)
        {
            years--;
            months = 12 - birthMonth + currMonth;
            if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                months--;
        } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            years--;
            months = 11;
        }
        //Calculate the days
        if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
            days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
        else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            int today = now.get(Calendar.DAY_OF_MONTH);
            now.add(Calendar.MONTH, -1);
            days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
        } else
        {
            days = 0;
            if (months == 12)
            {
                years++;
                months = 0;
            }
        }
        //Create new Age object
        return new Age(days, months, years);
    }
    /**
     * Giving confirmation for the logout
     */
    public static  void callDialogConformation(final Context context, final Activity activity) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Logout confirmation").setMessage("Are you sure you want to logout?").setPositiveButton(R.string.LOGOUT, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                setLogout(activity);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Logger.logD("setNegativeButton","DialogInterface ");
            }
        }).show();
    }

    /**
     * method which contains logout functionality which will logout from app
     */
    public static void setLogout(Activity context) {
        SharedPreferences syncSurveyPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String username=syncSurveyPreferences.getString("UserName", "");
        int userID=syncSurveyPreferences.getInt("partner_id", 0);
        Logger.logD("Username","Username for SP"+username);
        Logger.logD("userID","userID for SP"+userID);
        Intent intent = new Intent(context, LoginParentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        context.finish();
        SharedPreferences.Editor editor = syncSurveyPreferences.edit();
        editor.putString("uId", "");
        editor.putString("UID", "");
        editor.putBoolean("MasterTable_Flag", false);
        editor.putString("UserNameUserID", username+"#"+userID);
        editor.putString("UsernamePartnerID", username+"#"+userID);
        editor.apply();
        context.finish();
    }


    /**
     * Giving confirmation for the logout
     */
    public static void contentUpdateConformation(final Activity context) {
        CheckNetwork checkNetwork = new CheckNetwork(context);
        final SharedPreferences syncSurveyPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!checkNetwork.checkNetwork()) {
            ToastUtils.displayToast(context.getString(R.string.checkNet),context.getApplicationContext());
            return;
        }
        AlertDialog.Builder contentUpdateConfirmDialog = new AlertDialog.Builder(context);
        contentUpdateConfirmDialog.setTitle("Update confirmation").setMessage("Content updating may take some time. Make sure you have good connectivity!").setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                updateMasterDatabase(context,syncSurveyPreferences);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Logger.logD("setNegativeButton","DialogInterface ");
            }
        }).show();
    }

    /**
     * method to update the master data
     */
    public static void updateMasterDatabase(final Activity activity, final SharedPreferences syncSurveyPreferences) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(activity, UpdateMasterLoading.class);
                intent.putExtra("url", activity.getString(R.string.main_url));
                intent.putExtra("urlBeneficiary", activity.getString(R.string.main_url));
                intent.putExtra("uId", Integer.parseInt(syncSurveyPreferences.getString("UID", "")));
                activity.startActivity(intent);
                activity.finish();
            }
        });
    }


    /**
     * this method will return the difference between the two dates .
     * @param startDate   starting date
     * @param endDate    secound date.
     * @return     retuen the different in hours
     */
    public static long printDifference(Date startDate, Date endDate) {

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();


        Logger.logV(TAG,"startDate : " + startDate);
        Logger.logV(TAG,"endDate : "+ endDate);
        Logger.logV(TAG,"different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        Logger.logV(TAG,"%d days" + elapsedDays +  "%d hours"+ elapsedHours + " %d minutes "+ elapsedMinutes + " %d seconds%n" + elapsedSeconds);
        return elapsedMinutes;
    }
    public static List<Address> getAddressList(JSONArray addressString, String syncStatusString) {
        Logger.logD("","addressIdString array from bundle extras" + addressString.toString());
        List<Address> addressBeanList=new ArrayList<>();
        try {
            for (int j = 0; j < addressString.length(); j++) {
                Address address = new Address();
                JSONObject jsonobject = addressString.getJSONObject(j);
                address.setAddress1(jsonobject.getString("address1"));
                address.setAddress2(jsonobject.getString("address2"));
                if(jsonobject.has(LOCATION_LEVEL_KEY)) {
                    address.setLocationLevel(jsonobject.getInt(LOCATION_LEVEL_KEY));
                }
                if(jsonobject.has(PRIMARY_KEY)){
                    address.setPrimary(jsonobject.getInt(PRIMARY_KEY));
                }
                address.setPincode(jsonobject.getString("pincode"));
                address.setLeastLocationId(jsonobject.getInt("boundary_id"));
                if("2".equalsIgnoreCase(syncStatusString)){
                    address.setLeastLocationName(jsonobject.getString(LEAST_LOCATION_NAME_KEY));
                    address.setAddressId(jsonobject.getString(ADDRESS_ID_KEY));
                }else{
                    address.setLeastLocationName(jsonobject.getString(LEAST_LOCATION_NAME_KEY));
                    address.setAddressId("");
                }
                addressBeanList.add(address);
            }
        }catch (Exception e){
            Logger.logE(TAG,"",e);
        }
        return addressBeanList;
    }

    public static HashMap<Integer,List<Address>> getAddressListHashMap(JSONArray addressString, String syncStatusString) {
        Logger.logD("","addressIdString array from bundle extras" + addressString.toString());

        HashMap<Integer,List<Address>> listHashMap=new HashMap<>();
        try {
            for (int j = 0; j < addressString.length(); j++) {
                List<Address> addressBeanList=new ArrayList<>();
                Address address = new Address();
                if (addressString.getJSONObject(j) != null) {
                    JSONObject jsonobject = addressString.getJSONObject(j);
                    address.setAddress1(jsonobject.getString("address1"));
                    address.setAddress2(jsonobject.getString("address2"));
                    if (jsonobject.has(LOCATION_LEVEL_KEY)) {
                        address.setLocationLevel(jsonobject.getInt(LOCATION_LEVEL_KEY));
                    }
                    if (jsonobject.has(PRIMARY_KEY)) {
                        address.setPrimary(jsonobject.getInt(PRIMARY_KEY));
                    }
                    address.setPincode(jsonobject.getString("pincode"));
                    address.setLeastLocationId(jsonobject.getInt("boundary_id"));
                    if ("2".equalsIgnoreCase(syncStatusString)) {
                        address.setLeastLocationName(jsonobject.getString(LEAST_LOCATION_NAME_KEY));
                        address.setAddressId(jsonobject.getString(ADDRESS_ID_KEY));
                    } else {
                        if (jsonobject.has(LEAST_LOCATION_NAME_KEY))
                        address.setLeastLocationName(jsonobject.getString(LEAST_LOCATION_NAME_KEY));
                        address.setAddressId("");
                    }
                    if(jsonobject.has("proof_id"))
                        address.setProofId(jsonobject.getString("proof_id"));
                    addressBeanList.add(address);
                    listHashMap.put(j, addressBeanList);
                }
            }
        }catch (Exception e){
            Logger.logE(TAG,"",e);
        }
        return listHashMap;
    }

        public static  void showAlertPopUp(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_alert_dialog);
        TextView text = dialog.findViewById(R.id.labelTextview);
        text.setText(R.string.alert_message);
        Button dialogBtn_okay = dialog.findViewById(R.id.okButton);
        dialogBtn_okay.setOnClickListener(v -> dialog.cancel());
        dialog.show();

    }


    /**
     * method to set the languages based on prefernce seletced value
     */
    public static void chooseLanguage(SharedPreferences defaultPreferences, final Activity activity) {
        String selectedValue = "SelectedValue";
        final SharedPreferences.Editor syncSurveyEditorEditor = defaultPreferences.edit();
        final String[] languages = new String[]{activity.getString(R.string.English), activity.getString(R.string.Hindi)};
        DialogInterface.OnClickListener dialogInterface = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String language = languages[which];

                if (activity.getString(R.string.English).equals(language)) {
                    syncSurveyEditorEditor.putInt(Constants.SELECTEDLANGUAGE, 1);
                    syncSurveyEditorEditor.putInt(selectedValue, which);
                    BaseActivity.setLocality(1, activity);
                    alert1.cancel();
                    alert1=null;
                } else {
                    syncSurveyEditorEditor.putInt(Constants.SELECTEDLANGUAGE, 3);
                    syncSurveyEditorEditor.putInt(selectedValue, which);
                    BaseActivity.setLocality(2, activity);
                    alert1.cancel();
                    alert1=null;
                }
                syncSurveyEditorEditor.apply();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.select));
        builder.setSingleChoiceItems(languages, defaultPreferences.getInt(selectedValue, 0), dialogInterface);
        alert1 = builder.create();
        alert1.show();
    }




    public static List<LocationType> getLocationList(String locationLevelString){
        List<LocationType>locationList=new ArrayList<>();
        try {
            JSONObject jsonObject=new JSONObject(locationLevelString);
            JSONArray jsonArray=jsonObject.getJSONArray("location-type");

            for(int i=0;i<jsonArray.length();i++){
                JSONObject locationNameJsonObject=jsonArray.getJSONObject(i);
                LocationType ly= new LocationType();
                ly.setName(locationNameJsonObject.getString("name"));
                ly.setId(locationNameJsonObject.getInt("id"));
                ly.setSlug(locationNameJsonObject.getString("slug"));
                locationList.add(ly);
            }
        } catch (JSONException e) {
            Logger.logE(TAG,"Exception on getting the location ", e);
        }
        return locationList;
    }

    /**
     *
     * @param levelString
     * @param locationLevelString
     * @return
     */
    public static List<Boundary> setBoundaryList(String levelString, String locationLevelString){
        List<Boundary> boundaryList=new ArrayList<>();
        try {
            JSONObject jsonObject=new JSONObject(levelString);
            JSONArray jsonArray1=jsonObject.getJSONArray("location-type");
            Logger.logV(TAG,"jsonArray1.get(0 values" + jsonArray1.get(0));
            Gson gson=new Gson();
            LocationTypeList typeList=gson.fromJson(levelString,LocationTypeList.class);
            if(("location-rural").equals(locationLevelString)){
                boundaryList=setBoundaryBasedOnLocationType(typeList.getLocationType().get(0).getBoundaries());
            }else{
                boundaryList=setBoundaryBasedOnLocationType(typeList.getLocationType().get(1).getBoundaries());
            }
        }catch (Exception e){
            Logger.logE(TAG,"Exception on getting the location types from setBoundaryList",e);
        }
        return boundaryList;
    }

    private static List<Boundary> setBoundaryBasedOnLocationType(List<Boundary> boundaryList) {
        List<Boundary> boundaryTempList=new ArrayList<>();
        Boundary boundary1=new Boundary();
        boundary1.setName(Constants.SELECT);
        boundaryTempList.add(boundary1);
        for(int j=0;j<boundaryList.size();j++){
            Boundary boundary=new Boundary();
            boundary.setName(boundaryList.get(j).getName());
            boundary.setLevel(boundaryList.get(j).getLevel());
            boundaryTempList.add(boundary);
        }
        return boundaryTempList;
    }

    public static String getINTODateformate(String minValue) {
        Logger.logV("getINTODateformate----> ",minValue);
        String day=Character.toString(minValue.charAt(0))+Character.toString(minValue.charAt(1));
        String month=Character.toString(minValue.charAt(2))+Character.toString(minValue.charAt(3));
        String year=Character.toString(minValue.charAt(4))+Character.toString(minValue.charAt(5))+Character.toString(minValue.charAt(6))+Character.toString(minValue.charAt(7));
        Logger.logD("Dateforamt","date in validation" + day+"-"+month+"-"+year);
        return day+"-"+month+"-"+year;
    }

    public  static void checkBoundaryValidation(TextView errorText, String message) {
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(message);
        errorText.setFocusable(true);
        errorText.setFocusableInTouchMode(true);
        errorText.requestFocus();
    }

    /**
     * Copying the whole database if the responsetype is 4 for updating the application
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void copyEncryptedConveneDataBase(Context con, SharedPreferences preferences) {
        String packageName = con.getApplicationContext().getPackageName();
        String DB_PATH = String.format("/data/data/%s/databases/", packageName);
        final String inFileName = DB_PATH + preferences.getString(Constants.CONVENE_DB, "");
        Logger.logV(TAG, "the path of the file is" + inFileName);
        File dbFile = new File(inFileName);
        String outFileName = Environment.getExternalStorageDirectory().getPath() + "/" + "Cry.sqlite";

        if (dbFile.exists()) {
            try (FileInputStream fis = new FileInputStream(dbFile); FileOutputStream output = new FileOutputStream(outFileName)) {
                Logger.logV("teh file path is", "the file path is sd card is" + Environment.getExternalStorageDirectory().getPath() + "/" + dbFile);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
                output.flush();
                output.close();
                fis.close();
            } catch (Exception e) {
                Logger.logE(UpdateMasterLoading.class.getSimpleName(), " in copyEncryptedDataBase method", e);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void copyEncryptedDataBase(Context con, SharedPreferences loginPreferences) {
        String packageName = con.getApplicationContext().getPackageName();
        String dbPath = String.format(con.getString(R.string.databasepath), packageName);
        final String inFileName = dbPath + loginPreferences.getString(Constants.DBNAME, "");
        Logger.logV(TAG, "the path of the file is" + inFileName);
        File dbFile = new File(inFileName);

        if (dbFile.exists()) {
            String outFileName = Environment.getExternalStorageDirectory().getPath() + "/" + "levels.sqlite";
            try (FileInputStream fis = new FileInputStream(dbFile); OutputStream output = new FileOutputStream(outFileName)) {
                Logger.logV("teh file path is", "the file path is sd card is" + Environment.getExternalStorageDirectory().getPath() + "/" + "levels");
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
                fis.close();
                output.flush();
                output.close();
            } catch (FileNotFoundException e) {
                Logger.logE("", "Exception in copyEncryptedDataBase method", e);
            } catch (IOException e) {
                Logger.logE("", "Exception in copyEncryptedDataBase method second catch", e);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean previousUserCopyEncryptedDataBase(Context con, String getPaceOfLoaction, String databasePath, String previousUserName, String dateformat) {
        Logger.logV("Location", "the location where copyEncriptedcalled " + getPaceOfLoaction);
        boolean isCopied=false;
        try{
            String outFileName="";
            String packageName = con.getApplicationContext().getPackageName();
            String dbpath = String.format("/data/data/%s/databases/", packageName);
            final String inFileName = dbpath +databasePath;
            Logger.logV(TAG, "the path of the file is= level=" + inFileName);
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(dateformat, Locale.ENGLISH);
            String data = sdf.format(date);
            File pyFolder=new File(Environment.getExternalStorageDirectory().getPath()+CRY+previousUserName+"_"+data);
            if (!pyFolder.exists())
                pyFolder.mkdirs();
            File dbFile = new File(inFileName);
            switch (databasePath){
                case "ENCRYPTED.db":
                    outFileName = Environment.getExternalStorageDirectory().getPath()+CRY+previousUserName+"_"+data+"/responses.sqlite";
                    break;
                case "DBNAME":
                    outFileName = Environment.getExternalStorageDirectory().getPath()+CRY+previousUserName+"_"+data+"/location_benefciaries _"+previousUserName+".sqlite";
                    break;
                default:
                        break;
            }
            File outPutFilePath = new File(outFileName);
            if(!outPutFilePath.exists())
                outPutFilePath.createNewFile();

            if (dbFile.exists()) {

                try (FileInputStream fis = new FileInputStream(dbFile); FileOutputStream output = new FileOutputStream(outFileName)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        output.write(buffer, 0, length);
                    }
                    output.flush();
                    output.close();
                    fis.close();
                    isCopied = true;
                    return isCopied;
                } catch (Exception e) {
                    Logger.logE(TAG, " in copyEncryptedDataBase method", e);
                }
            }
        }catch (Exception e){
            Logger.logE(TAG,"Exception on taking the backup data",e);
        }
        return isCopied;
    }

    /**
     * @param currentMonth
     * @return
     */
    public static List<String> getQuarterlyMonth(String currentMonth) {
        Logger.logD(TAG, "is" + currentMonth);
        List<String> quarterlyOne = new ArrayList<>();
        quarterlyOne.add("01");
        quarterlyOne.add("02");
        quarterlyOne.add("03");
        List<String> quarterlyTwo = new ArrayList<>();
        quarterlyTwo.add("04");
        quarterlyTwo.add("05");
        quarterlyTwo.add("06");
        List<String> quarterlyThree = new ArrayList<>();
        quarterlyThree.add("07");
        quarterlyThree.add("08");
        quarterlyThree.add("09");
        List<String> QuarterlyFour = new ArrayList<>();
        QuarterlyFour.add("10");
        QuarterlyFour.add("11");
        QuarterlyFour.add("12");
        if (quarterlyOne.contains(currentMonth)) {
            Logger.logD(TAG, " belong to quarterlyOne" + quarterlyOne.toString());
            return quarterlyOne;
        } else if (quarterlyTwo.contains(currentMonth)) {
            Logger.logD(TAG, belongTo + quarterlyTwo.toString());
            return quarterlyTwo;
        } else if (quarterlyThree.contains(currentMonth)) {
            Logger.logD(TAG, belongTo + quarterlyThree.toString());
            return quarterlyThree;
        } else if (QuarterlyFour.contains(currentMonth)) {
            Logger.logD(TAG, belongTo + QuarterlyFour.toString());
            return QuarterlyFour;
        }
        return quarterlyOne;
    }

    /**
     * @param currentMonth
     * @return
     */
    public static List<String> getHalfYearly(String currentMonth) {
        Logger.logD(TAG, "is" + currentMonth);
        List<String> quarterlyone = new ArrayList<>();
        quarterlyone.add("01");
        quarterlyone.add("02");
        quarterlyone.add("03");
        quarterlyone.add("04");
        quarterlyone.add("05");
        quarterlyone.add("06");
        List<String> quarterlyTwo = new ArrayList<>();
        quarterlyTwo.add("07");
        quarterlyTwo.add("08");
        quarterlyTwo.add("09");
        quarterlyTwo.add("10");
        quarterlyTwo.add("11");
        quarterlyTwo.add("12");
        if (quarterlyone.contains(currentMonth)) {
            Logger.logD(TAG, belongTo + quarterlyone.toString());
            return quarterlyone;
        } else if (quarterlyTwo.contains(currentMonth)) {
            Logger.logD(TAG, belongTo + quarterlyTwo.toString());
            return quarterlyTwo;
        }
        return quarterlyone;
    }



}
