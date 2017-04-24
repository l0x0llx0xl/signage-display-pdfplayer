// ITPVSICPCallback.aidl
package com.tpv.ScalarService;

// Declare any non-default types here with import statements

interface ITPVSICPCallback {
    /**
     * callback for SICp reply data
     */
    void onSICPReply(in byte [] byteSICPReply);

    /**
     * callback for MonitorID
     */
    void onUpdateMonitorID(int nMonitorID);

    /**
     * callback for GroupID
     */
    void onUpdateGroupID(int nGroupID);

}
