package com.example.android.englishword.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.englishword.MainActivity;
import com.example.android.englishword.data.WordContract.WordEntry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 用于控制对数据库访问权限
 */
public class WordProvider extends ContentProvider {

    // 存储类名
    private final String LOG_TAG = WordProvider.class.getSimpleName();

    // 创建一个sUriMatcher 对象，用于装载uri 与code 的匹配项
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // 查询所有值code
    private static final int ALL_ITEM_CODE = 1;
    // 查询单一值code
    private static final int SINGLE_ITEM_CODE = 2;

//    sUriMatcher.addURI(WordContract.CONTENT_AUTHORITY, WordContract.PATH_WORDS, ALL_ITEM_CODE);

    // 静态代码块，用于新建对象后，自动执行，且只执行一次
    static {
        sUriMatcher.addURI(WordContract.CONTENT_AUTHORITY, WordContract.PATH_WORDS, ALL_ITEM_CODE);
        sUriMatcher.addURI(WordContract.CONTENT_AUTHORITY, WordContract.PATH_WORDS + "/#", SINGLE_ITEM_CODE);
    }


    // 辅助数据库类对象
    WordDbHelper mWordDbHelper;

    // SQLiteDatabase 对象
    SQLiteDatabase db;

    @Override
    public boolean onCreate() {

        mWordDbHelper = new WordDbHelper(getContext());
        db = mWordDbHelper.getReadableDatabase();

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // 设置cursor 变量，用于存储返回值
        Cursor cursor;

        // 设置匹配值，用于决定执行查询哪种数据
        int match = sUriMatcher.match(uri);
        switch (sUriMatcher.match(uri)) {
            // 查询整个表的值
            case ALL_ITEM_CODE:
                cursor = db.query(WordEntry.TABLE_NAME,
                        null,                                     // 要展示的列
                        null,                                     // where 过滤条件参数部分
                        null,                                  // where 过滤条件值部分
                        null,                                      // group by 字段
                        null,                                       // group by 后的过滤字段
                        null                                       // 排序字段
                );

                int count = cursor.getCount();
                Log.i(LOG_TAG, "query: "+count);

                break;
            case SINGLE_ITEM_CODE:

                cursor = db.query(WordEntry.TABLE_NAME,
                        projection,                                        // 要展示的列
                        selection,                                         // where 过滤条件参数部分
                        selectionArgs,                                     // where 过滤条件值部分
                        null,                                      // group by 字段
                        null,                                       // group by 后的过滤字段
                        null                                       // 排序字段
                );
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown uri " + uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        // 设置匹配值，用于决定执行查询哪种数据
        int match = sUriMatcher.match(uri);
        switch (match) {
            // 查询整个表的值
            case ALL_ITEM_CODE:
                return insertWord(uri, values);

            default:
                throw new IllegalArgumentException("Cannot insert unknown uri " + uri);

        }
    }

    private Uri insertWord(@NonNull Uri uri, @Nullable ContentValues values) {
        db = mWordDbHelper.getReadableDatabase();

        long rowId = db.insert(WordEntry.TABLE_NAME,
                null,                                     // 要展示的列
                values                                    // where 过滤条件参数部分
        );

        return ContentUris.withAppendedId(uri, rowId);

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
