package org.fwwb.convene.convenecode.api.BeneficiaryApis;

/**
 * Created by Aviansh Raj  on 13/09/17.
 */

@FunctionalInterface
public interface PeriodicTypeInterface {
    void onSuccessPeriodicResponse(String PeriodicResponse ,boolean flag);
}
