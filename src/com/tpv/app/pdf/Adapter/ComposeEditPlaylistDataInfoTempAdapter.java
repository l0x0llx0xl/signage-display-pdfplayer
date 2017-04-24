package com.tpv.app.pdf.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tpv.app.pdf.Common.Utils;
import com.tpv.app.pdf.Common.Utils_PlayListDataInfo;
import com.tpv.app.pdf.R;

import java.util.List;


/**
 * Created by Andy.Hsu on 2015/7/21.
 */
public class ComposeEditPlaylistDataInfoTempAdapter extends BaseAdapter {
    private List<Utils_PlayListDataInfo> mUtilsPlayListDataInfoTemp;
    private Context mCtx;
    private LayoutInflater layoutInflater;
    private Holder holder = new Holder();
    private int arg;
    private int selectedIndex;

    public ComposeEditPlaylistDataInfoTempAdapter(List<Utils_PlayListDataInfo> playlistdatainfo, Context context) {
        this.mUtilsPlayListDataInfoTemp = playlistdatainfo;
        this.mCtx = context;
        layoutInflater = LayoutInflater.from(context);
        selectedIndex = -1;
    }
    public void setSelectedIndex(int ind) {
        selectedIndex = ind;
    }
    public int getSelectedIndex() {
        return selectedIndex;
    }

    @Override
    public int getCount() {
        return mUtilsPlayListDataInfoTemp.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mUtilsPlayListDataInfoTemp.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        arg = arg0;

        arg1 = layoutInflater.inflate(R.layout.playlist_edit_item, null);

        if (mUtilsPlayListDataInfoTemp.get(arg).isSelected().equals("true")) {
            holder.imageIcon = (ImageView) arg1.findViewById(R.id.list_item_icon);
            holder.imageIcon.setVisibility(View.GONE);

            holder.textView = (TextView) arg1.findViewById(R.id.list_item_name);
            holder.textView.setText(mUtilsPlayListDataInfoTemp.get(arg).getFileName());
        }

        if(Utils.SUPPORT_TOUCH) {
            holder.linearlayout = (LinearLayout) arg1.findViewById(R.id.list_item_layout);
            if (selectedIndex != -1 && arg == selectedIndex) {
                holder.linearlayout.setBackgroundColor(0xFF005F8C);
                holder.textView.setSelected(true);
            }
        }
        return arg1;
    }

    static class Holder {
        public LinearLayout linearlayout;
        public ImageView imageIcon;
        public TextView textView;
    }

}
