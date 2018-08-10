package org.assistindia.convene.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.assistindia.convene.AddBeneficiaryActivity;
import org.assistindia.convene.BeenClass.beneficiary.Datum;
import org.assistindia.convene.R;
import org.assistindia.convene.TypeDetailsActivity;
import org.assistindia.convene.utils.AnimationUtils;
import org.assistindia.convene.utils.Constants;
import org.assistindia.convene.utils.Logger;

import java.util.List;


public class BeneficiaryTypeAdapter extends BaseAdapter {
    private Context mcontext;
    private  List<Datum> beneficiaryList;
    private String beneficiaryTypeHeader="";
    private SharedPreferences preferences;
    private boolean isEditBeneficiary=false;
    private String addressId;
    private int boundaryId;
    private String locationName="";
    private Integer locationID;
    private Integer locationLevel;
    private static final String TAG="BeneficiaryTypeAdapter";
    private static final String BOUNDARY_ID_KEY="boundary_id";
    private static final String LOCATION_ID_KEY="location_id";
    private int surveyIdDCF;

    /**
     *  @param context
     * @param typeDetailsList
     * @param headerName
     * @param surveyIdDCF
     */
    public BeneficiaryTypeAdapter(Context context, List<Datum> typeDetailsList, String headerName, int surveyIdDCF) {
        mcontext= context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.beneficiaryList= typeDetailsList;
        this.beneficiaryTypeHeader=headerName;
        this.surveyIdDCF = surveyIdDCF;
    }


    @Override
    public int getCount() {
        return beneficiaryList.size();
    }


    /**
     *
     * @param beneficiaryPosition
     * @return
     */
    @Override
    public Object getItem(int beneficiaryPosition) {
        return beneficiaryList.get(beneficiaryPosition);
    }


    /**
     *
     * @param beneficiaryPosition
     * @return
     */
    @Override
    public long getItemId(int beneficiaryPosition) {
        return  beneficiaryPosition;
    }


    /**
     *
     * @param beneficiaryPosition
     * @param convertView
     * @param viewGroup
     * @return
     */

    @Override
    public View getView(final int beneficiaryPosition, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final Viewholder viewholder;
        View layoutView = convertView;
        final SharedPreferences.Editor editor=preferences.edit();
        if (layoutView == null) {
            viewholder = new Viewholder();
            layoutView = inflater.inflate(R.layout.beneficiary_list_display, viewGroup,false);
            initializeVariables(viewholder,layoutView);
            layoutView.setTag(viewholder);
        } else {
            layoutView = convertView;
            viewholder = (Viewholder) layoutView.getTag();
        }
        viewholder.beneficiaryname.setText(beneficiaryList.get(beneficiaryPosition).getName());
        if(("Offline").equalsIgnoreCase(beneficiaryList.get(beneficiaryPosition).getStatus())||("Update").equalsIgnoreCase(beneficiaryList.get(beneficiaryPosition).getStatus())){
            viewholder.beneficiaryLoaction.setText(beneficiaryList.get(beneficiaryPosition).getAddress().get(0).getLeastLocationName());
            viewholder.statusTextView.setVisibility(View.VISIBLE);
            viewholder.statusTextView.setText(String.format("(%s)", mcontext.getString(R.string.offline)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                viewholder.statusTextView.setTextColor(mcontext.getColor(R.color.Red));
            }
            if(("Household").equalsIgnoreCase(beneficiaryList.get(beneficiaryPosition).getBeneficiaryType())){
                viewholder.lastModifiedDate.setVisibility(View.GONE);
            }else{
                viewholder.lastModifiedDate.setVisibility(View.VISIBLE);
                viewholder.lastModifiedDate.setText(String.format("Parent :%s", beneficiaryList.get(beneficiaryPosition).getParent()));
            }
        }else{
            viewholder.statusTextView.setVisibility(View.GONE);
            if(beneficiaryList.get(beneficiaryPosition).getLeast_location_name()!=null){
                viewholder.beneficiaryLoaction.setText(String.format("From : %s", beneficiaryList.get(beneficiaryPosition).getLeast_location_name()));
            }else{
                viewholder.beneficiaryLoaction.setText("");
            }
            if(("Household").equalsIgnoreCase(beneficiaryList.get(beneficiaryPosition).getBeneficiaryType())){
                viewholder.lastModifiedDate.setVisibility(View.GONE);
            }else{
                viewholder.lastModifiedDate.setVisibility(View.VISIBLE);
                viewholder.lastModifiedDate.setText(String.format("Parent :%s", beneficiaryList.get(beneficiaryPosition).getParent()));
            }
        }
        viewholder.linearArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewholder.linearItem.performClick();
            }
        });

        final JSONArray jsonArray=new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ben_name", beneficiaryList.get(beneficiaryPosition).getName());
            jsonObject.put("ben_type", beneficiaryList.get(beneficiaryPosition).getBtype());
            jsonObject.put("ben_id", beneficiaryList.get(beneficiaryPosition).getId());
            jsonObject.put("fac_uuid","");
            jsonObject.put("type", "Beneficiary");
            jsonObject.put("uuid",beneficiaryList.get(beneficiaryPosition).getUuid());
            jsonArray.put(jsonObject);
        }catch (Exception e){
            Logger.logE("Exception","Exception" , e);
        }
        viewholder.editBeneficiary.setOnClickListener(view -> {
            isEditBeneficiary=true;
            AnimationUtils.viewAnimation(view);
            setToPreference(editor,beneficiaryPosition,jsonArray);

        });
        viewholder.linearItem.setOnClickListener(view -> {
            isEditBeneficiary=false;
            AnimationUtils.viewAnimation(view);
            Logger.logD("TimeTest","@linearItem.setOnClickListener");
            setToPreference(editor,beneficiaryPosition,jsonArray);
        });
        return layoutView;
    }

    private void initializeVariables(Viewholder viewholder, View layoutView) {
        Typeface typeface = Typeface.createFromAsset(mcontext.getAssets(),  "fonts/Roboto-Light.ttf");
        Typeface customfont = Typeface.createFromAsset(mcontext.getAssets(),  "fonts/Roboto-Bold.ttf");
        viewholder.beneficiaryname = layoutView.findViewById(R.id.beneficiaryname);
        viewholder.linearArrow= layoutView.findViewById(R.id.linear);
        viewholder.beneficiaryname.setTypeface(customfont);
        viewholder.beneficiaryLoaction = layoutView.findViewById(R.id.beneficiary_location);
        viewholder.lastModifiedDate = layoutView.findViewById(R.id.beneficiary_modi_ddate);
        viewholder.beneficiaryLoaction .setTypeface(typeface);
        viewholder.lastModifiedDate.setTypeface(typeface);
        viewholder.linearItem= layoutView.findViewById(R.id.linearItem);
        viewholder.editBeneficiary=layoutView.findViewById(R.id.editDetails);
        viewholder.statusTextView=layoutView.findViewById(R.id.statusDetails);
    }


    /**
     *
     * @param editor
     * @param position
     * @param jsonArray
     */
    private void setToPreference(SharedPreferences.Editor editor, int position, JSONArray jsonArray) {
        editor.putString(Constants.BENEFICIARY_ID,String.valueOf(beneficiaryList.get(position).getId()));
        editor.putString(Constants.FACILITY_ID,"");
        editor.putString(Constants.BENEFICIARY_NAME,beneficiaryList.get(position).getName());
        editor.putString(Constants.BENEFICIARY_TYPE_ID,String.valueOf(beneficiaryList.get(position).getBeneficiaryTypeId()));
        editor.putString(Constants.UUID,beneficiaryList.get(position).getUuid());
        editor.putString(Constants.AGE,beneficiaryList.get(position).getAge());
        editor.putString(Constants.GENDER,beneficiaryList.get(position).getGender());
        editor.putString(Constants.STATUS,beneficiaryList.get(position).getStatus());
        editor.putString(Constants.PARENT_UUID,beneficiaryList.get(position).getParent_uuid());
        editor.putString(Constants.SYNC_STATUS,String.valueOf(beneficiaryList.get(position).getSyncStatus()));
        Logger.logV("uuid","uuid" + beneficiaryList.get(position).getUuid()+"beneficiaryName-->"+beneficiaryList.get(position).getName());
        editor.putString(Constants.PARTNER,beneficiaryList.get(position).getPartner());
        editor.putString(Constants.PARENT,beneficiaryList.get(position).getParent());
        editor.putString(Constants.PARENT_ID, String.valueOf(beneficiaryList.get(position).getParentId()));
        editor.putString(Constants.CONTACT_NO,beneficiaryList.get(position).getContactNo().toString());
        editor.putString(Constants.TYPE_NAME,beneficiaryTypeHeader);
        editor.putString(Constants.TYPE_VALUE, beneficiaryList.get(position).getBeneficiaryType());
        editor.putString(Constants.BENEFICIARY_ARRAY,jsonArray.toString());
        editor.putString(Constants.ID,String.valueOf(beneficiaryList.get(position).getId()));
        editor.putString(Constants.AGE,beneficiaryList.get(position).getAge());
        editor.putString("alias_name",beneficiaryList.get(position).getAliasName());
        editor.apply();
        if(isEditBeneficiary){
            Intent intent=new Intent(mcontext, AddBeneficiaryActivity.class);
            SharedPreferences.Editor editor1=preferences.edit();
            editor1.putBoolean("isEditFacility",false);
            editor1.putBoolean("isEditBeneficiary",true);
            editor1.putBoolean("fromUserDetails",false);
            editor1.putBoolean("fromHomeScreen",false);
            editor1.apply();
            setToBundle(jsonArray, position,intent);
            intent.putExtra("Beneficiary","EDIT_BENEFICIARY");
            mcontext.startActivity(intent);
        }else{
            Intent intent=new Intent(mcontext, TypeDetailsActivity.class);
            intent.putExtra("surveyIdDCF",surveyIdDCF);
            intent.putExtra(Constants.CREATED_DATE,beneficiaryList.get(position).getCreated());
            setToBundle(jsonArray, position,intent);
            Logger.logD("TimeTest","@startActivity");
            mcontext.startActivity(intent);
        }
    }

    private void setToBundle(JSONArray jsonArray, int position, Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putString("id",String.valueOf(beneficiaryList.get(position).getId()));
        bundle.putString("parent_id",String.valueOf(beneficiaryList.get(position).getParentId()));
        bundle.putString("beneficiaryArray",jsonArray.toString());
        if(beneficiaryList.get(position).getBeneficiaryType() == null){
            bundle.putString("beneficiary_type", beneficiaryList.get(position).getBtype());
        }else{
            bundle.putString("beneficiary_type", beneficiaryList.get(position).getBeneficiaryType());
        }

        Logger.logV(TAG,"beneficiary type in beneficiary adapter ==>" +beneficiaryList.get(position).getBtype());
        Logger.logV(TAG,"beneficiary type in beneficiary id adapter ==>" +beneficiaryList.get(position).getId());
        bundle.putString("parent_name",beneficiaryList.get(position).getParent());
        try{
            JSONArray addressArray=new JSONArray();
            for(int j=0;j<beneficiaryList.get(position).getAddress().size();j++){
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("address1",beneficiaryList.get(position).getAddress().get(j).getAddress1());
                jsonObject.put("address2",beneficiaryList.get(position).getAddress().get(j).getAddress2());
                jsonObject.put("least_location_name",beneficiaryList.get(position).getAddress().get(j).getLeastLocationName());
                jsonObject.put("address_id",beneficiaryList.get(position).getAddress().get(j).getAddressId());
                addressId=beneficiaryList.get(position).getAddress().get(0).getAddressId();
                Logger.logD(TAG,beneficiaryList.get(position).getAddress().get(j).getProofId());
                jsonObject.put("proof_id",beneficiaryList.get(position).getAddress().get(j).getProofId());
                jsonObject.put(BOUNDARY_ID_KEY,beneficiaryList.get(position).getAddress().get(j).getBoundaryId());
                boundaryId=beneficiaryList.get(position).getAddress().get(0).getBoundaryId();
                jsonObject.put("pincode",beneficiaryList.get(position).getAddress().get(j).getPincode());
                jsonObject.put("primary",beneficiaryList.get(position).getAddress().get(j).getPrimary());
                jsonObject.put("location_level",beneficiaryList.get(position).getAddress().get(j).getLocationLevel());
                addressArray.put(jsonObject);
            }
            Logger.logD(TAG,addressArray.toString());

            locationName=beneficiaryList.get(position).getAddress().get(0).getLeastLocationName();
            locationID=beneficiaryList.get(position).getAddress().get(0).getBoundaryId();
            locationLevel=beneficiaryList.get(position).getAddress().get(0).getLocationLevel();
            SharedPreferences.Editor editor1=preferences.edit();
            editor1.putString("address",addressArray.toString());
            editor1.putString(Constants.DOB,beneficiaryList.get(position).getDateOfBirth());
            editor1.putString(Constants.DOB_OPTION_KEY,beneficiaryList.get(position).getDobOption());
            editor1.putString(LOCATION_ID_KEY,String.valueOf(locationID));
            editor1.putString(BOUNDARY_ID_KEY, String.valueOf(boundaryId));
            editor1.putString(Constants.CREATED_DATE,beneficiaryList.get(position).getCreated());
            editor1.putInt("surveyIdDCF",surveyIdDCF);
            editor1.putString("boundary_level",String.valueOf(locationLevel));
            editor1.putString("locationName",locationName);
            editor1.putString("uuid",beneficiaryList.get(position).getUuid());
            editor1.apply();
            bundle.putString("address",addressArray.toString());
            bundle.putString("address_id",addressId);
            bundle.putString(BOUNDARY_ID_KEY, String.valueOf(boundaryId));
            bundle.putString(LOCATION_ID_KEY,String.valueOf(boundaryId));
            bundle.putString(Constants.DOB_OPTION_KEY,beneficiaryList.get(position).getDobOption());
            bundle.putString(Constants.DOB,beneficiaryList.get(position).getDateOfBirth());
            bundle.putString("status",beneficiaryList.get(position).getStatus());
        }catch (Exception e){
            Logger.logE("","",e);
        }
        bundle.putString("parent_uuid",beneficiaryList.get(position).getParent_uuid());
        bundle.putString("contact_no",beneficiaryList.get(position).getContactNo().toString());
        bundle.putString("partner_name",beneficiaryList.get(position).getPartner());
        bundle.putString("beneficiaryName",beneficiaryList.get(position).getName());
        bundle.putString("uuid",beneficiaryList.get(position).getUuid());
        bundle.putString("gender",beneficiaryList.get(position).getGender());
        bundle.putString("beneficiary_type_id",String.valueOf(beneficiaryList.get(position).getBeneficiaryTypeId()));
        if(beneficiaryList.get(position).getBeneficiaryType() == null){
            bundle.putString("typeValue", beneficiaryList.get(position).getBtype());
        }else{
            bundle.putString("typeValue", beneficiaryList.get(position).getBeneficiaryType());
        }
        bundle.putString("alias_name",beneficiaryList.get(position).getAliasName());
        bundle.putString("sync_status",String.valueOf(beneficiaryList.get(position).getSyncStatus()));
        bundle.putString("age",beneficiaryList.get(position).getAge());
        bundle.putString("typeHeaderName",beneficiaryTypeHeader);
        Logger.logV(TAG,"beneficiary locationName ==>" +locationName);
        bundle.putString("locationName",locationName);
        bundle.putString(LOCATION_ID_KEY,String.valueOf(locationID));
        bundle.putString("boundary_level",String.valueOf(locationLevel));
        intent.putExtras(bundle);
    }

    public void add(Datum datum){
        beneficiaryList.add(datum);
    }

    private class Viewholder {
        TextView beneficiaryname;
        TextView beneficiaryLoaction;
        TextView lastModifiedDate;
        TextView editBeneficiary;
        TextView statusTextView;
        LinearLayout linearItem;
        LinearLayout linearArrow;
    }
}
