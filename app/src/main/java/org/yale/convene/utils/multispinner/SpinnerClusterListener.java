package org.yale.convene.utils.multispinner;



import org.yale.convene.BeenClass.parentChild.LevelBeen;

import java.util.List;

public interface SpinnerClusterListener {
    void onItemsSelected(List<LevelBeen> items);
}
