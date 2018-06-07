package org.mahiti.convenemis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mancj.materialsearchbar.MaterialSearchBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mahiti.convenemis.BeenClass.Linkage;
import org.mahiti.convenemis.BeenClass.QuestionAnswer;
import org.mahiti.convenemis.BeenClass.childLink;
import org.mahiti.convenemis.adapter.HomeTileAdapter;
import org.mahiti.convenemis.api.CallServerForApi;
import org.mahiti.convenemis.api.PushingResultsInterface;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.ToastUtils;
import org.mahiti.convenemis.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ShowMemberListActivity extends AppCompatActivity implements HomeTileAdapter.ItemSelectedListener
        , View.OnClickListener, PushingResultsInterface,MaterialSearchBar.OnSearchActionListener {

    private static final int APIUPDATECODE = 200;
    private static final String TAG = "ShowMemberListActivity";
    private RecyclerView memberListRV;
    private String getChild_form_type = "";

    private DBHandler dbHandlershowMember;
    ArrayList<childLink> fss;
    private Button addMember;
    private List<QuestionAnswer> selectedListTemp= new ArrayList<>();
    private String parent_form_id;
    List<QuestionAnswer> getSelectedChildList = new ArrayList<>();
    private SharedPreferences surveyPreferences;
    private LinearLayout parentTextLayout;
    private int parent_form_primaryid;
    private int parent_form_type;
    private LinearLayout noDataLabel;
    private int relationId;
    private String groupids;
    private String configuredQuestion;
    private MaterialSearchBar searchBar;
    private TextView tittleHeading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_member_list);
        initVariable();
        getBundleDetails();
        callDatabaseToGetList();
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Logger.logD(TAG,"beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Logger.logD(TAG,"onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterMemberList(s.toString());
            }
        });

    }

    private void callDatabaseToGetList() {
        List<QuestionAnswer> getMemberList = dbHandlershowMember.getSortedChildRecord(groupids, fss, configuredQuestion);
        if (!getMemberList.isEmpty()) {
            Logger.logD("getChild_form_id", getMemberList.get(0).getAnswerText() + "");
            setAdapterAsigne(getMemberList, false);
            noDataLabel.setVisibility(View.GONE);
            memberListRV.setVisibility(View.VISIBLE);

        } else {
            noDataLabel.setVisibility(View.VISIBLE);
            memberListRV.setVisibility(View.GONE);
        }

    }

    private void getBundleDetails() {
        Bundle getBundle = getIntent().getExtras();
        if (getBundle != null) {
            getChild_form_type = getBundle.getString("getChild_form_type");
            parent_form_id = getBundle.getString("surveyPrimaryKeyId");
            configuredQuestion = getBundle.getString("configuredQuestion");
            groupids = getBundle.getString("GroupIds");
            parent_form_primaryid = getBundle.getInt("parent_form_primaryid");
            parent_form_type = getBundle.getInt("parent_form_type");
            relationId = getBundle.getInt("relation_id");
            fss = getBundle.getParcelableArrayList("getChild_form_id");
            Logger.logD("getChild_form_type", getChild_form_type + "");
            Logger.logD("parent_form_id", parent_form_id + "");
            Logger.logD("getChild_form_id", fss.size() + "");
        }
    }

    private void initVariable() {
        surveyPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        memberListRV = (RecyclerView) findViewById(R.id.schoolAsignRv);
        addMember = (Button) findViewById(R.id.addMember);
        parentTextLayout = (LinearLayout) findViewById(R.id.parentTextLayout);
        noDataLabel = (LinearLayout) findViewById(R.id.nodatalabel);
        searchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        tittleHeading = (TextView) findViewById(R.id.title_heading);

        searchBar.setHint("Search member");
        searchBar.setTextHintColor(getResources().getColor(R.color.white));
        searchBar.setMaxSuggestionCount(3);
        dbHandlershowMember = new DBHandler(this);
        addMember.setOnClickListener(this);
        searchBar.setOnSearchActionListener(this);



    }

    private void filterMemberList(String userEnteredText) {
        List<QuestionAnswer> getMemberList = dbHandlershowMember.getSearchChildRecord(groupids, fss, configuredQuestion,userEnteredText);
        if (!getMemberList.isEmpty()) {
            Logger.logD("getChild_form_id", getMemberList.get(0).getAnswerText() + "");
            setAdapterAsigne(getMemberList, false);
            noDataLabel.setVisibility(View.GONE);
            memberListRV.setVisibility(View.VISIBLE);

        } else {
            noDataLabel.setVisibility(View.VISIBLE);
            memberListRV.setVisibility(View.GONE);
        }
    }

    private void setAdapterAsigne(List<QuestionAnswer> getMemberList, boolean checkBoxVisible) {
        HomeTileAdapter homeTileAdapter = new HomeTileAdapter(ShowMemberListActivity.this, getMemberList, 1, this, selectedListTemp, checkBoxVisible);
        GridLayoutManager lLayout = new GridLayoutManager(getApplicationContext(), 1);
        memberListRV.setAdapter(homeTileAdapter);
        memberListRV.setLayoutManager(lLayout);
    }

    @Override
    public void itemSelected(List<QuestionAnswer> selectedList) {
        selectedListTemp = selectedList;
        if (selectedList.isEmpty()) {
            addMember.setVisibility(View.GONE);
            parentTextLayout.setVisibility(View.GONE);
        } else {
            addMember.setVisibility(View.VISIBLE);
            parentTextLayout.setVisibility(View.VISIBLE);
            createText();
        }

    }

    private void createText() {
        getSelectedChildList.clear();
        LinearLayout parentLinearLayout = (LinearLayout) findViewById(R.id.parentTextLayout);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parentLinearLayout.removeAllViews();
        for (QuestionAnswer schoolTypeBean : selectedListTemp) {

            View headerLayout = inflater.inflate(R.layout.text_layout, parentLinearLayout, false);
            TextView userName = (TextView) headerLayout.findViewById(R.id.schoolInput);
            userName.setText(schoolTypeBean.getAnswerText());
            parentLinearLayout.addView(headerLayout);
            QuestionAnswer questionAnswer = new QuestionAnswer();
            questionAnswer.setSelectedChildUUID(schoolTypeBean.getSelectedChildUUID());
            questionAnswer.setChild_form_primaryid(schoolTypeBean.getChild_form_primaryid());
            getSelectedChildList.add(questionAnswer);
        }

    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.addMember) {
            if (!getSelectedChildList.isEmpty()) {
                createLinkageBundle();
            } else {
                ToastUtils.displayToast("No member selected", this);
            }

        }
    }
    private void createLinkageBundle() {
        try {
            JSONObject jsonObjectMain = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            List<Linkage> fillLinkagebean = new ArrayList<>();
            for (int i = 0; i < getSelectedChildList.size(); i++) {
                Linkage linkage = new Linkage();
                String getUUIDIfExist = dbHandlershowMember.isRecordExist(getSelectedChildList.get(i).getSelectedChildUUID(),parent_form_id);
                if (getUUIDIfExist.equals("")) {
                    getUUIDIfExist = String.valueOf(UUID.randomUUID());
                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HHmmss");
                String currentDateandTime = sdf.format(new Date());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("uuid", getUUIDIfExist);
                linkage.setUuid(String.valueOf(getUUIDIfExist));
                jsonObject.put("linked_on", currentDateandTime);
                linkage.setLinkedOn(currentDateandTime);
                jsonObject.put("active", "2");
                linkage.setActive(2);
                jsonObject.put("modified_on", currentDateandTime);
                jsonObject.put("parent_form_type", parent_form_primaryid);
                linkage.setParentFormType(parent_form_type);
                jsonObject.put("parent_form_id", parent_form_id);
                linkage.setParentFormId(parent_form_id);

                jsonObject.put("child_form_id", getSelectedChildList.get(i).getSelectedChildUUID());
                linkage.setChildFormId(getSelectedChildList.get(i).getSelectedChildUUID());

                jsonObject.put("child_form_type", getSelectedChildList.get(i).getChild_form_primaryid());
                jsonObject.put("relation_id", relationId);

                linkage.setRelationId(relationId);
                linkage.setParentFormPrimaryid(parent_form_primaryid);
                linkage.setChildFormPrimaryid(getSelectedChildList.get(i).getChild_form_primaryid());
                linkage.setChildFormType(Integer.parseInt(groupids));

                jsonArray.put(jsonObject);
                fillLinkagebean.add(linkage);

            }
            jsonObjectMain.put("linkages", jsonArray);
            Logger.logD("jsonArray", jsonObjectMain.toString());
            updateMemberToBeneficiaryLinkageTodatabase(fillLinkagebean);
            callApiToUpdateBeneficiaryLinkage(jsonArray);


        } catch (Exception e) {
            Logger.logE("", "createLinkageBundle", e);
        }


    }


    private void updateMemberToBeneficiaryLinkageTodatabase(List<Linkage> filledList) {
        if (!filledList.isEmpty()) {
            for (int i = 0; i < filledList.size(); i++) {
                long responseId = dbHandlershowMember.insertLinkageDataToDB(filledList.get(i), "0");
                Logger.logD("likage Updated Successfully", responseId + "");
            }

        }
    }


    private void callApiToUpdateBeneficiaryLinkage(JSONArray responseJson) {
        HashMap<String, String> beneficiaryLinkageParms = new HashMap<>();
        beneficiaryLinkageParms.put("URL", "/api/beneficiary-link/");
        beneficiaryLinkageParms.put("user_id", surveyPreferences.getString("UID", ""));
        beneficiaryLinkageParms.put("linkages", responseJson.toString());
        if (Utils.haveNetworkConnection(this)) {
            CallServerForApi.callServerApi(this, this, beneficiaryLinkageParms, null, APIUPDATECODE);

        } else {
            finish();
        }
    }

    @Override
    public void setResults(String results, int apiCode) {
        if (apiCode == APIUPDATECODE) {
            Logger.logD("result here", results);
            updateResponse(results);
        }
    }

    private void updateResponse(String results) {
        try {
            JSONObject jsonObject = new JSONObject(results);
            if (jsonObject.getInt("status") == 2) {
                JSONArray jsonArray = jsonObject.getJSONArray("success_records");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String getUUID = jsonObject1.getString("uuid");
                    String createdon = jsonObject1.getString("created_on");
                    int updatedResponseKey = dbHandlershowMember.updateBeneficiaryLinkageStatus(getUUID, createdon, dbHandlershowMember);
                    Logger.logD("response Updated successfullty", updatedResponseKey + "");
                    finish();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (enabled){
            tittleHeading.setVisibility(View.GONE);

        }else{
            tittleHeading.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        Logger.logD(TAG,"onSearchConfirmed");

    }

    @Override
    public void onButtonClicked(int buttonCode) {
        Logger.logD(TAG,"onButtonClicked");
    }
}
