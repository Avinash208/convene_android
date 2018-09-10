package org.fwwb.convene.convenecode.utils.multispinner;



import org.fwwb.convene.convenecode.BeenClass.parentChild.LevelBeen;

import java.util.List;

public interface SpinnerListenerFilter {
    void onItemsSelected(List<LevelBeen> items);
}
