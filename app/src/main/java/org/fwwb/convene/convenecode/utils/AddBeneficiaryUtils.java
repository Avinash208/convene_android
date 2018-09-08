package org.fwwb.convene.convenecode.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.fwwb.convene.convenecode.AddBeneficiaryActivity;
import org.fwwb.convene.convenecode.BeenClass.StatusBean;
import org.fwwb.convene.convenecode.BeenClass.SurveysBean;
import org.fwwb.convene.convenecode.BeenClass.beneficiary.Address;
import org.fwwb.convene.convenecode.BeenClass.beneficiary.Datum;
import org.fwwb.convene.convenecode.BeenClass.boundarylevel.Boundary;
import org.fwwb.convene.convenecode.BeenClass.boundarylevel.LocationType;
import org.fwwb.convene.convenecode.BeenClass.parentChild.LevelBeen;
import org.fwwb.convene.convenecode.BeenClass.parentChild.SurveyDetail;
import org.fwwb.convene.R;
import org.fwwb.convene.convenecode.SupportClass;
import org.fwwb.convene.convenecode.SurveyListLevels;
import org.fwwb.convene.convenecode.SurveyQuestionActivity;
import org.fwwb.convene.convenecode.adapter.BoundarySpinnerAdapter;
import org.fwwb.convene.convenecode.adapter.LocationSpinnerAdapter;
import org.fwwb.convene.convenecode.adapter.spinnercustomadapter.CustomNewSpinnerAdapter;
import org.fwwb.convene.convenecode.adapter.spinnercustomadapter.CustomSpinnerAdapter;
import org.fwwb.convene.convenecode.database.DBHandler;
import org.fwwb.convene.convenecode.database.ExternalDbOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.Integer.parseInt;

public class AddBeneficiaryUtils {
    private static final String RURAL_LOCATION_TYPE = "Rural";
    private static final String TAG = "AddBeneficiaryUtils";
    private static final String SAVE_TO_DRAFT_FLAG_KEY ="SaveDraftButtonFlag";
    private static final String MY_PREFS_NAME = "MyPrefs";
    private static final String BEN_ID_KEY = "ben_id" ;
    private static final String SERVICE_STRING = "services";
    private static final String ADDRESS_ID_KEY = "address_id";
    private static String levelSeven = "Level7";
    private static String editViewStr = "EDIT/VIEW";
    private SharedPreferences sharedPreferences1;
    private ExternalDbOpenHelper dbhelper;
    private Activity activity;

    private UpdateAddressDetails updateAddressDetails;

    public AddBeneficiaryUtils(Activity activityLoc) {
        this.activity=activityLoc;
        sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(activityLoc);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activityLoc);
        dbhelper = ExternalDbOpenHelper.getInstance(activityLoc, sharedPreferences.getString(Constants.DBNAME, ""), sharedPreferences.getString("inv_id", ""));

    }

    public AddBeneficiaryUtils(AddBeneficiaryActivity activityLoc) {
        this.activity=activityLoc;
        updateAddressDetails = activityLoc;
        sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(activityLoc);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activityLoc);
        dbhelper = ExternalDbOpenHelper.getInstance(activityLoc, sharedPreferences.getString(Constants.DBNAME, ""), sharedPreferences.getString("inv_id", ""));

    }


    /**
     * method to add the selected services to json object
     *
     * @param listService
     * @param jsonObject
     */
    public static JSONObject setTheServices(List<String> listService, JSONObject jsonObject) {
        try {
            String services = "";
            if (listService.isEmpty()) {
                jsonObject.put(SERVICE_STRING, services);
            } else {
                for (int j = 0; j < listService.size(); j++) {
                    services = services + addServiceToString(listService, j);
                    Logger.logD("service", "Service concatinate" + services);
                }
                jsonObject.put(SERVICE_STRING, services);
            }
        } catch (Exception e) {
            Logger.logE(TAG, "Exception on setting the services", e);
        }
        return jsonObject;
    }
    /**
     * method to add the selected services to list of string
     *
     * @param listService
     * @param j
     * @return
     */
    private static String addServiceToString(List<String> listService, int j) {
        String serviceString = "";
        if (listService.size() == j + 1) {
            serviceString = serviceString.concat(listService.get(j));
        } else {
            serviceString = serviceString.concat(listService.get(j).concat(","));
            Logger.logD(TAG, "check box==" + serviceString.trim());
        }
        return serviceString;
    }
    /**
     * @param locationLevelString
     * @param spinner
     */
    public  List<LocationType> setLocationLevelAdapter(Activity activity, String locationLevelString, Spinner spinner) {
        List<LocationType> locationList = Utils.getLocationList(locationLevelString);
        // attaching data adapter to spinner
        LocationSpinnerAdapter spinnerAdapter = new LocationSpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, locationList);
        spinner.setAdapter(spinnerAdapter);
        return locationList;
    }

    public  List<LocationType> getAddressProof(String locationLevelString){
        List<LocationType>locationList=new ArrayList<>();
        try {
            LocationType locationTypeTemp=new LocationType();
            locationTypeTemp.setName(Constants.SELECT);
            locationList.add(locationTypeTemp);
            JSONArray jArray = new JSONArray(locationLevelString);
            for (int i=0;i<jArray.length();i++){
                JSONObject jsonObject=jArray.getJSONObject(i);
                LocationType locationType=new LocationType();
                locationType.setId(jsonObject.getInt("id"));
                locationType.setName(jsonObject.getString("name"));
                locationList.add(locationType);
            }
        } catch (JSONException e) {
            Logger.logE(TAG,"Exception on getting the address proof ", e);
        }
        return locationList;
    }
    /**
     * setting the household list to spinner adapter
     *
     * @param houseHoldList - list containing list of household
     */
    public void setHouseHoldAdapter(List<Datum> houseHoldList, Spinner spinner) {
        // attaching data adapter to spinner
        CustomNewSpinnerAdapter houseHoldAdapter = new CustomNewSpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, houseHoldList);
        spinner.setAdapter(houseHoldAdapter);
    }

    public void addToMainBeneficiaryList(List<Datum> beneficiaryPageList, List<Datum> beneficiaryFinalList) {
        for(int i=0;i<beneficiaryPageList.size();i++){
            if(i!=0){
                beneficiaryFinalList.add(beneficiaryPageList.get(i));
            }
        }
    }
    /**
     * @param slugName
     * @param locationlevel
     * @param ruralLocationType
     */
    public  void setHouseHoldLocation(String slugName, Spinner locationlevel, String ruralLocationType,List<LocationType> locationList) {
        if ((ruralLocationType).equalsIgnoreCase(slugName)) {
            locationlevel.setSelection(locationList.indexOf(locationList.get(0)));
        } else {
            locationlevel.setSelection(locationList.indexOf(locationList.get(1)));
        }
    }


    public List<String> getGenderList(){
        List<String> genderList = new ArrayList<>();
        genderList.add(Constants.SELECT_GENDER);
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Transgender");
        return genderList;
    }
    /**
     * @param statedynamicLabel
     * @param districtDynamicLabel
     * @param talukDynamicLabel
     * @param grampanchayatDynamicLabel
     * @param villageDynamicLabel
     * @param hamletdynamicLabel
     * @param slugName
     */
    public void setClusterLabels(TextView statedynamicLabel, TextView districtDynamicLabel, TextView talukDynamicLabel, TextView grampanchayatDynamicLabel, TextView villageDynamicLabel, TextView hamletdynamicLabel, String slugName) {
        statedynamicLabel.setText(activity.getString(R.string.state));
        SupportClass.setRedStar(statedynamicLabel, activity.getString(R.string.state));
        districtDynamicLabel.setText(activity.getString(R.string.district));
        SupportClass.setRedStar(districtDynamicLabel, activity.getString(R.string.district));
        if (RURAL_LOCATION_TYPE.equalsIgnoreCase(slugName)) {
            talukDynamicLabel.setText(activity.getString(R.string.taluk));
            SupportClass.setRedStar(talukDynamicLabel, activity.getString(R.string.taluk));
            grampanchayatDynamicLabel.setText(activity.getString(R.string.gramapanchayath));
            SupportClass.setRedStar(grampanchayatDynamicLabel, activity.getString(R.string.gramapanchayath));
            villageDynamicLabel.setText(activity.getString(R.string.village));
            SupportClass.setRedStar(villageDynamicLabel, activity.getString(R.string.village));
            hamletdynamicLabel.setText(activity.getString(R.string.Hamlet));
            SupportClass.setRedStar(hamletdynamicLabel, activity.getString(R.string.Hamlet));
        } else {
            talukDynamicLabel.setText(activity.getString(R.string.city));
            SupportClass.setRedStar(talukDynamicLabel, activity.getString(R.string.city));
            grampanchayatDynamicLabel.setText(activity.getString(R.string.area));
            SupportClass.setRedStar(grampanchayatDynamicLabel, activity.getString(R.string.area));
            villageDynamicLabel.setText(activity.getString(R.string.ward));
            SupportClass.setRedStar(villageDynamicLabel, activity.getString(R.string.ward));
            hamletdynamicLabel.setText(activity.getString(R.string.mohallam));
            SupportClass.setRedStar(hamletdynamicLabel, activity.getString(R.string.mohallam));
        }

    }


    /**
     *
     * @param spinnerList
     * @param
     */
    public void setEditedStateAdapter(List<Spinner> spinnerList) {
        List<LevelBeen> boundaries = new ArrayList<>();
        LevelBeen boundary1 = new LevelBeen();
        boundary1.setName(Constants.SELECT);
        boundaries.add(boundary1);
        CustomSpinnerAdapter dataAdapter = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, boundaries);
        spinnerList.get(0).setAdapter(dataAdapter);
        spinnerList.get(1).setAdapter(dataAdapter);
        spinnerList.get(2).setAdapter(dataAdapter);
        spinnerList.get(3).setAdapter(dataAdapter);
        spinnerList.get(4).setAdapter(dataAdapter);
    }


    /**
     * method to set the district based on parent selection
     *
     * @param houseHoldId
     * @return
     */
    public List<LevelBeen> setHouseholdDistrict(int houseHoldId, ExternalDbOpenHelper dbhelper) {
        int level3Id = dbhelper.getLevelIdFromLevels(levelSeven, 3, "", houseHoldId);
        String levelThree = "Level3";
        List<LevelBeen> preDistrictLevelList = dbhelper.getDistrictNamesFromLevels(levelThree, 3, level3Id);
        Logger.logV(TAG, "District level been values" + preDistrictLevelList.get(0).getName());
        return preDistrictLevelList;
    }

    /**
     * method to get the taluk list from levels based on parent selction
     *
     * @param houseHoldId
     * @param slugName
     * @return
     */
    private List<LevelBeen> setHouseholdTaluk(int houseHoldId, String slugName) {
        int level4Id = dbhelper.getLevelIdFromLevels(levelSeven, 4, "", houseHoldId);
        String levelFour = "Level4";
        return dbhelper.getLevelNamesFromLevels(levelFour, 4, level4Id, slugName);
    }

    /**
     * method to get the grampanachat list from levels table
     *
     * @param houseHoldId
     * @param slugName
     * @return
     */
    public List<LevelBeen> setHouseholdGramapanchayat(int houseHoldId, String slugName) {
        int level5Id = dbhelper.getLevelIdFromLevels(levelSeven, 5, "", houseHoldId);
        String levelFive = "Level5";
        return dbhelper.getLevelNamesFromLevels(levelFive, 5, level5Id, slugName);
    }


    /**
     * method to get the hamlet list from levels table
     *
     * @param houseHoldId
     * @param slugName
     * @return
     */
    public List<LevelBeen> setHouseholdHamlet(int houseHoldId, String slugName) {
        return dbhelper.getLevelNamesFromLevels(levelSeven, 7, houseHoldId, slugName);
    }


    /**
     * method to get the village list from levels table
     *
     * @param houseHoldId
     * @param slugName
     * @return
     */
    public List<LevelBeen> setHouseholdVillage(int houseHoldId, String slugName) {
        int level6Id = dbhelper.getLevelIdFromLevels(levelSeven, 6, "", houseHoldId);
        String levelSix = "Level6";
        return dbhelper.getLevelNamesFromLevels(levelSix, 6, level6Id, slugName);
    }

    /**
     *
     * @param talukSpinner
     * @param talukList
     * @param leastLocationId
     * @param selectedSlugName
     * @param editSecondaryAddress
     */
    public void setUpdatedDistrictAdapter(Spinner talukSpinner, List<LevelBeen> talukList, Integer leastLocationId, String selectedSlugName, boolean editSecondaryAddress) {
        if (editSecondaryAddress) {
                    /*getting the states details from levels*/
            List<LevelBeen> preSelectedList = setHouseholdTaluk(leastLocationId, selectedSlugName);
            for (int j = 0; j < talukList.size(); j++) {
                if ((!preSelectedList.isEmpty() && (preSelectedList.get(0).getName().contains(talukList.get(j).getName())&&(preSelectedList.get(0).getName().equals(talukList.get(j).getName()))))) {
                    talukSpinner.setSelection(talukList.indexOf(talukList.get(j)));
                    Logger.logV(TAG, "State level values names" + talukList.get(0).getName());
                    Logger.logV(TAG, "State level values" + j);
                    break;
                }
            }
        }
    }


    /**
     *
     * @param levelString
     * @param locationLevelString
     * @param activity
     * @param spFacilityStateFilter
     */
    public static void setStateAdapter(String levelString, String locationLevelString, Activity activity, Spinner spFacilityStateFilter) {
        try {
            List<Boundary> boundaryList=Utils.setBoundaryList(levelString,locationLevelString);
            BoundarySpinnerAdapter adapter1=new BoundarySpinnerAdapter(activity,android.R.layout.simple_spinner_dropdown_item,boundaryList);
            spFacilityStateFilter.setAdapter(adapter1);
        }catch (Exception e){
            Logger.logE(TAG,"",e);
        }

    }
    /**
     * method to get the secondary address values
     * @param
     * @param secondaryAddressLayout
     * @param addresssecJsonObject
     */
    public JSONObject getDynamicAddressValues(LinearLayout secondaryAddressLayout, JSONObject addresssecJsonObject) {
        if (secondaryAddressLayout != null) {
            int dynamicLinearChildCount = secondaryAddressLayout.getChildCount();
            if (dynamicLinearChildCount > 0) {
                try {
                    addresssecJsonObject = methodToGetTheMultipleAddressDetails(dynamicLinearChildCount,secondaryAddressLayout);
                } catch (Exception e) {
                    Logger.logE(TAG, "", e);
                }
            }
        }
        return addresssecJsonObject;
    }

    public static JSONArray getFacilityArray(List<org.fwwb.convene.convenecode.BeenClass.facilities.Datum> beneficiaryList, int position){
        final JSONArray jsonArray=new JSONArray();
        try {
            JSONObject spinnerjsonObject = new JSONObject();
            spinnerjsonObject.put("ben_name", beneficiaryList.get(position).getName());
            spinnerjsonObject.put("ben_type", beneficiaryList.get(position).getFacilityType());
            spinnerjsonObject.put(BEN_ID_KEY, beneficiaryList.get(position).getId());
            spinnerjsonObject.put("type", "Facility");
            spinnerjsonObject.put("uuid", beneficiaryList.get(position).getUuid());

            jsonArray.put(spinnerjsonObject);
        }catch (Exception e){
            Logger.logE(TAG,"Exception on getFacilityArray" , e);
        }
        return jsonArray;
    }


    public static JSONArray getBeneficiaryArray(List<Datum> beneficiaryList, int position){
        final JSONArray jsonArray=new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ben_name", beneficiaryList.get(position).getName());
            jsonObject.put("ben_type", beneficiaryList.get(position).getBtype());
            jsonObject.put(BEN_ID_KEY, beneficiaryList.get(position).getId());
            jsonObject.put("type", "Beneficiary");
            jsonObject.put("uuid",beneficiaryList.get(position).getUuid());
            jsonObject.put("fac_uuid","");
            jsonArray.put(jsonObject);
        }catch (Exception e){
            Logger.logE("Exception","Exception on getBeneficiary array" , e);
        }
        return jsonArray;
    }

    public static void setPreviousFlag(int previousStatusFlag, TextView editBeneficiary, String primaryId) {
        if (previousStatusFlag==2) {
            editBeneficiary.setText("View");
            editBeneficiary.setTag(primaryId);
        } else {
            editBeneficiary.setText(editViewStr);
            editBeneficiary.setTag(primaryId);
        }
    }




    public static void beneficiaryLocationSetting(List<org.fwwb.convene.convenecode.BeenClass.facilities.Datum> beneficiaryList, int position, TextView textView){
        if(("Offline").equalsIgnoreCase(beneficiaryList.get(position).getStatus())||("Update").equalsIgnoreCase(beneficiaryList.get(position).getStatus())){
            textView.setText(String.format("From : %s", String.format("%s(Offline)", beneficiaryList.get(position).getBoundaryName())));
        }else{
            textView.setText(String.format("From : %s", beneficiaryList.get(position).getBoundaryName()));
        }

    }

    /**
     * method to set the pending or continue status for adding survey
     * @param getPausedSurveyID
     * @param editBeneficiary
     */
    public static  void setPendingTextView(Context context,int getPausedSurveyID, TextView editBeneficiary) {
        if (getPausedSurveyID!=0) {
            editBeneficiary.setText(R.string.continue_text);
            editBeneficiary.setTag(getPausedSurveyID);
        } else {
            editBeneficiary.setText(context.getString(R.string.pending));
        }
    }

    public static void setLocationBasedSurveyCompletedTextView(final Context context, List<StatusBean> getPausedCompletedSurveyList, TextView statusTextView, DBHandler summeryHandler, final String surveyId, int clusterid, final SharedPreferences preferences){
        if (!getPausedCompletedSurveyList.isEmpty()){
            final int getSurveyPid= summeryHandler.checkPrimarySaveDraftExist(Integer.parseInt(surveyId),clusterid);
            Logger.logD(TAG,"OFline"+getSurveyPid);
            for(int k=0;k<getPausedCompletedSurveyList.size();k++){
                statusTextView.setText(editViewStr);
                statusTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences.Editor editorSaveDraft= preferences.edit();
                        editorSaveDraft.putBoolean(SAVE_TO_DRAFT_FLAG_KEY,true);
                        editorSaveDraft.apply();
                        Intent intent = new Intent(context, SurveyQuestionActivity.class);
                        intent.putExtra("SurveyId", String.valueOf(getSurveyPid));
                        Logger.logD(TAG,"-->"+getSurveyPid);
                        intent.putExtra(PreferenceConstants.SURVEY_ID,String.valueOf(surveyId));
                        context.startActivity(intent);
                    }
                });

            }
        }
    }

    public static void setSurveyCompletedTextView(final Context context, List<StatusBean> getPausedCompletedSurveyList, TextView statusTextView, DBHandler summeryHandler, final String surveyId, String uuid, final SharedPreferences preferences){
        if (!getPausedCompletedSurveyList.isEmpty()){
            final int getSurveyPid= summeryHandler.checkPrymarySaveDraftExist(Integer.parseInt(surveyId),uuid);
            Logger.logD(TAG,"OFline"+getSurveyPid);
            for(int k=0;k<getPausedCompletedSurveyList.size();k++){
                statusTextView.setText(editViewStr);
                statusTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences.Editor editorSaveDraft= preferences.edit();
                        editorSaveDraft.putBoolean(SAVE_TO_DRAFT_FLAG_KEY,true);
                        editorSaveDraft.apply();
                        Intent intent = new Intent(context, SurveyQuestionActivity.class);
                        intent.putExtra("SurveyId", String.valueOf(getSurveyPid));
                        Logger.logD(TAG,"-->"+getSurveyPid);
                        intent.putExtra("survey_id",String.valueOf(surveyId));
                        context.startActivity(intent);
                    }
                });

            }
        }
    }
    /**
     *
     * @param dynamicLinearChildCount
     * @param secondaryAddressLayout
     * @return
     */
    private JSONObject methodToGetTheMultipleAddressDetails(int dynamicLinearChildCount, LinearLayout secondaryAddressLayout) {
        JSONObject addressJsonObject = new JSONObject();
        try {

            for (int address = 0; address < dynamicLinearChildCount; address++) {
                View v = secondaryAddressLayout.getChildAt(address);
                TextView address1TextView = v.findViewById(R.id.address1);
                addressJsonObject = new JSONObject((String) address1TextView.getTag());
                String jsonKey = addressJsonObject.keys().next();
                Logger.logD(TAG,"main address jsn object jsonKey" + jsonKey);
                addressJsonObject.put(jsonKey, addressJsonObject.get(jsonKey));
                Logger.logD(TAG,"main address jsn object jsonKey" + addressJsonObject.get(jsonKey));
                Logger.logD(TAG,"main address jsn object" + addressJsonObject.toString());
            }
        }catch (Exception e){
            Logger.logE(TAG,"",e);
        }

        return addressJsonObject;
    }



    public static boolean setCompleteTextViewFlag(int recentCompletedCount){
        boolean statusFlag;
        statusFlag = recentCompletedCount > 0;
        return statusFlag;
    }


    /**
     * method to set the survey selected details to shared preference
     * @param name
     * @param locationID
     * @param locationLevel
     * @param locationName
     * @param beneficiaryArray
     * @param preferences
     * @param surveysBean
     */
    public  void setToPreferences(Context context, String name, int locationID, Integer locationLevel, String locationName, String beneficiaryArray, SharedPreferences preferences, SurveysBean surveysBean) {
        try{
            List<SurveyDetail>  surveyDetail= SurveyListLevels.getSurveyDetails(context, preferences.getString(Constants.DBNAME,""), preferences.getString("UID",""),String.valueOf(surveysBean.getId()));
            SurveyDetail surveyDetailBean;
            for (int i=0;i<surveyDetail.size();i++) {
                if (name.equalsIgnoreCase(surveyDetail.get(i).getSurveyName())) {
                    SharedPreferences preferences1 = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                    surveyDetailBean = surveyDetail.get(i);
                    Logger.logD(TAG, "selected survey detaills");
                    SharedPreferences.Editor editorPreference = preferences1.edit();
                    editorPreference.putString(Constants.SURVEY_NAMe, surveyDetailBean.getSurveyName());
                    editorPreference.putString("Survey_tittle",surveyDetailBean.getSurveyName());
                    editorPreference.putInt(Constants.FEATURE, surveyDetailBean.getPFeature());
                    editorPreference.putInt(Constants.LIMIT, surveyDetailBean.getPLimit());
                    editorPreference.putInt(Constants.PERIODICITY, parseInt(surveyDetailBean.getPiriodicity()));
                    editorPreference.putString(Constants.PERIODICITY_FLAG, surveyDetailBean.getPiriodicityFlag());
                    editorPreference.putString(Constants.LABEL, surveyDetailBean.getLabels());
                    editorPreference.putString(Constants.VERSION, surveyDetailBean.getVn());
                    Logger.logD(TAG,"version VALUE to PREFERENCE" + surveyDetailBean.getVn());
                    editorPreference.putInt(Constants.CONFIG, (surveyDetailBean.getBConfig()));
                    editorPreference.putInt(Constants.RD, surveyDetailBean.getReasonDisagree());
                    String[] orderLevels = surveyDetailBean.getOrderLevels().split(",");
                    editorPreference.putString(Constants.O_LEAVEL, orderLevels[orderLevels.length - 1]);
                    editorPreference.putString(Constants.CODE, surveyDetailBean.getPcode());
                    editorPreference.putInt(Constants.SURVEY_ID, surveyDetailBean.getSurveyId());
                    Logger.logD(TAG," VALUE IN PREFERENCE" + surveyDetailBean.getSurveyId());
                    editorPreference.putString(Constants.BENEFICIARY_TYPE, surveyDetailBean.getBeneficiaryType());
                    editorPreference.putString(Constants.BENEFICIARY_IDS, surveyDetailBean.getBeneficiaryIds());
                    editorPreference.putString(Constants.FACILITY_IDS, surveyDetailBean.getFacilityIds());
                    editorPreference.putString("Survey_tittle", surveyDetailBean.getSurveyName());
                    editorPreference.putString("location_name", locationName);
                    editorPreference.putString("location_id", String.valueOf(locationID));
                    editorPreference.putString("beneficiary_array", beneficiaryArray);
                    if(beneficiaryArray!=null && beneficiaryArray.isEmpty()){
                        editorPreference.putString("beneficiaryPids", "0");
                        editorPreference.putString("uuid", "");
                        editorPreference.putString("facilityPids", "0");
                    }else if(beneficiaryArray!=null ){
                        JSONArray jsonArray = new JSONArray(beneficiaryArray);
                        String uuidBen = jsonArray.getJSONObject(0).getString("uuid");
                        editorPreference.putString("beneficiaryPids", jsonArray.getJSONObject(0).getString(BEN_ID_KEY));
                        editorPreference.putString("uuid", uuidBen);
                        editorPreference.putString("facilityPids", "0");
                    }
                    editorPreference.putString("clusterName", String.valueOf(locationLevel));
                    editorPreference.putString("cluster_id", String.valueOf(locationID));
                    editorPreference.putInt(Constants.Q_CONFIGS, surveyDetailBean.getQConfig());
                    editorPreference.apply();
                }
            }
        }catch (Exception e){
            Logger.logE("","",e);
        }
    }

    /**
     * method to set the font styles and labels for all the widgets
     * @param textViewList
     * @param face
     */
    public void setFontStyleLabels(List<TextView> textViewList, Typeface face) {
        textViewList.get(0).setTypeface(face);
        textViewList.get(1).setTypeface(face);
        SupportClass.setRedStar(textViewList.get(1), activity.getString(R.string.age));
        textViewList.get(2).setTypeface(face);
        SupportClass.setRedStar(textViewList.get(2), activity.getString(R.string.contact_person_number));
    }

    /**
     * method to set the multiple contact number while creation of beneficiary
     * @param contactCount
     * @param editTextHashMap
     * @param secondaryAddressLayout
     */
    public LinearLayout addMultipleContact(final int contactCount, final HashMap<Integer, EditText> editTextHashMap, LinearLayout secondaryAddressLayout) {
        try {
            final View boundaryView = activity.getLayoutInflater().inflate(R.layout.contact_number, secondaryAddressLayout, false);
            EditText contactEditText = boundaryView.findViewById(R.id.contactNoEdittext);
            TextView removeButton = boundaryView.findViewById(R.id.remove);
            editTextHashMap.put(contactCount, contactEditText);
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((LinearLayout) boundaryView.getParent()).removeView(boundaryView);
                    editTextHashMap.remove(contactCount);
                }
            });
            Logger.logD(TAG, "mobilenumber hasmap values" + editTextHashMap.toString());
            secondaryAddressLayout.addView(boundaryView);
        } catch (Exception e) {
            Logger.logE("", "", e);
        }
        return secondaryAddressLayout;
    }

    /**
     *
     * @param levelThree
     * @param districtSpinner
     * @param levelBeen
     */
    public void setStateSpinner(String levelThree, Spinner districtSpinner, LevelBeen levelBeen) {
        List<LevelBeen> districtListBeenList = dbhelper.getLevelDistrictValuesFromDB(levelThree, "name", String.valueOf(levelBeen.getId()));
        CustomSpinnerAdapter dataAdapter3 = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, districtListBeenList);
        districtSpinner.setAdapter(dataAdapter3);
    }

    /**
     * @param levelFour
     * @param levelBeen1
     * @param slugName
     * @param talukSpinner
     */
    public void setDistrictToSpinner(String levelFour, LevelBeen levelBeen1, String slugName, Spinner talukSpinner) {
        List<LevelBeen> levelBeenList = dbhelper.getLevelTalukValuesFromDB(levelFour, "name", String.valueOf(levelBeen1.getId()), slugName);
        CustomSpinnerAdapter dataAdapter3 = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, levelBeenList);
        talukSpinner.setAdapter(dataAdapter3);
    }

    /**
     * @param levelFive
     * @param grampanchayatSpinner
     * @param levelBeen2
     * @param slugName
     */
    public void setGrampanchayatToSpinner(String levelFive, Spinner grampanchayatSpinner, LevelBeen levelBeen2, String slugName) {
        List<LevelBeen> grampanchayatLevelList = dbhelper.getLevelGrampanchayatValuesFromDB(levelFive, "name", String.valueOf(levelBeen2.getId()), slugName);
        CustomSpinnerAdapter dataAdapter3 = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, grampanchayatLevelList);
        grampanchayatSpinner.setAdapter(dataAdapter3);
    }

    /**
     * @param levelSix
     * @param villageSpinner
     * @param slugName
     * @param levelBeen4
     */
    public void setGrampanachayatLevelToSpinner(String levelSix, Spinner villageSpinner, String slugName, LevelBeen levelBeen4) {
        List<LevelBeen> vilageLevelList = dbhelper.getLevelVillageValuesFromDB(levelSix, "name", String.valueOf(levelBeen4.getId()), slugName);
        CustomSpinnerAdapter dataAdapter3 = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, vilageLevelList);
        villageSpinner.setAdapter(dataAdapter3);
    }

    /**
     *  @param hamletDynamicSpinner
     * @param levelBeen5
     * @param slugName
     */
    public void setHamletLevelListToSpinner(Spinner hamletDynamicSpinner, LevelBeen levelBeen5, String slugName) {
        List<LevelBeen> hamletList = dbhelper.getLevelHamletDetails(levelSeven, "name", String.valueOf(levelBeen5.getId()), slugName);
        CustomSpinnerAdapter dataAdapter3 = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, hamletList);
        hamletDynamicSpinner.setAdapter(dataAdapter3);
    }

    public void setMultipleAddressView(final MultipleAddressPopUp addressPopUp,  HashMap<Integer, List<Address>> addressListHashMap, LinearLayout secondaryAddressLayout) {
        if(!addressListHashMap.isEmpty()&&secondaryAddressLayout!=null){
            JSONObject mainAddressJsonObject=new JSONObject();
            secondaryAddressLayout.removeAllViews();
            addressListHashMap = AddBeneficiaryUtils.getUpdatedHashMap(addressListHashMap);
            for(int i=0;i<addressListHashMap.size();i++){
                try {
                    if (addressListHashMap.get(i).isEmpty())
                        continue;
                    for (int j=0; j<addressListHashMap.get(i).size();j++) {
                        final View child = activity.getLayoutInflater().inflate(R.layout.address_details_item, secondaryAddressLayout, false);
                        TextView address1TextView = child.findViewById(R.id.address1);
                        TextView address2TextView = child.findViewById(R.id.address2);
                        TextView primaryLabelTextView = child.findViewById(R.id.primaryLabel);
                        final TextView editTextView = child.findViewById(R.id.edit);
                        final TextView remove = child.findViewById(R.id.remove);
                        remove.setTag("remove_" + i);
                        JSONObject jsonObject = new JSONObject();
                        if (addressListHashMap.get(i) != null && addressListHashMap.get(i).get(j) != null) {
                            address1TextView.setText(String.format("Address : %s", addressListHashMap.get(i).get(j).getAddress1()));
                            address2TextView.setText(String.format("Pincode : %s", addressListHashMap.get(i).get(j).getPincode()));


                            jsonObject.put("address1", addressListHashMap.get(i).get(j).getAddress1());
                            jsonObject.put("address2", addressListHashMap.get(i).get(j).getAddress2());
                            jsonObject.put("pincode", addressListHashMap.get(i).get(j).getPincode());
                            jsonObject.put("location_level", addressListHashMap.get(i).get(j).getLocationLevel());
                            jsonObject.put("proof_id", addressListHashMap.get(i).get(j).getProofId());
                            jsonObject.put("primary", addressListHashMap.get(i).get(j).getPrimary());
                        }

                        if (addressListHashMap.get(i) != null && addressListHashMap.get(i).get(j) != null && addressListHashMap.get(i).get(j).getPrimary() == 1) {
                            primaryLabelTextView.setVisibility(View.VISIBLE);
                        } else {
                            primaryLabelTextView.setVisibility(View.GONE);
                        }
                        if (addressListHashMap.get(i) != null && addressListHashMap.get(i).get(j) != null && "2".equalsIgnoreCase(sharedPreferences1.getString(Constants.SYNC_STATUS, ""))) {
                            jsonObject.put(ADDRESS_ID_KEY, addressListHashMap.get(i).get(j).getAddressId());
                        } else {
                            jsonObject.put(ADDRESS_ID_KEY, "");
                        }
                        jsonObject.put("boundary_id", addressListHashMap.get(i).get(j).getBoundaryId());
                        jsonObject.put("least_location_name", addressListHashMap.get(i).get(j).getLeastLocationName());

                        mainAddressJsonObject.put("address_" + i + "", jsonObject);
                        editTextView.setTag(i);
                        address1TextView.setTag(mainAddressJsonObject.toString());

                        HashMap<Integer, List<Address>> finalAddressListHashMap = addressListHashMap;
                        editTextView.setOnClickListener(view -> {
                            int editCount = (int) editTextView.getTag();
                            callEditAddressPopUp(finalAddressListHashMap, editCount, addressPopUp);
                        });

                        HashMap<Integer, List<Address>> finalAddressListHashMap1 = addressListHashMap;
                        remove.setOnClickListener(view -> {
                            SharedPreferences.Editor editor5 = sharedPreferences1.edit();
                            editor5.putBoolean(Constants.ISADDITIONALADDRESS_FLAG, true);
                            editor5.putBoolean(Constants.EDITSECONDARY_ADDRESS_FLAG, true);
                            editor5.apply();
                            String tag = remove.getTag().toString().replace("remove_", "");
                            finalAddressListHashMap1.remove(Integer.parseInt(tag));
                            HashMap<Integer, List<Address>> tempList = getUpdatedHashMap(finalAddressListHashMap1);
                            updateAddressDetails.onUpdateAddressSuccess(tempList);
                        });
                        secondaryAddressLayout.addView(child);
                    }
                }catch (Exception e){
                    Logger.logE(TAG,"Exception on setting the values to object",e);
                }

            }

        }
    }

    public static HashMap<Integer, List<Address>> getUpdatedHashMap(HashMap<Integer, List<Address>> addressListHashMap) {
        HashMap<Integer, List<Address>> tempHashMap = new HashMap<>();
        Iterator it = addressListHashMap.entrySet().iterator();
        int i = 0;
        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry)it.next();
            tempHashMap.put(i, (List<Address>) pair.getValue());
            i++;
        }

        return tempHashMap;
    }

    /**
     * below lines of code for primary and secondary address edit functionality
     * @param addressListHashMap
     * @param editCount
     * @param addressPopUp
     */
    private void callEditAddressPopUp(HashMap<Integer, List<Address>> addressListHashMap, int editCount, MultipleAddressPopUp addressPopUp) {
        SharedPreferences.Editor editor5 = sharedPreferences1.edit();
        editor5.putBoolean(Constants.ISADDITIONALADDRESS_FLAG, true);
        editor5.putBoolean(Constants.EDITSECONDARY_ADDRESS_FLAG, true);
        editor5.apply();
        addressListHashMap=addressPopUp.showAddMultipleAddressPopUp(addressListHashMap,editCount);
        Logger.logD(TAG,"" +addressListHashMap.size());
    }

    /**
     *
     * @param districtSpinner
     * @param leastLocationId
     * @param levelBeen
     */
    public void setEdittedStateSpinner(String levelThree, Spinner districtSpinner, int leastLocationId, LevelBeen levelBeen,AddBeneficiaryUtils addBeneficiaryUtils) {
        List<LevelBeen> districtListBeenList = dbhelper.getLevelDistrictValuesFromDB(levelThree, "name", String.valueOf(levelBeen.getId()));
        CustomSpinnerAdapter dataAdapter3 = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, districtListBeenList);
        districtSpinner.setAdapter(dataAdapter3);
        List<LevelBeen> preSelectedList = addBeneficiaryUtils.setHouseholdDistrict(leastLocationId,dbhelper);
        for (int i = 0; i < districtListBeenList.size(); i++) {
            if (!preSelectedList.isEmpty() && (preSelectedList.get(0).getName().contains(districtListBeenList.get(i).getName()))) {
                districtSpinner.setSelection(districtListBeenList.indexOf(districtListBeenList.get(i)));
                break;
            }
        }
    }

    /**
     *  @param grampanchayatSpinner
     * @param levelBeen2
     * @param slugName
     * @param selectedSlugName
     * @param leastLocationId
     */
    public void setEdittedTalukLevelToSpinner(String levelFive,Spinner grampanchayatSpinner, LevelBeen levelBeen2, String slugName, String selectedSlugName, int leastLocationId,AddBeneficiaryUtils addBeneficiaryUtils) {
        List<LevelBeen> grampanchayatList = dbhelper.getLevelGrampanchayatValuesFromDB(levelFive, "name", String.valueOf(levelBeen2.getId()), slugName);
        CustomSpinnerAdapter dataAdapter3 = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, grampanchayatList);
        grampanchayatSpinner.setAdapter(dataAdapter3);
        List<LevelBeen> preSelectedList = addBeneficiaryUtils.setHouseholdGramapanchayat(leastLocationId, selectedSlugName);
        for (int j = 0; j < grampanchayatList.size(); j++) {
            if (!preSelectedList.isEmpty() && preSelectedList.get(0).getName().contains(grampanchayatList.get(j).getName())) {
                grampanchayatSpinner.setSelection(grampanchayatList.indexOf(grampanchayatList.get(j)));
                break;
            }
        }
    }

    /**
     *  @param talukSpinner
     * @param leastLocationId
     * @param levelBeen1
     * @param slugName
     * @param selectedSlugName
     */
    public void setEdittedDistrictSpinner(String levelFour,Spinner talukSpinner, int leastLocationId, LevelBeen levelBeen1, String slugName, String selectedSlugName,AddBeneficiaryUtils addBeneficiaryUtils) {
        List<LevelBeen> talukList = dbhelper.getLevelTalukValuesFromDB(levelFour, "name", String.valueOf(levelBeen1.getId()), slugName);
        CustomSpinnerAdapter dataAdapter3 = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, talukList);
        talukSpinner.setAdapter(dataAdapter3);
        addBeneficiaryUtils.setUpdatedDistrictAdapter(talukSpinner, talukList, leastLocationId, selectedSlugName,sharedPreferences1.getBoolean(Constants.EDITSECONDARY_ADDRESS_FLAG,false));
    }

    /**
     *  @param villageSpinner
     * @param slugName
     * @param selectedSlugName
     * @param levelBeen4
     * @param leastLocationId
     */
    public void setEdittedGrampanchayatSpinner(String levelSix, Spinner villageSpinner, String slugName, String selectedSlugName, LevelBeen levelBeen4, int leastLocationId, AddBeneficiaryUtils addBeneficiaryUtils) {
        List<LevelBeen> vilageList = dbhelper.getLevelVillageValuesFromDB(levelSix, "name", String.valueOf(levelBeen4.getId()), slugName);
        CustomSpinnerAdapter dataAdapter3 = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, vilageList);
        villageSpinner.setAdapter(dataAdapter3);
        List<LevelBeen> preSelectedList = addBeneficiaryUtils.setHouseholdVillage(leastLocationId, selectedSlugName);
        for (int j = 0; j < vilageList.size(); j++) {
            if (!preSelectedList.isEmpty() && preSelectedList.get(0).getName().contains(vilageList.get(j).getName())) {
                villageSpinner.setSelection(vilageList.indexOf(vilageList.get(j)));
                break;
            }
        }
    }

    /**
     *  @param slugName
     * @param hamletDynamicSpinner
     * @param levelBeen5
     * @param leastLocationId
     */
    public void setEdittedVillageSpinner(String levelSeven,String slugName,String selectedSlugName, Spinner hamletDynamicSpinner, LevelBeen levelBeen5, int leastLocationId,AddBeneficiaryUtils addBeneficiaryUtils) {
        List<LevelBeen> hamletList = dbhelper.getLevelHamletDetails(levelSeven, "name", String.valueOf(levelBeen5.getId()), slugName);
        CustomSpinnerAdapter dataAdapter3 = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, hamletList);
        hamletDynamicSpinner.setAdapter(dataAdapter3);
        List<LevelBeen> preSelectedList = addBeneficiaryUtils.setHouseholdHamlet(leastLocationId, selectedSlugName);
        for (int j = 0; j < hamletList.size(); j++) {
            if (!preSelectedList.isEmpty() && preSelectedList.get(0).getName().contains(hamletList.get(j).getName())) {
                hamletDynamicSpinner.setSelection(hamletList.indexOf(hamletList.get(j)));
            }
        }
    }

    /**
     * method to get the values of multiple contact number fields
     *
     * @param addBeneficiaryActivityNew
     * @param contactList
     * @param secondaryContactLayout
     */
    public List<String> getDynamicContactValues(Activity addBeneficiaryActivityNew, List<String> contactList, LinearLayout secondaryContactLayout) {
        Boolean isContactValid= true;
        try {
            int dynamicContactChildCount = secondaryContactLayout.getChildCount();
            if (dynamicContactChildCount > 0) {
                for (int contact = 0; contact < secondaryContactLayout.getChildCount(); contact++) {
                    View v = secondaryContactLayout.getChildAt(contact);
                    EditText editText = v.findViewById(R.id.contactNoEdittext);
                    if (editText.getText().toString().trim().length() < 10) {
                        Toast.makeText(addBeneficiaryActivityNew, "Please enter 10 digit mobile number", Toast.LENGTH_SHORT).show();
                        isContactValid = false;
                    } else if ((!StringUtils.isValidMobileNumber(editText.getText().toString().trim()))) {
                        Toast.makeText(addBeneficiaryActivityNew, "Please enter valid mobile number", Toast.LENGTH_SHORT).show();
                        isContactValid = false;
                    } else {
                        contactList.add(editText.getText().toString().trim());
                        Logger.logV(TAG, "list of contact number" + contactList.toString());
                    }
                }
            }
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
        //Modify by guru
        if (!isContactValid)
            contactList.clear();
        return contactList;
    }

    public void setAddressProofSpinner(List<LocationType> addressProofTypes, Spinner addressProofSpinner) {
        LocationSpinnerAdapter spinnerAdapter = new LocationSpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, addressProofTypes);
        addressProofSpinner.setAdapter(spinnerAdapter);
    }

    public JSONObject getAddressJsonFrom(HashMap<Integer, List<Address>> addressListHashMap) {
        JSONObject mainAddressJsonObject=new JSONObject();
        int count =0;
        for (int i=0;i<addressListHashMap.size();i++)
        {

            try {
                for(int j=0;j< addressListHashMap.get(i).size();j++) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("address1", addressListHashMap.get(i).get(j).getAddress1());
                    jsonObject.put("address2", addressListHashMap.get(i).get(j).getAddress2());
                    jsonObject.put("pincode", addressListHashMap.get(i).get(j).getPincode());
                    jsonObject.put("location_level", addressListHashMap.get(i).get(j).getLocationLevel());
                    jsonObject.put("proof_id", addressListHashMap.get(i).get(j).getProofId());
                    jsonObject.put("primary", addressListHashMap.get(i).get(j).getPrimary());
                    jsonObject.put("boundary_id", addressListHashMap.get(i).get(j).getBoundaryId());
                    jsonObject.put("least_location_name", addressListHashMap.get(i).get(j).getLeastLocationName());
                    mainAddressJsonObject.put("address_" + count + "", jsonObject);
                    count++;

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mainAddressJsonObject;
    }
}