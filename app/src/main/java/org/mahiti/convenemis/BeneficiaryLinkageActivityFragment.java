package org.mahiti.convenemis;

/**
 * Created by mahiti on 28/05/18.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.mahiti.convenemis.BeenClass.BeneficiaryLinkage;
import org.mahiti.convenemis.BeenClass.QuestionAnswer;
import org.mahiti.convenemis.BeenClass.childLink;
import org.mahiti.convenemis.api.CallServerForApi;
import org.mahiti.convenemis.api.PushingResultsInterface;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class BeneficiaryLinkageActivityFragment extends Fragment implements PushingResultsInterface {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int GETBENEFICIARYCODE = 100;
    private DBHandler dbHandler;
    private String surveyPrimaryKeyId="";
    private int surveysId;
    private static final String SURVEYID = "survey_id";
    private int parentID;
    private LinearLayout getHeadingLayout;
    private SharedPreferences sharedPreferences;
    ExternalDbOpenHelper dbOpenHelper;
    private int parent_form_primaryid;
    private boolean statusFlag=true;


    public BeneficiaryLinkageActivityFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BeneficiaryLinkageActivityFragment newInstance(int sectionNumber) {
        BeneficiaryLinkageActivityFragment fragment = new BeneficiaryLinkageActivityFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.beneficiarylinkages, container, false);
        initVariable(rootView);
       // callBeneficiaryLiakageApi();
        List<String> headingNameList= dbOpenHelper.getLinkageHeadings(surveysId,dbOpenHelper);
        renderView(headingNameList);

        return rootView;
    }

    private void initVariable(View rootView) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        dbOpenHelper = ExternalDbOpenHelper.getInstance(getActivity(), sharedPreferences.getString(Constants.DBNAME, ""), sharedPreferences.getString("uId", ""));
        dbHandler = new DBHandler(getActivity());
        getHeadingLayout= rootView.findViewById(R.id.heading_dynamic_inflat);

        Intent surveyPrimaryKeyIntent = getActivity().getIntent();
        if (surveyPrimaryKeyIntent != null) {
            surveyPrimaryKeyId = surveyPrimaryKeyIntent.getStringExtra("SurveyId");
            Logger.logV("surveyPrimaryKeyId", "surveyPrimaryKeyId" + surveyPrimaryKeyId);
        }
        if (surveyPrimaryKeyIntent != null ) {
            surveysId = Integer.parseInt(surveyPrimaryKeyIntent.getStringExtra(SURVEYID));
            parentID = Integer.parseInt(surveyPrimaryKeyIntent.getStringExtra("parentID"));
            parent_form_primaryid = Integer.parseInt(surveyPrimaryKeyIntent.getStringExtra("parent_form_primaryid"));
        }
        Logger.logD("SurveyId in parentID",parentID+"");
    }

    private void callBeneficiaryLiakageApi() {
        HashMap<String,String> nextBirthDayParams= new HashMap<>();
        nextBirthDayParams.put("URL","survey/all-linkages/");

        if(Utils.haveNetworkConnection(getActivity())){
            CallServerForApi.callServerApi(getActivity(),this,nextBirthDayParams,null, GETBENEFICIARYCODE);

        }


    }

    @Override
    public void setResults(String results, int apiCode) {
       if (apiCode==GETBENEFICIARYCODE){
           ParceAndUpdateToDatabase(results);

       }
    }

    private void ParceAndUpdateToDatabase(String result) {
        try {
            Gson gson = new Gson();
            BeneficiaryLinkage beneficiaryLinkage = gson.fromJson(result, BeneficiaryLinkage.class);
            Logger.logD("ParceAndUpdateToDatabase in bean",beneficiaryLinkage.getMessage());
            for (int i=0;i<beneficiaryLinkage.getLinkages().size();i++){
                long responseId = dbHandler.insertLinkageDataToDB(beneficiaryLinkage.getLinkages().get(i));
                Logger.logD("likage Updated Successfully",responseId+"");
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        List<String> headingNameList= dbOpenHelper.getLinkageHeadings(surveysId,dbOpenHelper);
        renderView(headingNameList);
    }
    private void renderView(List<String> headingNameList) {
        if (!headingNameList.isEmpty()){
            try {
                getHeadingLayout.removeAllViews();
                for (int i=0;i<headingNameList.size();i++){
                    View child = this.getLayoutInflater().inflate(R.layout.beneficiarylinkage_heading_row, getHeadingLayout, false);//child.xml
                    LinearLayout childdynamicinflater = (LinearLayout) child.findViewById(R.id.child_dynamic_inflater);
                    ImageView showmore = (ImageView) child.findViewById(R.id.showmore);
                    TextView addlink = (TextView) child.findViewById(R.id.addlink);
                    ImageView addmembers = (ImageView) child.findViewById(R.id.addmembers);
                    TextView holdername = (TextView) child.findViewById(R.id.holdername);
                    holdername.setText(headingNameList.get(i));
                    ArrayList<childLink> getChildUUids= dbHandler.getChildDetailsFromBeneficiaryLinkage(surveysId,surveyPrimaryKeyId,dbHandler);
                    Logger.logD("likage getChildUUids",getChildUUids+"");
                    List<QuestionAnswer> getUnderChildList=dbHandler.getAllChildRecord(getChildUUids,dbHandler);
                    if (!getChildUUids.isEmpty()){
                        for (int j=0;j<getUnderChildList.size();j++){
                            View childView = this.getLayoutInflater().inflate(R.layout.linkage_custom_row, childdynamicinflater, false);//child.xml
                            TextView childaddress = (TextView) childView.findViewById(R.id.childaddress);
                            TextView childname = (TextView) childView.findViewById(R.id.childname);
                            childname.setText(getUnderChildList.get(j).getAnswerText());
                            childaddress.setText(getUnderChildList.get(j).getQuestionText());
                            childdynamicinflater.addView(childView);
                            int finalJ = j;
                            addmembers.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Logger.logD("likage getChildUUids",getChildUUids.get(finalJ).getChild_form_type()+"");
                                    Bundle bundle= new Bundle();
                                    bundle.putString("getChild_form_type",String.valueOf(getChildUUids.get(finalJ).getChild_form_type()));
                                    bundle.putString("surveyPrimaryKeyId",surveyPrimaryKeyId);
                                    bundle.putInt("parent_form_primaryid",parent_form_primaryid);
                                    bundle.putInt("parent_form_type",parentID);
                                    bundle.putParcelableArrayList("getChild_form_id",getChildUUids);
                                    Intent callMemberActivityIntent= new Intent(getActivity(),ShowMemberListActivity.class);
                                    callMemberActivityIntent.putExtras(bundle);
                                    startActivity(callMemberActivityIntent);
                                }
                            });
                            showmore.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    statusFlag=false;
                                    childdynamicinflater.setVisibility(View.VISIBLE);
                                }
                            });

                        }
                    }

                    getHeadingLayout.addView(child);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
    
}
