package com.tpv.app.pdf.Common;

/**
 * Created by Andy.Hsu on 2015/8/25.
 */
public class Utils_ErrorFileList {
    String mFilePath;
    public Utils_ErrorFileList(String filePath) {
        this.mFilePath = filePath;
    }

    public void setErrorFilePath(String path) {
        mFilePath = path;
    }
    public String getErrorFilePath() {
        return mFilePath;
    }
}
