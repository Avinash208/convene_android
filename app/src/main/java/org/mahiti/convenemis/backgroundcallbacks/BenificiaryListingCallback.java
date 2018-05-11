package org.mahiti.convenemis.backgroundcallbacks;

import org.mahiti.convenemis.BeenClass.SurveysBean;

import java.util.List;

/**
 * Created by mahiti on 10/05/18.
 */

public interface BenificiaryListingCallback {
    void surveysDetails(List<SurveysBean> pendingSurvey);
}
