package com.example.android.englishword.data;


import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 用于查询的约束条件
 */
public class WordContract implements BaseColumns{

    // 存储类名
    private final String LOG_TAG = WordContract.class.getSimpleName();


    // 存储content uri 的字符串形式
    public static final String CONTENT_AUTHORITY = "com.example.android.englishword";
    // 存储基本的内容uri
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    // 存储路径，用于查询数据库中的数据
    public static final String PATH_WORDS = "words";

    /**
     * 内部类，数据库的Schema
     */
    public static class WordEntry {

        /*
        数据库表名
         */
        public static final String TABLE_NAME = "words";

        /**
         * 组装content Uri
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_WORDS);

        /*
        字段id
         */
        public static final String _ID = BaseColumns._ID;
        /*
        字段英语单词
         */
        public static final String COLUMN_ENGLISH_WORD = "englishWord";

        /*
        字段英语单词词性
         */
        public static final String COLUMN_ENGLISH_SPEECH = "englishSpeech";

        /*
        字段英语单词中文意思
         */
        public static final String COLUMN_CHINESE = "chinese";

        /*
        字段常用短语
         */
        public static final String COLUMN_COMMON_PHRASE = "phrase";

        /*
        字段例句
         */
        public static final String COLUMN_EXAMPLE = "example";

        /*
        字段是否可见
         */
        public static final String COLUMN_VISIBLE = "visible";

        /*
        创建日期
         */
        public static final String COLUMN_CREATE_DATE = "createData";
    }

}
