package org.mahiti.convenemis;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.mahiti.convenemis.utils.CheckNetwork;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.ProgressUtils;
import org.mahiti.convenemis.utils.ToastUtils;
import org.mahiti.convenemis.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class PopUpShow {
    private PopUpShow() {
        Logger.logV(Constants.DO_NOTHING,Constants.DO_NOTHING);
    }

    public static void showingErrorPopUp(final Activity activity, final String rId) {
        if(!new CheckNetwork(activity).checkNetwork()){
            ToastUtils.displayToast(activity.getString(R.string.checkNet),activity.getApplicationContext());
            return;
        }
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.error_popup);
        final Context context= activity;
        int width = (int)(activity.getResources().getDisplayMetrics().widthPixels*0.90);
        int height = (int)(activity.getResources().getDisplayMetrics().heightPixels*0.90);

        dialog.getWindow().setLayout(width, height);
        dialog.setTitle("Facing an issue");
        final RadioButton pjmeetings = dialog.findViewById(R.id.meeting);
        final RadioButton pjBeneficiary = dialog.findViewById(R.id.beneficiary);
        final RadioButton facility= dialog.findViewById(R.id.facility);
        final RadioButton pjdatacollection = dialog.findViewById(R.id.datacollection);
        final EditText edtIssue = dialog.findViewById(R.id.edtIssue);
        Button btnIssue = dialog.findViewById(R.id.btnIssue);
        dialog.show();
        final ProgressDialog progressDialog = ProgressUtils.showProgress(activity, false, "Loading ...");
        btnIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.haveNetworkConnection(activity)){
                    String issueKey = "";
                    if (edtIssue.getText().toString().length() < 2) {
                        ToastUtils.displayToast("Enter the description ", activity);
                        return;
                    }
                    if (pjmeetings.isChecked()) {
                        issueKey = "Beneficiary";
                    } else if (pjBeneficiary.isChecked()) {
                        issueKey = "Beneficiary";
                    }else if(pjdatacollection.isChecked()) {
                        issueKey = "data collection form";
                    }else if(facility.isChecked()){
                        issueKey="facility";
                    }
                    Map<String, String> params = new HashMap<>();
                    params.put("userId", rId);
                    params.put("issue_key", issueKey);
                    params.put("description", edtIssue.getText().toString());
                    if (Utils.haveNetworkConnection(activity)) {
                        if (pjdatacollection.isChecked() || pjBeneficiary.isChecked() || facility.isChecked()) {
                            issueApi(dialog, progressDialog, params, activity, issueKey);
                        } else {
                            ToastUtils.displayToast("Please select options ", activity);
                            return;
                        }
                    }
                    else {
                        ToastUtils.displayToast("Check internet", activity);
                    }
                }else{
                    ToastUtils.displayToast("Please check internet connection.",context);
                }
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }

    private static void issueApi(final Dialog dialog, final ProgressDialog progressDialog, final Map<String, String> map, final Activity activity, final String key) {
        String url = activity.getString(R.string.main_url)+"app-issue-tracker/";
        Logger.logD("issueApi send", "issueApi send==" + url + "_--" + map);
        progressDialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ProgressUtils.CancelProgress(progressDialog);
                        Logger.logV("the response is ", "the response issueApi" + response);
                        if ("enroll".equalsIgnoreCase(key)) {
                            if (dialog.isShowing())
                                dialog.dismiss();
                            try {
                                JSONObject result = new JSONObject(response);
                                ToastUtils.displayToast(result.getString("message"), activity);
                            } catch (Exception e) {
                                Logger.logE(PopUpShow.class.getName(), "facing an issue---", e);
                            }
                        } else if ("engage".equalsIgnoreCase(key)) {
                            ToastUtils.displayToast("We got your problem, will get back to you", activity);
                            if (dialog.isShowing())
                                dialog.dismiss();
                        }
                    }
                },
                error -> {
                    error.printStackTrace();
                    ProgressUtils.CancelProgress(progressDialog);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return map;
            }
        };
        Volley.newRequestQueue(activity).add(postRequest);
    }

}
