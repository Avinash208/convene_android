package org.mahiti.convenemis.utils.multispinner;



import org.mahiti.convenemis.BeenClass.parentChild.LevelBeen;

import java.util.List;

public interface SpinnerListenerFilter {
    void onItemsSelected(List<LevelBeen> items);
}
