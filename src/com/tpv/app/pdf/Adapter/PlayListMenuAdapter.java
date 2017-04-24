package com.tpv.app.pdf.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tpv.app.pdf.Common.Utils_PlayListMenuInfo;
import com.tpv.app.pdf.Common.Utils;
import com.tpv.app.pdf.R;

import java.util.List;

/**
 * Created by Andy.Hsu on 2015/7/29.
 */
public class PlayListMenuAdapter extends BaseAdapter {
    private List<Utils_PlayListMenuInfo> mUtilsPlayListMenuInfo;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private Holder holder = new Holder();
    private int arg;

    public PlayListMenuAdapter(List<Utils_PlayListMenuInfo> playlistmenuinfo, Context context) {
        this.mUtilsPlayListMenuInfo = playlistmenuinfo;
        this.mContext = context;
        layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return mUtilsPlayListMenuInfo.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mUtilsPlayListMenuInfo.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        arg = arg0;

        arg1 = layoutInflater.inflate(R.layout.playlist_menu_item, null);

        holder.textView = (TextView) arg1.findViewById(R.id.menu_list_item_name);
        holder.textView.setText(mUtilsPlayListMenuInfo.get(arg).getIconText());

        holder.hintimageView = (ImageView) arg1.findViewById(R.id.menu_list_item_hint_icon);
        if(mUtilsPlayListMenuInfo.get(arg).getListStyle() == Utils.playlistStyle.PLAYLIST_NO_DATA) {
            //holder.hintimageView.setImageResource(R.drawable.no_data_icon);
            holder.hintimageView.setVisibility(View.INVISIBLE);
        }
        else {
            holder.hintimageView.setVisibility(View.VISIBLE);
            holder.hintimageView.setImageResource(R.drawable.edited);
        }
        return arg1;
    }

    static class Holder {
        public TextView textView;
        public ImageView hintimageView;
    }
}
