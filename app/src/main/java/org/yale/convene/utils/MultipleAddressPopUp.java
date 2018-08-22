package org.yale.convene.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.yale.convene.AddBeneficiaryActivity;
import org.yale.convene.BeenClass.beneficiary.Address;
import org.yale.convene.BeenClass.boundarylevel.LocationType;
import org.yale.convene.BeenClass.parentChild.LevelBeen;
import org.yale.convene.R;
import org.yale.convene.SupportClass;
import org.yale.convene.adapter.LocationSpinnerAdapter;
import org.yale.convene.adapter.spinnercustomadapter.CustomSpinnerAdapter;
import org.yale.convene.database.ExternalDbOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mahiti on 13/12/17.
 */

public class MultipleAddressPopUp {
    private static final String TAG="MultipleAddressPopUp";
    private static String levelSeven = "Level7";
    private static String levelFive = "Level5";
    private static String levelFour = "Level4";
    private static String levelThree = "Level3";
    private static String levelSix = "Level6";
    private static String levelTwo = "Level2";
    private Typeface face;
    private static final String RURAL_LOCATION_TYPE = "Rural";
    private String selectedSlugName="";
    private Activity activity;
    private ExternalDbOpenHelper dbhelper;
    private TextView statedynamicLabel;
    private Spinner stateSpinner;
    private TextView districtDynamicLabel;
    private Spinner districtSpinner;
    private TextView talukDynamicLabel;
    private Spinner talukSpinner;
    private TextView grampanchayatDynamicLabel;
    private Spinner grampanchayatSpinner;
    private TextView villageDynamicLabel;
    private Spinner villageSpinner;
    private TextView hamletdynamicLabel;
    private TextView errorState;
    private TextView errorDIstrict;
    private TextView errorGrampanchayat;
    private TextView errorVillage;
    private TextView errorHamlet;
    private Spinner hamletDynamicSpinner;
    private TextView errorTextDynamicAddress1;
    private EditText address1DynamicEditText;
    private TextView errorTextDynamicPincode;
    private EditText pincodeDynamicEditText;
    private TextView errorTaluk;
    private String slugName="";
    private List<LevelBeen> stateList=new ArrayList<>();
    private SharedPreferences sharedPreferences1;
    private EditText address2DynamciEditText;
    private AddBeneficiaryUtils addBeneficiaryUtils;
    private Spinner locationLevelSpinner;
    private UpdateAddressDetails updateAddressDetails;
    private TextView addressProofLabel;
    private Spinner addressProofSpinner;
    private RadioGroup addressValidRadioGroup;
    private LinearLayout linearAddressproof;
    private TextView errorTextAddressProofTextView;
    private TextView errorTextAddressValidTextView;
    private RadioButton radioNoRadioButton;
    private RadioButton radioYesRadioButton;

    public MultipleAddressPopUp(AddBeneficiaryActivity activity){
        this.activity=activity;
        sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(this.activity);
        addBeneficiaryUtils=new AddBeneficiaryUtils(activity);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.activity);
        face = Typeface.createFromAsset(activity.getAssets(),
                "fonts/Roboto-Light.ttf");
        addBeneficiaryUtils=new AddBeneficiaryUtils(activity);
        updateAddressDetails= activity;
        dbhelper = ExternalDbOpenHelper.getInstance(activity, sharedPreferences.getString(Constants.DBNAME, ""), sharedPreferences.getString("inv_id", ""));
    }


    public HashMap<Integer, List<Address>> showAddMultipleAddressPopUp(final HashMap<Integer, List<Address>> addressListHashMap, final int addressCount){
        try{
            View dialogView = null;
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            dialogView = inflater.inflate(R.layout.add_address_layout, null);
            builder.setView(dialogView);
            initializeVariables(dialogView);
            String locationLevelString = sharedPreferences1.getString("LOCATION_LEVELTYPE_UID", "");
            String addressproofString=sharedPreferences1.getString("ADDRESS_PROOF_UID","");
            List<LocationType> addressProofTypes=addBeneficiaryUtils.getAddressProof(addressproofString);
            List<LocationType> locationList= addBeneficiaryUtils.setLocationLevelAdapter(activity,locationLevelString, locationLevelSpinner);
            addBeneficiaryUtils.setAddressProofSpinner(addressProofTypes,addressProofSpinner);
            if (sharedPreferences1.getBoolean(Constants.EDITSECONDARY_ADDRESS_FLAG,false)) {
                address1DynamicEditText.setText(addressListHashMap.get(addressCount).get(0).getAddress1());
                address2DynamciEditText.setText(addressListHashMap.get(addressCount).get(0).getAddress2());
                pincodeDynamicEditText.setText(addressListHashMap.get(addressCount).get(0).getPincode());
                selectedSlugName = dbhelper.getLocationTypeFromDB(addressListHashMap.get(addressCount).get(0).getBoundaryId());
                addBeneficiaryUtils.setHouseHoldLocation(selectedSlugName, locationLevelSpinner, RURAL_LOCATION_TYPE, locationList);

                if (addressListHashMap.get(addressCount).get(0).getProofId() != null){
                if (addressListHashMap.get(addressCount).get(0).getProofId().isEmpty()) {
                    radioNoRadioButton.setChecked(true);
                    addressProofLabel.setVisibility(View.GONE);
                    linearAddressproof.setVisibility(View.GONE);
                } else {
                    radioYesRadioButton.setChecked(true);
                    addressProofLabel.setVisibility(View.VISIBLE);
                    linearAddressproof.setVisibility(View.VISIBLE);
                    LocationSpinnerAdapter spinnerAdapter = new LocationSpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, addressProofTypes);
                    addressProofSpinner.setAdapter(spinnerAdapter);
                    if (sharedPreferences1.getBoolean(Constants.EDITSECONDARY_ADDRESS_FLAG, false)) {
                        for (int i = 0; i < addressProofTypes.size(); i++) {
                            if ((String.valueOf(addressProofTypes.get(i).getId()).contains(addressListHashMap.get(addressCount).get(0).getProofId()))) {
                                addressProofSpinner.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }
            }

            addressValidRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
                if(i==R.id.radio_yes){
                    addressProofLabel.setVisibility(View.VISIBLE);
                    linearAddressproof.setVisibility(View.VISIBLE);
                }else{
                    addressProofLabel.setVisibility(View.GONE);
                    linearAddressproof.setVisibility(View.GONE);
                }
            });

            locationLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    LocationType locationType = (LocationType) adapterView.getSelectedItem();
                    setLocationLevelAdapter(locationType,addressCount,addressListHashMap);
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Logger.logV(TAG, "nothing is selected in dynamic spinner selection");
                }
            });


            stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    LevelBeen levelBeen = (LevelBeen) parent.getSelectedItem();
                    Logger.logV(TAG, "State selected Id" + levelBeen.getId());
                    if (sharedPreferences1.getBoolean(Constants.EDITSECONDARY_ADDRESS_FLAG,false)) {
                    /*getting the states details from levels*/
                       addBeneficiaryUtils.setEdittedStateSpinner(levelThree,districtSpinner, addressListHashMap.get(addressCount).get(0).getBoundaryId(),levelBeen,addBeneficiaryUtils);
                    } else {
                        addBeneficiaryUtils.setStateSpinner(levelThree,districtSpinner,levelBeen);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Logger.logV(TAG, " state dynamic onNothingSelected");
                }
            });
            districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    LevelBeen levelBeen1 = (LevelBeen) adapterView.getSelectedItem();
                    if (sharedPreferences1.getBoolean(Constants.EDITSECONDARY_ADDRESS_FLAG,false)) {
                       addBeneficiaryUtils.setEdittedDistrictSpinner(levelFour,talukSpinner, addressListHashMap.get(addressCount).get(0).getBoundaryId(),levelBeen1,slugName,selectedSlugName,addBeneficiaryUtils);
                    } else {
                        addBeneficiaryUtils.setDistrictToSpinner(levelFour,levelBeen1,slugName,talukSpinner);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Logger.logV(TAG, "onNothingSelected");
                }
            });
            talukSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    LevelBeen levelBeen2 = (LevelBeen) adapterView.getSelectedItem();
                    if (sharedPreferences1.getBoolean(Constants.EDITSECONDARY_ADDRESS_FLAG,false)) {
                    /*getting the states details from levels*/
                        addBeneficiaryUtils.setEdittedTalukLevelToSpinner(levelFive,grampanchayatSpinner,levelBeen2,slugName,selectedSlugName, addressListHashMap.get(addressCount).get(0).getBoundaryId(),addBeneficiaryUtils);

                    } else {
                        addBeneficiaryUtils.setGrampanchayatToSpinner(levelFive,grampanchayatSpinner,levelBeen2,slugName);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Logger.logV(TAG, "the house hold dynamic taluk nothing selected");
                }
            });
            grampanchayatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    LevelBeen levelBeen4 = (LevelBeen) adapterView.getSelectedItem();
                    if (sharedPreferences1.getBoolean(Constants.EDITSECONDARY_ADDRESS_FLAG,false)) {
                    /*getting the states details from levels*/
                       addBeneficiaryUtils.setEdittedGrampanchayatSpinner(levelSix,villageSpinner,slugName,selectedSlugName,levelBeen4, addressListHashMap.get(addressCount).get(0).getBoundaryId(),addBeneficiaryUtils);
                    } else {
                        addBeneficiaryUtils.setGrampanachayatLevelToSpinner(levelSix,villageSpinner,slugName,levelBeen4);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Logger.logV(TAG, "the house hold dynamic grampanchat nothing selected");
                }
            });
            villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    LevelBeen levelBeen5 = (LevelBeen) adapterView.getSelectedItem();
                    if (sharedPreferences1.getBoolean(Constants.EDITSECONDARY_ADDRESS_FLAG,false)) {
                    /*getting the states details from levels*/
                       addBeneficiaryUtils.setEdittedVillageSpinner(levelSeven,slugName,selectedSlugName,hamletDynamicSpinner,levelBeen5, addressListHashMap.get(addressCount).get(0).getBoundaryId(),addBeneficiaryUtils);
                    } else {
                        addBeneficiaryUtils.setHamletLevelListToSpinner(hamletDynamicSpinner,levelBeen5,slugName);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Logger.logV(TAG, "the house hold dynamic village nothing selected");
                }
            });
            hamletDynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Logger.logV(TAG, "the house hold dynamic hamlet onitem selected");
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Logger.logV(TAG, "the house hold dynamic hamlet");
                }
            });

            final Button saveButton = dialogView.findViewById(R.id.saveDataButton);
            final Button cancelButton = dialogView.findViewById(R.id.cancelButton);

            final AlertDialog b = builder.create();
            b.setCanceledOnTouchOutside(false);
            b.show();
            cancelButton.setOnClickListener(view -> b.dismiss());
            saveButton.setOnClickListener(view -> saveAddress(addressListHashMap,b,addressCount));


        }catch (Exception e){
            Logger.logE(TAG,"Exception on Multiple address popup",e);
        }

        return addressListHashMap;
    }

    private void saveAddress(HashMap<Integer, List<Address>> addressListHashMap, AlertDialog b, int addressCount) {
       addressListHashMap = validateAllFields(addressListHashMap,addressCount);
        if(addressListHashMap != null && !addressListHashMap.isEmpty()){
            b.dismiss();
            updateAddressDetails.onUpdateAddressSuccess(addressListHashMap);
        }

    }

    /**
     * method to validate all the fields before saving
     * @param addressListHashMap
     * @param addressCount
     */
    private HashMap<Integer, List<Address>> validateAllFields(HashMap<Integer, List<Address>> addressListHashMap, int addressCount) {

        Address address=new Address();
        List<Address> addressList=new ArrayList<>();
        LevelBeen stateLevelBeen=(LevelBeen)stateSpinner.getSelectedItem();
        if((Constants.SELECT).equalsIgnoreCase(stateLevelBeen.getName())){
            Utils.checkBoundaryValidation(errorState, activity.getString(R.string.please_select_state));
            return null;
        }
        errorState.setVisibility(View.GONE);
        LevelBeen districtLevelBeen=(LevelBeen)districtSpinner.getSelectedItem();
        if((Constants.SELECT).equalsIgnoreCase(districtLevelBeen.getName())){
            Utils.checkBoundaryValidation(errorDIstrict, activity.getString(R.string.please_select_district));
            return null;
        }
        errorDIstrict.setVisibility(View.GONE);
        LevelBeen talukLevelBeen=(LevelBeen)talukSpinner.getSelectedItem();
        if((Constants.SELECT).equalsIgnoreCase(talukLevelBeen.getName())){
            Utils.checkBoundaryValidation(errorTaluk, activity.getString(R.string.please_select_taluk));
            return null;
        }
        errorTaluk.setVisibility(View.GONE);
        LevelBeen grampanchayatLevelBeen=(LevelBeen)grampanchayatSpinner.getSelectedItem();
        if((Constants.SELECT).equalsIgnoreCase(grampanchayatLevelBeen.getName())){
            Utils.checkBoundaryValidation(errorGrampanchayat, activity.getString(R.string.please_select_grampanchayat));
            return null;
        }
        errorGrampanchayat.setVisibility(View.GONE);
        LevelBeen villageLevelBeen=(LevelBeen)villageSpinner.getSelectedItem();
        if((Constants.SELECT).equalsIgnoreCase(villageLevelBeen.getName())){
            Utils.checkBoundaryValidation(errorVillage, activity.getString(R.string.please_select_village));
            return null;
        }
        errorVillage.setVisibility(View.GONE);

        LevelBeen hamletLevelBeen = (LevelBeen) hamletDynamicSpinner.getSelectedItem();
        if ((Constants.SELECT).equalsIgnoreCase(hamletLevelBeen.getName())) {
            Utils.checkBoundaryValidation(errorHamlet, activity.getString(R.string.please_select_hamlet));
            return null;
        }
        errorHamlet.setVisibility(View.GONE);
        address.setLeastLocationName(hamletLevelBeen.getName());
        address.setLocationLevel(hamletLevelBeen.getLocationLevel());
        address.setLeastLocationId(hamletLevelBeen.getId());
        if(addressCount==0){
            address.setPrimary(1);
        }else{
            address.setPrimary(0);
        }
        if(!radioYesRadioButton.isChecked()&&!radioNoRadioButton.isChecked()){
            errorTextAddressValidTextView.setVisibility(View.VISIBLE);
            Utils.checkBoundaryValidation(errorTextAddressValidTextView,activity.getString(R.string.please_choose));
            return null;
        }else
            errorTextAddressValidTextView.setVisibility(View.GONE);
        if(radioYesRadioButton.isChecked()){
            LocationType locationType=(LocationType)addressProofSpinner.getSelectedItem();
            if((Constants.SELECT).equalsIgnoreCase(locationType.getName())){
                errorTextAddressProofTextView.setVisibility(View.VISIBLE);
                Utils.checkBoundaryValidation(errorTextAddressProofTextView,activity.getString(R.string.please_select_address_proof));
                return null;
            }
            errorTextAddressProofTextView.setVisibility(View.VISIBLE);
            address.setProofId(String.valueOf(locationType.getId()));
        }else{
            if (address1DynamicEditText.getText().toString().trim().trim().isEmpty()) {
                Utils.checkBoundaryValidation(errorTextDynamicAddress1, activity.getString(R.string.please_enter_address1));
                return null;
            }
            errorTextDynamicAddress1.setVisibility(View.GONE);
            address.setAddress1(address1DynamicEditText.getText().toString().trim());
            address.setProofId("");
        }
        if (address1DynamicEditText.getText().toString().trim().trim().isEmpty()) {
            Utils.checkBoundaryValidation(errorTextDynamicAddress1, activity.getString(R.string.please_enter_address1));
            return null;
        }
        errorTextDynamicAddress1.setVisibility(View.GONE);
        address.setAddress1(address1DynamicEditText.getText().toString().trim());
        address.setAddress2(address2DynamciEditText.getText().toString().trim());
        if (pincodeDynamicEditText.getText().toString().trim().isEmpty() || pincodeDynamicEditText.getText().toString().trim().length() < 6) {
            errorTextDynamicPincode.setVisibility(View.VISIBLE);
            Utils.checkBoundaryValidation(errorTextDynamicPincode, activity.getString(R.string.please_enter_6_digit_pincode));
            return null;
        } else {
            errorTextDynamicPincode.setVisibility(View.GONE);
        }
        address.setPincode(pincodeDynamicEditText.getText().toString().trim());
        addressList.add(address);
        if (sharedPreferences1.getBoolean(Constants.EDITSECONDARY_ADDRESS_FLAG,false))
            addressListHashMap.put(addressCount,addressList);
        else {
            HashMap<Integer, List<Address>> tempList = AddBeneficiaryUtils.getUpdatedHashMap(addressListHashMap);
            addressListHashMap.put(tempList.size(),addressList);
        }
        HashMap<Integer, List<Address>> tempList2 = AddBeneficiaryUtils.getUpdatedHashMap(addressListHashMap);

        return tempList2;
    }


    /**
     * method to set the state spinner based on location type selection(Rural/Urban)
     * @param locationType
     * @param addressCount
     * @param addressListHashMap
     */
    private void setLocationLevelAdapter(LocationType locationType, int addressCount, HashMap<Integer, List<Address>> addressListHashMap) {
        slugName = locationType.getName();
        addBeneficiaryUtils.setClusterLabels(statedynamicLabel, districtDynamicLabel, talukDynamicLabel, grampanchayatDynamicLabel, villageDynamicLabel, hamletdynamicLabel, slugName);
                            /*getting the states details from levels*/
        if (sharedPreferences1.getBoolean(Constants.EDITSECONDARY_ADDRESS_FLAG,false)) {
            setStateSpinnerBasedOnLocationType(selectedSlugName,stateSpinner, addressListHashMap.get(addressCount).get(0).getBoundaryId(),locationType);
        } else {
            stateList = dbhelper.getLevelValuesFromDB(levelTwo, "name");
            CustomSpinnerAdapter dataAdapter3 = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, stateList);
            stateSpinner.setAdapter(dataAdapter3);
            List<Spinner> spinnerList=new ArrayList<>();
            spinnerList.add(districtSpinner);
            spinnerList.add(talukSpinner);
            spinnerList.add(grampanchayatSpinner);
            spinnerList.add(villageSpinner);
            spinnerList.add(hamletDynamicSpinner);
            addBeneficiaryUtils.setEditedStateAdapter(spinnerList);
        }
    }

    private void setStateSpinnerBasedOnLocationType(String selectedSlugName, Spinner stateSpinner, Integer boundaryId, LocationType locationType) {
        if (!selectedSlugName.equalsIgnoreCase(locationType.getName())) {
                    /*getting the states details from levels*/
            List<LevelBeen> stateListBeenList = dbhelper.getLevelValuesFromDB(levelTwo, "name");
            CustomSpinnerAdapter dataAdapter3 = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, stateListBeenList);
            stateSpinner.setAdapter(dataAdapter3);
        } else {
            stateList = dbhelper.getLevelValuesFromDB(levelTwo, "name");
            setUpdatedStateListAdapter(stateSpinner, stateList, boundaryId);
        }

    }

    private void setUpdatedStateListAdapter(Spinner stateSpinner, List<LevelBeen> stateList, Integer boundaryId) {
        CustomSpinnerAdapter dataAdapter3 = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, stateList);
        stateSpinner.setAdapter(dataAdapter3);
        if (sharedPreferences1.getBoolean(Constants.EDITSECONDARY_ADDRESS_FLAG,false) && (selectedSlugName).equalsIgnoreCase(slugName)) {
            int level2Id = dbhelper.getLevelIdFromLevels(levelSeven, 2, "", boundaryId);
            List<LevelBeen> stateListLevelBean = dbhelper.getDistrictNamesFromLevels(levelTwo, 2, level2Id);
            Logger.logV(TAG, "State level values" + stateListLevelBean.get(0).getName());
            for (int i = 0; i < stateList.size(); i++) {
                if (!stateListLevelBean.isEmpty() && stateListLevelBean.get(0).getName().contains(stateList.get(i).getName())) {
                    stateSpinner.setSelection(stateList.indexOf(stateList.get(i)));
                    Logger.logV(TAG, "State level values" + stateListLevelBean.get(0).getName());
                    break;
                }
            }
        } else {
            List<Spinner> spinnerList=new ArrayList<>();
            spinnerList.add(districtSpinner);
            spinnerList.add(talukSpinner);
            spinnerList.add(grampanchayatSpinner);
            spinnerList.add(villageSpinner);
            spinnerList.add(hamletDynamicSpinner);
            addBeneficiaryUtils.setEditedStateAdapter(spinnerList);
        }
    }


    /**
     * method to initialize the dialog varibles
     * @param dialogView
     */
    private void initializeVariables(View dialogView) {
        radioYesRadioButton= dialogView.findViewById(R.id.radio_yes);
        radioNoRadioButton= dialogView.findViewById(R.id.radio_no);
        errorTextAddressValidTextView= dialogView.findViewById(R.id.errorTextAddressValid);
        TextView textAddressValid = dialogView.findViewById(R.id.textAddressValid);
        errorTextAddressProofTextView= dialogView.findViewById(R.id.errorTextAddressProof);
        linearAddressproof= dialogView.findViewById(R.id.linearAddressproof);
        addressValidRadioGroup= dialogView.findViewById(R.id.addressValidRadioGroup);
        addressProofSpinner= dialogView.findViewById(R.id.addressProofSpinner);
        addressProofLabel= dialogView.findViewById(R.id.addressProofLabel);
        address2DynamciEditText = dialogView.findViewById(R.id.address2);
        statedynamicLabel = dialogView.findViewById(R.id.stateLabel);
        stateSpinner = dialogView.findViewById(R.id.housrholdstate);
        districtDynamicLabel = dialogView.findViewById(R.id.districtLabel);
        districtSpinner = dialogView.findViewById(R.id.housrholddistrict);
        talukDynamicLabel = dialogView.findViewById(R.id.talukLabel);
        talukSpinner = dialogView.findViewById(R.id.housrholdtaluk);
        grampanchayatDynamicLabel = dialogView.findViewById(R.id.grampanchayatLabel);
        grampanchayatSpinner = dialogView.findViewById(R.id.housrholdgrampanchayat);
        villageDynamicLabel = dialogView.findViewById(R.id.villageLabel);
        villageSpinner = dialogView.findViewById(R.id.housrholdvillage);
        hamletdynamicLabel = dialogView.findViewById(R.id.hamletLabel);
        errorState= dialogView.findViewById(R.id.errorTextState);
        errorDIstrict= dialogView.findViewById(R.id.errorTextDistrict);
        errorTaluk= dialogView.findViewById(R.id.errorTextTaluk);
        errorGrampanchayat= dialogView.findViewById(R.id.errorTextGrampanchat);
        errorVillage= dialogView.findViewById(R.id.errorTextVillage);
        errorHamlet= dialogView.findViewById(R.id.errorTextHamlet);
        locationLevelSpinner = dialogView.findViewById(R.id.locationlevel);
        hamletDynamicSpinner = dialogView.findViewById(R.id.housrholdhamlet);
        TextView addressLabel1TextView = dialogView.findViewById(R.id.addressLabel1);
        errorTextDynamicAddress1 = dialogView.findViewById(R.id.errorTextAddress1);
        address1DynamicEditText = dialogView.findViewById(R.id.address1);
        TextView addressLabel2TextView = dialogView.findViewById(R.id.addressLabel2);
        TextView pincodeDynamicTextView = dialogView.findViewById(R.id.pincodeLabel);
        errorTextDynamicPincode = dialogView.findViewById(R.id.errorTextPincode);
        pincodeDynamicEditText = dialogView.findViewById(R.id.pincode);
        addressLabel1TextView.setTypeface(face);
        SupportClass.setRedStar(addressLabel1TextView, activity.getString(R.string.line_one));
        SupportClass.setRedStar(addressProofLabel, activity.getString(R.string.proofs));
        addressLabel2TextView.setTypeface(face);
        pincodeDynamicTextView.setTypeface(face);
        SupportClass.setRedStar(pincodeDynamicTextView,activity.getString(R.string.pincode));
        SupportClass.setRedStar(textAddressValid,activity.getString(R.string.is_add_valid));
    }
}
