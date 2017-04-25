package com.tpv.app.pdf.Common;

/**
 * Created by Andy.Hsu on 2015/7/31.
 */
public class Utils_SettingsListInfo {
    public int mIconId;
    public String mItemText;
    public boolean mIsSelected;

    public Utils_SettingsListInfo(int iconId, String itemText, boolean isSelected) {
        this.mIconId = iconId;
        this.mItemText = itemText;
        this.mIsSelected = isSelected;
    }


    public int getIconId() {
        return mIconId;
    }

    public String getItemText() {
        return mItemText;
    }

    public boolean IsSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }
}
