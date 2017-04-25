package com.tpv.app.pdf.Compose;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tpv.app.pdf.Adapter.ComposeEditPlaylistDataInfoAdapter;
import com.tpv.app.pdf.Adapter.ComposeEditPlaylistDataInfoTempAdapter;
import com.tpv.app.pdf.Adapter.HintBarAdapter;
import com.tpv.app.pdf.Common.Utils_HintBarInfo;
import com.tpv.app.pdf.Common.Utils_PlayListDataInfo;
import com.tpv.app.pdf.Common.Utils;
import com.tpv.app.pdf.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andy.Hsu on 2015/7/20.
 */
public class ComposeEditFragment extends Fragment {
    private static final String TAG = ComposeEditFragment.class.getName();

    private Context mCtx;

    //region Right list
    private ListView mStorageListView;
    private ComposeEditPlaylistDataInfoAdapter mComposeEditPlaylistDataInfoAdapter;
    private List<Utils_PlayListDataInfo> mUtilsPlayListDataInfoStorage;
    private int mStorageListPage;
    private int mStorageListItemPosition;
    private int mStorageListFirstItemPosition;
    private int mStorageListVisibleItemCount;
    private int mStorageListItemHeight;

    //endregion

    //region Center list
    private ListView mTempListView;
    private ComposeEditPlaylistDataInfoTempAdapter mComposeEditPlaylistDataInfoTempAdapter;
    private List<Utils_PlayListDataInfo> mUtilsPlayListDataInfoTemp;
    private int mTempListItemPosition;
    private int mTempListFirstItemPosition;
    private int mTempListVisibleItemCount;
    private int mTempListItemHeight;
    //endregion

    //region Hint list
    public enum hintItem{
        HINT_SELECT_ALL,
        HINT_DELETE_ALL,
        HINT_UPDATE,
        HINT_SORT,
        HINT_SAVE,
        //HINT_PLAY,
        //HINT_INFO
        HINT_BACK
    }

    /** Animation*/
    private FrameLayout mHintFrameLayout;
    private Animation mHintFrameLayoutShowAnimation;
    private Animation mHintFrameLayoutHideAnimation;
    private static boolean hasAnimation = true;

    private GridView mLeftHintListView;
    private HintBarAdapter mLeftHintBarAdapter;
    private List<Utils_HintBarInfo> mLeftUtilsHintBarInfo;
    private int mHintBarSelection;

    //private ImageView mHintSelectImage;
    //private TextView mHintSelectText;
    private ImageView mHintToolbarImage;
    private TextView mHintToolbarText;
    //endregion

    private static boolean mIsLongClick = false;
    private static boolean mIsDpadEnterPress = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Utils.DEBUG_LOG(TAG, "onCreate of ComposeEditFragment");
        super.onCreate(savedInstanceState);
        mHintBarSelection = 0;
        Utils.mIsPlayOrInfoClick = false;
        Utils.mIsOptionsClick = false;

        /** Temp file list */
        {
            if (Utils.mPlayListSelection == -1) {
                mUtilsPlayListDataInfoTemp = new ArrayList<Utils_PlayListDataInfo>();
            } else {
                mUtilsPlayListDataInfoTemp = Utils.PDFPlayerContentProvider_Get_Playlist(Utils.mPlayListSelection);
                //UpdateStorageDataToTempList();
            }
        }
        Utils.setPlayListDataInfoTemp(mUtilsPlayListDataInfoTemp);

        /** Storage file list */
        mUtilsPlayListDataInfoStorage = new ArrayList<Utils_PlayListDataInfo>();
        addFolderListToStorageList();
        //addStorageDataToStorageList();


        /** Hint file list */
        mLeftUtilsHintBarInfo = new ArrayList<Utils_HintBarInfo>();
        addLeftHintBarDataToHintList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Utils.DEBUG_LOG(TAG, "onCreateView of ComposeEditFragment");

        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.frag_compose_edit, null);
        mCtx = vg.getContext();

        //TextView textView = (TextView) vg.findViewById(R.id.compose_edit_left_text);
        //textView.setTypeface(Utils.mTypeface1);
        //textView.setText(getString(R.string.compose));

        //region Right list
        /** Storage file list */
        mStorageListView = (ListView) vg.findViewById(R.id.compose_edit_right_list);
        mComposeEditPlaylistDataInfoAdapter = new ComposeEditPlaylistDataInfoAdapter(mUtilsPlayListDataInfoStorage, mCtx);
        mStorageListView.setOnItemClickListener(mStorageListViewItemClickListener);
        mStorageListView.setOnScrollListener(mStorageListViewItemScrollListener);
        mStorageListView.setOnKeyListener(onComposeEditFragmentKeyDownListener);
        mStorageListView.setAdapter(mComposeEditPlaylistDataInfoAdapter);
        mStorageListView.requestFocus();
        if(Utils.SUPPORT_TOUCH) {
            mStorageListView.setOnItemLongClickListener(mStorageListViewItemLongClickListener);
            mStorageListView.setFocusableInTouchMode(true);
            mStorageListView.setActivated(true);
        }
        //endregion

        //region Center list
        /** Temp file list */
        mTempListView = (ListView) vg.findViewById(R.id.compose_edit_center_list);
        mComposeEditPlaylistDataInfoTempAdapter = new ComposeEditPlaylistDataInfoTempAdapter(mUtilsPlayListDataInfoTemp, mCtx);
        mTempListView.setOnItemClickListener(mTempListViewItemClickListener);
        mTempListView.setOnScrollListener(mTempListViewItemScrollListener);
        mTempListView.setOnKeyListener(onComposeEditFragmentKeyDownListener);
        mTempListView.setAdapter(mComposeEditPlaylistDataInfoTempAdapter);

        if(Utils.SUPPORT_TOUCH) {
            mTempListView.setFocusableInTouchMode(true);
        }
        //endregion

        //region left Hint list
        /** Hint file list */
        {/** Animation */
            mHintFrameLayout = (FrameLayout) vg.findViewById(R.id.compose_hint_left_frame);
            Utils.sDm = this.getResources().getDisplayMetrics();
            if(hasAnimation) {
                //Set animation path(start_X1, to_X2, start_Y1, to_Y2)
                mHintFrameLayoutShowAnimation = new TranslateAnimation(-(mHintFrameLayout.getWidth()), 0, 0, 0);
                mHintFrameLayoutHideAnimation = new TranslateAnimation(0, -(mHintFrameLayout.getWidth()), 0, 0);
                //Set animation duration
                mHintFrameLayoutShowAnimation.setDuration(500);
                mHintFrameLayoutHideAnimation.setDuration(500);
                //repeat mode(-1:always repeat, 0:play once)
                mHintFrameLayoutShowAnimation.setRepeatCount(0);
                mHintFrameLayoutHideAnimation.setRepeatCount(0);
            }
            mHintFrameLayout.setVisibility(View.GONE);
        }

        mLeftHintListView = (GridView) vg.findViewById(R.id.compose_edit_left_hint_list);
        mLeftHintBarAdapter = new HintBarAdapter(mLeftUtilsHintBarInfo, mCtx);
        mLeftHintListView.setOnItemClickListener(mLeftHintBarListViewItemClickListener);
        mLeftHintListView.setOnScrollListener(mLeftHintBarViewItemScrollListener);
        mLeftHintListView.setOnKeyListener(onComposeEditFragmentKeyDownListener);
        //mLeftHintListView.setAdapter(mLeftHintBarAdapter);

        if(Utils.SUPPORT_TOUCH) {
            //hint select
            //mHintSelectImage = (ImageView) vg.findViewById(R.id.compose_edit_list_down_select_icon);
            //mHintSelectText = (TextView ) vg.findViewById(R.id.compose_edit_list_down_select_text);


            //hint toolbar
            mHintToolbarImage = (ImageView) vg.findViewById(R.id.compose_edit_list_down_toolbar_icon);
            mHintToolbarImage.setOnClickListener(mOnHintToolbarClick);
            mHintToolbarText = (TextView) vg.findViewById(R.id.compose_edit_list_down_toolbar_text);
            mHintToolbarText.setOnClickListener(mOnHintToolbarClick);
            //endregion
        }

        return vg;
    }
    private View.OnClickListener mOnHintToolbarClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            Utils.DEBUG_LOG(TAG, "Toolbar click");
            if(!mLeftHintListView.isFocused()) {
                HintBarShowOrHide(true, 0);
            }
        }
    };

    @Override
    public void onResume() {
        Utils.DEBUG_LOG(TAG, "onResume of ComposeNewFragment");

        /* highlight list item when go to hintbar
        Utils.mIsOptionsClick = false;
        */

        mStorageListView.setFocusable(true);
        if(mUtilsPlayListDataInfoStorage != null) {
            mComposeEditPlaylistDataInfoAdapter = new ComposeEditPlaylistDataInfoAdapter(mUtilsPlayListDataInfoStorage, mCtx);
            mStorageListView.setAdapter(mComposeEditPlaylistDataInfoAdapter);
        }

        mTempListView.setFocusable(true);
        mUtilsPlayListDataInfoTemp = Utils.getPlayListDataInfoTemp();
        if(mUtilsPlayListDataInfoTemp != null) {
            mComposeEditPlaylistDataInfoTempAdapter = new ComposeEditPlaylistDataInfoTempAdapter(mUtilsPlayListDataInfoTemp, mCtx);
            mTempListView.setAdapter(mComposeEditPlaylistDataInfoTempAdapter);
        }

        int scrollY;
        if(Utils.mIsPlayOrInfoClick || Utils.mIsOptionsClick) {
            //Utils.DEBUG_LOG(TAG, "[onResume] Utils.mFromListType: " + Utils.mFromListType);
            //Utils.DEBUG_LOG(TAG, "[onResume] mStorageListItemPosition: " + mStorageListItemPosition);
            //Utils.DEBUG_LOG(TAG, "[onResume] mStorageListFirstItemPosition: " + mStorageListFirstItemPosition);
            //Utils.DEBUG_LOG(TAG, "[onResume] mStorageListItemHeight: " + mStorageListItemHeight);
            //Utils.DEBUG_LOG(TAG, "[onResume] mUtilsPlayListDataInfoStorage.size(): " + mUtilsPlayListDataInfoStorage.size());
            if (Utils.listtype.STORAGE_LIST == Utils.mFromListType) {
                mStorageListView.requestFocus();
                if(mStorageListItemPosition == (mUtilsPlayListDataInfoStorage.size()-1)) {
                    mStorageListView.setSelection(mStorageListItemPosition);
                }
                else {
                    scrollY = (mStorageListItemPosition - mStorageListFirstItemPosition) * mStorageListItemHeight;
                    mStorageListView.setSelectionFromTop(mStorageListItemPosition, scrollY);
                }
                mComposeEditPlaylistDataInfoAdapter.notifyDataSetChanged();
            } else if (Utils.listtype.TEMP_LIST == Utils.mFromListType) {
                mTempListView.requestFocus();
                if(mTempListItemPosition == (mUtilsPlayListDataInfoTemp.size()-1)) {
                    mTempListView.setSelection(mTempListItemPosition);
                }
                else {
                    scrollY = (mTempListItemPosition - mTempListFirstItemPosition) * mTempListItemHeight;
                    mTempListView.setSelectionFromTop(mTempListItemPosition, scrollY);
                }
                mComposeEditPlaylistDataInfoTempAdapter.notifyDataSetChanged();
            }
        }
        Utils.mIsPlayOrInfoClick = false;
        Utils.mIsOptionsClick = false;

        Utils.DEBUG_LOG(TAG, "mHintBarSelection : " + mHintBarSelection);
        mLeftHintListView.setAdapter(mLeftHintBarAdapter);
        mLeftHintListView.setSelection(mHintBarSelection);
        //mLeftHintListView.requestFocus();
        //mLeftHintListView.setVisibility(View.GONE);

        if(Utils.mIsOptionback)
        {
            Utils.mIsOptionback = false;
            HintBarShowOrHide(true, mHintBarSelection);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        Utils.DEBUG_LOG(TAG, "OnPause of ComposeNewFragment");
        /* highlight list item when go to hintbar
        Utils.mIsOptionsClick = false;
        */

        super.onPause();
    }

    private AdapterView.OnItemLongClickListener mStorageListViewItemLongClickListener = new AdapterView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            if(Utils.SUPPORT_TOUCH) {
                if (mUtilsPlayListDataInfoStorage.get(position).isFile().equals("false"))
                {
                    mIsLongClick = true;
                    mComposeEditPlaylistDataInfoAdapter.setSelectedIndex(position);
                    mComposeEditPlaylistDataInfoAdapter.notifyDataSetInvalidated();
                }
            }

            return false;
        }
    };

    private AdapterView.OnItemClickListener mStorageListViewItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent,  //The AdapterView where the click happened
                                View v,                 //The view within the AdapterView that was clicked
                                int position,           //The position of the view in the adapter
                                long id                 //The row id of the item that was clicked
        ){
            Utils.DEBUG_LOG(TAG, "[mStorageListViewItemClickListener] position: " + position);

            if(Utils.SUPPORT_TOUCH && mIsDpadEnterPress == false) {
                mStorageListItemPosition = position;
                if (mLeftHintListView.isFocused()) {
                    HintBarShowOrHide(false, 0);
                }
            }

            if(mUtilsPlayListDataInfoStorage.get(position).isFile().equals("true")) {
                ImageView iv = (ImageView) v.findViewById(R.id.list_item_check_icon);
                if (mUtilsPlayListDataInfoStorage.get(position).isSelected().equals("true")) {
                    mUtilsPlayListDataInfoStorage.get(position).setSelected("false");
                    iv.setImageResource(0);
                } else {
                    mUtilsPlayListDataInfoStorage.get(position).setSelected("true");
                    iv.setImageResource(R.drawable.check_icon);
                }
                if(Utils.SUPPORT_TOUCH && mIsDpadEnterPress == false) {
                    if (mComposeEditPlaylistDataInfoTempAdapter.getSelectedIndex() != -1) {
                        mComposeEditPlaylistDataInfoTempAdapter.setSelectedIndex(-1);
                        mComposeEditPlaylistDataInfoTempAdapter.notifyDataSetInvalidated();
                    }
                    if (mComposeEditPlaylistDataInfoAdapter.getSelectedIndex() != position) {
                        mComposeEditPlaylistDataInfoAdapter.setSelectedIndex(position);
                        mComposeEditPlaylistDataInfoAdapter.notifyDataSetInvalidated();
                    }
                }
            }
            else {
                boolean hasData = false;
                if(mUtilsPlayListDataInfoStorage.get(position).getFileName().equals(getString(R.string.dot))) {
                    if (mStorageListPage > 0) {
                        mStorageListPage--;
                        if (mStorageListPage == 0) {
                            addFolderListToStorageList();
                        }
                        mComposeEditPlaylistDataInfoAdapter = new ComposeEditPlaylistDataInfoAdapter(mUtilsPlayListDataInfoStorage, mCtx);
                        mStorageListView.setAdapter(mComposeEditPlaylistDataInfoAdapter);
                    }
                }
                else {
                    if(mIsLongClick == false) {
	                    if (mUtilsPlayListDataInfoStorage.get(position).getFileName().equals(Utils.FILE_FOLDER_PDF)) {
	                        Utils.DEBUG_LOG(TAG, "[mStorageListViewItemClickListener] Pdf Folder: " + position);
	                        hasData = addStorageDataToStorageList(Utils.FILE_FOLDER_PDF);
                        }

                        if (hasData) {
                            mComposeEditPlaylistDataInfoAdapter = new ComposeEditPlaylistDataInfoAdapter(mUtilsPlayListDataInfoStorage, mCtx);
                            mStorageListView.setAdapter(mComposeEditPlaylistDataInfoAdapter);
                            mStorageListPage++;
                        }
                    }

                    if (Utils.SUPPORT_TOUCH && mIsDpadEnterPress == false) {
                        if (mComposeEditPlaylistDataInfoTempAdapter.getSelectedIndex() != -1) {
                            mComposeEditPlaylistDataInfoTempAdapter.setSelectedIndex(-1);
                            mComposeEditPlaylistDataInfoTempAdapter.notifyDataSetInvalidated();
                        }
                        if (mComposeEditPlaylistDataInfoAdapter.getSelectedIndex() != position) {
                            mComposeEditPlaylistDataInfoAdapter.setSelectedIndex(position);
                            mComposeEditPlaylistDataInfoAdapter.notifyDataSetInvalidated();
                        }
                    }
                    mIsLongClick = false;
                }
            }
            mIsDpadEnterPress = false;
        }
    };

    private AbsListView.OnScrollListener mStorageListViewItemScrollListener = new AbsListView.OnScrollListener(){
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                    break;
            }
        }
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            // TODO Auto-generated method stub
            //Utils.DEBUG_LOG(TAG, "[mStorageListViewItemScrollListener] view.getSelectedItem() : " + view.getSelectedItem());
            //Utils.DEBUG_LOG(TAG, "[mStorageListViewItemScrollListener] view.getSelectedItemPosition() : " + view.getSelectedItemPosition());
            //Utils.DEBUG_LOG(TAG, "[mStorageListViewItemScrollListener] mStorageListView.isFocused() : " + mStorageListView.isFocused());
            //Utils.DEBUG_LOG(TAG, "[mStorageListViewItemScrollListener] firstVisibleItem : " + firstVisibleItem);
            //Utils.DEBUG_LOG(TAG, "[mStorageListViewItemScrollListener] visibleItemCount : " + visibleItemCount);

            //Utils.DEBUG_LOG(TAG, "[mStorageListViewItemScrollListener] Utils.mIsPlayOrInfoClick : " + Utils.mIsPlayOrInfoClick);
            //Utils.DEBUG_LOG(TAG, "[mStorageListViewItemScrollListener] Utils.mIsOptionsClick : " + Utils.mIsOptionsClick);
            if(!Utils.mIsPlayOrInfoClick && !Utils.mIsOptionsClick) {
                mStorageListFirstItemPosition = firstVisibleItem;
            }
            mStorageListVisibleItemCount = visibleItemCount;

            if (view.getSelectedItem() != null) {
                //Utils.DEBUG_LOG(TAG, "[mStorageListViewItemScrollListener] view.getMeasuredHeight() : " + view.getMeasuredHeight());
                //Utils.DEBUG_LOG(TAG, "[mStorageListViewItemScrollListener] view.getCount() : " + view.getCount());

                if(mStorageListView.isFocused()) {
                    if(view.getChildAt(0) != null) {
                        //Utils.DEBUG_LOG(TAG, "[mStorageListViewItemScrollListener] view.getChildAt(0).getMeasuredHeight() : " + view.getChildAt(0).getMeasuredHeight());
                        mStorageListItemHeight = view.getChildAt(0).getMeasuredHeight();
                    }
                    //Utils_PlayListDataInfo item = (Utils_PlayListDataInfo) view.getSelectedItem();
                    mStorageListItemPosition = view.getSelectedItemPosition();
                    //Utils.DEBUG_LOG(TAG, "[mStorageListViewItemScrollListener] mStorageListItemPosition : " + mStorageListItemPosition);
                    Utils.mListIndex = mStorageListItemPosition;
                }
            }
        }
    };

    private AdapterView.OnItemClickListener mTempListViewItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent,  //The AdapterView where the click happened
                                View v,                 //The view within the AdapterView that was clicked
                                int position,           //The position of the view in the adapter
                                long id                 //The row id of the item that was clicked
        ){
            Utils.DEBUG_LOG(TAG, "[mTempListViewItemClickListener] position: " + position);
            if(Utils.SUPPORT_TOUCH && mIsDpadEnterPress == false) {
                mTempListItemPosition = position;
                if (mLeftHintListView.isFocused()) {
                    HintBarShowOrHide(false, 0);
                }
                if (mComposeEditPlaylistDataInfoAdapter.getSelectedIndex() != -1) {
                    mComposeEditPlaylistDataInfoAdapter.setSelectedIndex(-1);
                    mComposeEditPlaylistDataInfoAdapter.notifyDataSetInvalidated();
                }
                if (mComposeEditPlaylistDataInfoTempAdapter.getSelectedIndex() != position) {
                    mComposeEditPlaylistDataInfoTempAdapter.setSelectedIndex(position);
                    mComposeEditPlaylistDataInfoTempAdapter.notifyDataSetInvalidated();
                }
            }
            mIsDpadEnterPress = false;
        }
    };

    private AbsListView.OnScrollListener mTempListViewItemScrollListener = new AbsListView.OnScrollListener(){
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                    break;
            }
        }
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            // TODO Auto-generated method stub
            //Utils.DEBUG_LOG(TAG, "[mTempListViewItemScrollListener] view.getSelectedItem() : " + view.getSelectedItem());
            //Utils.DEBUG_LOG(TAG, "[mTempListViewItemScrollListener] view.getSelectedItemPosition() : " + view.getSelectedItemPosition());
            //Utils.DEBUG_LOG(TAG, "[mTempListViewItemScrollListener] mTempListView.isFocused() : " + mTempListView.isFocused());
            //Utils.DEBUG_LOG(TAG, "[mTempListViewItemScrollListener] firstVisibleItem : " + firstVisibleItem);
            //Utils.DEBUG_LOG(TAG, "[mTempListViewItemScrollListener] visibleItemCount : " + visibleItemCount);

            if(!Utils.mIsPlayOrInfoClick && !Utils.mIsOptionsClick) {
                mTempListFirstItemPosition = firstVisibleItem;
            }
            mTempListVisibleItemCount = visibleItemCount;

            if (view.getSelectedItem() != null) {
                //Utils.DEBUG_LOG(TAG, "[mTempListViewItemScrollListener] view.getMeasuredHeight() : " + view.getMeasuredHeight());
                //Utils.DEBUG_LOG(TAG, "[mTempListViewItemScrollListener] view.getCount() : " + view.getCount());

                if(mTempListView.isFocused()) {
                    if(view.getChildAt(0) != null) {
                        ///Utils.DEBUG_LOG(TAG, "[mTempListViewItemScrollListener] view.getChildAt(0).getMeasuredHeight() : " + view.getChildAt(0).getMeasuredHeight());
                        mTempListItemHeight = view.getChildAt(0).getMeasuredHeight();
                    }
                    //Utils_PlayListDataInfo item = (Utils_PlayListDataInfo) view.getSelectedItem();
                    mTempListItemPosition = view.getSelectedItemPosition();
                    //Utils.DEBUG_LOG(TAG, "[mTempListViewItemScrollListener] mTempListItemPosition : " + mTempListItemPosition);
                    Utils.mListIndex = mTempListItemPosition;
                }
            }
        }
    };

    private AbsListView.OnScrollListener mLeftHintBarViewItemScrollListener = new AbsListView.OnScrollListener(){
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                    break;
            }
        }
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            // TODO Auto-generated method stub
            Utils.DEBUG_LOG(TAG, "[mLeftHintBarViewItemScrollListener] view.getSelectedItem() : " + view.getSelectedItem());

            if (view.getSelectedItem() != null) {
                if(!mLeftHintListView.isFocused()) {
                    HintBarShowOrHide(false, 0);
                }
            }
        }
    };

    private AdapterView.OnItemClickListener mLeftHintBarListViewItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent,  //The AdapterView where the click happened
                                View v,                 //The view within the AdapterView that was clicked
                                int position,           //The position of the view in the adapter
                                long id                 //The row id of the item that was clicked
        ){
            Utils.DEBUG_LOG(TAG, "[mLeftHintBarListViewItemClickListener] position: " + position);
            hintItem hintItemPosition = hintItem.values()[position];
            Utils.mIsOptionsClick = true;
            switch(hintItemPosition) {
                case HINT_SELECT_ALL:
                    mHintBarSelection = hintItem.HINT_SELECT_ALL.ordinal();
                    Utils.DEBUG_LOG(TAG, "HINT_SELECT_ALL : " + mHintBarSelection);

                    SelectAllStorageData();

                    break;
                case HINT_UPDATE:
                    mHintBarSelection = hintItem.HINT_UPDATE.ordinal();
                    Utils.DEBUG_LOG(TAG, "HINT_UPDATE : " + mHintBarSelection);
                    int selectCount = 0;
                    if(mUtilsPlayListDataInfoStorage.size() > 0) {
                        /** Check mUtilsPlayListDataInfoStorage select item is already at mUtilsPlayListDataInfoTemp or not */
                        for (Utils_PlayListDataInfo item : mUtilsPlayListDataInfoStorage) {
                            if (item.isFile().equals("true")) {
                                if (item.isSelected().equals("true")) {
                                    selectCount++;
                                }
                            }
                        }
                    }
                    if(selectCount > Utils.MAX_FILES) {
                        Utils.Toast(mCtx, getString(R.string.overfilesselected));
                    }
                    else {
                        UpdateStorageDataToTempList();
                    }
                    break;
                case HINT_DELETE_ALL:
                    mHintBarSelection = hintItem.HINT_DELETE_ALL.ordinal();
                    Utils.DEBUG_LOG(TAG, "HINT_DELETE_ALL : " + mHintBarSelection);

                    DeleteAllStorageDataSelected();

                    mUtilsPlayListDataInfoTemp.clear();
                    mComposeEditPlaylistDataInfoTempAdapter = new ComposeEditPlaylistDataInfoTempAdapter(mUtilsPlayListDataInfoTemp, mCtx);
                    mTempListView.setAdapter(mComposeEditPlaylistDataInfoTempAdapter);
                    Utils.setPlayListDataInfoTemp(mUtilsPlayListDataInfoTemp);
                    break;
                case HINT_SORT:
                    mHintBarSelection = hintItem.HINT_SORT.ordinal();
                    Utils.DEBUG_LOG(TAG, "HINT_SORT : " + mHintBarSelection);

                    if(mUtilsPlayListDataInfoTemp.size() > 0) {
                        //Utils.mIsOptionsClick = true;

                        Utils.setPlayListDataInfoTemp(mUtilsPlayListDataInfoTemp);

                        Utils.getFragmanetManager().beginTransaction()
                                .replace(R.id.main_container, new ComposeSortFragment(), "ComposeSortFragmentTag")
                                .addToBackStack(null)
                                .commit();
                    }
                    else {
                        Utils.Toast(mCtx, getString(R.string.nodatainplaylist));
                    }
                    break;
                case HINT_SAVE:
                    mHintBarSelection = hintItem.HINT_SAVE.ordinal();
                    Utils.DEBUG_LOG(TAG, "HINT_SAVE : " + mHintBarSelection);
                    if(mUtilsPlayListDataInfoTemp.size() > 0) {
                        //Utils.mIsOptionsClick = true;

                        Utils.setPlayListDataInfoTemp(mUtilsPlayListDataInfoTemp);

                        Utils.getFragmanetManager().beginTransaction()
                                .replace(R.id.main_container, new ComposeSaveOrAbortDialog(), "ComposeSaveOrAbortDialogTag")
                                .addToBackStack(null)
                                .commit();
                    }
                    else {
                        Utils.Toast(mCtx, getString(R.string.nodatainplaylist));
                    }
                    break;
                case HINT_BACK: {
                    if (mLeftHintListView.isFocused()) {
                        HintBarShowOrHide(false, 0);
                    }
                }
                break;
            }
        }
    };

    private View.OnKeyListener onComposeEditFragmentKeyDownListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            Utils.DEBUG_LOG(TAG, "event.getAction() : " + event.getAction());
            Utils.DEBUG_LOG(TAG, "keyCode : " + keyCode);

            if(event.getAction() == KeyEvent.ACTION_DOWN) {
                switch(keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                    case KeyEvent.KEYCODE_ESCAPE: {
                        Utils.DEBUG_LOG(TAG, "KEYCODE_BACK");
                        if(mLeftHintListView.isFocused()) {
                            HintBarShowOrHide(false, 0);
                            return true;
                        }
                        else {
                            if (mStorageListPage > 0) {
                                mStorageListPage--;
                                if (mStorageListPage == 0) {
                                    addFolderListToStorageList();
                                }
                                mComposeEditPlaylistDataInfoAdapter = new ComposeEditPlaylistDataInfoAdapter(mUtilsPlayListDataInfoStorage, mCtx);
                                mStorageListView.setAdapter(mComposeEditPlaylistDataInfoAdapter);
                                return true;
                            }
                        }
                    }
                    break;

                    case KeyEvent.KEYCODE_GUIDE:
                    case KeyEvent.KEYCODE_SPACE: {
                        Utils.DEBUG_LOG(TAG, "Options");
                        if(!mLeftHintListView.isFocused()) {
                            Utils.DEBUG_LOG(TAG, "[onKey] mStorageListView.isFocused() : " + mStorageListView.isFocused());
                            Utils.DEBUG_LOG(TAG, "[onKey] mStorageListItemPosition : " + mStorageListItemPosition);
                            Utils.DEBUG_LOG(TAG, "[onKey] mTempListView.isFocused() : " + mTempListView.isFocused());
                            Utils.DEBUG_LOG(TAG, "[onKey] mTempListItemPosition : " + mTempListItemPosition);
                            Utils.DEBUG_LOG(TAG, "[onKey] before mStorageListFirstItemPosition : " + mStorageListFirstItemPosition);
                            HintBarShowOrHide(true, 0);
                            Utils.DEBUG_LOG(TAG, "[onKey] after mStorageListFirstItemPosition : " + mStorageListFirstItemPosition);
                            return true;
                        }
                    }
                    break;

                    case KeyEvent.KEYCODE_DPAD_RIGHT: {
                        if(mLeftHintListView.isFocused()) {
                            HintBarShowOrHide(false, 0);
                            return true;
                        }
                    }
                    break;
                    case KeyEvent.KEYCODE_ENTER:
                    case KeyEvent.KEYCODE_DPAD_CENTER:{
                        Utils.DEBUG_LOG(TAG, "KEYCODE_DPAD_CENTER");

                        if(mStorageListView.isFocused() || mTempListView.isFocused()) {
                            mIsDpadEnterPress = true;
                        }
                        else {
                            mIsDpadEnterPress = false;
                        }
                    }
                    break;
                }
            }
            return false;
        }
    };

    private void addFolderListToStorageList() {
        if(mUtilsPlayListDataInfoStorage != null) {
            mUtilsPlayListDataInfoStorage.clear();
        }
        mStorageListPage = 0;

        Utils_PlayListDataInfo playlistdatainfo;

        playlistdatainfo =
                new Utils_PlayListDataInfo(Utils.FILE_FOLDER_PDF,
                        "filePath",
                        "false",
                        "false",
                        "false");
        mUtilsPlayListDataInfoStorage.add(playlistdatainfo);

    }

    private boolean addStorageDataToStorageList(String foldername) {

        boolean hasData = false;

        //Utils.storageType storagetype = Utils.storageType.values()[0];
        Utils.storageType storagetype = Utils.PDFPlayerContentProvider_Get_PlaylistStorageSettings();
        String storagepath = Utils.getInternalStoragePath();
        switch(storagetype) {
            case STORAGE_INTERNAL:
                storagepath = Utils.getInternalStoragePath()  + foldername;
                break;
            case STORAGE_USB:
                storagepath = Utils.getUSBStoragePath()  + foldername;
                break;
            case STORAGE_SDCARD:
                storagepath = Utils.getSDCardStoragePath()  + foldername;
                break;
        }

        Utils.fileType filetype = Utils.fileType.NONE;
        if(Utils.FILE_FOLDER_PDF.equals(foldername)){
            filetype = Utils.fileType.FILE_PDF;
        }

        List<Utils_PlayListDataInfo> storageplaylistdatainfo = Utils.getStorageFiles(storagepath, filetype);
        if(storageplaylistdatainfo.size() != 0) {
            mUtilsPlayListDataInfoStorage.clear();
            mUtilsPlayListDataInfoStorage = Utils.getStorageFiles(storagepath, filetype);

            comparePlayListAndStorageData();
            hasData = true;
        }
        else {
            Utils.Toast(mCtx, getString(R.string.nodatainfolder));
        }
        return hasData;
    }

    /** compare mUtilsPlayListDataInfoStorage and mUtilsPlayListDataInfoTemp items, the same item will set selected. */
    private void comparePlayListAndStorageData() {
        mUtilsPlayListDataInfoTemp = Utils.getPlayListDataInfoTemp();
        if(mUtilsPlayListDataInfoStorage != null && mUtilsPlayListDataInfoTemp != null) {
            if (mUtilsPlayListDataInfoTemp.size() > 0) {
                for (Utils_PlayListDataInfo itemTemp : mUtilsPlayListDataInfoTemp) {
                    for (int i = 0; i < mUtilsPlayListDataInfoStorage.size(); i++) {
                        if (itemTemp.getFileName().equals(mUtilsPlayListDataInfoStorage.get(i).getFileName())) {
                            mUtilsPlayListDataInfoStorage.get(i).setSelected("true");
                        }
                    }
                }
            }
        }
    }

    private void SelectAllStorageData() {
        if (mUtilsPlayListDataInfoStorage.size() > 0) {
            //Utils.mIsOptionsClick = true;
            for (int i=0; i< mUtilsPlayListDataInfoStorage.size(); i++) {
                if (mUtilsPlayListDataInfoStorage.get(i).isFile().equals("true")
                        && mUtilsPlayListDataInfoStorage.get(i).isSelected().equals("false"))
                    mUtilsPlayListDataInfoStorage.get(i).setSelected("true");
            }

            mComposeEditPlaylistDataInfoAdapter = new ComposeEditPlaylistDataInfoAdapter(mUtilsPlayListDataInfoStorage, mCtx);
            mStorageListView.setAdapter(mComposeEditPlaylistDataInfoAdapter);

            Utils.DEBUG_LOG(TAG, "[SelectAllStorageData] mStorageListItemPosition : " + mStorageListItemPosition);
            Utils.DEBUG_LOG(TAG, "[SelectAllStorageData] mUtilsPlayListDataInfoStorage.size() : " + mUtilsPlayListDataInfoStorage.size());
            Utils.DEBUG_LOG(TAG, "[SelectAllStorageData] mStorageListFirstItemPosition : " + mStorageListFirstItemPosition);
            Utils.DEBUG_LOG(TAG, "[SelectAllStorageData] mStorageListItemHeight : " + mStorageListItemHeight);
            if(mStorageListItemPosition == (mUtilsPlayListDataInfoStorage.size()-1)) {
                mStorageListView.setSelection(mStorageListItemPosition);
            }
            else {
                int scrollY = (mStorageListItemPosition - mStorageListFirstItemPosition) * mStorageListItemHeight;
                mStorageListView.setSelectionFromTop(mStorageListItemPosition, scrollY);
            }
            mComposeEditPlaylistDataInfoAdapter.notifyDataSetChanged();
        }
    }


    private void DeleteAllStorageDataSelected() {
        if (mUtilsPlayListDataInfoStorage.size() > 0) {
            //Utils.mIsOptionsClick = true;
            for (int i=0; i< mUtilsPlayListDataInfoStorage.size(); i++) {
                if (mUtilsPlayListDataInfoStorage.get(i).isFile().equals("true")
                        && mUtilsPlayListDataInfoStorage.get(i).isSelected().equals("true"))
                    mUtilsPlayListDataInfoStorage.get(i).setSelected("false");
            }

            mComposeEditPlaylistDataInfoAdapter = new ComposeEditPlaylistDataInfoAdapter(mUtilsPlayListDataInfoStorage, mCtx);
            mStorageListView.setAdapter(mComposeEditPlaylistDataInfoAdapter);

            Utils.DEBUG_LOG(TAG, "[DeleteAllStorageDataSelected] mStorageListItemPosition : " + mStorageListItemPosition);
            Utils.DEBUG_LOG(TAG, "[DeleteAllStorageDataSelected] mUtilsPlayListDataInfoStorage.size() : " + mUtilsPlayListDataInfoStorage.size());
            Utils.DEBUG_LOG(TAG, "[DeleteAllStorageDataSelected] mStorageListFirstItemPosition : " + mStorageListFirstItemPosition);
            Utils.DEBUG_LOG(TAG, "[DeleteAllStorageDataSelected] mStorageListItemHeight : " + mStorageListItemHeight);
            if(mStorageListItemPosition == (mUtilsPlayListDataInfoStorage.size()-1)) {
                mStorageListView.setSelection(mStorageListItemPosition);
            }
            else {
                int scrollY = (mStorageListItemPosition - mStorageListFirstItemPosition) * mStorageListItemHeight;
                mStorageListView.setSelectionFromTop(mStorageListItemPosition, scrollY);
            }
            mComposeEditPlaylistDataInfoAdapter.notifyDataSetChanged();
        }
    }

    private void UpdateStorageDataToTempList() {
        if(mUtilsPlayListDataInfoStorage.size() > 0) {
            //Utils.mIsOptionsClick = true;
            /** Check mUtilsPlayListDataInfoStorage select item is already at mUtilsPlayListDataInfoTemp or not */
            for (Utils_PlayListDataInfo item : mUtilsPlayListDataInfoStorage) {
                if (item.isFile().equals("true")) {
                    if(item.isSelected().equals("true")) {
                        boolean IsAddItem = true;
                        if (mUtilsPlayListDataInfoTemp.size() > 0) {
                            for (int i = 0; i < mUtilsPlayListDataInfoTemp.size(); i++) {
                                if (item.getFileName().equals(mUtilsPlayListDataInfoTemp.get(i).getFileName())) {
                                    if (mUtilsPlayListDataInfoTemp.get(i).isSelected().equals("true")) {
                                        Utils.DEBUG_LOG(TAG, "this item already add !");
                                        IsAddItem = false;
                                        break;
                                    }
                                }
                            }
                        }
                        if (IsAddItem) {
                            Utils_PlayListDataInfo playlistdatainfo =
                                    new Utils_PlayListDataInfo(item.getFileName(),
                                            item.getFilePath(),
                                            item.isFile(),
                                            item.isResume(),
                                            item.isSelected());
                            mUtilsPlayListDataInfoTemp.add(playlistdatainfo);
                        }
                    }
                    else {
                        /** Check mUtilsPlayListDataInfoStorage item is "false", but mUtilsPlayListDataInfoTemp item is "true" */
                        if (mUtilsPlayListDataInfoTemp.size() > 0) {
                            for (int i = 0; i < mUtilsPlayListDataInfoTemp.size(); i++) {
                                if (item.getFileName().equals(mUtilsPlayListDataInfoTemp.get(i).getFileName())) {
                                    if (mUtilsPlayListDataInfoTemp.get(i).isSelected().equals("true")) {
                                        Utils.DEBUG_LOG(TAG, "remove this item !");
                                        mUtilsPlayListDataInfoTemp.remove(i);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            mComposeEditPlaylistDataInfoTempAdapter = new ComposeEditPlaylistDataInfoTempAdapter(mUtilsPlayListDataInfoTemp, mCtx);
            mTempListView.setAdapter(mComposeEditPlaylistDataInfoTempAdapter);
            Utils.setPlayListDataInfoTemp(mUtilsPlayListDataInfoTemp);

            if(mUtilsPlayListDataInfoTemp.size() > 0) {

                Utils.DEBUG_LOG(TAG, "[UpdateStorageDataToTempList] mTempListItemPosition : " + mTempListItemPosition);
                Utils.DEBUG_LOG(TAG, "[UpdateStorageDataToTempList] mUtilsPlayListDataInfoTemp.size() : " + mUtilsPlayListDataInfoTemp.size());
                Utils.DEBUG_LOG(TAG, "[UpdateStorageDataToTempList] mTempListFirstItemPosition : " + mTempListFirstItemPosition);
                Utils.DEBUG_LOG(TAG, "[UpdateStorageDataToTempList] mTempListItemHeight : " + mTempListItemHeight);
                //if(mTempListItemPosition == (mUtilsPlayListDataInfoTemp.size()-1)) {
                //    mTempListView.setSelection(mTempListItemPosition);
                //}
                //else {
                //    int scrollY = (mTempListItemPosition - mTempListFirstItemPosition) * mTempListItemHeight;
                //    mTempListView.setSelectionFromTop(mTempListItemPosition, scrollY);
                //}
                mTempListItemPosition = (mUtilsPlayListDataInfoTemp.size()-1);
                mTempListView.setSelection(mTempListItemPosition);
                mComposeEditPlaylistDataInfoTempAdapter.notifyDataSetChanged();
            }

            mComposeEditPlaylistDataInfoTempAdapter.notifyDataSetChanged();
        }
    }

    private void addLeftHintBarDataToHintList() {
        Utils_HintBarInfo hintbarinfo;
        hintbarinfo = new Utils_HintBarInfo(R.drawable.select_all_icon, getString(R.string.selectall));
        mLeftUtilsHintBarInfo.add(hintbarinfo);
        hintbarinfo = new Utils_HintBarInfo(R.drawable.delete_icon, getString(R.string.deleteall));
        mLeftUtilsHintBarInfo.add(hintbarinfo);
        hintbarinfo = new Utils_HintBarInfo(R.drawable.update_icon, getString(R.string.addremove));
        mLeftUtilsHintBarInfo.add(hintbarinfo);
        hintbarinfo = new Utils_HintBarInfo(R.drawable.sort_icon, getString(R.string.sort));
        mLeftUtilsHintBarInfo.add(hintbarinfo);
        hintbarinfo = new Utils_HintBarInfo(R.drawable.save_icon, (getString(R.string.save) + "/" + getString(R.string.abort)));
        mLeftUtilsHintBarInfo.add(hintbarinfo);
        hintbarinfo = new Utils_HintBarInfo(R.drawable.back_icon, getString(R.string.back));
        mLeftUtilsHintBarInfo.add(hintbarinfo);
    }

    private void HintBarShowOrHide(boolean isShow, int selectedItem) {

        if(isShow) {
            if(Utils.SUPPORT_TOUCH) {
                if (mComposeEditPlaylistDataInfoAdapter.getSelectedIndex() != -1) {
                    mComposeEditPlaylistDataInfoAdapter.setSelectedIndex(-1);
                    mComposeEditPlaylistDataInfoTempAdapter.notifyDataSetInvalidated();
                }
                if (mComposeEditPlaylistDataInfoTempAdapter.getSelectedIndex() != -1) {
                    mComposeEditPlaylistDataInfoTempAdapter.setSelectedIndex(-1);
                    mComposeEditPlaylistDataInfoTempAdapter.notifyDataSetInvalidated();
                }
            }

            Utils.mIsOptionsClick = false;
            if(mStorageListView.isFocused()) {
                Utils.mFromListType = Utils.listtype.STORAGE_LIST;
            }
            else if(mTempListView.isFocused()) {
                Utils.mFromListType = Utils.listtype.TEMP_LIST;
            }

            mStorageListView.setFocusable(false);
            mTempListView.setFocusable(false);

            mLeftHintListView.setSelection(selectedItem);
            //mLeftHintListView.setVisibility(View.VISIBLE);
            mLeftHintListView.requestFocus();
            {/** Animation */
                mHintFrameLayout.setVisibility(View.VISIBLE);
                if(hasAnimation) {
                    //set animation to FrameLayout
                    mHintFrameLayout.setAnimation(mHintFrameLayoutShowAnimation);
                    //start animation
                    mHintFrameLayoutShowAnimation.startNow();
                }
            }
        }
        else {
            /* highlight list item when go to hintbar
            Utils.mIsOptionsClick = false;
            */

            mStorageListView.setFocusable(true);
            mTempListView.setFocusable(true);

            {/** Animation */
                mHintFrameLayout.setVisibility(View.VISIBLE);
                if(hasAnimation) {
                    //set animation to FrameLayout
                    mHintFrameLayout.setAnimation(mHintFrameLayoutHideAnimation);
                    //start animation
                    mHintFrameLayoutHideAnimation.startNow();
                }
            }
            mHintFrameLayout.setVisibility(View.GONE);
            //mLeftHintListView.setVisibility(View.GONE);

            Utils.DEBUG_LOG(TAG, "[HintBarShowOrHide] mStorageListItemPosition : " + mStorageListItemPosition);
            Utils.DEBUG_LOG(TAG, "[HintBarShowOrHide] mUtilsPlayListDataInfoStorage.size() : " + mUtilsPlayListDataInfoStorage.size());
            Utils.DEBUG_LOG(TAG, "[HintBarShowOrHide] mStorageListItemHeight : " + mStorageListItemHeight);
            Utils.DEBUG_LOG(TAG, "[HintBarShowOrHide] mStorageListFirstItemPosition : " + mStorageListFirstItemPosition);
            Utils.DEBUG_LOG(TAG, "[HintBarShowOrHide] getLastVisiblePosition  : " + mStorageListView.getLastVisiblePosition());
            Utils.DEBUG_LOG(TAG, "[HintBarShowOrHide] getFirstVisiblePosition : " + mStorageListView.getFirstVisiblePosition());
            Utils.DEBUG_LOG(TAG, "[HintBarShowOrHide] mStorageListVisibleItemCount : " + mStorageListVisibleItemCount);

            Utils.DEBUG_LOG(TAG, "[HintBarShowOrHide] getFirstVisiblePosition : " + mStorageListView.getFirstVisiblePosition());

            int scrollY;
            if(Utils.listtype.STORAGE_LIST == Utils.mFromListType)
            {
                mStorageListView.requestFocus();
                mStorageListView.setAdapter(mComposeEditPlaylistDataInfoAdapter);
                if(mStorageListItemPosition == (mUtilsPlayListDataInfoStorage.size()-1)) {
                    mStorageListView.setSelection(mStorageListItemPosition);
                }
                else {
                    scrollY = (mStorageListItemPosition - mStorageListFirstItemPosition) * mStorageListItemHeight;
                    mStorageListView.setSelectionFromTop(mStorageListItemPosition, scrollY);
                }
                mComposeEditPlaylistDataInfoAdapter.notifyDataSetChanged();
            }
            else if(Utils.listtype.TEMP_LIST == Utils.mFromListType) {
                mTempListView.requestFocus();
                mTempListView.setAdapter(mComposeEditPlaylistDataInfoTempAdapter);
                if(mTempListItemPosition == (mUtilsPlayListDataInfoTemp.size()-1)) {
                    mTempListView.setSelection(mTempListItemPosition);
                }
                else {
                    scrollY = (mTempListItemPosition - mTempListFirstItemPosition) * mTempListItemHeight;
                    mTempListView.setSelectionFromTop(mTempListItemPosition, scrollY);
                }
                mComposeEditPlaylistDataInfoTempAdapter.notifyDataSetChanged();
            }

            /* highlight list item when go to hintbar
            if (Utils.listtype.STORAGE_LIST == Utils.mFromListType) {
                mComposeEditPlaylistDataInfoAdapter.notifyDataSetChanged();
                mStorageListView.setSelection(mStorageListItemPosition);
                mStorageListView.smoothScrollToPosition(mStorageListItemPosition);
                mStorageListView.requestFocus();
            } else if (Utils.listtype.TEMP_LIST == Utils.mFromListType) {
                mComposeEditPlaylistDataInfoTempAdapter.notifyDataSetChanged();
                mTempListView.setSelection(mTempListItemPosition);
                mTempListView.smoothScrollToPosition(mTempListItemPosition);
                mTempListView.requestFocus();
            }
            */
        }
    }
}
