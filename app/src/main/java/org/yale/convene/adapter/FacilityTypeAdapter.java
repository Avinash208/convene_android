package org.yale.convene.adapter;

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
import org.yale.convene.AddFacilityActivity;
import org.yale.convene.BeenClass.facilities.Datum;
import org.yale.convene.R;
import org.yale.convene.TypeDetailsActivity;
import org.yale.convene.utils.AnimationUtils;
import org.yale.convene.utils.Constants;
import org.yale.convene.utils.Logger;

import java.util.List;

/**
 * Created by Aviansh Raj  on 26/07/17.
 */
public class FacilityTypeAdapter extends BaseAdapter {
    private Context mcontext;
    private  List<Datum> facilityList;
    private String facilityTypeHeader="";
    private SharedPreferences preferences;
    private boolean isEditBeneficiary=false;
    private static final String SERVICES_STRING="services";
    private int surveyIdDCF;


    /**
     *  @param context
     * @param typeDetailsList
     * @param headerName
     * @param surveyIdDCF
     */
    public FacilityTypeAdapter(Context context, List<Datum> typeDetailsList, String headerName, int surveyIdDCF) {
        mcontext= context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.facilityList= typeDetailsList;
        this.surveyIdDCF = surveyIdDCF;
        this.facilityTypeHeader=headerName;

    }

    @Override
    public int getCount() {
        return facilityList.size();
    }

    @Override
    public Object getItem(int position) {
        return facilityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return  position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final Viewholder vh;
        View layoutView = convertView;
        Typeface custom_font = Typeface.createFromAsset(mcontext.getAssets(),  "fonts/Roboto-Light.ttf");
        Typeface customfont = Typeface.createFromAsset(mcontext.getAssets(),  "fonts/Roboto-Bold.ttf");
        if (layoutView == null) {
            vh = new Viewholder();
            layoutView = inflater.inflate(R.layout.beneficiary_list_display, viewGroup,false);
            vh.beneficiaryname = layoutView.findViewById(R.id.beneficiaryname);
            vh.linearArrow= layoutView.findViewById(R.id.linear);
            vh.beneficiaryname .setTypeface(customfont);
            vh.beneficiaryLoaction = layoutView.findViewById(R.id.beneficiary_location);
            vh.lastModifiedDate = layoutView.findViewById(R.id.beneficiary_modi_ddate);
            vh.beneficiaryLoaction .setTypeface(custom_font);
            vh.editFacility=layoutView.findViewById(R.id.editDetails);
            vh.lastModifiedDate.setTypeface(custom_font);
            vh.linearItem= layoutView.findViewById(R.id.linearItem);
            vh.statusTextView=layoutView.findViewById(R.id.statusDetails);
            layoutView.setTag(vh);
        } else {
            layoutView = convertView;
            vh = (Viewholder) layoutView.getTag();
        }
        vh.linearArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vh.linearItem.performClick();
            }
        });
        final SharedPreferences.Editor editor=preferences.edit();
        vh.beneficiaryname.setText(facilityList.get(position).getName());
        Logger.logD("","getStatus"+facilityList.get(position).getStatus());
        if(("Offline").equalsIgnoreCase(facilityList.get(position).getStatus())||("Update").equalsIgnoreCase(facilityList.get(position).getStatus())){
            vh.statusTextView.setVisibility(View.VISIBLE);
            vh.beneficiaryLoaction.setText(facilityList.get(position).getBoundaryName());
            vh.statusTextView.setText(String.format("(%s)", mcontext.getString(R.string.offline)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                vh.statusTextView.setTextColor(mcontext.getColor(R.color.Red));
            }
        }else{
            vh.statusTextView.setVisibility(View.GONE);
            vh.beneficiaryLoaction.setText(String.format("From : %s", facilityList.get(position).getBoundaryName()));
        }


        final JSONArray jsonArray=new JSONArray();
        try {
            JSONObject spinnerjsonObject = new JSONObject();
            spinnerjsonObject.put("ben_name", facilityList.get(position).getName());
            spinnerjsonObject.put("ben_type", facilityList.get(position).getFacilityType());
            spinnerjsonObject.put("ben_id", facilityList.get(position).getId());
            spinnerjsonObject.put("type", "Facility");
            spinnerjsonObject.put("uuid",facilityList.get(position).getUuid());

            jsonArray.put(spinnerjsonObject);
        }catch (Exception e){
            Logger.logE("Exception","Exception" , e);
        }

        vh.editFacility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEditBeneficiary=true;
                AnimationUtils.viewAnimation(view);
                setToPrefernce(editor,position,jsonArray);

            }
        });
        vh.linearItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimationUtils.viewAnimation(view);
                setToPrefernce(editor,position,jsonArray);
            }
        });
        return layoutView;
    }

    private void setToPrefernce(SharedPreferences.Editor editor, int position, JSONArray jsonArray) {
        editor.putString(Constants.FACILITY_ID,String.valueOf(facilityList.get(position).getId()));
        editor.putString(Constants.BENEFICIARY_ID,"");
        editor.putString(Constants.BENEFICIARY_NAME,facilityList.get(position).getName());
        editor.putString(Constants.CLUSTER_NAME,facilityList.get(position).getBoundaryName());
        editor.putString(Constants.PARTNER,facilityList.get(position).getPartner());
        editor.putString(Constants.ADDRESS1,facilityList.get(position).getAddress1());
        editor.putString(Constants.ADDRESS2,facilityList.get(position).getAddress2());
        editor.putString(Constants.PINCODE,facilityList.get(position).getPincode());
        editor.putString(Constants.FACILITY_TYPE_ID,String.valueOf(facilityList.get(position).getFacilityTypeId()));
        editor.putString(Constants.TYPE_NAME,facilityTypeHeader);
        editor.putString(Constants.TYPE_VALUE,facilityList.get(position).getFacilityType());
        editor.putString(Constants.ID, String.valueOf(facilityList.get(position).getId()));
        editor.putString(Constants.BOUNDARY_LEVEL,facilityList.get(position).getBoundaryLevel());
        editor.putString(Constants.UUID,facilityList.get(position).getUuid());
        editor.putString(Constants.THEMATIC_AREA_NAME,facilityList.get(position).getThematicArea());
        editor.putString(Constants.THEMATIC_AREA_ID,String.valueOf(facilityList.get(position).getThematicAreaId()));
        String beneficiaryJsonString=jsonArray.toString();
        Logger.logV("","beneficiary arrray string" + beneficiaryJsonString);
        editor.putString(Constants.BENEFICIARY_ARRAY,jsonArray.toString());
        editor.putString(Constants.BENEFICIARY_TYPE,facilityList.get(position).getFacilityType());
        editor.putString(Constants.FACILITY_SUB_TYPE, String.valueOf(facilityList.get(position).getFacilitySubtype()));
        editor.putString(Constants.LOCATION_ID,String.valueOf(facilityList.get(position).getBoundaryId()));
        editor.putString(SERVICES_STRING,String.valueOf(facilityList.get(position).getServices()));
        editor.apply();
        Logger.logV("uuid","uuid" + facilityList.get(position).getUuid()+"beneficiaryName-->"+facilityList.get(position).getName());


        if(isEditBeneficiary){
            Intent intent=new Intent(mcontext, AddFacilityActivity.class);
            SharedPreferences.Editor editor2=preferences.edit();
            editor2.putBoolean("isEditFacility",true);
            editor2.putBoolean("isEditBeneficiary",false);
            editor2.putBoolean("fromHomeScreen",false);
            editor2.apply();
            setToBundle(jsonArray, position,intent);
            mcontext.startActivity(intent);
        }else {
            Intent intent=new Intent(mcontext, TypeDetailsActivity.class);
            setToBundle(jsonArray,position,intent);
            intent.putExtra("surveyIdDCF",surveyIdDCF);
            intent.putExtra(Constants.CREATED_DATE,facilityList.get(position).getCreated());

            mcontext.startActivity(intent);
        }

    }

    private void setToBundle(JSONArray jsonArray, int position, Intent intent) {
        Bundle bundle = new Bundle();
        SharedPreferences.Editor editor2=preferences.edit();
        bundle.putParcelable("facilitytype_details_list", facilityList.get(position));
        bundle.putString("beneficiary_id",String.valueOf(facilityList.get(position).getId()));
        bundle.putString("beneficiaryArray",jsonArray.toString());
        bundle.putString("beneficiary_type",facilityList.get(position).getFacilityType());
        bundle.putString("address1",facilityList.get(position).getAddress1());
        bundle.putString("address2",facilityList.get(position).getAddress2());
        bundle.putString("pincode",facilityList.get(position).getPincode());
        bundle.putString("beneficiaryName",facilityList.get(position).getName());
        bundle.putString("uuid",facilityList.get(position).getUuid());
        bundle.putString("locationName",facilityList.get(position).getBoundaryName());
        bundle.putString("boundary_level",facilityList.get(position).getBoundaryLevel());
        bundle.putString(Constants.BENEFICIARY_TYPE_ID,String.valueOf(facilityList.get(position).getFacilityTypeId()));
        bundle.putString("location_id",String.valueOf(facilityList.get(position).getBoundaryId()));
        bundle.putString("typeValue",facilityList.get(position).getFacilityType());
        bundle.putString(Constants.THEMATIC_AREA_ID,String.valueOf(facilityList.get(position).getThematicAreaId()));
        bundle.putString("typeHeaderName",facilityTypeHeader);
        bundle.putString("address",facilityList.get(position).getAddress1());

        if(!facilityList.get(position).getServices().isEmpty()){
            bundle.putString(SERVICES_STRING,String.valueOf(facilityList.get(position).getServices()));
        }else{
            bundle.putString(SERVICES_STRING,"");
        }
        bundle.putString("sync_status", String.valueOf(facilityList.get(position).getSyncStatus()));
        bundle.putString(Constants.BOUNDARY_LEVEL,facilityList.get(position).getBoundaryLevel());
        bundle.putString(Constants.THEMATIC_AREA_NAME,facilityList.get(position).getThematicArea());
        bundle.putString(Constants.FACILITY_SUB_TYPE,String.valueOf(facilityList.get(position).getFacilitySubtype()));
        editor2.putString(Constants.CREATED_DATE,facilityList.get(position).getCreated());
        editor2.putString("uuid",facilityList.get(position).getUuid());
        editor2.putInt("surveyIdDCF",surveyIdDCF);
        editor2.apply();
        intent.putExtras(bundle);
    }

    private class Viewholder {
        TextView beneficiaryname;
        TextView lastModifiedDate;
        TextView beneficiaryLoaction;
        TextView editFacility;
        TextView statusTextView;
        LinearLayout linearItem;
        LinearLayout linearArrow;
    }
}
