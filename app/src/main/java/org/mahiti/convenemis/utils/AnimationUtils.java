package org.mahiti.convenemis.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;

/**
 * Copyright Mahiti Infotech Pvt Ltd (here after referred to as Mahiti) 2017.
 * All rights reserved. This library cannot be repackaged, included in any other application, reverse engineered, altered or extended without written permission from Mahiti.
 */

public class AnimationUtils {

    private AnimationUtils() {
        Logger.logV(Constants.DO_NOTHING,Constants.DO_NOTHING);
    }

    public static void viewAnimation(View view){
        AlphaAnimation animation1 = new AlphaAnimation(0.2f, 1.0f);
        animation1.setDuration(50);
        view.setAlpha(1f);
        view.startAnimation(animation1);
    }
}
