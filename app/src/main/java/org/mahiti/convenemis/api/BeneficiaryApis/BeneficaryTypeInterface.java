package org.mahiti.convenemis.api.BeneficiaryApis;


@FunctionalInterface
public interface BeneficaryTypeInterface {

    void onSuccessBeneficiaryResponse(String response,boolean flag);
}
