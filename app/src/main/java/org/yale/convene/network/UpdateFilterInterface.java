package org.yale.convene.network;

import org.yale.convene.BeenClass.beneficiary.Datum;

import java.util.List;

/**
 * Created by mahiti on 30/5/17.
 */

public interface UpdateFilterInterface
{
    void UpdateFilterAdapter(List<Datum> getSortedList);
    void UpdateFilterAdapterFacility(List<org.yale.convene.BeenClass.facilities.Datum> getSortedList);
}
