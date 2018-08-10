package org.assistindia.convene.utils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Copyright Mahiti Infotech Pvt Ltd (here after referred to as Mahiti) 2017.
 * All rights reserved. This library cannot be repackaged, included in any other application, reverse engineered, altered or extended without written permission from Mahiti.
 */

public class RuleEngineUtils {
    final static String TAG = "RuleEngineUtils";

    private RuleEngineUtils() {
        Logger.logV(Constants.DO_NOTHING,Constants.DO_NOTHING);
    }

    public static String fetchRuleEngineFromPrefs(String ruleEngine) {
        String queryHelperText="";
        try {
            JSONArray jsonArray=new JSONArray(ruleEngine);
            if(jsonArray.length()>0 && jsonArray.getJSONObject(0).has("rules")) {
                String ruleList = jsonArray.getJSONObject(0).getString("rules");
                JSONArray ruleListJsonArray = new JSONArray(ruleList);
                Logger.logD(TAG, "ruleListJsonArray : " + ruleListJsonArray.toString());
                if(ruleListJsonArray.length()>0 && ruleListJsonArray.getJSONObject(0).has("type")) {
                    JSONObject ruleObj = new JSONObject(ruleListJsonArray.get(0).toString());
                    Logger.logD(TAG, "ruleObj : " + ruleObj.toString());
                    if (ruleObj.has("Operation") && "!=".equalsIgnoreCase(ruleObj.getString("Operation"))){
                        queryHelperText= "NOT IN"+"("+ruleObj.getString("value")+")";
                        Logger.logD(TAG, "queryHelperText : " + queryHelperText);
                    }
                }
            }
            return queryHelperText;
        }catch (Exception e){
            Logger.logE(TAG, "fetchRuleEngineFromPrefs : ", e);
        }
        return queryHelperText;
    }
}
