package com.tpv.app.pdf.Home;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tpv.app.pdf.Common.Utils;
import com.tpv.app.pdf.Compose.ComposeHomeSelectPlaylistFragment;
import com.tpv.app.pdf.Play.PlayHomeSelectPlaylistFragment;
import com.tpv.app.pdf.R;
import com.tpv.app.pdf.Settings.SettingsHomeFragment;

/**
 * Created by Andy.Hsu on 2015/7/8.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getName();
    private Context mCtx;

    private Button mBtn_play;
    private Button mBtn_compose;
    private Button mBtn_settings;
    private int mBtn_focusid;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Utils.DEBUG_LOG(TAG, "onCreate of HomeFragment");
        super.onCreate(savedInstanceState);

        mBtn_focusid = R.id.btn_play;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //if(Utils.getFragmanetManager() != null) {
        //    Utils.getFragmanetManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        //}
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.frag_home, null);
        mCtx = vg.getContext();

        Utils.DEBUG_LOG(TAG, "[onCreateView] Utils.isDirectPlayback()" + Utils.isDirectPlayback());
        if(Utils.isDirectPlayback()) {
            int listNo = Utils.getDirectPlaybackListNumber();
            Utils.mPlayListSelection = listNo;
            if(Utils.getNormalFinishPdfplayer()) {
                Utils.resetResumeFile();
            }
            if(!Utils.GotoFullScreenPlayback(listNo)) {
                initHomeBtn(vg);
            }
        }
        else {
            initHomeBtn(vg);
        }

        Utils.setNormalFinishPdfplayer("false");

        return vg;
    }

    @Override
    public void onResume() {
        Utils.DEBUG_LOG(TAG, "[onResume]");

        /** reload import text file **/
        Utils.ReadImportFile();
        /** ~reload import text file  **/

        //Utils.mPlayListSelection = -1;
        switch (mBtn_focusid) {
            case R.id.btn_play:
                if(mBtn_play != null) {
                    mBtn_play.requestFocus();
                }
                break;
            case R.id.btn_compose:
                if(mBtn_compose != null) {
                    mBtn_compose.requestFocus();
                }
                break;
            case R.id.btn_settings:
                if(mBtn_settings != null) {
                    mBtn_settings.requestFocus();
                }
                break;
            default:
                if(mBtn_play != null) {
                    mBtn_play.requestFocus();
                }
                break;
        }
        super.onResume();
    }

    private void initHomeBtn(View v) {
        mBtn_play = (Button) v.findViewById(R.id.btn_play);
        if (mBtn_play != null) {
            mBtn_play.setOnClickListener(BtnClick);
        }

        mBtn_compose = (Button) v.findViewById(R.id.btn_compose);
        if (mBtn_compose != null) {
            mBtn_compose.setOnClickListener(BtnClick);
        }

        mBtn_settings = (Button) v.findViewById(R.id.btn_settings);
        if (mBtn_settings != null) {
            mBtn_settings.setOnClickListener(BtnClick);
        }
    }

    private BtnClickListener BtnClick = new BtnClickListener();
    class BtnClickListener implements View.OnClickListener {
        public void onClick(View v) {
            mBtn_focusid = v.getId();
            switch (mBtn_focusid) {
                case R.id.btn_play:
                    Utils.mPlayListSelection = 0;
                    Utils.getFragmanetManager().beginTransaction()
                            .replace(R.id.main_container, new PlayHomeSelectPlaylistFragment(), "PlayHomeSelectPlaylistFragmentTag")
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.btn_compose:
                    Utils.getFragmanetManager().beginTransaction()
                            .replace(R.id.main_container, new ComposeHomeSelectPlaylistFragment(), "ComposeSelectPlaylistLoadFragmentTag")
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.btn_settings:
                    Utils.getFragmanetManager().beginTransaction()
                            .replace(R.id.main_container, new SettingsHomeFragment(), "SettingsHomeFragmentTag")
                            .addToBackStack(null)
                            .commit();
                    break;
                default:
                    break;
            }
        }
    }
}