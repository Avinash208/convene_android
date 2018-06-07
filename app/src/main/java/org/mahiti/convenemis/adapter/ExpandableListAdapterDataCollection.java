package org.mahiti.convenemis.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.mahiti.convenemis.BeenClass.beneficiary.Datum;
import org.mahiti.convenemis.BeenClass.parentChild.SurveyDetail;
import org.mahiti.convenemis.ListingActivity;
import org.mahiti.convenemis.R;
import org.mahiti.convenemis.SurveyListLevels;
import org.mahiti.convenemis.utils.AnimationUtils;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.ToastUtils;

import java.util.HashMap;
import java.util.List;


public class ExpandableListAdapterDataCollection extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> headerList; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Datum>> dataChildList;
    private SharedPreferences preferences;
    private static final String MyPREFERENCES = "MyPrefs" ;

    public ExpandableListAdapterDataCollection(Context context, List<String> listDataHeader,
                                 HashMap<String, List<Datum>> listChildData) {
        this._context = context;
        this.headerList = listDataHeader;
        this.dataChildList = listChildData;
        preferences = PreferenceManager.getDefaultSharedPreferences(_context);

    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.dataChildList.get(this.headerList.get(groupPosition)).get(childPosititon);
    }

    public Object getChildCustome(int groupPosition, int childPosititon) {
       String getName=this.headerList.get(childPosititon);
       List<Datum> list=this.dataChildList.get(getName);
        return list.get(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Datum childText = (Datum) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
               convertView = infalInflater.inflate(R.layout.list_item, null);

        }

        final TextView txtListChild = convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText.getName());
        convertView.setOnClickListener(v -> {
            try {
                txtListChild.setTextColor(_context.getResources().getColor(R.color.pink));
                AnimationUtils.viewAnimation(v);
                getDataCollectionDetails(groupPosition,childPosition);
            }catch (Exception e){
                Logger.logE("","",e);
            }
        });


        return convertView;
    }


    /**
     *
     * @param groupPosition
     * @param childPosition
     */
    private void getDataCollectionDetails(int groupPosition, int childPosition) {
        if(true){
            Datum datum=(Datum)getChildCustome(childPosition,groupPosition);
            datum.setName(dataChildList.get(headerList.get(groupPosition)).get(childPosition).getName());
            List<SurveyDetail>  surveyDetail= SurveyListLevels.getSurveyList(_context,preferences.getString(Constants.DBNAME,""),preferences.getString("UID",""),"");
            Logger.logD("LOG_TAG","selected name"+datum.getName());
            SurveyDetail  surveyDetailBean;
            for (int i=0;i<surveyDetail.size();i++){
                if (datum.getName().equalsIgnoreCase(surveyDetail.get(i).getSurveyName())){
                    surveyDetailBean =surveyDetail.get(i);
                    SharedPreferences sharedpreferences = _context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    Logger.logD("-->start time","checking time line");
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(Constants.SURVEY_NAMe, surveyDetailBean.getSurveyName());
                    editor.putInt(Constants.FEATURE, surveyDetailBean.getPFeature());
                    editor.putInt(Constants.LIMIT, surveyDetailBean.getPLimit());
                    editor.putInt(Constants.PERIODICITY, Integer.parseInt(surveyDetailBean.getPiriodicity()));
                    editor.putString(Constants.LABEL, surveyDetailBean.getLabels());
                    editor.putString(Constants.VERSION, surveyDetailBean.getVn());
                    editor.putInt(Constants.CONFIG, (surveyDetailBean.getBConfig()));
                    editor.putInt(Constants.RD, surveyDetailBean.getReasonDisagree());
                    String[] orderLevels = surveyDetailBean.getOrderLevels().split(",");
                    editor.putString(Constants.O_LEAVEL, orderLevels[orderLevels.length-1]);
                    editor.putString(Constants.CODE, surveyDetailBean.getPcode());
                    if (datum.getActive()==0)
                        editor.putInt(Constants.SURVEY_ID, datum.getBeneficiaryTypeId());
                    else
                        editor.putInt(Constants.SURVEY_ID, surveyDetailBean.getSurveyId());

                    editor.putString(Constants.BENEFICIARY_TYPE,surveyDetailBean.getBeneficiaryType());
                    editor.putString(Constants.BENEFICIARY_IDS,surveyDetailBean.getBeneficiaryIds());
                    editor.putString(Constants.FACILITY_IDS,surveyDetailBean.getFacilityIds());
                    editor.putString("Survey_tittle",surveyDetailBean.getSurveyName());
                    Logger.logV("the survye id is","the survey id is"+surveyDetailBean.getSurveyId());
                    editor.putInt(Constants.Q_CONFIGS, surveyDetailBean.getQConfig());
                    editor.apply();
                    Logger.logD("-->start time","checking time line");
                  if (sharedpreferences.getInt(Constants.SURVEY_ID,0)!=0) {
                      Intent survrySummaryReport = new Intent(_context, ListingActivity.class);
                      survrySummaryReport.putExtra(Constants.HEADER_NAME, surveyDetailBean.getBeneficiaryType());
                      _context.startActivity(survrySummaryReport);
                  }else{
                      ToastUtils.displayToast("Sorry no beneficiary records",_context);
                  }
                }
            }
        }else {
            if (preferences.getInt(Constants.SURVEY_ID,0)!=0) {
                Logger.logD("-->start time","checking time line");
                Intent intent = new Intent(_context, ListingActivity.class);
                intent.putExtra(Constants.HEADER_NAME,headerList.get(groupPosition));
                intent.putExtra("beneficiary_type_id",String.valueOf(dataChildList.get(headerList.get(groupPosition)).get(childPosition).getId()));
                intent.putExtra(Constants.TYPE_VALUE, dataChildList.get(headerList.get(groupPosition)).get(childPosition).getName());
                _context.startActivity(intent);
            }else {
                ToastUtils.displayToast("Sorry no beneficiary records",_context);
            }


        }
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.dataChildList.get(this.headerList.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.headerList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.headerList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }
        ImageView img = (ImageView) convertView.findViewById(R.id.image);
        if (isExpanded) {
            img.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        } else {
            img.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        }

        TextView lblListHeader = convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
