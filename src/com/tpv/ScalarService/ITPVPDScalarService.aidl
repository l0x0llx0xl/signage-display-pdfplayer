// ITPVPDScalarService.aidl
package com.tpv.ScalarService;

// Declare any non-default types here with import statements
import com.tpv.ScalarService.ITPVSICPCallback;
import com.tpv.ScalarService.ITPVOTAUpgradeStatus;
import com.tpv.ScalarService.ITPVQInfoCallback;
import com.tpv.ScalarService.IPVSettingsCallback;

interface ITPVPDScalarService {

    void setErrMessages(int nErrCode);
    void setFailOver(String strSource, int nIndex);

    String getCurrentSource();

    /**
     * set PD current input source
     */
    void setPDCurrentInputSource(int nSource, int iIndex);

    /**
     * get PD current input source
     */
    int[] getPDCurrentInputSource();

    /**
     * get PD current input source string
     */
    String[] getPDCurrentInputSourceString();

    /**
     * get PD current input source info
     */
    String[] getPDCurrentInputSourceInfoString();

    /**
     * send SIPC commands
     */
    void sendSICPCmd(in byte [] SICPCmd);

    /**
     * OTA informs if there is new OTA FW
     */
    void setNewFW(boolean bIsNewFW);

    /**
     * OTA informs if OTA is ready to upgrade
     */
    void setUpgradeReady(boolean bIsReady);

    /**
     * OTA informs if validating OTA file is ok
     */
    void setOTAValidation(boolean bIsValid);

    /**
     * OTA informs if OTA download is ready
     */
    void setDownloadReady(boolean bIsReady);

    /**
     * provide functions for app to register SICP callbacks
     */
    void registerSICPCallback(ITPVSICPCallback cb);
    void unregisterSICPCallback(ITPVSICPCallback cb);

    /**
     * provide functions for app to register OTA callbacks
     */
    void registerOTACallback(ITPVOTAUpgradeStatus cb);
    void unregisterOTACallback(ITPVOTAUpgradeStatus cb);

    /**
     * provide functions for app to register OTA callbacks
     */
    void registerQInfoCallback(ITPVQInfoCallback cb);
    void unregisterQInfoCallback(ITPVQInfoCallback cb);

    /**
     * Notify exiting Admon mode
     */
    void exitAdminMode();

    /**
     * Notify Screenshot of settings changed
     */
    void notifyScreenShotChanged();

    /**
     * Notify Scheduler of settings changed
     */
    void notifySchedulerChanged();

    /**
     * Notify the OOBE is exiting with reboot
     */
    void notifyOOBEExit(boolean bReboot);

    /**
     * Notify the source apk is normal exiting (exiting by back key and going to launcher).
     */
    void notifySourceNormalExit(String strSourceName);

    /**
     * Query if the source normally exits to launcher
     */
    boolean IsSourceNormalExit(String strSourceName);

    /**
     * Notify Launcher is on Resume state
     */
     void notifyLauncherOnResume();

     /**
      * Get PD GroupID
      */
     int getPDGroupID();

     /**
      * Get PD MonitorID
      */
     int getPDMonitorID();

     /**
      * Query if source is switching
      */
     boolean IsSourceSwitching();

     /**
      * Query and update for ApkMonitor if source is switching
      */
     boolean IsSourceSwitchingForAPKMonitor();
     void updateSourceSwitchingForAPKMonitor();

    /**
      * Get Scalar Serial No
      */
    String getScalarSerialNo();

     /**
      * Get SICP version
      */
    String getSICPVersion();

     /**
      * Get Scalar FW version
      */
    String getScalarFWVersion();

    /**
     * Query Temperature
     */
    void queryScalarTemperature();

    /**
      * Query Scalar Operation Hours
      */
    void queryScalarOperationHours();

    /**
     * Notify the Off Time has been changed
     */
    void notifyOffTimeChanged();

    /**
     * Notify SmartCMS is running
     */
    void notifySmartRunningState(boolean bStop);

    /**
     * Notify CustomZoom settings changed
     */
    void notifyCustomZoomChanged();

    /**
     * change new port of SICP LAN
     */
    boolean startSICPLanSocket(int nPort);

    /**
     * Stop SICP LAN Socket
     */
    boolean stopSICPLanSocket();

    /**
     * isSICPLanSocketConnected
     */
    boolean isSICPLanSocketConnected(int nPort);

    /**
     * provide functions for changing lan port result  callbacks
     */
    void registerSettingsCallback(IPVSettingsCallback cb);
    void unregisterSettingsCallback(IPVSettingsCallback cb);
}
