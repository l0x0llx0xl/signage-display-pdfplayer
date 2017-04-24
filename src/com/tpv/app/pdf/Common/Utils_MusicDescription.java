package com.tpv.app.pdf.Common;

/**
 * Created by Andy.Hsu on 2015/8/5.
 */
public class Utils_MusicDescription {
    String mFileName;
    String mDuration;
    String mLastModified;
    String mFileSize;
    String mBitRate;
    String mTitle;
    String mAlbum;
    String mMimeType;
    String mArtist;

    public Utils_MusicDescription(String fileName,
                                  String duration,
                                  String lastModified,
                                  String fileSize,
                                  String bitRate,
                                  String title,
                                  String album,
                                  String mimeType,
                                  String artist)
    {
        this.mFileName = fileName;
        this.mDuration = duration;
        this.mLastModified = lastModified;
        this.mFileSize = fileSize;
        this.mBitRate = bitRate;
        this.mTitle = title;
        this.mAlbum = album;
        this.mMimeType = mimeType;
        this.mArtist = artist;
    }

    public String getFileName() {
        return mFileName;
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

    public String getAlbum() {
        return mAlbum;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public String getArtist() {
        return mArtist;
    }
}
