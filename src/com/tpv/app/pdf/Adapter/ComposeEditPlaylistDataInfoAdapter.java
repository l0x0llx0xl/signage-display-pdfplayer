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
public class ComposeEditPlaylistDataInfoAdapter extends BaseAdapter {
    private List<Utils_PlayListDataInfo> mUtilsPlayListDataInfoStorage;
    private Context mCtx;
    private LayoutInflater layoutInflater;
    private Holder holder = new Holder();
    private int arg;
    private int selectedIndex;

    public ComposeEditPlaylistDataInfoAdapter(List<Utils_PlayListDataInfo> playlistdatainfo, Context context) {
        this.mUtilsPlayListDataInfoStorage = playlistdatainfo;
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
        return mUtilsPlayListDataInfoStorage.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mUtilsPlayListDataInfoStorage.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        arg = arg0;

        arg1 = layoutInflater.inflate(R.layout.playlist_edit_item, null);

        holder.imageIcon =(ImageView) arg1.findViewById(R.id.list_item_icon);
        holder.textView = (TextView) arg1.findViewById(R.id.list_item_name);
        if (mUtilsPlayListDataInfoStorage.get(arg).isFile().equals("false")) {
            if(mUtilsPlayListDataInfoStorage.get(arg).getFileName().equals(Utils.FILE_FOLDER_PDF)) {
                holder.imageIcon.setImageResource(R.drawable.base_folder);
                holder.textView.setText("PDF" + "(" + mCtx.getText(R.string.storagepath) + "/" + Utils.getFileFolderPublic() + "/" + Utils.FILE_FOLDER_PDF + ")");
            }
            else{
                holder.imageIcon.setImageResource(R.drawable.base_folder);
                holder.textView.setText(mCtx.getText(R.string.dot));
            }
        }
        else {
            holder.imageIcon.setVisibility(View.GONE);
            holder.textView.setText(mUtilsPlayListDataInfoStorage.get(arg).getFileName());
        }

        if (mUtilsPlayListDataInfoStorage.get(arg).isFile().equals("true")) {
            holder.imageCheckIcon = (ImageView) arg1.findViewById(R.id.list_item_check_icon);
            holder.imageCheckIcon.setVisibility(View.VISIBLE);
            if (mUtilsPlayListDataInfoStorage.get(arg).isSelected().equals("false")) {
                //holder.imageCheckIcon.setImageResource(R.drawable.uncheck_icon);
            } else {
                holder.imageCheckIcon.setImageResource(R.drawable.check_icon);
            }
        }

        //Utils.DEBUG_LOG("AAA", "arg = " + arg);
        //Utils.DEBUG_LOG("AAA", "selectedIndex = " + selectedIndex);
        if(Utils.SUPPORT_TOUCH) {
            holder.linearlayout = (LinearLayout) arg1.findViewById(R.id.list_item_layout);
            if (selectedIndex != -1 && arg == selectedIndex) {
                holder.linearlayout.setBackgroundColor(0xFF005F8C);
                holder.textView.setSelected(true);
            }
            else {
                holder.linearlayout.setBackgroundColor(0x00000000);
            }
        }

        return arg1;
    }

    static class Holder {
        public LinearLayout linearlayout;
        public ImageView imageIcon;
        public TextView textView;
        public ImageView imageCheckIcon;
    }

}
