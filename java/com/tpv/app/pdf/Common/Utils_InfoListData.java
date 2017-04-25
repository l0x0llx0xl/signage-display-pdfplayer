package com.tpv.app.pdf.Common;

/**
 * Created by Andy.Hsu on 2015/8/6.
 */
public class Utils_InfoListData {
    String mItemName;
    String mItemValue;

    public Utils_InfoListData(String itemName, String itemValue)
    {
        this.mItemName = itemName;
        this.mItemValue = itemValue;
    }

    public String getItemName() {
        return mItemName;
    }
    public String getItemValue() {
        return mItemValue;
    }
}
