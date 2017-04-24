package com.tpv.app.pdf.Compose;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tpv.app.pdf.Common.Utils;
import com.tpv.app.pdf.R;

/**
 * Created by Andy.Hsu on 2015/8/27.
 */
public class ComposeSelectUsbDialog extends Fragment {
    private static final String TAG = ComposeSelectUsbDialog.class.getName();
    private Context mCtx;

    String[] mUsbPath = new String[20];

    private TextView mtextView;

    private Button mBtn_Usb1;
    private Button mBtn_Usb2;
    private Button mBtn_Usb3;
    private Button mBtn_Usb4;
    private Button mBtn_Usb5;
    private int mBtn_focusid;

    private int mOrientation;
    private int mAvailCnt = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.frag_select_usb, null);
        mCtx = vg.getContext();

        mUsbPath = Utils.findUSBStorage();
        mAvailCnt = Utils.getUSBAvailCnt();

        mtextView = (TextView)vg.findViewById(R.id.dialog_text);
        mBtn_Usb1 = (Button) vg.findViewById(R.id.dialog_btn_usb1);
        mBtn_Usb2 = (Button) vg.findViewById(R.id.dialog_btn_usb2);
        mBtn_Usb3 = (Button) vg.findViewById(R.id.dialog_btn_usb3);
        mBtn_Usb4 = (Button) vg.findViewById(R.id.dialog_btn_usb4);
        mBtn_Usb5 = (Button) vg.findViewById(R.id.dialog_btn_usb5);

        String textStr = getString(R.string.selectusbstorage);
        if(mAvailCnt == 0) {
            textStr = getString(R.string.pleasepluginusb);
        }
        mtextView.setText(textStr);

        mOrientation = getResources().getConfiguration().orientation;

        initBtn();

        registerReceiver();

        return vg;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        Utils.DEBUG_LOG(TAG, "[onDestroy]");

        if (mComposeSelectUsbDialogBroadcastReceiver != null) {
            Utils.DEBUG_LOG(TAG, "[unregisterReceiver]");
            mCtx.unregisterReceiver(mComposeSelectUsbDialogBroadcastReceiver);
            mComposeSelectUsbDialogBroadcastReceiver = null;
        }
        super.onDestroy();
    }

    private void initBtn() {
        if (mBtn_Usb1 != null) {
            mBtn_Usb1.setOnClickListener(BtnClick);
        }

        if (mBtn_Usb2 != null) {
            mBtn_Usb2.setOnClickListener(BtnClick);
        }

        if (mBtn_Usb3 != null) {
            mBtn_Usb3.setOnClickListener(BtnClick);
        }

        if (mBtn_Usb4 != null) {
            mBtn_Usb4.setOnClickListener(BtnClick);
        }

        if (mBtn_Usb5 != null) {
            mBtn_Usb5.setOnClickListener(BtnClick);
        }
        mBtn_Usb5.setVisibility(View.GONE);

        if(Configuration.ORIENTATION_LANDSCAPE == mOrientation) {
            mBtn_Usb1.setVisibility(View.GONE);
            mBtn_Usb2.setVisibility(View.GONE);
            mBtn_Usb3.setVisibility(View.GONE);
            mBtn_Usb4.setVisibility(View.GONE);
            mBtn_Usb5.setVisibility(View.GONE);
        }
        else {
            mBtn_Usb1.setVisibility(View.INVISIBLE);
            mBtn_Usb2.setVisibility(View.INVISIBLE);
            mBtn_Usb3.setVisibility(View.INVISIBLE);
            mBtn_Usb4.setVisibility(View.INVISIBLE);
            mBtn_Usb5.setVisibility(View.INVISIBLE);
        }

        for(int i=0; i<mAvailCnt; i++) {
            String[] btntext;
            //if(mUsbPath[i].contains(String.valueOf(i+1)))
			{
                btntext = mUsbPath[i].split("/");
                Utils.DEBUG_LOG(TAG, "btntext : " + btntext);
                if(mBtn_Usb1.getVisibility() == View.GONE
                    || mBtn_Usb1.getVisibility() == View.INVISIBLE) {
                    mBtn_Usb1.setText(btntext[btntext.length - 1]);
                    mBtn_Usb1.setVisibility(View.VISIBLE);
                    mBtn_Usb1.requestFocus();
                }
                else if(mBtn_Usb2.getVisibility() == View.GONE
                        || mBtn_Usb2.getVisibility() == View.INVISIBLE) {
                    mBtn_Usb2.setText(btntext[btntext.length - 1]);
                    mBtn_Usb2.setVisibility(View.VISIBLE);
                }
                else if(mBtn_Usb3.getVisibility() == View.GONE
                        || mBtn_Usb3.getVisibility() == View.INVISIBLE) {
                    mBtn_Usb3.setText(btntext[btntext.length - 1]);
                    mBtn_Usb3.setVisibility(View.VISIBLE);
                }
                else if(mBtn_Usb4.getVisibility() == View.GONE
                        || mBtn_Usb4.getVisibility() == View.INVISIBLE) {
                    mBtn_Usb4.setText(btntext[btntext.length - 1]);
                    mBtn_Usb4.setVisibility(View.VISIBLE);
                }
                else if(mBtn_Usb5.getVisibility() == View.GONE
                        || mBtn_Usb5.getVisibility() == View.INVISIBLE) {
                    mBtn_Usb5.setText(btntext[btntext.length - 1]);
                    mBtn_Usb5.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private BtnClickListener BtnClick = new BtnClickListener();
    class BtnClickListener implements View.OnClickListener {
        public void onClick(View v) {
            mBtn_focusid = v.getId();
            switch (mBtn_focusid) {
                case R.id.dialog_btn_usb1:
                    Utils.setUSBStoragePathIndex(0);
                    break;
                case R.id.dialog_btn_usb2:
                    Utils.setUSBStoragePathIndex(1);
                    break;
                case R.id.dialog_btn_usb3:
                    Utils.setUSBStoragePathIndex(2);
                    break;
                case R.id.dialog_btn_usb4:
                    Utils.setUSBStoragePathIndex(3);
                    break;
                case R.id.dialog_btn_usb5:
                    Utils.setUSBStoragePathIndex(4);
                    break;
                default:
                    Utils.getFragmanetManager().popBackStack();
                    return;
            }

            Utils.getFragmanetManager().beginTransaction()
                    .replace(R.id.main_container, new ComposeEditFragment(), "ComposeEditFragmentTag")
                    .addToBackStack(null)
                    .commit();
        }
    }


    private BroadcastReceiver mComposeSelectUsbDialogBroadcastReceiver;
    private void registerReceiver() {
        if (mComposeSelectUsbDialogBroadcastReceiver == null) {
            Utils.DEBUG_LOG(TAG, "[registerReceiver]");
            mComposeSelectUsbDialogBroadcastReceiver = new ComposeSelectUsbDialogReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
            filter.addAction(Intent.ACTION_MEDIA_EJECT);
            filter.addDataScheme("file");
            mCtx.registerReceiver(mComposeSelectUsbDialogBroadcastReceiver, filter);
        }
    }
    class ComposeSelectUsbDialogReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Utils.DEBUG_LOG(TAG, "[onReceive] ComposeSelectUsbDialogReceiver : " + intent);
            final String action = intent.getAction();

            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                Utils.DEBUG_LOG(TAG, "[onReceive] ACTION_MEDIA_MOUNTED");

                mUsbPath = Utils.findUSBStorage();
                mAvailCnt = Utils.getUSBAvailCnt();
                String textStr = getString(R.string.selectusbstorage);
                if(mAvailCnt == 0) {
                    textStr = getString(R.string.pleasepluginusb);
                }
                mtextView.setText(textStr);
                initBtn();
            }
            else if(action.equals(Intent.ACTION_MEDIA_EJECT) || action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                Utils.DEBUG_LOG(TAG, "[onReceive] ACTION_MEDIA_EJECT || ACTION_MEDIA_UNMOUNTED");

                mUsbPath = Utils.findUSBStorage();
                mAvailCnt = Utils.getUSBAvailCnt();

                String textStr = getString(R.string.selectusbstorage);
                if(mAvailCnt == 0) {
                    textStr = getString(R.string.pleasepluginusb);
                }
                mtextView.setText(textStr);
                initBtn();
            }
        }
    }
}
