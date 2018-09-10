package org.fwwb.convene.convenecode.utils;

import org.fwwb.convene.convenecode.BeenClass.beneficiary.Address;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mahiti on 21/2/18.
 */

public interface UpdateAddressDetails {

    void onUpdateAddressSuccess(HashMap<Integer,List<Address>> listHashMap);
}
