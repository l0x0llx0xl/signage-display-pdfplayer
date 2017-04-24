package com.tpv.app.pdf.Import;

import com.tpv.app.pdf.Common.Utils;
import com.tpv.app.pdf.Common.Utils_PlayListDataInfo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andy.Hsu on 2017/2/17.
 */
public class ImportFile {
    private static final String TAG = ImportFile.class.getName();

    public static String IMPORT_FILE_NAME = "pdfplaylist";
    public static String IMPORT_FILE_EXTENSIONS = ".txt";
    public static String IMPORT_FILE_STORAGE = Utils.getInternalStoragePath();//Utils.getUSBStoragePath();

    public static String readListFile(String path) {
        String filePath = path;
        StringBuilder sb = new StringBuilder();
        try{
            FileInputStream fileInputStream = new FileInputStream(filePath);
            Reader r = new InputStreamReader(fileInputStream, "UTF-8");  //or whatever encoding
            char[] buf = new char[1024];
            int ch = r.read(buf);
            while(ch >= 0) {
                sb.append(buf, 0, ch);
                ch = r.read(buf);
            }
            //Utils.DEBUG_LOG(TAG, "++readListFile, sb.toString() :" + sb.toString());
            fileInputStream.close();
            return sb.toString();
        }
        catch (IOException e){
            Utils.DEBUG_LOG(TAG, "++readListFile :" + e.toString());
        }
        return "";
    }

    public static String[] parseTextFile(String text){
        Utils.DEBUG_LOG(TAG, "[parseTextFile]");
        String contents[] = text.split("\\n");
        for(String url : contents){
            Utils.DEBUG_LOG(TAG, url + "");
        }
        return contents;
    }

    public static List<Utils_PlayListDataInfo> loadImportFileToPlaylist(String contents){

        List<Utils_PlayListDataInfo> importplaylistdatainfo = new ArrayList<Utils_PlayListDataInfo>();

        String lines[] = parseTextFile(contents);

        for(String line : lines) {
            String[] mFileName = line.split("./");
            String mFilePath = IMPORT_FILE_STORAGE + line.trim();
            if(mFileName.length >= 2) {
                String filename = mFileName[1].trim();
                boolean bFile = Utils.isPdf(filename);
                String isFile = bFile ? "true" : "false";
                String isResume = "false";
                String isSelected = "true";
                if (bFile) {
                    Utils_PlayListDataInfo playlistdatainfotemp =
                            new Utils_PlayListDataInfo(filename,
                                    mFilePath,
                                    isFile,
                                    isResume,
                                    isSelected);
                    importplaylistdatainfo.add(playlistdatainfotemp);
                }
            }
        }
        return importplaylistdatainfo;
    }
}