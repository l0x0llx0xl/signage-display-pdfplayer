package com.tpv.app.pdf.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tpv.app.pdf.Common.Utils_HintBarInfo;
import com.tpv.app.pdf.R;

import java.util.List;

/**
 * Created by Andy.Hsu on 2015/7/22.
 */
public class HintBarAdapter extends BaseAdapter {
    private List<Utils_HintBarInfo> mUtilsHintBarInfo;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private Holder holder = new Holder();
    private int arg;

    public HintBarAdapter(List<Utils_HintBarInfo> hintbarinfo, Context context) {
        this.mUtilsHintBarInfo = hintbarinfo;
        this.mContext = context;
        layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return mUtilsHintBarInfo.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mUtilsHintBarInfo.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        arg = arg0;

        arg1 = layoutInflater.inflate(R.layout.hint_item, null);
        holder.imageView = (ImageView) arg1.findViewById(R.id.hint_item_icon);
        holder.imageView.setImageResource(mUtilsHintBarInfo.get(arg).getIconId());

        holder.textView = (TextView) arg1.findViewById(R.id.hint_item_name);
        holder.textView.setText(mUtilsHintBarInfo.get(arg).getIconText());
        return arg1;
    }

    static class Holder {
        public ImageView imageView;
        public TextView textView;
    }
}
