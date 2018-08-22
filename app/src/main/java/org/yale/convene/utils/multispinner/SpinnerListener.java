package org.yale.convene.utils.multispinner;



import org.yale.convene.BeenClass.beneficiary.Datum;

import java.util.List;

public interface SpinnerListener {
    void onItemsSelected(List<Datum> items);
}
