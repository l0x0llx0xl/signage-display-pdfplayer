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
import com.tpv.app.pdf.R;

import java.util.List;

/**
 * Created by Andy.Hsu on 2015/8/31.
 */
public class ComposeEditOrDeleteDialog extends Fragment {
    private static final String TAG = ComposeEditOrDeleteDialog.class.getName();
    private Context mCtx;

    private int mPlaylistNumber;
    private List<Utils_PlayListDataInfo> mUtilsPlayListDataInfo;

    private TextView mtextView;

    private Button mBtn_left;
    private Button mBtn_right;
    private int mBtn_focusid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.frag_dialog, null);
        mCtx = vg.getContext();

        mPlaylistNumber = Utils.mPlayListSelection;
        mUtilsPlayListDataInfo = Utils.PDFPlayerContentProvider_Get_Playlist(mPlaylistNumber);
        mtextView = (TextView)vg.findViewById(R.id.dialog_text);
        String textStr = getString(R.string.editordelete);
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
            mBtn_left.setText(R.string.edit);
            mBtn_left.setOnClickListener(BtnClick);
            mBtn_left.requestFocus();
        }

        mBtn_right = (Button) v.findViewById(R.id.dialog_btn_right);
        if (mBtn_right != null) {
            mBtn_right.setText(R.string.delete);
            mBtn_right.setOnClickListener(BtnClick);
            //mBtn_right.requestFocus();
        }
    }

    private BtnClickListener BtnClick = new BtnClickListener();
    class BtnClickListener implements View.OnClickListener {
        public void onClick(View v) {
            mBtn_focusid = v.getId();
            switch (mBtn_focusid) {
                case R.id.dialog_btn_left:
                    Utils.getFragmanetManager().beginTransaction()
                            .replace(R.id.main_container, new ComposeEditFragment(), "ComposeEditFragmentTag")
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.dialog_btn_right:
                    mUtilsPlayListDataInfo.clear();
                    Utils.PDFPlayerContentProvider_Set_Playlist(mPlaylistNumber, mUtilsPlayListDataInfo);
                    Utils.PDFPlayerContentProvider_Set_PlaylistStyle(mPlaylistNumber, Utils.playlistStyle.PLAYLIST_NO_DATA);

                    Utils.Toast(mCtx, getString(R.string.deletesuccess));

                    Utils.getFragmanetManager().popBackStack();
                    break;
                default:
                    break;
            }
        }
    }
}
