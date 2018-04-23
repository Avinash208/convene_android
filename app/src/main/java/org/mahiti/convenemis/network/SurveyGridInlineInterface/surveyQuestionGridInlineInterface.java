package org.mahiti.convenemis.network.SurveyGridInlineInterface;

import android.view.View;

import org.mahiti.convenemis.BeenClass.Response;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Aviansh Raj  on 05/06/17.
 */
public interface surveyQuestionGridInlineInterface {
    void OnSuccessfullGridInline(HashMap<String, List<Response>> hashMapGridResponse, View v, int currentQuestionNumber, HashMap<String, List<String>> fillInlineHashMapKey, int gridType);
}
