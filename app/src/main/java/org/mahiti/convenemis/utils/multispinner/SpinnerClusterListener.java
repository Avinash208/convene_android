package org.mahiti.convenemis.utils.multispinner;



import org.mahiti.convenemis.BeenClass.parentChild.LevelBeen;

import java.util.List;

public interface SpinnerClusterListener {
    void onItemsSelected(List<LevelBeen> items);
}
