package org.fwwb.convene.convenecode.network;

import org.fwwb.convene.convenecode.BeenClass.beneficiary.Datum;

import java.util.List;

/**
 * Created by mahiti on 30/5/17.
 */

public interface UpdateFilterInterface
{
    void UpdateFilterAdapter(List<Datum> getSortedList);
    void UpdateFilterAdapterFacility(List<org.fwwb.convene.convenecode.BeenClass.facilities.Datum> getSortedList);
}
