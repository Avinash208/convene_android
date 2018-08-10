package org.assistindia.convene.backgroundcallbacks;

import org.assistindia.convene.BeenClass.SurveysBean;

import java.util.List;

/**
 * Created by mahiti on 10/05/18.
 */

public interface BenificiaryListingCallback {
    void surveysDetails(List<SurveysBean> pendingSurvey);
}
