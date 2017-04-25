package com.tpv.app.pdf.Compose;

import android.app.Fragment;
import android.content.Context;
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
public class ComposeSelectStorageDialog extends Fragment {
    private static final String TAG = ComposeSelectStorageDialog.class.getName();
    private Context mCtx;

    private TextView mtextView;

    private Button mBtn_Internal;
    private Button mBtn_Usb;
    private Button mBtn_Sdcard;
    private int mBtn_focusid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.frag_select_storage, null);
        mCtx = vg.getContext();

        mtextView = (TextView)vg.findViewById(R.id.dialog_text);
        String textStr = getString(R.string.selectfilefrom);
        mtextView.setText(textStr);

        initBtn(vg);

        return vg;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initBtn(View v) {
        mBtn_Internal = (Button) v.findViewById(R.id.dialog_btn_internal);
        if (mBtn_Internal != null) {
            mBtn_Internal.setOnClickListener(BtnClick);
            mBtn_Internal.requestFocus();
        }

        mBtn_Usb = (Button) v.findViewById(R.id.dialog_btn_usb);
        if (mBtn_Usb != null) {
            mBtn_Usb.setOnClickListener(BtnClick);
            //mBtn_Usb.requestFocus();
        }

        mBtn_Sdcard = (Button) v.findViewById(R.id.dialog_btn_sdcard);
        if (mBtn_Sdcard != null) {
            mBtn_Sdcard.setOnClickListener(BtnClick);
            //mBtn_Sdcard.requestFocus();
        }
    }

    private BtnClickListener BtnClick = new BtnClickListener();
    class BtnClickListener implements View.OnClickListener {
        public void onClick(View v) {
            mBtn_focusid = v.getId();
            switch (mBtn_focusid) {
                case R.id.dialog_btn_internal:
                    Utils.PDFPlayerContentProvider_Set_PlaylistStorageSettings(Utils.storageType.STORAGE_INTERNAL);
                    break;
                case R.id.dialog_btn_usb:
                    Utils.PDFPlayerContentProvider_Set_PlaylistStorageSettings(Utils.storageType.STORAGE_USB);
                    break;
                case R.id.dialog_btn_sdcard:
                    Utils.PDFPlayerContentProvider_Set_PlaylistStorageSettings(Utils.storageType.STORAGE_SDCARD);
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
}
