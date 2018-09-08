package org.fwwb.convene.convenecode.utils.multispinner;



import org.fwwb.convene.convenecode.BeenClass.beneficiary.Datum;

import java.util.List;

public interface SpinnerListener {
    void onItemsSelected(List<Datum> items);
}
