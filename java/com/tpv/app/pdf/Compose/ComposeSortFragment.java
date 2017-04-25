package com.tpv.app.pdf.Compose;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tpv.app.pdf.Adapter.ComposeSortPlaylistDataInfoAdapter;
import com.tpv.app.pdf.Common.Utils;
import com.tpv.app.pdf.Common.Utils_PlayListDataInfo;
import com.tpv.app.pdf.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Andy.Hsu on 2015/7/28.
 */
public class ComposeSortFragment extends Fragment {
    private static final String TAG = ComposeSortFragment.class.getName();

    private Context mCtx;

    //region photo and video list
    private ListView mSortTempListView;
    private ComposeSortPlaylistDataInfoAdapter mSortPlaylistDataInfoTempAdpter;
    private List<Utils_PlayListDataInfo> mSortUtilsPlayListDataInfoTemp;
    private int mMoveItem = -1;
    private int mSortListItemHeight;
    private int mVisibleItemCount;
    private int mFirstVisibleItem;
    private int mScrollY;

    private TextView mSortHintOkTextView;
    //endregion

    private ImageView mHintUpImage;
    private ImageView mHintDownImage;
    private ImageView mHintSaveImage;
    private TextView mHintSaveText;
    private ImageView mHintBackImage;
    private TextView mHintBackText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vg = inflater.inflate(R.layout.frag_compose_sort, container, false);

        mCtx = vg.getContext();

        /** Temp file list */
        mSortUtilsPlayListDataInfoTemp = Utils.getPlayListDataInfoTemp();

        //region photo and video list
        mSortTempListView = (ListView) vg.findViewById(R.id.compose_sort_photo_video_list);
        mSortPlaylistDataInfoTempAdpter = new ComposeSortPlaylistDataInfoAdapter(mSortUtilsPlayListDataInfoTemp, -1, mCtx);
        mSortTempListView.setOnItemClickListener(mSortListViewItemClickListener);
        mSortTempListView.setOnScrollListener(mSortListViewItemScrollListener);
        mSortTempListView.setOnKeyListener(onComposeSortFragmentKeyDownListener);
        mSortTempListView.setAdapter(mSortPlaylistDataInfoTempAdpter);
        mSortTempListView.requestFocus();

        mSortHintOkTextView = (TextView) vg.findViewById(R.id.compose_sort_photo_video_hint_ok_text);
        //endregion

        if(Utils.SUPPORT_TOUCH) {
            //hint up
            mHintUpImage = (ImageView) vg.findViewById(R.id.compose_sort_photo_video_hint_up_icon);
            mHintUpImage.setOnClickListener(mOnHintUpClick);
            mHintUpImage.setVisibility(View.INVISIBLE);
            //hint down
            mHintDownImage = (ImageView) vg.findViewById(R.id.compose_sort_photo_video_hint_down_icon);
            mHintDownImage.setOnClickListener(mOnHintDownClick);
            mHintDownImage.setVisibility(View.INVISIBLE);
            //hint save
            mHintSaveImage = (ImageView) vg.findViewById(R.id.compose_sort_photo_video_hint_ok_icon);
            mHintSaveImage.setOnClickListener(mOnHintSaveClick);
            mHintSaveText = (TextView) vg.findViewById(R.id.compose_sort_photo_video_hint_ok_text);
            mHintSaveText.setOnClickListener(mOnHintSaveClick);
            //hint back
            mHintBackImage = (ImageView) vg.findViewById(R.id.compose_sort_photo_video_hint_back_icon);
            mHintBackImage.setOnClickListener(mOnHintBackClick);
            mHintBackText = (TextView) vg.findViewById(R.id.compose_sort_photo_video_hint_back_text);
            mHintBackText.setOnClickListener(mOnHintBackClick);
        }
        return vg;
    }
    private View.OnClickListener mOnHintUpClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            Utils.DEBUG_LOG(TAG, "up click");
            ///* move up */
            if(mMoveItem != -1) {
                if (mMoveItem > 0) {
                    MoveUpOrDown(true);
                }
            }
            setSortCompletePlayListToTempList();
        }
    };

    private View.OnClickListener mOnHintDownClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            Utils.DEBUG_LOG(TAG, "down click");
            ///* move down */
            if(mMoveItem != -1) {
                if (mMoveItem < (mSortUtilsPlayListDataInfoTemp.size()-1)) {
                    MoveUpOrDown(false);
                }
            }
        }
    };
    private View.OnClickListener mOnHintSaveClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            Utils.DEBUG_LOG(TAG, "save click");
            if(mMoveItem != -1) {
                ItemClickDown(false);
                mMoveItem = -1;
            }
        }
    };
    private View.OnClickListener mOnHintBackClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            Utils.DEBUG_LOG(TAG, "back click");
            if(mMoveItem != -1) {
                ItemClickDown(false);
                mMoveItem = -1;
            }
            else {
                setSortCompletePlayListToTempList();
                getFragmentManager().popBackStack();
            }
        }
    };

    //region photo and video list
    private AdapterView.OnItemClickListener mSortListViewItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent,  //The AdapterView where the click happened
                                View v,                 //The view within the AdapterView that was clicked
                                int position,           //The position of the view in the adapter
                                long id                 //The row id of the item that was clicked
        ){
            Utils.DEBUG_LOG(TAG, "[mSortListViewItemClickListener] position: " + position);
            mSortListItemHeight = v.getMeasuredHeight();
            Utils.DEBUG_LOG(TAG, "[mSortListViewItemClickListener] mSortListItemHeight : " + mSortListItemHeight);
            mScrollY = (position - mFirstVisibleItem) * mSortListItemHeight;
            Utils.DEBUG_LOG(TAG, "[mSortListViewItemClickListener] mScrollY : " + mScrollY);
            mMoveItem = position;
            ItemClickDown(true);
        }
    };

    private AbsListView.OnScrollListener mSortListViewItemScrollListener = new AbsListView.OnScrollListener(){
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
            //Utils.DEBUG_LOG(TAG, "[mSortListViewItemScrollListener] view.getSelectedItem() : " + view.getSelectedItem());
            //Utils.DEBUG_LOG(TAG, "[mSortListViewItemScrollListener] visibleItemCount : " + visibleItemCount);
            //Utils.DEBUG_LOG(TAG, "[mSortListViewItemScrollListener] firstVisibleItem : " + firstVisibleItem);
            mFirstVisibleItem = firstVisibleItem;
            mVisibleItemCount = visibleItemCount;

            //if (view.getSelectedItem() != null) {
            //    Utils.DEBUG_LOG(TAG, "[mSortListViewItemScrollListener] view.getSelectedItemPosition() : " + view.getSelectedItemPosition());
            //}
        }
    };

    private View.OnKeyListener onComposeSortFragmentKeyDownListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(mMoveItem != -1) {
                //Utils.DEBUG_LOG(TAG, "event.getAction() : " + event.getAction());
                //Utils.DEBUG_LOG(TAG, "keyCode : " + keyCode);
                if(event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch(keyCode) {
                        case KeyEvent.KEYCODE_DPAD_UP:
                            Utils.DEBUG_LOG(TAG, "KEYCODE_DPAD_UP");
                            ///* move up */
                            if(mMoveItem > 0) {
                                MoveUpOrDown(true);
                            }
                            break;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            Utils.DEBUG_LOG(TAG, "KEYCODE_DPAD_DOWN");
                            ///* move down */
                            if(mMoveItem < (mSortUtilsPlayListDataInfoTemp.size()-1)) {
                                MoveUpOrDown(false);
                            }
                            break;
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                        case KeyEvent.KEYCODE_BACK:
                        case KeyEvent.KEYCODE_ESCAPE:
                            Utils.DEBUG_LOG(TAG, "KEYCODE_BACK");
                            ItemClickDown(false);
                            mMoveItem = -1;
                    }
                }
                return true;
            }
            else {
                if(event.getAction() == KeyEvent.ACTION_DOWN) {
                    if(keyCode == KeyEvent.KEYCODE_BACK
                            || keyCode == KeyEvent.KEYCODE_ESCAPE) {
                        Utils.mIsOptionback = true;
                    }
                }
            }
            setSortCompletePlayListToTempList();
            return false;
        }
    };

    private void ItemClickDown(boolean isMove) {
        if(isMove) {
            mSortHintOkTextView.setText(R.string.save);
            mSortPlaylistDataInfoTempAdpter = new ComposeSortPlaylistDataInfoAdapter(mSortUtilsPlayListDataInfoTemp, mMoveItem, mCtx);

            if(Utils.SUPPORT_TOUCH) {
                mHintUpImage.setVisibility(View.VISIBLE);
                mHintDownImage.setVisibility(View.VISIBLE);
            }
        }
        else {
            mSortHintOkTextView.setText(R.string.select);
            mSortPlaylistDataInfoTempAdpter = new ComposeSortPlaylistDataInfoAdapter(mSortUtilsPlayListDataInfoTemp, -1, mCtx);

            if(Utils.SUPPORT_TOUCH) {
                mHintUpImage.setVisibility(View.INVISIBLE);
                mHintDownImage.setVisibility(View.INVISIBLE);
            }
        }

        mSortTempListView.setAdapter(mSortPlaylistDataInfoTempAdpter);
        mSortTempListView.setAdapter(mSortPlaylistDataInfoTempAdpter);

        if(mMoveItem == (mSortUtilsPlayListDataInfoTemp.size()-1)) {
            mSortTempListView.setSelection(mMoveItem);
        }
        else {
            mSortTempListView.setSelectionFromTop(mMoveItem, mScrollY);
        }

        mSortPlaylistDataInfoTempAdpter.notifyDataSetChanged();
    }

    private void MoveUpOrDown(boolean isUp) {
        //Utils.DEBUG_LOG(TAG, "mSortUtilsPlayListDataInfoTemp.size() : " + mSortUtilsPlayListDataInfoTemp.size());
        //Utils.DEBUG_LOG(TAG, "mMoveItem : " + mMoveItem);
        //Utils.DEBUG_LOG(TAG, "mFirstVisibleItem : " + mFirstVisibleItem);
        //Utils.DEBUG_LOG(TAG, "mVisibleItemCount : " + mVisibleItemCount);
        Utils_PlayListDataInfo playlistdatainfo = mSortUtilsPlayListDataInfoTemp.get(mMoveItem);
        mSortUtilsPlayListDataInfoTemp.remove(mMoveItem);

        if(isUp) {
            mMoveItem--;
        }
        else {
            mMoveItem++;
        }

        mSortUtilsPlayListDataInfoTemp.add(mMoveItem, playlistdatainfo);
        mSortPlaylistDataInfoTempAdpter = new ComposeSortPlaylistDataInfoAdapter(mSortUtilsPlayListDataInfoTemp, mMoveItem, mCtx);
        mSortTempListView.setAdapter(mSortPlaylistDataInfoTempAdpter);

        if(isUp) {
            if(mMoveItem > mFirstVisibleItem) {
                mScrollY = (mMoveItem - mFirstVisibleItem) * mSortListItemHeight;
            } else if(mMoveItem == 0) {
                mScrollY = 0;
            } else {
                mScrollY = (mVisibleItemCount - 9) * mSortListItemHeight;
            }
            mSortTempListView.setSelectionFromTop(mMoveItem, mScrollY);
        }
        else {
            if(mMoveItem < (mSortUtilsPlayListDataInfoTemp.size()-1)) {
                if((mMoveItem - mFirstVisibleItem) == (mVisibleItemCount -1)) {
                    mFirstVisibleItem++;
                }
                mScrollY = (mMoveItem - mFirstVisibleItem) * mSortListItemHeight;
                mSortTempListView.setSelectionFromTop(mMoveItem, mScrollY);
            } else if(mMoveItem == (mSortUtilsPlayListDataInfoTemp.size()-1)) {
                mSortTempListView.setSelection(mMoveItem);
            }
        }

        mSortPlaylistDataInfoTempAdpter.notifyDataSetChanged();
    }
    //endregion

    private void setSortCompletePlayListToTempList() {
        Utils.setPlayListDataInfoTemp(mSortUtilsPlayListDataInfoTemp);
    }
}
