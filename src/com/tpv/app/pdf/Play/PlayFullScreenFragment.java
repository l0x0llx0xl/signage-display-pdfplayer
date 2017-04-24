package com.tpv.app.pdf.Play;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.tpv.app.pdf.Common.Utils;
import com.tpv.app.pdf.Common.Utils_PlayListDataInfo;
import com.tpv.app.pdf.R;

import java.util.List;

/**
 * Created by Andy.Hsu on 2015/8/3.
 */
public class PlayFullScreenFragment extends Fragment {
    private static final String TAG = PlayFullScreenFragment.class.getName();
    private Context mCtx;
    private boolean mIsPlaylistValid;
    private int mDefaultFileListSize;

    private List<Utils_PlayListDataInfo> mCurUtilsPlayListDataInfo;

    private Utils.playlistStyle mPlayListStyle;
    private Utils.repeatMode mRepeatMode;
    private Utils.effectDuration mPhotoEffectDuration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.frag_play_fullscreen, null);

        mCtx = vg.getContext();

        mIsPlaylistValid = true;
        mDefaultFileListSize = 0;

        if(Utils.mUtilsLogFileDataInfo != null && Utils.mUtilsLogFileDataInfo.mUtilsErrorFileList != null) {
            Utils.mUtilsLogFileDataInfo.mUtilsErrorFileList.clear();
        }

        //Utils.mDefaultFilePath = Utils.getInternalStoragePath() + "default/pdf/";
        Utils.mDefaultFilePath = Utils.getInternalStoragePath() + Utils.FILE_FOLDER_DEFAULT;

        Utils.DEBUG_LOG(TAG, "Utils.mPlayListSelection : " + Utils.mPlayListSelection);
        mCurUtilsPlayListDataInfo = Utils.PDFPlayerContentProvider_Get_Playlist(Utils.mPlayListSelection);
        mPlayListStyle = Utils.PDFPlayerContentProvider_Get_PlaylistStyle(Utils.mPlayListSelection);
        Utils.DEBUG_LOG(TAG, "mCurUtilsPlayListDataInfo.size() : " + mCurUtilsPlayListDataInfo.size());
        if(mCurUtilsPlayListDataInfo == null || mCurUtilsPlayListDataInfo.size() == 0) {
            getDefaultFileList();
            if(mCurUtilsPlayListDataInfo == null || mDefaultFileListSize == 0) {
                mIsPlaylistValid = false;
                Utils.NotifyScalarServiceFailOver(Utils.mPlayListSelection + 1);
            }
        }
        Utils.setPlayListDataInfoTemp(mCurUtilsPlayListDataInfo);
        Utils.DEBUG_LOG(TAG, "mPlayListStyle : " + mPlayListStyle.toString());

        if(mIsPlaylistValid) {
            mRepeatMode = Utils.PDFPlayerContentProvider_Get_PlaylistRepeatModeSettings();
            mPhotoEffectDuration = Utils.PDFPlayerContentProvider_Get_EffectDuration();
            Utils.DEBUG_LOG(TAG, "mRepeatType : " + mRepeatMode.toString());
            Utils.DEBUG_LOG(TAG, "mPhotoPeriodType : " + mPhotoEffectDuration.toString());

            /** write log file */
            Utils.writeLogFileData(Utils.logFileItems.LOG_PLAY_LIST_NO, String.valueOf(Utils.mPlayListSelection + 1));

            Utils.storageType storagetype = Utils.PDFPlayerContentProvider_Get_PlaylistStorageSettings();
            String storagepath = Utils.getInternalStoragePath();
            switch (storagetype) {
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

            Utils.writeLogFileData(Utils.logFileItems.LOG_REPEAT_MODE, String.valueOf(mRepeatMode));

            Utils.writeLogFileData(Utils.logFileItems.LOG_ANI_PERIOD, String.valueOf(mPhotoEffectDuration));
            Utils.writeLogFile();
            /** ~write log file */

            Intent intent = new Intent(mCtx, MuPDFActivity.class);
            startActivity(intent);

            Utils.setUnregisterReceiverOnPause(true);
        }
        return vg;
    }

    @Override
    public void onResume() {
        Utils.DEBUG_LOG(TAG, "[onResume]");
        super.onResume();
    }

    @Override
    public void onPause() {
        Utils.DEBUG_LOG(TAG, "[onPause]");
        super.onPause();
    }

    private void getDefaultFileList() {
        mCurUtilsPlayListDataInfo.clear();
        mCurUtilsPlayListDataInfo = Utils.getStorageFiles(Utils.mDefaultFilePath, Utils.fileType.NONE);
        if(mCurUtilsPlayListDataInfo.size() > 0) {
            mCurUtilsPlayListDataInfo.remove(0);
        }
        mDefaultFileListSize = mCurUtilsPlayListDataInfo.size();

        /** check playlist style and store to contentprovider. */
        mPlayListStyle = Utils.checkPlaylistStyle(mCurUtilsPlayListDataInfo);

        Utils.DEBUG_LOG(TAG, "[getDefaultFileList] mPlayListStyle : " + mPlayListStyle);
        Utils.DEBUG_LOG(TAG, "[getDefaultFileList] mDefaultFileListSize : " + mDefaultFileListSize);
    }
}

