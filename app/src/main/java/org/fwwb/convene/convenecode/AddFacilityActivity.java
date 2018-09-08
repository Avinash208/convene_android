package org.fwwb.convene.convenecode;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.fwwb.convene.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.fwwb.convene.convenecode.BeenClass.boundarylevel.Boundary;
import org.fwwb.convene.convenecode.BeenClass.boundarylevel.LocationType;
import org.fwwb.convene.convenecode.BeenClass.facilitiesBeen.FacilitiesAreaBeen;
import org.fwwb.convene.convenecode.BeenClass.facilitiesBeen.FacilitiesAreaInterface;
import org.fwwb.convene.convenecode.BeenClass.facilitiesBeen.FacilitiesTypesBeen;
import org.fwwb.convene.convenecode.BeenClass.facilitiesBeen.FacilitySubTypeBeen;
import org.fwwb.convene.convenecode.BeenClass.parentChild.LevelBeen;
import org.fwwb.convene.convenecode.adapter.AreaSpinnerAdapter;
import org.fwwb.convene.convenecode.adapter.BoundarySpinnerAdapter;
import org.fwwb.convene.convenecode.adapter.FacilitySpinnerAdapter;
import org.fwwb.convene.convenecode.adapter.LocationSpinnerAdapter;
import org.fwwb.convene.convenecode.adapter.SubFacilitySpinnerAdapter;
import org.fwwb.convene.convenecode.api.BeneficiaryApis.FacilityTypeInterface;
import org.fwwb.convene.convenecode.api.BeneficiaryApis.GetBoundaryLevelInterface;
import org.fwwb.convene.convenecode.api.BeneficiaryApis.SaveFacility;
import org.fwwb.convene.convenecode.api.BeneficiaryApis.UpdateFacility;
import org.fwwb.convene.convenecode.database.ExternalDbOpenHelper;
import org.fwwb.convene.convenecode.utils.AddBeneficiaryUtils;
import org.fwwb.convene.convenecode.utils.AddFacilityUtils;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.convenecode.utils.ToastUtils;
import org.fwwb.convene.convenecode.utils.Utils;
import org.fwwb.convene.convenecode.utils.multispinner.SpinnerClusterSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddFacilityActivity extends BaseActivity implements GetBoundaryLevelInterface, FacilitiesAreaInterface, FacilityTypeInterface, AdapterView.OnItemSelectedListener, View.OnClickListener {

    /**
     * facilityTypeList contains list of all the facility types
     */
    List<FacilitiesTypesBeen> facilityTypeList = new ArrayList<>();
    /**
     * facilitySubTypeList contains list of all the sub facility types
     */
    List<FacilitySubTypeBeen> facilitySubTypeList = new ArrayList<>();
    /**
     * thematicAreaList contains list of all the thematic area for selected facility types
     */
    List<FacilitiesAreaBeen> thematicAreaList = new ArrayList<>();
    /**
     * boundaryList contains list of all the levels data for particular partner
     */
    List<Boundary> boundaryList = new ArrayList<>();
    /**
     * serviceList contains list of all the services
     */
    List<org.fwwb.convene.convenecode.BeenClass.service.Datum> serviceList = new ArrayList<>();
    /**
     * boundaryList contains list of all the levels data for particular partner based on slug selection(Rural or urban)
     */
    List<LevelBeen> boundarylevelList = new ArrayList<>();
    Spinner facilitiesBeneficiaryType;
    /*facilitiesType spinner is a varible which is used to set the facility types*/
    Spinner facilitiesType;
    /*facilitiesSubType spinner is a varible which is used to set the sub facility types*/
    Spinner facilitiesSubType;
    /*locationLevel spinner is a varible which is used to set the location types(rural or urban)*/
    Spinner locationLevel;
    /*spFacilityDistricts and spFacilityState spinner is a varible which is used to set the location levels*/
    Spinner spFacilityDistricts;
    Spinner spFacilityState;
    Spinner spLocationLevel;
    /*intializing the save and cancel to button*/
    Button btnFacilitySave;
    Button btnFacilityCancel;
    /*initializing the name,address1,address2 and pincode to edittext*/
    EditText edtName;
    EditText address1;
    EditText address2;
    EditText pincodeNumber;
    SharedPreferences facilityPreferences;
    ExternalDbOpenHelper dbhelper;
    LinearLayout dynamicCheckBox;
    String getToken;
    String locationLevelString = "";
    String slugName = "";
    String selectedSlugName = "";
    FacilitiesAreaBeen facilitiesAreaBeen;
    ImageView imageMenu;
    /*declaring the place holder variables for the facility forms*/
    TextView namelable;
    TextView faciltylable;
    TextView subfaciltylable;
    TextView thematiclable;
    TextView locationLevelLabel;
    TextView stateLabel;
    TextView districtLabel;
    TextView addressLabel1;
    TextView addressLabel2;
    TextView pincodeLabel;
    TextView contactLabel;
    Typeface face;
    TextView serviceLabel;
    private TextView errorTextName;
    private TextView errorTextFacility;
    private TextView errorTextSubFacility;
    private TextView errorTextThematic;
    private TextView errorTextLocationtype;
    private TextView errorTextAddress1;
    private TextView errorTextAddress2;
    private TextView errorTextPincode;
    private String beneficiaryId;
    private static final String TAG = "AddFacilityAcivity";
    private String nameString = "";
    private String thematicAreaString = "";
    private String pincodeString = "";
    private String address1String = "";
    private LinearLayout linearservice;
    boolean isServiceAvailable = false;
    private static final String RURAL_LOCATION_TYPE = "Rural";
    private String facilitySubType = "";
    private String boundaryLevelString = "";
    private String boundaryNameString = "";
    private LinearLayout locationLinearLayout;
    List<Integer> servicesEditedList = new ArrayList<>();
    private String editedSlugName = "";
    private int selectedBoundry;
    List<LocationType> locationList = new ArrayList<>();
    private static final String SERVICE_STRING = "services";
    private static final String LOCATION_LEVEL_TYPE_PREFERENCE = "LOCATION_LEVELTYPE_UID";
    private static final String ISEDITFACILITY_CHECK = "isEditFacility";
    private static final String UPDATE_FACILITY_FLAG_CHECK = "UpdateFacilityUi";
    private static final String UPDATE_FACILITY_FLAG = "UpdateFacility";
    private static final String UPDATE_BENEFICIARY_FLAG = "UpdateBeneficiary";
    private static final String FACILITY_STATUS = "status";
    private static final String FACILITY_TYPES_PREF_KEY = "FACILITY_TYPES_UID";
    private static final String BENEFICIARY_NAME_KEY = "beneficiaryName";
    private static final String BOUNDARY_LEVEL_KEY = "boundary_level";
    private static final String LOCATION_NAME_KEY = "locationName";
    private static final String LOCATION_ID_KEY = "location_id";
    private static final String ADDRESS1_KEY = "address1";
    private static final String ADDRESS2_KEY = "address2";
    private static final String PINCODE_KEY = "pincode";
    private static final String UUID_KEY = "uuid";
    private String uuidString = "";
    private String address2String = "";
    private SpinnerClusterSearch spinnerSearch;
    private String selectedBoundaryName="";
    private int selectedBoundaryId=0;
    private int selectedBoundaryLevel=0;
    private String syncStatus="";
    private AddFacilityUtils addFacilityUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_facility);
        face = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Light.ttf");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            getToken = extras.getString("typeValue");
        }
        initVariables();
        addFacilityUtils=new AddFacilityUtils(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        facilitiesBeneficiaryType.setOnItemSelectedListener(this);
        spFacilityState.setOnItemSelectedListener(this);
        spLocationLevel.setOnItemSelectedListener(this);
        facilitiesType.setOnItemSelectedListener(this);
        facilitiesSubType.setOnItemSelectedListener(this);
        btnFacilitySave.setOnClickListener(this);
        btnFacilityCancel.setOnClickListener(this);
        if (extras != null) {
            beneficiaryId = extras.getString("beneficiary_type_id");
        }
        facilityPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        dbhelper = ExternalDbOpenHelper.getInstance(AddFacilityActivity.this, facilityPreferences.getString(Constants.DBNAME, ""), facilityPreferences.getString("UID", ""));
        locationLevelString = facilityPreferences.getString(LOCATION_LEVEL_TYPE_PREFERENCE, "");
        setLocationLevelAdapter(locationLevelString,spLocationLevel);
        if (facilityPreferences.getBoolean(ISEDITFACILITY_CHECK, false)) {
            btnFacilitySave.setText(getString(R.string.update));
        } else {
            btnFacilitySave.setText(getString(R.string.save));
        }
        Logger.logD(TAG, "FacilitySubTypesList size==" + facilitySubTypeList.size());
        SharedPreferences.Editor editor = facilityPreferences.edit();
        editor.putBoolean(UPDATE_FACILITY_FLAG_CHECK, false);
        editor.apply();

        if (facilityPreferences.getBoolean(ISEDITFACILITY_CHECK, false) && (extras != null)) {
            setBundleValuesForEdit(extras);
        }
        String facilityTypes = facilityPreferences.getString(FACILITY_TYPES_PREF_KEY, "");
        facilitySubTypeList = dbhelper.getFacilitySubType(facilityTypes, Integer.parseInt(beneficiaryId));
        if (!facilitySubTypeList.isEmpty()) {
            // attaching data adapter to spinner*/
            errorTextSubFacility.setVisibility(View.GONE);
            SubFacilitySpinnerAdapter adapter = new SubFacilitySpinnerAdapter(AddFacilityActivity.this, android.R.layout.simple_spinner_dropdown_item, facilitySubTypeList);
            facilitiesType.setAdapter(adapter);
            if (facilityPreferences.getBoolean(ISEDITFACILITY_CHECK, false)) {
                setSelectedSubFacility(facilitiesType, facilitySubTypeList);
            }
            if (facilitySubTypeList.size() <= 1) {
                errorTextSubFacility.setVisibility(View.VISIBLE);
                errorTextSubFacility.setText("Sub Facility has not  tagged to selected facility");
            }
        }
        setTheServicesFromDb();
        callBoundaries();
    }

    /**
     * method to set the location boundaries into location spinners
     */
    public void setLocationLevelAdapter(String locationLevelString, Spinner spLocationLevel) {
/**
 * locationList contains only two values (rural and Urban)
 */
        locationList = Utils.getLocationList(locationLevelString);
        // attaching data adapter to spinner
        LocationSpinnerAdapter spinnerAdapter = new LocationSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, locationList);
        spLocationLevel.setAdapter(spinnerAdapter);
    }


    /*method to set the services dynamically to the checkbox*/
    private void setTheServicesFromDb() {
        serviceList = dbhelper.getServiceName("Service");
        if (facilityPreferences.getBoolean(ISEDITFACILITY_CHECK, false)) {
            if (servicesEditedList.isEmpty()) {
                isServiceAvailable = true;
                dynamicCheckBox = findViewById(R.id.dynamiclayoutCheckBox);
                addFacilityUtils.setServiceToCheckBox(serviceList,face,dynamicCheckBox);
            } else {
                linearservice.setVisibility(View.VISIBLE);
                dynamicCheckBox = findViewById(R.id.dynamiclayoutCheckBox);
        /*setting the checkbox dynamically based on json response*/
                isServiceAvailable = true;
                    /*main service list*/
                dynamicCheckBox.removeAllViews();
                for (int j = 0; j < serviceList.size(); j++) {
                    CheckBox dynamicCheck = new CheckBox(this);
                    dynamicCheck.setText(serviceList.get(j).getName());
                    dynamicCheck.setTag(serviceList.get(j).getId());
                    dynamicCheck.setTextSize(14);
                    dynamicCheck.setTypeface(face);
                    addFacilityUtils.setEdittedServiceList(dynamicCheck, j,servicesEditedList,serviceList);
                    dynamicCheckBox.addView(dynamicCheck);
                }
            }
        } else {
            if (serviceList.isEmpty()) {
                linearservice.setVisibility(View.GONE);
            } else {
                linearservice.setVisibility(View.VISIBLE);
                dynamicCheckBox = findViewById(R.id.dynamiclayoutCheckBox);
        /*setting the checkbox dynamically based on json response*/
                isServiceAvailable = true;
                addFacilityUtils.setServiceToCheckBox(serviceList,face,dynamicCheckBox);
            }
        }
    }
    /**
     * setting the bundle values while editting
     *
     * @param extras
     */
    private void setBundleValuesForEdit(Bundle extras) {
        String leastLocationIdString = "";
        if(extras!=null){
            nameString = extras.getString(BENEFICIARY_NAME_KEY);
            facilitySubType = extras.getString(Constants.FACILITY_SUB_TYPE);
            thematicAreaString = extras.getString(Constants.THEMATIC_AREA_NAME);
            boundaryLevelString = extras.getString(BOUNDARY_LEVEL_KEY);
            boundaryNameString = extras.getString(LOCATION_NAME_KEY);
            leastLocationIdString = extras.getString(LOCATION_ID_KEY);
            address1String = extras.getString(ADDRESS1_KEY);
            address2String = extras.getString(ADDRESS2_KEY);
            pincodeString = extras.getString(PINCODE_KEY);
            uuidString = extras.getString(UUID_KEY);
            syncStatus=extras.getString("sync_status");
        }else{
            nameString=facilityPreferences.getString(Constants.BENEFICIARY_NAME,"");
            facilitySubType = facilityPreferences.getString(Constants.FACILITY_SUB_TYPE,"");
            thematicAreaString=facilityPreferences.getString(Constants.THEMATIC_AREA_NAME,"");
            boundaryLevelString = facilityPreferences.getString(BOUNDARY_LEVEL_KEY,"");
            boundaryNameString = facilityPreferences.getString(LOCATION_NAME_KEY,"");
            leastLocationIdString = facilityPreferences.getString(LOCATION_ID_KEY,"");
            address1String = facilityPreferences.getString(ADDRESS1_KEY,"");
            address2String = facilityPreferences.getString(ADDRESS2_KEY,"");
            pincodeString = facilityPreferences.getString(PINCODE_KEY,"");
            uuidString = facilityPreferences.getString(UUID_KEY,"");
            syncStatus=facilityPreferences.getString("sync_status","");
        }

        String servicesString = null;
        if (extras != null) {
            servicesString = extras.getString(SERVICE_STRING);
        }else{
            servicesString=defaultPreferences.getString(SERVICE_STRING,"");
        }
        String servicesSplitedString = "";
        if (servicesString != null && !servicesString.isEmpty()) {
            servicesSplitedString = servicesString.replaceAll("\\[", "").replaceAll("\\]", "");
            String[] serviceListFromDb = servicesSplitedString.split(",");
            for (String aServiceList : serviceListFromDb) {
                servicesEditedList.add(Integer.parseInt(aServiceList.trim()));
            }
        }
        editedSlugName = dbhelper.getLocationTypeFacilityFromDB(Integer.parseInt(leastLocationIdString), boundaryLevelString);
        slugName = RURAL_LOCATION_TYPE;
        if (!editedSlugName.isEmpty()) {
            addFacilityUtils.setLocationType(editedSlugName, spLocationLevel,RURAL_LOCATION_TYPE,locationList);
        }
        setEditableValues();
    }
    /**
     * method to set the edittedname,pincoed and address
     */
    private void setEditableValues() {
        edtName.setText(nameString);
        pincodeNumber.setText(pincodeString);
        address1.setText(address1String);
        address2.setText(address2String);
    }
    /**
     * method to set the adapter with default as select
     */
    private void setDefaultAdapter() {
        List<LevelBeen> boundaries = new ArrayList<>();
        LevelBeen boundary1 = new LevelBeen();
        boundary1.setName(Constants.SELECT);
        boundaries.add(boundary1);
        createSpinnerSearch(boundaries);
        ArrayAdapter<LevelBeen> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,boundaries);
        spinnerSearch.setAdapter(adapterSpinner);

    }
    /**
     * method to intialize all the fields
     */
    private void initVariables() {
        spinnerSearch= findViewById(R.id.searchSingleSpinner);
        linearservice = findViewById(R.id.linearservice);
        imageMenu = findViewById(R.id.imageMenu);
        namelable = findViewById(R.id.namelable);
        errorTextFacility = findViewById(R.id.errorTextFacility);
        errorTextName = findViewById(R.id.errorTextName);
        errorTextSubFacility = findViewById(R.id.errorTextSubFacility);
        errorTextThematic = findViewById(R.id.errorTextThematic);
        errorTextLocationtype = findViewById(R.id.errorTextLocationtype);
        errorTextAddress1 = findViewById(R.id.errorTextAddress1);
        errorTextAddress2 = findViewById(R.id.errorTextAddress2);
        errorTextPincode = findViewById(R.id.errorTextPincode);
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        locationLinearLayout = findViewById(R.id.LocationLinear);
        toolbarTitle.setText(getToken);
        faciltylable = findViewById(R.id.faciltylable);
        subfaciltylable = findViewById(R.id.subfaciltylable);
        namelable.setTypeface(face);
        SupportClass.setRedStar(namelable, getString(R.string.name));
        faciltylable.setTypeface(face);
        SupportClass.setRedStar(faciltylable, getString(R.string.facility_type));
        subfaciltylable.setTypeface(face);
        SupportClass.setRedStar(subfaciltylable, getString(R.string.subfacility_type));
        thematiclable = findViewById(R.id.thematiclable);
        thematiclable.setTypeface(face);
        SupportClass.setRedStar(thematiclable, getString(R.string.thematic_area));
        locationLevelLabel = findViewById(R.id.locationLevelLabel);
        locationLevelLabel.setTypeface(face);
        SupportClass.setRedStar(locationLevelLabel, getString(R.string.location_type));
        stateLabel = findViewById(R.id.stateLabel);
        SupportClass.setRedStar(stateLabel, getString(R.string.boundary));
        serviceLabel = findViewById(R.id.serviceLabel);
        serviceLabel.setTypeface(face);
        stateLabel.setTypeface(face);
        districtLabel = findViewById(R.id.districtLabel);
        districtLabel.setTypeface(face);
        SupportClass.setRedStar(districtLabel, getString(R.string.Location));
        addressLabel1 = findViewById(R.id.addressLabel1);
        addressLabel1.setTypeface(face);
        SupportClass.setRedStar(addressLabel1, getString(R.string.line_one));
        addressLabel2 = findViewById(R.id.addressLabel2);
        addressLabel2.setTypeface(face);
        pincodeLabel = findViewById(R.id.pincodeLabel);
        pincodeLabel.setTypeface(face);
        SupportClass.setRedStar(pincodeLabel, getString(R.string.pincode));
        contactLabel = findViewById(R.id.contactLabel);
        contactLabel.setTypeface(face);
        SupportClass.setRedStar(contactLabel, getString(R.string.contact_person_number));
        imageMenu.setVisibility(View.GONE);
        facilitiesBeneficiaryType = findViewById(R.id.facilitiesbeneficiarytype);
        facilitiesType = findViewById(R.id.facilitiestype);
        facilitiesSubType = findViewById(R.id.facilitiessubtype);
        spLocationLevel = findViewById(R.id.spLocationLevel);
        locationLevel = findViewById(R.id.locationlevel);
        btnFacilitySave = findViewById(R.id.btnFacilitySave);
        btnFacilityCancel = findViewById(R.id.cancelFAC);
        edtName = findViewById(R.id.name);
        edtName.setTypeface(face);
        locationLevel.setVisibility(View.GONE);
        spFacilityDistricts = findViewById(R.id.spFacilityDistricts);
        spFacilityState = findViewById(R.id.spFacilityState);
        address1 = findViewById(R.id.address1);
        address1.setTypeface(face);
        address2 = findViewById(R.id.address2);
        address2.setTypeface(face);
        pincodeNumber = findViewById(R.id.pincodenumber);
        pincodeNumber.setTypeface(face);
    }





    /**
     * method to set the adapter based on location spinner
     */
    private void setStateAdapter(String levelString, String locationLevelString) {
        try {
            boundaryList = Utils.setBoundaryList(levelString, locationLevelString);
            // attaching data adapter to spinner
            BoundarySpinnerAdapter adapter1 = new BoundarySpinnerAdapter(AddFacilityActivity.this, android.R.layout.simple_spinner_dropdown_item, boundaryList);
            spFacilityState.setAdapter(adapter1);
            if (facilityPreferences.getBoolean(ISEDITFACILITY_CHECK, false)) {
                if (editedSlugName.isEmpty()) {
                    setEdittedBoundary(boundaryList, spFacilityState);
                }
                if ((selectedSlugName).equalsIgnoreCase(editedSlugName)) {
                    setEdittedBoundary(boundaryList, spFacilityState);
                }
            }

        } catch (Exception e) {
            Logger.logE("", "", e);
        }

    }
    /**
     * method to set the previously editted boundary level to spinner adapter
     * @param boundaryList
     * @param spFacilityState
     */
    private void setEdittedBoundary(List<Boundary> boundaryList, Spinner spFacilityState) {
        for (int i = 0; i < boundaryList.size(); i++) {
            if ((boundaryLevelString).equalsIgnoreCase(String.valueOf(boundaryList.get(i).getLevel()))) {
                spFacilityState.setSelection(i);
            }
        }
    }


    /**
     * method to set the thematic  area and subtype facility in spinner
     */
    private void callBoundaries() {
        if (!facilityPreferences.getBoolean(ISEDITFACILITY_CHECK, false)) {
            setDefaultAdapter();
        }
            String facilityTypes = facilityPreferences.getString(FACILITY_TYPES_PREF_KEY, "");
            facilityTypeList = getfacilityType(facilityTypes);
            Logger.logD(TAG, "FacilityTypesList size==" + facilityTypeList.size());
            if (!facilityTypeList.isEmpty()) {
                // attaching data adapter to spinner
                FacilitySpinnerAdapter adapternew = new FacilitySpinnerAdapter(AddFacilityActivity.this, android.R.layout.simple_spinner_dropdown_item, facilityTypeList);
                facilitiesBeneficiaryType.setAdapter(adapternew);
            }
            String thematicAreaListString = facilityPreferences.getString("THEMATIC_AREA_UID", "");
            thematicAreaList = getThematicAreaListing(thematicAreaListString);
            // attaching data adapter to spinner
            AreaSpinnerAdapter adapter1 = new AreaSpinnerAdapter(AddFacilityActivity.this, android.R.layout.simple_spinner_dropdown_item, thematicAreaList);
            facilitiesSubType.setAdapter(adapter1);
            if (facilityPreferences.getBoolean(ISEDITFACILITY_CHECK, false)) {
                addFacilityUtils.setEdittedThematicArea(thematicAreaString,facilitiesSubType, thematicAreaList);
            }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFacilitySave:
                saveFacility();
                break;
            case R.id.cancelFAC:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.facilitiesbeneficiarytype:
                onClickFacilities();
                break;
            case R.id.spLocationLevel:
                getLocationLevelValues(adapterView);
                break;
            case R.id.spFacilityState:
                setBoundaryLevel(adapterView);
                break;
            case R.id.spFacilityDistricts:

                break;
            case R.id.facilitiestype:
                break;
            case R.id.facilitiessubtype:
                break;
            default:
                break;
        }
    }
    /**
     * method to set the boundary location
     *
     * @param adapterView
     */
    private void setBoundaryLevel(AdapterView<?> adapterView) {
        Boundary boundaryLevelList = (Boundary) adapterView.getSelectedItem();
        if (!(Constants.SELECT).equals(boundaryLevelList.getName())) {
            selectedBoundry = boundaryLevelList.getLevel();
            if ((2) == selectedBoundry || (3) == selectedBoundry) {
                locationLevelLabel.setVisibility(View.GONE);
                locationLinearLayout.setVisibility(View.GONE);
                setBoundaryLevelAdapter(selectedBoundry, selectedSlugName);
            } else {
                locationLevelLabel.setVisibility(View.VISIBLE);
                locationLinearLayout.setVisibility(View.VISIBLE);
                setBoundaryLevelAdapter(selectedBoundry, selectedSlugName);
            }
        } else {
            if (!facilityPreferences.getBoolean(ISEDITFACILITY_CHECK, false)) {
                setDefaultAdapter();
            }

        }
    }
    /**
     * method to setthe location type in dropdowm(Rural/urban)
     */
    private void getLocationLevelValues(AdapterView<?> adapterView) {
        LocationType locationType = (LocationType) adapterView.getSelectedItem();
        slugName = locationType.getSlug();
        locationLevelString = facilityPreferences.getString(LOCATION_LEVEL_TYPE_PREFERENCE, "");
        selectedSlugName = locationType.getName();
        setStateAdapter(locationLevelString, slugName);
        setBoundaryLevelAdapter(selectedBoundry, selectedSlugName);
    }


    /**
     * method to set the location based on slugname
     */
    private void setBoundaryLevelAdapter(Integer boundaryString, String slugName) {
        try {
            boundarylevelList = dbhelper.getBoundaryListFromDB(boundaryString, slugName);
            // attaching data adapter to spinner
            if (boundarylevelList.isEmpty()) {
                List<LevelBeen> levelBeenList = new ArrayList<>();
                LevelBeen levelBeen = new LevelBeen();
                levelBeen.setName(Constants.SELECT);
                levelBeenList.add(levelBeen);
                spinnerSearch.setVisibility(View.VISIBLE);
                createSpinnerSearch(levelBeenList);
            } else {
                spFacilityDistricts.setVisibility(View.GONE);
                spinnerSearch.setVisibility(View.VISIBLE);
                createSpinnerSearch(boundarylevelList);
                int spinnerCount = 0;
                spinnerSearch.setTag(spinnerCount);

            }
        } catch (Exception e) {
            Logger.logE("", "", e);
        }
    }


    /*method to set the selected least level from listview search and set to the adapter*/
    private void createSpinnerSearch(List<LevelBeen> boundarylevelList) {
        if(!boundarylevelList.isEmpty()){
            int count=0;
            if(boundarylevelList.isEmpty()){
                count=-1;
            }else{
                if (facilityPreferences.getBoolean(ISEDITFACILITY_CHECK, false)){
                    count=setCountValue(boundarylevelList,count);
                }else {
                    count=0;
                }

            }
            spinnerSearch.setItems(boundarylevelList, count, items -> {
                for(int j=0;j<items.size();j++){
                    if (items.get(j).isSelected()) {
                        Logger.logD(TAG,"Selected Search" + items.get(j).getName());
                        setBoundaryValuesToString(items,j);
                    }
                }
            });

        }
    }


    /**
     * method to set the boundaryname,boundary id and boundary level to string values
     * @param items
     * @param j
     */
    private void setBoundaryValuesToString(List<LevelBeen> items, int j) {
        selectedBoundaryName=items.get(j).getName();
        selectedBoundaryId=items.get(j).getId();
        selectedBoundaryLevel=items.get(j).getLocationLevel();
    }


    /**
     * method to set the previously selected least location to spinner
     * @param boundarylevelList
     * @param count
     * @return
     */
    private int setCountValue(List<LevelBeen> boundarylevelList, int count) {
        for(int i=0;i<boundarylevelList.size();i++){
            if(boundaryNameString.equalsIgnoreCase(boundarylevelList.get(i).getName())){
                count=i;
                break;
            }
        }
        return count;
    }

    private void onClickFacilities() {
        FacilitiesTypesBeen been = (FacilitiesTypesBeen) facilitiesBeneficiaryType.getSelectedItem();
        if (been != null && !facilitiesBeneficiaryType.getSelectedItem().equals(Constants.FACILITY_TYPE)) {
            Logger.logV(TAG, "the id of the facilities is" + been.getId());

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Logger.logV("nothing selected", "spinner nothing selected");
    }

    @Override
    public void onSuccessFaciltyResponse(boolean flag) {
        String facilityTypes = facilityPreferences.getString(FACILITY_TYPES_PREF_KEY, "");
        facilityTypeList = getfacilityType(facilityTypes);
        Logger.logD("FacilityTypesList value", "FacilityTypesList size==" + facilityTypeList.size());
        if (!facilityTypeList.isEmpty()) {
            // attaching data adapter to spinner
            FacilitySpinnerAdapter adapternew = new FacilitySpinnerAdapter(AddFacilityActivity.this, android.R.layout.simple_spinner_dropdown_item, facilityTypeList);
            facilitiesBeneficiaryType.setAdapter(adapternew);
        }
    }
    /**
     * method to set the thematic area to adapter
     */
    @Override
    public void getThematicAreaResponse(boolean flag) {
        String thematicAreaListString = facilityPreferences.getString("THEMATIC_AREA_UID", "");
        thematicAreaList = getThematicAreaListing(thematicAreaListString);
        // attaching data adapter to spinner
        AreaSpinnerAdapter adapter = new AreaSpinnerAdapter(AddFacilityActivity.this, android.R.layout.simple_spinner_dropdown_item, thematicAreaList);
        facilitiesSubType.setAdapter(adapter);
        if (facilityPreferences.getBoolean(ISEDITFACILITY_CHECK, false)) {
            addFacilityUtils.setEdittedThematicArea(thematicAreaString,facilitiesSubType, thematicAreaList);
        }
    }
    /*method to check all the validation for all the fields and call the submit facility*/
    private void saveFacility() {
        try {
            JSONObject jsonObject = new JSONObject();
            if (edtName.getText().toString().trim().isEmpty()) {
                Utils.checkBoundaryValidation(errorTextName, getString(R.string.enter_name));
                edtName.setFocusable(true);
                return;
            }
            errorTextName.setVisibility(View.GONE);
            jsonObject.put("facility_name", edtName.getText().toString());

            errorTextFacility.setVisibility(View.GONE);
            jsonObject.put("facility_type", getToken);
            jsonObject.put("facility_type_id", beneficiaryId);

            FacilitySubTypeBeen facilitySubTypeBeen = (FacilitySubTypeBeen) facilitiesType.getSelectedItem();
            if ((Constants.SUB_TYPE).equalsIgnoreCase(facilitiesType.getSelectedItem().toString())) {
                Utils.checkBoundaryValidation(errorTextSubFacility, getString(R.string.select_subtype));
                return;
            }
            errorTextSubFacility.setVisibility(View.GONE);
            jsonObject.put("facility_subtype", facilitySubTypeBeen.getName());
            jsonObject.put("facility_subtype_id", String.valueOf(facilitySubTypeBeen.getId()));

            facilitiesAreaBeen = (FacilitiesAreaBeen) facilitiesSubType.getSelectedItem();
            if ((Constants.THEMATIC_AREA).equalsIgnoreCase(facilitiesSubType.getSelectedItem().toString())) {
                Utils.checkBoundaryValidation(errorTextThematic, getString(R.string.select_thematic_type));
                return;
            }
            errorTextThematic.setVisibility(View.GONE);
            jsonObject.put("thematic_area", facilitiesAreaBeen.getName());
            jsonObject.put("thematic_area_id", facilitiesAreaBeen.getId());
            Logger.logV(TAG, "Thematic Area id value==>" + facilitiesSubType.getSelectedItemId());

            LocationType locationType = (LocationType) spLocationLevel.getSelectedItem();
            if (("Select").equals(locationType.getName())) {
                Utils.checkBoundaryValidation(errorTextLocationtype, getString(R.string.please_select_location_type));
                return;
            }
            errorTextLocationtype.setVisibility(View.GONE);
            jsonObject.put("location_type", locationType.getName());

            Boundary boundary = (Boundary) spFacilityState.getSelectedItem();
            if ((Constants.SELECT).equalsIgnoreCase(boundary.getName())) {
                ToastUtils.displayToast(getString(R.string.please_select_location_level), AddFacilityActivity.this);
                return;
            }


            if (("Select Location").equalsIgnoreCase(spinnerSearch.getSelectedItem().toString())) {
                ToastUtils.displayToast(getString(R.string.please_select_location), AddFacilityActivity.this);
                return;
            }
            jsonObject.put("boundary_name", selectedBoundaryName);
            jsonObject.put("boundary_id", String.valueOf(selectedBoundaryId));
            jsonObject.put(BOUNDARY_LEVEL_KEY, selectedBoundaryLevel);


            if (address1.getText().toString().trim().isEmpty()) {
                Utils.checkBoundaryValidation(errorTextAddress1, getString(R.string.please_enter_address1));
                address1.setFocusable(true);
                return;
            }
            errorTextAddress1.setVisibility(View.GONE);
            jsonObject.put(ADDRESS1_KEY, address1.getText().toString());

            errorTextAddress2.setVisibility(View.GONE);
            jsonObject.put(ADDRESS2_KEY, address2.getText().toString());
            if (pincodeNumber.getText().toString().trim().isEmpty() || pincodeNumber.getText().toString().length() < 6) {
                Utils.checkBoundaryValidation(errorTextPincode, getString(R.string.please_enter_6_digit_pincode));
                pincodeNumber.setFocusable(true);
                return;
            } else {
                errorTextPincode.setVisibility(View.GONE);
                jsonObject.put(PINCODE_KEY, pincodeNumber.getText().toString());
                Logger.logV("pincodeNumber", "pincodeNumber" + pincodeNumber.getText().toString());
            }

            int checkCounter = 0;
            List<String> listService = new ArrayList<>();
            if (isServiceAvailable) {
                getServiceSelected(checkCounter, listService);
            }
            /*below lines of code to add the services to json object*/
            jsonObject = AddBeneficiaryUtils.setTheServices(listService, jsonObject);
            if (facilityPreferences.getBoolean(ISEDITFACILITY_CHECK, false)) {
                jsonObject.put(UUID_KEY, uuidString);
            } else {
                UUID uuid = UUID.randomUUID();
                jsonObject.put("UUID", String.valueOf(uuid));
            }
            SharedPreferences.Editor editor = facilityPreferences.edit();
            editor.putBoolean(UPDATE_FACILITY_FLAG_CHECK, true);
            editor.putBoolean("UpdateUi", false);
            callSaveFacilityDetails(jsonObject, btnFacilitySave, editor);
            editor.apply();
            finish();
        } catch (Exception ex) {
            Logger.logE("", "Exception on validation", ex);
        }
    }


    /**
     * method to call the add facility api in online and insert into database offline
     *
     * @param jsonObject
     * @param btnFacilitySave
     * @param editor
     */
    private void callSaveFacilityDetails(JSONObject jsonObject, Button btnFacilitySave, SharedPreferences.Editor editor) {
        try {

            if (btnFacilitySave.getText().toString().equalsIgnoreCase(getString(R.string.save))) {
                editor.putBoolean(UPDATE_FACILITY_FLAG, false);
                editor.putBoolean(UPDATE_BENEFICIARY_FLAG, false);
                editor.putBoolean(ISEDITFACILITY_CHECK, false);
                jsonObject.put(FACILITY_STATUS, "Offline");
                long record = dbhelper.insertOfflineFacilityData(jsonObject);
                ToastUtils.displayToast(edtName.getText().toString() + " added successfully", this);
                Logger.logV(TAG, "Inserted Successfully " + record);
            }else{
                if (facilityPreferences.getBoolean(ISEDITFACILITY_CHECK, false)) {
                    if(("2").equalsIgnoreCase(syncStatus) || ("3").equalsIgnoreCase(syncStatus)){
                        jsonObject.put(FACILITY_STATUS, "Update");
                        editor.putBoolean(UPDATE_FACILITY_FLAG, true);
                        editor.putBoolean(UPDATE_BENEFICIARY_FLAG, false);
                        editor.putBoolean(ISEDITFACILITY_CHECK, true);
                        long updateRecord=dbhelper.updateOfflineFacilityData(jsonObject, uuidString);
                        ToastUtils.displayToast(edtName.getText().toString() + " updated successfully", this);
                        Logger.logV(TAG, "Updates Successfully " + updateRecord);
                    }else{
                        jsonObject.put(FACILITY_STATUS, "Offline");
                        editor.putBoolean(UPDATE_FACILITY_FLAG, true);
                        editor.putBoolean(UPDATE_BENEFICIARY_FLAG, false);
                        editor.putBoolean(ISEDITFACILITY_CHECK, true);
                        long updateRecord=dbhelper.updateOfflineFacilityData(jsonObject, uuidString);
                        ToastUtils.displayToast(edtName.getText().toString() + " updated successfully", this);
                        Logger.logV(TAG, "Updates Successfully " + updateRecord);
                    }

                }
            }
            if (Utils.haveNetworkConnection(this)) {
                    if (btnFacilitySave.getText().toString().equalsIgnoreCase(getString(R.string.save))) {
                        new SaveFacility(AddFacilityActivity.this, getString(R.string.main_Url) + "facilities/facilityadd/").execute();
                    } else {
                        new UpdateFacility(AddFacilityActivity.this, getString(R.string.main_Url) + "facilities/facilityupdate/").execute();
                    }
            }
            finish();
        } catch (Exception e) {
            Logger.logE(TAG, "Exception on adding the facility", e);
        }
    }
    /**
     * method to set the edited or recently selected services to checkbox
     * @param checkcounter
     * @param listService
     */
    private void getServiceSelected(int checkcounter, List<String> listService) {
        int checkBocChildCount = dynamicCheckBox.getChildCount();
        if (checkBocChildCount > 0) {
            for (int i = 0; i < dynamicCheckBox.getChildCount(); i++) {
                CheckBox checkBox = (CheckBox) dynamicCheckBox.getChildAt(i);
                if (checkBox.isChecked()) {
                    checkcounter = checkcounter + 1;
                    listService.add(checkBox.getTag().toString());
                }
            }
        }
    }
    @Override
    public void onSuccessBoundaryLevelResponse() {
        setStateAdapter(locationLevelString, locationLevelString);
    }
    /**
     * method to set the selected sub facility type to spinner
     *
     * @param facilitiesType
     * @param facilitySubTypeList
     */
    private void setSelectedSubFacility(Spinner facilitiesType, List<FacilitySubTypeBeen> facilitySubTypeList) {
        for (int i = 0; i < facilitySubTypeList.size(); i++) {
            if (facilitySubTypeList.get(i).getName().equals(facilitySubType)) {
                facilitiesType.setSelection(i);
            }
        }
    }
    /**
     * @param array
     * @return
     */
    public List<FacilitiesTypesBeen> getfacilityType(String array) {
        List<FacilitiesTypesBeen> facilitiesTypesBeens = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(array);
            FacilitiesTypesBeen setDefault = new FacilitiesTypesBeen(0, Constants.FACILITY_TYPE);
            facilitiesTypesBeens.add(setDefault);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                FacilitiesTypesBeen typesBeen = new FacilitiesTypesBeen();
                typesBeen.setName(object.getString("name"));
                typesBeen.setId(object.getInt("id"));
                facilitiesTypesBeens.add(typesBeen);
            }
        } catch (Exception e) {
            Logger.logE(TAG, "Exception on getting the category", e);
        }
        Logger.logD(TAG, "facilities type---" + facilitiesTypesBeens.size());
        return facilitiesTypesBeens;
    }

    /**
     * @param array
     * @return
     */
    public List<FacilitiesAreaBeen> getThematicAreaListing(String array) {
        List<FacilitiesAreaBeen> facilitiesTypesBeens = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(array);
            FacilitiesAreaBeen facilitiesAreaBeen1 = new FacilitiesAreaBeen();
            facilitiesAreaBeen1.setName(Constants.THEMATIC_AREA);
            facilitiesTypesBeens.add(facilitiesAreaBeen1);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                FacilitiesAreaBeen facilitiesAreaBeen = new FacilitiesAreaBeen();
                facilitiesAreaBeen.setName(object.getString("name"));
                facilitiesAreaBeen.setId(object.getInt("id"));
                facilitiesTypesBeens.add(facilitiesAreaBeen);
            }
        } catch (JSONException e) {
            Logger.logE(TAG, "", e);
        }
        return facilitiesTypesBeens;
    }
}