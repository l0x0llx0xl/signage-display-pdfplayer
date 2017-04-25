package com.tpv.app.pdf.Settings;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.tpv.app.pdf.Adapter.SettingsListViewAdapter;
import com.tpv.app.pdf.Common.Utils_SettingsListInfo;
import com.tpv.app.pdf.Common.Utils;
import com.tpv.app.pdf.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andy.Hsu on 2015/9/1.
 */
public class SettingsHomeFragment extends Fragment {
    private static final String TAG = SettingsHomeFragment.class.getName();

    private Context mCtx;

    //region Button
    private Button mBtn_RepeatMode;
    private Button mBtn_SlideshowPeriod;
    private int mBtn_focusid;
    //endregion

    //region repeat mode list
    private ListView mRepeatSettingsListView;
    private SettingsListViewAdapter mRepeatInfoAdpter;
    private List<Utils_SettingsListInfo> mRepeatUtilsSettingsListInfo;
    private Utils.repeatMode mRepeatMode;
    //endregion

    //region Photo Period mode list
    private ListView mPhotoSettingsPeriodListView;
    private SettingsListViewAdapter mPhotoPeriodInfoAdpter;
    private List<Utils_SettingsListInfo> mPhotoSettingsPeriodListInfo;
    private Utils.effectDuration mPhotoEffectDuration;
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.frag_settings, null);
        mCtx = vg.getContext();

        //region repeat mode list
        /** repeat mode list */
        addRepeatModeInfo();
        mRepeatSettingsListView = (ListView) vg.findViewById(R.id.settings_repeat_listview);
        mRepeatInfoAdpter = new SettingsListViewAdapter(mRepeatUtilsSettingsListInfo, mCtx);
        mRepeatSettingsListView.setOnItemClickListener(mRepeatSettingsListViewItemClickListener);
        mRepeatSettingsListView.setAdapter(mRepeatInfoAdpter);
        mRepeatSettingsListView.setSelection(mRepeatMode.ordinal());
        mRepeatSettingsListView.setFocusable(false);
        mRepeatSettingsListView.setVisibility(View.GONE);
        //endregion

        //region Photo Period mode list
        /** Photo Period list */
        addPhotoPeriodModeInfo();
        mPhotoSettingsPeriodListView = (ListView) vg.findViewById(R.id.settings_animation_slideshow_perid_listview);
        mPhotoPeriodInfoAdpter = new SettingsListViewAdapter(mPhotoSettingsPeriodListInfo, mCtx);
        mPhotoSettingsPeriodListView.setOnItemClickListener(mPhotoSettingsPeriodListViewItemClickListener);
        mPhotoSettingsPeriodListView.setAdapter(mPhotoPeriodInfoAdpter);
        mPhotoSettingsPeriodListView.setSelection(mPhotoEffectDuration.ordinal());
        mPhotoSettingsPeriodListView.setFocusable(false);
        mPhotoSettingsPeriodListView.setVisibility(View.GONE);
        //mPhotoSettingsPeriodListView.requestFocus();
        //endregion

        initHomeBtn(vg);

        registerReceiver();

        return vg;
    }

    @Override
    public void onResume() {
        Utils.DEBUG_LOG(TAG, "[onResume]");
        super.onResume();
    }

    private void initHomeBtn(View v) {
        mBtn_RepeatMode = (Button) v.findViewById(R.id.btn_repeat_mode);
        if (mBtn_RepeatMode != null) {
            mBtn_RepeatMode.setOnClickListener(BtnClick);
            mBtn_RepeatMode.requestFocus();
        }

        mBtn_SlideshowPeriod = (Button) v.findViewById(R.id.btn_slideshow_period);
        if (mBtn_SlideshowPeriod != null) {
            mBtn_SlideshowPeriod.setOnClickListener(BtnClick);
        }
    }

    private BtnClickListener BtnClick = new BtnClickListener();
    class BtnClickListener implements View.OnClickListener {
        public void onClick(View v) {
            mBtn_focusid = v.getId();
            switch (mBtn_focusid) {
                case R.id.btn_repeat_mode:
                    if(mRepeatSettingsListView.isFocusable()) {
                        mRepeatSettingsListView.setFocusable(false);
                        mRepeatSettingsListView.setVisibility(View.GONE);
                    }
                    else if(!mRepeatSettingsListView.isFocusable()) {
                        mRepeatSettingsListView.setFocusable(true);
                        mRepeatSettingsListView.setVisibility(View.VISIBLE);

                        mPhotoSettingsPeriodListView.setFocusable(false);
                        mPhotoSettingsPeriodListView.setVisibility(View.GONE);
                    }
                    break;
                case R.id.btn_slideshow_period:
                    if(mPhotoSettingsPeriodListView.isFocusable()) {
                        mPhotoSettingsPeriodListView.setFocusable(false);
                        mPhotoSettingsPeriodListView.setVisibility(View.GONE);
                    }
                    else if(!mPhotoSettingsPeriodListView.isFocusable()) {
                        mPhotoSettingsPeriodListView.setFocusable(true);
                        mPhotoSettingsPeriodListView.setVisibility(View.VISIBLE);

                        mRepeatSettingsListView.setFocusable(false);
                        mRepeatSettingsListView.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    //region repeat mode
    private AdapterView.OnItemClickListener mRepeatSettingsListViewItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent,  //The AdapterView where the click happened
                                View v,                 //The view within the AdapterView that was clicked
                                int position,           //The position of the view in the adapter
                                long id                 //The row id of the item that was clicked
        ){
            Utils.DEBUG_LOG(TAG, "[mRepeatSettingsListViewItemClickListener] position: " + position);
            for(int i=0; i< mRepeatUtilsSettingsListInfo.size(); i++) {
                if(i == position) {
                    mRepeatUtilsSettingsListInfo.get(i).setSelected(true);
                }
                else {
                    mRepeatUtilsSettingsListInfo.get(i).setSelected(false);
                }
            }

            Utils.repeatMode type = Utils.repeatMode.values()[position];
            Utils.PDFPlayerContentProvider_Set_PlaylistRepeatModeSettings(type);

            mRepeatSettingsListView.setAdapter(mRepeatInfoAdpter);
            mRepeatSettingsListView.setSelection(position);
        }
    };

    private void addRepeatModeInfo() {

        mRepeatMode = Utils.PDFPlayerContentProvider_Get_PlaylistRepeatModeSettings();

        mRepeatUtilsSettingsListInfo = new ArrayList<Utils_SettingsListInfo>();
        Utils_SettingsListInfo repeatmodeinfo;
        repeatmodeinfo = new Utils_SettingsListInfo(0,
                getString(R.string.repeatonce),
                (mRepeatMode == Utils.repeatMode.REPEAT_ONCE)? true : false);
        mRepeatUtilsSettingsListInfo.add(repeatmodeinfo);

        repeatmodeinfo = new Utils_SettingsListInfo(0,
                getString(R.string.repeatall),
                (mRepeatMode == Utils.repeatMode.REPEAT_ALL)? true : false);
        mRepeatUtilsSettingsListInfo.add(repeatmodeinfo);

        //repeatmodeinfo = new Utils_SettingsListInfo(0,
        //        getString(R.string.none),
        //        (mRepeatMode == Utils.repeatMode.REPEAT_NONE)? true : false);
        //mRepeatUtilsSettingsListInfo.add(repeatmodeinfo);
    }
    //endregion

    //region sildeshow period
    private AdapterView.OnItemClickListener mPhotoSettingsPeriodListViewItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent,  //The AdapterView where the click happened
                                View v,                 //The view within the AdapterView that was clicked
                                int position,           //The position of the view in the adapter
                                long id                 //The row id of the item that was clicked
        ){
            Utils.DEBUG_LOG(TAG, "[mPhotoSettingsPeriodListViewItemClickListener] position: " + position);
            for(int i=0; i< mPhotoSettingsPeriodListInfo.size(); i++) {
                if(i == position) {
                    mPhotoSettingsPeriodListInfo.get(i).setSelected(true);
                }
                else {
                    mPhotoSettingsPeriodListInfo.get(i).setSelected(false);
                }
            }

            Utils.effectDuration periodtype = Utils.effectDuration.values()[position];
            Utils.PDFPlayerContentProvider_Set_EffectDuration(periodtype);

            mPhotoSettingsPeriodListView.setAdapter(mPhotoPeriodInfoAdpter);
            mPhotoSettingsPeriodListView.setSelection(position);
        }
    };
    private void addPhotoPeriodModeInfo() {
        mPhotoEffectDuration = Utils.PDFPlayerContentProvider_Get_EffectDuration();

        mPhotoSettingsPeriodListInfo = new ArrayList<Utils_SettingsListInfo>();
        Utils_SettingsListInfo slideshowperiodinfo;
        slideshowperiodinfo = new Utils_SettingsListInfo(0,
                getString(R.string.period_5),
                (mPhotoEffectDuration == Utils.effectDuration.PERIOD_5)? true : false);
        mPhotoSettingsPeriodListInfo.add(slideshowperiodinfo);

        slideshowperiodinfo = new Utils_SettingsListInfo(0,
                getString(R.string.period_10),
                (mPhotoEffectDuration == Utils.effectDuration.PERIOD_10)? true : false);
        mPhotoSettingsPeriodListInfo.add(slideshowperiodinfo);

        slideshowperiodinfo = new Utils_SettingsListInfo(0,
                getString(R.string.period_15),
                (mPhotoEffectDuration == Utils.effectDuration.PERIOD_15)? true : false);
        mPhotoSettingsPeriodListInfo.add(slideshowperiodinfo);

        slideshowperiodinfo = new Utils_SettingsListInfo(0,
                getString(R.string.period_20),
                (mPhotoEffectDuration == Utils.effectDuration.PERIOD_20)? true : false);
        mPhotoSettingsPeriodListInfo.add(slideshowperiodinfo);
    }
    //endregion

    private BroadcastReceiver mSettingsRepeatFragmentBroadcastReceiver;
    private void registerReceiver() {
        if (mSettingsRepeatFragmentBroadcastReceiver == null) {
            Utils.DEBUG_LOG(TAG, "[registerReceiver]");
            mSettingsRepeatFragmentBroadcastReceiver = new SettingsRepeatFragmentReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Utils.INTENT_WEBCONTROLSERVICE_CALLBACK);
            mCtx.registerReceiver(mSettingsRepeatFragmentBroadcastReceiver, filter);
        }
    }
    class SettingsRepeatFragmentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Utils.DEBUG_LOG(TAG, "[onReceive] SettingsRepeatFragmentReceiver : " + intent);
            final String action = intent.getAction();

            if(action.equals(Utils.INTENT_WEBCONTROLSERVICE_CALLBACK)) {
                Utils.DEBUG_LOG(TAG, "[onReceive] INTENT_WEBCONTROLSERVICE_CALLBACK");
                String type = intent.getStringExtra("type");
                Utils.DEBUG_LOG(TAG, "[onReceive] type : " + type);
                int position = 0;
                if(type!=null && !type.isEmpty()) {
                    if(type.equals("repeatmode")) {
                        mRepeatMode = Utils.PDFPlayerContentProvider_Get_PlaylistRepeatModeSettings();
                        for(int i=0; i<= Utils.repeatMode.REPEAT_NONE.ordinal(); i++) {
                            if(mRepeatMode == Utils.repeatMode.values()[i]) {
                                mRepeatUtilsSettingsListInfo.get(i).setSelected(true);
                                position = i;
                            }
                            else {
                                mRepeatUtilsSettingsListInfo.get(i).setSelected(false);
                            }
                        }
                        mRepeatSettingsListView.setAdapter(mRepeatInfoAdpter);
                        mRepeatSettingsListView.setSelection(position);
                    }
                    else if(type.equals("slideshowperiodtype")){
                        mPhotoEffectDuration = Utils.PDFPlayerContentProvider_Get_EffectDuration();
                        for(int i=0; i<= Utils.effectDuration.PERIOD_20.ordinal(); i++) {
                            if(mPhotoEffectDuration == Utils.effectDuration.values()[i]) {
                                mPhotoSettingsPeriodListInfo.get(i).setSelected(true);
                                position = i;
                            }
                            else {
                                mPhotoSettingsPeriodListInfo.get(i).setSelected(false);
                            }
                        }
                        mPhotoSettingsPeriodListView.setAdapter(mPhotoPeriodInfoAdpter);
                        mPhotoSettingsPeriodListView.setSelection(position);
                    }
                }
            }
        }
    }

}