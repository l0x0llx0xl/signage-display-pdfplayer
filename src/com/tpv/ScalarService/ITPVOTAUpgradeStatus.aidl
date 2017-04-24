// ITPVOTAUpgradeStatus.aidl
package com.tpv.ScalarService;

// Declare any non-default types here with import statements

interface ITPVOTAUpgradeStatus {

    // query redbend service if there is new FW
    void onQueryNewFW();

    // notify OTA to start downloading ota package
    boolean onStartDownload();
    // notify OTA to start updating ota package
    boolean onStartUpdate();

//    // Notify OTA app to start checking OTA package.
//    boolean onStartUpgradeChecking();

//    // Notify OTA app that it could do updagrade now.
//    void onDoUpgrade();
}
