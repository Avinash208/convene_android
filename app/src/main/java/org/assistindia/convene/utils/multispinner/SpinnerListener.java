package org.assistindia.convene.utils.multispinner;



import org.assistindia.convene.BeenClass.beneficiary.Datum;

import java.util.List;

public interface SpinnerListener {
    void onItemsSelected(List<Datum> items);
}
