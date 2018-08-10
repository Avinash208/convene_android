package org.assistindia.convene;

/**
 * Created by mahiti on 28/05/18.
 */

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.assistindia.convene.BeenClass.BeneficiaryLinkage;
import org.assistindia.convene.BeenClass.Linkage;
import org.assistindia.convene.BeenClass.QuestionAnswer;
import org.assistindia.convene.BeenClass.childLink;
import org.assistindia.convene.api.CallServerForApi;
import org.assistindia.convene.api.PushingResultsInterface;
import org.assistindia.convene.database.DBHandler;
import org.assistindia.convene.database.ExternalDbOpenHelper;
import org.assistindia.convene.database.Utilities;
import org.assistindia.convene.utils.Constants;
import org.assistindia.convene.utils.Logger;
import org.assistindia.convene.utils.StartSurvey;
import org.assistindia.convene.utils.ToastUtils;
import org.assistindia.convene.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

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
    private static final String PARENT_FORM_ID = "parent_form_primaryid";
    private static final int APIUPDATECODE = 200;
    private static final String GETCHILDFORMTYPE = "getChild_form_type";
    private static final String PARENTFORMPRIMARYID = "parent_form_type";
    private static final String RELATIONID = "relation_id";
    private static final java.lang.String GROUPID = "GroupIds";
    private static final String CONFIGURTATIONQUESTION = "configuredQuestion";
    private static final String GETCHILDFORMPRIMARYID = "getChild_form_primaryid";
    private static final String GETCHILDFORMID = "getChild_form_id";
    private static final String TAG = "BeneficiaryLinkageActivityFragment";
    private DBHandler dbHandler;
    private String surveyPrimaryKeyId = "";
    private int surveysId;
    private static final String SURVEYID = "survey_id";
    private int parentID;
    private LinearLayout getHeadingLayout;
    private SharedPreferences sharedPreferences;
    ExternalDbOpenHelper dbOpenHelper;
    private int parent_form_primaryid;
    private boolean statusFlag = true;
    private String getGroupIds;
    private String getQuestionIds;
    private static final String MY_PREFS_NAME = "MyPrefs";
    private SharedPreferences prefs;
    IntentFilter linkageIntentFilter;
    private LinkageReceiver linkageReceiver;


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
        getGroupIds = dbOpenHelper.getGroupIds(surveysId, dbOpenHelper);
        getQuestionIds = dbOpenHelper.getQuestionids(getGroupIds, dbOpenHelper);
        List<QuestionAnswer> headingNameList = dbOpenHelper.getLinkageHeadings(getGroupIds, dbOpenHelper);
        renderView(headingNameList);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        linkageIntentFilter = new IntentFilter("Linkage");
        linkageReceiver=new LinkageReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        callBeneficiaryLiakageApi();
        getActivity().registerReceiver(linkageReceiver, linkageIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(linkageReceiver);
    }

    private void initVariable(View rootView) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        dbOpenHelper = ExternalDbOpenHelper.getInstance(getActivity(), sharedPreferences.getString(Constants.DBNAME, ""), sharedPreferences.getString("uId", ""));
        dbHandler = new DBHandler(getActivity());
        getHeadingLayout = rootView.findViewById(R.id.heading_dynamic_inflat);

        Intent surveyPrimaryKeyIntent = getActivity().getIntent();
        if (surveyPrimaryKeyIntent != null) {
            surveyPrimaryKeyId = surveyPrimaryKeyIntent.getStringExtra("SurveyId");
            Logger.logV("surveyPrimaryKeyId", "surveyPrimaryKeyId" + surveyPrimaryKeyId);
        }
        if (surveyPrimaryKeyIntent != null) {
            surveysId = Integer.parseInt(surveyPrimaryKeyIntent.getStringExtra(SURVEYID));
            parentID = Integer.parseInt(surveyPrimaryKeyIntent.getStringExtra("parentID"));
            parent_form_primaryid = Integer.parseInt(surveyPrimaryKeyIntent.getStringExtra(PARENT_FORM_ID));
        }
        Logger.logD("SurveyId in parentID", parentID + "");
    }



    private void callBeneficiaryLiakageApi() {
        HashMap<String, String> nextBirthDayParams = new HashMap<>();
        nextBirthDayParams.put("URL", "survey/all-linkages/");
        if (Utils.haveNetworkConnection(getActivity())) {
            CallServerForApi.callServerApi(getActivity(), this, nextBirthDayParams, null, GETBENEFICIARYCODE);

        } else {

            String getGroupId = dbOpenHelper.getGroupIds(surveysId, dbOpenHelper);
            List<QuestionAnswer> headingNameList = dbOpenHelper.getLinkageHeadings(getGroupId, dbOpenHelper);
            renderView(headingNameList);
        }


    }

    @Override
    public void setResults(String results, int apiCode) {
        if (apiCode == GETBENEFICIARYCODE) {
            ParceAndUpdateToDatabase(results);

        }
        if (apiCode == APIUPDATECODE) {
            Logger.logD("result here", results);

            updateResponse(results);
        }
    }

    private void ParceAndUpdateToDatabase(String result) {
        try {
            Gson gson = new Gson();
            BeneficiaryLinkage beneficiaryLinkage = gson.fromJson(result, BeneficiaryLinkage.class);
            Logger.logD("ParceAndUpdateToDatabase in bean", beneficiaryLinkage.getMessage());
            for (int i = 0; i < beneficiaryLinkage.getLinkages().size(); i++) {
                long responseId = dbHandler.insertLinkageDataToDB(beneficiaryLinkage.getLinkages().get(i), "2");
                Logger.logD("likage Updated Successfully", responseId + "");
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        String getGroupIds = dbOpenHelper.getGroupIds(surveysId, dbOpenHelper);
        List<QuestionAnswer> headingNameList = dbOpenHelper.getLinkageHeadings(getGroupIds, dbOpenHelper);
        renderView(headingNameList);
    }

    private void renderView(List<QuestionAnswer> headingNameList) {
        if (!headingNameList.isEmpty()) {
            try {
                getHeadingLayout.removeAllViews();
                for (int i = 0; i < headingNameList.size(); i++) {
                    View child = this.getLayoutInflater().inflate(R.layout.beneficiarylinkage_heading_row, getHeadingLayout, false);
                    LinearLayout childdynamicinflater = (LinearLayout) child.findViewById(R.id.child_dynamic_inflater);
                    TextView addlink = (TextView) child.findViewById(R.id.addlink);
                    TextView linkageEmptyLabel = (TextView) child.findViewById(R.id.linkage_empty_label);
                    LinearLayout addlinkContainer = (LinearLayout) child.findViewById(R.id.linklabelcontainer);
                    ImageView addmembers = (ImageView) child.findViewById(R.id.addmembers);
                    TextView holdername = (TextView) child.findViewById(R.id.holdername);
                    holdername.setText(headingNameList.get(i).getQuestionText());
                    ArrayList<childLink> getChildUUids = dbHandler.getChildDetailsFromBeneficiaryLinkage(surveysId, surveyPrimaryKeyId, dbHandler);
                    Logger.logD("likage getChildUUids", getChildUUids + "");
                    List<QuestionAnswer> getUnderChildList = dbHandler.getAllChildRecord(getChildUUids, dbHandler, getQuestionIds);
                    if (!getChildUUids.isEmpty() && !getUnderChildList.isEmpty()) {
                        linkageEmptyLabel.setVisibility(View.GONE);
                        childdynamicinflater.setVisibility(View.VISIBLE);
                       addlink.setVisibility(View.VISIBLE);
                        addlink.setText(String.valueOf(getUnderChildList.size()));
                        for (int j = 0; j < getUnderChildList.size(); j++) {
                            View childView = this.getLayoutInflater().inflate(R.layout.linkage_custom_row, childdynamicinflater, false);
                            TextView syncLabel = (TextView) childView.findViewById(R.id.synclabel);
                            int getSyncStatus= dbHandler.getSyncStatus(String.valueOf(getChildUUids.get(j).getChild_form_id()),surveyPrimaryKeyId);
                            if (getSyncStatus==0)
                                syncLabel.setText("Offline");
                            else
                                syncLabel.setVisibility(View.GONE);
                            TextView childaddress = (TextView) childView.findViewById(R.id.childaddress);

                            TextView childname = (TextView) childView.findViewById(R.id.childname);
                            LinearLayout deleteSelectedBen = (LinearLayout) childView.findViewById(R.id.deletebeneficiary);
                            childname.setText(getUnderChildList.get(j).getAnswerText());
                            childaddress.setText(getUnderChildList.get(j).getQuestionText());
                            childdynamicinflater.addView(childView);

                            int finalJ = j;
                            int finalI = i;
                            addlinkContainer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Logger.logD("likage getChildUUids", getChildUUids.get(finalJ).getChild_form_type() + "");
                                    Bundle bundle = new Bundle();
                                    bundle.putString(GETCHILDFORMTYPE, String.valueOf(getChildUUids.get(finalJ).getChild_form_type()));
                                    bundle.putString("surveyPrimaryKeyId", surveyPrimaryKeyId);
                                    bundle.putInt(PARENT_FORM_ID, parent_form_primaryid);
                                    bundle.putInt(PARENTFORMPRIMARYID, parentID);
                                    bundle.putInt(RELATIONID, headingNameList.get(finalI).getRelationId());
                                    bundle.putString(GROUPID, getGroupIds);
                                    bundle.putString(CONFIGURTATIONQUESTION, getQuestionIds);
                                    bundle.putInt(GETCHILDFORMPRIMARYID, getUnderChildList.get(finalJ).getChild_form_primaryid());
                                    bundle.putParcelableArrayList(GETCHILDFORMID, getChildUUids);
                                    Intent callMemberActivityIntent = new Intent(getActivity(), ShowMemberListActivity.class);
                                    callMemberActivityIntent.putExtras(bundle);
                                    startActivity(callMemberActivityIntent);
                                }
                            });
                            addmembers.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    SharedPreferences.Editor preferenceEditer = prefs.edit();
                                    preferenceEditer.putInt(Constants.SURVEY_ID, Integer.valueOf(getGroupIds));
                                    preferenceEditer.apply();
                                    SharedPreferences.Editor preferenceEd = sharedPreferences.edit();
                                    preferenceEd.putBoolean("isLocationBased", true);
                                    preferenceEd.apply();
                                    Utilities.setSurveyStatus(prefs,"new");
                                    new StartSurvey(getActivity(), getActivity(), sharedPreferences.getInt(Constants.SURVEY_ID, 0), sharedPreferences.getInt(Constants.SURVEY_ID, 0), "Village Name", "", "", "", "").execute();
                                }
                            });
                            deleteSelectedBen.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert);
                                    alertDialogBuilder.setTitle(R.string.confirmMessage).setMessage("Do you want to remove " + " " + getUnderChildList.get(finalJ).getAnswerText()).setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteFunctionality(getChildUUids,finalJ,headingNameList,finalI,getUnderChildList);

                                        }
                                    }).setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Logger.logD(TAG, "cancel case");
                                        }
                                    }).show();
                                }
                            });
                        }
                    } else {
                        linkageEmptyLabel.setVisibility(View.VISIBLE);
                        addlink.setVisibility(View.GONE);
                        int finalI1 = i;
                        addlinkContainer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                               addlinkContainerFunctionality(headingNameList,finalI1,getChildUUids);

                            }
                        });
                        addmembers.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addMemberFunctionality();
                            }
                        });
                    }

                    getHeadingLayout.addView(child);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }


    private void addlinkContainerFunctionality(List<QuestionAnswer> headingNameList, int finalI1, ArrayList<childLink> getChildUUids) {
        Bundle bundle = new Bundle();
        bundle.putString(GETCHILDFORMTYPE, surveyPrimaryKeyId);
        bundle.putString("surveyPrimaryKeyId", surveyPrimaryKeyId);
        bundle.putInt(PARENT_FORM_ID, parent_form_primaryid);
        bundle.putInt(PARENTFORMPRIMARYID, parentID);
        bundle.putInt(RELATIONID, headingNameList.get(finalI1).getRelationId());
        bundle.putString(GROUPID, getGroupIds);
        bundle.putString(CONFIGURTATIONQUESTION, getQuestionIds);
        bundle.putParcelableArrayList(GETCHILDFORMID, getChildUUids);
        Intent callMemberActivityIntent = new Intent(getActivity(), ShowMemberListActivity.class);
        callMemberActivityIntent.putExtras(bundle);
        startActivity(callMemberActivityIntent);
    }

    private void addMemberFunctionality() {
        SharedPreferences.Editor preferenceEditer = prefs.edit();
        preferenceEditer.putInt(Constants.SURVEY_ID, Integer.valueOf(getGroupIds));
        preferenceEditer.apply();
        SharedPreferences.Editor preferenceEd = sharedPreferences.edit();
        preferenceEd.putBoolean("isLocationBased", true);
        preferenceEd.apply();
        Utilities.setSurveyStatus(prefs,"new");
        new StartSurvey(getActivity(), getActivity(), sharedPreferences.getInt(Constants.SURVEY_ID, 0), sharedPreferences.getInt(Constants.SURVEY_ID, 0), "Village Name", "", "", "", "").execute();
    }

    private void deleteFunctionality(ArrayList<childLink> getChildUUids, int finalJ, List<QuestionAnswer> headingNameList, int finalI, List<QuestionAnswer> getUnderChildList) {
        Bundle bundle = new Bundle();
        bundle.putString(GETCHILDFORMTYPE, String.valueOf(getChildUUids.get(finalJ).getChild_form_type()));
        bundle.putString("surveyPrimaryKeyId", surveyPrimaryKeyId);
        bundle.putInt(PARENT_FORM_ID, parent_form_primaryid);
        bundle.putInt(PARENTFORMPRIMARYID, parentID);
        bundle.putInt(RELATIONID, headingNameList.get(finalI).getRelationId());
        bundle.putString(GROUPID, getGroupIds);
        bundle.putString(CONFIGURTATIONQUESTION, getQuestionIds);
        bundle.putInt(GETCHILDFORMPRIMARYID, getUnderChildList.get(finalJ).getChild_form_primaryid());
        bundle.putString(GETCHILDFORMID, String.valueOf(getChildUUids.get(finalJ).getChild_form_id()));
        createLinkageBundle(bundle);
    }

    /**
     * @param bundle bundle to create link.
     */
    private void createLinkageBundle(Bundle bundle) {
        try {
            JSONObject jsonObjectMain = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            List<Linkage> fillLinkagebean = new ArrayList<>();
            Linkage linkage = new Linkage();
            String getUUIDIfExist = dbHandler.isRecordExist(bundle.getString(GETCHILDFORMID),surveyPrimaryKeyId);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HHmmss");
            String currentDateandTime = sdf.format(new Date());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("uuid", getUUIDIfExist);
            linkage.setUuid(String.valueOf(getUUIDIfExist));
            jsonObject.put("linked_on", currentDateandTime);
            linkage.setLinkedOn(currentDateandTime);
            jsonObject.put("active", "0");
            linkage.setActive(0);
            jsonObject.put("modified_on", currentDateandTime);
            jsonObject.put(PARENTFORMPRIMARYID, parent_form_primaryid);
            linkage.setParentFormType(parentID);
            jsonObject.put("parent_form_id", surveyPrimaryKeyId);
            linkage.setParentFormId(surveyPrimaryKeyId);

            jsonObject.put("child_form_id", bundle.getString(GETCHILDFORMID));
            linkage.setChildFormId(bundle.getString(GETCHILDFORMID));

            jsonObject.put("child_form_type", bundle.getInt(GETCHILDFORMPRIMARYID));
            jsonObject.put(RELATIONID, bundle.getInt(RELATIONID));

            linkage.setRelationId(bundle.getInt(RELATIONID));
            linkage.setParentFormPrimaryid(bundle.getInt(GETCHILDFORMPRIMARYID));
            linkage.setChildFormPrimaryid(bundle.getInt(GETCHILDFORMPRIMARYID));
            linkage.setChildFormType(bundle.getInt(GROUPID));

            jsonArray.put(jsonObject);
            fillLinkagebean.add(linkage);
            jsonObjectMain.put("linkages", jsonArray);
            Logger.logD("jsonArray", jsonObjectMain.toString());
            updateMemberToBeneficiaryLinkageTodatabase(fillLinkagebean);
            ToastUtils.displayToast("Successfully removed",getActivity());
            callApiToUpdateBeneficiaryLinkage(jsonArray);


        } catch (Exception e) {
            Logger.logE("", "createLinkageBundle", e);
        }


    }

    /**
     * @param responseJson response Json array
     */
    private void callApiToUpdateBeneficiaryLinkage(JSONArray responseJson) {
        HashMap<String, String> beneficiaryLinkageParms = new HashMap<>();
        beneficiaryLinkageParms.put("URL", "/api/beneficiary-link/");
        beneficiaryLinkageParms.put("user_id", sharedPreferences.getString("UID", ""));
        beneficiaryLinkageParms.put("linkages", responseJson.toString());
        if (Utils.haveNetworkConnection(getActivity())) {
            CallServerForApi.callServerApi(getActivity(), this, beneficiaryLinkageParms, null, APIUPDATECODE);

        }else{
            String getGroupId = dbOpenHelper.getGroupIds(surveysId, dbOpenHelper);
            List<QuestionAnswer> headingNameList = dbOpenHelper.getLinkageHeadings(getGroupId, dbOpenHelper);
            renderView(headingNameList);
        }
    }

    /**
     * @param filledList updated filled list
     */
    private void updateMemberToBeneficiaryLinkageTodatabase(List<Linkage> filledList) {
        if (!filledList.isEmpty()) {
            for (int i = 0; i < filledList.size(); i++) {
                long responseId = dbHandler.insertLinkageDataToDB(filledList.get(i), "0");
                Logger.logD("likage Updated Successfully", responseId + "");
            }

        }
    }

    /**
     * @param results updated Result.
     */
    private void updateResponse(String results) {
        try {
            JSONObject jsonObject = new JSONObject(results);
            if (jsonObject.getInt("status") == 2) {
                JSONArray jsonArray = jsonObject.getJSONArray("success_records");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String getUUID = jsonObject1.getString("uuid");
                    String createdon = jsonObject1.getString("created_on");
                    int updatedResponseKey = dbHandler.updateBeneficiaryLinkageStatus(getUUID, createdon, dbHandler);
                    Logger.logD("response Updated successfullty", updatedResponseKey + "");

                }
            }
            String getGroupId = dbOpenHelper.getGroupIds(surveysId, dbOpenHelper);
            List<QuestionAnswer> headingNameList = dbOpenHelper.getLinkageHeadings(getGroupId, dbOpenHelper);
            renderView(headingNameList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private class LinkageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String getGroupId = dbOpenHelper.getGroupIds(surveysId, dbOpenHelper);
            List<QuestionAnswer> headingNameList = dbOpenHelper.getLinkageHeadings(getGroupId, dbOpenHelper);
            renderView(headingNameList);
        }
    }
}
