package com.tpv.app.pdf.Common;

import android.graphics.Bitmap;

/**
 * Created by Andy.Hsu on 2015/8/5.
 */
public class Utils_VideoDescription {
    String mFileName;
    String mResolution;
    String mDuration;
    String mLastModified;
    String mFileSize;
    String mBitRate;
    String mTitle;
    String mMimeType;
    String mArtist;
    Bitmap mBitmap;

    public Utils_VideoDescription(String fileName,
                                  String resolution,
                                  String duration,
                                  String lastModified,
                                  String fileSize,
                                  String bitRate,
                                  String title,
                                  String mimeType,
                                  String artist,
                                  Bitmap bitmap)
    {
        this.mFileName = fileName;
        this.mResolution = resolution;
        this.mDuration = duration;
        this.mLastModified = lastModified;
        this.mFileSize = fileSize;
        this.mBitRate = bitRate;
        this.mTitle = title;
        this.mMimeType = mimeType;
        this.mArtist = artist;
        this.mBitmap = bitmap;
    }

    public String getFileName() {
        return mFileName;
    }

    public String getResolution() {
        return mResolution;
    }

    public String getDuration() {
        return mDuration;
    }

    public String getLastModified() {
        return mLastModified;
    }

    public String getFileSize() {
        return mFileSize;
    }

    public String getBitRate() {
        return mBitRate;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public String getArtist() {
        return mArtist;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
}
