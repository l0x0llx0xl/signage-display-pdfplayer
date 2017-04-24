package com.tpv.app.pdf.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tpv.app.pdf.Common.Utils_SettingsListInfo;
import com.tpv.app.pdf.R;

import java.util.List;

/**
 * Created by Andy.Hsu on 2015/7/31.
 */
public class SettingsListViewAdapter extends BaseAdapter {
    private List<Utils_SettingsListInfo> mSettingsListDataInfo;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private Holder holder = new Holder();
    private int arg;

    public SettingsListViewAdapter(List<Utils_SettingsListInfo> storagedatainfo, Context context) {
        this.mSettingsListDataInfo = storagedatainfo;
        this.mContext = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mSettingsListDataInfo.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mSettingsListDataInfo.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        arg = arg0;

        arg1 = layoutInflater.inflate(R.layout.settings_items, null);
        holder.imageIcon =(ImageView) arg1.findViewById(R.id.settings_list_item_icon);
        if(mSettingsListDataInfo.get(arg).IsSelected()) {
            holder.imageIcon.setImageResource(R.drawable.check_icon);
        }
        else {
            //holder.imageIcon.setImageResource(R.drawable.circle_uncheck_icon);
        }

        holder.textView = (TextView) arg1.findViewById(R.id.settings_list_item_name);
        holder.textView.setText(mSettingsListDataInfo.get(arg).getItemText());
        return arg1;
    }

    static class Holder {
        public ImageView imageIcon;
        public TextView textView;
    }

}
