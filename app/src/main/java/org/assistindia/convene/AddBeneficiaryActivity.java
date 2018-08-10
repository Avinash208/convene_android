package org.assistindia.convene;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.assistindia.convene.BeenClass.beneficiary.Address;
import org.assistindia.convene.BeenClass.beneficiary.Datum;
import org.assistindia.convene.adapter.spinnercustomadapter.SpinnerAdapter;
import org.assistindia.convene.api.BeneficiaryApis.SaveBeneficiary;
import org.assistindia.convene.api.BeneficiaryApis.UpdateBeneficiary;
import org.assistindia.convene.database.ExternalDbOpenHelper;
import org.assistindia.convene.utils.AddBeneficiaryUtils;
import org.assistindia.convene.utils.Age;
import org.assistindia.convene.utils.Constants;
import org.assistindia.convene.utils.Logger;
import org.assistindia.convene.utils.MultipleAddressPopUp;
import org.assistindia.convene.utils.RestUrl;
import org.assistindia.convene.utils.StringUtils;
import org.assistindia.convene.utils.ToastUtils;
import org.assistindia.convene.utils.UpdateAddressDetails;
import org.assistindia.convene.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public class AddBeneficiaryActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener,UpdateAddressDetails {
    Spinner gender;
    Spinner houseHoldSpinner;
    LinearLayout householdContainer;
    LinearLayout genderContainer;
    Button save;
    Button cancel;
    TextView name;
    TextView age;
    TextView address1;
    TextView address2;
    TextView pincode;
    TextView contactNo;
    String getToken;
    private static final String CHILD_TYPE = "Child";
    private static final String HOUSEHOLD_TYPE = "Household";
    private static final String MOTHER_TYPE = "Mother";
    TextView toolbarTitle;
    List<Datum> houseHoldList = new ArrayList<>();
    ExternalDbOpenHelper dbhelper;
    SharedPreferences sharedPreferences;
    String beneficiaryId;
    ImageView imageMenu;
    TextView genderLabel;
    TextView houseHoldLabel;
    TextView nameLabel;
    TextView ageLabel;
    TextView addressLabel1;
    TextView addressLabel2;
    TextView pincodeLabel;
    TextView contactLabel;
    Typeface face;
    JSONObject jsonObject;
    private TextView errorTexthousehold;
    private TextView errorTextName;
    private TextView errorTextAge;
    private TextView errorTextGender;
    private TextView errorTextContact;
    Button addMoreContact;
    LinearLayout secondaryAddressLayout;
    LinearLayout secondaryContactLayout;
    JSONObject addresssecJsonObject;
    int addressCount = 0;
    List<String> stringList = new ArrayList<>();
    private static String tag = "AddBeneficiaryActivity";
    private HashMap<Integer, EditText> editTextHashMap = new HashMap<>();
    private HashMap<Integer,List<Address>> addressListHashMap=new HashMap<>();
    RestUrl restUrl;
    SharedPreferences sharedPreferences1;
    private String ageString;
    private String contactNoString = "";
    private String nameString;
    private String genderString;
    private String uuidString="";
    private String parentUuidString="";
    private String syncStatusString="";
    private String addressString="";
    private static final String BENEFICIARY_TYPE_ID = "beneficiary_type_id";
    private static final String EDIT_BENEFICIARY = "isEditBeneficiary";
    private static final String CONTACT_NO = "contact_no";
    private static final String GENDER_STRING = "gender";
    private static final String UPDATE_UI_FLAG = "UpdateUi";
    private static final String ISADDITIONALADDRESS_FLAG = "isAddAdditionalAddress";
    private static final String EDITSECONDARY_ADDRESS_FLAG = "editSecondaryAddress";
    private static final String UPDATEFACILITY_FLAG = "UpdateFacilityUi";
    private static final String BENEFICIARY_TABLE = "Beneficiary";
    private static final String BENEFICIARY_NAME = "beneficiaryName";
    private static final String AGE_STRING = "age";
    private static final String STATUS_STRING = "status";
    private static final String UUID_STRING = "uuid";
    private static final String PARENT_ID = "parent_id";
    private static final String PARENT_UUID = "parent_uuid";
    private static final String ADDRESS = "address";
    private static final String SYNC_STATUS = "sync_status";
    private static final String FROM_USER_DETAILS_FLAG = "fromUserDetails";
    AddBeneficiaryUtils addBeneficiaryUtils;
    MultipleAddressPopUp addressPopUp;
    private String parentUuid="";
    private int contactCount=0;
    private CardView addressCardView;
    private TextView addressTitleTextView;
    private RadioGroup ageGroup;
    private TextView dateOfbirthTexView;
    private int setDateView;
    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;
    Age ageModel;
    private TextView agebasedondobTextView;
    private RadioButton yesRadioButton;
    private RadioButton noRadioButton;
    private TextView errorTextDobTextView;
    private TextView errorTextDobpickerTextView;
    private TextView errorTextageDobTextView;
    private EditText aliasNameEditText;
    private String aliasNameString="";
    private String dateOfBirthString="";
    private TextView ageInYearsLabel;
    private TextView ageQuestionLabel;
    private TextView aliasNameLabel;
    private boolean isAddressUpdate;
    private static final String aliasStr= "alias_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_beneficiary);
        initializeVariables();
        setInstances();
        setBundleValues();
        setFontLabels();
        setFieldVisibility();
        setAdapter();
        callOnClickListener();
    }

    /**
     * below lines of code for on click listener for all the widgets
     */
    private void callOnClickListener() {
        SpinnerAdapter dataAdapter1 = new SpinnerAdapter(AddBeneficiaryActivity.this, android.R.layout.simple_spinner_dropdown_item, addBeneficiaryUtils.getGenderList());
        // attaching data adapter to spinner
        gender.setAdapter(dataAdapter1);
        if (sharedPreferences1.getBoolean(EDIT_BENEFICIARY, false) && !MOTHER_TYPE.equals(getToken)) {
            if (("Male").equalsIgnoreCase(genderString)) {
                gender.setSelection(1);
            } else if (("Female").equalsIgnoreCase(genderString)) {
                gender.setSelection(2);
            } else {
                gender.setSelection(3);
            }
        }
        if(addressListHashMap.isEmpty()){
            addressCardView.setVisibility(View.GONE);
        }else{
            addressCardView.setVisibility(View.VISIBLE);
        }
        ageGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId==R.id.radio_yes){
                ageLabel.setVisibility(View.GONE);
                age.setVisibility(View.GONE);
                ageInYearsLabel.setVisibility(View.VISIBLE);
                SupportClass.setRedStar(ageInYearsLabel, getResources().getString(R.string.age_in_years_and_months));
                dateOfbirthTexView.setVisibility(View.VISIBLE);
                agebasedondobTextView.setVisibility(View.VISIBLE);
            }else{
                showAgeInput();
            }
        });
        dateOfbirthTexView.setOnClickListener(view -> {
            setDateView = 1;
            showDateOfBirthDialog(setDateView);
        });

    }

    private void showAgeInput() {
        dateOfbirthTexView.setVisibility(View.GONE);
        agebasedondobTextView.setVisibility(View.GONE);
        ageInYearsLabel.setVisibility(View.GONE);
        ageLabel.setVisibility(View.VISIBLE);
        age.setVisibility(View.VISIBLE);
    }

    /**
     * method to shhow the date picker dialog
     * @param setDateView
     */
    private void showDateOfBirthDialog(int setDateView) {
        Calendar calendar=Calendar.getInstance();
        Calendar minCalendar = Calendar.getInstance();
        if ( mYear != 0)
        {
            calendar.set(mYear,mMonth,mDay);
        }
        DatePickerDialog dpd = new DatePickerDialog(this, mDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        if ((CHILD_TYPE).equals(getToken)) {
            minCalendar.add(Calendar.YEAR, -18);
            dpd.getDatePicker().setMinDate(minCalendar.getTimeInMillis());
        }

        if(setDateView==1){
            dpd.getDatePicker().setMaxDate(new Date().getTime());

            dpd.show();

        }
    }

    /**
     * Date picker dialogue showing
     */
    private DatePickerDialog.OnDateSetListener mDateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
        if(view!=null) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDisplay();
        }
    };

    private void updateDisplay() {
        try {
            if (setDateView == 1) {
                Date date = new Date();
                String userFormate=(new StringBuilder().append(mDay).append("/").append(mMonth + 1).append("/").append(mYear).append(" ")).toString().trim();
                SimpleDateFormat sdf = new SimpleDateFormat(userFormate, Locale.ENGLISH);
                String data = sdf.format(date);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date birthDate = simpleDateFormat.parse(data);
                ageModel = Utils.calculateAge(birthDate);
                agebasedondobTextView.setText(ageModel.getYears()+ "."+ ageModel.getMonths());
                dateOfbirthTexView.setText(data);
            }
        }catch (Exception e){
            Logger.logE("","Exception on setting the date picker in forms",e);
        }
    }

    /**
     * method to set the font and labels for all the widgets
     */
    private void setFontLabels() {
        if (HOUSEHOLD_TYPE.equalsIgnoreCase(getToken)) {
            SupportClass.setRedStar(nameLabel, "Head of family");
            name.setHint("head of family");
        } else {
            SupportClass.setRedStar(nameLabel, getString(R.string.name));
            name.setHint("name");
        }
        ageQuestionLabel.setTypeface(face);
        aliasNameLabel.setTypeface(face);
        ageInYearsLabel.setTypeface(face);
        List<TextView> textViewList=new ArrayList<>();
        textViewList.add(nameLabel);
        textViewList.add(ageLabel);
        textViewList.add(contactLabel);

        addBeneficiaryUtils.setFontStyleLabels(textViewList,face);
    }

    /**
     * method to set some fields make visibility based on the beneficiary selection
     */
    private void setFieldVisibility() {
        try{
            SupportClass.setRedStar(ageQuestionLabel, getString(R.string.do_you_know_date_of_birth));
            if (!houseHoldList.isEmpty()) {
                errorTexthousehold.setVisibility(View.GONE);
                addBeneficiaryUtils.setHouseHoldAdapter(houseHoldList, houseHoldSpinner);
            } else {
                errorTexthousehold.setVisibility(View.VISIBLE);
                errorTexthousehold.setText(R.string.please_add_household);
            }
            if (getToken.equalsIgnoreCase(MOTHER_TYPE)) {
                SupportClass.setRedStar(genderLabel, getString(R.string.gender));
                genderLabel.setVisibility(View.GONE);
                toolbarTitle.setText(R.string.mother_title);
                genderContainer.setVisibility(View.GONE);
                householdContainer.setVisibility(View.VISIBLE);
                houseHoldLabel.setVisibility(View.VISIBLE);
                houseHoldLabel.setTypeface(face);
                contactNo.setVisibility(View.VISIBLE);
                contactLabel.setVisibility(View.VISIBLE);
                SupportClass.setRedStar(houseHoldLabel, getString(R.string.household));
            } else if (getToken.equalsIgnoreCase(HOUSEHOLD_TYPE)) {
                householdContainer.setVisibility(View.GONE);
                houseHoldLabel.setVisibility(View.GONE);
                genderLabel.setVisibility(View.VISIBLE);
                gender.setVisibility(View.VISIBLE);
                SupportClass.setRedStar(genderLabel, getString(R.string.gender));
            } else {
                setViewVisibility();
            }
        }catch (Exception e){
            Logger.logE(tag,"Exception on setvisisbility method",e);
        }

    }

    /**
     * method to set visibility excluding mother and household beneficiairy
     */
    private void setViewVisibility() {
        gender.setVisibility(View.VISIBLE);
        toolbarTitle.setText(getToken);
        SupportClass.setRedStar(genderLabel, getString(R.string.gender));
        gender.setVisibility(View.VISIBLE);
        householdContainer.setVisibility(View.VISIBLE);
        houseHoldLabel.setVisibility(View.VISIBLE);
        houseHoldLabel.setTypeface(face);
        SupportClass.setRedStar(houseHoldLabel, getString(R.string.household));
        genderLabel.setVisibility(View.VISIBLE);
        genderLabel.setTypeface(face);
        genderLabel.setTypeface(face);
        SupportClass.setRedStar(genderLabel, getString(R.string.gender));
    }

    /**
     * method to set all the adapter (location spinners,location types and setting all the text field values
     */
    private void setAdapter() {
        if (sharedPreferences1.getBoolean(EDIT_BENEFICIARY, false) && sharedPreferences1.getBoolean(FROM_USER_DETAILS_FLAG, false)) {
            editBeneficiaryListing();
        }else if (sharedPreferences1.getBoolean(FROM_USER_DETAILS_FLAG, false)) {
            newBeneficiaryFromHousehold();
        }else if (sharedPreferences1.getBoolean(EDIT_BENEFICIARY, false)) {
            editBeneficiaryListing();
        }else{
            /*getting the household data from beneficiary table*/
            houseHoldList = dbhelper.getHouseHoldFromDb();
        }
        if (!houseHoldList.isEmpty()) {
            errorTexthousehold.setVisibility(View.GONE);
            addBeneficiaryUtils.setHouseHoldAdapter(houseHoldList, houseHoldSpinner);
        } else {
            errorTexthousehold.setVisibility(View.VISIBLE);
            errorTexthousehold.setText(R.string.please_add_household);
        }
        toolbarTitle.setText(getToken);
    }

    /**
     * method to the bundle values to string values
     */
    private void setBundleValues() {
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            getToken = extras.getString(Constants.TYPE_VALUE);
            beneficiaryId = extras.getString(BENEFICIARY_TYPE_ID);
            syncStatusString = extras.getString(SYNC_STATUS);
            addressString = extras.getString(ADDRESS);
            ageString = extras.getString(AGE_STRING);
            contactNoString = extras.getString(CONTACT_NO);
            Logger.logD(tag, "Contact number list from been from desccription screen" + contactNoString);
            nameString = extras.getString(BENEFICIARY_NAME);
            genderString = extras.getString(GENDER_STRING);
            uuidString = extras.getString(UUID_STRING);
            parentUuidString=extras.getString(PARENT_UUID);
            syncStatusString = extras.getString(SYNC_STATUS);
            aliasNameString=extras.getString(aliasStr);
            Logger.logD(tag, "parentUuidString" + parentUuidString);
            parentUuid=extras.getString(PARENT_UUID);
            dateOfBirthString=extras.getString(Constants.DOB);
        }else{
            getToken = sharedPreferences.getString(Constants.TYPE_VALUE, "");
            beneficiaryId = sharedPreferences.getString(Constants.BENEFICIARY_TYPE_ID, "");
            syncStatusString = sharedPreferences.getString(Constants.SYNC_STATUS, "");
            addressString = sharedPreferences.getString(ADDRESS, "");
            contactNoString = sharedPreferences.getString(CONTACT_NO,"");
            nameString = sharedPreferences.getString(BENEFICIARY_NAME,"");
            genderString = sharedPreferences.getString(GENDER_STRING,"");
            uuidString = sharedPreferences.getString(UUID_STRING,"");
            parentUuidString=sharedPreferences.getString(PARENT_UUID,"");
            aliasNameString=sharedPreferences.getString(aliasStr,"");
            dateOfBirthString=sharedPreferences.getString(Constants.DOB,"");
            parentUuid=sharedPreferences1.getString(PARENT_UUID,"");
        }

    }

    /**
     * below lines of code for editing the beneficiary details from Listing page
     */
    private void editBeneficiaryListing() {
        try{
            if (!HOUSEHOLD_TYPE.equalsIgnoreCase(getToken)) {
                houseHoldList = dbhelper.getSelectedHouseHoldFromDb(parentUuid);
                if(addressString.isEmpty()){
                    JSONArray jsonArray = new JSONArray(houseHoldList.get(0).getAddress());
                    addressListHashMap = Utils.getAddressListHashMap(jsonArray, syncStatusString);
                }else{
                    JSONArray jsonArray = new JSONArray(addressString);
                    addressListHashMap = Utils.getAddressListHashMap(jsonArray, syncStatusString);
                }
                setAddressCardView(addressListHashMap);
                addressCount=addressListHashMap.size();
            }else{
                JSONArray jsonArray = new JSONArray(addressString);
                addressListHashMap = Utils.getAddressListHashMap(jsonArray, syncStatusString);
                setAddressCardView(addressListHashMap);
                addressCount=addressListHashMap.size();
                Logger.logD(tag, "addressCount value on edit beneficiary" + addressCount);
            }
            if (dateOfBirthString == null)
                dateOfBirthString ="";
            if(dateOfBirthString.isEmpty()){
                noRadioButton.setChecked(true);
                age.setText(ageString);
                showAgeInput();

            }else{
                yesRadioButton.setChecked(true);
                dateOfbirthTexView.setVisibility(View.VISIBLE);
                agebasedondobTextView.setVisibility(View.VISIBLE);
                ageInYearsLabel.setVisibility(View.VISIBLE);
                SupportClass.setRedStar(ageInYearsLabel, getResources().getString(R.string.age_in_years_and_months));
                dateOfbirthTexView.setText(dateOfBirthString);
                agebasedondobTextView.setText(ageString);

                updateMonthAndYear();
            }
            aliasNameEditText.setText(aliasNameString);
            setEditedDetails();

        }catch (Exception e){
            Logger.logE(tag,"Exception on editting the beneficiary details from listing module",e);
        }
    }

    private void updateMonthAndYear() {
        try {
            if (!dateOfBirthString.isEmpty()) {
                Date previousDate = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(dateOfBirthString);
                Calendar calendar =  Calendar.getInstance();
                calendar.setTime(previousDate);
                mYear= calendar.get(Calendar.YEAR);
                mMonth= calendar.get(Calendar.MONTH);
                mDay= calendar.get(Calendar.DAY_OF_MONTH);
            }
        } catch (Exception e) {
            Logger.logE(tag,e.getMessage(),e);
        }
    }

    /**
     * method to set the basic details
     */
    private void setEditedDetails() {

        name.setText(nameString);
        Logger.logD(tag, "Contact number list from setEditedDetails" + contactNoString);
        String contactNoSplitedString = contactNoString.replaceAll("\\[", "").replaceAll("\\]", "");
        List<String> contactNumbersList = Arrays.asList(contactNoSplitedString.split(","));
        if (!contactNumbersList.isEmpty()) {
            contactNo.setText(contactNumbersList.get(0).trim());
            setSecondaryContactNumber(contactNumbersList);
        }
    }

    private void setSecondaryContactNumber(final List<String> contactNumbersList) {
        secondaryContactLayout = findViewById(R.id.secondaryContactLayout);
        secondaryContactLayout.setVisibility(View.VISIBLE);
        for (int i = 1; i < contactNumbersList.size(); i++) {
            try {
                final View boundaryView = getLayoutInflater().inflate(R.layout.contact_number, secondaryContactLayout, false);
                EditText contactEditText = boundaryView.findViewById(R.id.contactNoEdittext);
                TextView removeButton = boundaryView.findViewById(R.id.remove);
                contactEditText.setText(contactNumbersList.get(i).trim());
                removeButton.setOnClickListener(view -> ((LinearLayout) boundaryView.getParent()).removeView(boundaryView));
                secondaryContactLayout.addView(boundaryView);
            } catch (Exception e) {
                Logger.logE("", "", e);
                restUrl.writeToTextFile("Conflict on add contact dynamically ", "", "AddContactDynamic");
            }
        }
    }


    /**
     * below lines of code for new mother and child from household decription module
     */
    private void newBeneficiaryFromHousehold() {
        try{
            Gson gson = new Gson();
            // convert your list to json

            houseHoldList = dbhelper.getSelectedHouseHoldFromDb(parentUuidString);
            if(!houseHoldList.isEmpty()){
                houseHoldList = dbhelper.getSelectedHouseHoldFromDb(parentUuid);
                String jsonAddressList = gson.toJson(houseHoldList.get(0).getAddress());
                JSONArray jsonArray = new JSONArray(jsonAddressList);
                addressListHashMap=Utils.getAddressListHashMap(jsonArray, syncStatusString);
                addressCount=addressListHashMap.size();
                addressListHashMap = Utils.getAddressListHashMap(jsonArray, syncStatusString);
                addressCount=addressListHashMap.size();
                setAddressCardView(addressListHashMap);
            }
        }catch (Exception e){
            Logger.logE(tag,"Exception on creation of mother and child from household module",e);
        }
    }

    /**
     * method to all the instances(sharedprefernce,externalbdhelper etc)
     */
    private void setInstances() {
        face = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Light.ttf");
        restUrl = new RestUrl(this);
        addBeneficiaryUtils = new AddBeneficiaryUtils(AddBeneficiaryActivity.this);
        addressPopUp=new MultipleAddressPopUp(AddBeneficiaryActivity.this);
        sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dbhelper = ExternalDbOpenHelper.getInstance(AddBeneficiaryActivity.this, sharedPreferences.getString(Constants.DBNAME, ""), sharedPreferences.getString("inv_id", ""));
        name.setInputType(InputType.TYPE_CLASS_TEXT);
        name.setFilters(new InputFilter[]{StringUtils.filterALPHAFacility});
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ISADDITIONALADDRESS_FLAG, false);
        editor.putBoolean(EDITSECONDARY_ADDRESS_FLAG, false);
        editor.putBoolean(UPDATE_UI_FLAG, false);
        editor.apply();
    }


    /**
     * method to initialize all the variables
     */
    private void initializeVariables() {
        ageInYearsLabel= findViewById(R.id.ageInYear);
        aliasNameEditText= findViewById(R.id.AliasName);
        errorTextageDobTextView= findViewById(R.id.errorTextageDob);
        errorTextDobTextView= findViewById(R.id.errorTextDob);
        errorTextDobpickerTextView= findViewById(R.id.errorTextDobpicker);
        agebasedondobTextView= findViewById(R.id.agebasedondob);
        dateOfbirthTexView= findViewById(R.id.dateOfbirthTexView);
        addressTitleTextView= findViewById(R.id.addressTitle);
        Button addAddressButton = findViewById(R.id.addAddress);
        secondaryAddressLayout = findViewById(R.id.secondaryAddressLayout);
        addressCardView = findViewById(R.id.addresscard_view);
        householdContainer = findViewById(R.id.householdContainer);
        genderContainer = findViewById(R.id.genderContainer);
        LinearLayout backPress = findViewById(R.id.backPress);
        ageGroup= findViewById(R.id.ageGroup);
        yesRadioButton= findViewById(R.id.radio_yes);
        noRadioButton= findViewById(R.id.radio_no);
        save = findViewById(R.id.saveData);
        cancel = findViewById(R.id.cancel);
        addMoreContact = findViewById(R.id.addMoreContact);
        errorTexthousehold = findViewById(R.id.errorTexthousehold);
        errorTextName = findViewById(R.id.errorTextName);
        errorTextContact = findViewById(R.id.errorTextContact);
        errorTextAge = findViewById(R.id.errorTextAge);
        errorTextGender = findViewById(R.id.errorTextGender);
        addressLabel1 = findViewById(R.id.addressLabel1);
        addressLabel2 = findViewById(R.id.addressLabel2);
        contactLabel = findViewById(R.id.contactLabel);
        pincodeLabel = findViewById(R.id.pincodeLabel);
        genderLabel = findViewById(R.id.genderLabel);
        aliasNameLabel = findViewById(R.id.AliasNameLabel);
        ageQuestionLabel = findViewById(R.id.AgeQuestionLabel);
        nameLabel = findViewById(R.id.nameLabel);
        ageLabel = findViewById(R.id.ageLabel);
        houseHoldLabel = findViewById(R.id.houseHoldLabel);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        imageMenu = findViewById(R.id.imageMenu);
        houseHoldSpinner= findViewById(R.id.beneficiaryhouseHold);
        gender = findViewById(R.id.beneficiarygender);
        name = (EditText) findViewById(R.id.name);
        age = (EditText) findViewById(R.id.age);
        address1 = (EditText) findViewById(R.id.address1);
        address2 = (EditText) findViewById(R.id.address2);
        pincode = (EditText) findViewById(R.id.pincode);
        contactNo = (EditText) findViewById(R.id.contact);
        imageMenu.setVisibility(View.GONE);
        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
        backPress.setOnClickListener(this);
        houseHoldSpinner.setOnItemSelectedListener(this);
        addAddressButton.setOnClickListener(this);
        addMoreContact.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()){
                case R.id.backPress:
                    onBackPressed();
                    break;
                case R.id.saveData:
                    validatBasicDetails();
                    break;
                case R.id.cancel:
                    setCancel();
                    break;
                case R.id.addAddress:
                    callAddressPopUp();
                    break;
                case R.id.addMoreContact:
                    callContactCreation();
                    break;
                default:
                    break;
            }
        }catch (Exception e){
            Logger.logE(tag,"Exception on click of widgets",e);
        }
    }

    private void callContactCreation() {
        if (secondaryContactLayout == null)
            secondaryContactLayout = findViewById(R.id.secondaryContactLayout);
        contactCount++;
        secondaryContactLayout =addBeneficiaryUtils.addMultipleContact(contactCount,editTextHashMap,secondaryContactLayout);

    }

    /**
     * below lines of code for basic validation
     */
    private void validatBasicDetails() {
        try {
            jsonObject = new JSONObject();
            jsonObject.put("partner_id", "");
            /*getting the mothers details*/
            if (!(HOUSEHOLD_TYPE).equals(getToken)) {
                Datum houseHoldDatum = (Datum) houseHoldSpinner.getSelectedItem();
                if ((Constants.SELECT).equals(houseHoldDatum.getName())) {
                    Utils.checkBoundaryValidation(errorTexthousehold, getString(R.string.please_select_household));
                    return;
                }
                errorTexthousehold.setVisibility(View.GONE);
                jsonObject.put(PARENT_UUID,houseHoldDatum.getUuid());
                Logger.logD(tag,"the uuid of household " + houseHoldDatum.getUuid());
                jsonObject.put(PARENT_ID, String.valueOf(houseHoldDatum.getId()));
                jsonObject.put("parent",houseHoldDatum.getName());
            } else {
                jsonObject.put(PARENT_ID, "");
                jsonObject.put(PARENT_UUID,"");
                jsonObject.put("parent","");
            }
            //name details
            if (name.getText().toString().trim().isEmpty()) {
                Utils.checkBoundaryValidation(errorTextName, getString(R.string.please_enter_name));
                name.setFocusable(true);
                return;
            }
            errorTextName.setVisibility(View.GONE);
            jsonObject.put("name", name.getText().toString().trim());
            jsonObject.put(aliasStr,aliasNameEditText.getText().toString());
            // age details
            if(!yesRadioButton.isChecked()&&!noRadioButton.isChecked()){
                errorTextDobTextView.setVisibility(View.VISIBLE);
                Utils.checkBoundaryValidation(errorTextDobTextView,getString(R.string.please_choose));
                return;
            }

            if(yesRadioButton.isChecked()){
                if(!dateOfbirthTexView.getText().toString().contains("Pick Date") && !agebasedondobTextView.getText().toString().isEmpty()){
                    String userAgeInMonths=agebasedondobTextView.getText().toString();
                    if(!validateAge(getToken,userAgeInMonths,errorTextageDobTextView,dateOfbirthTexView.getText().toString(),agebasedondobTextView))
                        return;
                    errorTextageDobTextView.setVisibility(View.GONE);
                }else{
                    errorTextDobpickerTextView.setVisibility(View.VISIBLE);
                    Utils.checkBoundaryValidation(errorTextDobpickerTextView, "Please select date of birth");
                    errorTextDobpickerTextView.setFocusable(true);
                    return;
                }
            }else{
                String userAge = age.getText().toString().trim();
                if (userAge.isEmpty()) {
                    Utils.checkBoundaryValidation(errorTextAge, getString(R.string.please_enter_age));
                    age.setFocusable(true);
                    return;
                }
                if(!validateAge(getToken,userAge,errorTextAge,"",age))
                    return;
                errorTextAge.setVisibility(View.GONE);
            }



           /*gender details*/
            if (!(MOTHER_TYPE).equals(getToken)) {
                if (gender.getSelectedItem().toString().equalsIgnoreCase(Constants.SELECT_GENDER)) {
                    Utils.checkBoundaryValidation(errorTextGender, getString(R.string.please_select_gender));
                    return;
                }
                errorTextGender.setVisibility(View.GONE);
                jsonObject.put(GENDER_STRING, gender.getSelectedItem().toString().toLowerCase());
            } else {
                jsonObject.put(GENDER_STRING, "female");
            }


            if (contactNo.getText().toString().trim().isEmpty() || contactNo.getText().toString().trim().length() < 10) {
                Utils.checkBoundaryValidation(errorTextContact, getString(R.string.please_enter_10_digit_mobile_number));
                contactNo.setFocusable(true);
                return;
            } else if ((!StringUtils.isValidMobileNumber(contactNo.getText().toString().trim()))) {
                Utils.checkBoundaryValidation(errorTextContact, getString(R.string.please_enter_valid_mobile_number));
                contactNo.setFocusable(true);
                return;
            }
            stringList.add(contactNo.getText().toString().trim());
            errorTextContact.setVisibility(View.GONE);

            jsonObject.put("btype", getToken);
            jsonObject.put(BENEFICIARY_TYPE_ID, beneficiaryId);
            if (!addressListHashMap.isEmpty()) {
                addresssecJsonObject = addBeneficiaryUtils.getAddressJsonFrom(addressListHashMap);
            }else {
                ToastUtils.displayToastUi("Please enter primary details",this);
                return;
            }
            if (secondaryContactLayout != null) {
                int dynamicLinearChildCount = secondaryContactLayout.getChildCount();
                if (dynamicLinearChildCount > 0) {
                    stringList = addBeneficiaryUtils.getDynamicContactValues(this,stringList,secondaryContactLayout);
                    //Modify by guru
                    if(stringList.isEmpty())
                        return;
                }
            }

            jsonObject.put(ADDRESS, addresssecJsonObject);
            jsonObject.put(CONTACT_NO, stringList.toString());
            if ( (sharedPreferences1.getBoolean(EDIT_BENEFICIARY, false))&&!uuidString.isEmpty()) {
                jsonObject.put("UUID", String.valueOf(uuidString));
                jsonObject.put(PARENT_UUID,parentUuidString);
            }
            else
            {
                UUID uuid = UUID.randomUUID();
                jsonObject.put("UUID", String.valueOf(uuid));

            }
            callBeneficiarySubmit(jsonObject);
        }catch (Exception e){
            Logger.logE(tag,"",e);
        }
    }

    private Boolean validateAge(String getToken, String userAge, TextView errorTextDobTextView, String dateOfBirth, TextView dobTextView) {
        try {
            String[] ageLength=userAge.split("\\.");
            if ((CHILD_TYPE).equals(getToken)) {
                if(ageLength.length>1){
                    if((Integer.parseInt(ageLength[0])>=0 && Integer.parseInt(ageLength[0])<=18)&& Integer.parseInt(ageLength[1])<=11){
                        jsonObject.put(AGE_STRING, dobTextView.getText().toString().trim());
                        jsonObject.put("dob",dateOfBirth);
                    } else {
                        Utils.checkBoundaryValidation(errorTextDobTextView, getString(R.string.please_enter_age_between_0_to_18));
                        dobTextView.setFocusable(true);
                        return false;
                    }
                }else{
                    if((Integer.parseInt(ageLength[0])>=0 && Integer.parseInt(ageLength[0])<=18 )){
                        jsonObject.put(AGE_STRING, dobTextView.getText().toString().trim());
                        jsonObject.put("dob",dateOfBirth);
                    } else {
                        Utils.checkBoundaryValidation(errorTextDobTextView, getString(R.string.please_enter_age_between_0_to_18));
                        dobTextView.setFocusable(true);
                        return false;
                    }
                }

            } else {
                if(ageLength.length>1){
                    if ((Integer.parseInt(ageLength[0]) >= 1 && Integer.parseInt(ageLength[0]) <= 120)&&(Integer.parseInt(ageLength[1])<12&& !ageLength[1].isEmpty())) {
                        jsonObject.put(AGE_STRING, dobTextView.getText().toString().trim());
                        jsonObject.put("dob",dateOfBirth);
                    } else {
                        Utils.checkBoundaryValidation(errorTextDobTextView, getString(R.string.please_enter_valid_age));
                        dobTextView.setFocusable(true);
                        return false;
                    }
                }else{
                    if ((Integer.parseInt(ageLength[0]) >= 1 && Integer.parseInt(ageLength[0]) <= 120)) {
                        jsonObject.put(AGE_STRING, dobTextView.getText().toString().trim());
                        jsonObject.put("dob",dateOfBirth);
                    } else {
                        Utils.checkBoundaryValidation(errorTextDobTextView, getString(R.string.please_enter_valid_age));
                        dobTextView.setFocusable(true);
                        return false;
                    }
                }

            }
            errorTextDobTextView.setVisibility(View.GONE);
        }catch (Exception e){
            Logger.logE(tag,"Exception on setting age",e);
        }

        return true;

    }

    /**
     *
     * @param jsonObject
     */
    private void callBeneficiarySubmit(JSONObject jsonObject) {
        try {
            SharedPreferences.Editor editor1 = sharedPreferences.edit();
            editor1.putBoolean(FROM_USER_DETAILS_FLAG, false);
            editor1.putBoolean(UPDATE_UI_FLAG, true);
            editor1.putBoolean(UPDATEFACILITY_FLAG, false);
            editor1.putBoolean("HouseholdUpdateApiStatus", false);
            editor1.apply();
            Logger.logD(tag, "json response to backend" + jsonObject.toString());
            Logger.logD(tag, "json response tosyncStatusString " + syncStatusString);
            if (( sharedPreferences1.getBoolean(EDIT_BENEFICIARY, false)) || sharedPreferences1.getBoolean(FROM_USER_DETAILS_FLAG, false)) {
                if (("2").equalsIgnoreCase(syncStatusString) || ("3").equalsIgnoreCase(syncStatusString)) {
                    jsonObject.put(STATUS_STRING, "Update");
                    dbhelper.updateBeneficiaryData(jsonObject, BENEFICIARY_TABLE,isAddressUpdate);
                    ToastUtils.displayToast(name.getText().toString() + " updated successfully", this);
                }else{
                    jsonObject.put(STATUS_STRING, "Offline");
                    dbhelper.updateBeneficiaryData(jsonObject, BENEFICIARY_TABLE,isAddressUpdate);
                }
            }else{
                jsonObject.put(STATUS_STRING, "Offline");
                Logger.logV(tag, "Database name in preference " + sharedPreferences.getString(Constants.DBNAME, ""));
                long insertrecordOffline = dbhelper.insertIntoBeneficiaryTemp(jsonObject);
                Logger.logV(tag, "Inserted into benefiicary temp " + insertrecordOffline);
            }
            if(Utils.haveNetworkConnection(this)){
                if (sharedPreferences1.getBoolean(EDIT_BENEFICIARY, false)  || sharedPreferences1.getBoolean(FROM_USER_DETAILS_FLAG, false)) {
                    new UpdateBeneficiary(this, getString(R.string.main_Url) + "beneficiary/householdupdate/").execute();
                }else{
                    new SaveBeneficiary(this, getString(R.string.main_Url) + "beneficiary/addhousehold/").execute();
                }
            }
            finish();
        } catch (Exception e) {
            Logger.logE("", "", e);
        }
    }

    /**
     * method to call the address details popup
     */
    private void callAddressPopUp() {
        SharedPreferences.Editor editor3 = sharedPreferences.edit();
        editor3.putBoolean(ISADDITIONALADDRESS_FLAG, true);

        editor3.putBoolean(EDITSECONDARY_ADDRESS_FLAG, false);
        editor3.apply();
        addressListHashMap=addressPopUp.showAddMultipleAddressPopUp(addressListHashMap, addressCount);
    }


    private void setCancel() {
        if (sharedPreferences1.getBoolean(EDIT_BENEFICIARY, false) || sharedPreferences1.getBoolean(FROM_USER_DETAILS_FLAG, false)) {
            SharedPreferences.Editor editor2 = sharedPreferences.edit();
            editor2.putBoolean(UPDATE_UI_FLAG, true);
            editor2.putBoolean(UPDATEFACILITY_FLAG, false);
            editor2.apply();
            finish();
        } else {
            finish();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if (addressString == null)
            addressString ="";
        if(adapterView.getId()==R.id.beneficiaryhouseHold &&  addressString.isEmpty()){
            addressListHashMap.put(addressCount,houseHoldList.get(position).getAddress());
            setAddressCardView(addressListHashMap);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Logger.logD(tag,"Nothing selected in dropdown");
    }

    @Override
    public void onUpdateAddressSuccess(HashMap<Integer, List<Address>> listHashMap) {
        addressListHashMap = listHashMap;
        addressCount = listHashMap.size();
        setAddressCardView(listHashMap);
        isAddressUpdate = true;

    }

    private void setAddressCardView(HashMap<Integer, List<Address>> listHashMap) {
        if(!listHashMap.isEmpty()){
            secondaryAddressLayout.setVisibility(View.VISIBLE);
            addressCardView.setVisibility(View.VISIBLE);
            addBeneficiaryUtils.setMultipleAddressView(addressPopUp,listHashMap,secondaryAddressLayout);
        }else{
            addressTitleTextView.setVisibility(View.GONE);
            addressCardView.setVisibility(View.GONE);
            addressCount=0;
        }
    }
}
