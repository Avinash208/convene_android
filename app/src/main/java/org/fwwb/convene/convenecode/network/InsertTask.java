package org.fwwb.convene.convenecode.network;

import org.fwwb.convene.convenecode.BeenClass.Response;
import org.fwwb.convene.convenecode.SurveyQuestionActivity;
import org.fwwb.convene.convenecode.database.DBHandler;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.convenecode.utils.PreferenceConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Aviansh raj  on 12/01/17.
 */
public class InsertTask {


    public boolean insertTask(List<Response> response, DBHandler _handler, String questionDisplayPageCount) {
        List<Long> responseData= new ArrayList<>();
        List<Response> capturedAnswer = response;
        DBHandler handler = _handler;
        String pageNumber = questionDisplayPageCount;

        if (capturedAnswer.isEmpty()) {
            return false;
        }
            long responseId = 0;
            for (int i = 0; i < capturedAnswer.size(); i++) {
                Response responseObj = capturedAnswer.get(i);
                String answerCode = responseObj.getAns_code();
                HashMap<String, String> checkboxInsert = new HashMap<>();
                checkboxInsert.put(PreferenceConstants.SURVEY_ID_HASH, pageNumber);
                checkboxInsert.put("q_id", responseObj.getQ_id());
                checkboxInsert.put("ans_code", answerCode);
                checkboxInsert.put(PreferenceConstants.ANS_TEXT, responseObj.getAnswer());
                checkboxInsert.put("pre_question", "");
                checkboxInsert.put("next_question", "");
                checkboxInsert.put("answered_on", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date()));
                checkboxInsert.put("sub_questionId", responseObj.getSub_questionId());
                checkboxInsert.put("q_code", String.valueOf(responseObj.getQcode()));
                checkboxInsert.put("primarykey", String.valueOf(responseObj.getPrimarykey()));
                checkboxInsert.put("typologyId", String.valueOf(responseObj.getTypologyId()));
                checkboxInsert.put("group_id", String.valueOf(responseObj.getGroup_id()));
                checkboxInsert.put("primaryID", String.valueOf(responseObj.getPrimaryID()));
                checkboxInsert.put("qtype", String.valueOf(responseObj.getQ_type()));
                if(responseObj.getGroup_id()!=0 || "0".equalsIgnoreCase(responseObj.getSub_questionId()))
                    checkboxInsert.put("response_dump_pid", String.valueOf(pageNumber));
                responseId = handler.insertResponseDataToDB(checkboxInsert);
                responseData.add(responseId);
                Logger.logV("Saving", "Saved Response Successfully-->" + responseId);
            }
            if (responseData.size() == capturedAnswer.size()) {
                SurveyQuestionActivity.answersEditText.clear();
                return true;
            }
        return false;
    }

}
