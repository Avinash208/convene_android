package org.mahiti.convenemis.view;

import org.mahiti.convenemis.BeenClass.beneficiary.Datum;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mahiti on 11/04/18.
 */

public interface HomeViewInterface {

    void getExpandableListHeading(List<String> listDataHeader, HashMap<String, List<Datum>> listDataChild);

}
