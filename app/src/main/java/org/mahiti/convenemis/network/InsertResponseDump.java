package org.mahiti.convenemis.network;

import org.mahiti.convenemis.BeenClass.Response;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.PreferenceConstants;

import java.util.HashMap;

/**
 * Created by Aviansh Raj  on 20/06/17.
 */
public class InsertResponseDump {
    Response capturedAnswer;
    static DBHandler handler;
    String Survey_id;
    String jsonDump;
    public void InsertResponseDump(Response response, DBHandler handler, String surveyPrimaryKeyId, String jsonDump) {

        this.capturedAnswer = response;
        InsertResponseDump.handler = handler;
        this.Survey_id=surveyPrimaryKeyId;
        this.jsonDump=jsonDump;

        HashMap<String, String> checkboxInsert = new HashMap<>();
        checkboxInsert.put(PreferenceConstants.SURVEY_ID, Survey_id);
        checkboxInsert.put("q_id", capturedAnswer.getQ_id());
        checkboxInsert.put("json_dump", jsonDump);
       Long responseId = handler.insertResponseJSONDump(checkboxInsert);
        Logger.logD("DATABASE"," inserted successfully"+responseId);


    }
}
