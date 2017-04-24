package com.tpv.app.pdf.Common;

import android.graphics.Bitmap;

/**
 * Created by Andy.Hsu on 2015/8/5.
 */
public class Utils_PhotoDescription {
    String mFileName;
    String mResolution;
    String mLastModified;
    String mFileSize;
    Bitmap mBitmap;

    public Utils_PhotoDescription(String fileName,
                                  String resolution,
                                  String lastModified,
                                  String fileSize,
                                  Bitmap bitmap)
    {
        this.mFileName = fileName;
        this.mResolution = resolution;
        this.mLastModified = lastModified;
        this.mFileSize = fileSize;
        this.mBitmap = bitmap;
    }

    public String getFileName() {
        return mFileName;
    }

    public String getResolution() {
        return mResolution;
    }

    public String getLastModified() {
        return mLastModified;
    }

    public String getFileSize() {
        return mFileSize;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
}
