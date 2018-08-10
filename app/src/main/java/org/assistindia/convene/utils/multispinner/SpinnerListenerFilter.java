package org.assistindia.convene.utils.multispinner;



import org.assistindia.convene.BeenClass.parentChild.LevelBeen;

import java.util.List;

public interface SpinnerListenerFilter {
    void onItemsSelected(List<LevelBeen> items);
}
