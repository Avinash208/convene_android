package org.assistindia.convene.utils.multispinner;



import org.assistindia.convene.BeenClass.parentChild.LevelBeen;

import java.util.List;

public interface SpinnerClusterListener {
    void onItemsSelected(List<LevelBeen> items);
}
