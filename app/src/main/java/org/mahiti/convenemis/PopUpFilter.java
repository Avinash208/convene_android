package org.mahiti.convenemis;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
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
import android.widget.TextView;

import org.mahiti.convenemis.BeenClass.SurveysBean;
import org.mahiti.convenemis.BeenClass.beneficiary.Datum;
import org.mahiti.convenemis.BeenClass.boundarylevel.Boundary;
import org.mahiti.convenemis.BeenClass.boundarylevel.LocationType;
import org.mahiti.convenemis.BeenClass.parentChild.LevelBeen;
import org.mahiti.convenemis.adapter.LocationSpinnerAdapter;
import org.mahiti.convenemis.adapter.spinnercustomadapter.CustomSpinnerAdapter;
import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.network.UpdateFilterInterface;
import org.mahiti.convenemis.utils.AddBeneficiaryUtils;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.ToastUtils;
import org.mahiti.convenemis.utils.Utils;
import org.mahiti.convenemis.utils.multispinner.SingleSpinnerSearch;

import java.util.ArrayList;
import java.util.List;


class PopUpFilter {
    private static final String TAG = "PopUpFilter";
    private static final String LOCATIONTYPE = "locationType";
    private List<Datum> listArray2 = new ArrayList<>();
    private List<String> surveyIds= new ArrayList<>();
    private List<Datum> sortedSurveyIdsTemp= new ArrayList<>();
    private static final String RECORD_FOUND=", RECORD FOUND ";
    private static final String LOCATIONBASEDFILTER="Location based filter";
    private static final String SURVEY_BASED_FILTER="Survey based filter";
    private static final String LOCATION_TYPE_KEY="LOCATION_LEVELTYPE_UID";
    private RadioButton pedingRadiobuttonLable;
    private RadioButton completedRadiobuttonLable;
    private  String slugName;

   private SingleSpinnerSearch searchSingleSpinner;
    private LinearLayout surveyNameCheckDymanic;
    private LinearLayout muiltiplespinnercontainerLable;
    private Button sortFilter;
    private Button resetFilter;
    private SharedPreferences preferences;
    private ExternalDbOpenHelper finalDbOpenHelper;
    private Spinner preSelectedSpinner;
    private ScrollView checkboxDymamicLayout;
    private List<String> globalDataCollectionForms = new ArrayList<>();
    private RadioButton allRadiobuttonLable;
    private Spinner spFacilityDistrictsFilter;
    private Spinner locationLevelFilter;
    private Spinner spFacilityStateFilter;
    private String getTableName="";
    private int getleastLocationId=0;
    private String getSelectedLocationType="";


    public void showingErrorPopUp(final ListingActivity activity, final ExternalDbOpenHelper dbOpenHelper, final String typeValue) {

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.popup_filter);
        final UpdateFilterInterface updatefilterinterface;
        //updatefilterinterface=activity;
        int width = (int)(activity.getResources().getDisplayMetrics().widthPixels*0.90);
        int height = (int)(activity.getResources().getDisplayMetrics().heightPixels*0.80);
        preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String modifiedDate=dbOpenHelper.getBeneficiaryLastModifiedDate(typeValue,"");
        final List<Datum> getVillagelist= dbOpenHelper.getBeneficiaryListFromDb(typeValue,5,modifiedDate);
        Logger.logD(TAG, "Village_name "+getVillagelist.toString()+"typeValue--->"+typeValue);
        initVariables(dialog);
        dialog.getWindow().setLayout(width, height);
        dialog.show();
        listArray2.clear();
        setPreSelectionSpinnerValues(activity);
        setLocationLevelAdapter(preferences.getString(LOCATION_TYPE_KEY,""),activity, locationLevelFilter);

        for (int i = 0; i < getVillagelist.size(); i++) {
            Datum h = new Datum();
            Logger.logD(TAG, "Village_id "+getVillagelist.get(i).getAddress().get(0).getBoundaryId());
            h.setId(getVillagelist.get(i).getAddress().get(0).getBoundaryId());
            h.setName(getVillagelist.get(i).getAddress().get(0).getLeastLocationName());
            Logger.logD(TAG, "Village_Name "+getVillagelist.get(i).getAddress().get(0).getLeastLocationName());
            h.setSelected(false);
            listArray2.add(h);
        }
        this.finalDbOpenHelper = dbOpenHelper;
        setDynamicCheckBoxSurvey(activity,typeValue);
        setRadioType();
        searchSingleSpinner.setItems(listArray2, -1, items -> {
            surveyIds.clear();
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).isSelected()) {
                    Log.i(TAG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                }
            }
        });

        preSelectedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               setPreSelectedSpinner(adapterView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Logger.logD(TAG,"sortFilter on Click-->");
            }
        });
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

        spFacilityStateFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Boundary boundaryLevelList=(Boundary)parent.getSelectedItem();
                if (boundaryLevelList!=null) {
                    getTableName = "level" + boundaryLevelList.getLevel() + "_id";
                    if (!(Constants.SELECT).equals(boundaryLevelList.getName())) {
                        setBoundaryLevelAdapter(boundaryLevelList.getLevel(), getSelectedLocationType, dbOpenHelper, activity, spFacilityDistrictsFilter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Logger.logD(TAG,"onNothingSelected spFacilityStateFilter");
            }
        } );
        spFacilityDistrictsFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LevelBeen boundaryLevelList1=(LevelBeen)parent.getSelectedItem();
                Logger.logD(TAG,"spFacilityDistrictsFilter Name"+boundaryLevelList1.getName());
                Logger.logD(TAG,"spFacilityDistrictsFilter id"+boundaryLevelList1.getId());
                getleastLocationId=boundaryLevelList1.getId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Logger.logD(TAG,"onNothingSelected");

            }
        });


      /*  sortFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSurveyStatus(activity,updatefilterinterface,dialog,typeValue);
            }
        });*/
        resetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pedingRadiobuttonLable.setChecked(false);
                completedRadiobuttonLable.setChecked(false);
                allRadiobuttonLable.setChecked(true);
                searchSingleSpinner.setPrompt("");

                getDataCollectionForm();
            }
        });
    }


    /**
     *
     * @param adapterView
     */
    private void setPreSelectedSpinner(AdapterView<?> adapterView) {
        switch (adapterView.getSelectedItem().toString()){
            case LOCATIONBASEDFILTER:
                enableSelectedView(1);
                SharedPreferences.Editor editor1= preferences.edit();
                editor1.putString(LOCATIONTYPE,LOCATIONBASEDFILTER);
                editor1.apply();
                break;
            case SURVEY_BASED_FILTER:
                enableSelectedView(2);
                SharedPreferences.Editor editor2= preferences.edit();
                editor2.putString(LOCATIONTYPE,SURVEY_BASED_FILTER);
                editor2.apply();
                break;
            default:
                break;

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
            List<LevelBeen>  boundarylevelList=dbOpenHelper.getBeneficiaryBoundaryListFromDB(boundaryString,slugName);
            CustomSpinnerAdapter adapter=new CustomSpinnerAdapter(activity,android.R.layout.simple_spinner_dropdown_item,boundarylevelList);
            spFacilityDistrictsFilter.setAdapter(adapter);
        }catch (Exception e){
            Logger.logE(TAG,"",e);
        }
    }

    private void setLocationLevelFacility(AdapterView<?> parent, ListingActivity activity) {
        LocationType locationType = (LocationType) parent.getSelectedItem();
        slugName = locationType.getSlug();
        getSelectedLocationType=locationType.getName();
        if(!(Constants.SELECT).equalsIgnoreCase(locationType.getName())){
            AddBeneficiaryUtils.setStateAdapter(preferences.getString(LOCATION_TYPE_KEY,""),slugName,activity,spFacilityStateFilter);
        }else {
            List<Boundary> list = new ArrayList<>();
            Boundary boundary = new Boundary();
            boundary.setName(Constants.SELECT);
            list.add(boundary);
            List<LevelBeen> levelBeenList = new ArrayList<>();
            LevelBeen levelBeen = new LevelBeen();
            levelBeen.setName(Constants.SELECT);
            levelBeenList.add(levelBeen);
            CustomSpinnerAdapter adapter2 = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, levelBeenList);
            spFacilityDistrictsFilter.setAdapter(adapter2);
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
        LocationSpinnerAdapter spinnerAdapter=new LocationSpinnerAdapter(activity,android.R.layout.simple_spinner_dropdown_item,locationList);
        spLocationLevelFilter.setAdapter(spinnerAdapter);
    }


    /**
     *
     */
    private void setRadioType() {
        allRadiobuttonLable.setChecked(true);
    }

    /**
     *
     * @param locationType if 1- location based , 2- survey based.
     */
    private void enableSelectedView(int locationType) {
        if (locationType==1) {
            checkboxDymamicLayout.setVisibility(View.GONE);
            pedingRadiobuttonLable.setText(R.string.pending_label);
            completedRadiobuttonLable.setText(R.string.completed_beneficiary);
            muiltiplespinnercontainerLable.setVisibility(View.VISIBLE);
        }else if (locationType==2){
            checkboxDymamicLayout.setVisibility(View.VISIBLE);
            muiltiplespinnercontainerLable.setVisibility(View.GONE);
        }else{
            checkboxDymamicLayout.setVisibility(View.GONE);
            muiltiplespinnercontainerLable.setVisibility(View.GONE);
        }


    }

    /**
     * method to set the preSelection spinner
     * @param activity  Filter
     */
    private void setPreSelectionSpinnerValues(ListingActivity activity) {
        List<String> selectionList= new ArrayList<>();
        selectionList.add(LOCATIONBASEDFILTER);
        selectionList.add(SURVEY_BASED_FILTER);
        ArrayAdapter adapter2 = new ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, selectionList);
        preSelectedSpinner.setAdapter(adapter2);
        if (preferences.getString(LOCATIONTYPE,LOCATIONBASEDFILTER).equalsIgnoreCase(LOCATIONBASEDFILTER)){
            preSelectedSpinner.setSelection(selectionList.indexOf(LOCATIONBASEDFILTER));
        }else{
            preSelectedSpinner.setSelection(selectionList.indexOf(SURVEY_BASED_FILTER));
        }
    }

    /**
     *
     * @param selectedType
     * @param surveyIds
     */
    private void saveInPreference(boolean selectedType, List<String> surveyIds) {
        SharedPreferences.Editor editor= preferences.edit();
        editor.putBoolean("FilterSelectedType",selectedType);
        editor.putString("FilterSelectedSurvey",surveyIds.toString());
        editor.apply();
    }

    /**
     * method to clear the Check box and also clear the Shared preference .
     */
    private void getDataCollectionForm() {
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
     *  @param activity
     * @param beneficiaryTypeID
     */
    private void setDynamicCheckBoxSurvey(ListingActivity activity, String beneficiaryTypeID) {
        List<SurveysBean>  surveyList;
        surveyList = finalDbOpenHelper.getBeneficiaryIdFromSurveyNEW(beneficiaryTypeID);
        surveyNameCheckDymanic.removeAllViews();
        surveyIds.clear();
        globalDataCollectionForms.clear();

        for (int j = 0; j < surveyList.size(); j++) {
            CheckBox surveyCheckBox= new CheckBox(activity);
            surveyCheckBox.setText(String.valueOf(surveyList.get(j).getSurveyName()));
            surveyCheckBox.setTag(surveyList.get(j).getId());
            String getPreviousFilterNames=preferences.getString("FilterSelectedSurvey","");
            if (!("").equals(getPreviousFilterNames)){
                getCheckBoxChecked(surveyCheckBox, getPreviousFilterNames);
            }
            globalDataCollectionForms.add(String.valueOf(surveyList.get(j).getId()));
            surveyNameCheckDymanic.addView(surveyCheckBox);
            surveyCheckBox.setOnCheckedChangeListener(handleCheck(surveyCheckBox));
        }
    }

    /**
     *
     * @param surveycheckbox
     * @param getPreviousFilterNames
     */
    private void getCheckBoxChecked(CheckBox surveycheckbox, String getPreviousFilterNames){
        String removeBraceLeft=getPreviousFilterNames.replace("[","");
        String removeBraceRight=removeBraceLeft.replace("]","");
        String[] splitSurveyIds= removeBraceRight.split(",");
        if (splitSurveyIds.length>1 || !"".equalsIgnoreCase(removeBraceLeft)) {
            for (int k = 0; k < splitSurveyIds.length; k++) {
                Logger.logD(TAG, "getPreviousFilterNames " + removeBraceRight);
                Logger.logD(TAG, "getPreviousFilterNames-->Surveycheckbox.getTag() " + surveycheckbox.getTag());
                if (surveycheckbox.getTag().toString().equalsIgnoreCase(splitSurveyIds[k].trim())) {
                    Logger.logD(TAG, "getPreviousFilterNames-->Surveycheckbox.getTag() " + surveycheckbox.getTag());
                    surveycheckbox.setChecked(true);
                }
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

        if (pedingRadiobuttonLable.isChecked())
            getCompletedSurveyRecords(2,typeValue,activity);
        else if (completedRadiobuttonLable.isChecked())
            getCompletedSurveyRecords(1,typeValue, activity);
        else if (allRadiobuttonLable.isChecked())
            getCompletedSurveyRecords(3,typeValue, activity);

        updatefilterinterface.UpdateFilterAdapter(sortedSurveyIdsTemp);
        dialog.dismiss();

    }

    /**
     *
     * @param completedStatusFlag
     * @param typeValue
     * @param activity
     */
    private void getCompletedSurveyRecords(int completedStatusFlag, String typeValue, ListingActivity activity) {
        String dataFormMainQuery=ExternalDbOpenHelper.SELECT_QUERY_BENEFICIARY_JOIN;
        if(getleastLocationId!=0){
            dataFormMainQuery = dataFormMainQuery + " b.least_location_id IN ( SELECT level7_id from  level7 where "+getTableName+"="+getleastLocationId+" AND  location_type= '"+getSelectedLocationType+"' ) AND  ";
        }
        String benFacCompletedQuery = "";
        switch(completedStatusFlag){
            case 1:
                benFacCompletedQuery = "";
                dataFormMainQuery = dataFormMainQuery + benFacCompletedQuery+"b.beneficiary_type_id="+typeValue +" AND";
                break;
            case 2:
                benFacCompletedQuery = " p.bene_uuid IS NULL AND   ";
                dataFormMainQuery = dataFormMainQuery + benFacCompletedQuery+"b.beneficiary_type_id="+typeValue;
                break;
            default:
        }

        String subQuery= "";
        List<String> tempSelectedSurveyIds = surveyIds;
        if(surveyIds.isEmpty()){
            tempSelectedSurveyIds = globalDataCollectionForms;
        }
        for(int i=0;completedStatusFlag!=2 && i<tempSelectedSurveyIds.size();i++) {
            String tempCommonQuery = "";
            if(i==0)
                tempCommonQuery =  "((p.survey_Id = "+tempSelectedSurveyIds.get(i) + " AND ";
            else
                tempCommonQuery = " OR (p.survey_Id = "+tempSelectedSurveyIds.get(i) + " AND ";
            subQuery = subQuery + tempCommonQuery + finalDbOpenHelper.getPeriodicQuery(finalDbOpenHelper.getperiodicity(tempSelectedSurveyIds.get(i)))+")";
            Logger.logD(TAG,"getCompletedSurveyRecords subQueryPeriodicity -- "+subQuery);
        }
        if (completedStatusFlag !=2){
            sortedSurveyIdsTemp = finalDbOpenHelper.getFilteredSurveyIdnew(dataFormMainQuery + subQuery + ")");
        }else{
            sortedSurveyIdsTemp = finalDbOpenHelper.getFilteredSurveyIdnew(dataFormMainQuery + subQuery );
        }
        ToastUtils.displayToast(sortedSurveyIdsTemp.size()+RECORD_FOUND, activity);
        saveInPreference(true,surveyIds);
    }


    /**
     *
     * @param dialog
     */
    private  void initVariables(Dialog dialog) {
        completedRadiobuttonLable = dialog.findViewById(R.id.completedRadiobutton);
        searchSingleSpinner = dialog.findViewById(R.id.searchSingleSpinner);
        surveyNameCheckDymanic = dialog.findViewById(R.id.checkboxDynamic);
        pedingRadiobuttonLable = dialog.findViewById(R.id.pedingRadiobutton);
        muiltiplespinnercontainerLable = dialog.findViewById(R.id.muiltiplespinnercontainer);
        completedRadiobuttonLable = dialog.findViewById(R.id.completedRadiobutton);
        allRadiobuttonLable = dialog.findViewById(R.id.allRadiobutton);
        sortFilter = dialog.findViewById(R.id.sortList);
        TextView surveyStatus = dialog.findViewById(R.id.survey_status);
        SupportClass.setRedStar(surveyStatus,"Select status");
        resetFilter = dialog.findViewById(R.id.resetFilter);
        preSelectedSpinner = dialog.findViewById(R.id.prefilterspinner);
        locationLevelFilter = dialog.findViewById(R.id.spLocationLevel);
        spFacilityStateFilter = dialog.findViewById(R.id.spFacilityState);
        spFacilityDistrictsFilter = dialog.findViewById(R.id.spFacilityDistricts);

        checkboxDymamicLayout = dialog.findViewById(R.id.checkboxDymamic);
    }

    /**
     *
     * @param checkBox
     * @return
     */
    private  CheckBox.OnCheckedChangeListener handleCheck(final CheckBox checkBox)
    {
        return new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked)
                    surveyIds.remove(String.valueOf(checkBox.getTag()));
                else
                    surveyIds.add(String.valueOf(checkBox.getTag()));
            }
        };
    }
}