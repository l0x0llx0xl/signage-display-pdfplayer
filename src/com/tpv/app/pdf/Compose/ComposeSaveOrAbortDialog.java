package com.tpv.app.pdf.Compose;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
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
 * Created by Andy.Hsu on 2015/8/27.
 */
public class ComposeSaveOrAbortDialog extends Fragment {
    private static final String TAG = ComposeSaveOrAbortDialog.class.getName();
    private Context mCtx;

    private TextView mtextView;

    private Button mBtn_left;
    private Button mBtn_right;
    private int mBtn_focusid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.frag_dialog, null);
        mCtx = vg.getContext();

        mtextView = (TextView)vg.findViewById(R.id.dialog_text);
        String textStr = getString(R.string.saveorabort);
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
            mBtn_left.setText(getString(R.string.save));
            mBtn_left.setOnClickListener(BtnClick);
            mBtn_left.setOnKeyListener(onComposeSaveOrAbortDialogKeyDownListener);
            mBtn_left.requestFocus();
        }

        mBtn_right = (Button) v.findViewById(R.id.dialog_btn_right);
        if (mBtn_right != null) {
            mBtn_right.setText(getString(R.string.abort));
            mBtn_right.setOnClickListener(BtnClick);
            mBtn_right.setOnKeyListener(onComposeSaveOrAbortDialogKeyDownListener);
            //mBtn_right.requestFocus();
        }
    }

    private BtnClickListener BtnClick = new BtnClickListener();
    class BtnClickListener implements View.OnClickListener {
        public void onClick(View v) {
            mBtn_focusid = v.getId();
            switch (mBtn_focusid) {
                case R.id.dialog_btn_left: {
                    Utils.playlistStyle style = Utils.PDFPlayerContentProvider_Get_PlaylistStyle(Utils.mPlayListSelection);
                    if (Utils.playlistStyle.PLAYLIST_NO_DATA == style) {
                        List<Utils_PlayListDataInfo> playlistdatainfoStoreTemp = Utils.getPlayListDataInfoTemp();
                        Utils.PDFPlayerContentProvider_Set_Playlist(Utils.mPlayListSelection, playlistdatainfoStoreTemp);

                        /** check playlist style and store to contentprovider. */
                        style = Utils.checkPlaylistStyle(playlistdatainfoStoreTemp);
                        Utils.PDFPlayerContentProvider_Set_PlaylistStyle(Utils.mPlayListSelection, style);
                        /** ~check playlist style and store to contentprovider. */

                        Utils.Toast(mCtx, getString(R.string.savesuccess));

                        if (Utils.mIsStartFromSettngs) {
                            Utils.getFragmanetManager().beginTransaction()
                                    .replace(R.id.main_container, new ComposeHomeSelectPlaylistFragment(), "ComposeHomeSelectPlaylistFragmentTag")
                                    .addToBackStack(null)
                                    .commit();
                        } else {
                            Utils.getFragmanetManager().beginTransaction()
                                    .replace(R.id.main_container, new HomeFragment(), "HomeFragmentTag")
                                    .addToBackStack(null)
                                    .commit();
                        }
                    } else {
                        Utils.getFragmanetManager().beginTransaction()
                                .replace(R.id.main_container, new ComposeOverwriteDialog(), "ComposeOverwriteDialogTag")
                                .addToBackStack(null)
                                .commit();
                    }
                    break;
                }
                case R.id.dialog_btn_right: {
                    Utils.Toast(mCtx, getString(R.string.abortedit));

                    if (Utils.mIsStartFromSettngs) {
                        Utils.getFragmanetManager().beginTransaction()
                                .replace(R.id.main_container, new ComposeHomeSelectPlaylistFragment(), "ComposeHomeSelectPlaylistFragmentTag")
                                .addToBackStack(null)
                                .commit();
                    } else {
                        Utils.getFragmanetManager().beginTransaction()
                                .replace(R.id.main_container, new HomeFragment(), "HomeFragmentTag")
                                .commit();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }

    private View.OnKeyListener onComposeSaveOrAbortDialogKeyDownListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(event.getAction() == KeyEvent.ACTION_DOWN) {
                if(keyCode == KeyEvent.KEYCODE_BACK
                        || keyCode == KeyEvent.KEYCODE_ESCAPE) {
                    Utils.mIsOptionback = true;
                }
            }
            return false;
        }
    };
}
