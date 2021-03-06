package com.example.android.englishword.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

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
                Log.i(LOG_TAG, "query: " + count);

                break;
            case SINGLE_ITEM_CODE:

                selection = WordEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

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

        // 管理通知，当有更新时
        // Notify  all listeners that the data has changed for the pet content URI
        // uri: content://com.example.android.pets/pets
//        getContext().getContentResolver().notifyChange(uri,null);

        /**
         * Set notification URI on the cursor.
         * so we know what content URI the cursor was created for.
         * if the data at this URI changes, then we know we need to update the cursor
         *
         * 我们传入第一个参数为ContentResolver，以使与这个Resolver关联的侦听器（这个案例中为catalog activity），自动收到通知。
         * URI:我们要监视的内容的URI
         */
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
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

        // 检测插入的值中 单词是否为空
        if (values.containsKey(WordEntry.COLUMN_ENGLISH_WORD)) {
            String englishWord = values.getAsString(WordEntry.COLUMN_ENGLISH_WORD);
            if (TextUtils.isEmpty(englishWord)) {
//                Toast.makeText(getContext(),"单词不能为空", Toast.LENGTH_SHORT).show();
                throw new IllegalArgumentException("Word can not be null");
            }
        }

        db = mWordDbHelper.getReadableDatabase();

        long rowId = db.insert(WordEntry.TABLE_NAME,
                null,                                     // 要展示的列
                values                                    // where 过滤条件参数部分
        );

        // Notify  all listeners that the data has changed for the pet content URI
        // uri: content://com.example.android.pets/pets
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, rowId);

    }

    // 删除word 条目
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int match = sUriMatcher.match(uri);
        int rowId;
        switch (match) {
            // 查询整个表的值
            case ALL_ITEM_CODE:
                db = mWordDbHelper.getWritableDatabase();
                rowId = db.delete(WordEntry.TABLE_NAME,null,null);

                break;
            case SINGLE_ITEM_CODE:
                selection = WordEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowId = db.delete(WordEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot insert unknown uri " + uri);
        }

        // Notify  all listeners that the data has changed for the pet content URI
        // uri: content://com.example.android.pets/pets
        // 当删除时，需要通知Loader ，否则删除后，页面不会发生变化
        getContext().getContentResolver().notifyChange(uri, null);

        return rowId;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            // 查询整个表的值
            case ALL_ITEM_CODE:
                return updateWord(uri, values, selection, selectionArgs);

            case SINGLE_ITEM_CODE:

                selection = WordEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                return updateWord(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Cannot insert unknown uri " + uri);

        }
    }

    private int updateWord(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

//        Log.i(LOG_TAG, "updateWord: "+selection);
//        db = mWordDbHelper.getReadableDatabase();
//        int id = db.update(WordEntry.TABLE_NAME, values, selection, selectionArgs);
//        Log.i(LOG_TAG, "updateWord: id "+id);
//        return id;



        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        // 检查Word 单词是否为空
        if (values.containsKey(WordEntry.COLUMN_ENGLISH_WORD)) {
            String englishWord = values.getAsString(WordEntry.COLUMN_ENGLISH_WORD);
            if (TextUtils.isEmpty(englishWord)) {
//                Toast.makeText(getContext(),"单词不能为空", Toast.LENGTH_SHORT).show();
                throw new IllegalArgumentException("Word can not be null");
            }
        }

        SQLiteDatabase database = mWordDbHelper.getWritableDatabase();
        /**
         * Insert the new pet with the given values
         */

        // Returns the number of database rows affected by the update statement

        int rowsUpdated  = database.update(
                WordEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        // Notify  all listeners that the data has changed for the pet content URI
        // uri: content://com.example.android.pets/pets
        // 当删除时，需要通知Loader ，否则删除后，页面不会发生变化
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
