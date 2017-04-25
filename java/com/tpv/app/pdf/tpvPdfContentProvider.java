package com.tpv.app.pdf;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.tpv.app.pdf.Common.Utils;

import java.util.HashMap;
/**
 * Created by Andy.Hsu on 2015/7/21.
 */

public class tpvPdfContentProvider extends ContentProvider {
    private SQLiteDatabase db;
    static final String DATABASE_NAME = "AndroidPDPdfData.db";
    static final int DATABASE_VERSION = 1;

    //static final String id = "id";

    private static HashMap<String, String> values;

    static final int URI_TYPE_TABLE1 = 1;
    static final int URI_TYPE_TABLE2 = 2;
    static final int URI_TYPE_TABLE3 = 3;
    static final int URI_TYPE_TABLE4 = 4;
    static final int URI_TYPE_TABLE5 = 5;
    static final int URI_TYPE_TABLE6 = 6;
    static final int URI_TYPE_TABLE7 = 7;
    static final int URI_TYPE_TABLE_STYLE = 8;
    static final int URI_TYPE_TABLE_SETTINGS = 9;
    static final int URI_TYPE_TABLE_NORMAL_FINISH_MEDIAPLAYER = 10;
    static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Utils.AUTHORITY, Utils.TABLE1, URI_TYPE_TABLE1);
        uriMatcher.addURI(Utils.AUTHORITY, Utils.TABLE2, URI_TYPE_TABLE2);
        uriMatcher.addURI(Utils.AUTHORITY, Utils.TABLE3, URI_TYPE_TABLE3);
        uriMatcher.addURI(Utils.AUTHORITY, Utils.TABLE4, URI_TYPE_TABLE4);
        uriMatcher.addURI(Utils.AUTHORITY, Utils.TABLE5, URI_TYPE_TABLE5);
        uriMatcher.addURI(Utils.AUTHORITY, Utils.TABLE6, URI_TYPE_TABLE6);
        uriMatcher.addURI(Utils.AUTHORITY, Utils.TABLE7, URI_TYPE_TABLE7);
        uriMatcher.addURI(Utils.AUTHORITY, Utils.TABLE_STYLE, URI_TYPE_TABLE_STYLE);
        uriMatcher.addURI(Utils.AUTHORITY, Utils.TABLE_SETTINGS, URI_TYPE_TABLE_SETTINGS);
        uriMatcher.addURI(Utils.AUTHORITY, Utils.TABLE_NORMAL_FINISH, URI_TYPE_TABLE_NORMAL_FINISH_MEDIAPLAYER);
    }

    //region
    static final String CREATE_DB_TABLE1 = " CREATE TABLE " + Utils.TABLE1
            + " (" + Utils._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + " " + Utils.FILE_NAME + ", "
            + " " + Utils.FILE_PATH + ", "
            + " " + Utils.IS_FILE + ", "
            + " " + Utils.IS_RESUME + ", "
            + " " + Utils.IS_SELECTED + ");";

    static final String CREATE_DB_TABLE2 = " CREATE TABLE " + Utils.TABLE2
            + " (" + Utils._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + " " + Utils.FILE_NAME + ", "
            + " " + Utils.FILE_PATH + ", "
            + " " + Utils.IS_FILE + ", "
            + " " + Utils.IS_RESUME + ", "
            + " " + Utils.IS_SELECTED + ");";

    static final String CREATE_DB_TABLE3 = " CREATE TABLE " + Utils.TABLE3
            + " (" + Utils._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + " " + Utils.FILE_NAME + ", "
            + " " + Utils.FILE_PATH + ", "
            + " " + Utils.IS_FILE + ", "
            + " " + Utils.IS_RESUME + ", "
            + " " + Utils.IS_SELECTED + ");";

    static final String CREATE_DB_TABLE4 = " CREATE TABLE " + Utils.TABLE4
            + " (" + Utils._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + " " + Utils.FILE_NAME + ", "
            + " " + Utils.FILE_PATH + ", "
            + " " + Utils.IS_FILE + ", "
            + " " + Utils.IS_RESUME + ", "
            + " " + Utils.IS_SELECTED + ");";

    static final String CREATE_DB_TABLE5 = " CREATE TABLE " + Utils.TABLE5
            + " (" + Utils._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + " " + Utils.FILE_NAME + ", "
            + " " + Utils.FILE_PATH + ", "
            + " " + Utils.IS_FILE + ", "
            + " " + Utils.IS_RESUME + ", "
            + " " + Utils.IS_SELECTED + ");";

    static final String CREATE_DB_TABLE6 = " CREATE TABLE " + Utils.TABLE6
            + " (" + Utils._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + " " + Utils.FILE_NAME + ", "
            + " " + Utils.FILE_PATH + ", "
            + " " + Utils.IS_FILE + ", "
            + " " + Utils.IS_RESUME + ", "
            + " " + Utils.IS_SELECTED + ");";

    static final String CREATE_DB_TABLE7 = " CREATE TABLE " + Utils.TABLE7
            + " (" + Utils._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + " " + Utils.FILE_NAME + ", "
            + " " + Utils.FILE_PATH + ", "
            + " " + Utils.IS_FILE + ", "
            + " " + Utils.IS_RESUME + ", "
            + " " + Utils.IS_SELECTED + ");";

    static final String CREATE_DB_TABLE_STYLE = " CREATE TABLE " + Utils.TABLE_STYLE
            + " (" + Utils._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + " " + Utils.PLAYLIST_NO + ", "
            + " " + Utils.STYLE + ");";

    static final String CREATE_DB_TABLE_SETTINGS = " CREATE TABLE " + Utils.TABLE_SETTINGS
            + " (" + Utils._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + " " + Utils.STORAGE + ", "
            + " " + Utils.REPEAT_MODE + ", "
            + " " + Utils.PHOTO_SLIDESHOW_PERIOD + ");";

    static final String CREATE_DB_TABLE_NORMAL_FINISH_MEDIAPLAYER = " CREATE TABLE " + Utils.TABLE_NORMAL_FINISH
            + " (" + Utils._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + " " + Utils.IS_NORMAL + ");";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("MailContentProvider", "[onCreate]");
            db.execSQL(CREATE_DB_TABLE1);
            db.execSQL(CREATE_DB_TABLE2);
            db.execSQL(CREATE_DB_TABLE3);
            db.execSQL(CREATE_DB_TABLE4);
            db.execSQL(CREATE_DB_TABLE5);
            db.execSQL(CREATE_DB_TABLE6);
            db.execSQL(CREATE_DB_TABLE7);
            db.execSQL(CREATE_DB_TABLE_STYLE);
            db.execSQL(CREATE_DB_TABLE_SETTINGS);
            db.execSQL(CREATE_DB_TABLE_NORMAL_FINISH_MEDIAPLAYER);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("MailContentProvider", "[onUpgrade] oldVersion: " + oldVersion + ", newVersion: " +newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + Utils.TABLE1);
            db.execSQL("DROP TABLE IF EXISTS " + Utils.TABLE2);
            db.execSQL("DROP TABLE IF EXISTS " + Utils.TABLE3);
            db.execSQL("DROP TABLE IF EXISTS " + Utils.TABLE4);
            db.execSQL("DROP TABLE IF EXISTS " + Utils.TABLE5);
            db.execSQL("DROP TABLE IF EXISTS " + Utils.TABLE6);
            db.execSQL("DROP TABLE IF EXISTS " + Utils.TABLE7);
            db.execSQL("DROP TABLE IF EXISTS " + Utils.TABLE_STYLE);
            db.execSQL("DROP TABLE IF EXISTS " + Utils.TABLE_SETTINGS);
            db.execSQL("DROP TABLE IF EXISTS " + Utils.TABLE_NORMAL_FINISH);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        if (db != null) {
            return true;
        }
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String table = getTableByUri(uri);
        qb.setTables(table);
        qb.setProjectionMap(values);

        if (sortOrder == null || sortOrder == "") {
            sortOrder = Utils._ID;
        }
        Cursor c = qb.query(db, projection, selection, selectionArgs, null,
                null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = 0;
        String table = getTableByUri(uri);
        rowID = db.insert(table, "", values);

        if (rowID > 0) {
            Uri contentUri = getContentUriByUri(uri);
            Uri _uri = ContentUris.withAppendedId(contentUri, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;
        String table = getTableByUri(uri);
        count = db.update(table, values, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        String table = getTableByUri(uri);
        count = db.delete(table, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        String table = getTableByUri(uri);
        return "vnd.android.cursor.dir/vnd." + Utils.AUTHORITY + "." + table;
    }

    private String getTableByUri(Uri uri) {
        String table;

        switch (uriMatcher.match(uri)) {
            case URI_TYPE_TABLE1:
                table = Utils.TABLE1;
                break;
            case URI_TYPE_TABLE2:
                table = Utils.TABLE2;
                break;
            case URI_TYPE_TABLE3:
                table = Utils.TABLE3;
                break;
            case URI_TYPE_TABLE4:
                table = Utils.TABLE4;
                break;
            case URI_TYPE_TABLE5:
                table = Utils.TABLE5;
                break;
            case URI_TYPE_TABLE6:
                table = Utils.TABLE6;
                break;
            case URI_TYPE_TABLE7:
                table = Utils.TABLE7;
                break;
            case URI_TYPE_TABLE_STYLE:
                table = Utils.TABLE_STYLE;
                break;
            case URI_TYPE_TABLE_SETTINGS:
                table = Utils.TABLE_SETTINGS;
                break;
            case URI_TYPE_TABLE_NORMAL_FINISH_MEDIAPLAYER:
                table = Utils.TABLE_NORMAL_FINISH;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return table;
    }

    private Uri getContentUriByUri(Uri uri) {
        Uri contentUri;
        switch (uriMatcher.match(uri)) {
            case URI_TYPE_TABLE1:
                contentUri = Utils.TABLE1_CONTENT_URI;
                break;
            case URI_TYPE_TABLE2:
                contentUri = Utils.TABLE2_CONTENT_URI;
                break;
            case URI_TYPE_TABLE3:
                contentUri = Utils.TABLE3_CONTENT_URI;
                break;
            case URI_TYPE_TABLE4:
                contentUri = Utils.TABLE4_CONTENT_URI;
                break;
            case URI_TYPE_TABLE5:
                contentUri = Utils.TABLE5_CONTENT_URI;
                break;
            case URI_TYPE_TABLE6:
                contentUri = Utils.TABLE6_CONTENT_URI;
                break;
            case URI_TYPE_TABLE7:
                contentUri = Utils.TABLE7_CONTENT_URI;
                break;
            case URI_TYPE_TABLE_STYLE:
                contentUri = Utils.TABLE_STYLE_CONTENT_URI;
                break;
            case URI_TYPE_TABLE_SETTINGS:
                contentUri = Utils.TABLE_SETTINGS_CONTENT_URI;
                break;
            case URI_TYPE_TABLE_NORMAL_FINISH_MEDIAPLAYER:
                contentUri = Utils.TABLE_NORMAL_FINISH_CONTENT_URI;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return contentUri;
    }
}
