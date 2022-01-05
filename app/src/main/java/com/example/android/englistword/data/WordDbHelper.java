package com.example.android.englistword.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.englistword.data.WordContract.WordEntry;
import androidx.annotation.Nullable;

/**
 * 辅助类创建、打开数据库及管理数据的连接
 */
public class WordDbHelper extends SQLiteOpenHelper {
    /*
     * 定义数据库版本,初始化为1
     */

    private static final int DATABASE_VERSION = 1;

    /*
     * 定义数据库名称
     */
    private static final String DATABASE_NAME = "english.db";


    /*
    创建SQL表的的schema
     */
    private static final String SQL_CREATE_WORDS_TABLE = "CREATE TABLE "
            + WordEntry.TABLE_NAME + " ("
            + WordEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "                 // ID
            + WordEntry.COLUMN_ENGLISH_WORD + " TEXT NOT NULL, "                     // englishWord
            + WordEntry.COLUMN_ENGLISH_SPEECH + " INTEGER NOT NULL, "                // englishSpeech
            + WordEntry.COLUMN_CHINESE + " TEXT, "                                   // chinese
            + WordEntry.COLUMN_COMMON_PHRASE + " TEXT, "                             // phrase
            + WordEntry.COLUMN_EXAMPLE + " TEXT, "                                   // example
            + WordEntry.COLUMN_VISIBLE + " INTEGER, "                                // visible
            + WordEntry.COLUMN_CREATE_DATE + " TEXT);";                              // date


    /*
    删除SQL表的的schema
     */
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + WordEntry.TABLE_NAME + ";";


    /**
     * 构造函数，用于创建数据库对象
     * @param context 上下文环境
     */
    public WordDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         *  execSQL 是void类型，用于执行没有返回值的SQL语句
         */
        db.execSQL(SQL_CREATE_WORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        /**
         *  该数据库仅是在线数据的一个缓存，它的更新测率是简化丢弃数据并重建。
         *  This database is only a cache for online data, so its upgrade policy is
         *  to simply to discard the data and start over
         */
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);

    }
}
