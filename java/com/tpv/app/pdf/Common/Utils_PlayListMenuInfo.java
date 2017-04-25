package com.tpv.app.pdf.Common;

/**
 * Created by Andy.Hsu on 2015/7/29.
 */
public class Utils_PlayListMenuInfo {
    int mIconId;
    String mIconText;
    Utils.playlistStyle mListStyle;

    public Utils_PlayListMenuInfo(int iconId,
                                  String iconText,
                                  Utils.playlistStyle listStyle)
    {
        this.mIconId = iconId;
        this.mIconText = iconText;
        this.mListStyle = listStyle;
    }

    public int getIconId() {
        return mIconId;
    }

    public String getIconText() {
        return mIconText;
    }

    public Utils.playlistStyle getListStyle() {
        return mListStyle;
    }
}
