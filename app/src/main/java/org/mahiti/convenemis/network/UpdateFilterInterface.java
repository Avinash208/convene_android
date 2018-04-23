package org.mahiti.convenemis.network;

import org.mahiti.convenemis.BeenClass.beneficiary.Datum;

import java.util.List;

/**
 * Created by mahiti on 30/5/17.
 */

public interface UpdateFilterInterface
{
    void UpdateFilterAdapter(List<Datum> getSortedList);
    void UpdateFilterAdapterFacility(List<org.mahiti.convenemis.BeenClass.facilities.Datum> getSortedList);
}
