package org.assistindia.convene.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.assistindia.convene.AddBeneficiaryActivity;
import org.assistindia.convene.BeenClass.beneficiary.Address;
import org.assistindia.convene.BeenClass.beneficiary.Datum;
import org.assistindia.convene.R;
import org.assistindia.convene.api.MeetingAPIs.BeneficiaryAsyncTask;
import org.assistindia.convene.database.ExternalDbOpenHelper;
import org.assistindia.convene.network.ClusterToTypo;
import org.assistindia.convene.utils.Constants;
import org.assistindia.convene.utils.Logger;
import org.assistindia.convene.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;




public class UserDetailsFragment extends Fragment implements ClusterToTypo {
    private String beneficiaryType;
    private ExternalDbOpenHelper dbOpenHelper;
    private String parentId;
    private String beneficiaryName;
    private String locationName;
    private String parent;
    private String address;
    String partner;
    SharedPreferences defaultPreferences;
    private String btype;
    private static final String BENEFICIARIES_TITLE ="Beneficiaries";
    private static final String MOTHER_TYPE="Mother";
    private static final String CHILD_TYPE="Child";
    private static final String TAG="UserDetailsFragment";
    private LinearLayout motherLinear;
    private LinearLayout childLinear;
    private TextView nameTextView;
    private TextView locationTextView;
    private TextView parentTextView;
    private TextView addressTextView;
    List<Datum> motherList=new ArrayList<>();
    List<Datum> childList=new ArrayList<>();
    String typeValue;
    Intent intent;
    IntentFilter filter;
    private TextView contactText;
    private String contactNo;
    private TextView ageTextView;
    private String pincode;
    TextView pincodeText;
    private LinearLayout parentLinear;
    private LinearLayout contactLinear;
    private LinearLayout ageLinear;
    private String beneficiaryAge;
    private String syncStatus="";
    private String addressId="";
    private String leastLocationId="";
    private String address1String;
    private String uuidString="";
    private static final String BENEFICIARY_TYPE="beneficiary_type";
    private static final String HOUSEHOLD_BTYPE="Household";
    private static final String PARENT_ID="parent_id";
    private static final String ADDRESS_ID="address_id";
    private static final String ADDRESS_STRING="address";
    private static final String CONTACT_NO_STRING="contact_no";
    private static final String SYNC_STATUS_STRING="sync_status";
    private static final String PARENT_UUID_STRING="parent_uuid";
    private Myreceiver beneficiryReceiver;
    private int locationID;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args=getArguments();
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String parentUuidString;

        if(args!=null){
            beneficiaryType=args.getString(BENEFICIARY_TYPE);
            if(HOUSEHOLD_BTYPE.equalsIgnoreCase(beneficiaryType)){
                parentId=args.getString("id");
            }else{
                parentId=args.getString(PARENT_ID);
            }
            typeValue = args.getString("typeValue");
            leastLocationId=args.getString("location_id");
            btype=args.getString("typeHeaderName");
            addressId=args.getString(ADDRESS_ID);
            if(!(BENEFICIARIES_TITLE).equals(btype)){
              address1String=args.getString("address1");
            }
            beneficiaryName=args.getString("beneficiaryName");
            locationName=args.getString("locationName");
            parent=args.getString("parent_name");
            address=args.getString(ADDRESS_STRING);
            partner=args.getString("partner_name");
            contactNo=args.getString(CONTACT_NO_STRING);
            beneficiaryAge=args.getString("age");
            syncStatus=args.getString(SYNC_STATUS_STRING);
            pincode=args.getString("pincode");
            uuidString=args.getString("uuid");
            parentUuidString =args.getString(PARENT_UUID_STRING);
            Logger.logD(TAG,"parentUuidString of selected household " + parentUuidString);
        }else{
            locationName= defaultPreferences.getString(Constants.CLUSTER_NAME,"");
            beneficiaryName= defaultPreferences.getString(Constants.BENEFICIARY_NAME,"");
            beneficiaryType= defaultPreferences.getString(Constants.TYPE_VALUE,"");
            typeValue = defaultPreferences.getString(Constants.TYPE_VALUE,"");
            if(!HOUSEHOLD_BTYPE.equalsIgnoreCase(typeValue)){
                parentId=defaultPreferences.getString(Constants.PARENT_ID,"");
            }else{
                parentId=defaultPreferences.getString(Constants.ID,"");
            }
            Logger.logD(TAG,"Household parent id" + parentId);
            leastLocationId=defaultPreferences.getString(Constants.LOCATION_ID,"");
            addressId=defaultPreferences.getString(ADDRESS_ID,"");
            beneficiaryAge=defaultPreferences.getString(Constants.AGE,"");
            parent=defaultPreferences.getString(Constants.PARENT,"");
            address1String=defaultPreferences.getString(Constants.ADDRESS1,"");
            address=defaultPreferences.getString("address","");
            btype= defaultPreferences.getString(Constants.TYPE_NAME,"");
            contactNo=defaultPreferences.getString(Constants.CONTACT_NO,"");
            pincode=defaultPreferences.getString(Constants.PINCODE,"");
            syncStatus=defaultPreferences.getString(Constants.SYNC_STATUS,"");
            uuidString=defaultPreferences.getString("uuid","");
            parentUuidString =defaultPreferences.getString(PARENT_UUID_STRING,"");
            Logger.logD(TAG,"uuid of selected household " + uuidString);
            Logger.logD(TAG,"parentUuidString of selected household " + parentUuidString);
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.userdetails_fragment, container, false);
        dbOpenHelper= ExternalDbOpenHelper.getInstance(getActivity(), defaultPreferences.getString(Constants.DBNAME,""), defaultPreferences.getString("uId",""));
        beneficiryReceiver = new Myreceiver();
        filter = new IntentFilter("BeneficiaryIntentReceiver");
        initiVariables(view);
        setTextValues();
        methodToCallBeneficiaryListing();
        setBeneficiaryListingView();
        return view;

    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(beneficiryReceiver, filter);
        Logger.logV("Userdetails","onResume called");
        methodToCallBeneficiaryListing();

    }

    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
        }catch (Exception e){
            Logger.logE(TAG," Exception on unregisterReceiver ",e);
        }
    }
    public class Myreceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
               new BeneficiaryAsyncTask(context, getActivity(), UserDetailsFragment.this, defaultPreferences.getString("uId", "")).execute();
            } catch (Exception e) {
                Logger.logE(TAG,"",e);
            }
        }
    }
    private void setBeneficiaryListingView() {
        Logger.logV(TAG,"parent id value" + parentId);
        if(btype != null && (BENEFICIARIES_TITLE).equals(btype)) {
            if((HOUSEHOLD_BTYPE).equalsIgnoreCase(beneficiaryType)){
                parentLinear.setVisibility(View.GONE);
                parentTextView.setVisibility(View.GONE);
                motherLinear.setVisibility(View.VISIBLE);
                getMotherChildDetails();
            }else if((MOTHER_TYPE).equalsIgnoreCase(beneficiaryType)){
                motherLinear.setVisibility(View.GONE);
                childLinear.setVisibility(View.VISIBLE);
                createChildListingListView();
            }else{
                motherLinear.setVisibility(View.GONE);
                childLinear.setVisibility(View.GONE);
            }
        }else{
            motherLinear.setVisibility(View.GONE);
            childLinear.setVisibility(View.GONE);
        }
    }
    private void methodToCallBeneficiaryListing() {
        if(defaultPreferences.getBoolean("UpdateUi",false) ||defaultPreferences.getBoolean("UpdateBeneficiary",false)){
            if(Utils.haveNetworkConnection(getActivity())){
                new BeneficiaryAsyncTask(getActivity(), getActivity(), this, defaultPreferences.getString("uId", "")).execute();            }else {
                setBeneficiaryListingView();
            }
        }else{
            setBeneficiaryListingView();
        }

    }
    private void callToNextActivity(String childType, String beneificiaryTypeid) {
        SharedPreferences.Editor editor=defaultPreferences.edit();
        editor.putBoolean("isEditBeneficiary",false);
        editor.putBoolean("fromUserDetails",true);
        editor.putBoolean("UpdateBeneficiary",false);
        editor.apply();
        intent=new Intent(getActivity(), AddBeneficiaryActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString(ADDRESS_STRING,address);
        bundle.putString("household_id",parentId);
        bundle.putString(SYNC_STATUS_STRING,syncStatus);
        bundle.putString("location_id",leastLocationId);
        bundle.putString(CONTACT_NO_STRING,contactNo);
        bundle.putString("parent_uuid",uuidString);
        bundle.putString(Constants.TYPE_VALUE,childType);
        bundle.putString("beneficiary_type_id",beneificiaryTypeid);
        intent.putExtra("Beneficiary","NEW_BENEFICIARY");
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }
    /**
     * method to set the values to all the fields
     */
    private void setTextValues() {
        nameTextView.setText(beneficiaryName);
        Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");

        locationTextView.setTypeface(customFont);
        Logger.logD(TAG,"address array from bundle extras" + address);
        try {
            addressTextView.setTypeface(customFont);
            pincodeText.setTypeface(customFont);
            contactText.setTypeface(customFont);
            if((BENEFICIARIES_TITLE).equals(btype)){
                contactLinear.setVisibility(View.VISIBLE);
                String contactNoSplitedString=contactNo.replaceAll("\\[", "").replaceAll("\\]","");
                List<String>contactNumbersList= Arrays.asList(contactNoSplitedString.split(","));
                contactText.setText(contactNumbersList.get(0));
                ageLinear.setVisibility(View.VISIBLE);
                ageTextView.setText(beneficiaryAge);
                JSONArray jsonArray=new JSONArray(address);
                List<Address> addressList=Utils.getAddressList(jsonArray,syncStatus);

                if(!HOUSEHOLD_BTYPE.equalsIgnoreCase(beneficiaryType)){
                    parentTextView.setText(parent);
                    parentTextView.setTypeface(customFont);
                }else{
                    parentLinear.setVisibility(View.GONE);
                }
                if(addressList.isEmpty()){
                    pincodeText.setText(pincode);
                    addressTextView.setText(address1String);
                    locationTextView.setText(locationName);
                }else{
                    locationTextView.setText(addressList.get(0).getLeastLocationName());
                    addressTextView.setText(addressList.get(0).getAddress1());
                    pincodeText.setText(addressList.get(0).getPincode());
                }
            }else{
                ageLinear.setVisibility(View.GONE);
                parentLinear.setVisibility(View.GONE);
                contactLinear.setVisibility(View.GONE);
                pincodeText.setText(pincode);
                addressTextView.setText(address1String);
                locationTextView.setText(locationName);
            }
        }catch (Exception e){
            Logger.logE(TAG,"Exception on getting the address array",e);
        }


    }
    /**
     * method to initialize the variables
     * @param view
     */
    private void initiVariables(View view) {
        motherLinear = view.findViewById(R.id.motherLinear);
        childLinear = view.findViewById(R.id.childLinear);
        nameTextView = view.findViewById(R.id.nameText);
        locationTextView = view.findViewById(R.id.locationText);
        parentTextView= view.findViewById(R.id.parentText);
        addressTextView= view.findViewById(R.id.addressText);
        contactText= view.findViewById(R.id.contactText);
        parentLinear= view.findViewById(R.id.parentPanel);
        pincodeText = view.findViewById(R.id.pincodeText);
        contactLinear= view.findViewById(R.id.contactLinear);
        ageLinear= view.findViewById(R.id.agePanel);
        ageTextView= view.findViewById(R.id.ageText);


    }
    /**
     * method to display the list of mother and child in listview
     */
    private void getMotherChildDetails() {
        createMotherListingListview();
        createChildListingListView();
    }

    private void createChildListingListView() {
        childList = dbOpenHelper.getMothersListBasedOnParent("4", uuidString);
        childLinear.removeAllViews();
        final View childView=getActivity().getLayoutInflater().inflate(R.layout.activity_child_listing_view, childLinear, false);
        final TextView addChildTextView= childView.findViewById(R.id.addChild);
        final TextView emptytextview1= childView.findViewById(R.id.emptytextview1);
        final LinearLayout grandChildLinear= childView.findViewById(R.id.grandChildLinear);
        final ListView childnamesListview= childView.findViewById(R.id.childnamesListview);
        final TextView showTextView= childView.findViewById(R.id.show);
        final TextView hideTextView= childView.findViewById(R.id.hide);
        childLinear.addView(childView);

        hideTextView.setVisibility(View.GONE);
        showTextView.setVisibility(View.GONE);

        if(childList.isEmpty()){
            childnamesListview.setVisibility(View.GONE);
            grandChildLinear.setVisibility(View.GONE);
            emptytextview1.setVisibility(View.VISIBLE);
        }else{
            emptytextview1.setVisibility(View.GONE);
            grandChildLinear.setVisibility(View.VISIBLE);
            setGrancdChildView(grandChildLinear,childList);
        }

        addChildTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               callToNextActivity(CHILD_TYPE,"4");
            }
        });
        hideTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                childnamesListview.setVisibility(View.GONE);
                grandChildLinear.setVisibility(View.GONE);
                hideTextView.setVisibility(View.GONE);
                emptytextview1.setVisibility(View.GONE);
                showTextView.setVisibility(View.VISIBLE);

            }
        });
        showTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideTextView.setVisibility(View.GONE);
                showTextView.setVisibility(View.GONE);
                grandChildLinear.setVisibility(View.GONE);
                childnamesListview.setVisibility(View.GONE);
                emptytextview1.setVisibility(View.GONE);
                setUIforInfo(hideTextView,showTextView,childnamesListview,grandChildLinear,emptytextview1,childLinear,childList);
            }
        });

    }
    /**
     * method to create the view for list of mother for selected house hold
     */
    private void createMotherListingListview() {
        motherList = dbOpenHelper.getMothersListBasedOnParent("3", uuidString);
        motherLinear.removeAllViews();
        final View childView=getActivity().getLayoutInflater().inflate(R.layout.activity_mother_listing_view, motherLinear, false);
        final TextView addMotherTextView= childView.findViewById(R.id.addMother);
        final TextView emptytextview0= childView.findViewById(R.id.emptytextview0);
        final ListView motherNameListview= childView.findViewById(R.id.motherNameListview);
        final LinearLayout grandMotherLinear= childView.findViewById(R.id.grandMotherLinear);
        final TextView showTextView= childView.findViewById(R.id.show);
        final TextView hideTextView= childView.findViewById(R.id.hide);
        showTextView.setVisibility(View.GONE);
        hideTextView.setVisibility(View.GONE);
        if(motherList.isEmpty()){
            motherNameListview.setVisibility(View.GONE);
            grandMotherLinear.setVisibility(View.GONE);
            emptytextview0.setVisibility(View.VISIBLE);
        }else{
            emptytextview0.setVisibility(View.GONE);
            grandMotherLinear.setVisibility(View.VISIBLE);
            setGrancdChildView(grandMotherLinear, motherList);
        }
        motherLinear.addView(childView);
        addMotherTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               callToNextActivity(MOTHER_TYPE,"3");
            }
        });
        showTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUIforInfo(hideTextView,showTextView,motherNameListview,grandMotherLinear,emptytextview0,grandMotherLinear,motherList);
            }
        });

        hideTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTextView.setVisibility(View.GONE);
                hideTextView.setVisibility(View.GONE);
                emptytextview0.setVisibility(View.GONE);
                grandMotherLinear.setVisibility(View.GONE);
                motherNameListview.setVisibility(View.GONE);
            }
        });

    }
    /**
     * @param hideTextView
     * @param showTextView
     * @param motherNameListview
     * @param grandMotherLinear
     * @param emptytextview0
     * @param motherList
     */
    private void setUIforInfo(TextView hideTextView, TextView showTextView, ListView motherNameListview, LinearLayout grandMotherLinear, TextView emptytextview0, LinearLayout motherLinear, List<Datum> motherList) {
        hideTextView.setVisibility(View.GONE);
        showTextView.setVisibility(View.GONE);
        if(motherList.isEmpty()){
            motherNameListview.setVisibility(View.GONE);
            grandMotherLinear.setVisibility(View.GONE);
            emptytextview0.setVisibility(View.VISIBLE);
        }else{
            emptytextview0.setVisibility(View.GONE);
            grandMotherLinear.setVisibility(View.VISIBLE);
            setGrancdChildView(grandMotherLinear, this.motherList);
        }

    }
    /**
     *  @param grandMotherLinear
     * @param beneficiaryList
     */
    private void setGrancdChildView(LinearLayout grandMotherLinear, final List<Datum> beneficiaryList) {
        grandMotherLinear.removeAllViews();
       try{

           for (int i=0;i<beneficiaryList.size();i++){
               final View grandChildView=getActivity().getLayoutInflater().inflate(R.layout.item_linear_single, grandMotherLinear, false);
               TextView alertTextView= grandChildView.findViewById(R.id.alertTextView);
               TextView textView= grandChildView.findViewById(R.id.ageTextview);
               TextView contactTextview= grandChildView.findViewById(R.id.contactTextview);
               TextView editTextView=grandChildView.findViewById(R.id.editTextview);
               if(("Offline").equalsIgnoreCase(beneficiaryList.get(i).getStatus())){
                   alertTextView.setText(String.format("Name: %s", beneficiaryList.get(i).getName()+ "( " + beneficiaryList.get(i).getStatus()) +" )");
               }else{
                   alertTextView.setText(String.format("Name: %s", beneficiaryList.get(i).getName() + beneficiaryList.get(i).getStatus()));

               }
               textView.setText(String.format("Age: %s", beneficiaryList.get(i).getAge()));
               String contactNumber=beneficiaryList.get(i).getContactNo().toString();
               String contactNoSplitedString=contactNumber.replaceAll("\\[", "").replaceAll("\\]","");
               List<String>contactNumbersList= Arrays.asList(contactNoSplitedString.split(","));
               contactTextview.setText(String.format("Contact No: %s", contactNumbersList.get(0)));
               final int finalI = i;
               editTextView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       setToPreference(beneficiaryList,finalI);
                       Intent intent1=new Intent(getActivity(), AddBeneficiaryActivity.class);
                       SharedPreferences.Editor editor1=defaultPreferences.edit();
                       editor1.putBoolean("fromUserDetails",true);
                       editor1.putBoolean("isEditBeneficiary",true);
                       editor1.putBoolean("isFacility",false);
                       editor1.apply();
                       Gson gson = new Gson();
                       String jsonArry =gson.toJson(beneficiaryList);
                       setToBundle(finalI,beneficiaryList,intent1);
                       intent1.putExtra("Beneficiary","EDIT_BENEFICIARY");
                       getActivity().startActivity(intent1);
                   }
               });
               grandMotherLinear.addView(grandChildView);
           }
       }catch (Exception e){
           Logger.logD("Exception",""+e);
       }

    }

    private void setToBundle(int position, List<Datum> beneficiaryList, Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putString("id", String.valueOf(beneficiaryList.get(position).getId()));
        bundle.putString(PARENT_ID,String.valueOf(beneficiaryList.get(position).getParentId()));
        bundle.putString(PARENT_UUID_STRING,beneficiaryList.get(position).getParent_uuid());
        bundle.putString(BENEFICIARY_TYPE, beneficiaryList.get(position).getBtype());
        bundle.putString("id",String.valueOf(beneficiaryList.get(position).getId()));
        bundle.putString(PARENT_ID,String.valueOf(beneficiaryList.get(position).getParentId()));
        bundle.putString(BENEFICIARY_TYPE, beneficiaryList.get(position).getBtype());
        Logger.logV("BeneficiaryAdapter","beneficiary type in beneficiary adapter ==>" +beneficiaryList.get(position).getBtype());
        Logger.logV("BeneficiaryAdapter","beneficiary type in beneficiary id adapter ==>" +beneficiaryList.get(position).getId());
        bundle.putString("parent_name",beneficiaryList.get(position).getParent());
        try{
            JSONArray addressArray=new JSONArray();
            for(int j=0;j<beneficiaryList.get(position).getAddress().size();j++){
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("address1",beneficiaryList.get(position).getAddress().get(j).getAddress1());
                jsonObject.put("address2",beneficiaryList.get(position).getAddress().get(j).getAddress2());
                jsonObject.put("least_location_name",beneficiaryList.get(position).getAddress().get(j).getLeastLocationName());
                addressId=beneficiaryList.get(position).getAddress().get(0).getAddressId();
                jsonObject.put(ADDRESS_ID,beneficiaryList.get(position).getAddress().get(j).getAddressId());
                jsonObject.put("proof_id",beneficiaryList.get(position).getAddress().get(j).getProofId());
                jsonObject.put("boundary_id",beneficiaryList.get(position).getAddress().get(j).getBoundaryId());
                jsonObject.put("pincode",beneficiaryList.get(position).getAddress().get(j).getPincode());
                jsonObject.put("primary",beneficiaryList.get(position).getAddress().get(j).getPrimary());
                jsonObject.put("location_level",beneficiaryList.get(position).getAddress().get(j).getLocationLevel());
                addressArray.put(jsonObject);
            }
            locationName=beneficiaryList.get(position).getAddress().get(0).getLeastLocationName();

            locationID=beneficiaryList.get(position).getAddress().get(0).getBoundaryId();
            SharedPreferences.Editor editor1=defaultPreferences.edit();
            editor1.putString(ADDRESS_STRING,addressArray.toString());
            editor1.putString(ADDRESS_ID,addressId);
            editor1.apply();
            bundle.putString(ADDRESS_STRING,addressArray.toString());
        }catch (Exception e){
            Logger.logE("","",e);
        }
        bundle.putString("boundary_id", String.valueOf(locationID));
        bundle.putString(CONTACT_NO_STRING,beneficiaryList.get(position).getContactNo().toString());
        bundle.putString("partner_name",beneficiaryList.get(position).getPartner());
        bundle.putString("beneficiaryName",beneficiaryList.get(position).getName());
        bundle.putString("uuid",beneficiaryList.get(position).getUuid());
        bundle.putString(PARENT_UUID_STRING,beneficiaryList.get(position).getParent_uuid());
        bundle.putString(ADDRESS_ID,addressId);
        bundle.putString("household_id",parentId);
        bundle.putString("dob",beneficiaryList.get(position).getDateOfBirth());
        bundle.putString("alias_name",beneficiaryList.get(position).getAliasName());
        bundle.putString(SYNC_STATUS_STRING,syncStatus);
        bundle.putString("gender",beneficiaryList.get(position).getGender());
        bundle.putString("beneficiary_type_id",String.valueOf(beneficiaryList.get(position).getBeneficiaryTypeId()));
        bundle.putString(Constants.TYPE_VALUE, beneficiaryList.get(position).getBeneficiaryType());
        bundle.putString(SYNC_STATUS_STRING,String.valueOf(beneficiaryList.get(position).getSyncStatus()));
        bundle.putString("age",beneficiaryList.get(position).getAge());
        intent.putExtras(bundle);
    }

    private void setToPreference(List<Datum> beneficiaryList, int position) {
        SharedPreferences.Editor editor=defaultPreferences.edit();
        editor.putString(Constants.BENEFICIARY_ID,String.valueOf(beneficiaryList.get(position).getId()));
        editor.putString(Constants.BENEFICIARY_NAME,beneficiaryList.get(position).getName());
        editor.putString(Constants.BENEFICIARY_TYPE_ID,String.valueOf(beneficiaryList.get(position).getBeneficiaryTypeId()));
        editor.putString(Constants.UUID,beneficiaryList.get(position).getUuid());
        editor.putString(Constants.AGE,beneficiaryList.get(position).getAge());
        editor.putString(Constants.GENDER,beneficiaryList.get(position).getGender());
        editor.putString(Constants.SYNC_STATUS,beneficiaryList.get(position).getStatus());
        Logger.logV("uuid","uuid" + beneficiaryList.get(position).getUuid()+"beneficiaryName-->"+beneficiaryList.get(position).getName());
        editor.putString(Constants.PARTNER,beneficiaryList.get(position).getPartner());
        editor.putString(Constants.PARENT,beneficiaryList.get(position).getParent());
        editor.putString(Constants.PARENT_ID, String.valueOf(beneficiaryList.get(position).getParentId()));
        editor.putString(Constants.CONTACT_NO,beneficiaryList.get(position).getContactNo().toString());
        editor.putString(Constants.TYPE_VALUE, beneficiaryList.get(position).getBeneficiaryType());
        editor.putString(Constants.ID,String.valueOf(beneficiaryList.get(position).getId()));
        editor.putString(Constants.AGE,beneficiaryList.get(position).getAge());
        editor.putString("alias_name",beneficiaryList.get(position).getAliasName());
        editor.apply();
    }

    @Override
    public void callTypoScreen(boolean flag) {
        Logger.logD(TAG,"falag value" + flag);
        setBeneficiaryListingView();
    }

    @Override
    public void surveyListSuccess(boolean flag) {
        Logger.logD(TAG,"surveyListSuccess value" + flag);
    }
}
