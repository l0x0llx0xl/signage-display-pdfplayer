package com.tpv.app.pdf.Compose;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tpv.app.pdf.Common.Utils_PlayListDataInfo;
import com.tpv.app.pdf.Adapter.PlayListMenuAdapter;
import com.tpv.app.pdf.Common.Utils_PlayListMenuInfo;
import com.tpv.app.pdf.Common.Utils;
import com.tpv.app.pdf.R;

import java.util.List;

/**
 * Created by Andy.Hsu on 2015/7/29.
 */
public class ComposeHomeSelectPlaylistFragment extends Fragment {
    private static final String TAG = ComposeHomeSelectPlaylistFragment.class.getName();

    private Context mCtx;

    //region play list menu
    private ListView mPlayListLoadMenuView;
    private PlayListMenuAdapter mPlayListLoadMenuAdapter;
    private List<Utils_PlayListMenuInfo> mPlayListLoadMenuInfo;
    //endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Utils.DEBUG_LOG(TAG, "onCreate of ComposeHomeSelectPlaylistFragment");
        super.onCreate(savedInstanceState);
        Utils.mPlayListSelection = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.frag_playlist_menu, null);
        mCtx = vg.getContext();

        //region play list menu
        /** Play List Menu list */
        mPlayListLoadMenuView = (ListView) vg.findViewById(R.id.playlist_menu_list);
        //endregion

        return vg;
    }

    @Override
    public void onResume() {
        Utils.DEBUG_LOG(TAG, "onResume ");
        /** Play List Menu list */
        mPlayListLoadMenuInfo = Utils.addPlayListForLoad();
        mPlayListLoadMenuAdapter = new PlayListMenuAdapter(mPlayListLoadMenuInfo, mCtx);
        mPlayListLoadMenuView.setOnItemClickListener(mPlayListLoadMenuViewItemClickListener);
        mPlayListLoadMenuView.setAdapter(mPlayListLoadMenuAdapter);
        mPlayListLoadMenuView.setSelection(Utils.mPlayListSelection);
        mPlayListLoadMenuView.requestFocus();
        super.onResume();
    }

    private AdapterView.OnItemClickListener mPlayListLoadMenuViewItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent,  //The AdapterView where the click happened
                                View v,                 //The view within the AdapterView that was clicked
                                int position,           //The position of the view in the adapter
                                long id                 //The row id of the item that was clicked
        ){
            Utils.DEBUG_LOG(TAG, "[mPlayListLoadMenuViewItemClickListener] position: " + position);
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
            Log.d(TAG, "playlistdatainfo.size() : " + playlistdatainfo.size());
            if(playlistdatainfo.size() > 0) {
                String filepath = playlistdatainfo.get(0).getFilePath();
                if(filepath.contains(Utils.getInternalStoragePath())) {
                    Utils.PDFPlayerContentProvider_Set_PlaylistStorageSettings(Utils.storageType.STORAGE_INTERNAL);
                }
                else if(filepath.contains(Utils.getUSBStoragePath())) {
                    Utils.PDFPlayerContentProvider_Set_PlaylistStorageSettings(Utils.storageType.STORAGE_USB);
                }
                else if(filepath.contains(Utils.getSDCardStoragePath())) {
                    Utils.PDFPlayerContentProvider_Set_PlaylistStorageSettings(Utils.storageType.STORAGE_SDCARD);
                }
                Utils.getFragmanetManager().beginTransaction()
                        .replace(R.id.main_container, new ComposeEditOrDeleteDialog(), "ComposeEditOrDeleteDialogTag")
                        .addToBackStack(null)
                        .commit();
            }
            else {
                //Utils.Toast(mCtx, getString(R.string.nodata));
                Utils.getFragmanetManager().beginTransaction()
                        .replace(R.id.main_container, new ComposeSelectStorageDialog(), "ComposeSelectStorageDialogTag")
                        .addToBackStack(null)
                        .commit();
            }
        }
    };
}
