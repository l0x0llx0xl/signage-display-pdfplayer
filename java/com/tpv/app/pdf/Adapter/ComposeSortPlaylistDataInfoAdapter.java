package com.tpv.app.pdf.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tpv.app.pdf.Common.Utils_PlayListDataInfo;
import com.tpv.app.pdf.Common.Utils;
import com.tpv.app.pdf.R;

import java.util.List;


/**
 * Created by Andy.Hsu on 2015/7/21.
 */
public class ComposeSortPlaylistDataInfoAdapter extends BaseAdapter {
    private static final String TAG = ComposeSortPlaylistDataInfoAdapter.class.getName();
    private List<Utils_PlayListDataInfo> mSortUtilsPlayListDataInfo;
    private int mShowMoveIconIndex;
    private Context mCtx;
    private LayoutInflater layoutInflater;
    private Holder holder = new Holder();
    private int arg;

    public ComposeSortPlaylistDataInfoAdapter(List<Utils_PlayListDataInfo> playlistdatainfo, int showmoveiconindex, Context context) {
        this.mSortUtilsPlayListDataInfo = playlistdatainfo;
        this.mShowMoveIconIndex = showmoveiconindex;
        this.mCtx = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mSortUtilsPlayListDataInfo.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mSortUtilsPlayListDataInfo.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        arg = arg0;
        arg1 = layoutInflater.inflate(R.layout.playlist_sort_item, null);
        if (mSortUtilsPlayListDataInfo.get(arg).isFile().equals("true")) {
            holder.imageIcon =(ImageView) arg1.findViewById(R.id.sort_list_item_icon);

            if(Utils.isPdf(mSortUtilsPlayListDataInfo.get(arg).getFileName())) {
                //holder.imageIcon.setImageResource(R.drawable.video_icon);
            }

            holder.textView = (TextView) arg1.findViewById(R.id.sort_list_item_name);
            holder.textView.setText(mSortUtilsPlayListDataInfo.get(arg).getFileName());

            if(arg == mShowMoveIconIndex) {
                if(Utils.SUPPORT_TOUCH) {
                    holder.linearLayout = (LinearLayout) arg1.findViewById(R.id.sort_list_item_group);
                    holder.linearLayout.setBackgroundColor(0xFF005F8C);
                }
                holder.imageMoveIcon =(ImageView) arg1.findViewById(R.id.sort_list_item_move_icon);
                holder.imageMoveIcon.setImageResource(R.drawable.sort_move_icon);
            }
        }
        return arg1;
    }

    static class Holder {
        public LinearLayout linearLayout;
        public ImageView imageIcon;
        public TextView textView;
        public ImageView imageMoveIcon;
    }

}
