package org.mahiti.convenemis.utils.multispinner;



import org.mahiti.convenemis.BeenClass.beneficiary.Datum;

import java.util.List;

public interface SpinnerListener {
    void onItemsSelected(List<Datum> items);
}
