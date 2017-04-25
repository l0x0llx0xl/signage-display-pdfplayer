package com.tpv.app.pdf.Play;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tpv.app.pdf.Adapter.PlayListMenuAdapter;
import com.tpv.app.pdf.Common.Utils;
import com.tpv.app.pdf.Common.Utils_PlayListDataInfo;
import com.tpv.app.pdf.Common.Utils_PlayListMenuInfo;
import com.tpv.app.pdf.R;

import java.util.List;

/**
 * Created by Andy.Hsu on 2015/7/8.
 */
public class PlayHomeSelectPlaylistFragment extends Fragment {
    private static final String TAG = PlayHomeSelectPlaylistFragment.class.getName();

    private Context mCtx;

    //region play list menu
    private ListView mPlayListMenuView;
    private PlayListMenuAdapter mPlayListMenuAdapter;
    private List<Utils_PlayListMenuInfo> mUtilsPlayListMenuInfo;
    //endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Utils.DEBUG_LOG(TAG, "onCreate of ComposeNewFragment");
        super.onCreate(savedInstanceState);

        Utils.mPlayListSelection = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.frag_playlist_menu, null);
        mCtx = vg.getContext();

        /** Play List Menu list */
        mUtilsPlayListMenuInfo = Utils.addPlayListForLoad();

        //region play list menu
        /** play list */
        mPlayListMenuView = (ListView) vg.findViewById(R.id.playlist_menu_list);
        mPlayListMenuAdapter = new PlayListMenuAdapter(mUtilsPlayListMenuInfo, mCtx);
        mPlayListMenuView.setOnItemClickListener(mPlayListMenuViewItemClickListener);
        mPlayListMenuView.setAdapter(mPlayListMenuAdapter);
        mPlayListMenuView.requestFocus();
        //endregion

        return vg;
    }

    @Override
    public void onResume() {
        Utils.DEBUG_LOG(TAG, "onResume ");
        mPlayListMenuView.setAdapter(mPlayListMenuAdapter);
        mPlayListMenuView.setSelection(Utils.mPlayListSelection);
        mPlayListMenuView.requestFocus();
        super.onResume();
    }

    private AdapterView.OnItemClickListener mPlayListMenuViewItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent,  //The AdapterView where the click happened
                                View v,                 //The view within the AdapterView that was clicked
                                int position,           //The position of the view in the adapter
                                long id                 //The row id of the item that was clicked
        ){
            Utils.DEBUG_LOG(TAG, "[mPlayListMenuViewItemClickListener] position: " + position);
            Utils.playlistMenu playlistMenuPosition = Utils.playlistMenu.values()[position];
            switch(playlistMenuPosition) {
                case PLAYLIST_1:
                    Utils.mPlayListSelection = Utils.playlistMenu.PLAYLIST_1.ordinal();
                    Utils.DEBUG_LOG(TAG, "PLAYLIST_1 : " + Utils.mPlayListSelection);
                    break;
                case PLAYLIST_2:
                    Utils.mPlayListSelection = Utils.playlistMenu.PLAYLIST_2.ordinal();
                    Utils.DEBUG_LOG(TAG, "PLAYLIST_2 : " + Utils.mPlayListSelection);
                    break;
                case PLAYLIST_3:
                    Utils.mPlayListSelection = Utils.playlistMenu.PLAYLIST_3.ordinal();
                    Utils.DEBUG_LOG(TAG, "PLAYLIST_3 : " + Utils.mPlayListSelection);
                    break;
                case PLAYLIST_4:
                    Utils.mPlayListSelection = Utils.playlistMenu.PLAYLIST_4.ordinal();
                    Utils.DEBUG_LOG(TAG, "PLAYLIST_4 : " + Utils.mPlayListSelection);
                    break;
                case PLAYLIST_5:
                    Utils.mPlayListSelection = Utils.playlistMenu.PLAYLIST_5.ordinal();
                    Utils.DEBUG_LOG(TAG, "PLAYLIST_5 : " + Utils.mPlayListSelection);
                    break;
                case PLAYLIST_6:
                    Utils.mPlayListSelection = Utils.playlistMenu.PLAYLIST_6.ordinal();
                    Utils.DEBUG_LOG(TAG, "PLAYLIST_6 : " + Utils.mPlayListSelection);
                    break;
                case PLAYLIST_7:
                    Utils.mPlayListSelection = Utils.playlistMenu.PLAYLIST_7.ordinal();
                    Utils.DEBUG_LOG(TAG, "PLAYLIST_7 : " + Utils.mPlayListSelection);
                    break;
            }

            List<Utils_PlayListDataInfo> playlistdatainfo = Utils.PDFPlayerContentProvider_Get_Playlist(Utils.mPlayListSelection);
            Utils.DEBUG_LOG(TAG, "playlistdatainfo.size() : " + playlistdatainfo.size());
            if(playlistdatainfo.size() > 0) {
                Utils.resetResumeFile();
                Utils.GotoFullScreenPlayback(Utils.mPlayListSelection);
            }
            else {
                Utils.Toast(mCtx, getString(R.string.nodatainplaylist));

                /** for test. */
                //Utils.GotoFullScreenPlayback(Utils.mPlayListSelection);
                /** ~for test. */
            }
        }
    };
}
