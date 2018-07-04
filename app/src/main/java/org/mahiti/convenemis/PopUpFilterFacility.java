package org.mahiti.convenemis;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import org.mahiti.convenemis.BeenClass.SurveysBean;
import org.mahiti.convenemis.BeenClass.beneficiary.Datum;
import org.mahiti.convenemis.BeenClass.boundarylevel.Boundary;
import org.mahiti.convenemis.BeenClass.boundarylevel.LocationType;
import org.mahiti.convenemis.BeenClass.parentChild.LevelBeen;
import org.mahiti.convenemis.adapter.BoundarySpinnerAdapter;
import org.mahiti.convenemis.adapter.LocationSpinnerAdapter;
import org.mahiti.convenemis.adapter.spinnercustomadapter.CustomSpinnerAdapter;
import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.network.UpdateFilterInterface;
import org.mahiti.convenemis.utils.AddBeneficiaryUtils;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.Utils;

import java.util.ArrayList;
import java.util.List;



public class PopUpFilterFacility {
    private static final String TAG = "PopUpFilterFacility";
    private static final String RECORD_FOUND = ", RECORD FOUND";
    private static final String LOCATION_TYPE_KEY="LOCATION_LEVELTYPE_UID";
    private static final String LOCATION_BASED_KEY="Location based filter";
    private static final String SURVEY_BASED_KEY="Survey based filter";
    private static final String LOCATION_TYPE_PREF_KEY="locationTypeFacility";
    private  String slugName;
    private  List<String> surveyIds= new ArrayList<>();
    private ExternalDbOpenHelper finalDbOpenHelper;
    private SharedPreferences sharedPreferences;
    private LinearLayout surveyNameCheckDymanic;
    private Spinner stateFilter;
    private Spinner spFacilityBoundaryFilter;
    private Spinner locationLevelFilter;
    private Spinner preSelectedSpinner;
    private LinearLayout facilitycontainerLabel;
    private ScrollView surveyListDynamicLabel;
    private LinearLayout filterContainerLabel;
    private RadioButton pendingRadioButtonLable;
    private RadioButton completedRadiobuttonLable;
    private List<String> globalDataCollectionForms = new ArrayList<>();
    private List<org.mahiti.convenemis.BeenClass.facilities.Datum> sortedSurveyIdsTemp= new ArrayList<>();
    private Integer selectedVallageID = 0;
    private RadioButton allRadiobuttonLable;

    /**
     *
     * @param activity
     * @param dbOpenHelper
     * @param typeValue
     *
     */
    public  void showingErrorPopUp(final ListingActivity activity, final ExternalDbOpenHelper dbOpenHelper, final String typeValue) {
        List<Datum> listArray2 = new ArrayList<>();
        final List<LevelBeen> boundarylevelList=new ArrayList<>();
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.popup_filter_facility);
        final UpdateFilterInterface updatefilterinterface;
       // updatefilterinterface=activity;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String modifiedDate=dbOpenHelper.getFacilityLastModifiedDate(typeValue,"");
        final List<Datum> getVillagelist= dbOpenHelper.getBeneficiaryListFromDb(typeValue,5,modifiedDate);
        Logger.logD(TAG, "Village_name "+getVillagelist.toString()+"typeValue--->"+typeValue);
        surveyNameCheckDymanic = dialog.findViewById(R.id.checkboxDynamic);
        facilitycontainerLabel = dialog.findViewById(R.id.facilitycontainer);
        filterContainerLabel = dialog.findViewById(R.id.filterContainer);
        pendingRadioButtonLable = dialog.findViewById(R.id.pedingRadiobutton);
        pendingRadioButtonLable.setText(R.string.pending_facility_label);
        completedRadiobuttonLable = dialog.findViewById(R.id.completedRadiobutton);
        completedRadiobuttonLable.setText(R.string.completed_facility_label);

        allRadiobuttonLable = dialog.findViewById(R.id.allRadiobutton);
        allRadiobuttonLable.setText("All");
        Button sortFilter = dialog.findViewById(R.id.sortList);
        Button resetFilter = dialog.findViewById(R.id.reset);
        locationLevelFilter = dialog.findViewById(R.id.spLocationLevel);
        stateFilter = dialog.findViewById(R.id.spFacilityState);
        spFacilityBoundaryFilter = dialog.findViewById(R.id.spFacilityDistricts);
        preSelectedSpinner = dialog.findViewById(R.id.prefilterspinnerFilter);
        surveyListDynamicLabel = dialog.findViewById(R.id.surveyListDynamic);
        setPreSelectionSpinnerValues(activity);
        setLocationLevelAdapter(sharedPreferences.getString(LOCATION_TYPE_KEY,""),activity, locationLevelFilter);

        dialog.show();
        listArray2.clear();
        allRadiobuttonLable.setChecked(true);

        finalDbOpenHelper  = dbOpenHelper;
        setDynamicCheckboxForSurvey(activity,typeValue);
        locationLevelFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setLocationLevelFacility(parent,activity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Logger.logD(TAG,"onNothingSelected on location level dropdown");
            }
        });
        stateFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Boundary boundaryLevelList=(Boundary)parent.getSelectedItem();
                if(!(Constants.SELECT).equals(boundaryLevelList.getName())) {
                    setBoundaryLevelAdapter(boundaryLevelList.getLevel(),slugName,dbOpenHelper,activity, spFacilityBoundaryFilter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Logger.logD(TAG,"onNothingSelected");
            }
        } );
        spFacilityBoundaryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setLocationFilter(spFacilityBoundaryFilter,activity,boundarylevelList,parent);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Logger.logD(TAG,"onNothingSelected spFacilityBoundaryFilter");

            }
        });

        preSelectedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               setPreselectedSpinner(adapterView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Logger.logD(TAG,"onNothingSelected on preSelectedSpinner");
            }
        });
        resetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completedRadiobuttonLable.setChecked(false);
                pendingRadioButtonLable.setChecked(false);

                getDataCollectionForm(activity);
            }
        });

       /* sortFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.logD(TAG,"sortFilter on Click-->"+surveyIds.toString());
                checkSurveyStatus(activity,updatefilterinterface,dialog,typeValue);
            }
        });*/
    }


    /**
     * method to set the value to prefernce based on previously selected dropdown
     * @param adapterView
     */
    private void setPreselectedSpinner(AdapterView<?> adapterView) {
        switch (adapterView.getSelectedItem().toString()){
            case LOCATION_BASED_KEY:
                enableSelectedView(1);
                SharedPreferences.Editor editor1= sharedPreferences.edit();
                editor1.putString(LOCATION_TYPE_PREF_KEY,LOCATION_BASED_KEY);
                editor1.apply();
                break;
            case SURVEY_BASED_KEY:
                enableSelectedView(2);
                SharedPreferences.Editor editor2= sharedPreferences.edit();
                editor2.putString(LOCATION_TYPE_PREF_KEY,SURVEY_BASED_KEY);
                editor2.apply();
                break;
            default:
                break;

        }
    }

    private void setLocationFilter(Spinner spFacilityDistrictsFilter, ListingActivity activity, List<LevelBeen> boundarylevelList, AdapterView<?> parent) {
        LevelBeen boundaryLevelList1=(LevelBeen)parent.getSelectedItem();
        if(Constants.SELECT.equals(boundaryLevelList1.getName())){
            CustomSpinnerAdapter adapter=new CustomSpinnerAdapter(activity,android.R.layout.simple_spinner_dropdown_item,boundarylevelList);
            spFacilityDistrictsFilter.setAdapter(adapter);
            try{
                LevelBeen locationType=(LevelBeen)spFacilityDistrictsFilter.getSelectedItem();
                Logger.logD(TAG, "locationType-->locationType "+locationType.getId());
                Logger.logD(TAG, "locationType-->locationType "+locationType.getName());
                selectedVallageID=locationType.getId();
            }catch (Exception e){
                Logger.logE(TAG,"exception in the village",e);
            }

        }
    }

    /**
     *
     * @param activity
     * @param updatefilterinterface
     * @param dialog
     * @param typeValue
     */
    private void checkSurveyStatus(ListingActivity activity, UpdateFilterInterface updatefilterinterface, Dialog dialog, String typeValue) {
        if (pendingRadioButtonLable.isChecked()) {
            getCompletedSurveyRecords(selectedVallageID, 2, typeValue, activity);
        }
        else if (completedRadiobuttonLable.isChecked())
            getCompletedSurveyRecords(selectedVallageID, 1,typeValue, activity);
        else if (allRadiobuttonLable.isChecked())
            getCompletedSurveyRecords(selectedVallageID, 3,typeValue, activity);

        updatefilterinterface.UpdateFilterAdapterFacility(sortedSurveyIdsTemp);
        dialog.dismiss();


    }


    /**
     *
     * @param villageId
     * @param completedStatus
     * @param typeValue
     * @param activity
     */
    private void getCompletedSurveyRecords(int villageId, int completedStatus, String typeValue, ListingActivity activity) {
        String dataFormMainQuery=ExternalDbOpenHelper.SELECT_QUERY_FACILITY_JOIN;
        if(villageId!=0){
            dataFormMainQuery = dataFormMainQuery + " b.least_location_id="+selectedVallageID+" AND  ";
        }
        String benFacCompletedQuery = "";
        switch(completedStatus){
            case 1:
                benFacCompletedQuery = "";
                dataFormMainQuery = dataFormMainQuery + benFacCompletedQuery+"b.facility_type_id="+typeValue +" AND";
                break;
            case 2:
                benFacCompletedQuery = " p.faci_uuid IS NULL AND  ";
                dataFormMainQuery = dataFormMainQuery + benFacCompletedQuery+"b.facility_type_id="+typeValue;
                break;

            default:
                break;

        }

        String subQueryPeriodicity= "";
        List<String> tempSelectedSurveyIds = surveyIds;
        if(surveyIds.isEmpty()){
            tempSelectedSurveyIds = globalDataCollectionForms;
        }

        for(int i=0; completedStatus!=2 && i<tempSelectedSurveyIds.size();i++) {
            String tempQuery = "";
            if(i==0)
                tempQuery =  "((p.survey_Id = "+tempSelectedSurveyIds.get(i) + " AND ";
            else
                tempQuery = " OR (p.survey_Id = "+tempSelectedSurveyIds.get(i) + " AND ";

            subQueryPeriodicity = subQueryPeriodicity + tempQuery + finalDbOpenHelper.getPeriodicQuery(finalDbOpenHelper.getperiodicity(tempSelectedSurveyIds.get(i)))+")";
            Logger.logD(TAG,"getCompletedSurveyRecords subQueryPeriodicity -- "+subQueryPeriodicity);
        }
        if (completedStatus !=2){
            sortedSurveyIdsTemp = finalDbOpenHelper.getFilteredSurveyIdFacility(dataFormMainQuery + subQueryPeriodicity + ")");
        }else{
            sortedSurveyIdsTemp = finalDbOpenHelper.getFilteredSurveyIdFacility(dataFormMainQuery + subQueryPeriodicity );
        }
        Toast.makeText(activity, +sortedSurveyIdsTemp.size()+RECORD_FOUND,
                Toast.LENGTH_LONG).show();
    }

    /**
     * method to clear the Check box and also clear the Shared preference .
     * @param activity
     */
    private void getDataCollectionForm(ListingActivity activity) {
        setLocationLevelAdapter(sharedPreferences.getString(LOCATION_TYPE_KEY,""),activity, locationLevelFilter);
        int dynamicLinearChildCount=surveyNameCheckDymanic.getChildCount();
        if(dynamicLinearChildCount>0){
            try{
                for(int address=0;address<dynamicLinearChildCount;address++){
                    CheckBox checkBox=(CheckBox)surveyNameCheckDymanic.getChildAt(address);
                    checkBox.setChecked(false);
                }
            }catch (Exception e){
                Logger.logE(TAG,"",e);
            }

        }
    }



    /**
     *
     * @param type
     * @return
     */
    private String setLocationSpinnerSelection(int type) {
        String tempString="";
        if (!"".equalsIgnoreCase(sharedPreferences.getString("FilterFacilityLocationBased", ""))){
            String getSelectedSpinnerValues=sharedPreferences.getString("FilterFacilityLocationBased","");
            String[] getValues=getSelectedSpinnerValues.split("@");
            String getLevels= getValues[2];
            String getState= getValues[1];
            String getDistrict= getValues[0];
            Logger.logD(TAG, "getLevels "+getLevels+"getState "+getState+"getDistrict "+getDistrict);
            if (type==1)
                tempString= getDistrict;
            else  if (type==2)
                tempString=  getState;
            else if (type==3)
                tempString= getLevels;

        }
        return tempString;
    }

    /**
     *
     * @param locationType if 1- location based , 2- survey based.
     */
    private void enableSelectedView(int locationType) {
        if (locationType==1) {
            facilitycontainerLabel.setVisibility(View.VISIBLE);
            surveyNameCheckDymanic.setVisibility(View.GONE);
            surveyListDynamicLabel.setVisibility(View.GONE);
            filterContainerLabel.setVisibility(View.VISIBLE);
        }
        if (locationType==2){
            facilitycontainerLabel.setVisibility(View.GONE);
            surveyNameCheckDymanic.setVisibility(View.VISIBLE);
            surveyListDynamicLabel.setVisibility(View.VISIBLE);
            filterContainerLabel.setVisibility(View.VISIBLE);
        }


    }

    /**
     * method to set the preSelection spinner
     * @param activity  Filter
     */
    private void setPreSelectionSpinnerValues(ListingActivity activity) {
        List<String> selectionList= new ArrayList<>();
        selectionList.add(LOCATION_BASED_KEY);
        selectionList.add(SURVEY_BASED_KEY);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, selectionList);
        preSelectedSpinner.setAdapter(adapter2);
        if (sharedPreferences.getString(LOCATION_TYPE_PREF_KEY,LOCATION_BASED_KEY).equalsIgnoreCase(LOCATION_BASED_KEY)){
            preSelectedSpinner.setSelection(selectionList.indexOf(LOCATION_BASED_KEY));
        }else{
            preSelectedSpinner.setSelection(selectionList.indexOf(SURVEY_BASED_KEY));
        }
    }

    private void setLocationLevelFacility(AdapterView<?> parent, ListingActivity activity) {
        LocationType locationType = (LocationType) parent.getSelectedItem();
        slugName = locationType.getSlug();
        if(!(Constants.SELECT).equalsIgnoreCase(locationType.getName())){
            AddBeneficiaryUtils.setStateAdapter(sharedPreferences.getString(LOCATION_TYPE_KEY,""),slugName,activity, stateFilter);

        }else {
            List<Boundary> list = new ArrayList<>();
            Boundary boundary = new Boundary();
            boundary.setName(Constants.SELECT);
            list.add(boundary);
            // attaching data adapter to spinner
            BoundarySpinnerAdapter adapter1 = new BoundarySpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, list);
            stateFilter.setAdapter(adapter1);
            List<LevelBeen> levelBeenList = new ArrayList<>();
            LevelBeen levelBeen = new LevelBeen();
            levelBeen.setName(Constants.SELECT);
            levelBeenList.add(levelBeen);
            CustomSpinnerAdapter adapter2 = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, levelBeenList);
            spFacilityBoundaryFilter.setAdapter(adapter2);
        }

    }

    /**
     * this method will create the survey list in the check Box
     *
     * @param activity
     * @param facilityTypeId
     */
    private void setDynamicCheckboxForSurvey(ListingActivity activity, String facilityTypeId) {
        List<SurveysBean>  surveyList;
        surveyList = finalDbOpenHelper.getFacilityIdsNew(facilityTypeId);
        surveyNameCheckDymanic.removeAllViews();
        surveyIds.clear();
        globalDataCollectionForms.clear();

        for (int j = 0; j < surveyList.size(); j++) {
            Logger.logD(TAG, "surveyList "+surveyList.get(j).getSurveyName());
            CheckBox Surveycheckbox= new CheckBox(activity);
            Surveycheckbox.setText(String.valueOf(surveyList.get(j).getSurveyName()));
            Surveycheckbox.setTag(surveyList.get(j).getId());
            String getPreviousFilterNames=sharedPreferences.getString("FilterFacilitySelectedSurvey","");
            if (!"".equals(getPreviousFilterNames)){
                String removeBraceLeft=getPreviousFilterNames.replace("[","");
                String removeBraceRight=removeBraceLeft.replace("]","");
                String[] splitSurveyIds= removeBraceRight.split(",");
                if (!("").equals(getPreviousFilterNames) && getPreviousFilterNames.length()>0){
                    for (int k=0;k<splitSurveyIds.length;k++){
                        Logger.logD(TAG, "getPreviousFilterNames "+removeBraceRight);
                        Logger.logD(TAG, "getPreviousFilterNames-->Surveycheckbox.getTag() "+Surveycheckbox.getTag());
                        if (Surveycheckbox.getTag().toString().equalsIgnoreCase(splitSurveyIds[k].trim())) {
                            Logger.logD(TAG, "getPreviousFilterNames-->Surveycheckbox.getTag() "+Surveycheckbox.getTag());
                            Surveycheckbox.setChecked(true);
                        }
                    }
                }

            }
            globalDataCollectionForms.add(String.valueOf(surveyList.get(j).getId()));
            surveyNameCheckDymanic.addView(Surveycheckbox);
            Surveycheckbox.setOnCheckedChangeListener(handleCheck(Surveycheckbox));
        }
    }

    /**
     *
     * @param boundaryString
     * @param slugName
     * @param dbOpenHelper
     * @param activity
     * @param spFacilityDistrictsFilter
     */
    private static void setBoundaryLevelAdapter(Integer boundaryString, String slugName, ExternalDbOpenHelper dbOpenHelper, Activity activity, Spinner spFacilityDistrictsFilter) {
        try {
            List<LevelBeen>  boundarylevelList=dbOpenHelper.getBoundaryListFromDB(boundaryString,slugName);
            // attaching data adapter to spinner
            CustomSpinnerAdapter adapter=new CustomSpinnerAdapter(activity,android.R.layout.simple_spinner_dropdown_item,boundarylevelList);
            spFacilityDistrictsFilter.setAdapter(adapter);
        }catch (Exception e){
            Logger.logE("","",e);
        }
    }


    /**
     *setting the state spinner in based on
     * @param locationLevelString
     * @param activity
     * @param spLocationLevelFilter
     */
    private  void setLocationLevelAdapter(String locationLevelString, Activity activity, Spinner spLocationLevelFilter) {
        List<LocationType>locationList;
        locationList = Utils.getLocationList(locationLevelString);
        // attaching data adapter to spinner
        LocationSpinnerAdapter spinnerAdapter=new LocationSpinnerAdapter(activity,android.R.layout.simple_spinner_dropdown_item,locationList);
        spLocationLevelFilter.setAdapter(spinnerAdapter);
        setLocationSpinnerSelection(spLocationLevelFilter);
    }

    /**
     * set the spinner if already selected from the shared preference .
     * @param spLocationLevelFilter
     *
     */
    private  void setLocationSpinnerSelection(Spinner spLocationLevelFilter) {
        spFacilityBoundaryFilter.setPrompt(setLocationSpinnerSelection(1));
        stateFilter.setPrompt(setLocationSpinnerSelection(2));
        spLocationLevelFilter.setPrompt(setLocationSpinnerSelection(3));
    }




    /**
     *
     * @param chk
     * @return
     */
    private  CheckBox.OnCheckedChangeListener handleCheck(final CheckBox chk)
    {
        return new CheckBox.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    surveyIds.remove(String.valueOf(chk.getTag()));
                    Logger.logD(TAG,"Deleted-->"+surveyIds.toString());
                }
                else
                {
                    surveyIds.add(String.valueOf(chk.getTag()));
                    Logger.logD(TAG,"added-->"+surveyIds.toString());
                }
            }
        };
    }

}