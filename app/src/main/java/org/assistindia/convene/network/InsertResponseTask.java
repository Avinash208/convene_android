package org.assistindia.convene.network;

import org.assistindia.convene.BeenClass.Response;
import org.assistindia.convene.SurveyQuestionActivity;
import org.assistindia.convene.database.DBHandler;
import org.assistindia.convene.utils.Logger;
import org.assistindia.convene.utils.PreferenceConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class InsertResponseTask {


    public boolean insertResponseTask(List<Response> response, DBHandler _handler, String questionDisplayPageCount) {
        List<Long> responseData= new ArrayList<>();
        List<Response> responseList = response;
        DBHandler handler = _handler;
        String pageNumber = questionDisplayPageCount;

        if (responseList.isEmpty()) {
            return false;
        }
            long responseId = 0;
            for (int i = 0; i < responseList.size(); i++) {
                Response response1 = responseList.get(i);
                String surveyAnswerCode = response1.getAns_code();
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(PreferenceConstants.SURVEY_ID_HASH, pageNumber);
                hashMap.put("q_id", response1.getQ_id());
                hashMap.put("ans_code", surveyAnswerCode);
                hashMap.put(PreferenceConstants.ANS_TEXT, response1.getAnswer());
                hashMap.put("pre_question", "");
                hashMap.put("next_question", "");
                hashMap.put("answered_on", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date()));
                hashMap.put("sub_questionId", response1.getSubquestionId());
                hashMap.put("q_code", String.valueOf(response1.getQcode()));
                hashMap.put("primarykey", String.valueOf(response1.getPrimarykey()));
                hashMap.put("typologyId", String.valueOf(response1.getTypologyId()));
                hashMap.put("group_id", String.valueOf(response1.getGroup_id()));
                hashMap.put("primaryID", String.valueOf(response1.getPrimaryID()));
                hashMap.put("qtype", String.valueOf(response1.getQ_type()));
                responseId = handler.insertResponseDataToDB(hashMap);
                responseData.add(responseId);
                Logger.logV("Saving", "Saved Response Successfully-->" + responseList.get(0));
            }
            if (responseData.size() == responseList.size()) {
                SurveyQuestionActivity.answersEditText.clear();
                return true;
            }
        return false;
    }

}
