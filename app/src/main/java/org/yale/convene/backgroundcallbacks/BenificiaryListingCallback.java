package org.yale.convene.backgroundcallbacks;

import org.yale.convene.BeenClass.SurveysBean;

import java.util.List;

/**
 * Created by mahiti on 10/05/18.
 */

public interface BenificiaryListingCallback {
    void surveysDetails(List<SurveysBean> pendingSurvey);
}
