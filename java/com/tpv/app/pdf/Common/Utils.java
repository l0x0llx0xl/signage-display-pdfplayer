package com.tpv.app.pdf.Common;


import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.tpv.app.pdf.Import.ImportFile;
import com.tpv.app.pdf.Play.PlayFullScreenFragment;
import com.tpv.app.pdf.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Andy.Hsu on 2015/7/8.
 */
public class Utils {
    private static final String TAG = Utils.class.getName();

    private static final boolean DEBUG = true;
    public static final boolean DEBUG_DELETE_LOGFILE = true;

    public static final boolean GET_FILETYPE_BY_COMMAND = true;

    public static final boolean SUPPORT_SCALAR_SERVICE = false;
    public static final boolean SUPPORT_MAIL_SERVICE = true;
    public static final boolean SUPPORT_TOUCH = true;

    public static final int MAX_FILES = 500;

    private static Context mCtx;
    private static String mLogFilePath;
    public static String mDefaultFilePath;

    public static boolean mIsUnregisterReceiver;

    public static DisplayMetrics sDm;
    public static FragmentManager mFragMamager;

    public static boolean mIsOptionback = false;
    public static boolean mIsPlayOrInfoClick;
    public static boolean mIsOptionsClick;
    public static int mListIndex;
    public static enum listtype{
        STORAGE_LIST,
        TEMP_LIST
    }
    public static listtype mFromListType;

    //region intent parameters
    public static boolean mIsDirectPlayback;
    public static int mDirectPlaybackListNumber;
    public static boolean mIsResetResumeFile;
    public static boolean mIsStartFromSettngs;

    public static final String EXTRA_KEY_SOURCE_FROM = "SOURCE_FROM";
    public static final String EXTRA_VALUE_SOURCE_SCALAR = "SCALAR";
    public static final String EXTRA_VALUE_SOURCE_SETTINGS = "SETTINGS";
    public static final String EXTRA_VALUE_SOURCE_MUPDF = "MUPDF";

    public static final String EXTRA_KEY_SOURCE_ACTION = "ACTION";
    public static final String EXTRA_VALUE_SOURCE_FINISH = "FINISH";

    public static final String EXTRA_KEY_PLAYLIST_NUMBER = "PLAYLIST_NUMBER";

    public static final String EXTRA_KEY_OPENPAGE = "OPEN_PAGE";
    public static final String EXTRA_VALUE_OPENPAGE_COMPOSE_HOME = "COMPOSE_HOME";
    public static final String EXTRA_VALUE_OPENPAGE_SETTINGS_HOME = "SETTINGS_HOME";

    public static final String INTENT_SCALARSERVICE_POWERSAVING_MODE = "com.tpv.ScalarService.powerSavingMode";
    public static final String INTENT_SCALARSERVICE_CHANGE_SOURCE = "com.tpv.ScalarService.changeSource";

    public static final String INTENT_WEBCONTROLSERVICE_CALLBACK = "com.tpv.WebControlService";

    public static final String INTENT_MAILSERVICE_ACTION = "MailService.Mail.SendEmail";
    public static final String INTENT_PDFPLAYER_MAIL_CALLBACK = "PdfPlayer.SendMail.Complete";
    //endregion

    //region contentprovide parameters
    public static final String AUTHORITY = "com.tpv.app.pdf";
    //public static final String AUTHORITY = "settings";

    public static final String TABLE1 = "pdf_playlist_1";
    public static final String TABLE2 = "pdf_playlist_2";
    public static final String TABLE3 = "pdf_playlist_3";
    public static final String TABLE4 = "pdf_playlist_4";
    public static final String TABLE5 = "pdf_playlist_5";
    public static final String TABLE6 = "pdf_playlist_6";
    public static final String TABLE7 = "pdf_playlist_7";
    public static final String TABLE_STYLE = "pdf_playlist_style";
    public static final String TABLE_SETTINGS = "pdf_playlist_settings";
    public static final String TABLE_NORMAL_FINISH = "pdf_normal_finish";
    public static final String TABLE1_URL = "content://" + AUTHORITY + "/" + TABLE1;
    public static final String TABLE2_URL = "content://" + AUTHORITY + "/" + TABLE2;
    public static final String TABLE3_URL = "content://" + AUTHORITY + "/" + TABLE3;
    public static final String TABLE4_URL = "content://" + AUTHORITY + "/" + TABLE4;
    public static final String TABLE5_URL = "content://" + AUTHORITY + "/" + TABLE5;
    public static final String TABLE6_URL = "content://" + AUTHORITY + "/" + TABLE6;
    public static final String TABLE7_URL = "content://" + AUTHORITY + "/" + TABLE7;
    public static final String TABLE_STYLE_URL = "content://" + AUTHORITY + "/" + TABLE_STYLE;
    public static final String TABLE_SETTINGS_URL = "content://" + AUTHORITY + "/" + TABLE_SETTINGS;
    public static final String TABLE_NORMAL_FINISH_URL = "content://" + AUTHORITY + "/" + TABLE_NORMAL_FINISH;
    public static final Uri TABLE1_CONTENT_URI = Uri.parse(TABLE1_URL);
    public static final Uri TABLE2_CONTENT_URI = Uri.parse(TABLE2_URL);
    public static final Uri TABLE3_CONTENT_URI = Uri.parse(TABLE3_URL);
    public static final Uri TABLE4_CONTENT_URI = Uri.parse(TABLE4_URL);
    public static final Uri TABLE5_CONTENT_URI = Uri.parse(TABLE5_URL);
    public static final Uri TABLE6_CONTENT_URI = Uri.parse(TABLE6_URL);
    public static final Uri TABLE7_CONTENT_URI = Uri.parse(TABLE7_URL);
    public static final Uri TABLE_STYLE_CONTENT_URI = Uri.parse(TABLE_STYLE_URL);
    public static final Uri TABLE_SETTINGS_CONTENT_URI = Uri.parse(TABLE_SETTINGS_URL);
    public static final Uri TABLE_NORMAL_FINISH_CONTENT_URI = Uri.parse(TABLE_NORMAL_FINISH_URL);

    public static final String _ID = "_id";
    public static final String FILE_NAME = "file_name";
    public static final String FILE_PATH = "file_path";
    public static final String IS_FILE = "is_file";
    public static final String IS_RESUME = "is_resume";
    public static final String IS_SELECTED = "is_selected";

    public static final String PLAYLIST_NO = "playlist_no";
    public static final String STYLE = "style";

    public static final String STORAGE = "storage";
    public static final String REPEAT_MODE = "repeat_mode";
    public static final String PHOTO_SLIDESHOW_PERIOD = "effect_duration";

    public static final String IS_NORMAL = "is_normal";
    //endregion

    //region play list menu
    public static enum playlistMenu{
        PLAYLIST_1,
        PLAYLIST_2,
        PLAYLIST_3,
        PLAYLIST_4,
        PLAYLIST_5,
        PLAYLIST_6,
        PLAYLIST_7,
        PLAYLIST_COUNT
    }
    public static enum playlistStyle{
        PLAYLIST_NO_DATA,
        PLAYLIST_PDF,
        PLAYLIST_OTHER
    }
    public static int mPlayListSelection = -1;   // 0 ~ 6 : 0=Playlist 1, 1=Playlist 2
    //endregion

    //public static int mPlaylistNo;   // 0 ~ 6 : 0=Playlist 1, 1=Playlist 2

    //region storage Type
    public enum storageType{
        STORAGE_INTERNAL,
        STORAGE_USB,
        STORAGE_SDCARD
    }
    //endregion

    //region File Type
    public enum fileType{
        FILE_PDF,
        NONE
    }
    //public static String FILE_FOLDER_PUBLIC = Build.BRAND.toLowerCase();
    public static String FILE_FOLDER_PDF = "pdf";
    public static String FILE_FOLDER_DEFAULT = "default/pdf/";
    //endregion

    //region repeat mode
    public enum repeatMode {
        REPEAT_ONCE,
        REPEAT_ALL,
        REPEAT_NONE
    }
    //endregion

    //region effect duration
    public enum effectDuration {
        PERIOD_5(5),
        PERIOD_10(10),
        PERIOD_15(15),
        PERIOD_20(20);

        private int value;

        private effectDuration(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    //endregion

    //region File Extensions
    private static String[] mPdfFileExtensions =
            new String[] {"pdf"};
    //endregion

    public static void DEBUG_LOG(String tag, String msg) {
        if(DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static boolean init(Context ctx) {
        DEBUG_LOG(TAG, "init : " + ctx);
        if(ctx == null) return false;
        mCtx = ctx;

        mLogFilePath = getInternalStoragePath();

        setDirectPlayback(false);
        //mTypeface1 = Typeface.createFromAsset(mCtx.getAssets(), "fonts/WCL-01.ttf");

        /** init log file class. */
        Utils.mUtilsLogFileDataInfo = new Utils_LogFileDataInfo();
        Utils.mUtilsLogFileDataInfo.mUtilsErrorFileList = new ArrayList<Utils_ErrorFileList>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        Utils.LOG_FILE_NAME_EXTENSION = Utils.LOG_FILE_NAME + currentDateandTime + Utils.LOG_FILE_EXTENSION;
        /** ~init log file class. */

        return true;
    }

    public static void setFragmanetManager(FragmentManager fragmamager) {
        mFragMamager = fragmamager;
    }
    public static FragmentManager getFragmanetManager() {
        return mFragMamager;
    }

    public static String getMimeType(String url)
    {
        String extension = url.substring(url.lastIndexOf("."));
        String mimeTypeMap = MimeTypeMap.getFileExtensionFromUrl(extension);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap);
        return mimeType;
    }

    public static int mPosition;
    private static List<Utils_PlayListDataInfo> mUtilsInfoDataTemp;
    public static void setInfoDataTemp(List<Utils_PlayListDataInfo> playlistdatainfotemp) {
        mUtilsInfoDataTemp = new ArrayList<Utils_PlayListDataInfo>(playlistdatainfotemp.size());
        for(Utils_PlayListDataInfo item: playlistdatainfotemp) mUtilsInfoDataTemp.add(item);
    }

    public static List<Utils_PlayListDataInfo> getInfoDataTemp() {
        return mUtilsInfoDataTemp;
    }
    public static void setInfoData(int position) {
        mPosition = position;
    }
    public static int getInfoDataItemPosition() {
        return mPosition;
    }

    public static String mPreviewFilePath;
    public static void setPreviewFilePath(String path) {
        mPreviewFilePath = path;
    }
    public static String getPreviewFilePath() {
        return mPreviewFilePath;
    }

    public static Utils_MusicDescription getMusicDescription(String filepath) {
        Utils_MusicDescription musicdes = null;

        File file = new File(filepath);
        if(file != null) {
            //if(isMusic(file.getName()))
            {
                Date lastModDate = new Date(file.lastModified());
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(file.getAbsolutePath());
                String bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
                String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                String mime = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
                String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); //ms

                musicdes = new Utils_MusicDescription(file.getName(),
                                                String.format("%d:%d",
                                                        TimeUnit.MILLISECONDS.toMinutes(Integer.parseInt(duration)),
                                                        TimeUnit.MILLISECONDS.toSeconds(Integer.parseInt(duration)) -
                                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Integer.parseInt(duration)))),
                                                (lastModDate != null)?lastModDate.toString():"",
                                                String.format("%.2fKB", ((float) file.length() / 1024)),
                                                (bitrate != null)?((Integer.parseInt(bitrate)/1000) + "kbps"):"",
                                                (title != null)?title:"",
                                                (album != null)?album:"",
                                                (mime != null)?mime:"",
                                                (artist != null)?artist:""
                                                );

                //byte[] embeddedPicture = retriever.getEmbeddedPicture();
                //DEBUG_LOG(TAG, "[getStorageFiles] [Music] embeddedPicture:" + embeddedPicture);
            }
        }

        return musicdes;
    }
    public static Utils_PhotoDescription getPhotoDescription(String filepath) {

        Utils_PhotoDescription photodes = null;
        File file = new File(filepath);
        if(file != null) {
            //if (isPhoto(file.getName()))
            {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

                Date lastModDate = new Date(file.lastModified());
                photodes = new Utils_PhotoDescription(file.getName(),
                                                (bitmap != null)?(bitmap.getWidth() + "x" + bitmap.getHeight()):"",
                                                (lastModDate != null)?lastModDate.toString():"",
                                                String.format("%.2fKB", ((float)file.length()/1024)),
                                                bitmap
                                                );
            }
        }

        return photodes;
    }
    public static Utils_VideoDescription getVideoDescription(String filepath) {
        Utils_VideoDescription videodes = null;

        File file = new File(filepath);
        if(file != null) {
            //if (isVideo(file.getName()))
            {
                Date lastModDate = new Date(file.lastModified());
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(file.getAbsolutePath());
                String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String mime = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
                String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); //ms
                String bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
                Bitmap bitmap = retriever.getFrameAtTime(195 * 1000000);

                videodes = new Utils_VideoDescription(file.getName(),
                                        (width + "x" + height),
                                        (duration != null)?String.format("%d:%d:%d ",
                                                            TimeUnit.MILLISECONDS.toHours(Integer.parseInt(duration)),
                                                            TimeUnit.MILLISECONDS.toMinutes(Integer.parseInt(duration)) -
                                                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(Integer.parseInt(duration))),
                                                            TimeUnit.MILLISECONDS.toSeconds(Integer.parseInt(duration)) -
                                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Integer.parseInt(duration))))
                                                            : "",
                                        (lastModDate != null)?lastModDate.toString():"",
                                        String.format("%.2fMB", ((float)file.length()/1024)/1024),
                                        (bitrate != null)?(String.valueOf(Integer.parseInt(bitrate)/1000) + "kbps") : "",
                                        (title != null)?title:"",
                                        (mime != null)?mime:"",
                                        (artist != null)?artist:"",
                                        bitmap
                                        );

                //byte[] embeddedPicture = retriever.getEmbeddedPicture();
                //DEBUG_LOG(TAG, "[getStorageFiles] [Video] embeddedPicture:" + embeddedPicture);

                //Bitmap bmp = retriever.getFrameAtTime(300 * 1000);
                //DEBUG_LOG(TAG, "[getStorageFiles] [Video] bmp.getHeight():" + bmp.getHeight());
                //DEBUG_LOG(TAG, "[getStorageFiles] [Video] bmp.getWidth():" + bmp.getWidth());
            }
        }

        return videodes;
    }

    public static boolean isPdf(String filename) {
        boolean ispdf = false;
        for (String extension : mPdfFileExtensions) {
            if (filename.toLowerCase().endsWith(extension)) {
                ispdf = true;
                break;
            }
        }
        return ispdf;
    }

    /**
     * Storage : STORAGE_INTERNAL
     * finalpath: /storage/emulated/0/
     */
    public static String getInternalStoragePath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }

        return sdDir.getAbsolutePath() + "/" + getFileFolderPublic() + "/";
    }

    /**
     * Storage : STORAGE_USB
     * finalpath: /mnt/usb*
     */
    public static String getUSBStoragePath() {
        String finalpath = "";
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            String[] patharray = new String[10];
            int i = 0;
            int available = 0;

            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                String mount = new String();
                //DEBUG_LOG(TAG, "line.toString(): " + line.toString());
                if (line.contains("secure")) continue;
                if (line.contains("asec")) continue;

                //if (line.contains("fat")) {
                if (line.contains("/mnt/usb")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        mount = mount.concat(columns[1]);

                        patharray[i] = mount;
                        i++;

                        // check directory is exist or not
                        File dir = new File(mount);
                        if (dir.exists() && dir.isDirectory()) {
                            // do something here

                            available = 1;
                            finalpath = mount;
                            break;
                        } else {

                        }
                    }
                }
            }
            if (available == 1) {

            } else if (available == 0) {
                finalpath = patharray[0];
            }

        } catch (Exception e) {

        }
        //DEBUG_LOG(TAG, "finalpath: " + finalpath);
        return finalpath + "/" + getFileFolderPublic() + "/";

        //return sdDir.getAbsolutePath() + "/";
    }

    /**
     * Storage : STORAGE_SDCARD
     * finalpath: /mnt/ext*
     */
    public static String getSDCardStoragePath() {
        //String finalpath = System.getenv("SECONDARY_STORAGE");
        //DEBUG_LOG(TAG, "finalpath: " + finalpath);
        //return finalpath + "/" + getFileFolderPublic() + "/";

        String finalpath = "";
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            String[] patharray = new String[10];
            int i = 0;
            int available = 0;

            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                String mount = new String();
                //DEBUG_LOG(TAG, "line.toString(): " + line.toString());
                if (line.contains("secure")) continue;
                if (line.contains("asec")) continue;

                //if (line.contains("fat")) {
                if (line.contains("/mnt/ext")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        mount = mount.concat(columns[1]);

                        patharray[i] = mount;
                        i++;

                        // check directory is exist or not
                        File dir = new File(mount);
                        if (dir.exists() && dir.isDirectory()) {
                            // do something here

                            available = 1;
                            finalpath = mount;
                            break;
                        } else {

                        }
                    }
                }
            }
            if (available == 1) {

            } else if (available == 0) {
                finalpath = patharray[0];
            }

        } catch (Exception e) {

        }
        //DEBUG_LOG(TAG, "finalpath: " + finalpath);
        return finalpath + "/" + getFileFolderPublic() + "/";
    }

    public static List<Utils_PlayListDataInfo> getStorageFiles(String storagePath, fileType filetype) {
        boolean isValid;
        List<Utils_PlayListDataInfo> storageplaylistdatainfo = new ArrayList<Utils_PlayListDataInfo>();
        Utils_PlayListDataInfo playlistdatainfotemp;

        //DEBUG_LOG(TAG, "storagePath: " + storagePath);
        File f = new File(storagePath);
        if(f != null) {
            File file[] = f.listFiles();
            if(file != null) {
                DEBUG_LOG(TAG, "Size: " + file.length);

                playlistdatainfotemp =
                        new Utils_PlayListDataInfo(mCtx.getString(R.string.dot),
                                mCtx.getString(R.string.returntotheprevious),
                                "false",
                                "false",
                                "false");

                storageplaylistdatainfo.add(playlistdatainfotemp);


                for (int i = 0; i < file.length; i++) {

                    //DEBUG_LOG(TAG, "FileName:" + file[i].getName());
                    //DEBUG_LOG(TAG, "getAbsolutePath:" + file[i].getAbsolutePath());
                    //DEBUG_LOG(TAG, "getPath:" + file[i].getPath());
                    //DEBUG_LOG(TAG, "isAbsolute:" + file[i].isAbsolute());
                    //DEBUG_LOG(TAG, "isDirectory:" + file[i].isDirectory());
                    //DEBUG_LOG(TAG, "isFile:" + file[i].isFile());

                    isValid = false;
                    if (file[i].isFile()) {
                        if (filetype == fileType.FILE_PDF) {
                            if (isPdf(file[i].getName())) {
                                isValid = true;
                            }
                        } else {
                            if (isPdf(file[i].getName())) {
                                isValid = true;
                            }
                        }

                        if (isValid) {
                            playlistdatainfotemp =
                                    new Utils_PlayListDataInfo(file[i].getName(),
                                            file[i].getAbsolutePath(),
                                            "true",
                                            "false",
                                            "false");

                            storageplaylistdatainfo.add(playlistdatainfotemp);
                        }
                    }
                }
            }
        }
        return storageplaylistdatainfo;
    }

    private static List<Utils_PlayListDataInfo> mUtilsUtilsPlayListDataInfoTemp;
    public static void setPlayListDataInfoTemp(List<Utils_PlayListDataInfo> playlistdatainfotemp) {
        mUtilsUtilsPlayListDataInfoTemp = new ArrayList<Utils_PlayListDataInfo>(playlistdatainfotemp.size());
        for(Utils_PlayListDataInfo item: playlistdatainfotemp) mUtilsUtilsPlayListDataInfoTemp.add(item);
    }

    public static List<Utils_PlayListDataInfo> getPlayListDataInfoTemp() {
        return mUtilsUtilsPlayListDataInfoTemp;
    }

    public static List<Utils_PlayListMenuInfo> addPlayListForLoad() {

        List<Utils_PlayListMenuInfo> PlayListLoadMenuInfo = new ArrayList<Utils_PlayListMenuInfo>();

        Utils_PlayListMenuInfo playlistmenuinfotemp =
                new Utils_PlayListMenuInfo(R.drawable.list_icon,
                        mCtx.getString(R.string.file1),
                        PDFPlayerContentProvider_Get_PlaylistStyle(0));
        PlayListLoadMenuInfo.add(playlistmenuinfotemp);

        playlistmenuinfotemp =
                new Utils_PlayListMenuInfo(R.drawable.list_icon,
                        mCtx.getString(R.string.file2),
                        PDFPlayerContentProvider_Get_PlaylistStyle(1));
        PlayListLoadMenuInfo.add(playlistmenuinfotemp);

        playlistmenuinfotemp =
                new Utils_PlayListMenuInfo(R.drawable.list_icon,
                        mCtx.getString(R.string.file3),
                        PDFPlayerContentProvider_Get_PlaylistStyle(2));
        PlayListLoadMenuInfo.add(playlistmenuinfotemp);

        playlistmenuinfotemp =
                new Utils_PlayListMenuInfo(R.drawable.list_icon,
                        mCtx.getString(R.string.file4),
                        PDFPlayerContentProvider_Get_PlaylistStyle(3));
        PlayListLoadMenuInfo.add(playlistmenuinfotemp);

        playlistmenuinfotemp =
                new Utils_PlayListMenuInfo(R.drawable.list_icon,
                        mCtx.getString(R.string.file5),
                        PDFPlayerContentProvider_Get_PlaylistStyle(4));
        PlayListLoadMenuInfo.add(playlistmenuinfotemp);

        playlistmenuinfotemp =
                new Utils_PlayListMenuInfo(R.drawable.list_icon,
                        mCtx.getString(R.string.file6),
                        PDFPlayerContentProvider_Get_PlaylistStyle(5));
        PlayListLoadMenuInfo.add(playlistmenuinfotemp);


        playlistmenuinfotemp =
                new Utils_PlayListMenuInfo(R.drawable.list_icon,
                        mCtx.getString(R.string.file7),
                        PDFPlayerContentProvider_Get_PlaylistStyle(6));
        PlayListLoadMenuInfo.add(playlistmenuinfotemp);

        return PlayListLoadMenuInfo;
    }
    public static void setDirectPlaybackListNumber(int number) {
        mDirectPlaybackListNumber = number;
    }
    public static int getDirectPlaybackListNumber() {
        return mDirectPlaybackListNumber;
    }
    public static void setDirectPlayback(boolean isdirect) {
        mIsDirectPlayback = isdirect;
    }
    public static boolean isDirectPlayback() {
        return mIsDirectPlayback;
    }
    public static boolean GotoFullScreenPlayback(int listNo) {
        DEBUG_LOG(TAG, "[GotoFullScreenPlayback] listNo: " + listNo);

        boolean isPlaylistValid = true;
        List<Utils_PlayListDataInfo> playlistdatainfo = Utils.PDFPlayerContentProvider_Get_Playlist(Utils.mPlayListSelection);
        if(playlistdatainfo == null || playlistdatainfo.size() == 0) {
            Utils.mDefaultFilePath = Utils.getInternalStoragePath() + Utils.FILE_FOLDER_DEFAULT;
            playlistdatainfo = Utils.getStorageFiles(Utils.mDefaultFilePath, Utils.fileType.NONE);
            if(playlistdatainfo == null || playlistdatainfo.size() == 0) {
                isPlaylistValid = false;
            }
        }

        if(isPlaylistValid) {
            if (isDirectPlayback()) {
                DEBUG_LOG(TAG, "[GotoFullScreenPlayback] Direct Playback");
                setDirectPlayback(false);
                DEBUG_LOG(TAG, "[GotoFullScreenPlayback] remove: " + getFragmanetManager().findFragmentByTag("PlayFullScreenFragmentTag"));
                if (getFragmanetManager().findFragmentByTag("PlayFullScreenFragmentTag") == null) {
                    getFragmanetManager().beginTransaction()
                            .replace(R.id.main_container, new PlayFullScreenFragment(), "PlayFullScreenFragmentTag")
                            .commit();
                } else {
                    getFragmanetManager().beginTransaction()
                            .replace(R.id.main_container, new PlayFullScreenFragment(), "PlayFullScreenFragmentTag")
                            .remove(getFragmanetManager().findFragmentByTag("PlayFullScreenFragmentTag"))
                            .commit();
                }
            } else {
                DEBUG_LOG(TAG, "[GotoFullScreenPlayback] Not Direct Playback");
                getFragmanetManager().beginTransaction()
                        .replace(R.id.main_container, new PlayFullScreenFragment(), "PlayFullScreenFragmentTag")
                        .addToBackStack(null)
                        .commit();
            }
        }
        else {
            setDirectPlayback(false);
            NotifyScalarServiceFailOver(listNo+1);
            Utils.Toast(mCtx, mCtx.getString(R.string.nodatainplaylist));
        }

        return isPlaylistValid;
    }

    public static void resetResumeFile() {
        DEBUG_LOG(TAG, "resetResumeFile");
        /** reset resume file. */
        List<Utils_PlayListDataInfo> playlistdatainfo = PDFPlayerContentProvider_Get_Playlist(mPlayListSelection);
        for(int i=0; i<playlistdatainfo.size(); i++) {
            if(playlistdatainfo.get(i).isResume().equals("true")) {
                playlistdatainfo.get(i).setResume("false");
            }
        }
        int playlistnumber = mPlayListSelection;
        PDFPlayerContentProvider_Set_Playlist(playlistnumber, playlistdatainfo);
    }

    public static int findResumeFileIndex(List<Utils_PlayListDataInfo> playlistdatainfo) {
        int resumefileIndex = 0;
        for(int i=0; i< playlistdatainfo.size(); i++) {
            if(playlistdatainfo.get(i).isResume().equals("true")) {
                resumefileIndex = i;
                break;
            }
        }

        return resumefileIndex;
    }

    public static void setNormalFinishPdfplayer(String isNormal) {
        PDFPlayerContentProvider_Set_NormalFinishPDFplayer(isNormal);
    }

    public static boolean getNormalFinishPdfplayer() {
        mIsResetResumeFile = false;
        if(PDFPlayerContentProvider_Get_NormalFinishPDFplayer().equals("true"))
            mIsResetResumeFile = true;
        return mIsResetResumeFile;
    }

//region contentprovider
    public static void PDFPlayerContentProvider_Set_NormalFinishPDFplayer(String isNormal) {
        DEBUG_LOG(TAG, "isNormal : " + isNormal);
        ContentValues values = new ContentValues();
        values.put(IS_NORMAL, isNormal);
        int uriUpdate = mCtx.getContentResolver().update(TABLE_NORMAL_FINISH_CONTENT_URI,
                values,
                (_ID + "=1"),
                null);
        DEBUG_LOG(TAG, "uriUpdate : " + uriUpdate);
        if(uriUpdate == 0) {
            Uri uriInsert = mCtx.getContentResolver().insert(TABLE_NORMAL_FINISH_CONTENT_URI, values);
        }
    }

    public static String PDFPlayerContentProvider_Get_NormalFinishPDFplayer() {

        String isnormal = "false";

        Cursor cursor = mCtx.getContentResolver().query(TABLE_NORMAL_FINISH_CONTENT_URI,
                new String[]{IS_NORMAL},
                (_ID + "=1"),
                null,
                null);

        if(cursor == null) {
            DEBUG_LOG(TAG, "cursor == null");
        }
        else {
            //DEBUG_LOG(TAG, "cursor.getCount() : " + cursor.getCount());
            while (cursor.moveToNext()) {
                if(cursor.getColumnIndex(IS_NORMAL) != -1) {
                    if(cursor.getString(cursor.getColumnIndex(IS_NORMAL)) != null) {
                        DEBUG_LOG(TAG, "isNormal : " + cursor.getString(cursor.getColumnIndex(IS_NORMAL)));
                        isnormal = cursor.getString(cursor.getColumnIndex(IS_NORMAL));
                    }
                }
            }
            cursor.close();
        }

        return isnormal;
    }

    public static void PDFPlayerContentProvider_Set_Playlist(int playlistNo, List<Utils_PlayListDataInfo> playlistdatainfotemp) {
        String importfilepath = ImportFile.IMPORT_FILE_STORAGE
                + ImportFile.IMPORT_FILE_NAME + (playlistNo+1)
                + ImportFile.IMPORT_FILE_EXTENSIONS;
        String contents = ImportFile.readListFile(importfilepath);
        if(!contents.isEmpty()) {
            return;
        }
        else {
            Uri tableContentUri = TABLE1_CONTENT_URI;
            playlistMenu playlistmenuNo = playlistMenu.values()[playlistNo];
            switch (playlistmenuNo) {
                case PLAYLIST_1:
                    tableContentUri = TABLE1_CONTENT_URI;
                    break;
                case PLAYLIST_2:
                    tableContentUri = TABLE2_CONTENT_URI;
                    break;
                case PLAYLIST_3:
                    tableContentUri = TABLE3_CONTENT_URI;
                    break;
                case PLAYLIST_4:
                    tableContentUri = TABLE4_CONTENT_URI;
                    break;
                case PLAYLIST_5:
                    tableContentUri = TABLE5_CONTENT_URI;
                    break;
                case PLAYLIST_6:
                    tableContentUri = TABLE6_CONTENT_URI;
                    break;
                case PLAYLIST_7:
                    tableContentUri = TABLE7_CONTENT_URI;
                    break;

            }
            int uriDelete = mCtx.getContentResolver().delete(tableContentUri, null, null);

            for (Utils_PlayListDataInfo item : playlistdatainfotemp) {
                //DEBUG_LOG(TAG, "item.getFileName() : " +item.getFileName());
                //DEBUG_LOG(TAG, "item.getFilePath() : " +item.getFilePath());
                //DEBUG_LOG(TAG, "item.isFile() : " +item.isFile());
                //DEBUG_LOG(TAG, "item.isResume() : " +item.isResume());
                //DEBUG_LOG(TAG, "item.isSelected() : " +item.isSelected());
                ContentValues values = new ContentValues();
                values.put(FILE_NAME, item.getFileName());
                values.put(FILE_PATH, item.getFilePath());
                values.put(IS_FILE, item.isFile());
                values.put(IS_RESUME, "false");
                values.put(IS_SELECTED, item.isSelected());
                Uri uriInsert = mCtx.getContentResolver().insert(tableContentUri, values);
            }
        }
    }

    public static List<Utils_PlayListDataInfo> PDFPlayerContentProvider_Get_Playlist(int playlistNo) {

        String importfilepath = ImportFile.IMPORT_FILE_STORAGE
                + ImportFile.IMPORT_FILE_NAME + (playlistNo + 1)
                + ImportFile.IMPORT_FILE_EXTENSIONS;
        String contents = ImportFile.readListFile(importfilepath);
        if(!contents.isEmpty()) {
            return ImportFile.loadImportFileToPlaylist(contents);
        }
        else {
            List<Utils_PlayListDataInfo> queryplaylistdatainfo = new ArrayList<Utils_PlayListDataInfo>();
            Uri tableContentUri = TABLE1_CONTENT_URI;
            playlistMenu playlistmenuNo = playlistMenu.values()[playlistNo];
            switch (playlistmenuNo) {
                case PLAYLIST_1:
                    tableContentUri = TABLE1_CONTENT_URI;
                    break;
                case PLAYLIST_2:
                    tableContentUri = TABLE2_CONTENT_URI;
                    break;
                case PLAYLIST_3:
                    tableContentUri = TABLE3_CONTENT_URI;
                    break;
                case PLAYLIST_4:
                    tableContentUri = TABLE4_CONTENT_URI;
                    break;
                case PLAYLIST_5:
                    tableContentUri = TABLE5_CONTENT_URI;
                    break;
                case PLAYLIST_6:
                    tableContentUri = TABLE6_CONTENT_URI;
                    break;
                case PLAYLIST_7:
                    tableContentUri = TABLE7_CONTENT_URI;
                    break;

            }

            Cursor cursor = null;
            if (mCtx != null) {
                cursor = mCtx.getContentResolver().query(tableContentUri,
                        new String[]{FILE_NAME,
                                FILE_PATH,
                                IS_FILE,
                                IS_RESUME,
                                IS_SELECTED},
                        null, null, null);
            }

            if (cursor == null) {
                DEBUG_LOG(TAG, "cursor == null");
            } else {
                //DEBUG_LOG("ContentProviderClient", "cursor.moveToNext() : " + cursor.moveToNext());
                //DEBUG_LOG(TAG, "cursor.getCount() : " + cursor.getCount());
                while (cursor.moveToNext()) {
                    Utils_PlayListDataInfo playlistdatainfotemp =
                            new Utils_PlayListDataInfo(cursor.getString(cursor.getColumnIndex(FILE_NAME)),
                                    cursor.getString(cursor.getColumnIndex(FILE_PATH)),
                                    cursor.getString(cursor.getColumnIndex(IS_FILE)),
                                    cursor.getString(cursor.getColumnIndex(IS_RESUME)),
                                    cursor.getString(cursor.getColumnIndex(IS_SELECTED)));

                    //DEBUG_LOG(TAG, "fileName : " + cursor.getString(cursor.getColumnIndex(FILE_NAME)));
                    //DEBUG_LOG(TAG, "filePath : " + cursor.getString(cursor.getColumnIndex(FILE_PATH)));
                    //DEBUG_LOG(TAG, "isFile : " + cursor.getString(cursor.getColumnIndex(IS_FILE)));
                    //DEBUG_LOG(TAG, "isResume : " + cursor.getString(cursor.getColumnIndex(IS_RESUME)));
                    //DEBUG_LOG(TAG, "isSelected : " + cursor.getString(cursor.getColumnIndex(IS_SELECTED)));
                    queryplaylistdatainfo.add(playlistdatainfotemp);
                }
                cursor.close();
            }
            return queryplaylistdatainfo;
        }
    }

    public static void PDFPlayerContentProvider_Set_PlaylistStyle(int playlistNo, playlistStyle playliststyle) {
        String importfilepath = ImportFile.IMPORT_FILE_STORAGE
                + ImportFile.IMPORT_FILE_NAME + (playlistNo+1)
                + ImportFile.IMPORT_FILE_EXTENSIONS;
        String contents = ImportFile.readListFile(importfilepath);
        if(!contents.isEmpty()) {
            return;
        }
        else {
            //DEBUG_LOG(TAG, "String.valueOf(playliststyle) : " + String.valueOf(playliststyle));
            ContentValues values = new ContentValues();
            values.put(PLAYLIST_NO, String.valueOf(playlistNo + 1));
            values.put(STYLE, String.valueOf(playliststyle));
            int uriUpdate = mCtx.getContentResolver().update(TABLE_STYLE_CONTENT_URI,
                    values,
                    (PLAYLIST_NO + "=?"),
                    new String[]{String.valueOf(playlistNo + 1)});
            //DEBUG_LOG(TAG, "uriUpdate : " + uriUpdate);
            if (uriUpdate == 0) {
                Uri uriInsert = mCtx.getContentResolver().insert(TABLE_STYLE_CONTENT_URI, values);
            }
        }
    }

    public static playlistStyle PDFPlayerContentProvider_Get_PlaylistStyle(int playlistNo) {
        String importfilepath = ImportFile.IMPORT_FILE_STORAGE
                + ImportFile.IMPORT_FILE_NAME + (playlistNo+1)
                + ImportFile.IMPORT_FILE_EXTENSIONS;
        String contents = ImportFile.readListFile(importfilepath);
        if(!contents.isEmpty()) {
            List<Utils_PlayListDataInfo> importplaylist = ImportFile.loadImportFileToPlaylist(contents);
            playlistStyle style = checkPlaylistStyle(importplaylist);
            //DEBUG_LOG(TAG, "++MediaPlayerContentProvider_Get_PlaylistStyle, style : " + style);
            return style;
        }
        else {
            playlistStyle style = playlistStyle.PLAYLIST_NO_DATA;

            Cursor cursor = mCtx.getContentResolver().query(TABLE_STYLE_CONTENT_URI,
                    new String[]{PLAYLIST_NO, STYLE},
                    (PLAYLIST_NO + "=?"),
                    new String[]{String.valueOf(playlistNo + 1)},
                    null);

            if (cursor == null) {
                DEBUG_LOG(TAG, "cursor == null");
            } else {
                //DEBUG_LOG(TAG, "cursor.getCount() : " + cursor.getCount());
                while (cursor.moveToNext()) {
                    //DEBUG_LOG(TAG, "playlistNo : " + cursor.getString(cursor.getColumnIndex(PLAYLIST_NO)));
                    //DEBUG_LOG(TAG, "style : " + cursor.getString(cursor.getColumnIndex(STYLE)));
                    style = playlistStyle.valueOf(cursor.getString(cursor.getColumnIndex(STYLE)));
                }
                cursor.close();
            }

            return style;
        }
    }

    public static void PDFPlayerContentProvider_Set_PlaylistStorageSettings(storageType storage) {
        DEBUG_LOG(TAG, "String.valueOf(storage) : " + String.valueOf(storage));
        ContentValues values = new ContentValues();
        //values.put("playlistNo", String.valueOf(playlistNo));
        values.put(STORAGE, String.valueOf(storage));
        int uriUpdate = mCtx.getContentResolver().update(TABLE_SETTINGS_CONTENT_URI,
                values,
                (_ID + "=1"),
                null);
        //DEBUG_LOG(TAG, "uriUpdate : " + uriUpdate);
        if(uriUpdate == 0) {
            Uri uriInsert = mCtx.getContentResolver().insert(TABLE_SETTINGS_CONTENT_URI, values);
        }
    }


    public static storageType PDFPlayerContentProvider_Get_PlaylistStorageSettings() {

        storageType storagetype = storageType.STORAGE_INTERNAL;

        Cursor cursor = mCtx.getContentResolver().query(TABLE_SETTINGS_CONTENT_URI,
                new String[]{STORAGE},
                (_ID + "=1"),
                null,
                null);

        if(cursor == null) {
            DEBUG_LOG(TAG, "cursor == null");
        }
        else {
            //DEBUG_LOG(TAG, "cursor.getCount() : " + cursor.getCount());
            while (cursor.moveToNext()) {
                if(cursor.getColumnIndex(STORAGE) != -1) {
                    if(cursor.getString(cursor.getColumnIndex(STORAGE)) != null) {
                        DEBUG_LOG(TAG, "Storage : " + cursor.getString(cursor.getColumnIndex(STORAGE)));
                        storagetype = storageType.valueOf(cursor.getString(cursor.getColumnIndex(STORAGE)));
                    }
                }
            }
            cursor.close();
        }

        return storagetype;
    }

    public static void PDFPlayerContentProvider_Set_PlaylistRepeatModeSettings(repeatMode type) {
        //DEBUG_LOG(TAG, "String.valueOf(type) : " + String.valueOf(type));
        ContentValues values = new ContentValues();
        values.put(REPEAT_MODE, String.valueOf(type));
        int uriUpdate = mCtx.getContentResolver().update(TABLE_SETTINGS_CONTENT_URI,
                values,
                (_ID + "=1"),
                null);
        //DEBUG_LOG(TAG, "uriUpdate : " + uriUpdate);
        if(uriUpdate == 0) {
            Uri uriInsert = mCtx.getContentResolver().insert(TABLE_SETTINGS_CONTENT_URI, values);
        }
    }

    public static repeatMode PDFPlayerContentProvider_Get_PlaylistRepeatModeSettings() {

        repeatMode type = repeatMode.REPEAT_ALL;

        Cursor cursor = mCtx.getContentResolver().query(TABLE_SETTINGS_CONTENT_URI,
                new String[]{REPEAT_MODE},
                null,
                null,
                null);

        if(cursor == null) {
            DEBUG_LOG(TAG, "cursor == null");
        }
        else {
            //DEBUG_LOG(TAG, "cursor.getCount() : " + cursor.getCount());
            while (cursor.moveToNext()) {
                if(cursor.getColumnIndex(REPEAT_MODE) != -1) {
                    if(cursor.getString(cursor.getColumnIndex(REPEAT_MODE)) != null) {
                        DEBUG_LOG(TAG, "RepeatMode : " + cursor.getString(cursor.getColumnIndex(REPEAT_MODE)));
                        type = repeatMode.valueOf(cursor.getString(cursor.getColumnIndex(REPEAT_MODE)));
                    }
                }
            }
            cursor.close();
        }

        return type;
    }

    public static void PDFPlayerContentProvider_Set_EffectDuration(effectDuration type) {
        //DEBUG_LOG(TAG, "String.valueOf(type) : " + String.valueOf(type));
        ContentValues values = new ContentValues();
        values.put(PHOTO_SLIDESHOW_PERIOD, String.valueOf(type));
        int uriUpdate = mCtx.getContentResolver().update(TABLE_SETTINGS_CONTENT_URI,
                values,
                (_ID + "=1"),
                null);
        //DEBUG_LOG(TAG, "uriUpdate : " + uriUpdate);
        if(uriUpdate == 0) {
            Uri uriInsert = mCtx.getContentResolver().insert(TABLE_SETTINGS_CONTENT_URI, values);
        }
    }


    public static effectDuration PDFPlayerContentProvider_Get_EffectDuration() {

        effectDuration type = effectDuration.PERIOD_5;

        Cursor cursor = mCtx.getContentResolver().query(TABLE_SETTINGS_CONTENT_URI,
                new String[]{PHOTO_SLIDESHOW_PERIOD},
                null,
                null,
                null);

        if(cursor == null) {
            DEBUG_LOG(TAG, "cursor == null");
        }
        else {
            //DEBUG_LOG(TAG, "cursor.getCount() : " + cursor.getCount());
            while (cursor.moveToNext()) {
                if(cursor.getColumnIndex(PHOTO_SLIDESHOW_PERIOD) != -1) {
                    if(cursor.getString(cursor.getColumnIndex(PHOTO_SLIDESHOW_PERIOD)) != null) {
                        //DEBUG_LOG(TAG, "PhotoSlideshowPeriod : " + cursor.getString(cursor.getColumnIndex(PHOTO_SLIDESHOW_PERIOD)));
                        type = effectDuration.valueOf(cursor.getString(cursor.getColumnIndex(PHOTO_SLIDESHOW_PERIOD)));
                    }
                }
            }
            cursor.close();
        }

        return type;
    }

    public static void PDFPlayerContentProvider_Set_PlaylistResumeFile(int playlistNo, String filename, String isresume) {
        Uri tableContentUri = TABLE1_CONTENT_URI;
        playlistMenu playlistmenuNo = playlistMenu.values()[playlistNo];
        switch(playlistmenuNo) {
            case PLAYLIST_1:
                tableContentUri = TABLE1_CONTENT_URI;
                break;
            case PLAYLIST_2:
                tableContentUri = TABLE2_CONTENT_URI;
                break;
            case PLAYLIST_3:
                tableContentUri = TABLE3_CONTENT_URI;
                break;
            case PLAYLIST_4:
                tableContentUri = TABLE4_CONTENT_URI;
                break;
            case PLAYLIST_5:
                tableContentUri = TABLE5_CONTENT_URI;
                break;
            case PLAYLIST_6:
                tableContentUri = TABLE6_CONTENT_URI;
                break;
            case PLAYLIST_7:
                tableContentUri = TABLE7_CONTENT_URI;
                break;

        }

        //DEBUG_LOG(TAG,"============================");
        //DEBUG_LOG(TAG,"playlistNo : " + playlistNo);
        //DEBUG_LOG(TAG,"playlistmenuNo : " + playlistmenuNo);
        //DEBUG_LOG(TAG,"tableContentUri : " + tableContentUri);
        //DEBUG_LOG(TAG,"filename : " + filename);
        //DEBUG_LOG(TAG,"isresume : " + isresume);
        //DEBUG_LOG(TAG,"============================");

        ContentValues values = new ContentValues();
        values.put(IS_RESUME, isresume);
        int uriUpdate = mCtx.getContentResolver().update(tableContentUri,
                    values,
                    (FILE_NAME + "=?"),
                    new String[]{filename});
        //DEBUG_LOG(TAG, "uriUpdate : " + uriUpdate);
    }
//endregion

//region get file type
    /**
     * get file type by signature id
     */
    private static final int[] SIGNATURE_PDF = hexStringToIntArray("25504446");

    private static enum signatureID {
        SIGNATURE_ID_PDF,
        SIGNATURE_ID_NONE
    }

    private static final int[][] SIGNATURES = new int[signatureID.SIGNATURE_ID_NONE.ordinal()][];

    static {
        SIGNATURES[signatureID.SIGNATURE_ID_PDF.ordinal()] = SIGNATURE_PDF;
    }

    public static final int MAX_SIGNATURE_LENGTH = 20;

    public static int[] hexStringToIntArray(String s){
        int[] temp = new int[s.length()/2];
        for(int i=0;i<s.length();i=i+2){
            temp[i/2] = Integer.valueOf(s.substring(i,i+2),16).intValue();
        }
        return temp;
    }

    public static signatureID getSignatureIdFromHeader(String filepath) throws IOException {
        // read signature from head of source and compare with known signatures

        InputStream is = null;
        File file = new File(filepath);
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            DEBUG_LOG(TAG, "[getSignatureIdFromHeader] FileNotFoundException : " + e.getCause());
            //e.printStackTrace();
        } catch (IOException e) {
            DEBUG_LOG(TAG, "[getSignatureIdFromHeader] IOException : " + e.getCause());
            //e.printStackTrace();
        }

        int signatureId = signatureID.SIGNATURE_ID_NONE.ordinal();
        if(is != null) {
            int[] byteArray = new int[MAX_SIGNATURE_LENGTH];
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < MAX_SIGNATURE_LENGTH; i++) {
                byteArray[i] = is.read();
                //DEBUG_LOG(TAG, "byteArray[" + i + "]=" + Integer.toHexString(byteArray[i]));
                builder.append(Integer.toHexString(byteArray[i]));
            }

            for (int i = 0; i < SIGNATURES.length; i++) {
                int mappingCount = 0;
                int startByte = 0;
                for (int j = 0; j < SIGNATURES[i].length; j++) {
                    //if (i == signatureID.SIGNATURE_ID_MP4.ordinal()) {
                    //    startByte = 4;
                    //}
                    if (SIGNATURES[i][j] == byteArray[j + startByte]) {
                        mappingCount++;
                        if (mappingCount == SIGNATURES[i].length) {
                            signatureId = i;
                            break;
                        }
                    }
                }
            }
        }
        DEBUG_LOG(TAG, "signatureId : " + signatureID.values()[signatureId]);
        return signatureID.values()[signatureId];
    }

    public static fileType getFileType(String filepath) {
        fileType type;
        signatureID signatureId = signatureID.SIGNATURE_ID_NONE;
        try {
            signatureId = getSignatureIdFromHeader(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        switch (signatureId) {
            case SIGNATURE_ID_PDF:
                type = fileType.FILE_PDF;
                break;
            default:
                //DEBUG_LOG(TAG, "File not support!!!");
                type = fileType.NONE;
                break;
        }

        DEBUG_LOG(TAG, "type : " + type);
        return type;
    }

    /**
     * get file type by execute shell command via ProcessBuilder
     *
     * @param command executed shell command
     */
    public static fileType processCmd(String[] command) {
        fileType type = fileType.NONE;

        //file --mime-type filenam
        //String[] command = { "file", "--mime-type", filePath };
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        try {
            process = processBuilder.start();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;

            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }

            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
        } catch (IOException e) {
            DEBUG_LOG(TAG, "IOException processCmd fail : " + e.getCause());
            //e.printStackTrace();
        } catch (Exception e) {
            DEBUG_LOG(TAG, "Exception processCmd fail : " + e.getCause());
            //e.printStackTrace();
        } finally {
            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                DEBUG_LOG(TAG, "[finally] IOException processCmd fail : " + e.getCause());
                //e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        Log.d(TAG, "successMsg: " + successMsg + ", ErrorMsg: " + errorMsg);

        if(successMsg.toString().contains("pdf")) {
            //DEBUG_LOG(TAG, "file type : " + fileType.FILE_VIDEO);
            type = fileType.FILE_PDF;
        }
        else {
            //DEBUG_LOG(TAG, "File not support!!!");
            type = fileType.NONE;
        }
        DEBUG_LOG(TAG, "file type : " + type);
        return type;
    }

    /**
     * dispatch get file type method
     */
    public static fileType dispatchGetFileTypeMethod(String filePath) {
        fileType type;
        if(GET_FILETYPE_BY_COMMAND) {
            String[] command = {"file", "--mime-type", filePath};
            type = processCmd(command);
            if(type == fileType.NONE) {
                type = getFileType(filePath);
            }
        }
        else {
            type = getFileType(filePath);
        }
        return type;
    }
//endregion

//region notify scalar service
    //public static ITPVPDScalarService mScalarservice = null;
    public static boolean mNotifyScalarServiceFailOverAgain;
    public static boolean mNotifyScalarServiceNormalExitAgain;
    public static ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            DEBUG_LOG(TAG, "onServiceDisconnected");
            //mScalarservice = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DEBUG_LOG(TAG, "onServiceConnected");
            //mScalarservice = ITPVPDScalarService.Stub.asInterface(service);
            if(mNotifyScalarServiceFailOverAgain) {
                //DEBUG_LOG(TAG, "Notify Scalar Service FailOver again ");
                NotifyScalarServiceFailOver((mPlayListSelection + 1));
                mNotifyScalarServiceFailOverAgain = false;
            }
            if(mNotifyScalarServiceNormalExitAgain) {
                //DEBUG_LOG(TAG, "Notify Scalar Service NormalExit again ");
                NotifyScalarSourceNormalExit();
                mNotifyScalarServiceNormalExitAgain = false;
            }
        }
    };
    public static void NotifyScalarServiceFailOver(int index) {
        //if(Utils.SUPPORT_SCALAR_SERVICE) {
        //    try {
        //        mNotifyScalarServiceFailOverAgain = false;
        //        //DEBUG_LOG(TAG, "mCtx.getString(R.string.app_name) : " + mCtx.getString(R.string.app_name));
        //        if (mScalarservice != null) {
        //            DEBUG_LOG(TAG, "Notify Scalar Service list not exist");
        //            mScalarservice.setFailOver("PDFPlayer", index);
        //            NotifyMailService();
        //        } else {
        //            mNotifyScalarServiceFailOverAgain = true;
        //            DEBUG_LOG(TAG, "Scalar service is null");
        //        }
        //    } catch (RemoteException e) {
        //        e.printStackTrace();
        //    }
        //}
    }

    public static void NotifyScalarSourceNormalExit() {
        //if(Utils.SUPPORT_SCALAR_SERVICE) {
        //    try {
        //        mNotifyScalarServiceNormalExitAgain = false;
        //        if (mScalarservice != null) {
        //            DEBUG_LOG(TAG, "NotifyScalarSourceNormalExit");
        //            mScalarservice.notifySourceNormalExit("PDFPlayer");
        //        }
        //        else {
        //            mNotifyScalarServiceNormalExitAgain = true;
        //            DEBUG_LOG(TAG, "Scalar service is null");
        //        }
        //    } catch (RemoteException e) {
        //        e.printStackTrace();
        //    }
        //}
    }
//endregion

//region notify mail service
    public static void NotifyMailService() {
        if(SUPPORT_MAIL_SERVICE) {
            DEBUG_LOG(TAG, "[NotifyMailService]");
            //Create a intent
            Intent intent = new Intent(INTENT_MAILSERVICE_ACTION);
            intent.setAction(INTENT_MAILSERVICE_ACTION);

            //Add Event Name (ftp, mediaplayer, pdfplayer, ...)

            intent.putExtra("EventName", "pdfplayer");//mCtx.getString(R.string.app_name));

            //Add Mail Subject
            intent.putExtra("Subject", "PDF Log File.");

            //Add Mail Body
            intent.putExtra("Body", "Attachment file is pdf log file.");

            //Add UUID
            intent.putExtra("UUID", LOG_FILE_NAME_EXTENSION);

            //Add Callback Intent
            intent.putExtra("CallbackIntent", INTENT_PDFPLAYER_MAIL_CALLBACK);

            //Add Attachments File
            String logfilepath = getInternalStoragePath();
            {
                File f = new File(logfilepath);
                if (f != null) {
                    File file[] = f.listFiles();
                    if (file != null) {
                        DEBUG_LOG(TAG, "Size: " + file.length);

                        /** Calculate PdfPlayer Log File number. */
                        int logFileCount = 0;
                        for (int i = 0; i < file.length; i++) {
                            if (file[i].getName().contains(LOG_FILE_NAME) && file[i].getName().endsWith(LOG_FILE_EXTENSION)) {
                                logFileCount++;
                            }
                        }
                        //DEBUG_LOG(TAG, "logFileCount : " + logFileCount);

                        /** Use logFileCount to assign strAttachments length. */
                        String[] strAttachments = new String[logFileCount];
                        int FileCount = 0;
                        for (int i = 0; i < file.length; i++) {
                            if (file[i].getName().contains(LOG_FILE_NAME) && file[i].getName().endsWith(LOG_FILE_EXTENSION)) {
                                strAttachments[FileCount] = file[i].getAbsolutePath();
                                DEBUG_LOG(TAG, "strAttachments[" + FileCount + "] : " + strAttachments[FileCount]);
                                FileCount++;
                            }
                        }
                        //DEBUG_LOG(TAG, "strAttachments.length : " + strAttachments.length);

                        /** put strAttachments to intent. */
                        intent.putExtra("AttachmentFiles", strAttachments);

                        ///** debug log */
                        //for (int i = 0; i < strAttachments.length; i++) {
                        //    DEBUG_LOG(TAG, "strAttachments[" + i + "] : " + strAttachments[i]);
                        //}
                        ///** ~debug log */
                    }
                }
            }

            intent.putExtra("AutoDelAttachFile", "DELETE_AFTER_SUCCESS");
            mCtx.sendBroadcast(intent);
        }

    }

    public static void deleteOldLogFile(String filename)
    {
        String logfilepath = getInternalStoragePath();
        File f = new File(logfilepath);
        if(f != null) {
            File file[] = f.listFiles();
            if(file != null) {
                DEBUG_LOG(TAG, "Size: " + file.length);

                //if(file.length > 1)
                {
                    for (int i = 0; i < file.length; i++) {
                        if(file[i].getName().contains(LOG_FILE_NAME)) {
                            file[i].delete();
                            //if (file[i].getName().contains(filename)) {
                            //    break;
                            //}
                        }
                    }
                }
            }
        }
    }
//endregion

//region write log file data
    public static String LOG_FILE_NAME_EXTENSION = "PDFLogFile.txt";
    public static String LOG_FILE_NAME = "PDFLogFile";
    public static String LOG_FILE_EXTENSION = ".txt";
    public static Utils_LogFileDataInfo mUtilsLogFileDataInfo;

    public static enum logFileItems{
        LOG_START_TIME,
        LOG_END_TIME,
        LOG_PLAY_LIST_NO,
        LOG_LAST_PLAY_FILE_NAME,
        LOG_LAST_PLAY_BACKGROUND_MUSIC_FILE_NAME,
        LOG_STORAGE,
        LOG_REPEAT_MODE,
        LOG_ANI_PERIOD,
        LOG_ERROR_FILE_LIST
    }
    public static void writeLogFileData(logFileItems item, String value) {
        switch(item) {
            case LOG_START_TIME:
                mUtilsLogFileDataInfo.mStartTime = value;
                break;
            case LOG_END_TIME:
                mUtilsLogFileDataInfo.mEndTime = value;
                break;
            case LOG_PLAY_LIST_NO:
                mUtilsLogFileDataInfo.mPlayListNo = value;
                break;
            case LOG_LAST_PLAY_FILE_NAME:
                mUtilsLogFileDataInfo.mLastPlayFileName = value;
                break;
            case LOG_LAST_PLAY_BACKGROUND_MUSIC_FILE_NAME:
                mUtilsLogFileDataInfo.mLastPlayBackgroundMusicFileName = value;
                break;
            case LOG_STORAGE:
                mUtilsLogFileDataInfo.mStorage = value;
                break;
            case LOG_REPEAT_MODE:
                mUtilsLogFileDataInfo.mRepeatMode = value;
                break;
            case LOG_ANI_PERIOD:
                mUtilsLogFileDataInfo.mAniPeriod = value;
                break;
            case LOG_ERROR_FILE_LIST:
                DEBUG_LOG(TAG, "value : " + value);
                Utils_ErrorFileList utilsErrorFileListTemp;
                utilsErrorFileListTemp = new Utils_ErrorFileList(value);
                mUtilsLogFileDataInfo.mUtilsErrorFileList.add(utilsErrorFileListTemp);
                break;
        }
    }

    public static void writeLogFile() {
        try {
            DEBUG_LOG(TAG, "outputFile filepath : " + mLogFilePath);
            DEBUG_LOG(TAG, "outputFile LOG_FILE_NAME : " + LOG_FILE_NAME_EXTENSION);

            File outputDir = new File(mLogFilePath);
            if(!outputDir.exists()) {
                outputDir.mkdir();
                DEBUG_LOG(TAG, "!outputDir.exists() : " + mLogFilePath);
            }

            File outputFile = new File(mLogFilePath, LOG_FILE_NAME_EXTENSION);
            if(!outputFile.exists()) {
                outputFile.createNewFile();
                DEBUG_LOG(TAG, "!outputFile.exists() : " + (mLogFilePath + LOG_FILE_NAME_EXTENSION));
            }

            if(outputFile.exists()) {
                outputFile.setWritable(true);
                //DEBUG_LOG(TAG, "outputFile.exists() ");
                OutputStream outputStreamWriter = new FileOutputStream(outputFile);
                //OutputStreamWriter outputStreamWriter = new OutputStreamWriter(mCtx.openFileOutput((filepath+LOG_FILE_NAME), Context.MODE_PRIVATE));
                String starttime = (mCtx.getString(R.string.starttime) + " : " + mUtilsLogFileDataInfo.mStartTime + "\n");
                String endTime = (mCtx.getString(R.string.endtime) + " : " + mUtilsLogFileDataInfo.mEndTime + "\n");
                String playlistNo = ((mCtx.getString(R.string.playListno) + " : " + mUtilsLogFileDataInfo.mPlayListNo + "\n"));
                String lastPlayFileName = ((mCtx.getString(R.string.lastplayfilename) + " : " + mUtilsLogFileDataInfo.mLastPlayFileName + "\n"));

                if(mUtilsLogFileDataInfo.mStorage != null) {
                    if(mUtilsLogFileDataInfo.mStorage.equals("Internal")) {
                        mUtilsLogFileDataInfo.mStorage = mCtx.getString(R.string.internal);
                    }
                    else if(mUtilsLogFileDataInfo.mStorage.equals("USB")) {
                        mUtilsLogFileDataInfo.mStorage = mCtx.getString(R.string.usb);
                    }
                    else if(mUtilsLogFileDataInfo.mStorage.equals("Sdcard")) {
                        mUtilsLogFileDataInfo.mStorage  =mCtx.getString(R.string.sdcard);
                    }
                }
                String storage = ((mCtx.getString(R.string.storagepath) + " : " + mUtilsLogFileDataInfo.mStorage + "\n"));

                if(mUtilsLogFileDataInfo.mRepeatMode != null) {
                    if(mUtilsLogFileDataInfo.mRepeatMode.equals("REPEAT_ONCE")) {
                        mUtilsLogFileDataInfo.mRepeatMode = mCtx.getString(R.string.repeatonce);
                    }
                    else if(mUtilsLogFileDataInfo.mRepeatMode.equals("REPEAT_ALL")) {
                        mUtilsLogFileDataInfo.mRepeatMode = mCtx.getString(R.string.repeatall);
                    }
                    else if(mUtilsLogFileDataInfo.mRepeatMode.equals("REPEAT_NONE")) {
                        mUtilsLogFileDataInfo.mRepeatMode  =mCtx.getString(R.string.none);
                    }
                }
                String repeatMode = ((mCtx.getString(R.string.repeatmode) + " : " + mUtilsLogFileDataInfo.mRepeatMode + "\n"));

                if(mUtilsLogFileDataInfo.mAniPeriod != null) {
                    if(mUtilsLogFileDataInfo.mAniPeriod.equals("PERIOD_5")) {
                        mUtilsLogFileDataInfo.mAniPeriod = mCtx.getString(R.string.period_5);
                    }
                    else if(mUtilsLogFileDataInfo.mAniPeriod.equals("PERIOD_10")) {
                        mUtilsLogFileDataInfo.mAniPeriod = mCtx.getString(R.string.period_10);
                    }
                    else if(mUtilsLogFileDataInfo.mAniPeriod.equals("PERIOD_15")) {
                        mUtilsLogFileDataInfo.mAniPeriod = mCtx.getString(R.string.period_15);
                    }
                    else if(mUtilsLogFileDataInfo.mAniPeriod.equals("PERIOD_20")) {
                        mUtilsLogFileDataInfo.mAniPeriod = mCtx.getString(R.string.period_20);
                    }
                }
                String aniPeriod = ((mCtx.getString(R.string.effectdurationsec) + " : " + mUtilsLogFileDataInfo.mAniPeriod + "\n"));
                String errorFileList = ((mCtx.getString(R.string.errorfilelistasbelow) + " :\n"));
                outputStreamWriter.write(starttime.getBytes());
                outputStreamWriter.write(endTime.getBytes());
                outputStreamWriter.write(playlistNo.getBytes());
                outputStreamWriter.write(lastPlayFileName.getBytes());
                outputStreamWriter.write(storage.getBytes());
                outputStreamWriter.write(repeatMode.getBytes());
                outputStreamWriter.write(aniPeriod.getBytes());
                outputStreamWriter.write(errorFileList.getBytes());
                for(Utils_ErrorFileList item : mUtilsLogFileDataInfo.mUtilsErrorFileList) {
                    DEBUG_LOG(TAG, ("Error file : " + item.mFilePath));
                    String errorFile = (" " + item.mFilePath + "\n");
                    outputStreamWriter.write(errorFile.getBytes());
                }
                outputStreamWriter.flush();
                outputStreamWriter.close();
            }
            DEBUG_LOG(TAG, "outputFile close ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//endregion

//region Toast custom
    public static void Toast(Context ctx, String text) {
        LayoutInflater inflater = LayoutInflater.from(ctx);

        View toastRoot = inflater.inflate(R.layout.toastlayout, null);
        TextView tv = (TextView) toastRoot.findViewById(R.id.toasttext);
        tv.setText(text);

        Toast toast = new Toast(ctx);

        toast.setView(toastRoot);
        toast.show();
//      toast.setText("I am toast");
        toast.setDuration(Toast.LENGTH_LONG);
    }
//endregion

    //region check playlist style and store to contentprovider.
    public static playlistStyle checkPlaylistStyle(List<Utils_PlayListDataInfo> playlistdatainfo) {

        /** check playlist style. */
        boolean hasPdf = false;
        for (Utils_PlayListDataInfo item : playlistdatainfo) {
            if (isPdf(item.getFileName())) {
                hasPdf = true;
            }
        }

        playlistStyle style = playlistStyle.PLAYLIST_OTHER;
        if (hasPdf) {
            style = playlistStyle.PLAYLIST_PDF;
        }
        /** ~check playlist style. */

        return style;
    }
    //endregion

    public static void setUnregisterReceiverOnPause(boolean isSet) {
        mIsUnregisterReceiver = isSet;
    }
    public static boolean isUnregisterReceiverOnPause() {
        return mIsUnregisterReceiver;
    }

    /** Read Import File text from /philips/importfile/ */
    public static void ReadImportFile() {
        //reading text from file
        try {
            String line;
            BufferedReader in = null;
            List<Utils_PlayListDataInfo> Loadfileplaylistdatainfo = new ArrayList<Utils_PlayListDataInfo>();
            Utils_PlayListDataInfo playlistdatainfotemp;
            String usbpath = Utils.getInternalStoragePath();

            String importfilepath = Utils.getInternalStoragePath() + "importfile/";
            for(int index = 1; index <= Utils.playlistMenu.PLAYLIST_COUNT.ordinal(); index++) {
                Loadfileplaylistdatainfo.clear();
                String filename = "pdfplaylist" + index + ".txt";
                File importfile = new File(importfilepath, filename);
                //Utils.DEBUG_LOG(TAG, "[ReadImportFile] importfile : " + importfile);
                //Utils.DEBUG_LOG(TAG, "[ReadImportFile] importfile.exists() : " + importfile.exists());
                if(importfile != null && importfile.exists()) {
                    in = new BufferedReader(new FileReader(importfile));
                    while ((line = in.readLine()) != null) {
                        // char to string conversion
                        //Utils.DEBUG_LOG(TAG, "[ReadImportFile] line : " + line);
                        if (line.equals("Internal")) {
                            usbpath = Utils.getInternalStoragePath();
                        }
                        else if(line.equals("USB")) {
                            usbpath = Utils.getUSBStoragePath();
                        }
                        else if(line.equals("SDcard")) {
                            usbpath = Utils.getSDCardStoragePath();
                        }
                        else {
                            String[] filepath = line.split("/");

                            playlistdatainfotemp =
                                    new Utils_PlayListDataInfo(filepath[3],
                                            usbpath + filepath[2] + "/" + filepath[3],
                                            "true",
                                            "false",
                                            "true");

                            Loadfileplaylistdatainfo.add(playlistdatainfotemp);
                        }
                    }
                    in.close();

                    Utils.PDFPlayerContentProvider_Set_Playlist((index-1), Loadfileplaylistdatainfo);

                    /** check playlist style and store to contentprovider. */
                    Utils.playlistStyle style = Utils.checkPlaylistStyle(Loadfileplaylistdatainfo);
                    Utils.PDFPlayerContentProvider_Set_PlaylistStyle((index-1), style);
                    /** ~check playlist style and store to contentprovider. */

                    File renamefile = new File(importfilepath, "old_" + filename);
                    importfile.renameTo(renamefile);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    public static String getFileFolderPublic() {
        return "tpv";//SystemProperties.get("ro.product.pdpath", "tpv").toLowerCase();
    }
}
