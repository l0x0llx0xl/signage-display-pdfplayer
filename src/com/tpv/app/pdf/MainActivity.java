package com.tpv.app.pdf;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;

import com.tpv.app.pdf.Common.Utils;
import com.tpv.app.pdf.Compose.ComposeHomeSelectPlaylistFragment;
import com.tpv.app.pdf.Home.HomeFragment;
import com.tpv.app.pdf.Settings.SettingsHomeFragment;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getName();

	/** Wake lock screen */
    protected PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.DEBUG_LOG(TAG, "[onCreate]");
        if(savedInstanceState != null) {
            Utils.DEBUG_LOG(TAG, "[onCreate] savedInstanceState.clear()");
            savedInstanceState.clear();
        }
		
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //設定隱藏APP標題
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);

        Utils.init(MainActivity.this);

        /** write log file */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        Utils.writeLogFileData(Utils.logFileItems.LOG_START_TIME, currentDateandTime);
        Utils.writeLogFileData(Utils.logFileItems.LOG_END_TIME, "");
        Utils.writeLogFile();
        /** ~write log file */

        if(!Utils.getNormalFinishPdfplayer()) {
            /** Notify mail service to get log file. */
            Utils.NotifyMailService();
            /** ~Notify mail service to get log file. */
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Utils.DEBUG_LOG(TAG, "[onNewIntent]");
        //super.onNewIntent(intent);
        setIntent(intent);//must store the new intent unless getIntent() will return the old one
        //checkIntent(intent);
    }

    @Override
    protected void onStart() {
        Utils.DEBUG_LOG(TAG, "[onStart]");

        /** bind service */
        if(Utils.SUPPORT_SCALAR_SERVICE) {
            Intent intentScalarService = new Intent("com.tpv.ScalarService.ITPVPDScalarService");
            intentScalarService.setPackage("com.tpv.ScalarService");
            bindService(intentScalarService, Utils.mConnection, Context.BIND_AUTO_CREATE);
        }
        /** ~bind service */

        /** Wake lock screen */
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "tpvMediaplayerWakeLockTag");
        this.mWakeLock.acquire();
        /** ~Wake lock screen */

        super.onStart();
    }

    @Override
    protected void onResume() {
        Utils.DEBUG_LOG(TAG, "[onResume]");
        /*
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        */

        registerReceiver();

        checkIntent(getIntent());

        MailActivityOnResume();

        super.onResume();
    }

    @Override
    protected void onPause() {
        Utils.DEBUG_LOG(TAG, "[onPause]");
        if(Utils.isUnregisterReceiverOnPause()) {
            Utils.setUnregisterReceiverOnPause(false);
            /** unregisterReceiver */
            if (mMainActivityBroadcastReceiver != null) {
                Utils.DEBUG_LOG(TAG, "[unregisterReceiver]");
                unregisterReceiver(mMainActivityBroadcastReceiver);
                mMainActivityBroadcastReceiver = null;
            }
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        Utils.DEBUG_LOG(TAG, "[onStop]");

        /** unbind service */
        if(Utils.SUPPORT_SCALAR_SERVICE) {
            unbindService(Utils.mConnection);
        }
        /** ~unbind service */

        /** Release wake lock screen */
        this.mWakeLock.release();
        /** ~Release wake lock screen */

        //finish();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Utils.DEBUG_LOG(TAG, "[onDestroy]");

        /** unregisterReceiver */
        if (mMainActivityBroadcastReceiver != null) {
            Utils.DEBUG_LOG(TAG, "[unregisterReceiver]");
            unregisterReceiver(mMainActivityBroadcastReceiver);
            mMainActivityBroadcastReceiver = null;
        }

        Utils.setNormalFinishPdfplayer("true");

        /** write log file */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        Utils.writeLogFileData(Utils.logFileItems.LOG_END_TIME, currentDateandTime);

        Utils.storageType storagetype = Utils.PDFPlayerContentProvider_Get_PlaylistStorageSettings();
        String storagepath = Utils.getInternalStoragePath();
        switch(storagetype) {
            case STORAGE_INTERNAL:
                storagepath = "Internal";
                break;
            case STORAGE_USB:
                storagepath = "USB";
                break;
            case STORAGE_SDCARD:
                storagepath = "Sdcard";
                break;
        }
        Utils.writeLogFileData(Utils.logFileItems.LOG_STORAGE, storagepath);

        Utils.repeatMode repeatMode = Utils.PDFPlayerContentProvider_Get_PlaylistRepeatModeSettings();
        Utils.writeLogFileData(Utils.logFileItems.LOG_REPEAT_MODE, String.valueOf(repeatMode));

        Utils.effectDuration photoEffectDuration = Utils.PDFPlayerContentProvider_Get_EffectDuration();
        Utils.writeLogFileData(Utils.logFileItems.LOG_ANI_PERIOD, String.valueOf(photoEffectDuration));
        Utils.writeLogFile();
        /** ~write log file */

        /** Notify mail service to get log file. */
        //Utils.NotifyMailService();
        /** ~Notify mail service to get log file. */

        /** debug for delete log file. */
        if(Utils.DEBUG_DELETE_LOGFILE) {
            Utils.deleteOldLogFile("");
        }
        /** ~debug for delete log file. */

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(Utils.getFragmanetManager().findFragmentByTag("HomeFragmentTag") != null
                && Utils.getFragmanetManager().findFragmentByTag("HomeFragmentTag").isVisible()) {
            Utils.DEBUG_LOG(TAG, "Utils.getFragmanetManager().findFragmentByTag(HomeFragmentTag).isVisible()");
            Utils.NotifyScalarSourceNormalExit();
            finish();
        }
        else {
            Utils.DEBUG_LOG(TAG, "onBackPressed");

            if(Utils.mIsStartFromSettngs) {
                if(Utils.getFragmanetManager().findFragmentByTag("ComposeHomeSelectPlaylistFragmentTag") != null
                        && Utils.getFragmanetManager().findFragmentByTag("ComposeHomeSelectPlaylistFragmentTag").isVisible()) {
                    finish();
                }
                else if(Utils.getFragmanetManager().findFragmentByTag("SettingsHomeFragmentTag") != null
                        && Utils.getFragmanetManager().findFragmentByTag("SettingsHomeFragmentTag").isVisible()) {
                    finish();
                }
                else {
                    super.onBackPressed();
                }
            }
            else {
                super.onBackPressed();
            }
        }
    }

    private BroadcastReceiver mMainActivityBroadcastReceiver;
    private void registerReceiver() {
        if (mMainActivityBroadcastReceiver == null) {
            Utils.DEBUG_LOG(TAG, "[registerReceiver]");
            mMainActivityBroadcastReceiver = new MainActivityReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Utils.INTENT_SCALARSERVICE_POWERSAVING_MODE);
            filter.addAction(Utils.INTENT_SCALARSERVICE_CHANGE_SOURCE);
            filter.addAction(Utils.INTENT_PDFPLAYER_MAIL_CALLBACK);
            registerReceiver(mMainActivityBroadcastReceiver, filter);
        }
    }

    class MainActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Utils.DEBUG_LOG(TAG, "[onReceive] MainActivityReceiver : " + intent);
            final String action = intent.getAction();
            if (action.equals(Utils.INTENT_SCALARSERVICE_CHANGE_SOURCE)) {
                Utils.DEBUG_LOG(TAG, "[onReceive] INTENT_SCALARSERVICE_CHANGE_SOURCE");
                Bundle extras = intent.getExtras();
                if(extras != null) {
                    String source = extras.getString("Source");
                    int listNo = extras.getInt("Index");
                    Utils.DEBUG_LOG(TAG, "[onReceive] source : " + source);
                    Utils.DEBUG_LOG(TAG, "[onReceive] listNo : " + listNo);
                    Utils.DEBUG_LOG(TAG, "[onReceive] Utils.mPlayListSelection : " + Utils.mPlayListSelection);
                    if(source.equals("PDFPlayer")) {
                        if ((listNo > 0) && (listNo <= Utils.playlistMenu.PLAYLIST_COUNT.ordinal())) {
                            if (Utils.mPlayListSelection != (listNo - 1)) {
                                if (Utils.getNormalFinishPdfplayer()) {
                                    Utils.setNormalFinishPdfplayer("false");
                                    Utils.resetResumeFile();
                                }
                                Utils.mPlayListSelection = (listNo - 1);
                                Utils.setDirectPlayback(true);
                                Utils.GotoFullScreenPlayback((listNo - 1));
                            }
                        }
                        else {
                            if(listNo == 0) {
                                Utils.DEBUG_LOG(TAG, "Normal start mediaplayer");
                                if(Utils.getFragmanetManager().findFragmentByTag("HomeFragmentTag") == null) {
                                    Utils.getFragmanetManager().beginTransaction()
                                            .replace(R.id.main_container, new HomeFragment(), "HomeFragmentTag")
                                            .commit();
                                }
                                else if((Utils.getFragmanetManager().findFragmentByTag("HomeFragmentTag") != null)
                                        && (!Utils.getFragmanetManager().findFragmentByTag("HomeFragmentTag").isVisible())) {
                                    Utils.getFragmanetManager().beginTransaction()
                                            .replace(R.id.main_container, new HomeFragment(), "HomeFragmentTag")
                                            .addToBackStack(null)
                                            .commit();
                                }
                            }
                            else {
                                //Utils.DEBUG_LOG(TAG, "Notify Scalar Service list not exist");
                                Utils.NotifyScalarServiceFailOver(listNo);
                            }
                        }
                    }
                    else{
                        finish();
                    }
                }
            }
            else if(action.equals(Utils.INTENT_SCALARSERVICE_POWERSAVING_MODE)) {
                Utils.DEBUG_LOG(TAG, "[onReceive] INTENT_SCALARSERVICE_POWERSAVING_MODE");
                finish();
            }
            else if(action.equals(Utils.INTENT_PDFPLAYER_MAIL_CALLBACK)) {
                //Utils.DEBUG_LOG(TAG, "[onReceive] INTENT_PDFPLAYER_MAIL_CALLBACK");
                //int result = intent.getIntExtra("result", 0);
                //String UUID = intent.getStringExtra("UUID");
                //Utils.DEBUG_LOG(TAG, "[onReceive] result : " + result);
                //Utils.DEBUG_LOG(TAG, "[onReceive] UUID : " + UUID);

                //if(Utils.DEBUG_DELETE_LOGFILE) {
                //    if(result >= 1) {
                //        Utils.deleteOldLogFile(UUID);
                //    }
                //}
            }
        }
    }

    private void checkIntent(Intent intent) {
        Utils.DEBUG_LOG(TAG, "checkIntent");

        ///** debug test */
        //List<Utils_PlayListDataInfo> curPlayListDataInfoTest = Utils.PDFPlayerContentProvider_Get_Playlist(0);
        //if(curPlayListDataInfoTest == null || curPlayListDataInfoTest.size() == 0) {
        //    Toast.makeText(this, getString(R.string.nodata), Toast.LENGTH_LONG).show();
        //}
        //else {
        //    Utils.setDirectPlaybackListNumber(0);
        //    Utils.setDirectPlayback(true);
        //}
        ///** ~debug test */


        if(intent != null) {
            Utils.mIsStartFromSettngs = false;
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String sourceFrom = extras.getString(Utils.EXTRA_KEY_SOURCE_FROM);
                Utils.DEBUG_LOG(TAG, "sourceFrom : " + sourceFrom);
                if(sourceFrom != null) {
                    if (sourceFrom.equals(Utils.EXTRA_VALUE_SOURCE_SCALAR)) {
                        int listNo = extras.getInt(Utils.EXTRA_KEY_PLAYLIST_NUMBER);
                        Utils.DEBUG_LOG(TAG, "Scalar Service extras list number : " + listNo);
                        if ((listNo > 0) && (listNo <= Utils.playlistMenu.PLAYLIST_COUNT.ordinal())) {
                            Utils.mPlayListSelection = (listNo - 1);
                            Utils.DEBUG_LOG(TAG, "[onReceive] Utils.mPlayListSelection : " + Utils.mPlayListSelection);
                            Utils.setDirectPlaybackListNumber(listNo - 1);
                            Utils.setDirectPlayback(true);
                        } else {
                            if (listNo == 0) {
                                Utils.DEBUG_LOG(TAG, "Normal start pdf");
                            } else {
                                Utils.NotifyScalarServiceFailOver(listNo);
                            }
                        }
                    } else if (sourceFrom.equals(Utils.EXTRA_VALUE_SOURCE_SETTINGS)) {
                        Utils.mIsStartFromSettngs = true;
                        String openPage = extras.getString(Utils.EXTRA_KEY_OPENPAGE);
                        if (openPage.equals(Utils.EXTRA_VALUE_OPENPAGE_COMPOSE_HOME)) {
                            Utils.DEBUG_LOG(TAG, "EXTRA_VALUE_OPENPAGE_COMPOSE_HOME");
                            Utils.setFragmanetManager(getFragmentManager());
                            Utils.getFragmanetManager().beginTransaction()
                                    .replace(R.id.main_container, new ComposeHomeSelectPlaylistFragment(), "ComposeHomeSelectPlaylistFragmentTag")
                                    .commit();
                        } else if (openPage.equals(Utils.EXTRA_VALUE_OPENPAGE_SETTINGS_HOME)) {
                            Utils.DEBUG_LOG(TAG, "EXTRA_VALUE_OPENPAGE_SETTINGS_HOME");
                            Utils.setFragmanetManager(getFragmentManager());
                            Utils.getFragmanetManager().beginTransaction()
                                    .replace(R.id.main_container, new SettingsHomeFragment(), "SettingsHomeFragmentTag")
                                    .commit();
                        }
                    } else if (sourceFrom.equals(Utils.EXTRA_VALUE_SOURCE_MUPDF)) {
                        String action = extras.getString(Utils.EXTRA_KEY_SOURCE_ACTION);
                        if (action != null && action.equals(Utils.EXTRA_VALUE_SOURCE_FINISH)) {
                            Utils.DEBUG_LOG(TAG, "EXTRA_VALUE_SOURCE_FINISH");
                            finish();
                        }
                    }
                }
            } else {
                Utils.DEBUG_LOG(TAG, "extras is null, so do nothing");
            }

            setIntent(null);
        }
    }

    private void MailActivityOnResume() {
        if(!Utils.mIsStartFromSettngs) {
            Utils.setFragmanetManager(getFragmentManager());
            Utils.getFragmanetManager().beginTransaction()
                    .replace(R.id.main_container, new HomeFragment(), "HomeFragmentTag")
                    .commit();
        }

        /** write log file */
        Utils.storageType storagetype = Utils.PDFPlayerContentProvider_Get_PlaylistStorageSettings();
        String storagepath = Utils.getInternalStoragePath();
        switch(storagetype) {
            case STORAGE_INTERNAL:
                storagepath = "Internal";
                break;
            case STORAGE_USB:
                storagepath = "USB";
                break;
            case STORAGE_SDCARD:
                storagepath = "Sdcard";
                break;
        }
        Utils.writeLogFileData(Utils.logFileItems.LOG_STORAGE, storagepath);

        Utils.repeatMode repeatMode = Utils.PDFPlayerContentProvider_Get_PlaylistRepeatModeSettings();
        Utils.writeLogFileData(Utils.logFileItems.LOG_REPEAT_MODE, String.valueOf(repeatMode));

        Utils.effectDuration photoEffectDuration = Utils.PDFPlayerContentProvider_Get_EffectDuration();
        Utils.writeLogFileData(Utils.logFileItems.LOG_ANI_PERIOD, String.valueOf(photoEffectDuration));
        Utils.writeLogFile();
        /** ~write log file */
    }

    @Override
    public void onConfigurationChanged(Configuration config) {

        super.onConfigurationChanged(config);
        Fragment currentFragment = Utils.getFragmanetManager().findFragmentById(R.id.main_container);
        FragmentTransaction fragTransaction = Utils.getFragmanetManager().beginTransaction();
        fragTransaction.detach(currentFragment);
        fragTransaction.attach(currentFragment);
        fragTransaction.commit();
    }
}
