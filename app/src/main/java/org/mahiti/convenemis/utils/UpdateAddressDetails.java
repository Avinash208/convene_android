package org.mahiti.convenemis.utils;

import org.mahiti.convenemis.BeenClass.beneficiary.Address;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mahiti on 21/2/18.
 */

public interface UpdateAddressDetails {

    void onUpdateAddressSuccess(HashMap<Integer,List<Address>> listHashMap);
}
