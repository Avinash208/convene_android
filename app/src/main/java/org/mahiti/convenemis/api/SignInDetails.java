package org.mahiti.convenemis.api;

/**
 * Created by mahiti on 24/7/17.
 */

@FunctionalInterface
public interface SignInDetails {
    void signingDetails(int var1, int var2, String userName, int partner_id, String response);
}

