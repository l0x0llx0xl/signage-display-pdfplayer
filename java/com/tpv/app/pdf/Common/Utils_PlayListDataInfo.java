package com.tpv.app.pdf.Common;

/**
 * Created by Andy.Hsu on 2015/7/21.
 */
public class Utils_PlayListDataInfo {
    String mFileName;
    String mFilePath;
    String isFile;
    String isResume;
    String isSelected;

    public Utils_PlayListDataInfo(String fileName,
                                  String filePath,
                                  String isFile,
                                  String isResume,
                                  String isSelected)
    {
        this.mFileName = fileName;
        this.mFilePath = filePath;
        this.isFile = isFile;
        this.isResume = isResume;
        this.isSelected = isSelected;
    }

    public String getFileName() {
        return mFileName;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public String isFile() {
        return isFile;
    }

    public void setResume(String resume) {
        isResume = resume;
    }

    public String isResume() {
        return isResume;
    }

    public void setSelected(String selected) {
        isSelected = selected;
    }

    public String isSelected() {
        return isSelected;
    }
}
