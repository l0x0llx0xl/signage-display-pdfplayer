// IPVSettingsCallback.aidl
package com.tpv.ScalarService;

// Declare any non-default types here with import statements

interface IPVSettingsCallback {
    /**
     * callback for GroupID
     */
    void onChangeLanPortResult(boolean bSuccess);
}
