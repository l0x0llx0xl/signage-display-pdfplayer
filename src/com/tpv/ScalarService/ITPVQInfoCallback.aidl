// ITPVQInfoCallback.aidl
package com.tpv.ScalarService;

// Declare any non-default types here with import statements

interface ITPVQInfoCallback {
    /**
     * callback for temperature
     */
    void onUpdateTemperature(int nTemperature);

    /**
     * callback for GroupID
     */
    void onUpdateOperationHour(int nOperationHour);
}
