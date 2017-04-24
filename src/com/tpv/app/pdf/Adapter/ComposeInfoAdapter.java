package com.tpv.app.pdf.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tpv.app.pdf.Common.Utils_InfoListData;
import com.tpv.app.pdf.R;

import java.util.List;

/**
 * Created by Andy.Hsu on 2015/8/6.
 */
public class ComposeInfoAdapter extends BaseAdapter {
    private List<Utils_InfoListData> mUtilsInfoListData;
    private Context mCtx;
    private LayoutInflater layoutInflater;
    private Holder holder = new Holder();
    private int arg;

    public ComposeInfoAdapter(List<Utils_InfoListData> infolistdata, Context context) {
        this.mUtilsInfoListData = infolistdata;
        this.mCtx = context;
        layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return mUtilsInfoListData.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mUtilsInfoListData.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        arg = arg0;

        arg1 = layoutInflater.inflate(R.layout.info_item, null);
        holder.itemTextName = (TextView) arg1.findViewById(R.id.list_info_item_name);
        holder.itemTextName.setText(mUtilsInfoListData.get(arg).getItemName());
        holder.itemTextName.setSelected(true);

        holder.itemTextValue = (TextView) arg1.findViewById(R.id.list_info_item_value);
        holder.itemTextValue.setText(mUtilsInfoListData.get(arg).getItemValue());
        holder.itemTextValue.setSelected(true);

        return arg1;
    }

    static class Holder {
        public TextView itemTextName;
        public TextView itemTextValue;
    }

}
