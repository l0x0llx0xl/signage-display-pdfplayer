package com.tpv.app.pdf.Common;

/**
 * Created by Andy.Hsu on 2015/7/22.
 */
public class Utils_HintBarInfo {
    int mIconId;
    String mIconText;

    public Utils_HintBarInfo(int iconId,
                             String iconText)
    {
        this.mIconId = iconId;
        this.mIconText = iconText;
    }

    public int getIconId() {
        return mIconId;
    }

    public String getIconText() {
        return mIconText;
    }
}
