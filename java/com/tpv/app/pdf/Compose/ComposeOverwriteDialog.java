package com.tpv.app.pdf.Compose;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tpv.app.pdf.Common.Utils_PlayListDataInfo;
import com.tpv.app.pdf.Common.Utils;
import com.tpv.app.pdf.Home.HomeFragment;
import com.tpv.app.pdf.R;

import java.util.List;

/**
 * Created by Andy.Hsu on 2015/7/29.
 */
public class ComposeOverwriteDialog extends Fragment {
    private static final String TAG = ComposeOverwriteDialog.class.getName();
    private Context mCtx;

    private int mPlaylistNumber;
    private List<Utils_PlayListDataInfo> mUtilsPlayListDataInfoStoreTemp;

    private TextView mtextView;

    private Button mBtn_left;
    private Button mBtn_right;
    private int mBtn_focusid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.frag_dialog, null);
        mCtx = vg.getContext();

        mPlaylistNumber = Utils.mPlayListSelection;
        mUtilsPlayListDataInfoStoreTemp = Utils.getPlayListDataInfoTemp();
        mtextView = (TextView)vg.findViewById(R.id.dialog_text);
        String textStr = String.format(getString(R.string.overwritetofile), (mPlaylistNumber+1));
        mtextView.setText(textStr);

        initBtn(vg);

        return vg;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initBtn(View v) {
        mBtn_left = (Button) v.findViewById(R.id.dialog_btn_left);
        if (mBtn_left != null) {
            mBtn_left.setOnClickListener(BtnClick);
            mBtn_left.requestFocus();
        }

        mBtn_right = (Button) v.findViewById(R.id.dialog_btn_right);
        if (mBtn_right != null) {
            mBtn_right.setOnClickListener(BtnClick);
            mBtn_right.requestFocus();
        }
    }

    private BtnClickListener BtnClick = new BtnClickListener();
    class BtnClickListener implements View.OnClickListener {
        public void onClick(View v) {
            mBtn_focusid = v.getId();
            switch (mBtn_focusid) {
                case R.id.dialog_btn_left:
                    Utils.PDFPlayerContentProvider_Set_Playlist(mPlaylistNumber, mUtilsPlayListDataInfoStoreTemp);

                    /** check playlist style and store to contentprovider. */
                    Utils.playlistStyle style = Utils.checkPlaylistStyle(mUtilsPlayListDataInfoStoreTemp);
                    Utils.PDFPlayerContentProvider_Set_PlaylistStyle(mPlaylistNumber, style);
                    /** ~check playlist style and store to contentprovider. */

                    Utils.Toast(mCtx, getString(R.string.savesuccess));
                    break;
                case R.id.dialog_btn_right:
                    break;
                default:
                    break;
            }

            if(Utils.mIsStartFromSettngs) {
                Utils.getFragmanetManager().beginTransaction()
                        .replace(R.id.main_container, new ComposeHomeSelectPlaylistFragment(), "ComposeHomeSelectPlaylistFragmentTag")
                        .addToBackStack(null)
                        .commit();
            }
            else {
                Utils.getFragmanetManager().beginTransaction()
                        .replace(R.id.main_container, new HomeFragment(), "HomeFragmentTag")
                        .addToBackStack(null)
                        .commit();
            }
        }
    }
}
